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
    public LaunchSessionInfo(String gameServer, List<PlayerInfo> players, String creator,
                             String savegame) {
        this(gameServer, players, creator, savegame, null);
    }

    public int normalizedTurnTimeSeconds() {
        int value = turnTimeSeconds == null ? 120 : turnTimeSeconds;
        return Math.max(30, Math.min(value, 300));
    }
}
