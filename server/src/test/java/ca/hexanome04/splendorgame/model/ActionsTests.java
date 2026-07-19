package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.actions.*;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.action.ActionResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests for doing actions in a game.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActionsTests {

    @DisplayName("Ensure players cannot buy a card with insufficient tokens in their inventory.")
    @Test
    void testPlayerBuyCard_InsufficientTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 3);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // should be no more tier 1 cards available to be purchased
        assertThat(p1.getDevCards().isEmpty()).isTrue();
        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players can buy a card when sufficient tokens provided.")
    @Test
    void testPlayerBuyCard_SufficientTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);

        p1.addTokens(tokensToUse);

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using normal and gold tokens.")
    @Test
    void testPlayerBuyCard_UseGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Gold, 1);
        tokensToAdd.put(TokenType.Red, 2);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 1);
        tokensToUse.put(TokenType.Gold, 1);
        tokensToUse.put(TokenType.Red, 2);

        Card expected = game.getCardFromId("02");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses.")
    @Test
    void testPlayerBuyCard_UseBonus() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Red, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 1);

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Gold, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Gold, 1);

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(game.getCardFromId(expected.getId())).isNull();
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndDoubleGoldTokensOnly() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Gold, 2);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(game.getCardFromId(expected.getId())).isNull();
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_DoubleGoldTokensOnly() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Gold, 4);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(game.getCardFromId(expected.getId())).isNull();
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players cannot buy a card when providing zero tokens.")
    @Test
    void testPlayerBuyCard_ZeroTokensGiven() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(p1.getDevCards().isEmpty()).isTrue();
        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players can take two of same token in one turn.")
    @Test
    void testPlayerTakeTokens_ValidDoubleToken() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 2);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        HashMap<TokenType, Integer> expected = new HashMap<>();
        for(TokenType type : TokenType.values()) {
            expected.put(type, 0);
        }
        expected.put(TokenType.Red, 4);
        expected.put(TokenType.Blue, 2);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(expected).isEqualTo(p1.getTokens());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can take three unique tokens in one turn.")
    @Test
    void testPlayerTakeTokens_ValidThreeUniqueTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        HashMap<TokenType, Integer> expected = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expected.put(type, 0);
        }
        expected.put(TokenType.Red, 4);
        expected.put(TokenType.Green, 1);
        expected.put(TokenType.Blue, 1);
        expected.put(TokenType.Brown, 1);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(expected).isEqualTo(p1.getTokens());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can take and put back tokens, remaining under 10 tokens in inventory")
    @Test
    void testPlayerTakeTokens_ValidPlayerTakeAndPutBack() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 3);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();
        tokensToPutBack.put(TokenType.White, 1);
        tokensToPutBack.put(TokenType.Red, 1);

        HashMap<TokenType, Integer> expected = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expected.put(type, 0);
        }
        expected.put(TokenType.Red, 3);
        expected.put(TokenType.Green, 3);
        expected.put(TokenType.White, 2);
        expected.put(TokenType.Brown, 1);
        expected.put(TokenType.Blue, 1);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(expected).isEqualTo(p1.getTokens());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players cannot have more than 10 tokens.")
    @Test
    void testPlayerTakeTokens_InvalidPlayerMaxTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 3);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        HashMap<TokenType, Integer> expected = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expected.put(type, 0);
        }
        expected.put(TokenType.Red, 4);
        expected.put(TokenType.Green, 2);
        expected.put(TokenType.White, 3);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(expected).isEqualTo(p1.getTokens());
        assertThat(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY).isIn(result);
    }

    @DisplayName("Ensure players cannot take more than 2 of the same token per turn (without Trade Routes power).")
    @Test
    void testPlayerTakeTokens_InvalidThreeOfSameToken() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 1);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 1);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 3);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        HashMap<TokenType, Integer> expected = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expected.put(type, 0);
        }
        expected.put(TokenType.Red, 1);
        expected.put(TokenType.Green, 2);
        expected.put(TokenType.White, 1);

        ArrayList<ActionRßn5¶‰žËkºwµç@€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÀÜˆ°…‘¤¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡ÀÉQ½­•¹Ì°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÈ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÀˆ¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÀˆ°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••Èø•áÁ•Ñ•‘	½¹ÕÍ•Ì€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€™½È€¡Q½­•¹QåÁ”ÑåÁ”€èQ½­•¹QåÁ”¹Ù…±Õ•Ì ¤¤ì4(€€€€€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡ÑåÁ”°€À¤ì4(€€€€€€€ô4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€È¤ì4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹	É½Ý¸°€Ä¤ì4(4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÄ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÈ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡ÀÄ¹•Ñ	½¹ÕÍ•Ì ¤¤¹¥ÍÅÕ…±Q¼¡•áÁ•Ñ•‘	½¹ÕÍ•Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÄ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÈ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹QUI9}=5A1Q¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”Á±…å•ÉÌ…¸‰ÕÉ¸„‰½¹ÕÌ…É…¹±½Ý•ÍÐÁÉ•ÍÑ¥”Á½¥¹Ð…É‘ÌÑ…­”ÁÉ¥½É¥Ñä¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ	ÕÉ¹½Õ‰±•	½¹ÕÍ…É‘=ÁÑ¥µ…±¡½¥” ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(€€€€€€€A±…å•ÈÀÈ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÈˆ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½‘€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹É••¸°€È¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹	±Õ”°€È¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€Ä¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½‘¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••Èø…‘€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€…‘¹ÁÕÐ¡Q½­•¹QåÁ”¹É••¸°€Ä¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡…‘¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡…‘¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÀÉQ½­•¹Ì€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€ÀÉQ½­•¹Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹	±Õ”°€Ä¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÄ€ô…µ”¹•Ñ…É‘É½µ% ˆÀÌˆ¤ì4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÀÌˆ°Ñ½­•¹ÍQ½‘¤¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡ÀÉQ½­•¹Ì°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÈˆ°…‘¤¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡ÀÉQ½­•¹Ì°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÌˆ°…‘¤¤ì4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•ÜI•Í•ÉÙ•9½‰±•Ñ¥½¸ ˆääˆ¤¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡ÀÉQ½­•¹Ì°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÈ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÀˆ¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÀˆ°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••Èø•áÁ•Ñ•‘	½¹ÕÍ•Ì€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€™½È€¡Q½­•¹QåÁ”ÑåÁ”€èQ½­•¹QåÁ”¹Ù…±Õ•Ì ¤¤ì4(€€€€€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡ÑåÁ”°€À¤ì4(€€€€€€€ô4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€Ä¤ì4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹	É½Ý¸°€Ä¤ì4(4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÄ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÈ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡ÀÄ¹•Ñ	½¹ÕÍ•Ì ¤¤¹¥ÍÅÕ…±Q¼¡•áÁ•Ñ•‘	½¹ÕÍ•Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡ÀÄ¹•ÑAÉ•ÍÑ¥•A½¥¹ÑÌ ¤¤¹¥ÍÅÕ…±Q¼ Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÄ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÈ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹QUI9}=5A1Q¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”„Á±…å•È…¸ÁÉ½Á•É±äÕÍ”„…Í…‘”Ñ¥•È€È…É¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ…Í…‘•Q¥•ÈÈ ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½‘€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹É••¸°€Ä¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½‘¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÄ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÐˆ¤ì4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÐˆ°Ñ½­•¹ÍQ½‘¤¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÈ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÀˆ¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü…Í…‘•Q¥•ÈÉÑ¥½¸ ˆÄÀˆ¤¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••Èø•áÁ•Ñ•‘	½¹ÕÍ•Ì€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€™½È€¡Q½­•¹QåÁ”ÑåÁ”€èQ½­•¹QåÁ”¹Ù…±Õ•Ì ¤¤ì4(€€€€€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡ÑåÁ”°€À¤ì4(€€€€€€€ô4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹	±Õ”°€Ä¤ì4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹	É½Ý¸°€Ä¤ì4(4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÄ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÈ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡ÀÄ¹•Ñ	½¹ÕÍ•Ì ¤¤¹¥ÍÅÕ…±Q¼¡•áÁ•Ñ•‘	½¹ÕÍ•Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÄ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÈ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹QUI9}=5A1Q¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”„Á±…å•È…¸ÁÉ½Á•É±äÕÍ”„…Í…‘”Ñ¥•È€Ä…É¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ…Í…‘•Q¥•ÈÄ ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½‘€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹É••¸°€Ä¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½‘¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÄ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÔˆ¤ì4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÔˆ°Ñ½­•¹ÍQ½‘¤¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÈ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÈˆ¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü…Í…‘•Q¥•ÈÅÑ¥½¸ ˆÄÈˆ¤¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••Èø•áÁ•Ñ•‘	½¹ÕÍ•Ì€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€™½È€¡Q½­•¹QåÁ”ÑåÁ”€èQ½­•¹QåÁ”¹Ù…±Õ•Ì ¤¤ì4(€€€€€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡ÑåÁ”°€À¤ì4(€€€€€€€ô4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€È¤ì4(4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÄ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÈ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡ÀÄ¹•Ñ	½¹ÕÍ•Ì ¤¤¹¥ÍÅÕ…±Q¼¡•áÁ•Ñ•‘	½¹ÕÍ•Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÄ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÈ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹QUI9}=5A1Q¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”„Á±…å•È…¸ÁÉ½Á•É±äÕÍ”„…Í…‘”Ñ¥•È€Ä…ÉÑ¼Á¥¬„Í…Ñ¡•°ˆ€¬4(€€€€€€€€€€€€ˆ…¹Ñ¡•¸¡½½Í”Ñ¡”½±½È½˜Ñ¡”Í…Ñ¡•°¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ…Í…‘•Q¥•ÈÅQ½M…Ñ¡•° ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½‘€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹É••¸°€Ä¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½‘¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÄ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÔˆ¤ì4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÄÔˆ°Ñ½­•¹ÍQ½‘¤¤ì4(4(€€€€€€€…É•áÁ•Ñ•‘…ÉÈ€ô…µ”¹•Ñ…É‘É½µ% ˆÄÄˆ¤ì4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü…Í…‘•Q¥•ÈÉÑ¥½¸ ˆÄÄˆ¤¤ì4(4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü¡½½Í•Q½­•¹QåÁ•Ñ¥½¸ ˆÄÄˆ°Q½­•¹QåÁ”¹I•¤¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••Èø•áÁ•Ñ•‘	½¹ÕÍ•Ì€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€™½È€¡Q½­•¹QåÁ”ÑåÁ”€èQ½­•¹QåÁ”¹Ù…±Õ•Ì ¤¤ì4(€€€€€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡ÑåÁ”°€À¤ì4(€€€€€€€ô4(€€€€€€€•áÁ•Ñ•‘	½¹ÕÍ•Ì¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€È¤ì4(4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÄ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•‘…ÉÈ¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡ÀÄ¹•Ñ	½¹ÕÍ•Ì ¤¤¹¥ÍÅÕ…±Q¼¡•áÁ•Ñ•‘	½¹ÕÍ•Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÄ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•‘…ÉÈ¤¹¥Í%¸¡ÀÄ¹•Ñ•Ù…É‘Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹QUI9}=5A1Q¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰Q•ÍÐÉ•Í•ÉÙ”¹½‰±”…Ñ¥½¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑI•Í•ÉÙ•9½‰±” ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½‘€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½‘¹ÁÕÐ¡Q½­•¹QåÁ”¹É••¸°€Ä¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½‘¤ì4(4(€€€€€€€…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü…Í…‘•Q¥•ÈÉÑ¥½¸ ˆÄÌˆ¤¤ì4(4(€€€€€€€…É•áÁ•Ñ•€ô…µ”¹•Ñ…É‘É½µ% ˆääˆ¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•ÜI•Í•ÉÙ•9½‰±•Ñ¥½¸ ˆääˆ¤¤ì4(4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ…É‘É½µ%¡•áÁ•Ñ•¹•Ñ% ¤¤¤¹¥Í9Õ±° ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡•áÁ•Ñ•¤¹¥Í%¸¡ÀÄ¹•ÑI•Í•ÉÙ•‘9½‰±•Ì ¤¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹QUI9}=5A1Q¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”•¹½˜…µ”¥Ìµ…É­•…Ð•¹½˜É½Õ¹Ý¡•¸Á±…å•ÈÉ•…¡•Ì€ÄÔÁÉ•ÍÑ¥”Á½¥¹ÑÌ¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ¹‘=™I½Õ¹‘}=¹•]¥¹¹•È ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(€€€€€€€A±…å•ÈÀÈ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÈˆ¤ì4(€€€€€€€ÀÄ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÐ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½UÍ”€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½UÍ”¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€Ð¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½UÍ”¤ì4(4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÀÄˆ°Ñ½­•¹ÍQ½UÍ”¤¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÈ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€€¼¼µ…­”ÍÕÉ”…Ñ¥½¸¥ÌÙ…±¥Í¥¹”Á±…å•È…¸…™™½É¥Ð4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÈ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹Í¥é” ¤¤¹¥ÍÅÕ…±Q¼ Ä¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹•Ð À¤¤¹¥ÍÅÕ…±Q¼¡ÀÄ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹¥Í…µ•=Ù•È ¤¤¹¥ÍQÉÕ” ¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”•¹½˜…µ”¥Ìµ…É­•…Ð•¹½˜É½Õ¹Ý¡•¸€øÄÁ±…å•ÈÉ•…¡•Ì€ÄÔÁÉ•ÍÑ¥”Á½¥¹ÑÌ¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ¹‘=™I½Õ¹‘}QÝ½A½Ñ•¹Ñ¥…±]¥¹¹•ÉÌ ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(€€€€€€€ÀÄ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÐ¤ì4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½UÍ”€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½UÍ”¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€Ð¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½UÍ”¤ì4(4(€€€€€€€A±…å•ÈÀÈ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÈˆ¤ì4(€€€€€€€ÀÈ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÜ¤ì4(4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÄ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÀÄˆ°Ñ½­•¹ÍQ½UÍ”¤¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÈ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€€¼¼µ…­”ÍÕÉ”…Ñ¥½¸¥ÌÙ…±¥Í¥¹”Á±…å•È…¸…™™½É¥Ð4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÄ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÈ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹Í¥é” ¤¤¹¥ÍÅÕ…±Q¼ Ä¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹•Ð À¤¤¹¥ÍÅÕ…±Q¼¡ÀÈ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹¥Í…µ•=Ù•È ¤¤¹¥ÍQÉÕ” ¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”•¹½˜…µ”¥Ìµ…É­•…Ð•¹½˜É½Õ¹Ý¡•¸„Ñ¥”½ÕÉÌ¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ¹‘=™I½Õ¹‘}Q¥” ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(€€€€€€€ÀÄ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÔ¤ì4(4(€€€€€€€A±…å•ÈÀÈ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÈˆ¤ì4(€€€€€€€ÀÈ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÔ¤ì4(4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÄ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÈ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€€¼¼µ…­”ÍÕÉ”…Ñ¥½¸¥ÌÙ…±¥Í¥¹”Á±…å•È…¸…™™½É¥Ð4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÄ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÈ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹½¹Ñ…¥¹Ì¡ÀÄ¤¤¹¥ÍQÉÕ” ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹½¹Ñ…¥¹Ì¡ÀÈ¤¤¹¥ÍQÉÕ” ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹Í¥é” ¤¤¹¥ÍÅÕ…±Q¼ È¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹¥Í…µ•=Ù•È ¤¤¹¥ÍQÉÕ” ¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”•¹½˜…µ”¥Ìµ…É­•…Ð•¹½˜É½Õ¹Ý¡•¸„Ñ¥”½ÕÉÌ¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ¹‘=™I½Õ¹‘}Q¡É••]…åQ¥” ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€Ì¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(€€€€€€€ÀÄ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÔ¤ì4(4(€€€€€€€A±…å•ÈÀÈ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÈˆ¤ì4(€€€€€€€ÀÈ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÔ¤ì4(4(€€€€€€€A±…å•ÈÀÌ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÌˆ¤ì4(€€€€€€€ÀÌ¹…‘‘AÉ•ÍÑ¥•A½¥¹ÑÌ ÄÔ¤ì4(4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÄ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÈ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÌ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÌ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€€¼¼µ…­”ÍÕÉ”…Ñ¥½¸¥ÌÙ…±¥Í¥¹”Á±…å•È…¸…™™½É¥Ð4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÄ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÈ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÌ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹½¹Ñ…¥¹Ì¡ÀÄ¤¤¹¥ÍQÉÕ” ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹½¹Ñ…¥¹Ì¡ÀÈ¤¤¹¥ÍQÉÕ” ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹½¹Ñ…¥¹Ì¡ÀÌ¤¤¹¥ÍQÉÕ” ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹Í¥é” ¤¤¹¥ÍÅÕ…±Q¼ Ì¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹¥Í…µ•=Ù•È ¤¤¹¥ÍQÉÕ” ¤ì4(€€€ô4(4(€€€¥ÍÁ±…å9…µ” ‰¹ÍÕÉ”•¹½˜…µ”¥Ì9=Pµ…É­•…Ð•¹½˜É½Õ¹Ý¡•É”¹¼Á±…å•È¡…Ì€ÄÔÁÉ•ÍÑ¥”Á½¥¹ÑÌ¸ˆ¤4(€€€Q•ÍÐ4(€€€Ù½¥Ñ•ÍÑ¹‘=™I½Õ¹‘}9½]¥¹¹•È ¤Ñ¡É½ÝÌ¥±•9½Ñ½Õ¹‘á•ÁÑ¥½¸ì4(€€€€€€€=É¥•¹Ñ…µ”…µ”€ô…µ•UÑ¥±Ì¹É•…Ñ•9•Ý=É¥•¹Ñ…µ” ÄÔ°€È¤ì4(4(€€€€€€€€¼¼•Ð™¥ÉÍÐÁ±…å•È€¡¹…µ”€ô€‰A±…å•ÈÄˆ¤4(€€€€€€€A±…å•ÈÀÄ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÄˆ¤ì4(€€€€€€€A±…å•ÈÀÈ€ô…µ”¹•ÑA±…å•ÉÉ½µ9…µ” ‰A±…å•ÈÈˆ¤ì4(4(€€€€€€€!…Í¡5…ÀñQ½­•¹QåÁ”°%¹Ñ••ÈøÑ½­•¹ÍQ½UÍ”€ô¹•Ü!…Í¡5…Àðø ¤ì4(€€€€€€€Ñ½­•¹ÍQ½UÍ”¹ÁÕÐ¡Q½­•¹QåÁ”¹I•°€Ð¤ì4(€€€€€€€ÀÄ¹…‘‘Q½­•¹Ì¡Ñ½­•¹ÍQ½UÍ”¤ì4(4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±Ð€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÄ¹•Ñ9…µ” ¤°¹•Ü	Õå…É‘Ñ¥½¸ ˆÀÄˆ°Ñ½­•¹ÍQ½UÍ”¤¤ì4(€€€€€€€ÉÉ…å1¥ÍÐñÑ¥½¹I•ÍÕ±ÐøÉ•ÍÕ±ÐÈ€ô…µ”¹Ñ…­•Ñ¥½¸¡ÀÈ¹•Ñ9…µ” ¤°¹•ÜQ…­•Q½­•¹Ñ¥½¸¡¹•Ü!…Í¡5…Àðø ¤°¹•Ü!…Í¡5…Àðø ¤¤¤ì4(4(€€€€€€€€¼¼µ…­”ÍÕÉ”…Ñ¥½¸¥ÌÙ…±¥Í¥¹”Á±…å•È…¸…™™½É¥Ð4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±Ð¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡Ñ¥½¹I•ÍÕ±Ð¹Y1%}Q%=8¤¹¥Í%¸¡É•ÍÕ±ÐÈ¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹•Ñ]¥¹¹•È ¤¹Í¥é” ¤¤¹¥ÍÅÕ…±Q¼ À¤ì4(€€€€€€€…ÍÍ•ÉÑQ¡…Ð¡…µ”¹¥Í…µ•=Ù•È ¤¤¹¥Í…±Í” ¤ì4(€€€ô4(4)ô4