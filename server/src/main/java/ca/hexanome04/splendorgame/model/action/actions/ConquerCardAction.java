package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.DevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.strongholds.StrongholdsGame;
import ca.hexanome04.splendorgame.model.gameversions.strongholds.StrongholdsPlayer;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;

/** Pay for and gain a card occupied by three of the current player's Strongholds. */
public class ConquerCardAction extends BuyCardAction {

    /**
     * Build a paid conquest action.
     *
     * @param cardId conquered card identifier
     * @param tokens real tokens selected as payment
     * @param virtualGoldPieces virtual Gold pieces selected as payment
     */
    public ConquerCardAction(String cardId, HashMap<TokenType, Integer> tokens,
                             int virtualGoldPieces) {
        super(cardId, tokens, virtualGoldPieces);
    }

    /** Construct an empty conquest action for JSON decoding. */
    public ConquerCardAction() {
        this("", new HashMap<>(), 0);
    }

    @Override
    protected Actions requiredAction() {
        return Actions.CONQUER_CARD;
    }

    @Override
    protected boolean isConquestPurchase() {
        return true;
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {
        if (!(game instanceof StrongholdsGame strongholds)) {
            return List.of(ActionResult.INVALID_CONQUEST);
        }
        DevelopmentCard card = (DevelopmentCard) game.getCardFromId(buyCardId);
        if (!strongholds.canConquer((StrongholdsPlayer) player, card)) {
            return List.of(ActionResult.INVALID_CONQUEST);
        }
        return super.run(game, player);
    }

    @Override
    public Action decodeAction(JsonObject json) {
        super.decodeAction(json);
        return this;
    }
}
