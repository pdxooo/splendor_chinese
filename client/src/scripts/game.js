import { SETTINGS } from "./settings.js";
import { startTurn, verifyNoModals, performFollowUpAction, showNextModal } from "./modals/modals.js";
import { animateMoveToken } from "./animation/tokens";
import { moveCardFromDeck, moveCard, moveCardReserved } from "./animation/cards";
import { writeUpdate } from "./history";
import { runDelayed } from "./animation/utils";
import { showError } from "./notify.js";
import { initGameOver } from "./modals/gameover.js";

// eslint-disable-next-line no-undef
var MD5 = CryptoJS.MD5;

const updateTokensCount = (parentSelector, tokenInfo, bonusInfo = null) => {
    const parentNode = document.querySelector(parentSelector);

    const setTokenInfo = (elm, color) => {
        // set token count
        const updateText = () => elm.textContent = tokenInfo[color];

        // set bonus
        if(bonusInfo) {
            // get bonus container
            elm.closest(".bonus-container").querySelector(".bonus-icon > span").textContent = bonusInfo[color];

            const hasCount = parseInt(elm.textContent);
            const newCount = tokenInfo[color];
            if(hasCount !== newCount) {
                // animate tokens moving
                // slight delay so that the player inventory will be correct size
                runDelayed(() => animateMoveToken(newCount - hasCount, color, parentSelector, "#board .board-tokens", updateText));
            }
        } else {
            updateText(); // update text immediately
        }
    };
    const colors = ["Red", "Blue", "Green", "White", "Brown", "Gold"];

    colors.forEach((col) => {
        setTokenInfo(parentNode.querySelector(`.${col.toLowerCase()}-token > span`), col);
    });
};

const updateTierRow = (selector, devCardsDeck, orientCardsDeck) => {
    const devCards = devCardsDeck.visibleCards;
    const orientCards = orientCardsDeck.visibleCards;
    const row = document.querySelector(selector);

    const updateCardElement = (deckSelector, cardSelector, freeCardElm, cardInfo) => {
        const cardId = cardInfo["id"];
        const cost = cardInfo["tokenCost"];
        const costType = cardInfo["costType"];

        const imgUrl = `/images/development-cards/${cardId}.jpg`;
        const imgElm = freeCardElm.querySelector("img");
        imgElm.src = imgUrl;
        imgElm.classList.add("invisible");

        // add additional info
        freeCardElm.setAttribute("card-id", cardId);
        freeCardElm.setAttribute("cost", JSON.stringify(cost));
        freeCardElm.setAttribute("cost-type", costType);

        // slight timeout to ensure no content shift
        // only delay if deck is not invisible
        const shouldDelay = document.querySelector(`${selector} ${deckSelector}`).getAttribute("empty") !== null;
        moveCardFromDeck(`${selector} ${deckSelector}`, `${selector} ${cardSelector}[card-id="${cardId}"]`, shouldDelay);
    };

    // clear images first
    const clearIfNeeded = (cards, cardSelector, deckSelector) => {
        const serverCardIds = cards.map(c => c.id);
        const currentCardIds = [...row.querySelectorAll(`${cardSelector}[card-id]`)]
                                       .map(elm => elm.getAttribute("card-id"));

        // cards that exist on board but not server side, must remove
        const outdatedCardIds = currentCardIds.filter(x => !serverCardIds.includes(x));
        outdatedCardIds.forEach(cid => {
            const oldElm = row.querySelector(`${selector} ${cardSelector}[card-id="${cid}"]`);
            oldElm.removeAttribute("card-id");
            oldElm.removeAttribute("cost");
            oldElm.removeAttribute("cost-type");
            oldElm.querySelector("img").removeAttribute("src");
        });

        const missingCards = cards.filter(c => !currentCardIds.includes(c.id));
        missingCards.forEach(card => {
            // select empty ones to fill
            const emptyCardElm = document.querySelector(`${selector} ${cardSelector}:not([card-id])`);
            updateCardElement(deckSelector, cardSelector, emptyCardElm, card);
        });
    };
    clearIfNeeded(devCards, ".board-card-dev:not(.board-card-dev-orient)", ".board-cards-dev-deck");
    clearIfNeeded(orientCards, ".board-card-dev.board-card-dev-orient", ".board-cards-dev-deck-orient");

    // mark deck as empty if needed
    const setEmptyIfNeeded = (deckSelector, canDraw) => {
        if(!canDraw) {
            row.querySelector(deckSelector).setAttribute("empty", "true");
        }
    };
    
    setEmptyIfNeeded(".board-cards-dev-deck", devCardsDeck.canDraw);
    setEmptyIfNeeded(".board-cards-dev-deck-orient", orientCardsDeck.canDraw);
};

