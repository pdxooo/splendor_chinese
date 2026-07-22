package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.actions.*;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;
import ca.hexanome04.splendorgame.model.action.ActionResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        OrientGame game = GameUtils.createNewOrientGa×~ºÖÚ$z{-®éÜj×J
NÂˆ\ÜÙ\]
Ø[YK™Ù]Y\Œ”\˜Ú\ØX›SÜšY[Ø\™Ê
JK˜\Ó\İ

Kš\Ñ[\J
NÂˆ\ÜÙ\]
Ø[YK™Ù]Y\ŒÔ\˜Ú\ØX›SÜšY[Ø\™Ê
JK˜\Ó\İ

Kš\Ñ[\J
NÂˆB‚ˆ\Ü^S˜[YJ‘İX›K]˜[YHš\X[ÛÛ™[XZ[œÈYYÈÛ™HÛÛİ\ˆ\ˆYXÙHŠBˆ\İˆ›ÚY\İİX›Uš\X[ÛÛYXÙPÛİ[[™Ê
HÂˆ˜Y[™ÔÜİÔ^Y\ˆ^Y\ˆH™]È˜Y[™ÔÜİÔ^Y\Š”^Y\ŒH‹˜›YHŠNÂˆ^Y\‹™ÛÛÚÙ[•ÛÜÛÕÚÙ[œË[›ØÚÔİÙ\Š^Y\ŠNÂˆ^Y\‹˜Y›Û\ÊÚÙ[•\K‘ÛÛŠNÂ‚ˆ\ÚX\ÚÙ[•\K[YÙ\ˆÜ]ÛÜİH™]È\ÚX\Š
NÂˆ›Üˆ
ÚÙ[•\H\HˆÚÙ[•\K˜[Y\Ê
JHÂˆÜ]ÛÜİœ]
\K
NÂˆBˆÜ]ÛÜİœ]
ÚÙ[•\K”™YJNÂˆÜ]ÛÜİœ]
ÚÙ[•\K›YKJNÂˆ™YÑ]™[ÜY[Ø\™Ø\™H™]È™YÑ]™[ÜY[Ø\™
Ø\™Y\‹•QT—ÌKˆÚÙ[•\K‘Ü™Y[‹KÛÜİ\K•ÚÙ[‹Ü]ÛÜİ™İX›KYÛÛ]\İŠNÂ‚ˆ\ÜÙ\]
Ø\™š\Ô\˜Ú\ØX›J^Y\‹™]È\ÚX\Š
JJKš\ÕYJ
NÂˆ\ÜÙ\]
Ø\™™Ù]š\X[ÛÛYXÙ\Õ\ÙY
^Y\‹™]È\ÚX\Š
JJKš\Ñ\]X[ÊŠNÂ‚ˆÜ]ÛÜİœ]
ÚÙ[•\K”™Y
NÂˆÜ]ÛÜİœ]
ÚÙ[•\K›YK
NÂˆ\ÜÙ\]
Ø\™™Ù]š\X[ÛÛYXÙ\Õ\ÙY
^Y\‹™]È\ÚX\Š
JJKš\Ñ\]X[ÊŠNÂˆBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆHXZÙ\È[İHÚÛÜÙH[ˆ^˜HÚÙ[ˆY\ˆØ\™\˜Ú\ÙHŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[‘^˜UÚÙ[Y\Ø\™\˜Ú\ÙTİÙ\ŒJ
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ˜Y[™ÔÜİÔ^Y\ˆHH
˜Y[™ÔÜİÔ^Y\ŠHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÃBƒBˆK™^˜UÚÙ[Y\”\˜Ú\ÙK[›ØÚÔİÙ\ŠJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒH‹ÚÙ[œÕĞY
JNÃBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]ÈZÙQ^˜UÚÙ[Y\”\˜Ú\ÙTİÙ\Xİ[ÛŠÚÙ[•\K‘Ü™Y[‹[
JNÃBƒBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”™Y
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K›YK
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\Kœ›İÛ‹
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘ÛÛ
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”Ø]Ú[
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K•Ú]K
NÃBƒBˆ\ÜÙ\]
K™Ù]ÚÙ[œÊ
JKš\Ñ\]X[ÊÚÙ[œÕĞY
NÃBƒBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆˆXZÙ\È[İHÚÛÜÙH[ˆ^˜HÚÙ[ˆY\ˆXÚÚ[™ÈˆÙˆHØ[YHÛÛİ\ˆŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[‘^˜UÚÙ[”İÙ\ŒŠ
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ˜Y[™ÔÜİÔ^Y\ˆHH
˜Y[™ÔÜİÔ^Y\ŠHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÃBƒBˆK™^˜UÚÙ[Y\•ZÚ[™ÔØ[YPÛÛÜ‹[›ØÚÔİÙ\ŠJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹ŠNÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”™YJNÃBƒBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]ÈZÙUÚÙ[Xİ[ÛŠÚÙ[œÕĞY™]È\ÚX\Š
JJNÃBƒBˆÚÙ[œÕĞYœ]
ÚÙ[•\K›YK
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\Kœ›İÛ‹
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘ÛÛ
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”Ø]Ú[
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K•Ú]K
NÃBƒBˆ\ÜÙ\]
K™Ù]ÚÙ[œÊ
JKš\Ñ\]X[ÊÚÙ[œÕĞY
NÃBƒBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™H[İHØ[ˆ]™H[›ØÚÙYİÙ\ˆˆ]›İ™XÙ\ÜØ\š[H\ÙH]ŠCBˆ\İBˆ›ÚY\İ^Y\•[›ØÚÙYİÙ\Œ]Ù\Ó›İ\ÙJ
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ˜Y[™ÔÜİÔ^Y\ˆHH
˜Y[™ÔÜİÔ^Y\ŠHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÃBƒBˆK™^˜UÚÙ[Y\•ZÚ[™ÔØ[YPÛÛÜ‹[›ØÚÔİÙ\ŠJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹ŠNÃBƒBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]ÈZÙUÚÙ[Xİ[ÛŠÚÙ[œÕĞY™]È\ÚX\Š
JJNÃBƒBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”™Y
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K›YK
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\Kœ›İÛ‹
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘ÛÛ
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”Ø]Ú[
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K•Ú]K
NÃBƒBˆ\ÜÙ\]
K™Ù]ÚÙ[œÊ
JKš\Ñ\]X[ÊÚÙ[œÕĞY
NÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™H[İHØ[››İZÙH[ˆ^˜HÚÙ[ˆÚ]İÙ\ˆˆYˆ\™H\™H›İ[›İYÚÚÙ[œÈYŠCBˆ\İBˆ›ÚY\İ^Y\•[›ØÚÙYİÙ\Œ—ÕÛÑ™]ÕÚÙ[œÒ[˜[šÊ
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ˜Y[™ÔÜİÔ^Y\ˆHH
˜Y[™ÔÜİÔ^Y\ŠHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÃBƒBˆK™^˜UÚÙ[Y\•ZÚ[™ÔØ[YPÛÛÜ‹[›ØÚÔİÙ\ŠJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹ŠNÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”™YJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕÔ™[[İ™HH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”™YÊNÃBƒBˆØ[YKœ™[[İ™UÚÙ[œÊÚÙ[œÕÔ™[[İ™JNÃBƒBˆ\œ˜^S\İXİ[Û”™\İ[ˆ™\İ[HØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]ÈZÙUÚÙ[Xİ[ÛŠÚÙ[œÕĞY™]È\ÚX\Š
JJNÃBƒBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”™Y
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K›YK
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\Kœ›İÛ‹
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘ÛÛ
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K”Ø]Ú[
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K•Ú]K
NÃBƒBˆ\ÜÙ\]
Xİ[Û”™\İ[•Ó×ÓPS–WÔĞSQWĞÓÓÕT—ÕÒÑS”ÊKš\Ò[Š™\İ[
NÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™H[İHØ[ˆ^HHØ\™\Ú[™ÈÛÛÚÙ[œÈÚ]İÙ\ˆÈŠCBˆ\İBˆ›ÚY\İ^Y\•\Ú[™ÔİÙ\ŒÕÔ^Q›ÜØ\™

H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ˜Y[™ÔÜİÔ^Y\ˆHH
˜Y[™ÔÜİÔ^Y\ŠHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÃBƒBˆK™ÛÛÚÙ[•ÛÜÛÕÚÙ[œË[›ØÚÔİÙ\ŠJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘ÛÛJNÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K›YKJNÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\Kœ›İÛ‹JNÃBƒBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBƒBˆ\œ˜^S\İXİ[Û”™\İ[ˆ™\İ[HØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒˆ‹ÚÙ[œÕĞY
JNÃBƒBˆ\ÜÙ\]
Xİ[Û”™\İ[•T“—ĞÓÓTUQ
Kš\Ò[Š™\İ[
NÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™H[İHØ[ˆ^HHØ\™™Yİ[\›HÚ[H]š[™ÈİÙ\ˆÈ[™\Ú[™ÈÛÛÚÙ[œÈŠCBˆ\İBˆ›ÚY\İ^Y\’]š[™ÔİÙ\ŒÕ[›ØÚÙY]›İ\Ú[™Ò]

H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ˜Y[™ÔÜİÔ^Y\ˆHH
˜Y[™ÔÜİÔ^Y\ŠHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÃBƒBˆK™ÛÛÚÙ[•ÛÜÛÕÚÙ[œË[›ØÚÔİÙ\ŠJNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘ÛÛŠNÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K›YKJNÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\Kœ›İÛ‹JNÃBƒBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBƒBˆ\œ˜^S\İXİ[Û”™\İ[ˆ™\İ[HØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒˆ‹ÚÙ[œÕĞY
JNÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆ[\HH™]È\ÚX\Š
NÃBˆ[\Kœ]
ÚÙ[•\K‘ÛÛ
NÃBˆ[\Kœ]
ÚÙ[•\K›YK
NÃBˆ[\Kœ]
ÚÙ[•\Kœ›İÛ‹
NÃBˆ[\Kœ]
ÚÙ[•\K”™Y
NÃBˆ[\Kœ]
ÚÙ[•\K‘Ü™Y[‹
NÃBˆ[\Kœ]
ÚÙ[•\K•Ú]K
NÃBˆ[\Kœ]
ÚÙ[•\K”Ø]Ú[
NÃBƒBˆ\ÜÙ\]
Xİ[Û”™\İ[•T“—ĞÓÓTUQ
Kš\Ò[Š™\İ[
NÃBˆ\ÜÙ\]
K™Ù]ÚÙ[œÊ
JKš\Ñ\]X[Ê[\JNÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆ
YH
HÛÜšÜÈ›Ü\›HÛˆXÜ]Z\Ú][ÛˆÙˆHØ\™šYÙÙ\š[™ÈØZ[š[™ÈH›Ø›HŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[TÛ“›Ø›PXÜ]Z\Ú][ÛŠ
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠBˆ^Y\ˆHHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÂˆ˜Y[™ÔÜİÔ^Y\ˆ˜Y[™Ô^Y\ˆH
˜Y[™ÔÜİÔ^Y\ŠHNÂˆ\ØX›P[İÙ\œÊ˜Y[™Ô^Y\ŠNÂˆ˜Y[™Ô^Y\‹˜Yš]™T™\İYÙTÚ[ÂˆœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\K‘Ü™Y[‹JJNÂƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBˆK˜Y›Û\ÊÚÙ[•\K›YKŠNÃBˆK˜Y›Û\ÊÚÙ[•\K”™YŠNÃBˆK˜Y›Û\ÊÚÙ[•\K‘Ü™Y[‹ÊNÃBƒBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕÕ\ÙHH™]È\ÚX\Š
NÃBˆÚÙ[œÕÕ\ÙKœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒH‹ÚÙ[œÕÕ\ÙJJNÃBƒBˆ\œ˜^S\İXİ[Û”™\İ[ˆ™\İ[HØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]ÈÚÛÜÙS›Ø›PXİ[ÛŠNHŠJNÃBƒBˆËÈ™XØ]\ÙHHİÙ\ˆYÈKH›Ø›HYÈÃBˆ\ÜÙ\]
K™Ù]™\İYÙTÚ[Ê
JKš\Ñ\]X[Ê
NÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆ
YH
HÛÜšÜÈ›Ü\›HÛˆXÜ]Z\Ú][ÛˆÙˆH›Ø›H[ˆHØ\™ŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[TÛØ\™XÜ]Z\Ú][ÛŠ
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÂˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ^Y\ˆHHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÂˆ

˜Y[™ÔÜİÔ^Y\ŠHJK˜Yš]™T™\İYÙTÚ[ÂˆœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\K‘Ü™Y[‹JJNÂƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBˆK˜Y›Û\ÊÚÙ[•\K‘Ü™Y[‹ÊNÃBˆ›Ø›PØ\™ÌHH
›Ø›PØ\™
HØ[YK™Ù]Ø\™œ›ÛRY
NHŠNÃBˆK˜Y›Ø›JÌJNÃBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒH‹ÚÙ[œÕĞY
JNÃBƒBƒBˆËÈ™XØ]\ÙHHİÙ\ˆYÈKH›Ø›HYÈÃBˆ\ÜÙ\]
K™Ù]™\İYÙTÚ[Ê
JKš\Ñ\]X[Ê
NÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆH
YH\ˆİÙ\ˆ[›ØÚÙY
HÛÜšÜÈ›Ü\›HÚ[ˆHİÙ\ˆØ\È[›ØÚÙY[ˆHØ[YH\›ˆŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[”Û”İÙ\U[›ØÚÕÚ]Û™Sİ\”İÙ\Š
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÂˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ^Y\ˆHHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÂˆ˜Y[™ÔÜİÔ^Y\ˆ˜Y[™Ô^Y\ˆH
˜Y[™ÔÜİÔ^Y\ŠHNÂˆ\ØX›P[İÙ\œÊ˜Y[™Ô^Y\ŠNÂˆ˜Y[™Ô^Y\‹˜Yš]™T™\İYÙTÚ[ËœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\K‘Ü™Y[‹JJNÂˆ˜Y[™Ô^Y\‹˜Y™\İYÙTÚ[ÕÚ]ÛØ]ÓÙ\›\ËœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\Kœ›İÛ‹ÊJNÂƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBˆK˜Y›Û\ÊÚÙ[•\K‘Ü™Y[‹ÊNÃBˆK˜Y›Û\ÊÚÙ[•\Kœ›İÛ‹ÊNÃBˆ›Ø›PØ\™ÌHH
›Ø›PØ\™
HØ[YK™Ù]Ø\™œ›ÛRY
NHŠNÃBˆK˜Y›Ø›JÌJNÃBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒH‹ÚÙ[œÕĞY
JNÃBƒBƒBˆËÈ™XØ]\ÙHİÙ\ˆYÈKH›Ø›HYÈË[ˆİÙ\ˆHYÈˆ™XØ]\ÙH\™H\™HˆİÙ\œÈ[›ØÚÙY[ˆİ[Bˆ\ÜÙ\]
K™Ù]™\İYÙTÚ[Ê
JKš\Ñ\]X[ÊL
NÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆH
YH\ˆİÙ\ˆ[›ØÚÙY
HÛÜšÜÈ›Ü\›HÚ[ˆ[›ØÚÙY[Û™HŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[”Û”İÙ\U[›ØÚÕÚ]›Óİ\”İÙ\Š
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÂˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMK
NÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠBˆ^Y\ˆHHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÂˆ˜Y[™ÔÜİÔ^Y\ˆ˜Y[™Ô^Y\ˆH
˜Y[™ÔÜİÔ^Y\ŠHNÂˆ\ØX›P[İÙ\œÊ˜Y[™Ô^Y\ŠNÂˆ˜Y[™Ô^Y\‹˜Y™\İYÙTÚ[ÕÚ]ÛØ]ÓÙ\›\ÂˆœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\Kœ›İÛ‹ÊJNÂƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒ‹ÚÙ[œÕĞY
JNÃBƒBƒBˆËÈH™XØ]\ÙHİÙ\ˆH\È[›ØÚÙYÛˆ]ÈİÛ‹ÛÈÛ›HHİÙ\ƒBˆ\ÜÙ\]
K™Ù]™\İYÙTÚ[Ê
JKš\Ñ\]X[ÊJNÃBˆCBƒBˆ\Ü^S˜[YJ‘[œİ\™HİÙ\ˆHÛÜšÜÈÚ[ˆ[›ØÚÙYÛˆÛÛYH]\ˆ\›ˆY\ˆİÙ\œÈÙ\™H[›ØÚÙYŠCBˆ\İBˆ›ÚY\İ^Y\‘ØZ[”Û”İÙ\U[›ØÚÕÚ]İÙ\•[›ØÚÙYX\›Y\•\›Š
H›İÜÈš[S›İ›İ[™^Ù\[ÛˆÃBˆ˜Y[™ÔÜİÑØ[YHØ[YHHØ[YU][Ë˜Ü™X]S™]Õ˜Y[™ÔÜİØ[YJMKŠNÃBƒBˆËÈÙ]š\œİ^Y\ˆ
˜[YHH”^Y\ŒHŠCBˆ^Y\ˆHHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒHŠNÂˆ^Y\ˆˆHØ[YK™Ù]^Y\‘œ›ÛS˜[YJ”^Y\ŒˆŠNÂˆ˜Y[™ÔÜİÔ^Y\ˆ˜Y[™Ô^Y\ˆH
˜Y[™ÔÜİÔ^Y\ŠHNÂˆ\ØX›P[İÙ\œÊ˜Y[™Ô^Y\ŠNÂˆ˜Y[™Ô^Y\‹™^˜UÚÙ[Y\•ZÚ[™ÔØ[YPÛÛÜ‹œÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\K•Ú]KŠJNÂˆ˜Y[™Ô^Y\‹˜Yš]™T™\İYÙTÚ[ËœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\K‘Ü™Y[‹JJNÂˆ˜Y[™Ô^Y\‹˜Y™\İYÙTÚ[ÕÚ]ÛØ]ÓÙ\›\ËœÙ]™\]Z\™[Y[ÊX\›ÙŠÚÙ[•\Kœ›İÛ‹ÊJNÂƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕĞYH™]È\ÚX\Š
NÃBˆÚÙ[œÕĞYœ]
ÚÙ[•\K‘Ü™Y[‹ÊNÃBˆK˜YÚÙ[œÊÚÙ[œÕĞY
NÃBƒBˆ\ÚX\ÚÙ[•\K[YÙ\ˆÚÙ[œÕÕ\ÙHH™]È\ÚX\Š
NÃBˆÚÙ[œÕÕ\ÙKœ]
ÚÙ[•\K‘Ü™Y[‹JNÃBƒBˆK˜Y›Û\ÊÚÙ[•\K‘Ü™Y[‹ÊNÃBˆ›Ø›PØ\™ÌHH
›Ø›PØ\™
HØ[YK™Ù]Ø\™œ›ÛRY
NHŠNÃBˆK˜Y›Ø›JÌJNÃBƒBˆËÈ[›ØÚÜÈİÙ\ˆY[™ÈTÈ^Y\ƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒH‹ÚÙ[œÕÕ\ÙJJNÃBƒBˆËÈ[˜Ü™[Y[\›ˆ˜XÚÈÈ^Y\ˆCBˆØ[YKZÙPXİ[ÛŠ‹™Ù]˜[YJ
K™]ÈZÙUÚÙ[Xİ[ÛŠÚÙ[œÕÕ\ÙK™]È\ÚX\Š
JJNÃBƒBˆËÈ[›ØÚÜÈİÙ\ˆƒBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒH‹ÚÙ[œÕÕ\ÙJJNÃBƒBˆØ[YKZÙPXİ[ÛŠ‹™Ù]˜[YJ
K™]ÈZÙUÚÙ[Xİ[ÛŠÚÙ[œÕÕ\ÙK™]È\ÚX\Š
JJNÃBƒBˆËÈ[›ØÚÜÈİÙ\ˆCBˆØ[YKZÙPXİ[ÛŠK™Ù]˜[YJ
K™]È^PØ\™Xİ[ÛŠŒ‹ÚÙ[œÕÕ\ÙJJNÃBƒBˆËÈL™XØ]\ÙHÈœ›ÛH›Ø›KHœ›ÛHİÙ\ˆ[›ØÚÙYÈœ›ÛHİÙ\ˆH[›ØÚÙY[™Ûİ[[™ÈÈİÙ\œÃBˆ\ÜÙ\]
K™Ù]™\İYÙTÚ[Ê
JKš\Ñ\]X[ÊLJNÂˆB‚ˆš]˜]Hİ]XÈ›ÚY\ØX›P[İÙ\œÊ˜Y[™ÔÜİÔ^Y\ˆ^Y\ŠHÂˆX\ÚÙ[•\K[YÙ\ˆ[\ÜÜÚX›HHX\›ÙŠÚÙ[•\K”™YNJNÂˆ^Y\‹™^˜UÚÙ[Y\”\˜Ú\ÙKœÙ]™\]Z\™[Y[Ê[\ÜÜÚX›JNÂˆ^Y\‹™^˜UÚÙ[Y\•ZÚ[™ÔØ[YPÛÛÜ‹œÙ]™\]Z\™[Y[Ê[\ÜÜÚX›JNÂˆ^Y\‹™ÛÛÚÙ[•ÛÜÛÕÚÙ[œËœÙ]™\]Z\™[Y[Ê[\ÜÜÚX›JNÂˆ^Y\‹˜Yš]™T™\İYÙTÚ[ËœÙ]™\]Z\™[Y[Ê[\ÜÜÚX›JNÂˆ^Y\‹˜Y™\İYÙTÚ[ÕÚ]ÛØ]ÓÙ\›\ËœÙ]™\]Z\™[Y[Ê[\ÜÜÚX›JNÂˆB‚ŸB