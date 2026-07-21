import { test, expect, Locator, type Page } from "@playwright/test";
import { Actions, DevCard, OrientDevCard, TokenType } from "./util/game.js";
import { mockGetUsername } from "./util/ls-mock.js";
import { checkActionRequest, verifyModalCloses } from "./util/modal-util.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";

test.describe.parallel("Test trading posts modals", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        mockGetUsername(page, MAIN_USER);
        const game = createBasicGame();
        mockGameState(page, game);
        await page.goto("/gameboard-tradingposts/?sessionId=123");
    });

    test("Verify take extra token modal works", async ({ page }) => {
        const game = createBasicGame();
        game
            .resetValidActions()
            .curValidActions.push(Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);
        mockGameState(page, game);

        // verify that the modal appears properly
        const takeModal = page.locator("#take-extra-token-modal");
        const takeGreenToken = takeModal.locator(".green-token");
        await expect(takeModal).toBeVisible();
        takeGreenToken.click(); // select token to take
        await expect(takeGreenToken).toHaveClass(/selected/);

        // verify that next modal appears properly
        await takeModal.getByText("CONFIRM").click();
        await expect(takeModal).toBeHidden();

        const putModal = page.locator("#putback-extra-token-modal");
        const putBrownToken = putModal.locator(".brown-token");
        await expect(putModal).toBeVisible();
        putBrownToken.click(); // select token to put back
        await expect(putBrownToken).toHaveClass(/selected/);

        // verify that action request is sent
        const requestPromise = checkActionRequest(page, Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);

        await putModal.getByText("CONFIRM").click();
        const request = await requestPromise;
        const jsonData = request.postDataJSON();

        expect(jsonData.takeToken).toEqual(TokenType.Green.toString());
        expect(jsonData.putBackToken).toEqual(TokenType.Brown.toString());

        await verifyModalCloses(page, game, putModal);
    });

    test("one gold pays two of one colour after the trading-post power unlocks", async ({ page }) => {
        const game = createBasicGame();
        const player = game.getPlayer(MAIN_USER) as any;
        player.tokens[TokenType.Gold] = 1;
        player.goldTokenWorthTwoTokens = { unlocked: true };
        const card = game.tier1Deck.getOne();
        card.tokenCost[TokenType.Red] = 2;
        mockGameState(page, game);
        await page.reload();

        await page.locator("#purchase-btn").click();
        const selectable = page.locator(`#buy-card-modal .board-card-dev[card-id="${card.id}"]`);
        await expect(selectable).toHaveClass(/purchasable/);
        await selectable.click();
        await page.locator("#buy-card-confirm-btn").click();

        const goldPayment = page.locator(
            '#dev-card-payment-modal board-token-counter[color="gold"] board-token .board-token span');
        await expect(goldPayment).toHaveText("1");
    });

});


