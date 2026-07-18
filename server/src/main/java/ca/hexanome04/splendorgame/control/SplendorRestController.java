package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.GameSession;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorException;
import ca.hexanome04.splendorgame.model.action.ActionDecoder;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.dacbiet.simpoll.ContentWatcher;
import dev.dacbiet.simpoll.Fetcher;
import dev.dacbiet.simpoll.ResultGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


/**
 * Rest controller for all API endpoints of Splendor game.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SplendorRestController {

    final Logger logger = LoggerFactory.getLogger(SplendorRestController.class);

    private String gameServiceName;
    private final SessionManager sessionManager;
    private final Authentication auth;
    private final GameSavesManager gameSavesManager;
    private final long longPollTimeout;
    private final Map<String, ContentWatcher> gameWatcher;

    @Autowired
    Initializer initializer;

    /**
     * Create an instance of Splendor's REST controller.
     *
     * @param sessionManager session manager
     * @param auth methods relating to authentication with LS
     * @param gameSavesManager game saves manager
     * @param gameServiceName game service name
     * @param longPollTimeout timeout for long polling
     */

    public SplendorRestController(@Autowired SessionManager sessionManager,
                                  @Autowired Authentication auth,
                                  @Autowired GameSavesManager gameSavesManager,
                                  @Value("${gs.name}") String gameServiceName,
                                  @Value("${longpoll.timeout}") int longPollTimeout) {
        this.sessionManager = sessionManager;
        this.auth = auth;
        this.gameSavesManager = gameSavesManager;
        this.gameServiceName = gameServiceName;
        this.longPollTimeout = longPollTimeout;
        this.gameWatcher = new HashMap<>();
    }

    /**
     * Check if server is online.
     *
     * @return Quick statement confirming whether server is online
     */
    @GetMapping(value = "/api/online", produces = "application/json; charset=utf-8")
    public String online() {
        return "The server currently has " + sessionManager.getNumSessions() + " sessions created.";

    }

    /**
     * Launch a session (only from lobby service).
     *
     * @param sessionId session id for new session
     * @param launchSessionInfo information about the new session
     * @return HTTP response entity
     */
    @PutMapping(value = "/api/sessions/{sessionId}", consumes = "application/json; charset=utf-8")
    public ResponseEntity launchSession(@PathVariable String sessionId, @RequestBody LaunchSessionInfo launchSessionInfo) {

        try {
            if (launchSessionInfo == null) {
                throw new SplendorException("Missing launch session info.");
            }
            if (launchSessionInfo.gameServer() == null) {
                throw new SplendorException("Missing service name in launch session info.");
            }

            // verify if game service name is valid.
            boolean validGameServiceName = false;
            GameVersions gameVersion = null;
            for (GameVersions gv : GameVersions.values()) {
                if (launchSessionInfo.gameServer().equals(gameServiceName + "_" + gv)) {
                    validGameServiceName = true;
                    gameVersion = gv;
                    break;
                }
            }
            if (!validGameServiceName) {
                throw new SplendorException("Lobby Service did not specify a matching Service name.");
            }

            if (sessionManager.getGameSession(sessionId) != null) {
                throw new SplendorException("Game can not be launched. Id is already in use.");
            }

            // Looks good, lets create the game
            this.addSession(sessionId, launchSessionInfo, gameVersion);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (SplendorException e) {
            logger.warn("Splendor issue while launching session: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Unable to launch game session: ", e);
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server ran into an issue.");
        }
    }

    /**
     * Adds a session to the session manger and initializes the game.
     *
     * @param sessionId session id of game
     * @param launchSessionInfo launch session info of game
     * @param gameVersion  game version to launch as
     * @throws Exception thrown due to IO or invalid information given
     */
    protected void addSession(String sessionId, LaunchSessionInfo launchSessionInfo, GameVersions gameVersion) throws Exception {
        if (!launchSessionInfo.savegame().isEmpty()) {
            // retrieve game state from save data
            Game game = gameSavesManager.getGameSaveData(launchSessionInfo.savegame());
            if (game == null) {
                logger.warn("Error while launching session: Retrieved game save state is null!");
                throw new SplendorException("Unable to load previous game save state!");
            }
            sessionManager.createSession(sessionId, game, launchSessionInfo);
        } else {
            sessionManager.createNewSession(sessionId, launchSessionInfo.players(),
                    launchSessionInfo.creator(),
                    launchSessionInfo.savegame(),
                    gameVersion,
                    launchSessionInfo.normalizedTurnTimeSeconds());
        }
        logger.info("Launched new game session: " + sessionId);
        gameWatcher.put(sessionId, new ContentWatcher());
    }

    /**
     * Obtain the game state of specified session.
     *
     * @param sessionId session id to get game state of
     * @param hash optional hash from client
     * @param token access token identifying which player's cards may be shown
     * @return JSON object of game state
     */
    @GetMapping(value = "/api/sessions/{sessionId}", produces = "application/json; charset=utf-8")
    public DeferredResult getGameState(@PathVariable String sessionId,
                                       @RequestParam(required = false) String hash,
                                       @RequestParam(name = "access_token", required = false) String token) {
        try {
            // Check if session exists
            GameSession game = sessionManager.getGameSession(sessionId);
            if (game == null) {
                throw new SplendorException("There is no session associated this session ID: " + sessionId + ".");
            }

            ContentWatcher watcher = gameWatcher.get(sessionId);
            String viewerName = token == null ? "" : auth.getNameFromToken(token);
            Fetcher fetcher = () -> serializeGameForViewer(game, viewerName);

            return ResultGenerator.getStringResult(watcher, fetcher, hash, this.longPollTimeout);
        } catch (SplendorException e) {
            DeferredResult result = new DeferredResult();
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()));
            return result;
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            logger.warn("Issue while getting game state: ", e);
            DeferredResult result = new DeferredResult();
            result.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server could not retrieve game state."));
            return result;
        }
    }

    /** Preserve the original two-argument entry point used by controller tests. */
    public DeferredResult getGameState(String sessionId, String hash) {
        return getGameState(sessionId, hash, null);
    }

    /**
     * Get players in the specified game session.
     *
     * @param sessionId game session id
     * @return JSON object of players
     */
    @GetMapping(value = "/api/sessions/{sessionId}/players", produces = "application/json; charset=utf-8")
    public ResponseEntity getPlayers(@PathVariable String sessionId) {
        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new SplendorException("There is no session associated this session ID: " + sessionId + ".");
            }

            Gson gson = SplendorTypeAdapter.newClientGson();
            JsonArray players = gson.toJsonTree(
                    sessionManager.getGameSession(sessionId).getGame().getPlayers()).getAsJsonArray();
            hideReservedCardFaces(players, "");
            String serializedPlayers = gson.toJson(players);
            return ResponseEntity.status(HttpStatus.OK).body(serializedPlayers);
        } catch (SplendorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Issue while retrieving player data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server ran into an issue while retrieving player data.");
        }
    }

    private String serializeGameForViewer(GameSession session, String viewerName) {
        Gson gson = SplendorTypeAdapter.newClientGson();
        Game game = session.getGame();
        JsonObject gameJson = gson.toJsonTree(game).getAsJsonObject();
        hideReservedCardFaces(gameJson.getAsJsonArray("players"), viewerName);
        gameJson.addProperty("turnTimeSeconds", session.getTurnTimeSeconds());
        gameJson.addProperty("turnDeadlineEpochMillis", session.getTurnDeadlineEpochMillis());
        gameJson.add("chatMessages", gson.toJsonTree(session.getChatMessages()));
        return gson.toJson(gameJson);
    }

    /**
     * Other players may know the tier of a reserved card, but not its face.
     * Replace every hidden card with a tier-only object so IDs and costs never
     * reach an unauthorized browser.
     */
    private void hideReservedCardFaces(JsonArray players, String viewerName) {
        if (players == null) {
            return;
        }
        for (JsonElement playerElement : players) {
            JsonObject player = playerElement.getAsJsonObject();
            String playerName = player.get("name").getAsString();
            if (playerName.equals(viewerName)) {
                continue;
            }

            JsonArray hiddenCards = new JsonArray();
            JsonArray reservedCards = player.getAsJsonArray("reservedCards");
            if (reservedCards != null) {
                for (JsonElement cardElement : reservedCards) {
                    JsonObject hiddenCard = new JsonObject();
                    JsonElement tier = cardElement.getAsJsonObject().get("cardTier");
                    if (tier != null) {
                        hiddenCard.add("cardTier", tier);
                    }
                    hiddenCards.add(hiddenCard);
                }
            }
            player.add("reservedCards", hiddenCards);
        }
    }

    /**
     * Obtain the available actions for this turn.
     *
     * @param sessionId session id to get actions for
     * @param playerName player whose action is being put
     * @return JSON object of available actions
     */
    @GetMapping(value = "/api/sessions/{sessionId}/players/{playerName}/actions",
            produces = "application/json; charset=utf-8")
    public ResponseEntity getActions(@PathVariable String sessionId, @PathVariable String playerName) {
        try {
            // Check if session exists
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new SplendorException("There is no session associated this session ID: " + sessionId + ".");
            }
            // Check if player exists
            GameSession gameSession = sessionManager.getGameSession(sessionId);
            if (gameSession.advanceTurnIfExpired()) {
                gameWatcher.get(sessionId).markDirty();
            }
            Game game = gameSession.getGame();
            Player player = game.getPlayerFromName(playerName);
            if (player == null) {
                throw new SplendorException("The specified player does not exist in this session.");
            }

            if (!player.getName().equals(game.getTurnCurrentPlayer().getName())) {
                throw new SplendorException("This player has no valid actions because it is not their turn.");
            }

            // If so, serialize actions and place it as body in a ResponseEntity
            String[] actions = new String[game.getCurValidActions().size()];
            for (int i = 0; i < game.getCurValidActions().size(); i++) {
                actions[i] = game.getCurValidActions().get(i).toString();
            }

            String serializedActions = SplendorTypeAdapter.newClientGson().toJson(actions);
            return ResponseEntity.status(HttpStatus.OK).body(serializedActions);
        } catch (SplendorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Issue while retrieving all possible actions: ", e);
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Perform an action to specified session for specified player.
     * Will perform checks to ensure correct player is performing the action
     * and that the player is able to perform the action.
     *
     * @param token access_token
     * @param sessionId session id
     * @param playerName player name who is performing action
     * @param actionIdentifier action identifier
     * @param bodyData JSON string information of action
     * @return String (empty is OK)
     */
    @PutMapping(value = "/api/sessions/{sessionId}/players/{playerName}/actions/{actionIdentifier}",
            consumes = "application/json; charset=utf-8")
    public ResponseEntity<String> putAction(@RequestParam("access_token") String token, @PathVariable String sessionId,
                                            @PathVariable String playerName, @PathVariable Actions actionIdentifier,
                                            @RequestBody String bodyData) {

        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new SplendorException("There is no session associated this session ID: " + sessionId + ".");
            }
            GameSession gameSession = sessionManager.getGameSession(sessionId);
            Game game = gameSession.getGame();
            Player player = game.getPlayerFromName(playerName);

            if (player == null) {
                throw new SplendorException("The specified player does not exist in this session.");
            }

            if (!auth.getNameFromToken(token).equals(playerName)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not match requested players name.");
            }

            synchronized (gameSession) {
                if (gameSession.advanceTurnIfExpired()) {
                    gameWatcher.get(sessionId).markDirty();
                }

                if (!game.getTurnCurrentPlayer().getName().equals(playerName)) {
                    throw new SplendorException("思考时间已到，当前回合已经交给下一位玩家。");
                }

                List<Actions> validActions = game.getCurValidActions();

                if (!validActions.contains(actionIdentifier)) {
                    throw new SplendorException("Given action is not valid: " + actionIdentifier);
                }

            JsonObject jobj = JsonParser.parseString(bodyData).getAsJsonObject();

            logger.info("Valid actions BEFORE turn: " + game.getCurValidActions());

                int turnBefore = game.getTurnCounter();
                ArrayList<ActionResult> actionResult =
                        game.takeAction(playerName, ActionDecoder.createAction(actionIdentifier.toString(), jobj));

            if (game.isGameOver()) {
                initializer.deleteGameSession(sessionId);
            }

                if (!actionResult.contains(ActionResult.VALID_ACTION)) {
                // This should technically only have the ones that are errors,
                // not the ones that are because they need to do an extra action.
                // (Since, the ones that require an additional action have VALID_ACTION)
                Optional<ActionResult> result = actionResult.stream()
                        .filter(ar -> ar != ActionResult.TURN_COMPLETED) // idk if necessary
                        .findFirst();
                if (result.isPresent() && !result.get().getDescription().isEmpty()) {
                    String desc = result.get().getDescription();
                    throw new SplendorException(desc);
                } else {
                    throw new SplendorException("Invalid action performed.");
                }
                }

                if (game.getTurnCounter() != turnBefore) {
                    gameSession.resetTurnDeadline();
                }
            }

            logger.info("Valid actions AFTER turn:  " + game.getCurValidActions() + "\n");

            // TODO: return what further actions are needed (if any)
            // mark that the game state has changed
            gameWatcher.get(sessionId).markDirty();
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (SplendorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Issue while executing action: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server ran into an issue while executing an action.");
        }
    }

    /** Send a chat message to players in the current room. */
    @PostMapping(value = "/api/sessions/{sessionId}/chat",
            consumes = "application/json; charset=utf-8")
    public ResponseEntity<String> postChatMessage(@RequestParam("access_token") String token,
                                                   @PathVariable String sessionId,
                                                   @RequestBody Map<String, String> body) {
        try {
            GameSession session = sessionManager.getGameSession(sessionId);
            if (session == null) {
                throw new SplendorException("游戏房间不存在。");
            }
            String sender = auth.getNameFromToken(token);
            if (session.getGame().getPlayerFromName(sender) == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有本局玩家可以发送消息。");
            }
            String text = body.getOrDefault("text", "").trim();
            if (text.isEmpty()) {
                throw new SplendorException("消息不能为空。");
            }
            if (text.length() > 300) {
                throw new SplendorException("消息不能超过 300 个字符。");
            }
            session.addChatMessage(sender, text);
            gameWatcher.get(sessionId).markDirty();
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (SplendorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Issue while sending a chat message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("消息发送失败。");
        }
    }

    /** Server-authoritative timeout; clients cannot pause it by closing the browser. */
    @Scheduled(fixedRate = 500)
    public void advanceExpiredTurns() {
        for (GameSession session : sessionManager.getGameSessions()) {
            if (session.advanceTurnIfExpired()) {
                ContentWatcher watcher = gameWatcher.get(session.getSessionId());
                if (watcher != null) {
                    watcher.markDirty();
                }
            }
        }
    }

    /**
     * Restart an already launched game.
     *
     * @param token token of player
     * @param sessionId session id
     * @param bodyData body data in post
     * @return String (empty is OK)
     */
    @PostMapping(value = "/api/sessions/{sessionId}/restart",
            consumes = "text/plain; charset=utf-8")
    public ResponseEntity<String> restartGame(@RequestParam("access_token") String token,
                                              @PathVariable String sessionId,
                                              @RequestBody(required = false) String bodyData) {
        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new SplendorException("There is no session associated this session ID: " + sessionId + ".");
            }
            GameSession gameSession = sessionManager.getGameSession(sessionId);
            String creatorName = gameSession.getCreatorUsername();

            if (!auth.getNameFromToken(token).equals(creatorName)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Only the creator of the game can restart the game.");
            }

            // username matches, maybe we add a vote to restart in the future?
            // recreate all info to launch a game
            GameVersions gameVersion = gameSession.getGame().getGameVersion();

            List<PlayerInfo> playersInfo = new ArrayList<>();
            for (Player p : gameSession.getGame().getPlayers()) {
                playersInfo.add(new PlayerInfo(p.getName(), p.getColour()));
            }

            Game newGame = sessionManager.launchNewGame(gameVersion, playersInfo);
            gameSession.setGame(newGame);

            logger.info("Restarted game session: " + sessionId);

            // game has updated
            gameWatcher.get(sessionId).markDirty();

            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (SplendorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Issue while restarting game: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Issue while trying to restart game.");
        }
    }

}
