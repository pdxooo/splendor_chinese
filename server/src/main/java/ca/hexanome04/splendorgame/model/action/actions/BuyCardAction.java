package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.*;

/**
 * Perform the buy card action.
 */
public class BuyCardAction extends Action {

    private String buyCardId;
    private HashMap<TokenType, Integer> selectedTokens;
    private int virtualGoldPieces;
    private List<String> burnCardIds;

    /**
     * Construct a buy card action.
     *
     * @param buyCardId card id to be bought from game
     * @param selectedTokens tokens that are used to buy the card
     */
    public BuyCardAction(String buyCardId, HashMap<TokenType, Integer> selectedTokens) {
        this(buyCardId, selectedTokens, -1);
    }

    /**
     * Construct a buy-card action with explicit virtual Gold selection.
     *
     * @param buyCardId card id
     * @param selectedTokens real tokens used
     * @param virtualGoldPieces virtual Gold pieces used, or -1 for legacy automatic selection
     */
    public BuyCardAction(String buyCardId, HashMap<TokenType, Integer> selectedTokens,
                         int virtualGoldPieces) {
        super(Actions.BUY_CARD);
        this.buyCardId = buyCardId;
        this.selectedTokens = selectedTokens;
        this.virtualGoldPieces = virtualGoldPieces;
        this.burnCardIds = null;
    }

    /**
     * Construct a buy-card action with an explicit choice of bonus cards to discard.
     *
     * @param buyCardId card id
     * @param selectedTokens real tokens used
     * @param virtualGoldPieces virtual Gold pieces used
     * @param burnCardIds purchased card ids selected for an Orient bonus cost
     */
    public BuyCardAction(String buyCardId, HashMap<TokenType, Integer> selectedTokens,
                         int virtualGoldPieces, List<String> burnCardIds) {
        this(buyCardId, selectedTokens, virtualGoldPieces);
        this.burnCardIds = burnCardIds == null ? null : new ArrayList<>(burnCardIds);
    }

    /**
     * Construct a buy card action (to be filled with info from decoder).
     */
    public BuyCardAction() {
        this("", new HashMap<>());
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        // get card from board
        // should be a development card (hopefully)
        DevelopmentCard dc = (DevelopmentCard) game.getCardFromId(this.buyCardId);

        // try reserved
        boolean wasReserved = false;
        if (dc == null) {
            dc = player.getReservedDevelopmentCard(this.buyCardId);
            wasReserved = dc != null;
        }

        if (dc == null) {
            throw new SplendorException("Card with id '" + this.buyCardId + "' does not exist.");
        }

        // for orient, can only buy satchel if you own another card with a bonus
        if (dc.getTokenType() == TokenType.Satchel) {
            boolean hasAnotherBonusCard = false;
            for (DevelopmentCard c : player.getDevCards()) {
                if (c.getTokenType() != null && c.getTokenType() != TokenType.Satchel
                        && c.getTokenType() != TokenType.Gold) {
                    // doesn't fail the checks above? should be good maybe
                    hasAnotherBonusCard = true;
                    break;
                }
            }
            if (!hasAnotherBonusCard) {
                throw new SplendorException("Cannot purchase card with satchel bonus "
                        + "without having another purchased card with a bonus.");
            }
        }


        ArrayList<ActionResult> result = new ArrayList<>();

        List<DevelopmentCard> selectedBurnCards = null;
        if (dc instanceof OrientDevelopmentCard orientCard
                && orientCard.getCostType() == CostType.Bonus && burnCardIds != null) {
            selectedBurnCards = validateBurnCards(player, orientCard);
            if (selectedBurnCards == null) {
                result.add(ActionResult.INVALID_TOKENS_GIVEN);
                return result;
            }
        }

        int virtualGoldPiecesUsed = virtualGoldPieces < 0
                ? dc.getVirtualGoldPiecesUsed(player, selectedTokens) : virtualGoldPieces;
        boolean purchasable = virtualGoldPieces < 0
                ? dc.isPurchasable(player, selectedTokens)
                : dc.isPurchasable(player, selectedTokens, virtualGoldPiecesUsed);
        if (!purchasable) {
            result.add(ActionResult.INVALID_TOKENS_GIVEN);
            return result;
        }

        if (virtualGoldPiecesUsed > 0) {
            discardVirtualGoldCards(player, virtualGoldPiecesUsed);
        }

        player.addCard(dc);
        if (wasReserved) {
            player.removeReservedCard(dc);
        } else {
            game.takeCard(dc);
        }

        if (dc.getTokenType() != TokenType.Satchel) {
            player.addBonus(dc.getTokenType(), dc.getBonus());
        }
        player.addPrestigePoints(dc.getPrestigePoints());

        game.addTokens(selectedTokens);
        player.removeTokens(selectedTokens);

        if (dc.getClass().equals(OrientDevelopmentCard.class)) {
            OrientDevelopmentCard orientCard = (OrientDevelopmentCard) dc;
            if (orientCard.getCostType() == CostType.Bonus) {
                if (selectedBurnCards == null) {
                    player.burnBonuses(orientCard.getTokenCost());
                } else {
                    discardSelectedBonusCards(player, selectedBurnCards);
                }
            }
            if (orientCard.getReserveNoble() && game instanceof OrientGame og && og.getNobles().size() > 0) {
                result.add(ActionResult.MUST_RESERVE_NOBLE);
            }
            if (orientCard.getCascadeType() == CascadeType.Tier1 && game instanceof OrientGame og
                    && (og.getTier1PurchasableDevelopmentCards().size() > 0
                    || og.getTier1PurchasableOrientCards().size() > 0)) {
                result.add(ActionResult.MUST_CHOOSE_CASCADE_CARD_TIER_1);

                // Differentiation between up and down needed? Unclear
            } else if (orientCard.getCascadeType() == CascadeType.Tier2 && game instanceof OrientGame og
                    && (og.getTier2PurchasableDevelopmentCards().size() > 0
                    || og.getTier2PurchasableOrientCards().size() > 0)) {
                result.add(ActionResult.MUST_CHOOSE_CASCADE_CARD_TIER_2);

            }
            if (orientCard.getTokenType() == TokenType.Satchel) {
                result.add(ActionResult.MUST_CHOOSE_TOKEN_TYPE);
            }
        }

        // Take extra token if player has unlocked power 1
        if (player instanceof TradingPostsPlayer tpp) {
            if (tpp.extraTokenAfterPurchase.isUnlocked()) {
                result.add(ActionResult.MUST_TAKE_EXTRA_TOKEN_AFTER_PURCHASE);
            }
        }

        if (result.size() == 0) {
            result.add(ActionResult.TURN_COMPLETED);
        }

        if (game.getCurValidActions().contains(Actions.BUY_CARD)) {
            result.add(ActionResult.VALID_ACTION);
        }

        // clear list of current player valid actions
        game.clearValidActions();

        return result;
    }

