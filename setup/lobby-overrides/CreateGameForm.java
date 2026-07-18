package eu.kartoffelquadrat.ls.lobby.control;

/** Data passed by the client when opening a new session. */
public class CreateGameForm {
    private final String game;
    private final String creator;
    private String savegame = "";
    private Integer turnTimeSeconds = 120;

    public CreateGameForm(String game, String creator, String savegame, Integer turnTimeSeconds) {
        this.game = game;
        this.creator = creator;
        this.savegame = savegame == null ? "" : savegame;
        this.turnTimeSeconds = turnTimeSeconds;
    }

    public String getGame() { return game; }
    public String getCreator() { return creator; }
    public String getSavegame() { return savegame; }
    public int getTurnTimeSeconds() {
        int value = turnTimeSeconds == null ? 120 : turnTimeSeconds;
        return Math.max(30, Math.min(value, 300));
    }
}
