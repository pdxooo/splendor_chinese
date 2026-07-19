package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.CardTier;
import ca.hexanome04.splendorgame.model.DevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorException;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Perform the reserve card action.
 */
public class ReserveCardAction extends Action {

    private String reserveCardId;
    private HashMap<TokenType, Integer> putBackTokens;

    /**
     * Construct a reserve card action.
     *
     * @param reserveCardId card id to be reserved from game
     */
    public ReserveCardAction(String reserveCardId) {
        this(reserveCardId, new HashMap<>());
    }

    /**
     * Construct a reserve action with tokens returned after receiving gold.
     *
     * @param reserveCardId card id, or a DECK_TIER_* id for a blind reservation
     * @param putBackTokens tokens returned so the player finishes with at most ten
     */
    public ReserveCardAction(String reserveCardId, HashMap<TokenType, Integer> putBackTokens) {
        super(Actions.RESERVE_CARD);
        this.reserveCardId = reserveCardId;
        this.putBackTokens = putBackTokens;
    }

    /**
     * Construct a reserve card action (to be filled with info from decoder).
     */
    public ReserveCardAction() {
        this("");
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        ArrayList<ActionResult> result = new ArrayList<>();

        if (player.getReservedCards().size() >= 3) {
            result.add(ActionResult.MAXIMUM_CARDS_RESERVED);
            return result;
        }

        int playerTokenCount = player.getTokens().values().stream().mapToInt(Integer::intValue).sum();
        boolean goldAvailable = game.getTokens().getOrDefault(TokenType.Gold, 0) > 0;
        int goldToReceive = goldAvailable ? 1 : 0;
        int requiredReturnCount = Math.max(0, playerTokenCount + goldToReceive - 10);
        int requestedReturnCount = 0;

        for (Map.Entry<TokenType, Integer> entry : putBackTokens.entrySet()) {
            int amount = entry.getValue();
            if (amount < 0) {
                result.add(ActionResult.INVALID_TOKENS_GIVEN);
                return result;
            }
            requestedReturnCount += amount;
            int availableToReturn = player.getTokens().getOrDefault(entry.getKey(), 0);
            if (entry.getKey() == TokenType.Gold) {
                availableToReturn += goldToReceive;
            }
            if (amount > availableToReturn) {
                result.add(ActionResult.NOT_ENOUGH_TOKENS_IN_INVENTORY);
                return result;
            }
        }

        if (requestedReturnCount != requiredReturnCount) {
            result.add(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY);
            return result;
        }

        boolean faceDownReservation = this.reserveCardId.startsWith("DECK_TIER_");
        DevelopmentCard dc;
        if (faceDownReservation) {
            CardTier tier = CardTier.valueOf(this.reserveCardId.substring("DECK_".length()));
            dc = game.takeTopDevelopmentCard(tier);
        } else {
            dc = (DevelopmentCard) game.getCardFromId(this.reserveCardId);
        }
        if (dc == null) {
            throw new SplendorException("Card with id '" + this.reserveCardId + "' does not exist.");
        }

        // no error handling
        if (!faceDownReservation) {
            game.takeCard(dc);
        }
        dc.setReservedFaceDown(faceDownReservation);
        player.reserveCard(dc);


        if (goldAvailable) {
            HashMap<TokenType, Integer> goldToken = new HashMap<>();
            goldToken.put(TokenType.Gold, 1);
            game.removeTokens(goldToken);
            player.addTokens(goldToken);
        }
        if (!putBackTokens.isEmpty()) {
            player.removeTokens(putBackTokens);
            game.addTokens(putBackTokens);
        }


        if (game.getCurValidActions().contains(Actions.RESERVE_CARD)) {
            result.add(ActionResult.VALID_ACTION);
        }
        result.add(ActionResult.TURN_COMPLETED);

        // clear list of current player valid actions
        game.clearValidActions();

        return result;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.reserveCardId = jobj.get("cardId").getAsString();
        if (jobj.has("putBackTokens") && jobj.get("putBackTokens").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry
                    : jobj.getAsJsonObject("putBackTokens").entrySet()) {
                TokenType type = TokenType.valueOf(entry.getKey());
                putBackTokens.put(type, entry.getValue().getAsInt());
            }
        }
        return this;

    }

}
