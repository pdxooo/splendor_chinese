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
    "8-32 characters long, with at least one uppercase, lowercase, digit and special character.": "密码可自由设置，只要不为空即可。",
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
    "Available to return": "可返还的宝石",
    "Select Tokens": "已选宝石",
    "BACK": "返回",
    "CONFIRM": "确认",
    "PURCHASE": "购买卡牌",
    "Please select the card you wish to purchase.": "请选择要购买的卡牌。",
    "CONFIRM PURCHASE": "确认购买",
    "Select the number of tokens you wish to purchase the card with.": "请选择购买该卡牌所使用的宝石数量。",
    "RESERVE": "预留卡牌",
    "Please select the card you wish to reserve.": "请选择要预留的卡牌。",
    "Or reserve the top card of a deck:": "或者从一个牌堆顶部盲抽预留：",
    "Click a card back on the left to reserve blindly from that level.": "点击每行左侧对应的牌背，即可从该等级牌堆盲抽预留。",
    "Level 1 deck": "一级牌堆",
    "Level 2 deck": "二级牌堆",
    "Level 3 deck": "三级牌堆",
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
    "SERVICE": "服务账号",
    "It is not this players turn.": "现在不是你的回合。",
    "The tokens chosen are invalid.": "所选宝石不符合规则，请重新选择。",
    "You cannot take three or more tokens of the same colour.": "不能拿取三个或更多同色宝石。",
    "You may take two tokens of one colour only when at least four remain.": "只有该颜色宝石至少剩余 4 枚时，才能拿取 2 枚。",
    "You must take three tokens of different colours when three colours are available.": "有至少三种颜色可选时，必须拿取三枚不同颜色的宝石。",
    "Gold tokens cannot be taken directly; reserve a card to receive one.": "黄金不能直接拿取；预留卡牌时才能获得黄金。",
    "You cannot combine two tokens of one colour with tokens of another colour.": "拿取两枚同色宝石时，不能再拿其他颜色。",
    "You already have the maximum amount of reserved cards!": "你已经预留了 3 张卡牌，不能继续预留。",
    "There are not enough tokens on the board for you to take.": "银行中的对应宝石数量不足。",
    "You must return enough tokens to keep no more than 10.": "请返还足够的宝石，使持有总数不超过 10 枚。",
    "You do not have enough tokens in your inventory.": "你没有足够的宝石可以返还或支付。",
    "You do not have enough tokens to purchase this card.": "你的宝石和黄金不足，无法购买这张卡牌。",
    "Invalid token type chosen for assigning satchel card bonus.": "选择的钱袋奖励颜色无效。",
    "You have not selected a card to purchase!": "请先选择一张要购买的卡牌。",
    "You have not selected a card to reserve!": "请先选择一张要预留的卡牌或一个牌堆。",
    "You have not selected a noble!": "请先选择一位贵族。",
    "You have not selected a city!": "请先选择一座城市。",
    "You have not selected a card!": "请先选择一张卡牌。"
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
    [/^Hello, (.+)!$/, "你好，$1！"],
    [/^You must return exactly (\d+) token\(s\) to keep no more than 10\.$/, "你必须恰好返还 $1 枚宝石，使总数不超过 10 枚。"]
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
