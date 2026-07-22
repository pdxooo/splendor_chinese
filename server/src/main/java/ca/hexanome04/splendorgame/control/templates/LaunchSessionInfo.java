package ca.hexanome04.splendorgame.control.templates;

import java.util.List;

/**
 * Construct a launch session info.
 *
 * @param gameServer game service name
 * @param players list of players
 * @param creator creator name
 * @param savegame save game name
 * @param turnTimeSeconds maximum thinking time for one turn
 */
public record LaunchSessionInfo(String gameServer, List<PlayerInfo> players, String creator,
                                String savegame, Integer turnTimeSeconds) {
    /**
     * Construct launch information without an explicit turn limit.
     *
     * @param gameServer game service name
     * @param players list of players
     * @param creator creator name
     * @param savegame save game name
     */
    public LaunchSessionInfo(String gameServer, List<PlayerInfo> players, String creator,
                             String savegame) {
        this(gameServer, players, creator, savegame, null);
    }

    /**
     * Normalize the requested turn limit to the supported range.
     *
     * @return turn limit between 30 and 300 seconds
     */
    public int normalizedTurnTimeSeconds() {
        int value = turnTimeSeconds == null ? 120 : turnTimeSeconds;
        return Math.max(30, Math.min(value, 300));
    }
}
