package com.example.cardgame.model;

import lombok.Data;

@Data
public class GameRoom {
    private String roomId;
    private Player player1;
    private Player player2;
    private String currentTurnPlayerId;
    private boolean started = false;

    // 初期カードの配布などのセッティング
    public void startGame() {
        this.started = true;
        this.currentTurnPlayerId = player1.getId();

        // テスト用初期手札の付与
        player1.getHand().add(new Card("c1", "ゴブリン", 1, 1, 2, false));
        player1.getHand().add(new Card("c2", "ナイト", 2, 2, 2, false));
        
        player2.getHand().add(new Card("c3", "ゴブリン", 1, 1, 2, false));
        player2.getHand().add(new Card("c4", "オーク", 3, 3, 4, false));
    }

    // ターン終了処理
    public void endTurn() {
        if (currentTurnPlayerId.equals(player1.getId())) {
            currentTurnPlayerId = player2.getId();
            player2.setMaxPp(Math.min(10, player2.getMaxPp() + 1));
            player2.setCurrentPp(player2.getMaxPp());
            // 自分の場のカードを攻撃可能状態にする
            player2.getBoard().forEach(c -> c.setCanAttack(true));
        } else {
            currentTurnPlayerId = player1.getId();
            player1.setMaxPp(Math.min(10, player1.getMaxPp() + 1));
            player1.setCurrentPp(player1.getMaxPp());
            player1.getBoard().forEach(c -> c.setCanAttack(true));
        }
    }
}