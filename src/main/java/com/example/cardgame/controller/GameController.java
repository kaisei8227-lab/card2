package com.example.cardgame.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.cardgame.model.Card;
import com.example.cardgame.model.GameRoom;
import com.example.cardgame.model.Player;

@Controller
public class GameController {

    private final GameRoom gameRoom = new GameRoom();

    @MessageMapping("/join")
    @SendTo("/topic/game")
    public GameRoom joinGame(Map<String, String> payload) {
        String playerId = payload.get("playerId");

        if (gameRoom.getPlayer1() == null) {
            gameRoom.setPlayer1(new Player(playerId));
        } else if (gameRoom.getPlayer2() == null && !playerId.equals(gameRoom.getPlayer1().getId())) {
            gameRoom.setPlayer2(new Player(playerId));
            gameRoom.startGame(); // 2名揃ったらゲーム開始
        }
        return gameRoom;
    }

    @MessageMapping("/action")
    @SendTo("/topic/game")
    public GameRoom handleAction(Map<String, Object> action) {
        String type = (String) action.get("type");
        String playerId = (String) action.get("playerId");

        // 手番チェック
        if (!playerId.equals(gameRoom.getCurrentTurnPlayerId())) {
            return gameRoom; // 相手のターンの場合は操作不可
        }

        Player me = playerId.equals(gameRoom.getPlayer1().getId()) ? gameRoom.getPlayer1() : gameRoom.getPlayer2();
        Player enemy = playerId.equals(gameRoom.getPlayer1().getId()) ? gameRoom.getPlayer2() : gameRoom.getPlayer1();

        switch (type) {
            case "PLAY_CARD":
                String cardId = (String) action.get("cardId");
                Card cardToPlay = me.getHand().stream().filter(c -> c.getId().equals(cardId)).findFirst().orElse(null);
                if (cardToPlay != null && me.getCurrentPp() >= cardToPlay.getCost()) {
                    me.setCurrentPp(me.getCurrentPp() - cardToPlay.getCost());
                    me.getHand().remove(cardToPlay);
                    me.getBoard().add(cardToPlay);
                }
                break;

            case "ATTACK_PLAYER":
                String attackerId = (String) action.get("attackerId");
                Card attacker = me.getBoard().stream().filter(c -> c.getId().equals(attackerId)).findFirst().orElse(null);
                if (attacker != null && attacker.isCanAttack()) {
                    enemy.setHp(enemy.getHp() - attacker.getAttack());
                    attacker.setCanAttack(false); // 1ターン1回攻撃
                }
                break;

            case "END_TURN":
                gameRoom.endTurn();
                break;
        }

        return gameRoom;
    }
}
