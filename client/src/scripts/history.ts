
type Card = {
    id: string;
    newTokenType?: string;
};

const historyCardPreview = document.createElement("img");
historyCardPreview.classList.add("history-card-preview");
historyCardPreview.alt = "卡牌放大预览";
historyCardPreview.hidden = true;
document.body.appendChild(historyCardPreview);

const hideHistoryCardPreview = (): void => {
    historyCardPreview.hidden = true;
};

const showHistoryCardPreview = (thumbnail: HTMLImageElement): void => {
    historyCardPreview.src = thumbnail.src;
    historyCardPreview.hidden = false;

    const thumbnailRect = thumbnail.getBoundingClientRect();
    const previewWidth = Math.min(224, window.innerWidth * 0.22);
    const previewHeight = previewWidth * 1.4;
    const gap = 12;
    let left = thumbnailRect.right + gap;
    if (left + previewWidth > window.innerWidth - gap) {
        left = thumbnailRect.left - previewWidth - gap;
    }
    const top = Math.max(gap, Math.min(
        thumbnailRect.top - previewHeight / 3,
        window.innerHeight - previewHeight - gap,
    ));

    historyCardPreview.style.left = `${Math.max(gap, left)}px`;
    historyCardPreview.style.top = `${top}px`;
};

const enableHistoryCardPreview = (thumbnail: HTMLImageElement): void => {
    thumbnail.classList.add("history-card-thumbnail");
    thumbnail.addEventListener("mouseenter", () => showHistoryCardPreview(thumbnail));
    thumbnail.addEventListener("mouseleave", hideHistoryCardPreview);
};

/**
 * Returns the difference of the two cards list.
 * @param oldCards 
 * @param newCards 
 * @returns added cards, deleted cards, changed cards
 */
export const cardsDiff = (oldCards: any[], newCards: any[]): [Card[], Card[], Card[]] => {
    const added: Card[] = [];
    const removed: Card[] = [];
    const changed: Card[] = [];

    const oldCardsMap: Map<string, any> = new Map<string, any>();
    const newCardsMap: Map<string, any> = new Map<string, any>();
    oldCards.forEach(c => oldCardsMap.set(c.id, c));
    newCards.forEach(c => newCardsMap.set(c.id, c));

    // check if new cards were added
    newCards.forEach(c => {
        const oc = oldCardsMap.get(c.id);
        if(!oc) {
            added.push({ id: c.id });
        } else if(oc.isSatchel && c.isSatchel) {
            // check for changes, could have support for adding custom checks if needed
            if(oc.tokenType !== c.tokenType) {
                changed.push({ id: c.id, newTokenType: c.tokenType });
            }
        }
    });

    // check if cards were removed
    oldCards.forEach(c => {
        const oc = newCardsMap.get(c.id);
        if(!oc) {
            removed.push({ id: c.id });
        }
    });

    return [added, removed, changed];
};

