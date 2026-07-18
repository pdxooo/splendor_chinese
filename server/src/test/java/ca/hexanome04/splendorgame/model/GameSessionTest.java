package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameSession.
 */
public class GameSessionTest {


    /**
     * Test the getGame method.
     *
     * @throws FileNotFoundException Throws exception if loading from file fails
     */
    @DisplayName("Test the getGame method.")
    @Test
    public void testGetGame() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        GameSession session = new GameSession("12345", "Player1", "MyGame");
        session.setGame(game);

        assertEquals(session.getGame(), game);
    }

    /**
     * Test the getSessionId method.
     */
    @DisplayName("Test the getSessionId method.")
    @Test
    public void testGetSessionId() {
        GameSession session = new GameSession("12345", "Player1", "MyGame");

        assertEquals(session.getSessionId(), "12345");
    }

    /**
     * Test the getCostType method.
     */
    @DisplayName("Test the getCostType method.")
    @Test
    public void testHasSessionLaunched() {
        GameSession session = new GameSession("12345", "Player1", "MyGame");

        assertEquals(session.hasGameLaunched(), false);
    }

    @Test
    @DisplayName("Expired turns advance once and receive a new deadline")
    public void testTurnTimeout() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);
        GameSession session = new GameSession("12345", "Player1", "MyGame", 30);
        session.setGame(game);
        int previousTurn = game.getTurnCounter();

        assertTrue(session.advanceTurnIfExpired(session.getTurnDeadlineEpochMillis() + 1));
        assertNotEquals(previousTurn, game.getTurnCounter());
        assertFalse(session.advanceTurnIfExpired(session.getTurnDeadlineEpochMillis() - 1));
    }

    @Test
    @DisplayName("Chat retains safe room history")
    public void testChatMessages() {
        GameSession session = new GameSession("12345", "Player1", "MyGame");
        session.addChatMessage("Player1", "你好");

        assertEquals(1, session.getChatMessages().size());
        assertEquals("Player1", session.getChatMessages().get(0).sender());
        assertEquals("你好", session.getChatMessages().get(0).text());
    }

}
