import { expect, test } from "@playwright/test";

test("available rooms display their configured turn time", async ({ page }) => {
    await page.addInitScript(() => {
        localStorage.setItem("username", "lxh");
        localStorage.setItem("accessToken", "test-token");
    });

    await page.route("**/oauth/username**", route => route.fulfill({ body: "lxh" }));
    await page.route("**/api/users/lxh**", route => route.fulfill({
        contentType: "application/json",
        body: JSON.stringify({ name: "lxh", preferredColour: "00DD44", role: "ROLE_PLAYER" })
    }));
    await page.route("**/api/sessions?hash=**", route => route.fulfill({
        contentType: "application/json",
        body: JSON.stringify({
            sessions: {
                "room-120": {
                    creator: "lxh",
                    players: ["lxh", "xyj"],
                    launched: false,
                    savegameid: "",
                    turnTimeSeconds: 120,
                    gameParameters: {
                        name: "splendor_BASE",
                        displayName: "璀璨宝石：经典版",
                        maxSessionPlayers: 4
                    }
                }
            }
        })
    }));

    await page.goto("/lobby/");

    await expect(page.getByRole("columnheader", { name: "回合限时" })).toBeVisible();
    await expect(page.locator('tr[session-id="room-120"] .session-turn-time')).toHaveText("每回合 120 秒");
});
