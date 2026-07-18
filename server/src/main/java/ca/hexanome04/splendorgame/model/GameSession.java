package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.Game;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represent a game session for a Splendor game.
 */
public class GameSession {
    private final String sessionId;
    private boolean launched;
    private Game game;
    private String creatorUsername;
    private String sessionName;
    private final int turnTimeSeconds;
    private long turnDeadlineEpochMillis;
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private long nextChatMessageId = 1;

    /**
     * Initialize a game session.
     *
     * @param sessionId         Session id associated with this game session
     * @param creatorUsername   Username of creator associated with this game session
     * @param sessionName       Name of this session
     */
    public GameSession(String sessionId, String creatorUsername, String sessionName) {
        this(sessionId, creatorUsername, sessionName, 120);
    }

    public GameSession(String sessionId, String creatorUsername, String sessionName,
                       int turnTimeSeconds) {
        this.launched = false;
        this.sessionId = sessionId;
        this.game = null;
        this.creatorUsername = creatorUsername;
        this.sessionName = sessionName;
        this.turnTimeSeconds = Math.max(30, Math.min(turnTimeSeconds, 300));
    }

    /**
     * Launch a session.
     *
     * @param players players playing
     */
    public void launchSession(List<Player> players) {
        // TODO: incomplete (more args?)
    }

    /**
     * Get the game associated with the session.
     *
     * @return game associated with this session
     */
    public Game getGame() {
        return game;
    }

    /**
     * Set the game associated with the session.
     *
     * @param game The game associated with the session.
     */
    public synchronized void setGame(Game game) {
        this.game = game;
        resetTurnDeadline();
    }

    /**
     * Check if the game has launched yet.
     *
     * @return has game launched
     */
    public boolean hasGameLaunched() {
        return this.launched;
    }

    /**
     * Get this game's session id.
     *
     * @return session id
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Get the username for the creator of this game.
     *
     * @return creator username
     */
    public String getCreatorUsername() {
        return this.creatorUsername;
    }

    public int getTurnTimeSeconds() {
        return turnTimeSeconds;
    }

    public synchronized long getTurnDeadlineEpochMillis() {
        return turnDeadlineEpochMillis;
    }

    public synchronized void resetTurnDeadline() {
        turnDeadlineEpochMillis = System.currentTimeMillis() + turnTimeSeconds * 1000L;
    }

    /** Advance an expired turn and discard any unfinished follow-up action. */
    public synchronized boolean advanceTurnIfExpired() {
        return advanceTurnIfExpired(System.currentTimeMillis());
    }

    boolean advanceTurnIfExpired(long now) {
        if (game == null || game.isGameOver() || now < turnDeadlineEpochMillis) {
            return false;
        }
        game.incrementTurn();
        turnDeadlineEpochMillis = now + turnTimeSeconds * 1000L;
        return true;
    }

    public synchronized ChatMessage addChatMessage(String sender, String text) {
        ChatMessage message = new ChatMessage(nextChatMessageId++, sender, text,
                Instant.now().toEpochMilli());
        chatMessages.add(message);
        if (chatMessages.size() > 100) {
            chatMessages.remove(0);
        }
        return message;
    }

    public synchronized List<ChatMessage> getChatMessages() {
        return Collections.unmodifiableList(new ArrayList<>(chatMessages));
    }

}
