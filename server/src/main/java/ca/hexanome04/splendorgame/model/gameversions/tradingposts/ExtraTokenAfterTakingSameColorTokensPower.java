package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.Map;

/**
 * Class that represents the extra token after taking token of same color power.
 */
public class ExtraTokenAfterTakingSameColorTokensPower extends Power {

    /**
     * Creates an extra token power object.
     */
    public ExtraTokenAfterTakingSameColorTokensPower() {
        super(Map.of(TokenType.White, 2));
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
