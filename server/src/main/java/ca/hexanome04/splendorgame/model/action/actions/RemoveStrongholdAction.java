package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.strongholds.StrongholdsGame;
import ca.hexanome04.splendorgame.model.gameversions.strongholds.StrongholdsPlayer;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/** Remove exactly one opposing Stronghold. */
public class RemoveStrongholdAction extends Action {

    private String targetCardId;

    /**
     * Build an action that removes one opposing stronghold.
     *
     * @param targetCardId occupied target card
     */
    public RemoveStrongholdAction(String targetCardId) {
        super(Actions.REMOVE_STRONGHOLD);
        this.targetCardId = targetCardId;
    }

    /** Construct an empty action for JSON decoding. */
    public RemoveStrongholdAction() {
        this(null);
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {
        if (!(game instanceof StrongholdsGame strongholds)
                || !strongholds.removeOpponentStronghold((StrongholdsPlayer) player, targetCardId)) {
            return List.of(ActionResult.INVALID_STRONGHOLD_ACTION);
        }
        strongholds.completeMandatoryStrongholdAction();
        game.clearValidActions();
        return new ArrayList<>(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED));
    }

    @Override
    public Action decodeAction(JsonObject json) {
        targetCardId = json.get("targetCardId").getAsString();
        return this;
    }
}
