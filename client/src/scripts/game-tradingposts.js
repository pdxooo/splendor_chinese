import { SETTINGS } from "./settings.js";
import { addUpdater } from "./game.js";
import { registerPlayerUpdater, writeBasicUpdate } from "./history";

const coatOfArmsImages = [
    "RedCoatOfArms.png",
    "BlackCoatOfArms.png",
    "YellowCoatOfArms.png",
    "BlueCoatOfArms.png"
];

// now update the coa on the powers
const allPowers = [
    ["ExtraTokenAfterPurchasePower", "extraTokenAfterPurchase"],
    ["ExtraTokenAfterTakingSameColorTokensPower", "extraTokenAfterTakingSameColor"],
    ["GoldTokenWorthTwoTokensPower", "goldTokenWorthTwoTokens"],
    ["AddFivePrestigePointsPower", "addFivePrestigePoints"],
    ["AddPrestigePointsWithCoatOfArmsPower", "addPrestigePointsWithCoatsOfArms"],
];

const renderPowerRequirements = (player, powersContainer) => {
    allPowers.forEach(([enumName, varName]) => {
        const powerData = player?.[varName];
        const power = powersContainer.querySelector(`.power[power='${enumName}']`);
        if(!power || !powerData?.requirements) return;

        let requirements = power.querySelector(".power-requirements");
        if(!requirements) {
            requirements = document.createElement("div");
            requirements.className = "power-requirements";
            power.appendChild(requirements);
        }
        requirements.replaceChildren(...Object.entries(powerData.requirements)
            .filter(([color]) => !["Gold", "Satchel"].includes(color))
            .map(([color, amount]) => {
                const chip = document.createElement("span");
                chip.className = `power-requirement-chip ${color.toLowerCase()}`;
                chip.textContent = String(amount);
                chip.title = `${amount} 张${color}奖励卡`;
                return chip;
            }));
        if(varName === "addFivePrestigePoints") {
            const noble = document.createElement("span");
            noble.className = "power-requirement-noble";
            noble.textContent = "+ 领主";
            requirements.appendChild(noble);
        }
    });
};

const updatePowers = (data) => {

    const curUsername = SETTINGS.getUsername();

    const currentPlayer = data.players.find(player => player.name === curUsername);
    const goldValue = currentPlayer?.goldTokenWorthTwoTokens?.unlocked ? 2 : 1;
    document.querySelector("#player-inventory")?.setAttribute("data-gold-token-value", String(goldValue));
    const powersContainer = document.querySelector(".powers-container");
    if(currentPlayer && powersContainer) renderPowerRequirements(currentPlayer, powersContainer);

    data.players.forEach((p, index) => {
        // update the coat of arms images for each player
        let coaElm = null;
        if(p.name === curUsername) {
            coaElm = document.querySelector("#player-inventory .player-coat-of-arms > img");
        } else {
            coaElm = document.querySelector(`#other-players .other-player[pname="${p.name}"] .op-coa-image-container > img`);
        }

        // only update if needed
        if(!coaElm.hasAttribute("src")) {
            coaElm.setAttribute("src", `/images/trading-posts/${coatOfArmsImages[index]}`);
        }

        // loop through each power and check if unlocked
        allPowers.forEach((v) => {
            const [enumName, varName] = v;

            // retrive the relevant power
            const power = powersContainer.querySelector(`.power[power='${enumName}']`);
            const coaIndicator = power.querySelector(`img[src="/images/trading-posts/${coatOfArmsImages[index]}"]`);
            if(p[varName] && p[varName].unlocked) {
                // check if we've already enabled showing the activated power for the player
                if(!coaIndicator.classList.contains("show")) {
                    coaIndicator.classList.add("show")
                }
            } else if(coaIndicator.classList.contains("show")) {
                coaIndicator.classList.remove("show");
            }
        });

    });


};

addUpdater(updatePowers);

// history updates
const updatePlayerTPHistory = (oldState, newState) => {
    let newPowersCount = 0;
    allPowers.forEach((v) => {
        const [enumName, varName] = v;

        if(!oldState[varName] || !newState[varName]) {
            return;
        }

        if(oldState[varName].unlocked !== newState[varName].unlocked) {
            newPowersCount++;
        }
    });

    if(newPowersCount > 0) {
        writeBasicUpdate(`Unlocked ${newPowersCount} new power${newPowersCount > 1 ? "s" : ""}`);
    }
};
registerPlayerUpdater(updatePlayerTPHistory);
