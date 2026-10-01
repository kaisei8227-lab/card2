package com.example.cardgame.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Player {
    private String id;
    private int hp = 20;
    private int maxPp = 1;
    private int currentPp = 1;
    private List<Card> hand = new ArrayList<>();
    private List<Card> board = new ArrayList<>();

    public Player(String id) {
        this.id = id;
    }
}