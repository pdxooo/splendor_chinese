package ca.hexanome04.splendorgame.model.action;

/**
 * Possible action identifiers.
 */
public enum Actions {

    /**
     * Buy card action.
     */
    BUY_CARD,

    /**
     * Take token action.
     */
    TAKE_TOKEN,

    /**
     * Reserve card action.
     */
    RESERVE_CARD,

    /**
     * Choose noble action.
     */
    CHOOSE_NOBLE,

    /**
     * Choose cascade tier 1 action.
     */
    CASCADE_1,

    /**
     * Choose cascade tier 2 action.
     */
    CASCADE_2,

    /**
     * Reserve noble action.
     */
    RESERVE_NOBLE,

    /**
     * Choose token type for satchel action.
     */
    CHOOSE_SATCHEL_TOKEN,
    
    /**
     * Take token from power 1.
     */
    TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER,

    /**
     * Choose city action.
     */
    CHOOSE_CITY,

    /** Place an available stronghold or move one already placed. */
    PLACE_OR_MOVE_STRONGHOLD,

    /** Remove exactly one opposing stronghold. */
    REMOVE_STRONGHOLD,

    /** Pay for a card occupied by three of the current player's strongholds. */
    CONQUER_CARD,

    /** Decline an available conquest and complete the turn. */
    SKIP_CONQUEST,
}
