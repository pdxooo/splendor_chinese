import { showNextModal, setupSelection, backButton } from "./modals.js";
import { performAction } from "../actions";
import { showError } from "../notify.js";

const turnModal = document.querySelector("#your-turn-modal");
const turnModalToggle = turnModal?.querySelector(".turn-modal-toggle");
if(turnModalToggle) {
    turnModalToggle.onclick = () => {
        const collapsed = turnModal.classList.toggle("collapsed");
        turnModalToggle.textContent = collapsed ? "+" : "−";
        turnModalToggle.setAttribute("aria-label", collapsed ? "展开操作框" : "收起操作框");
        turnModalToggle.setAttribute("aria-expanded", String(!collapsed));
    };
}

/**
 * Get the list of tokens when taking / putting back tokens or purchasing a development card
 * @returns [{ token: count }]
 */
const getTokensList = (selector) => {
    const tokensCountNodes = document.querySelectorAll(selector);

    const tokens = {};

    tokensCountNodes.forEach(node => {
        const tokenType = node.getAttribute("jewel-color");
        const count = node.parentNode.count;

        tokens[tokenType] = count;
    });

    return tokens;
};

const setBoardTokens = (modal) => {
    const boardNode = document.querySelector("#board .board-tokens");
    const modalNode = document.querySelector(modal);
    modalNode.querySelector(".red-token > span").textContent = boardNode.querySelector(".red-token > span").textContent;
    modalNode.querySelector(".blue-token > span").textContent = boardNode.querySelector(".blue-token > span").textContent;
    modalNode.querySelector(".green-token > span").textContent = boardNode.querySelector(".green-token > span").textContent;
    modalNode.querySelector(".white-token > span").textContent = boardNode.querySelector(".white-token > span").textContent;
    modalNode.querySelector(".brown-token > span").textContent = boardNode.querySelector(".brown-token > span").textContent;
    modalNode.querySelector(".gold-token > span").textContent = boardNode.querySelector(".gold-token > span").textContent;
};

const readTokenCount = (node) => {
    if(!node) return 0;
    const displayedCount = node.querySelector?.(".board-token > span, span")?.textContent;
    if(displayedCount !== undefined && displayedCount !== null && displayedCount.trim() !== "") {
        return Number(displayedCount) || 0;
    }
    return Number(node.count ?? node.parentNode?.count) || 0;
};

const countTokens = (selector) => Array.from(document.querySelectorAll(selector))
    .reduce((total, node) => total + readTokenCount(node), 0);

const submitTakeTokens = (putBackTokens = {}) => {
    const takeTokens = getTokensList("#take-token-modal board-token-counter board-token .board-token");
    return performAction("TAKE_TOKEN", () => ({ takeTokens, putBackTokens }))
        .then((resp) => {
            if(resp.error) showError(resp.message);
        }).catch((err) => showError(err.toString()));
};

const paymentColors = ["Red", "Blue", "Green", "White", "Brown"];

const getPlayerPaymentState = () => {
    const tokens = {};
    const bonuses = {};
    document.querySelectorAll("#player-inventory .player-inventory-tokens .bonus-container").forEach(container => {
        const token = container.querySelector("board-token");
        const color = token?.getAttribute("color");
        if(!color) return;
        const key = color.charAt(0).toUpperCase() + color.slice(1);
        tokens[key] = readTokenCount(token);
        bonuses[key] = Number(container.querySelector(".bonus-icon > span")?.textContent) || 0;
    });
    return { tokens, bonuses };
};

