package ca.hexanome04.splendorgame.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassicGameTest {

    @Test
    @DisplayName("Classic game contains base cards but no Orient cards")
    void classicGameHasNoOrientCards() {
        OrientGame game = new OrientGame(GameVersions.BASE, 15, 0);
        game.setPlayers(List.of(
                new OrientPlayer("Player1", "ffffff"),
                new OrientPlayer("Player2", "000000")
        ));

        game.createSplendorBoard();
        game.initBoard();

        assertEquals(GameVersions.BASE, game.getGameVersion());
        assertEquals(4, game.getTier1PurchasableDevelopmentCards().size());
        assertEquals(4, game.getTier2PurchasableDevelopmentCards().size());
        assertEquals(4, game.getTier3PurchasableDevelopmentCards().size());
        assertEquals(0, game.getTier1PurchasableOrientCards().size());
        assertEquals(0, game.getTier2PurchasableOrientCards().size());
    }
}
