import { test, expect } from "@playwright/test";

test("Clicking start goes to login page", async ({ page }) => {
    await page.goto("/");

    await page.getByRole("button", { name: "登录" }).click();

    await expect(page).toHaveURL("/login/");
});
