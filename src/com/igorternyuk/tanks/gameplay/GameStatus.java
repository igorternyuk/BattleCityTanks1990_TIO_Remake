package com.igorternyuk.tanks.gameplay;

import java.awt.Color;

/**
 *
 * @author igor
 */
public enum GameStatus {
   PLAY("", Color.white),
    PAUSED("GAME PAUSED", Color.yellow),
    GAME_OVER("GAME OVER!", Color.red);
    
    String description;
    Color color;

    private GameStatus(String description, Color color) {
        this.description = description;
        this.color = color;
    }

    public String getDescription() {
        return this.description;
    }
    
    public Color getColor(){
        return this.color;
    } 
}
