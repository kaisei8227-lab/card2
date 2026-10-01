let stompClient = null;
let myPlayerId = "player_" + Math.floor(Math.random() * 10000); // 簡易プレイヤーID
let currentGameState = null;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        document.getElementById('connection-status').textContent = "接続成功！";

        // 全員共通のゲーム状態トピックを購読
        stompClient.subscribe('/topic/game', function (message) {
            currentGameState = JSON.parse(message.body);
            renderGame(currentGameState);
        });

        // 部屋に参加申請
        stompClient.send("/app/join", {}, JSON.stringify({ playerId: myPlayerId }));
    });
}

function renderGame(room) {
    if (!room.started) {
        document.getElementById('connection-status').textContent = "対戦相手を待っています...";
        return;
    }

    document.getElementById('game-board').style.display = "block";

    const isPlayer1 = room.player1.id === myPlayerId;
    const me = isPlayer1 ? room.player1 : room.player2;
    const enemy = isPlayer1 ? room.player2 : room.player1;

    // ステータス更新
    document.getElementById('player-hp').textContent = me.hp;
    document.getElementById('player-pp').textContent = me.currentPp + "/" + me.maxPp;
    document.getElementById('enemy-hp').textContent = enemy.hp;

    // 自分の手札の描画
    const handContainer = document.getElementById('player-hand');
    handContainer.innerHTML = "";
    me.hand.forEach(card => {
        const cardDiv = createCardElem(card);
        cardDiv.onclick = () => playCard(card.id);
        handContainer.appendChild(cardDiv);
    });

    // 自分の場の描画
    const playerBoardContainer = document.getElementById('player-board');
    playerBoardContainer.innerHTML = "";
    me.board.forEach(card => {
        const cardDiv = createCardElem(card);
        if (card.canAttack && room.currentTurnPlayerId === myPlayerId) {
            cardDiv.classList.add("can-attack");
            cardDiv.onclick = () => attackPlayer(card.id);
        }
        playerBoardContainer.appendChild(cardDiv);
    });

    // 相手の場の描画
    const enemyBoardContainer = document.getElementById('enemy-board');
    enemyBoardContainer.innerHTML = "";
    enemy.board.forEach(card => {
        const cardDiv = createCardElem(card);
        enemyBoardContainer.appendChild(cardDiv);
    });
}

function createCardElem(card) {
    const div = document.createElement('div');
    div.className = 'card';
    div.innerHTML = `
        <div><b>${card.name}</b></div>
        <div>コスト: ${card.cost}</div>
        <div>攻撃: ${card.attack} / HP: ${card.hp}</div>
    `;
    return div;
}

function playCard(cardId) {
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'PLAY_CARD',
        playerId: myPlayerId,
        cardId: cardId
    }));
}

function attackPlayer(attackerId) {
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'ATTACK_PLAYER',
        playerId: myPlayerId,
        attackerId: attackerId
    }));
}

function endTurn() {
    stompClient.send("/app/action", {}, JSON.stringify({
        type: 'END_TURN',
        playerId: myPlayerId
    }));
}