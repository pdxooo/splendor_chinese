import { performAction } from "../actions";
import { backButton, setupSelection, showNextModal } from "./modals.js";
import { showError } from "../notify.js";

export const showExtraTokens = () => {

    const extraTokenSelectionSelector = "#take-extra-token-modal .board-token";
    const submitExtraToken = (takeToken, putBackToken = null) => {
        const data = {};
        if(takeToken) data.takeToken = takeToken;
        if(putBackToken) data.putBackToken = putBackToken;
        return performAction("TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER", data)
            .then((resp) => {
                if(resp.error) showError(resp.message);
                return resp;
            }).catch((err) => showError(err.toString()));
    };

    const takeConfirmBtn = document.querySelector("#take-extra-token-modal .extra-token-confirm-btn");
    takeConfirmBtn.onclick = () => {
        const selected = document.querySelector(`${extraTokenSelectionSelector}.selected-token`);
        if(!selected) {
            showError("请选择一枚银行中仍有库存的宝石。");
            return;
        }
        if(selected.classList.contains("unavailable")) {
            showError("银行中已没有这种颜色的宝石。");
            return;
        }
        const currentTotal = Array.from(document.querySelectorAll(
            "#player-inventory .player-inventory-tokens board-token .board-token"))
            .reduce((sum, token) => sum + (Number(token.textContent?.trim()) || 0), 0);
        const takeToken = selected.getAttribute("color");
        if(currentTotal + 1 <= 10) {
            takeConfirmBtn.disabled = true;
            submitExtraToken(takeToken).finally(() => takeConfirmBtn.disabled = false);
            return;
        }
        showNextModal("#putback-extra-token-modal");
    };

    setupSelection(extraTokenSelectionSelector, "selected-token");
    document.querySelectorAll(extraTokenSelectionSelector).forEach(token => {
        const color = token.getAttribute("color").toLowerCase();
        const bankToken = document.querySelector(`#board .board-tokens board-token[color="${color}"] .board-token`);
        const count = Number(bankToken?.textContent?.trim() || 0);
        token.querySelector(".bank-count").textContent = `银行：${count}`;
        token.classList.toggle("unavailable", count <= 0);
        if(count <= 0) token.onclick = null;
    });

    const putBackTokenSelectionSelector = "#putback-extra-token-modal .board-token";
    setupSelection(putBackTokenSelectionSelector,  "selected-token");

    const putBackConfirmBtn = document.querySelector("#putback-extra-token-modal #putback-token-confirm-btn");
    document.querySelector("#putback-extra-token-modal #putback-token-back-btn").onclick = () => {
        backButton();
    };

    putBackConfirmBtn.onclick = () => {
        putBackConfirmBtn.disabled = true;

        const selectedExtraToken = document.querySelector(`${extraTokenSelectionSelector}.selected-token`);
        const takeToken = selectedExtraToken ? selectedExtraToken.getAttribute("color") : null;
        if(selectedExtraToken?.classList.contains("unavailable")) {
            showError("银行中已没有这种颜色的宝石。");
            putBackConfirmBtn.disabled = false;
            return;
        }

        const selectedPutBackToken = document.querySelector(`${putBackTokenSelectionSelector}.selected-token`);
        const putBackToken = selectedPutBackToken ? selectedPutBackToken.getAttribute("color") : null;

        submitExtraToken(takeToken, putBackToken)
            .finally(() => putBackConfirmBtn.disabled = false);
    };
};
