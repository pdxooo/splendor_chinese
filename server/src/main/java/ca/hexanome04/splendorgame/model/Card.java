package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a Card object.
 */
public abstract class Card {

    private final String id;
    private final int prestigePoints;
    private final CostType costType;
    private final HashMap<TokenType, Integer> tokenCost;

    // maybe attribute for the actual image of the card to be added?
    // add an attribute for a list of tokens representing the token/bonus cost for this card

    /**
     * Creates a new card with provided prestige point amount, cost type and card type.
     *
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     * @param id             ID Associated to this card.
     */
    public Card(int prestigePoints, CostType costType, HashMap<TokenType, Integer> tokenCost, String id) {
        this.prestigePoints = prestigePoints;
        this.costType = costType;
        this.tokenCost = tokenCost;
        this.id = id;
    }

    /**
     * Get the cost type of this card (tokens or bonuses).
     *
     * @return The cost type of the card.
     */
    public CostType getCostType() {
        return costType;
    }

    /**
     * Get the number of prestige points associated to this card.
     *
     * @return The number of prestige points.
     */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Returns the cost (in tokens) of the Card.
     *
     * @return Returns new list of TokenTypes.
     */
    public HashMap<TokenType, Integer> getTokenCost() {
        return new HashMap<TokenType, Integer>(tokenCost);
    }

    /**
     * Get ID of this card.
     *
     * @return id of card
     */
    public String getId() {
        return this.id;
    }

