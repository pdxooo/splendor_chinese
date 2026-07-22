package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.*;
import java.util.*;

/**
 * Class that represents the current state of an orient + trading posts game.
 */
public class TradingPostsGame extends OrientGame {

    private List<HashMap<TokenType, Integer>> powerRequirements;

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param prestigePointsToWin The amount of prestige points needed to win the game.
     * @param turnCounter         The turn id associated with the player.
     */
    public TradingPostsGame(int prestigePointsToWin, int turnCounter) {
        super(GameVersions.BASE_ORIENT_TRADE_ROUTES, prestigePointsToWin, turnCounter);
        this.powerRequirements = generatePowerRequirements();
    }

    private List<HashMap<TokenType, Integer>> generatePowerRequirements() {
        List<TokenType> mainColours = new ArrayList<>(List.of(
                TokenType.Red, TokenType.Blue, TokenType.Green, TokenType.White, TokenType.Brown));
        Collections.shuffle(mainColours);
        int[] mainAmounts = {3, 2, 3, 5, 3};
        List<HashMap<TokenType, Integer>> generated = new ArrayList<>();

        for (int index = 0; index < mainColours.size(); index++) {
            HashMap<TokenType, Integer> requirement = new HashMap<>();
            TokenType main = mainColours.get(index);
            requirement.put(main, mainAmounts[index]);
            if (index == 0 || index == 2) {
                List<TokenType> secondaryChoices = new ArrayList<>(mainColours);
                secondaryChoices.remove(main);
                Collections.shuffle(secondaryChoices);
                requirement.put(secondaryChoices.get(0), 1);
            }
            generated.add(requirement);
        }
        return generated;
    }

    /**
     * Allows player to perform an action.
     *
     * @param playerName player performing action
     * @param action     action being executed
     * @return result of action execution
     */
    @Override
    public ArrayList<ActionResult> takeAction(String playerName, Action action) {
        final ArrayList<ActionResult> results = super.takeAction(playerName, action);

        TradingPostsPlayer p = (TradingPostsPlayer) this.getPlayerFromName(playerName);

        // Check if player qualifies for Powers at the end of each turn
        // Power 1
        if (!p.extraTokenAfterPurchase.isUnlocked() && p.extraTokenAfterPurchase.conditionMet(p)) {
            p.extraTokenAfterPurchase.unlockPower(p);
        }

        // Power 2
        if (!p.extraTokenAfterTakingSameColor.isUnlocked() && p.extraTokenAfterTakingSameColor.conditionMet(p)) {
            p.extraTokenAfterTakingSameColor.unlockPower(p);
        }

        // Power 3
        if (!p.goldTokenWorthTwoTokens.isUnlocked() && p.goldTokenWorthTwoTokens.conditionMet(p)) {
            p.goldTokenWorthTwoTokens.unlockPower(p);
        }

        // Power 4
        if (!p.addFivePrestigePoints.isUnlocked() && p.addFivePrestigePoints.conditionMet(p)) {
            p.addFivePrestigePoints.unlockPower(p);
            p.addFivePrestigePoints.execute(p);
        }

        // Power 5
        if (!p.addPrestigePointsWithCoatsOfArms.isUnlocked() && p.addPrestigePointsWithCoatsOfArms.conditionMet(p)) {
            p.addPrestigePointsWithCoatsOfArms.unlockPower(p);
        }

        if (results.contains(ActionResult.MUST_TAKE_EXTRA_TOKEN_AFTER_PURCHASE)) {
            this.addValidAction(Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);
        }

        return results;
    }

    /**
     * Creates a trading post player.
     *
     * @param name name of the player
     * @param colour color of the player
     * @return player created
     */
    @Override
    public Player createPlayer(String name, String colour) {
        TradingPostsPlayer player = new TradingPostsPlayer(name, colour);
        player.setPowerRequirements(powerRequirements);
        return player;
    }

    /**
     * Add players and give all of them the same randomized requirements.
     *
     * @param players ordered player list
     */
    @Override
    public void setPlayers(List<Player> players) {
        for (Player player : players) {
            ((TradingPostsPlayer) player).setPowerRequirements(powerRequirements);
        }
        super.setPlayers(players);
    }

}