export const writeCardUpdate = (prefix: string, cards: Card[], imagePath: string, suffix: string = null) => {
    if(cards.length === 0) return;

    const tNode = (document.querySelector("#history-card-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;
    tNode.querySelector(".prefix").innerHTML = prefix;
    tNode.querySelector(".suffix").innerHTML = suffix;
    const imgContainer = tNode.querySelector(".content");
    cards.forEach(c => {
        const img = document.createElement("img");
        img.setAttribute("src", `/images/${imagePath}/${c.id}.jpg`);
        img.setAttribute("alt", `卡牌 ${c.id}`);
        enableHistoryCardPreview(img);
        imgContainer.appendChild(img);
    });

    appendHistory(tNode.querySelector(".event").innerHTML, "card-event");
};

const writeSatchelUpdate = (cards: Card[]) => {
    if(cards.length === 0) return;
    const tokenText = document.createElement("div");
    tokenText.textContent = "现在是";
    tokenText.appendChild(createToken(cards[0].newTokenType));

    writeCardUpdate("宝物袋奖励颜色变更：", cards, "development-cards", tokenText.innerHTML);
};

// token amount for showing
const createToken = (color: string, amount: number = null) => {
    const tNode = (document.querySelector("#history-token-template") as HTMLTemplateElement)
                    .content.cloneNode(true) as HTMLDivElement;
    if(amount) {
        tNode.querySelector(".amount").textContent = amount + "";
    }
    const tokenImg = document.createElement("div");
    tokenImg.classList.add(`${color.toLowerCase()}-token`, "board-token");
    tNode.querySelector(".token-change").appendChild(tokenImg);
    return tNode;
};

/**
 * Check if tokens changed between state, write update if yes.
 * @param oldTokens 
 * @param newTokens 
 */
const checkTokens = (oldTokens: any, newTokens: any) => {
    const setupDiv = (startText: string) => {
        const t = document.createElement("div");
        const headerText = document.createElement("div");
        headerText.textContent = startText;
        t.appendChild(headerText);
        return t;
    };

    // initial event content
    const tookContent = setupDiv("拿取宝石：");
    const putBackContent = setupDiv("返还宝石：");

    let taken = [];
    let putback = [];

    // add comma to last
    const addComma = (list: HTMLElement[]) => {
        if(list.length > 0) {
            const comma = document.createElement("div");
            comma.textContent = ",";
            list[list.length - 1].querySelector(".token-change").appendChild(comma);
        }
    };

    for (const [key, value] of Object.entries(oldTokens)) {
        if(newTokens[key] !== value) {
            // check the delta of token count
            const change = (newTokens[key] as number) - (value as number);
            if(change > 0) {
                addComma(taken);
                taken.push(createToken(key, change));
            } else {
                addComma(putback);
                putback.push(createToken(key, change * -1));
            }
        }
    }

    if(taken.length > 0) {
        taken.forEach(e => tookContent.appendChild(e));
        appendHistory(tookContent.innerHTML, "token-event");
    }
    if(putback.length > 0) {
        putback.forEach(e => putBackContent.appendChild(e));
        appendHistory(putBackContent.innerHTML, "token-event");
    }
};

const historyContainer = document.querySelector("#history .drawer");
export const focusLastEvent = () => {
    const lastEventContainer = historyContainer.querySelector(".event-container:last-child");
    if(lastEventContainer) {
        const lastEventContent = lastEventContainer.querySelector(".event:last-child");
        if(lastEventContent) {
            lastEventContent.scrollIntoView({ behavior: "smooth" });
        } else {
            lastEventContainer.scrollIntoView({ behavior: "smooth" });
        }
    }
};

const startEvent = (name: string): void => {
    const tNode = (document.querySelector("#history-event-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;
    tNode.querySelector(".event-name").textContent = `${name} 的回合`;

    historyContainer.appendChild(tNode);
    focusLastEvent();
};

const appendHistory = (htmlText: string, ...classes: string[]): void => {
    const lastEventContainer = historyContainer.querySelector(".event-container:last-child");
    if(!lastEventContainer) {
        console.log("append history called but no event has been made");
        return;
    }

    const newEvent = document.createElement("div");
    newEvent.classList.add("event");
    newEvent.classList.add(...classes);
    newEvent.innerHTML = htmlText;
    lastEventContainer.querySelector(".event-content").appendChild(newEvent);

    focusLastEvent();
};

/**
 * Writes out who's turn it is, only if the current player has changed.
 * @param last 
 * @param current 
 */
const writeCurrentTurn = (last: any, current: any): void => {
    if(current.gameOver) {
        return;
    }

    if(!last || current.players[current.turnCounter].name !== last.players[last.turnCounter].name) {
        const name = current.players[current.turnCounter].name;
        console.log(`${name} 的回合`);
        startEvent(name);
    }
};

const writeGameOver = (data: any): void => {
    const tNode = (document.querySelector("#history-event-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;

    const overNode = (document.querySelector("#history-event-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;
    overNode.querySelector(".event-name").textContent = "本局游戏结束。";

    const winners = data.winners;

    if(winners.length > 1) {
        const names = winners.map(p => p.name);
        const text = names.join("、");
        tNode.querySelector(".event-name").textContent = `🎉 ${text} 并列获胜！🎉`;
    } else {
        // one person won
        tNode.querySelector(".event-name").textContent = `🎉 ${winners[0].name} 获胜！🎉`;
    }

    historyContainer.appendChild(overNode);
    historyContainer.appendChild(tNode);
    focusLastEvent();
};

const playerUpdaters = [];
export const registerPlayerUpdater = (func: (oldState: any, newState: any) => {}): void => {
    playerUpdaters.push(func);
};

const gameUpdaters = [];
export const registerGameUpdater = (func: (oldState: any, newState: any) => {}): void => {
    gameUpdaters.push(func);
};

export const writeUpdate = (last: any, current: any): void => {
    if(current.gameOver && !last) {
        writeGameOver(current);
    }

    if(!last) {
        writeCurrentTurn(last, current);
        return;
    }

    // they should always have the same number of players
    const oldPlayerState: Map<string, any> = new Map<string, any>();
    last.players.forEach(p => oldPlayerState.set(p.name, p));
    current.players.forEach((newState) => {
        const oldState = oldPlayerState.get(newState.name);
        checkTokens(oldState.tokens, newState.tokens);

        // dev cards
        {
            const [newCards, oldCards, changedCards] = cardsDiff(oldState.devCards, newState.devCards);
            writeCardUpdate("获得发展卡：", newCards, "development-cards");
            writeCardUpdate("弃置发展卡：", oldCards, "development-cards");
            writeSatchelUpdate(changedCards);
        }

        // reserved dev
        writeCardUpdate("预留发展卡：", cardsDiff(oldState.reservedCards, newState.reservedCards)[0], "development-cards");

        // nobles
        writeCardUpdate("获得领主牌：", cardsDiff(oldState.nobleCards, newState.nobleCards)[0], "nobles");

        // reserved nobles
        writeCardUpdate("预留领主牌：", cardsDiff(oldState.reservedNobles, newState.reservedNobles)[0], "nobles");

        playerUpdaters.forEach(func => func(oldState, newState));
    });

    gameUpdaters.forEach(func => func(last, current));

    writeCurrentTurn(last, current);
    if(current.gameOver) {
        writeGameOver(current);
    }
};

export const writeBasicUpdate = (text: string): void => {
    const textNode = document.createElement("div");
    textNode.textContent = text;
    appendHistory(textNode.innerHTML, "card-event");
};