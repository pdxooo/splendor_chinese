package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a power.
 */
public abstract class Power {

    private boolean unlocked;
    private HashMap<TokenType, Integer> requirements;

    /**
     * Construct a power.
     */
    public Power() {
        this(new HashMap<>());
    }

    /**
     * Construct a power with its development-card requirements.
     *
     * @param requirements coloured bonuses needed to unlock the power
     */
    public Power(Map<TokenType, Integer> requirements) {
        this.unlocked = false;
        this.requirements = new HashMap<>(requirements);
    }

    /**
     * Get a safe copy of the requirements displayed to every player.
     *
     * @return requirements for this power
     */
    public HashMap<TokenType, Integer> getRequirements() {
        return new HashMap<>(requirements);
    }

    /**
     * Apply the requirements generated once for this game session.
     *
     * @param requirements new requirements
     */
    public void setRequirements(Map<TokenType, Integer> requirements) {
        this.requirements = new HashMap<>(requirements);
    }

    /**
     * Check the common coloured-bonus requirement.
     *
     * @param player player being checked
     * @return whether the player owns the required bonuses
     */
    protected boolean requirementsMet(TradingPostsPlayer player) {
        return player.hasBonuses(requirements);
    }

    /**
     * Let a player unlock the power.
     *
     * @param player The player unlocking the power
     */
    public void unlockPower(TradingPostsPlayer player) {
        this.unlocked = true;
        player.placeCoatOfArms();
    }

    /**
     * Check if a player has unlocked the power.
     *
     * @return true if the player has unlocked the power
     */
    public boolean isUnlocked() {
        return this.unlocked;
    }

    /**
     * Check if the player can unlock this power.
     *
     * @param player The player that uses the power
     * @return true if player can unlock the power
     */
    public abstract boolean conditionMet(TradingPostsPlayer player);

    /**
     * Use this power.
     *
     * @param player player who uses the power
     */
    public abstract void execute(TradingPostsPlayer player);

}
