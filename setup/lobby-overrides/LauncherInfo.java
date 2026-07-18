package eu.kartoffelquadrat.ls.lobby.model;

import java.util.LinkedList;

/** Launch payload sent from the lobby to a game server. */
public class LauncherInfo {
    String gameServer;
    LinkedList<PlayerInfo> players;
    String creator;
    String savegame;
    int turnTimeSeconds = 120;

    public LauncherInfo() {}
    public LauncherInfo(String gameServer, LinkedList<PlayerInfo> players, String creator) {
        this(gameServer, players, creator, "", 120);
    }
    public LauncherInfo(String gameServer, LinkedList<PlayerInfo> players, String creator, String savegame) {
        this(gameServer, players, creator, savegame, 120);
    }
    public LauncherInfo(String gameServer, LinkedList<PlayerInfo> players, String creator,
                        String savegame, int turnTimeSeconds) {
        this.gameServer = gameServer;
        this.players = players;
        this.creator = creator;
        this.savegame = savegame;
        this.turnTimeSeconds = turnTimeSeconds;
    }
    public String getGameServer() { return gameServer; }
    public void setGameServer(String gameServer) { this.gameServer = gameServer; }
    public LinkedList<PlayerInfo> getPlayers() { return players; }
    public void setPlayers(LinkedList<PlayerInfo> players) { this.players = players; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public String getSavegame() { return savegame; }
    public void setSavegame(String savegame) { this.savegame = savegame; }
    public int getTurnTimeSeconds() { return turnTimeSeconds; }
    public void setTurnTimeSeconds(int turnTimeSeconds) { this.turnTimeSeconds = turnTimeSeconds; }
}
