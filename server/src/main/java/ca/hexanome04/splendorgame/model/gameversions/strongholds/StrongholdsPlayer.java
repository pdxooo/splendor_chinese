package ca.hexanome04.splendorgame.model.gameversions.strongholds;

import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;

/** Player state for the Strongholds module. */
public class StrongholdsPlayer extends OrientPlayer {

    private int availableStrongholds = 3;

    /**
     * Create a player with three available strongholds.
     *
     * @param name player name
     * @param colour player display colour
     */
    public StrongholdsPlayer(String name, String colour) {
        super(name, colour);
    }

    /**
     * Obtain the strongholds not currently on the board.
     *
     * @return available stronghold count
     */
    public int getAvailableStrongholds() {
        return availableStrongholds;
    }

    /**
     * Take one stronghold from the player's available supply.
     *
     * @return whether a stronghold was available
     */
    public boolean useAvailableStronghold() {
        if (availableStrongholds <= 0) {
            return false;
        }
        availableStrongholds--;
        return true;
    }

    /**
     * Return strongholds to the player's available supply.
     *
     * @param count number to return
     */
    public void returnStrongholds(int count) {
        if (count < 0 || availableStrongholds + count > 3) {
            throw new IllegalStateException("Stronghold total must remain three.");
        }
        availableStrongholds += count;
    }
}