const calculateMinimumPayment = (cardNode) => {
    const rawCost = cardNode.getAttribute("cost");
    if(!rawCost) return { purchasable: false, payment: {} };

    const cost = JSON.parse(rawCost);
    const { tokens, bonuses } = getPlayerPaymentState();
    const payment = {};

    // Some Orient cards are paid by permanently discarding bonuses instead
    // of spending tokens. They are purchasable only when the bonus cost is met.
    if(cardNode.getAttribute("cost-type") === "Bonus") {
        const purchasable = paymentColors.every(color =>
            Number(bonuses[color] || 0) >= Number(cost[color] || 0));
        paymentColors.forEach(color => payment[color] = 0);
        payment.Gold = 0;
        return { purchasable, payment };
    }

    const goldValue = Number(document.querySelector("#player-inventory")
        ?.getAttribute("data-gold-token-value")) === 2 ? 2 : 1;
    let virtualGoldAvailable = Number(bonuses.Gold || 0);
    let realGoldAvailable = Number(tokens.Gold || 0);
    let realGoldUsed = 0;
    let purchasable = true;

    paymentColors.forEach(color => {
        const remainingCost = Math.max(0, Number(cost[color] || 0) - Number(bonuses[color] || 0));
        const coloredPayment = Math.min(remainingCost, Number(tokens[color] || 0));
        payment[color] = coloredPayment;
        let unpaid = remainingCost - coloredPayment;

        const virtualForColor = Math.min(virtualGoldAvailable, Math.ceil(unpaid / goldValue));
        virtualGoldAvailable -= virtualForColor;
        unpaid = Math.max(0, unpaid - virtualForColor * goldValue);

        const realForColor = Math.min(realGoldAvailable, Math.ceil(unpaid / goldValue));
        realGoldAvailable -= realForColor;
        realGoldUsed += realForColor;
        unpaid = Math.max(0, unpaid - realForColor * goldValue);
        if(unpaid > 0) purchasable = false;
    });
    // Each physical or virtual Gold piece covers up to goldValue tokens of
    // one colour. Virtual Gold is consumed first to minimise real-token use.
    payment.Gold = realGoldUsed;

    return {
        purchasable,
        payment
    };
};


// -----------------------------------------------------------------------------------------
// Setup take token & put back token
const getTokensBtn = document.getElementById("get-tokens-btn");
getTokensBtn.onclick = () => {
    takeTokens();
    showNextModal("#take-token-modal");
};
document.querySelectorAll(".take-token-back-btn").forEach(elm => elm.onclick = backButton);
document.querySelectorAll(".put-back-token-back-btn").forEach(elm => elm.onclick = backButton);

const takeTokens = () => {
    // clear previous numbers
    document.querySelectorAll("#take-token-modal board-token").forEach(elm => {
        elm.setCount(0);
    });

    // set min and max for counters, TODO: set max and min values to what tokens the player has
    document.querySelectorAll("#take-token-modal board-token-counter").forEach(elm => {
        elm.setMax(10);
    });

    setBoardTokens("#take-token-modal");

    document.querySelector("#take-token-modal #take-token-confirm-btn").onclick = () => {
        const currentTotal = countTokens("#player-inventory .player-inventory-tokens board-token");
        const selectedTotal = countTokens("#take-token-modal board-token-counter board-token .board-token");
        if(currentTotal + selectedTotal <= 10) {
            submitTakeTokens();
        } else {
            putBackTokens(currentTotal + selectedTotal - 10);
            showNextModal("#put-back-token-modal");
        }
    };
};

const putBackTokens = (requiredCount) => {
    const confirmBtn = document.querySelector("#put-back-token-modal .put-back-token-confirm-btn");

    // clear previous numbers
    document.querySelectorAll("#put-back-token-modal board-token").forEach(elm => {
        elm.setCount(0);
    });

    // A player may return tokens already held as well as tokens selected in
    // this action. Restrict each counter to that actual available amount.
    document.querySelectorAll("#put-back-token-modal board-token-counter").forEach(elm => {
        const color = elm.getAttribute("color");
        const held = readTokenCount(document.querySelector(
            `#player-inventory .player-inventory-tokens board-token[color="${color}"]`));
        const taken = readTokenCount(document.querySelector(
            `#take-token-modal board-token-counter[color="${color}"] board-token`));
        elm.setMax(held + taken);
    });

    document.querySelectorAll("#put-back-token-modal .player-token-count-container board-token").forEach(elm => {
        const color = elm.getAttribute("color");
        const held = readTokenCount(document.querySelector(
            `#player-inventory .player-inventory-tokens board-token[color="${color}"]`));
        const taken = readTokenCount(document.querySelector(
            `#take-token-modal board-token-counter[color="${color}"] board-token`));
        elm.setCount(held + taken);
    });

    confirmBtn.onclick = () => {
        const selectedCount = countTokens("#put-back-token-modal board-token-counter board-token .board-token");
        if(selectedCount !== requiredCount) {
            showError(`You must return exactly ${requiredCount} token(s) to keep no more than 10.`);
            return;
        }
        confirmBtn.disabled = true;
        submitTakeTokens(getTokensList("#put-back-token-modal board-token-counter board-token .board-token"))
            .finally(() => confirmBtn.disabled = false);
    };
};


