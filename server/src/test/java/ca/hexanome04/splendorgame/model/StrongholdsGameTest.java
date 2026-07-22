package ca.hexanome04.splendorgame.model;

import static org.assertj.core.api.Assertions.assertThat;

import ca.hexanome04.splendorgame.control.SplendorTypeAdapter;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import ca.hexanome04.splendorgame.model.action.actions.ConquerCardAction;
import ca.hexanome04.splendorgame.model.action.actions.PlaceOrMoveStrongholdAction;
import ca.hexanome04.splendorgame.model.action.actions.RemoveStrongholdAction;
import ca.hexanome04.splendorgame.model.action.actions.ReserveCardAction;
import ca.hexanome04.splendorgame.model.action.actions.SkipConquestAction;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.strongholds.StrongholdsGame;
import ca.hexanome04.splendorgame.model.gameversions.strongholds.StrongholdsPlayer;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class StrongholdsGameTest {

    @Test
    void initializesPureBaseBoardWithNoblesAndThreeStrongholdsPerPlayer() {
        StrongholdsGame game = createGame();

        assertThat(game.getGameVersion()).isEqualTo(GameVersions.BASE_STRONGHOLDS);
        assertThat(game.getTier1PurchasableDevelopmentCards()).isNotEmpty();
        assertThat(game.getTier1PurchasableOrientCards()).isEmpty();
        assertThat(game.getNobles()).isNotEmpty();
        assertThat(game.getPlayers()).allSatisfy(player ->
                assertThat(((StrongholdsPlayer) player).getAvailableStrongholds()).isEqualTo(3));
    }

    @Test
    void canPlaceStackAndMoveStrongholdsButNeverExceedThree() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer player = firstPlayer(game);
        DevelopmentCard first = game.getTier1PurchasableDevelopmentCards().get(0);
        DevelopmentCard second = game.getTier1PurchasableDevelopmentCards().get(1);

        assertThat(game.placeOrMoveStronghold(player, null, first.getId())).isTrue();
        assertThat(game.placeOrMoveStronghold(player, null, first.getId())).isTrue();
        assertThat(game.placeOrMoveStronghold(player, null, first.getId())).isTrue();
        assertThat(game.placeOrMoveStronghold(player, first.getId(), first.getId())).isFalse();
        assertThat(first.getStrongholdCount()).isEqualTo(3);
        assertThat(player.getAvailableStrongholds()).isZero();

        assertThat(game.placeOrMoveStronghold(player, first.getId(), second.getId())).isTrue();
        assertThat(first.getStrongholdCount()).isEqualTo(2);
        assertThat(second.getStrongholdCount()).isEqualTo(1);
        assertStrongholdInvariant(game, player);
    }

    @Test
    void differentPlayersCannotShareOneCardOrAccessAnOccupiedCard() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer owner = firstPlayer(game);
        StrongholdsPlayer opponent = secondPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);

        assertThat(game.placeOrMoveStronghold(owner, null, card.getId())).isTrue();
        assertThat(game.placeOrMoveStronghold(opponent, null, card.getId())).isFalse();
        assertThat(game.canAccessDevelopmentCard(owner, card)).isTrue();
        assertThat(game.canAccessDevelopmentCard(opponent, card)).isFalse();
    }

    @Test
    void removesExactlyOneEnemyStrongholdAndReturnsItToOwner() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer owner = firstPlayer(game);
        StrongholdsPlayer opponent = secondPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(owner, null, card.getId());
        game.placeOrMoveStronghold(owner, null, card.getId());
        game.placeOrMoveStronghold(owner, null, card.getId());

        assertThat(game.removeOpponentStronghold(opponent, card.getId())).isTrue();
        assertThat(card.getStrongholdCount()).isEqualTo(2);
        assertThat(owner.getAvailableStrongholds()).isEqualTo(1);
        assertThat(game.removeOpponentStronghold(owner, card.getId())).isFalse();
        assertStrongholdInvariant(game, owner);
    }

    @Test
    void purchaseRequiresStrongholdActionBeforeRefillingTheBoard() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer player = firstPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);
        int visibleBefore = game.getTier1PurchasableDevelopmentCards().size();
        HashMap<TokenType, Integer> payment = fundExactPayment(player, card);

        List<ActionResult> purchase = game.takeAction(player.getName(),
                new BuyCardAction(card.getId(), payment, 0));

        assertThat(purchase).contains(ActionResult.VALID_ACTION,
                ActionResult.MUST_CHOOSE_STRONGHOLD_ACTION);
        assertThat(game.getCurValidActions()).containsExactlyInAnyOrder(
                Actions.PLACE_OR_MOVE_STRONGHOLD, Actions.REMOVE_STRONGHOLD);
        assertThat(game.getTier1PurchasableDevelopmentCards()).hasSize(visibleBefore - 1);

        String targetId = game.getTier1PurchasableDevelopmentCards().get(0).getId();
        game.takeAction(player.getName(), new PlaceOrMoveStrongholdAction(null, targetId));
        assertThat(game.getTier1PurchasableDevelopmentCards()).hasSizeBetween(
                visibleBefore - 1, visibleBefore);
        assertStrongholdInvariant(game, player);
    }

    @Test
    void ownerGetsAllStrongholdsBackWhenBuyingOccupiedCard() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer player = firstPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(player, null, card.getId());
        game.placeOrMoveStronghold(player, null, card.getId());
        HashMap<TokenType, Integer> payment = fundExactPayment(player, card);

        game.takeAction(player.getName(), new BuyCardAction(card.getId(), payment, 0));

        assertThat(player.getAvailableStrongholds()).isEqualTo(3);
        assertThat(card.getStrongholdOwner()).isNull();
        assertThat(card.getStrongholdCount()).isZero();
    }

    @Test
    void occupiedCardCannotBeBoughtByOpponent() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer owner = firstPlayer(game);
        StrongholdsPlayer opponent = secondPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(owner, null, card.getId());
        HashMap<TokenType, Integer> payment = fundExactPayment(opponent, card);

        game.incrementTurn();
        List<ActionResult> result = game.takeAction(opponent.getName(),
                new BuyCardAction(card.getId(), payment, 0));

        assertThat(result).containsExactly(ActionResult.CARD_OCCUPIED_BY_OTHER_STRONGHOLD);
        assertThat(opponent.getDevCards()).isEmpty();
    }

    @Test
    void occupiedCardCannotBeReservedByOpponent() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer owner = firstPlayer(game);
        StrongholdsPlayer opponent = secondPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(owner, null, card.getId());

        game.incrementTurn();
        List<ActionResult> result = game.takeAction(opponent.getName(),
                new ReserveCardAction(card.getId()));

        assertThat(result).containsExactly(ActionResult.CARD_OCCUPIED_BY_OTHER_STRONGHOLD);
        assertThat(opponent.getReservedCards()).isEmpty();
        assertThat(game.getTier1PurchasableDevelopmentCards())
                .extracting(DevelopmentCard::getId).contains(card.getId());
    }

    @Test
    void threeStrongholdsOfferOnePaidConquestAndNoInfiniteChain() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer player = firstPlayer(game);
        DevelopmentCard conquestCard = game.getTier1PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(player, null, conquestCard.getId());
        game.placeOrMoveStronghold(player, null, conquestCard.getId());
        game.placeOrMoveStronghold(player, null, conquestCard.getId());

        assertThat(game.offerConquestIfAvailable(player)).isTrue();
        assertThat(game.getCurValidActions()).contains(Actions.CONQUER_CARD, Actions.SKIP_CONQUEST);
        HashMap<TokenType, Integer> conquestPayment = fundExactPayment(player, conquestCard);
        List<ActionResult> conquest = game.takeAction(player.getName(),
                new ConquerCardAction(conquestCard.getId(), conquestPayment, 0));

        assertThat(conquest).contains(ActionResult.MUST_CHOOSE_STRONGHOLD_ACTION);
        assertThat(player.getAvailableStrongholds()).isEqualTo(3);
        assertThat(game.isConquestUsedThisTurn()).isTrue();
        String afterConquestTarget = game.getTier1PurchasableDevelopmentCards().get(0).getId();
        List<ActionResult> end = game.takeAction(player.getName(),
                new PlaceOrMoveStrongholdAction(null, afterConquestTarget));
        assertThat(end).doesNotContain(ActionResult.MUST_CHOOSE_CONQUEST);
    }

    @Test
    void conquestFailsWithoutFullPaymentAndCanBeSkipped() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer player = firstPlayer(game);
        DevelopmentCard card = game.getTier3PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(player, null, card.getId());
        game.placeOrMoveStronghold(player, null, card.getId());
        game.placeOrMoveStronghold(player, null, card.getId());
        game.offerConquestIfAvailable(player);

        List<ActionResult> failed = game.takeAction(player.getName(),
                new ConquerCardAction(card.getId(), emptyPayment(), 0));
        assertThat(failed).contains(ActionResult.INVALID_TOKENS_GIVEN);
        assertThat(game.isConquestUsedThisTurn()).isFalse();

        List<ActionResult> skipped = game.takeAction(player.getName(), new SkipConquestAction());
        assertThat(skipped).contains(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED);
    }

    @Test
    void saveAndLoadPreserveStrongholdsAndPendingState() {
        StrongholdsGame game = createGame();
        StrongholdsPlayer player = firstPlayer(game);
        DevelopmentCard card = game.getTier1PurchasableDevelopmentCards().get(0);
        game.placeOrMoveStronghold(player, null, card.getId());
        Gson gson = SplendorTypeAdapter.createGson();

        String json = gson.toJson(game, Game.class);
        Game restoredBase = gson.fromJson(json, Game.class);
        StrongholdsGame restored = (StrongholdsGame) restoredBase;
        StrongholdsPlayer restoredPlayer = (StrongholdsPlayer) restored.getPlayerFromName(player.getName());
        DevelopmentCard restoredCard = (DevelopmentCard) restored.getCardFromId(card.getId());

        assertThat(restoredPlayer.getAvailableStrongholds()).isEqualTo(2);
        assertThat(restoredCard.getStrongholdOwner()).isEqualTo(player.getName());
        assertThat(restoredCard.getStrongholdCount()).isEqualTo(1);
        assertStrongholdInvariant(restored, restoredPlayer);
    }

    private StrongholdsGame createGame() {
        StrongholdsGame game = new StrongholdsGame(0);
        game.setPlayers(new ArrayList<>(List.of(
                new StrongholdsPlayer("Player1", "#ff0000"),
                new StrongholdsPlayer("Player2", "#0000ff"))));
        game.createSplendorBoard();
        game.initBoard();
        return game;
    }

    private StrongholdsPlayer firstPlayer(StrongholdsGame game) {
        return (StrongholdsPlayer) game.getPlayers().get(0);
    }

    private StrongholdsPlayer secondPlayer(StrongholdsGame game) {
        return (StrongholdsPlayer) game.getPlayers().get(1);
    }

    private HashMap<TokenType, Integer> fundExactPayment(Player player, DevelopmentCard card) {
        HashMap<TokenType, Integer> payment = emptyPayment();
        for (TokenType type : List.of(TokenType.Red, TokenType.Blue, TokenType.Green,
                TokenType.White, TokenType.Brown)) {
            int amount = card.getTokenCost().getOrDefault(type, 0);
            payment.put(type, amount);
        }
        player.addTokens(payment);
        return payment;
    }

    private HashMap<TokenType, Integer> emptyPayment() {
        HashMap<TokenType, Integer> payment = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            payment.put(type, 0);
        }
        return payment;
    }

    private void assertStrongholdInvariant(StrongholdsGame game, StrongholdsPlayer player) {
        int placed = game.getAllVisibleDevelopmentCards().stream()
                .filter(card -> player.getName().equals(card.getStrongholdOwner()))
                .mapToInt(DevelopmentCard::getStrongholdCount)
                .sum();
        assertThat(player.getAvailableStrongholds() + placed).isEqualTo(3);
    }
}
