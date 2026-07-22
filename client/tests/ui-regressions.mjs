import assert from "node:assert/strict";
import { readFileSync } from "node:fs";

const read = path => readFileSync(new URL(`../src/${path}`, import.meta.url), "utf8");

const history = read("scripts/history.ts");
const game = read("scripts/game.js");
const strongholds = read("scripts/modals/strongholds.js");
const strongholdCss = read("styles/strongholds.css");
const strongholdModal = read("components/modals/StrongholdActionModal.astro");

assert.match(history, /historyContainer\.addEventListener\("mouseover"/,
    "游戏记录应使用事件委托，使通过 innerHTML 加入的卡牌仍能悬停放大");
assert.match(history, /closest\("\.history-card-thumbnail"\)/,
    "悬停处理必须识别游戏记录中的卡牌缩略图");

assert.match(strongholdModal, /class="stronghold-action-footer"/,
    "要塞确认区必须固定在弹窗底部可见区域");
assert.match(strongholdModal, /class="stronghold-confirm-btn"[^>]*disabled/,
    "尚未选中目标时确认按钮必须明确禁用");
assert.match(strongholds, /confirmButton\.disabled = !targetCardId/,
    "选中卡牌后必须启用确认按钮");
assert.match(strongholds, /已选择目标/,
    "选择完成后必须显示醒目的中文确认提示");

assert.match(strongholdCss, /grid-template-columns:\s*repeat\(4,/,
    "要塞选择页应固定为每行四张卡牌");
assert.match(game, /createStrongholdIcon/,
    "要塞应使用项目内 SVG 图标，而不是文字棋子符号");
assert.doesNotMatch(game, /"♜"\.repeat/,
    "不得继续使用遮挡卡面的文字棋子符号");
assert.match(strongholdCss, /\.stronghold-markers[\s\S]*top:\s*50%/,
    "卡牌上的要塞必须位于右侧中部，避开右上奖励和左下费用");

console.log("要塞与游戏记录界面回归检查通过");
