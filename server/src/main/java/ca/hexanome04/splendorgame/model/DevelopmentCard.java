package ca.hexanome04.splendorgame.model;

import java.util.HashMap;

/**
 * Abstract class that represents a development card.
 */
public abstract class DevelopmentCard extends Card {

    private TokenType tokenType;
    private final int bonus;
    private final CardTier cardTier;
    private Boolean reservedFaceDown = Boolean.TRUE;
    private String strongholdOwner;
    private int strongholdCount;

    /**
     * Creates a development card from the class in which this was called super from.
     *
     * @param tokenType      The type of token associated to this development card.
     * @param bonus          The integer token bonus associated to this development card.
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     * @param id             ID associated to this card.
     * @param cardTier       Tier of the development card (e.g. 1, 2, or 3).
     */
    public DevelopmentCard(CardTier cardTier, TokenType tokenType, int bonus, int prestigePoints, CostType costType,
                           HashMap<TokenType, Integer> tokenCost, String id) {
        super(prestigePoints, costType, tokenCost, id);
        this.tokenType = tokenType;
        this.bonus = bonus;
        this.cardTier = cardTier;
    }

    /**
     * Returns the type of bonus associated with this card.
     *
     * @return The type of token.
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * Sets the token type of this card, but only if it's of type satchel to begin with.
     *
     * @param tokenType The type of token to be assigned to this card.
     */

    public void setTokenType(TokenType tokenType) {
        if (this.tokenType == TokenType.Satchel) {
            this.tokenType = tokenType;
        }
    }

    /**
     * Gets the number of bonuses associated to this card.
     *
     * @return The integer value of bonus(es) for this card.
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Whether this card entered a player's reserve from the face-down deck.
     * Missing values from older saves are treated as hidden to avoid leaking
     * information that may have been private.
     *
     * @return true when only the owner may see the card face
     */
    public boolean isReservedFaceDown() {
        return !Boolean.FALSE.equals(reservedFaceDown);
    }

    /**
     * Record whether this reservation came from a face-down deck.
     *
     * @param reservedFaceDown true for a blind reservation, false for a public card
     */
    public void setReservedFaceDown(boolean reservedFaceDown) {
        this.reservedFaceDown = reservedFaceDown;
    }

    /**
     * Obtain the tier containing this development card.
     *
     * @return card tier
     */
    public CardTier getCardTier() {
        return cardTier;
    }

    /**
     * Obtain the player whose strongholds occupy this card.
     *
     * @return owner name, or null when unoccupied
     */
    public String getStrongholdOwner() {
        return strongholdOwner;
    }

    /**
     * Obtain the number of strongholds on this card.
     *
     * @return stronghold count
     */
    public int getStrongholdCount() {
        return strongholdCount;
    }

    /**
     * Add one stronghold while enforcing a single owner and a maximum of three.
     *
     * @param owner player placing the stronghold
     * @return whether the stronghold was added
     */
    public boolean addStronghold(String owner) {
        if (owner == null || strongholdCount >= 3
                || (strongholdOwner != null && !strongholdOwner.equals(owner))) {
            return false;
        }
        strongholdOwner = owner;
        strongholdCount++;
        return true;
    }

    /**
     * Remove one stronghold owned by the specified player.
     *
     * @param owner expected owner
     * @return whether a stronghold was removed
     */
    public boolean removeStronghold(String owner) {
        if (strongholdCount == 0 || !java.util.Objects.equals(strongholdOwner, owner)) {
            return false;
        }
        strongholdCount--;
        if (strongholdCount == 0) {
            strongholdOwner = null;
        }
        return true;
    }

    /**
     * Remove every stronghold from this card.
     *
     * @return number of removed strongholds
     */
    public int clearStrongholds() {
        int removed = strongholdCount;
        strongholdCount = 0;
        strongholdOwner = null;
        return removed;
    }

}
