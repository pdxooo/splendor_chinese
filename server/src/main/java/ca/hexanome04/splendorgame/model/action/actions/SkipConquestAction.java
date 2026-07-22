package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/** Decline the optional conquest and finish the normal turn. */
public class SkipConquestAction extends Action {

    /** Construct an action that declines conquest. */
    public SkipConquestAction() {
        super(Actions.SKIP_CONQUEST);
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {
        game.clearValidActions();
        return new ArrayList<>(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED));
    }

    @Override
    public Action decodeAction(JsonObject json) {
        return this;
    }
}
