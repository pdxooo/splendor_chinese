import { test, expect, Locator, type Page } from "@playwright/test";
import { mockGetUsername } from "./util/ls-mock.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";

test.describe.parallel("Test default game modals", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        const game = createBasicGame();
        mockGameState(page, game);
        mockGetUsername(page, MAIN_USER);
        await page.goto("/gameboard/?sessionId=123");
    });

    test("Verify take token modal appears if it's your turn", async ({ page }) => {
        await expect(page.locator("#your-turn-modal")).toBeVisible();
    });

    test("the turn action modal can collapse and expand without closing", async ({ page }) => {
        const modal = page.locator("#your-turn-modal");
        const toggle = page.getByRole("button", { name: "收起操作框" });
        await expect(toggle).toHaveText("−");

        await toggle.click();
        await expect(modal).toHaveClass(/collapsed/);
        await expect(page.getByRole("heading", { name: "轮到你了" })).toBeVisible();
        await expect(page.locator("#get-tokens-btn")).toBeHidden();
        await expect(page.getByRole("button", { name: "展开操作框" })).toHaveText("+");

        const timerBox = await page.locator("#turn-timer").boundingBox();
        const collapsedBox = await modal.locator(".modal-container").boundingBox();
        expect(timerBox).not.toBeNull();
        expect(collapsedBox).not.toBeNull();
        expect(collapsedBox!.y).toBeGreaterThanOrEqual(timerBox!.y + timerBox!.height + 8);

        await page.getByRole("button", { name: "展开操作框" }).click();
        await expect(modal).not.toHaveClass(/collapsed/);
        await expect(page.locator("#get-tokens-btn")).toBeVisible();
    });

    test("Verify take token modal does not appear if it's not your turn", async ({ page }) => {
        mockGetUsername(page, "user3"); // change our username
        await page.reload();
        await page.waitForLoadState();

        await expect(page.locator("#your-turn-modal")).toBeHidden();
    });

});

