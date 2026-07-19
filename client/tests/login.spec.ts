import { test, expect } from "@playwright/test";

test("Login goes to lobby", async ({ page }) => {
    await page.goto("/");

    await page.getByRole("button", { name: "登录" }).click();

    await expect(page).toHaveURL("/login/");
});

test("Login page declares UTF-8 and renders Chinese text", async ({ page }) => {
    await page.goto("/login/");

    await expect(page.locator('meta[charset="utf-8"]')).toHaveCount(1);
    await expect(page.getByRole("heading", { name: "璀璨宝石" })).toBeVisible();
    await expect(page.getByRole("button", { name: "使用邀请码注册" })).toBeVisible();
});

test("Invited player can open and submit the registration form", async ({ page }) => {
    let registrationRequest = null;
    await page.route("**/api/registration", async route => {
        registrationRequest = route.request().postDataJSON();
        await route.fulfill({ status: 200, body: "注册成功，请登录。" });
    });

    await page.goto("/login/");
    await page.getByRole("button", { name: "使用邀请码注册" }).click();
    await expect(page.locator("#register-form")).toBeVisible();

    await page.locator("input[name='reg-uname']").fill("newplayer");
    await page.locator("input[name='reg-psw']").fill("123456");
    await page.locator("input[name='invite-code']").fill("TEST-INVITE-CODE");
    await page.getByRole("button", { name: "注册", exact: true }).click();

    await expect(page.locator("#login-form")).toBeVisible();
    await expect(page.locator("input[name='uname']")).toHaveValue("newplayer");
    expect(registrationRequest).toMatchObject({
        name: "newplayer",
        password: "123456",
        inviteCode: "TEST-INVITE-CODE",
        preferredColour: "2684FF"
    });
});