    /**
     * Check if this card can be purchased by the player.
     *
     * @param player player that wants to purchase this card
     * @param tokensToUse tokens requested to use
     * @return true if player has the funds (and/or bonuses) and no excess tokens given
     */
    public boolean isPurchasable(Player player, HashMap<TokenType, Integer> tokensToUse) {
        if (tokensToUse.values().stream().anyMatch(value -> value == null || value < 0)
                || !player.hasTokens(tokensToUse)) {
            return false;
        }
        Map<TokenType, Integer> bonuses = player.getBonuses();
        if (costType == CostType.Bonus) {
            return tokenCost.entrySet().stream().allMatch(entry ->
                    bonuses.getOrDefault(entry.getKey(), 0) >= entry.getValue());
        }

        HashMap<TokenType, Integer> remaining = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            if (type != TokenType.Gold && type != TokenType.Satchel) {
                remaining.put(type, tokenCost.getOrDefault(type, 0)
                        - bonuses.getOrDefault(type, 0) - tokensToUse.getOrDefault(type, 0));
            }
        }
        int goldValue = player instanceof TradingPostsPlayer tradingPostsPlayer
                && tradingPostsPlayer.goldTokenWorthTwoTokens.isUnlocked() ? 2 : 1;
        applyAvailableWildPieces(remaining, tokensToUse.getOrDefault(TokenType.Gold, 0), goldValue);
        applyAvailableWildPieces(remaining, bonuses.getOrDefault(TokenType.Gold, 0), goldValue);
        return remaining.values().stream().noneMatch(value -> value > 0);
    }

    /**
     * Check a purchase with an explicit player-selected amount of virtual Gold.
     * Physical Gold remains in {@code tokensToUse}; virtual Gold represents the
     * temporary bonuses printed on Orient cards.
     *
     * @param player player buying the card
     * @param tokensToUse real tokens selected by the player
     * @param virtualGoldPieces virtual Gold pieces selected for this purchase
     * @return whether this exact payment is legal
     */
    public boolean isPurchasable(Player player, HashMap<TokenType, Integer> tokensToUse,
                                 int virtualGoldPieces) {
        if (virtualGoldPieces < 0 || virtualGoldPieces > player.getBonuses().getOrDefault(TokenType.Gold, 0)
                || tokensToUse.values().stream().anyMatch(value -> value == null || value < 0)
                || !player.hasTokens(tokensToUse)) {
            return false;
        }

        Map<TokenType, Integer> bonuses = player.getBonuses();
        if (costType == CostType.Bonus) {
            if (virtualGoldPieces != 0 || tokensToUse.values().stream().anyMatch(value -> value != 0)) {
                return false;
            }
            return tokenCost.entrySet().stream().allMatch(entry ->
                    bonuses.getOrDefault(entry.getKey(), 0) >= entry.getValue());
        }

        HashMap<TokenType, Integer> remaining = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            if (type == TokenType.Gold || type == TokenType.Satchel) {
                continue;
            }
            int amount = Math.max(0, tokenCost.getOrDefault(type, 0)
                    - bonuses.getOrDefault(type, 0));
            int colouredPayment = tokensToUse.getOrDefault(type, 0);
            if (colouredPayment > amount) {
                return false;
            }
            remaining.put(type, amount - colouredPayment);
        }

        if (tokensToUse.getOrDefault(TokenType.Satchel, 0) != 0) {
            return false;
        }
        int goldValue = player instanceof TradingPostsPlayer tradingPostsPlayer
                && tradingPostsPlayer.goldTokenWorthTwoTokens.isUnlocked() ? 2 : 1;
        if (!applyWildPieces(remaining, tokensToUse.getOrDefault(TokenType.Gold, 0), goldValue)
                || !applyWildPieces(remaining, virtualGoldPieces, goldValue)) {
            return false;
        }
        return remaining.values().stream().noneMatch(value -> value > 0);
    }

    private void applyAvailableWildPieces(HashMap<TokenType, Integer> remaining, int pieces, int value) {
        for (int index = 0; index < pieces; index++) {
            for (TokenType type : TokenType.values()) {
                if (remaining.getOrDefault(type, 0) > 0) {
                    remaining.put(type, remaining.get(type) - value);
                    break;
                }
            }
        }
    }

    private boolean applyWildPieces(HashMap<TokenType, Integer> remaining, int pieces, int value) {
        for (int index = 0; index < pieces; index++) {
            TokenType colour = null;
            for (TokenType type : TokenType.values()) {
                if (remaining.getOrDefault(type, 0) > 0) {
                    colour = type;
                    break;
                }
            }
            if (colour == null) {
                return false;
            }
            remaining.put(colour, Math.max(0, remaining.get(colour) - value));
        }
        return true;
    }

    /**
     * Calculate how many virtual Gold pieces are required after applying the
     * selected real tokens and permanent colored bonuses. This method never
     * mutates the player and is intended to be called only for a successful
     * token-cost purchase.
     *
     * @param player player buying this card
     * @param tokensToUse real tokens selected for payment
     * @return number of virtual Gold pieces used
     */
    public int getVirtualGoldPiecesUsed(Player player, HashMap<TokenType, Integer> tokensToUse) {
        if (costType != CostType.Token) {
            return 0;
        }

        HashMap<TokenType, Integer> remaining = new HashMap<>(tokenCost);
        Map<TokenType, Integer> bonuses = player.getBonuses();
        for (TokenType type : remaining.keySet()) {
            if (type != TokenType.Gold && type != TokenType.Satchel) {
                int value = remaining.get(type) - bonuses.getOrDefault(type, 0)
                        - tokensToUse.getOrDefault(type, 0);
                remaining.put(type, Math.max(0, value));
            }
        }

        int goldValue = player instanceof TradingPostsPlayer tradingPostsPlayer
                && tradingPostsPlayer.goldTokenWorthTwoTokens.isUnlocked() ? 2 : 1;
        int realGoldPieces = tokensToUse.getOrDefault(TokenType.Gold, 0);
        for (int i = 0; i < realGoldPieces; i++) {
            for (TokenType type : TokenType.values()) {
                if (type != TokenType.Gold && type != TokenType.Satchel
                        && remaining.getOrDefault(type, 0) > 0) {
                    remaining.put(type, Math.max(0, remaining.get(type) - goldValue));
                    break;
                }
            }
        }

        int virtualPieces = 0;
        for (TokenType type : TokenType.values()) {
            if (type != TokenType.Gold && type != TokenType.Satchel) {
                int unpaidForColour = remaining.getOrDefault(type, 0);
                virtualPieces += (unpaidForColour + goldValue - 1) / goldValue;
            }
        }
        return virtualPieces;
    }

}
