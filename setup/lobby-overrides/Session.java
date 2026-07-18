package eu.kartoffelquadrat.ls.lobby.model;

import eu.kartoffelquadrat.asyncrestlib.BroadcastContent;
import eu.kartoffelquadrat.ls.gameregistry.controller.LocationValidator;
import eu.kartoffelquadrat.ls.gameregistry.model.GameServerParameters;
import eu.kartoffelquadrat.ls.lobby.control.SessionException;
import java.util.*;

/** A lobby session, including the game-specific turn time limit. */
public class Session implements BroadcastContent {
    private final GameServerParameters gameParameters;
    private final String creator;
    private final LinkedList<String> players;
    private final int turnTimeSeconds;
    private boolean launched;
    private String savegameid;
    private Map<String, String> playerLocations;

    public Session(String creator, GameServerParameters gameParameters) {
        this(creator, gameParameters, "", 120);
    }

    public Session(String creator, GameServerParameters gameParameters, String savegameid) {
        this(creator, gameParameters, savegameid, 120);
    }

    public Session(String creator, GameServerParameters gameParameters, String savegameid,
                   int turnTimeSeconds) {
        this.creator = creator;
        this.gameParameters = gameParameters;
        this.players = new LinkedList<>();
        this.players.add(creator);
        this.savegameid = savegameid == null ? "" : savegameid;
        this.turnTimeSeconds = Math.max(30, Math.min(turnTimeSeconds, 300));
        this.launched = false;
        this.playerLocations = new LinkedHashMap<>();
    }

    public void addPlayerLocation(String player, String location) throws SessionException {
        if (!players.contains(player))
            throw new SessionException("Player locator can not be added. The player is not registered to this session.");
        if (!LocationValidator.isValidClientLocation(location))
            throw new SessionException("Player locator can not be added. The provided location is not a valid IP address.");
        playerLocations.put(player, location);
    }

    public boolean isFull() { return players.size() >= gameParameters.getMaxSessionPlayers(); }
    public String getGameName() { return gameParameters.getName(); }
    public String getCreator() { return creator; }
    public List<String> getPlayers() { return Collections.unmodifiableList(players); }
    public void addPlayer(String playerid) {
        if (isFull()) throw new RuntimeException("Player cannot be added to session. Session is already full.");
        players.add(playerid);
    }
    public boolean isLaunched() { return launched; }
    public void markAsLaunched() {
        if (launched) throw new RuntimeException("Session cannot be marked as launched, because is it already launched.");
        launched = true;
    }
    public void removePlayer(String player) {
        if (!players.contains(player)) throw new RuntimeException("Player can not be removed, because she is not registered to the session.");
        players.remove(player);
    }
    public GameServerParameters getGameParameters() { return gameParameters; }
    public String getSavegameid() { return savegameid; }
    public int getTurnTimeSeconds() { return turnTimeSeconds; }
    @Override public boolean isEmpty() { return false; }
}
