package ca.hexanome04.splendorgame.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import ca.hexanome04.splendorgame.model.action.actions.ReserveCardAction;
import ca.hexanome04.splendorgame.model.action.actions.TakeTokenAction;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassicGameTest {

    @Test
    @DisplayName("Classic game contains base cards but no Orient cards")
    void classicGameHasNoOrientCards() {
        OrientGame game = createClassicGame(0);

        assertEquals(GameVersions.BASE, game.getGameVersion());
        assertEquals(3, game.getTier1PurchasableDevelopmentCards().size());
        assertEquals(4, game.getTier2PurchasableDevelopmentCards().size());
        assertEquals(2, game.getTier3PurchasableDevelopmentCards().size());
        assertEquals(0, game.getTier1PurchasableOrientCards().size());
        assertEquals(0, game.getTier2PurchasableOrientCards().size());
    }

    @Test
    @DisplayName("Classic game requires three different gems when they are available")
    void classicGameRejectsTakingOnlyTwoDifferentGems() {
        OrientGame game = createClassicGame(0);
        HashMap<TokenType, Integer> take = new HashMap<>();
        take.put(TokenType.Red, 1);
        take.put(TokenType.Blue, 1);

        List<ActionResult> result = game.takeAction("Player1", new TakeTokenAction(take, new HashMap<>()));

        assertEquals(List.of(ActionResult.MUST_TAKE_THREE_DIFFERENT_TOKENS), result);
    }

    @Test
    @DisplayName("A player at ten tokens may take first and then return the excess")
    void classicGameAllowsReturningExcessAfterTaking() {
        OrientGame game = createClassicGame(0);
        Player player = game.getPlayerFromName("Player1");
        HashMap<TokenType, Integer> startingTokens = new HashMap<>();
        startingTokens.put(TokenType.Red, 2);
        startingTokens.put(TokenType.Blue, 2);
        startingTokens.put(TokenType.Green, 2);
        startingTokens.put(TokenType.White, 2);
        startingTokens.put(TokenType.Brown, 2);
        player.addTokens(startingTokens);

        HashMap<TokenType, Integer> take = new HashMap<>();
        take.put(TokenType.Red, 1);
        take.put(TokenType.Blue, 1);
        take.put(TokenType.Green, 1);
        HashMap<TokenType, Integer> putBack = new HashMap<>();
        putBack.put(TokenType.White, 2);
        putBack.put(TokenType.Brown, 1);

        List<ActionResult> result = game.takeAction("Player1", new TakeTokenAction(take, putBack));
        int finalTokenCount = player.getTokens().values().stream().mapToInt(Integer::intValue).sum();

        assertEquals(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED), result);
        assertEquals(10, finalTokenCount);
    }

    @Test
    @DisplayName("Classic game allows reserving a face-down card and grants gold")
    void classicGameAllowsBlindReservation() {
        OrientGame game = createClassicGame(0);
        addFaceDownTier1Card(game);

        List<ActionResult> result = game.takeAction("Player1", new ReserveCardAction("DECK_TIER_1"));
        Player player = game.getPlayerFromName("Player1");

        assertEquals(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED), result);
        assertEquals(1, player.getReservedCards().size());
        assertTrue(player.getReservedCards().get(0).isReservedFaceDown());
        assertEquals(1, player.getTokens().get(TokenType.Gold));
        assertEquals(3, game.getTier1PurchasableDevelopmentCards().size());
    }

    @Test
    @DisplayName("A player at ten tokens receives reserve gold and returns one token")
    void classicGameReturnsOneTokenAfterReserveGold() {
        OrientGame game = createClassicGame(0);
        Player player = game.getPlayerFromName("Player1");
        addTenTokens(player);
        String cardId = game.getTier1PurchasableDevelopmentCards().get(0).getId();
        int bankGoldBefore = game.getTokens().get(TokenType.Gold);
        int bankRedBefore = game.getTokens().get(TokenType.Red);

        HashMap<TokenType, Integer> returned = new HashMap<>(Map.of(TokenType.Red, 1));
        List<ActionResult> result = game.takeAction("Player1", new ReserveCardAction(cardId, returned));
        int finalTokenCount = player.getTokens().values().stream().mapToInt(Integer::intValue).sum();

        assertEquals(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED), result);
        assertEquals(10, finalTokenCount);
        assertEquals(1, player.getTokens().get(TokenType.Gold));
        assertEquals(bankGoldBefore - 1, game.getTokens().get(TokenType.Gold));
        assertEquals(bankRedBefore + 1, game.getTokens().get(TokenType.Red));
        assertEquals(List.of(cardId), player.getReservedCards().stream().map(Card::getId).toList());
    }

    @Test
    @DisplayName("A player may return the gold just received when reserving at ten tokens")
    void classicGameCanReturnReserveGold() {
        OrientGame game = createClassicGame(0);
        Player player = game.getPlayerFromName("Player1");
        addTenTokens(player);
        String cardId = game.getTier1PurchasableDevelopmentCards().get(0).getId();
        int bankGoldBefore = game.getTokens().get(TokenType.Gold);

        HashMap<TokenType, Integer> returned = new HashMap<>(Map.of(TokenType.Gold, 1));
        List<ActionResult> result = game.takeAction("Player1", new ReserveCardAction(cardId, returned));

        assertEquals(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED), result);
        assertEquals(10, player.getTokens().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(0, player.getTokens().get(TokenType.Gold));
        assertEquals(bankGoldBefore, game.getTokens().get(TokenType.Gold));
    }

    @Test
    @DisplayName("A reservation at ten tokens is rejected until one token is returned")
    void classicGameRejectsReserveWithoutRequiredReturn() {
        OrientGame game = createClassicGame(0);
        Player player = game.getPlayerFromName("Player1");
        addTenTokens(player);
        String cardId = game.getTier1PurchasableDevelopmentCards().get(0).getId();
        int bankGoldBefore = game.getTokens().get(TokenType.Gold);

        List<ActionResult> result = game.takeAction("Player1", new ReserveCardAction(cardId));

        assertEquals(List.of(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY), result);
        assertEquals(0, player.getReservedCards().size());
        assertEquals(10, player.getTokens().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(bankGoldBefore, game.getTokens().get(TokenType.Gold));
    }

    @Test
    @DisplayName("A purchased card permanently discounts a later purchase")
    void classicGameAppliesPurchasedCardBonusToNextPurchase() {
        OrientGame game = createClassicGame(0);
        Player player = game.getPlayerFromName("Player1");
        HashMap<TokenType, Integer> tokens = new HashMap<>();
        tokens.put(TokenType.Green, 1);
        tokens.put(TokenType.Blue, 2);
        tokens.put(TokenType.Red, 1);
        player.addTokens(tokens);

        game.takeAction("Player1", new BuyCardAction("05", new HashMap<>(Map.of(TokenType.Green, 1))));
        game.takeAction("Player2", new TakeTokenAction(new HashMap<>(Map.of(
                TokenType.White, 1, TokenType.Blue, 1, TokenType.Brown, 1)), new HashMap<>()));
        List<ActionResult> secondPurchase = game.takeAction("Player1", new BuyCardAction("03",
                new HashMap<>(Map.of(TokenType.Blue, 2, TokenType.Red, 1))));

        assertEquals(List.of(ActionResult.TURN_COMPLETED, ActionResult.VALID_ACTION), secondPurchase);
        assertEquals(2, player.getBonuses().get(TokenType.Green));
        assertEquals(0, player.getTokens().get(TokenType.Green));
        assertEquals(0, player.getTokens().get(TokenType.Blue));
        assertEquals(0, player.getTokens().get(TokenType.Red));
    }

    @Test
    @DisplayName("A single qualifying noble visits automatically at end of turn")
    void classicGameAutomaticallyAwardsSingleQualifyingNoble() {
        OrientGame game = createClassicGame(0);
        Player player = game.getPlayerFromName("Player1");
        player.addBonus(TokenType.Blue, 2);
        player.addBonus(TokenType.Green, 2);
        player.addBonus(TokenType.Red, 1);
        player.addBonus(TokenType.Brown, 1);

        game.takeAction("Player1", new TakeTokenAction(new HashMap<>(Map.of(
                TokenType.White, 1, TokenType.Blue, 1, TokenType.Green, 1)), new HashMap<>()));

        assertEquals(List.of("98"), player.getNobles().stream().map(Card::getId).toList());
        assertEquals(3, player.getPrestigePoints());
    }

    @Test
    @DisplayName("Classic game finishes the round and ranks by prestige first")
    void classicGameFinishesRoundAndUsesCorrectWinnerOrder() {
        OrientGame unfinishedRound = createClassicGame(0);
        Player first = unfinishedRound.getPlayerFromName("Player1");
        first.addPrestigePoints(15);
        unfinishedRound.addPlayersWhoCanWin(first);
        assertNull(unfinishedRound.checkForWin());

        OrientGame finishedRound = createClassicGame(1);
        Player lowerScore = finishedRound.getPlayerFromName("Player1");
        Player higherScore = finishedRound.getPlayerFromName("Player2");
        lowerScore.addPrestigePoints(15);
        higherScore.addPrestigePoints(16);
        finishedRound.addPlayersWhoCanWin(lowerScore);
        finishedRound.addPlayersWhoCanWin(higherScore);

        assertEquals(List.of(higherScore), finishedRound.checkForWin());
    }

    @Test
    @DisplayName("Prestige ties are broken by fewest purchased development cards")
    void classicGameBreaksPrestigeTieWithFewestDevelopmentCards() {
        OrientGame game = createClassicGame(1);
        Player moreCards = game.getPlayerFromName("Player1");
        Player fewerCards = game.getPlayerFromName("Player2");
        moreCards.addPrestigePoints(15);
        fewerCards.addPrestigePoints(15);
        moreCards.addCard(new RegDevelopmentCard(CardTier.TIER_1, TokenType.White, 1,
                0, CostType.Token, new HashMap<>(), "tie-break-card"));
        game.addPlayersWhoCanWin(moreCards);
        game.addPlayersWhoCanWin(fewerCards);

        assertEquals(List.of(fewerCards), game.checkForWin());
    }

    private OrientGame createClassicGame(int turnCounter) {
        OrientGame game = new OrientGame(GameVersions.BASE, 15, turnCounter);
        game.setPlayers(List.of(
                new OrientPlayer("Player1", "ffffff"),
                new OrientPlayer("Player2", "000000")
        ));
        game.createSplendorBoard();
        game.initBoard();
        return game;
    }

    private void addFaceDownTier1Card(OrientGame game) {
        try {
            var field = OrientGame.class.getDeclaredField("tier1Deck");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Deck<RegDevelopmentCard> deck = (Deck<RegDevelopmentCard>) field.get(game);
            deck.add(game.getTier1PurchasableDevelopmentCards().get(0));
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Unable to prepare a face-down test card", exception);
        }
    }

    private void addTenTokens(Player player) {
        player.addTokens(new HashMap<>(Map.of(
                TokenType.Red, 2,
                TokenType.Blue, 2,
                TokenType.Green, 2,
                TokenType.White, 2,
                TokenType.Brown, 2
        )));
    }
}