    /**
     * Discard the Orient card or cards supplying virtual Gold for this purchase.
     * Each such card supplies two pieces and the whole card is discarded even
     * when only one of its pieces is required.
     *
     * @param player player spending virtual Gold
     * @param piecesUsed number of virtual Gold pieces required
     */
    private void discardVirtualGoldCards(Player player, int piecesUsed) {
        int cardsToDiscard = (piecesUsed + 1) / 2;
        HashMap<TokenType, Integer> bonusesToRemove = new HashMap<>();
        bonusesToRemove.put(TokenType.Gold, cardsToDiscard * 2);
        player.removeBonuses(bonusesToRemove);

        for (DevelopmentCard card : new ArrayList<>(player.getDevCards())) {
            if (cardsToDiscard == 0) {
                break;
            }
            if (card.getTokenType() == TokenType.Gold) {
                player.removeCard(card);
                cardsToDiscard--;
            }
        }
    }

    private List<DevelopmentCard> validateBurnCards(Player player, OrientDevelopmentCard card) {
        if (burnCardIds.isEmpty() || new HashSet<>(burnCardIds).size() != burnCardIds.size()
                || burnCardIds.size() > 2) {
            return null;
        }
        HashMap<TokenType, Integer> selectedBonuses = new HashMap<>();
        List<DevelopmentCard> selectedCards = new ArrayList<>();
        for (String cardId : burnCardIds) {
            DevelopmentCard selected = player.getPurchasedDevelopmentCard(cardId);
            if (selected == null || selected.getTokenType() == null
                    || selected.getTokenType() == TokenType.Gold
                    || selected.getTokenType() == TokenType.Satchel) {
                return null;
            }
            selectedCards.add(selected);
            selectedBonuses.merge(selected.getTokenType(), selected.getBonus(), Integer::sum);
        }
        for (TokenType type : TokenType.values()) {
            if (!Objects.equals(selectedBonuses.getOrDefault(type, 0),
                    card.getTokenCost().getOrDefault(type, 0))) {
                return null;
            }
        }
        return selectedCards;
    }

    private void discardSelectedBonusCards(Player player, List<DevelopmentCard> cards) {
        for (DevelopmentCard card : cards) {
            HashMap<TokenType, Integer> bonus = new HashMap<>();
            bonus.put(card.getTokenType(), card.getBonus());
            player.removeBonuses(bonus);
            player.addPrestigePoints(-card.getPrestigePoints());
            player.removeCard(card);
        }
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.buyCardId = jobj.get("cardId").getAsString();

        JsonObject tokens = jobj.get("tokens").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : tokens.entrySet()) {
            TokenType type = TokenType.valueOf(entry.getKey());
            int amount = entry.getValue().getAsInt();

            selectedTokens.put(type, amount);
        }
        this.virtualGoldPieces = jobj.has("virtualGoldPieces")
                ? jobj.get("virtualGoldPieces").getAsInt() : -1;
        if (jobj.has("burnCardIds")) {
            this.burnCardIds = new ArrayList<>();
            for (JsonElement element : jobj.getAsJsonArray("burnCardIds")) {
                this.burnCardIds.add(element.getAsString());
            }
        }

        return this;
    }

}