// -----------------------------------------------------------------------------------------
// Set up card purchasing
const purchaseBtn = document.getElementById("purchase-btn");
purchaseBtn.onclick = () => {
    // load the purchase items
    showPurchasableDevCards();
    showNextModal("#buy-card-modal");
};
document.querySelectorAll(".buy-card-back-btn").forEach(elm => elm.onclick = backButton);

const showPurchasableDevCards = () => {
    // show reserved cards (from player inv, maybe should pass in data from server instead?)
    const reservedCardNodes = [];
    const reservedCards = document.querySelectorAll("#player-inventory .player-inventory-reservedcards .player-inventory-card-reserved");
    reservedCards.forEach((elm) => {
        const cid = elm.getAttribute("card-id");
        const node = document.querySelector("#board-card-dev-template").content.cloneNode(true);
        const div = node.querySelector("div");
        const imgUrl = `/images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        if(elm.hasAttribute("cost")) div.setAttribute("cost", elm.getAttribute("cost"));
        if(elm.hasAttribute("cost-type")) div.setAttribute("cost-type", elm.getAttribute("cost-type"));
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        reservedCardNodes.push(node);
    });
    document.querySelector("#buy-card-modal .reserved-cards").replaceChildren(...reservedCardNodes);

    // literally just duplicate them from the board

    const cardRows = document.querySelectorAll("#board .board-cards .board-cards-row");

    // clear if already has
    const modalCardRows = document.querySelector("#buy-card-board .modal-board-cards");
    modalCardRows.innerHTML = "";

    cardRows.forEach((elm) => {
        const cNode = elm.cloneNode(true);
        modalCardRows.appendChild(cNode);
    });

    const cardsSelectionSelector = "#buy-card-modal .board-card-dev";

    document.querySelectorAll(cardsSelectionSelector).forEach(card => {
        const { purchasable } = calculateMinimumPayment(card);
        card.classList.toggle("purchasable", purchasable);
        card.classList.toggle("unaffordable", !purchasable);
    });

    setupSelection(cardsSelectionSelector);

    document.querySelector("#buy-card-modal #buy-card-confirm-btn").onclick = () => {
        const selectedCard = document.querySelector(`${cardsSelectionSelector}.selected`);
        if(!selectedCard) {
            // no card has been selected, error
            showError("You have not selected a card to purchase!");
            return;
        }
        if(!calculateMinimumPayment(selectedCard).purchasable) {
            showError("You do not have enough tokens to purchase this card.");
            return;
        }

        showPayment(selectedCard);
        showNextModal("#dev-card-payment-modal");
    };
};

const showPayment = (cardNode) => {

    const confirmBtn = document.querySelector("#dev-card-payment-modal .buy-card-confirm-btn");

    // to be used to restrict selection of tokens in the FUTURE
    // const cost = JSON.parse(cardNode.getAttribute("cost"));
    const cardId = cardNode.getAttribute("card-id");

    // show card in purchase window
    const imgSrc = `/images/development-cards/${cardId}.jpg`;
    document.querySelector("#dev-card-payment-modal .purchase-show-card img").setAttribute("src", imgSrc);

    const { payment } = calculateMinimumPayment(cardNode);
    document.querySelectorAll("#dev-card-payment-modal board-token-counter").forEach(elm => {
        const color = elm.getAttribute("color");
        const key = color.charAt(0).toUpperCase() + color.slice(1);
        const amount = Number(payment[key] || 0);
        elm.querySelector("board-token").setCount(amount);
        elm.setMin(amount);
        elm.setMax(amount);
    });

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const dataCallback = () => {
            return {
                "cardId": cardId,
                "tokens": getTokensList("#dev-card-payment-modal board-token-counter board-token .board-token")
            };
        };

        performAction("BUY_CARD", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    showError(resp.message);
                }
            }).catch((err) => {
                showError(err.toString());
            }).finally(() =>  confirmBtn.disabled = false);
    };

};


// -----------------------------------------------------------------------------------------
// Set up reserving cards
const reserveBtn = document.getElementById("reserve-btn");
reserveBtn.onclick = () => {
    // load the purchase items
    showReservableDevCards();
    showNextModal("#reserve-card-modal");
};
document.querySelectorAll(".reserve-card-back-btn").forEach(elm => elm.onclick = backButton);

const showReservableDevCards = () => {

    const confirmBtn = document.querySelector("#reserve-card-modal .reserve-card-confirm-btn");

    // literally just duplicate them from the board
    const cardRows = document.querySelectorAll("#board .board-cards .board-cards-row");

    // clear if already has
    const modalCardRows = document.querySelector("#reserve-card-board .modal-board-cards");
    modalCardRows.innerHTML = "";

    cardRows.forEach((elm) => {
        const cNode = elm.cloneNode(true);
        modalCardRows.appendChild(cNode);
    });

    const deckByRow = {
        "board-cards-level1": "DECK_TIER_1",
        "board-cards-level2": "DECK_TIER_2",
        "board-cards-level3": "DECK_TIER_3"
    };
    Object.entries(deckByRow).forEach(([rowClass, deckId]) => {
        const deck = modalCardRows.querySelector(`.${rowClass} .board-cards-dev-deck`);
        if(deck) {
            deck.setAttribute("data-deck-id", deckId);
            deck.setAttribute("title", `Reserve blindly from ${deckId.replace("DECK_TIER_", "level ")}`);
        }
    });

    const cardsSelectionSelector = "#reserve-card-modal .modal-board-cards .board-card-dev";

    setupSelection(cardsSelectionSelector);

    let selectedDeckId = null;
    const deckButtons = document.querySelectorAll("#reserve-card-modal [data-deck-id]");
    deckButtons.forEach(button => {
        button.classList.remove("selected");
        button.onclick = () => {
            document.querySelectorAll(cardsSelectionSelector).forEach(card => card.classList.remove("selected"));
            deckButtons.forEach(other => other.classList.remove("selected"));
            button.classList.add("selected");
            selectedDeckId = button.getAttribute("data-deck-id");
        };
    });
    document.querySelectorAll(cardsSelectionSelector).forEach(card => {
        card.addEventListener("click", () => {
            selectedDeckId = null;
            deckButtons.forEach(button => button.classList.remove("selected"));
        });
    });

    const submitReservation = (cardId, putBackTokens = {}) => {
        confirmBtn.disabled = true;
        return performAction("RESERVE_CARD", () => ({ cardId, putBackTokens }))
            .then((resp) => {
                if(resp.error) showError(resp.message);
            }).catch((err) => showError(err.toString()))
            .finally(() => confirmBtn.disabled = false);
    };

    const requestReservationReturn = (cardId, requiredCount) => {
        const modalSelector = "#put-back-token-modal";
        const modal = document.querySelector(modalSelector);

        modal.querySelectorAll("board-token-counter").forEach(counter => {
            const color = counter.getAttribute("color");
            const held = readTokenCount(document.querySelector(
                `#player-inventory .player-inventory-tokens board-token[color="${color}"]`));
            const goldReceived = color === "gold" ? 1 : 0;
            counter.querySelector("board-token").setCount(0);
            counter.setMin(0);
            counter.setMax(held + goldReceived);
        });

        modal.querySelectorAll(".player-token-count-container board-token").forEach(token => {
            const color = token.getAttribute("color");
            const held = readTokenCount(document.querySelector(
                `#player-inventory .player-inventory-tokens board-token[color="${color}"]`));
            token.setCount(held + (color === "gold" ? 1 : 0));
        });

        const returnConfirmBtn = modal.querySelector(".put-back-token-confirm-btn");
        returnConfirmBtn.onclick = () => {
            const selectedCount = countTokens(
                `${modalSelector} board-token-counter board-token .board-token`);
            if(selectedCount !== requiredCount) {
                showError(`You must return exactly ${requiredCount} token(s) to keep no more than 10.`);
                return;
            }
            returnConfirmBtn.disabled = true;
            const returnedTokens = getTokensList(
                `${modalSelector} board-token-counter board-token .board-token`);
            submitReservation(cardId, returnedTokens)
                .finally(() => returnConfirmBtn.disabled = false);
        };

        showNextModal(modalSelector);
    };

    confirmBtn.onclick = () => {
        const selectedCard = document.querySelector(`${cardsSelectionSelector}.selected`);
        if(!selectedCard && !selectedDeckId) {
            // no card has been selected, error
            showError("You have not selected a card to reserve!");
            return;
        }

        const cardId = selectedDeckId ?? selectedCard.getAttribute("card-id");
        const currentTotal = countTokens("#player-inventory .player-inventory-tokens board-token");
        const bankGold = readTokenCount(document.querySelector("#board .board-tokens .gold-token"));
        const requiredReturnCount = Math.max(0, currentTotal + (bankGold > 0 ? 1 : 0) - 10);

        if(requiredReturnCount > 0) {
            requestReservationReturn(cardId, requiredReturnCount);
        } else {
            submitReservation(cardId);
        }
    };
};

