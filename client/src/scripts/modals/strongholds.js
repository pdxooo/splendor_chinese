import { performAction } from "../actions";
import { SETTINGS } from "../settings.js";
import { showError } from "../notify.js";
import { showNextModal } from "./modals.js";
import { showPayment } from "./taketurn.js";

const makeCardOption = (card, className) => {
    const option = document.createElement("div");
    option.className = className;
    option.setAttribute("card-id", card.getAttribute("card-id"));
    ["cost", "cost-type", "stronghold-owner", "stronghold-count"].forEach(name => {
        if(card.hasAttribute(name)) option.setAttribute(name, card.getAttribute(name));
    });
    const image = document.createElement("img");
    image.src = card.querySelector("img")?.src || "";
    option.appendChild(image);
    return option;
};

const visibleBoardCards = () => Array.from(document.querySelectorAll(
    "#board .board-card-dev[card-id]:not(.board-card-dev-orient)"));

export const showStrongholdAction = (data) => {
    const modal = document.querySelector("#stronghold-action-modal");
    const container = modal.querySelector(".stronghold-card-options");
    const status = modal.querySelector(".stronghold-selection-status");
    const username = SETTINGS.getUsername();
    const player = data.players.find(candidate => candidate.name === username);
    let mode = "place";
    let sourceCardId = null;
    let targetCardId = null;

    const refresh = () => {
        container.replaceChildren();
        const available = Number(player?.availableStrongholds ?? 0);
        status.textContent = mode === "remove"
            ? "请选择一张带有对手要塞的卡牌；只拆除其中一个。"
            : available > 0
                ? `你还有 ${available} 个未放置要塞，请选择目标卡牌。`
                : sourceCardId
                    ? "已选择要移动的要塞，请选择目标卡牌。"
                    : "三个要塞均已放置，请先选择一个自己的来源卡牌。";

        visibleBoardCards().forEach(card => {
            const owner = card.getAttribute("stronghold-owner");
            const count = Number(card.getAttribute("stronghold-count") || 0);
            const option = makeCardOption(card, "stronghold-card-option");
            if(card.getAttribute("card-id") === sourceCardId) option.classList.add("source");
            if(card.getAttribute("card-id") === targetCardId) option.classList.add("selected");
            option.onclick = () => {
                const id = option.getAttribute("card-id");
                if(mode === "remove") {
                    if(!owner || owner === username) {
                        showError("只能拆除对手的要塞。");
                        return;
                    }
                    targetCardId = id;
                } else if(available === 0 && !sourceCardId) {
                    if(owner !== username || count === 0) {
                        showError("请先选择一张放有自己要塞的来源卡牌。");
                        return;
                    }
                    sourceCardId = id;
                } else {
                    if(owner && owner !== username) {
                        showError(`该卡被 ${owner} 的要塞占领。`);
                        return;
                    }
                    if(count >= 3 || id === sourceCardId) {
                        showError("该目标不能再放置要塞。");
                        return;
                    }
                    targetCardId = id;
                }
                refresh();
            };
            container.appendChild(option);
        });
    };

    modal.querySelectorAll("[data-mode]").forEach(button => {
        button.onclick = () => {
            mode = button.getAttribute("data-mode");
            sourceCardId = null;
            targetCardId = null;
            modal.querySelectorAll("[data-mode]").forEach(item =>
                item.classList.toggle("active", item === button));
            refresh();
        };
    });
    modal.querySelector('[data-mode="place"]').classList.add("active");
    modal.querySelector(".stronghold-confirm-btn").onclick = () => {
        if(!targetCardId) {
            showError("请先完成要塞卡牌选择。");
            return;
        }
        const action = mode === "remove" ? "REMOVE_STRONGHOLD" : "PLACE_OR_MOVE_STRONGHOLD";
        const payload = mode === "remove" ? {targetCardId} : {sourceCardId, targetCardId};
        performAction(action, payload).then(response => {
            if(response.error) showError(response.message);
        });
    };
    refresh();
};

export const showConquest = () => {
    const modal = document.querySelector("#conquest-modal");
    const container = modal.querySelector(".conquest-card-options");
    const username = SETTINGS.getUsername();
    let selected = null;
    container.replaceChildren();
    visibleBoardCards().filter(card =>
        card.getAttribute("stronghold-owner") === username
        && Number(card.getAttribute("stronghold-count")) === 3)
        .forEach(card => {
            const option = makeCardOption(card, "conquest-card-option");
            option.onclick = () => {
                container.querySelectorAll(".selected").forEach(item => item.classList.remove("selected"));
                option.classList.add("selected");
                selected = option;
            };
            container.appendChild(option);
        });
    modal.querySelector(".conquest-skip-btn").onclick = () => {
        performAction("SKIP_CONQUEST", {}).then(response => {
            if(response.error) showError(response.message);
        });
    };
    modal.querySelector(".conquest-confirm-btn").onclick = () => {
        if(!selected) {
            showError("请选择要征服的卡牌。");
            return;
        }
        showPayment(selected, "CONQUER_CARD");
        showNextModal("#dev-card-payment-modal");
    };
};

window.splendorStrongholds = {showStrongholdAction, showConquest};
