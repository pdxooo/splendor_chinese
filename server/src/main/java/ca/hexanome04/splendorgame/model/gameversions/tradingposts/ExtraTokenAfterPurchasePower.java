package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.Map;

/**
 * Class that represents the extra token after purchase power.
 */
public class ExtraTokenAfterPurchasePower extends Power {

    /**
     * Creates an extra token after purchase power object.
     */
    public ExtraTokenAfterPurchasePower() {
        super(Map.of(TokenType.Red, 3, TokenType.White, 1));
    }

    /**
     * Check if the player can unlock this power.
     *
     * @param player player who is unlocking this power.
     * @return true if player can unlock this power.
     */
    @Override
    public boolean conditionMet(TradingPostsPlayer player) {
        return requirementsMet(player);
    }

    /**
     * Executing the power.
     *
     * @param player player who is using this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {

    }
}
