package ca.hexanome04.splendorgame.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.actions.ReserveCardAction;
import ca.hexanome04.splendorgame.model.action.actions.TakeTokenAction;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;
import java.util.HashMap;
import java.util.List;
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

        assertEquals(List.of(ActionResult.INVALID_TOKENS_GIVEN), result);
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
        assertEquals(1, player.getTokens().get(TokenType.Gold));
        assertEquals(3, game.getTier1PurchasableDevelopmentCards().size());
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
}
