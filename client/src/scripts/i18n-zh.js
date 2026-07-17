/**
 * Simplified Chinese UI translation layer.
 *
 * The original project mixes visible copy into Astro templates and dynamic
 * JavaScript.  Keeping translations here avoids changing API values and game
 * enums, while the observer also translates content created after page load.
 */
const translations = new Map(Object.entries({
    "Splendor": "璀璨宝石",
    "Splendor Classic": "璀璨宝石：经典版",
    "Splendor | Login": "璀璨宝石 | 登录",
    "Splendor | Lobby": "璀璨宝石 | 游戏大厅",
    "Splendor | Settings": "璀璨宝石 | 设置",
    "Splendor | Admin Zone": "璀璨宝石 | 管理员专区",
    "Splendor Cities": "璀璨宝石：城市扩展",
    "Splendor Trading Posts": "璀璨宝石：贸易站",
    "Splendor Orient": "璀璨宝石：东方扩展",
    "Username": "用户名",
    "Password": "密码",
    "Login": "登录",
    "Sessions": "游戏房间",
    "Load a Game": "载入游戏",
    "Available Sessions": "可加入的房间",
    "Create Session": "创建房间",
    "Game Version": "游戏版本",
    "Creator": "创建者",
    "Players": "玩家",
    "Saved Games": "已保存的游戏",
    "Name": "名称",
    "Timestamp": "保存时间",
    "Delete": "删除",
    "Leave": "离开",
    "Join": "加入",
    "Launch": "开始",
    "Play": "进入游戏",
    "Spectate": "观战",
    "Load": "载入",
    "My Saved Game": "我的存档",
    "Admin Zone": "管理员专区",
    "Settings": "设置",
    "Logout": "退出登录",
    "Account Settings": "账号设置",
    "Property": "项目",
    "Current Value": "当前值",
    "Actions": "操作",
    "Name:": "名称：",
    "Password:": "密码：",
    "Role:": "角色：",
    "Preferred Colour:": "偏好颜色：",
    "Danger Zone:": "危险操作：",
    "Old password": "旧密码",
    "New password": "新密码",
    "Update Password": "更新密码",
    "Update Colour": "更新颜色",
    "Delete Account": "删除账号",
    "Add User": "添加用户",
    "Type": "类型",
    "Colour": "颜色",
    "Player": "玩家",
    "Admin": "管理员",
    "Service": "服务账号",
    "Only latin alphabet characters.": "只能使用英文字母。",
    "8-32 characters long, with at least one uppercase, lowercase, digit and special character.": "长度为 8–32 位，且至少包含一个大写字母、小写字母、数字和特殊字符。",
    "Select the user's type.": "选择用户类型。",
    "Preferred in-game colour.": "选择游戏中的偏好颜色。",
    "Registered Users": "已注册用户",
    "Registered Game Services": "已注册游戏服务",
    "Display Name": "显示名称",
    "Location": "地址",
    "Player Min-Max": "玩家人数范围",
    "Update": "更新",
    "Force unregister": "强制注销",
    "Password (Salted, Peppered, Hashed)": "密码（已加盐并哈希）",
    "History": "游戏记录",
    "RESUME": "继续游戏",
    "SAVE GAME": "保存游戏",
    "RESTART": "重新开始",
    "QUIT": "退出游戏",
    "TAKE TOKEN": "拿取宝石",
    "Please select the tokens you wish to take from the bank.": "请选择要从银行拿取的宝石。",
    "Bank Tokens": "银行宝石",
    "Select Tokens": "已选宝石",
    "BACK": "返回",
    "CONFIRM": "确认",
    "PURCHASE": "购买卡牌",
    "Please select the card you wish to purchase.": "请选择要购买的卡牌。",
    "CONFIRM PURCHASE": "确认购买",
    "Select the number of tokens you wish to purchase the card with.": "请选择购买该卡牌所使用的宝石数量。",
    "RESERVE": "预留卡牌",
    "Please select the card you wish to reserve.": "请选择要预留的卡牌。",
    "RESERVE NOBLE": "预留贵族",
    "Please select the noble you wish to reserve.": "请选择要预留的贵族。",
    "TAKE EXTRA TOKEN": "额外拿取宝石",
    "Please select an extra token if desired.": "如有需要，请选择一枚额外宝石。",
    "PUT BACK TOKEN": "归还宝石",
    "Please put back a token if desired.": "如有需要，请归还一枚宝石。",
    "Please select the tokens you wish to put back into the bank.": "请选择要归还到银行的宝石。",
    "YOUR TURN": "轮到你了",
    "Choose an action.": "请选择一个操作。",
    "GET TOKENS": "拿取宝石",
    "CASCADE": "连锁奖励",
    "Please select a card.": "请选择一张卡牌。",
    "CHOOSE CITY": "选择城市",
    "You have qualified for more than one city. Please choose one.": "你同时满足多个城市的条件，请选择一个。",
    "CHOOSE NOBLE": "选择贵族",
    "You have been visited by more than one noble! Please choose one.": "有多位贵族来访，请选择一位。",
    "CHOOSE SATCHEL BONUS": "选择钱袋奖励",
    "You've purchased a satchel card. Please select the bonus you wish to pair with it.": "你购买了钱袋卡，请选择与其搭配的奖励颜色。",
    "SAVE GAME": "保存游戏",
    "Please name your game file": "请为游戏存档命名",
    "RESTART": "重新开始",
    "Are you sure you want to restart?": "确定要重新开始吗？",
    "NO": "否",
    "YES": "是",
    "GAME OVER": "游戏结束",
    "VICTORY": "胜利",
    "NAME": "玩家名称",
    "Red": "红色",
    "Blue": "蓝色",
    "Green": "绿色",
    "White": "白色",
    "Brown": "黑色",
    "Gold": "黄金",
    "PLAYER": "玩家",
    "ADMIN": "管理员",
    "SERVICE": "服务账号"
}));