/**
 * Update a series of cards with the given array.
 * 
 * @param {Array} cards list of cards
 * @param {HTMLElement} baseElement base element for all other selectors
 * @param {string} containerSelector container selector for all cards
 * @param {string} cardSelector selector for single card
 * @param {string} imageFolder folder name for images
 * @param {string} cardTemplateSelector template for card (could become just an HTMLElement name todo)
 */
export const updateCards = (cards, baseElement, containerSelector, cardSelector, imageFolder, cardTemplateSelector) => {

    const cardContainer = baseElement.querySelector(containerSelector);

    const currentCardIds = [];
    cardContainer.querySelectorAll(cardSelector).forEach(elm => currentCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverCardIds = cards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const outdatedCardIds = currentCardIds.filter(x => !serverCardIds.includes(x));
    outdatedCardIds.forEach(cid => cardContainer.querySelector(`${cardSelector}[card-id="${cid}"]`).remove());

    // add new reserved cards to inv
    const missingCardIds = serverCardIds.filter(x => !currentCardIds.includes(x));
    missingCardIds.forEach(cid => {
        const tNode = document.querySelector(cardTemplateSelector).content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/${imageFolder}/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        const cardInfo = cards.find(card => card.id === cid);
        if(cardInfo?.tokenCost) div.setAttribute("cost", JSON.stringify(cardInfo.tokenCost));
        if(cardInfo?.costType) div.setAttribute("cost-type", cardInfo.costType);
        const imgElm = div.querySelector("img");
        imgElm.setAttribute("src", imgUrl);

        // add to inv
        cardContainer.appendChild(tNode);

        // since this will be called before removing the card from the board,
        // we can check if the card already exists on the board,
        // if yes => make it invisible and queue move animation
        // else don't animate or make invisible
        const boardCardSel = document.querySelector(`.board-container *[card-id="${cid}"]`)
                             || document.querySelector(`.board *[card-id="${cid}"]`); // for regular base + orient board
        if(boardCardSel) {
            imgElm.classList.add("invisible");

            // slight delay so that the player inventory will be correct size
            if(containerSelector.includes("reserve")) {
                moveCardReserved(boardCardSel, cardContainer.querySelector(`*[card-id="${cid}"]`), `${imageFolder}/${cid}.jpg`, true);
            } else {
                moveCard(boardCardSel, cardContainer.querySelector(`*[card-id="${cid}"]`), `${imageFolder}/${cid}.jpg`, true);
            }
        }
    });
};

const setPlayerIdentity = (container, name, order) => {
    const nameNode = container.querySelector(".player-name");
    const orderNode = container.querySelector(".turn-order-label");
    if(nameNode) nameNode.textContent = name;
    if(orderNode) {
        orderNode.textContent = `第 ${order} 位`;
        orderNode.title = `行动顺序：第 ${order} 位`;
    }
};

const updateMainPlayerInfo = (playerInfo, order) => {

    const playerInv = document.querySelector("#player-inventory");
    setPlayerIdentity(playerInv.querySelector(".player-identity"), playerInfo.name, order);


    // update prestige points
    playerInv.querySelector(".player-prestige-points-container > span").textContent = playerInfo.prestigePoints;

    // why
    const tokenMap = playerInfo.tokens;
    updateTokensCount(".player-inventory-tokens", tokenMap, playerInfo.bonuses);

    // UPDATE DEVELOPMENT CARDS IN PLAYER INVENTORY
    updateCards(playerInfo.devCards, playerInv, ".player-inventory-card-drawer",
                ".player-inventory-card", "development-cards", "#development-card-template");

    // mark cards as satchel (so that we can select them later in the choose satchel token type)
    playerInfo.devCards.forEach((c) => {
        const card = document.querySelector(`.player-inventory-card-drawer .player-inventory-card[card-id="${c.id}"]`);
        if(c.tokenType === "Satchel") {
            card.setAttribute("satchel", "true");
        } else if(c.tokenType !== "Satchel" && card.hasAttribute("satchel")) {
            card.removeAttribute("satchel");
        }
    });


    // UPDATE RESERVED CARDS IN PLAYER INVENTORY
    updateCards(playerInfo.reservedCards, playerInv, ".player-inventory-reservedcards",
                ".player-inventory-card-reserved", "development-cards", "#player-inventory-reserved-card-template");

    // Update nobles
    updateCards(playerInfo.nobleCards, playerInv, ".player-inventory-nobles",
                ".noble-card", "nobles", "#noble-card-template");

    // Update reserved nobles
    updateCards(playerInfo.reservedNobles, playerInv, ".player-inventory-reserved-nobles",
                ".noble-card", "nobles", "#noble-card-template");

    // shrink player inventory if too much crap
    let containerCount = 0;
    if(playerInfo.devCards.length > 0) containerCount++;
    if(playerInfo.reservedCards.length > 0) containerCount++;
    if(playerInfo.nobleCards.length > 0) containerCount++;
    if(playerInfo.reservedNobles.length > 0) containerCount++;

    if(containerCount > 2) {
        playerInv.classList.add("shrink");
    } else {
        playerInv.classList.remove("shrink");
    }
};

const updateOtherPlayerInfo = (pInfo, order) => {
    const selector = `.other-players .other-player[pname="${pInfo.name}"]`;
    let pNode = document.querySelector(selector);

    // if node doesn't exist, we must make
    if(!pNode) {
        const tNode = document.querySelector("#other-player-tab-template").content.cloneNode(true);
        const pDiv = tNode.querySelector(".other-player");

        pDiv.setAttribute("pname", pInfo.name);
        pDiv.querySelector(".other-player-name").textContent = pInfo.name;

        document.querySelector(".other-players").appendChild(tNode);

        // now reget the node
        pNode = document.querySelector(selector);
    }
    setPlayerIdentity(pNode.querySelector(".other-player-profile"), pInfo.name, order);

    // update tokens, cards, prestige points
    const tokenMap = pInfo.tokens;
    updateTokensCount(selector, tokenMap, pInfo.bonuses);


    // UPDATE DEVELOPMENT CARDS IN OTHER PLAYER INVENTORIES
    updateCards(pInfo.devCards, pNode, ".other-inventory-cards",
                ".other-inventory-card", "development-cards", "#other-player-dev-card-template");

    // Face-up reservations remain public. Blind reservations intentionally
    // contain only cardTier and are rendered with the matching card back.
    const reservedCardBacks = {
        "TIER_1": "/images/GreenCard.jpg",
        "TIER_2": "/images/YellowCard.jpg",
        "TIER_3": "/images/BlueCard.jpg"
    };
    const reservedCards = (pInfo.reservedCards || []).map((card, index) => {
        const node = document.querySelector("#other-player-reserved-card-template").content.cloneNode(true);
        const div = node.querySelector(".other-inventory-card-reserved");
        const image = div.querySelector("img");
        if(card.id) {
            div.setAttribute("card-id", card.id);
            div.setAttribute("card-tier", card.cardTier || "UNKNOWN");
            if(card.tokenCost) div.setAttribute("cost", JSON.stringify(card.tokenCost));
            if(card.costType) div.setAttribute("cost-type", card.costType);
            image.setAttribute("src", `/images/development-cards/${card.id}.jpg`);
            image.setAttribute("alt", `公开预留牌 ${index + 1}`);
        } else {
            div.setAttribute("card-tier", card.cardTier || "UNKNOWN");
            div.setAttribute("face-down", "true");
            image.setAttribute("src", reservedCardBacks[card.cardTier] || "/images/GreenCard.jpg");
            image.setAttribute("alt", `暗置预留的${card.cardTier || "未知等级"}卡牌 ${index + 1}`);
        }
        return node;
    });
    pNode.querySelector(".other-inventory-cards-reserved").replaceChildren(...reservedCards);

    // Update nobles
    updateCards(pInfo.nobleCards, pNode, ".other-inventory-noble-cards",
                ".noble-card", "nobles", "#noble-card-template");

    // Update reserved nobles
    updateCards(pInfo.reservedNobles, pNode, ".other-inventory-reserved-noble-cards",
                ".noble-card", "nobles", "#noble-card-template");

    // update prestige points
    pNode.querySelector(".other-prestige-point-container > span").textContent = pInfo.prestigePoints;

};

const updateNoblesBoard = async (cards) => {
    const selector = ".board-nobles .noble-card";

    const serverCardIds = cards.map(c => c.id);
    const currentCardIds = [...document.querySelectorAll(`${selector}[card-id]`)].map(elm => elm.getAttribute("card-id"));

    // cards that exist on board but not server side, must remove
    const outdatedCardIds = currentCardIds.filter(x => !serverCardIds.includes(x));
    outdatedCardIds.forEach(cid => {
        const oldElm = document.querySelector(`${selector}[card-id="${cid}"]`);
        oldElm.removeAttribute("card-id");
        oldElm.querySelector("img").removeAttribute("src");
    });

    const missingCards = cards.filter(c => !currentCardIds.includes(c.id));
    missingCards.forEach(card => {
        // select empty ones to fill
        const emptyCardElm = document.querySelector(`${selector}:not([card-id])`);
        emptyCardElm.setAttribute("card-id", card.id);
        emptyCardElm.querySelector("img").setAttribute("src", `/images/nobles/${card.id}.jpg`);
        emptyCardElm.setAttribute("had-card", "true");
    });
};

let gameStateHash = "-";
let lastState = null;
let currentState = null;
let turnDeadline = 0;
let serverClockOffset = 0;

const updateTurnTimer = () => {
    const timer = document.querySelector("#turn-timer");
    if(!timer || !currentState?.players?.length) return;
    if(currentState.gameOver) {
        timer.textContent = "游戏已结束";
        timer.classList.remove("warning");
        return;
    }
    const remaining = Math.max(0, Math.ceil((turnDeadline - (Date.now() + serverClockOffset)) / 1000));
    const player = currentState.players[currentState.turnCounter];
    if(!player) {
        timer.textContent = "正在同步回合…";
        timer.classList.remove("warning");
        return;
    }
    timer.textContent = remaining > 0 ? `${player.name} · ${remaining} 秒` : "时间到，正在换人…";
    timer.classList.toggle("warning", remaining <= 10);
};

const attempUpdate = () => {
    let nextCallTime = 1;
    updateGameboard().catch((err) => {
        if(!err.toString().includes("Failed to fetch" )) {
            console.log("[AS] Error during check (retry 30s): " + err);
        }
        showError(err.toString());
        nextCallTime = 30000;
    }).finally(() => {
        setTimeout(attempUpdate, nextCallTime);
    });
};

// array for other updates that can be done (extensions)
const furtherUpdates = [];

const updateGameboard = async () => {
    await SETTINGS.verifyCredentials();

    const windowParams = (new URL(document.location)).searchParams;
    const sessionId = windowParams.get("sessionId");
    const params = {
        "hash": gameStateHash,
        "access_token": SETTINGS.getAccessToken()
    };

    const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}`);
    url.search = new URLSearchParams(params).toString();

    const resp = await fetch(url);

    const dataText = await resp.text();

    if(resp.status == 204) {
        // no update
        return;
    }

    if(!resp.ok) {
        throw new Error(dataText);
    }

    // update hash
    // console.log("Update: " + t);
    const newHash = MD5(dataText);

    // update only if needed
    if(newHash === gameStateHash) {
        return;
    }

    gameStateHash = newHash;
    console.log("[AS] Update available!");
    const data = JSON.parse(dataText);
    const baseOnlyVersions = new Set([
        "BASE",
        "BASE_ORIENT_CITIES",
        "BASE_ORIENT_TRADE_ROUTES"
    ]);
    document.body.classList.toggle("classic-game", baseOnlyVersions.has(data.gameVersion));
    lastState = currentState;
    currentState = data;
    turnDeadline = data.turnDeadlineEpochMillis || 0;
    serverClockOffset = (data.serverTimeEpochMillis || Date.now()) - Date.now();
    updateTurnTimer();
    window.dispatchEvent(new CustomEvent("splendor-state-update", {detail: data}));
    writeUpdate(lastState, currentState);

    // tokens update
    const tokenMap = data.tokens;
    updateTokensCount("#board .board-tokens", tokenMap);

    const curUsername = SETTINGS.getUsername();

    const playersData = data.players;
    let amPlaying = false;
    playersData.forEach((pInfo, index) => {
        if(pInfo.name === curUsername) {
            updateMainPlayerInfo(pInfo, index + 1);
            amPlaying = true;
        } else {
            updateOtherPlayerInfo(pInfo, index + 1);
        }
    });
    const playerInv = document.querySelector("#player-inventory");
    if(!amPlaying) {
        // you are spectating, hide the player inventory on the left
        playerInv.classList.add("hide");
    } else if(playerInv.classList.contains("hide")) {
        playerInv.classList.remove("hide");
    }

    updateNoblesBoard(data.nobleDeck.visibleCards);
    updateTierRow(".board-cards-row.board-cards-level1", data.tier1Deck, data.tier1OrientDeck);
    updateTierRow(".board-cards-row.board-cards-level2", data.tier2Deck, data.tier2OrientDeck);
    updateTierRow(".board-cards-row.board-cards-level3", data.tier3Deck, data.tier3OrientDeck);

    furtherUpdates.forEach((func) => func(data));

    if(data.gameOver) {
        verifyNoModals();
        initGameOver(data);
        showNextModal("#gameover-modal");
    } else {
        // check if it's your turn
        if(data.players[data.turnCounter].name === curUsername) {
            if(!performFollowUpAction(data)) {
                startTurn();
            }
        } else {
            verifyNoModals();
        }
    }
};

/**
 * Add an updater to the main update executor.
 * The added function should take in as an argument the data from the game server.
 * 
 * @param {Function} func 
 */
export const addUpdater = (func) => {
    furtherUpdates.push(func);
}

document.addEventListener("DOMContentLoaded", () => {
    const timer = document.createElement("div");
    timer.id = "turn-timer";
    timer.className = "turn-timer";
    timer.textContent = "等待游戏状态…";
    document.body.appendChild(timer);
    setInterval(updateTurnTimer, 250);
    setTimeout(attempUpdate, 1);
});
