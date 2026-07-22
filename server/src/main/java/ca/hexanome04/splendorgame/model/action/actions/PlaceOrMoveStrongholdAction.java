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

/** Place an available Stronghold, or move one already on the board. */
public class PlaceOrMoveStrongholdAction extends Action {

    private String sourceCardId;
    private String targetCardId;

    /**
     * Build a Stronghold placement or movement action.
     *
     * @param sourceCardId source card when moving an existing stronghold
     * @param targetCardId target visible development card
     */
    public PlaceOrMoveStrongholdAction(String sourceCardId, String targetCardId) {
        super(Actions.PLACE_OR_MOVE_STRONGHOLD);
        this.sourceCardId = sourceCardId;
        this.targetCardId = targetCardId;
    }

    /** Construct an empty action for JSON decoding. */
    public PlaceOrMoveStrongholdAction() {
        this(null, null);
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {
        if (!(game instanceof StrongholdsGame strongholds)
                || !strongholds.placeOrMoveStronghold((StrongholdsPlayer) player,
                sourceCardId, targetCardId)) {
            return List.of(ActionResult.INVALID_STRONGHOLD_ACTION);
        }
        strongholds.completeMandatoryStrongholdAction();
        game.clearValidActions();
        return new ArrayList<>(List.of(ActionResult.VALID_ACTION, ActionResult.TURN_COMPLETED));
    }

    @Override
    public Action decodeAction(JsonObject json) {
        sourceCardId = json.has("sourceCardId") && !json.get("sourceCardId").isJsonNull()
                ? json.get("sourceCardId").getAsString() : null;
        targetCardId = json.get("targetCardId").getAsString();
        return this;
    }
}
