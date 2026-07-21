package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.Map;

/**
 * Class that represents the gold token worth two tokens power.
 */
public class GoldTokenWorthTwoTokensPower extends Power {

    /**
     * Creates a gold token worth two tokens power object.
     */
    public GoldTokenWorthTwoTokensPower() {
        super(Map.of(TokenType.Blue, 3, TokenType.Brown, 1));
    }

    /**
     * Check if player can unlock this power.
     *
     * @param player player who is unlocking this power.
     * @return true if player can unlock this power.
     */
    @Override
    public boolean conditionMet(TradingPostsPlayer player) {
        return requirementsMet(player);
    }

    /**
     * Executing this power.
     *
     * @param player player who is using this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {
    }
}
