package ca.hexanome04.splendorgame.model.action;

/**
 * Constants explaining why an action completed successfully or failed.
 */
public enum ActionResult {

    /**
     * Turn completed.
     */
    TURN_COMPLETED,

    /**
     * Valid action.
     */
    VALID_ACTION,

    /**
     * Invalid player attempting to take action (e.g. not their turn).
     */
    INVALID_PLAYER("It is not this players turn."),

    /**
     * Invalid number of tokens given.
     */
    INVALID_TOKENS_GIVEN("The tokens chosen are invalid."),

    /** Too many tokens of one colour were selected. */
    TOO_MANY_SAME_COLOUR_TOKENS("You cannot take three or more tokens of the same colour."),

    /** Taking a pair requires a sufficiently large bank pile. */
    DOUBLE_TOKENS_REQUIRE_FOUR("You may take two tokens of one colour only when at least four remain."),

    /** The classic different-colour option requires three colours when available. */
    MUST_TAKE_THREE_DIFFERENT_TOKENS("You must take three tokens of different colours when three colours are available."),

    /** Gold is obtained by reserving, not by taking tokens. */
    CANNOT_TAKE_GOLD_TOKEN("Gold tokens cannot be taken directly; reserve a card to receive one."),

    /** A pair cannot be combined with another colour. */
    CANNOT_MIX_DOUBLE_AND_SINGLE_TOKENS("You cannot combine two tokens of one colour with tokens of another colour."),

    /**
     * Invalid token chosen for satchel card assignment.
     */
    INVALID_TOKEN_CHOSEN("Invalid token type chosen for assigning satchel card bonus."),

    /**
     * Maximum number of cards reserved.
     */
    MAXIMUM_CARDS_RESERVED("You already have the maximum amount of reserved cards!"),

    /**
     * Not enough tokens left on the board to select.
     */
    NOT_ENOUGH_TOKENS_ON_BOARD("There are not enough tokens on the board for you to take."),

    /**
     * Player has hit maximum token threshold in inventory.
     */
    MAXIMUM_TOKENS_IN_INVENTORY("You must return enough tokens to keep no more than 10."),

    /**
     * Player does not have enough tokens in inventory.
     */
    NOT_ENOUGH_TOKENS_IN_INVENTORY("You do not have enough tokens in your inventory."),

    /**
     * Player has qualified for 2+ nobles and must choose noble.
     */
    MUST_CHOOSE_NOBLE,

    /**
     * Player has received an Orient card allowing them to reserve a noble.
     */
    MUST_RESERVE_NOBLE,

    /**
     * Player has received an Orient card allowing them to choose a cascade card of tier 1.
     */
    MUST_CHOOSE_CASCADE_CARD_TIER_1,

    /**
     * Player has received an Orient card allowing them to choose a cascade card of tier 2.
     */
    MUST_CHOOSE_CASCADE_CARD_TIER_2,

    /**
     * Player has received an Orient card of type satchel, so they must choose a token type.
     */
    MUST_CHOOSE_TOKEN_TYPE,

    /**
     * Player has purchased a development card, so they can take an extra token using power 1.
     */
    MUST_TAKE_EXTRA_TOKEN_AFTER_PURCHASE,

    /**
     * Player has qualified for 2+ cities and must choose city.
     */
    MUST_CHOOSE_CITY;


    private final String description;

    /**
     * Construct a new Action Result.
     *
     * @param description description of the action result
     */
    ActionResult(String description) {
        this.description = description;
    }

    /**
     * Construct a new Action Result with no description.
     */
    ActionResult() {
        this("");
    }

    /**
     * Retrieve the description of the action result.
     *
     * @return action result description
     */
    public String getDescription() {
        return this.description;
    }
}
