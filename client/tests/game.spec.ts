import { test, expect } from "@playwright/test";
import { mockGetUsername } from "./util/ls-mock.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";
import { DevCard, TokenType } from "./util/game.js";

test.describe.parallel("Test orient game", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        const game = createBasicGame();
        await mockGameState(page, game);
        mockGetUsername(page, MAIN_USER);
        await page.goto("/gameboard/?sessionId=123");
    });

    test("Verify dev cards & nobles appear", async ({ page }) => {
        // await page.screenshot({ path: 'screenshot.png' });

        const game = createBasicGame();
        const nobleCount = game.players.length + 1;

        await expect(page.locator("#board .board-nobles .noble-card img[src]")).toHaveCount(nobleCount);
        await expect(page.locator("#board .board-cards-dev-selectable .board-card-dev:not(.board-card-dev-orient) img[src]")).toHaveCount(12);
        await expect(page.locator("#board .board-cards-dev-selectable .board-card-dev.board-card-dev-orient img[src]")).toHaveCount(6);
    });

    test("chat uses UTF-8 Chinese text and a top-right collapse control", async ({ page }) => {
        await expect(page.locator('meta[charset="utf-8"]')).toHaveCount(1);
        await expect(page.getByRole("heading", { name: "房间聊天" })).toBeVisible();

        const chat = page.locator("#game-chat");
        const toggle = page.getByRole("button", { name: "收起聊天" });
        const chatBox = await chat.boundingBox();
        const toggleBox = await toggle.boundingBox();
        expect(chatBox).not.toBeNull();
        expect(toggleBox).not.toBeNull();
        expect(toggleBox!.x).toBeGreaterThan(chatBox!.x + chatBox!.width / 2);

        await toggle.click();
        await expect(chat).toHaveClass(/collapsed/);
        await expect(page.locator(".chat-panel")).toBeHidden();
        await expect(page.getByRole("button", { name: "展开聊天" })).toBeVisible();

        await page.getByRole("button", { name: "展开聊天" }).click();
        await expect(page.locator(".chat-panel")).toBeVisible();
    });

    test("turn-order labels stay inside player panels", async ({ page }) => {
        const mainPanel = page.locator(".player-inventory-container");
        const mainBadge = mainPanel.locator(":scope > .turn-order-badge");
        await expect(mainBadge).toHaveText("第 1 位");

        const otherPanel = page.locator(".other-player-container").first();
        const otherBadge = otherPanel.locator(":scope > .turn-order-badge");
        await expect(otherBadge).toHaveText("第 2 位");

        for (const [panel, badge] of [[mainPanel, mainBadge], [otherPanel, otherBadge]]) {
            const panelBox = await panel.boundingBox();
            const badgeBox = await badge.boundingBox();
            expect(panelBox).not.toBeNull();
            expect(badgeBox).not.toBeNull();
            expect(badgeBox!.x).toBeGreaterThanOrEqual(panelBox!.x);
            expect(badgeBox!.y).toBeGreaterThanOrEqual(panelBox!.y);
            expect(badgeBox!.x + badgeBox!.width).toBeLessThanOrEqual(panelBox!.x + panelBox!.width);
        }
    });

    test("the current player's reserved card remains fully visible when enlarged", async ({ page }) => {
        const game = createBasicGame();
        game.getPlayer(MAIN_USER).reservedCards.push(new DevCard("d1_0", TokenType.Red));
        await mockGameState(page, game);
        await page.reload();

        const reservedCard = page.locator(".player-inventory-card-reserved").first();
        await reservedCard.hover();
        const imageBox = await reservedCard.locator("img").boundingBox();
        const viewport = page.viewportSize();
        expect(imageBox).not.toBeNull();
        expect(viewport).not.toBeNull();
        expect(imageBox!.x).toBeGreaterThanOrEqual(8);
        expect(imageBox!.x + imageBox!.width).toBeLessThanOrEqual(viewport!.width - 8);
    });
});

