import { SETTINGS } from "./settings.js";
import { transition } from "./titleScreen.js";
import { startToastLoad, updateToastLoad } from "./notify.js";

var inputs = document.querySelectorAll(".login-form input, .login-form button");
var enableInputs = () => inputs.forEach(e => e.disabled = false);
var disableInputs = () => inputs.forEach(e => e.disabled = true);

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector("#login-form");
    const registerForm = document.querySelector("#register-form");

    document.querySelector(".show-register-button").addEventListener("click", () => {
        loginForm.classList.add("hidden");
        registerForm.classList.remove("hidden");
    });
    document.querySelector(".show-login-button").addEventListener("click", () => {
        registerForm.classList.add("hidden");
        loginForm.classList.remove("hidden");
    });

    loginForm.addEventListener("submit", (event) => {
        disableInputs();

        const username = document.querySelector(".txt-field input[name='uname']").value;
        const password = document.querySelector(".txt-field input[name='psw']").value;
        const params = {
            "grant_type": "password",
            "username": username,
            "password": password
        };
        const headers = new Headers();
        headers.set("Authorization", `Basic ${btoa("bgp-client-name:bgp-client-pw")}`);

        const url = new URL(`${SETTINGS.getLS_API()}/oauth/token`);
        url.search = new URLSearchParams(params).toString();

        const notify = startToastLoad("正在登录……");

        fetch(url, {
            method: "POST",
            headers: headers
        })
            .catch((reason) => {
                // catch exceptions here

                // console.log(reason);
                // show user error message
                updateToastLoad(notify, reason, "error", 2000);
            })
            .then((resp) => !resp ? null : resp.json())
            .then((data) => {
                // console.log(data);
                if(!data) {
                    updateToastLoad(notify, "无法连接大厅服务。", "error", 2000);
                    return null;
                }

                if(!("access_token" in data)) {
                    updateToastLoad(notify, data.error_description, "error", 2000);
                    return null;
                }

                SETTINGS.setAccessToken(data.access_token);
                SETTINGS.setRefreshToken(data.refresh_token);

                return SETTINGS.fetchUsername();
            }).then((username) => {
                if(!username) {
                    return;
                }
                updateToastLoad(notify, "登录成功！", "success");
                SETTINGS.setUsername(username);

                transition("/lobby/");
            }).finally(() => enableInputs());
        event.preventDefault();
    });

    registerForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        disableInputs();
        const username = registerForm.querySelector("input[name='reg-uname']").value.trim();
        const password = registerForm.querySelector("input[name='reg-psw']").value;
        const inviteCode = registerForm.querySelector("input[name='invite-code']").value;
        const colour = registerForm.querySelector("input[name='preferred-colour']")
            .value.substring(1).toUpperCase();
        const notify = startToastLoad("正在注册……");

        try {
            const response = await fetch(`${SETTINGS.getLS_API()}/api/registration`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: username,
                    password: password,
                    preferredColour: colour,
                    inviteCode: inviteCode
                })
            });
            const message = await response.text();
            if(!response.ok) {
                throw new Error(message || "注册失败。");
            }

            loginForm.querySelector("input[name='uname']").value = username;
            loginForm.querySelector("input[name='psw']").value = password;
            registerForm.reset();
            registerForm.classList.add("hidden");
            loginForm.classList.remove("hidden");
            updateToastLoad(notify, message || "注册成功，请登录。", "success");
        } catch(error) {
            updateToastLoad(notify, error.message || "注册失败。", "error", 4000);
        } finally {
            enableInputs();
        }
    });
});