const dynamicTranslations = [
    [/^Please select a card from level (\d+)\.$/, "请选择一张 $1 级卡牌。"],
    [/^(.+) wins the game!$/, "$1 赢得了游戏！"],
    [/^(.+) tied as winners of the game!$/, "$1 并列获胜！"],
    [/^The game has ended\.$/, "游戏已结束。"],
    [/^🎉 (.+) are the winners! 🎉$/, "🎉 $1 获胜！🎉"],
    [/^🎉 (.+) is the winner! 🎉$/, "🎉 $1 获胜！🎉"],
    [/^(.+)'s turn$/, "轮到 $1"],
    [/^Welcome, (.+)!$/, "欢迎，$1！"],
    [/^Hello, (.+)!$/, "你好，$1！"]
];

function translateValue(value) {
    const trimmed = value.trim();
    if (!trimmed) return value;
    let translated = translations.get(trimmed);
    if (!translated) {
        for (const [pattern, replacement] of dynamicTranslations) {
            if (pattern.test(trimmed)) {
                translated = trimmed.replace(pattern, replacement);
                break;
            }
        }
    }
    if (!translated) return value;
    const leading = value.match(/^\s*/)?.[0] ?? "";
    const trailing = value.match(/\s*$/)?.[0] ?? "";
    return `${leading}${translated}${trailing}`;
}

function translateElement(root) {
    if (root.nodeType === Node.TEXT_NODE) {
        const currentValue = root.nodeValue ?? "";
        const translatedValue = translateValue(currentValue);
        // Avoid emitting a new characterData mutation when no translation is
        // needed. Reassigning the same value can otherwise keep the observer
        // busy and make buttons appear unresponsive.
        if (translatedValue !== currentValue) root.nodeValue = translatedValue;
        return;
    }
    if (!(root instanceof Element) && root !== document) return;

    const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
    let node;
    while ((node = walker.nextNode())) {
        const parent = node.parentElement;
        if (parent?.matches("script, style, code, pre")) continue;
        const currentValue = node.nodeValue ?? "";
        const translatedValue = translateValue(currentValue);
        if (translatedValue !== currentValue) node.nodeValue = translatedValue;
    }

    const elements = root instanceof Element ? [root, ...root.querySelectorAll("[placeholder], [title], [aria-label]")] : root.querySelectorAll("[placeholder], [title], [aria-label]");
    for (const element of elements) {
        for (const attribute of ["placeholder", "title", "aria-label"]) {
            if (element.hasAttribute(attribute)) {
                const currentValue = element.getAttribute(attribute) ?? "";
                const translatedValue = translateValue(currentValue);
                if (translatedValue !== currentValue) element.setAttribute(attribute, translatedValue);
            }
        }
    }
}

translateElement(document);
new MutationObserver((mutations) => {
    for (const mutation of mutations) {
        if (mutation.type === "characterData") translateElement(mutation.target);
        for (const node of mutation.addedNodes) translateElement(node);
    }
}).observe(document.body, { childList: true, subtree: true, characterData: true });
