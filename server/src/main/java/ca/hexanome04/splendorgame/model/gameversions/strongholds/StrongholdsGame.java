package ca.hexanome04.splendorgame.model.gameversions.strongholds;

import ca.hexanome04.splendorgame.model.CardTier;
import ca.hexanome04.splendorgame.model.DevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.action.actions.SkipConquestAction;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Base game plus nobles and the Strongholds module. */
public class StrongholdsGame extends OrientGame {

    private CardTier pendingRefillTier;
    private boolean mandatoryStrongholdAction;
    private boolean conquestUsedThisTurn;

    /**
     * Create a pure Strongholds game.
     *
     * @param turnCounter initial turn counter
     */
    public StrongholdsGame(int turnCounter) {
        super(GameVersions.BASE_STRONGHOLDS, 15, turnCounter);
    }

    @Override
    protected boolean usesOrientCards() {
        return false;
    }

    @Override
    protected boolean usesNobles() {
        return true;
    }

    @Override
    public Player createPlayer(String name, String colour) {
        return new StrongholdsPlayer(name, colour);
    }

    /**
     * Collect every visible base development card.
     *
     * @return visible cards from all three tiers
     */
    public List<DevelopmentCard> getAllVisibleDevelopmentCards() {
        List<DevelopmentCard> cards = new ArrayList<>();
        cards.addAll(getTier1PurchasableDevelopmentCards());
        cards.addAll(getTier2PurchasableDevelopmentCards());
        cards.addAll(getTier3PurchasableDevelopmentCards());
        return cards;
    }

    /**
     * Check whether the player owns the occupying strongholds, if any.
     *
     * @param player acting player
     * @param card selected visible card
     * @return true when the card is unoccupied or occupied by this player
     */
    public boolean canAccessDevelopmentCard(Player player, DevelopmentCard card) {
        return card.getStrongholdOwner() == null
                || Objects.equals(card.getStrongholdOwner(), player.getName());
    }

    @Override
    public ActionResult validateDevelopmentCardAccess(Player player, DevelopmentCard card) {
        return canAccessDevelopmentCard(player, card)
                ? null : ActionResult.CARD_OCCUPIED_BY_OTHER_STRONGHOLD;
    }

    @Override
    public void beforeDevelopmentCardLeavesBoard(Player player, DevelopmentCard card) {
        if (Objects.equals(card.getStrongholdOwner(), player.getName())) {
            ((StrongholdsPlayer) player).returnStrongholds(card.clearStrongholds());
        }
    }

    @Override
    public boolean takePurchasedDevelopmentCard(DevelopmentCard card) {
        pendingRefillTier = card.getCardTier();
        return takeCardWithoutRefill(card);
    }

    @Override
    public void beginMandatoryStrongholdAction(Player player, DevelopmentCard card,
                                               boolean fromBoard, boolean conquest) {
        mandatoryStrongholdAction = true;
        clearValidActions();
        if (conquest) {
            conquestUsedThisTurn = true;
        }
    }

    /**
     * Place an available stronghold or move one already on the board.
     *
     * @param player acting player
     * @param sourceCardId source when all strongholds are already placed
     * @param targetCardId target card
     * @return whether the change was applied
     */
    public boolean placeOrMoveStronghold(StrongholdsPlayer player, String sourceCardId,
                                         String targetCardId) {
        DevelopmentCard target = visibleCard(targetCardId);
        if (target == null || !canAccessDevelopmentCard(player, target)
                || target.getStrongholdCount() >= 3) {
            return false;
        }
        DevelopmentCard source = null;
        if (player.getAvailableStrongholds() == 0) {
            source = visibleCard(sourceCardId);
            if (source == null || source == target
                    || !Objects.equals(source.getStrongholdOwner(), player.getName())) {
                return false;
            }
            if (!source.removeStronghold(player.getName())) {
                return false;
            }
        } else if (!player.useAvailableStronghold()) {
            return false;
        }
        if (!target.addStronghold(player.getName())) {
            if (source != null) {
                source.addStronghold(player.getName());
            } else {
                player.returnStrongholds(1);
            }
            return false;
        }
        return true;
    }

    /**
     * Remove exactly one opposing stronghold.
     *
     * @param player acting player
     * @param targetCardId occupied target card
     * @return whether one stronghold was removed
     */
    public boolean removeOpponentStronghold(StrongholdsPlayer player, String targetCardId) {
        DevelopmentCard target = visibleCard(targetCardId);
        if (target == null || target.getStrongholdOwner() == null
                || Objects.equals(target.getStrongholdOwner(), player.getName())) {
            return false;
        }
        StrongholdsPlayer owner = (StrongholdsPlayer) getPlayerFromName(target.getStrongholdOwner());
        if (owner == null || !target.removeStronghold(owner.getName())) {
            return false;
        }
        owner.returnStrongholds(1);
        return true;
    }

    /**
     * Check whether the player may conquer a card this turn.
     *
     * @param player acting player
     * @param card target card
     * @return true when the target has three of the player's strongholds
     */
    public boolean canConquer(StrongholdsPlayer player, DevelopmentCard card) {
        return !conquestUsedThisTurn && card != null && card.getStrongholdCount() == 3
                && Objects.equals(card.getStrongholdOwner(), player.getName())
                && getAllVisibleDevelopmentCards().contains(card);
    }

    /**
     * Replace normal actions with the optional conquest choice when available.
     *
     * @param player acting player
     * @return whether conquest choices were installed
     */
    public boolean offerConquestIfAvailable(StrongholdsPlayer player) {
        if (conquestUsedThisTurn || getAllVisibleDevelopmentCards().stream()
                .noneMatch(card -> canConquer(player, card))) {
            return false;
        }
        clearValidActions();
        addValidAction(Actions.CONQUER_CARD);
        addValidAction(Actions.SKIP_CONQUEST);
        return true;
    }

    /**
     * Finish the mandatory action and only then refill the purchased card slot.
     */
    public void completeMandatoryStrongholdAction() {
        if (!mandatoryStrongholdAction) {
            throw new IllegalStateException("No Stronghold action is pending.");
        }
        mandatoryStrongholdAction = false;
        if (pendingRefillTier != null) {
            refillDevelopmentCard(pendingRefillTier);
            pendingRefillTier = null;
        }
    }

    /**
     * Check whether this player turn already included a conquest.
     *
     * @return true after a conquest in the current turn
     */
    public boolean isConquestUsedThisTurn() {
        return conquestUsedThisTurn;
    }

    @Override
    public ArrayList<ActionResult> transformActionResults(Player player, Action action,
                                                           ArrayList<ActionResult> results) {
        if (!results.contains(ActionResult.VALID_ACTION)
                || !results.contains(ActionResult.TURN_COMPLETED)) {
            return results;
        }
        if (!(action instanceof SkipConquestAction)
                && offerConquestIfAvailable((StrongholdsPlayer) player)) {
            results.remove(ActionResult.TURN_COMPLETED);
            results.add(ActionResult.MUST_CHOOSE_CONQUEST);
        }
        return results;
    }

    @Override
    public Player incrementTurn() {
        conquestUsedThisTurn = false;
        mandatoryStrongholdAction = false;
        pendingRefillTier = null;
        return super.incrementTurn();
    }

    private DevelopmentCard visibleCard(String cardId) {
        if (cardId == null) {
            return null;
        }
        return getAllVisibleDevelopmentCards().stream()
                .filter(card -> cardId.equals(card.getId()))
                .findFirst().orElse(null);
    }
}
