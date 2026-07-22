package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.Map;

/**
 * Class that represents the add five prestige points power.
 */
public class AddFivePrestigePointsPower extends Power {

    private boolean isUsed = false;

    /**
     * Creates a new add five prestige points power object.
     */
    public AddFivePrestigePointsPower() {
        super(Map.of(TokenType.Green, 5));
    }

    /**
     * Checking if the player can unlock this power.
     *
     * @param player player who is unlocking this power.
     * @return true if player can unlock this power.
     */
    @Override
    public boolean conditionMet(TradingPostsPlayer player) {
        return requirementsMet(player) && player.hasNobles();
    }

    /**
     * Executing the power.
     *
     * @param player player who is using this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {
        if (!isUsed) {
            player.addPrestigePoints(5);
            this.isUsed = true;
        }
    }
}
