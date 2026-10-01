package com.example.cardgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private String id;
    private String name;
    private int cost;
    private int attack;
    private int hp;
    private boolean canAttack = false; // 出したターンは攻撃不可（突進等がない場合）
}