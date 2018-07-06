package com.igorternyuk.tanks.gameplay;

import java.awt.Color;

/**
 *
 * @author igor
 */
public enum GameStatus {
   PLAY("", Color.white),
    PAUSED("Game paused", Color.yellow.darker()),
    PLAYER_WON("You won!!!", Color.green.darker()),
    PLAYER_LOST("You lost!", Color.red.darker());
    
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
