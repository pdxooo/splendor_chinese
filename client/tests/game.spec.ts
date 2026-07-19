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

    test("turn-order labels appear directly below player nicknames", async ({ page }) => {
        const identities = [
            page.locator(".player-identity"),
            page.locator(".other-player-profile").first()
        ];

        for (const [index, identity] of identities.entries()) {
            const nickname = identity.locator(".player-name");
            const order = identity.locator(".turn-order-label");
            await expect(nickname).toBeVisible();
            await expect(order).toHaveText(`第 ${index + 1} 位`);

            const nicknameBox = await nickname.boundingBox();
            const orderBox = await order.boundingBox();
            expect(nicknameBox).not.toBeNull();
            expect(orderBox).not.toBeNull();
            expect(orderBox!.y).toBeGreaterThanOrEqual(nicknameBox!.y + nicknameBox!.height - 1);
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

    test("other players show public reservations face-up and blind reservations face-down", async ({ page }) => {
        const game = createBasicGame();
        const otherPlayer = game.players.find(player => player.name !== MAIN_USER)!;
        otherPlayer.reservedCards.push(new DevCard("d1_0", TokenType.Red));
        otherPlayer.reservedCards.push({ cardTier: "TIER_2" } as DevCard);
        await mockGameState(page, game);
        await page.reload();

        const reservedContainer = page.locator(".other-player").first()
            .locator(".other-inventory-cards-reserved");
        await expect(reservedContainer.locator(".other-inventory-card-reserved")).toHaveCount(2);
        await expect(reservedContainer.locator('[card-id="d1_0"] img'))
            .toHaveAttribute("src", /development-cards\/d1_0\.jpg$/);
        await expect(reservedContainer.locator('[face-down="true"] img'))
            .toHaveAttribute("src", /YellowCard\.jpg$/);
    });
});

