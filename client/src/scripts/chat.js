import { SETTINGS } from "./settings.js";
import { showError } from "./notify.js";

const chat = document.querySelector("#game-chat");
const messages = document.querySelector("#chat-messages");
const form = document.querySelector("#chat-form");
const input = document.querySelector("#chat-input");
const toggle = document.querySelector("#chat-toggle");

const updateToggle = () => {
    const collapsed = chat.classList.contains("collapsed");
    toggle.setAttribute("aria-expanded", String(!collapsed));
    toggle.setAttribute("aria-label", collapsed ? "展开聊天" : "收起聊天");
    toggle.setAttribute("title", collapsed ? "展开聊天" : "收起聊天");
    toggle.querySelector("span").textContent = collapsed ? "+" : "−";
};

toggle?.addEventListener("click", () => {
    chat.classList.toggle("collapsed");
    updateToggle();
});

window.addEventListener("splendor-state-update", (event) => {
    const wasAtBottom = messages.scrollHeight - messages.scrollTop - messages.clientHeight < 20;
    const nodes = (event.detail.chatMessages || []).map((message) => {
        const row = document.createElement("div");
        row.className = "chat-message";
        const sender = document.createElement("strong");
        sender.textContent = `${message.sender}:`;
        const text = document.createElement("span");
        text.textContent = message.text;
        row.append(sender, text);
        return row;
    });
    messages.replaceChildren(...nodes);
    if(wasAtBottom) messages.scrollTop = messages.scrollHeight;
});

form?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const text = input.value.trim();
    if(!text) return;
    try {
        await SETTINGS.verifyCredentials();
        const sessionId = new URL(document.location).searchParams.get("sessionId");
        const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/chat`);
        url.search = new URLSearchParams({access_token: SETTINGS.getAccessToken()}).toString();
        const response = await fetch(url, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({text})
        });
        if(!response.ok) throw new Error(await response.text());
        input.value = "";
    } catch(error) {
        showError(error.toString());
    }
});
