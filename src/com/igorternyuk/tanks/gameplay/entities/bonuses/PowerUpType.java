package com.igorternyuk.tanks.gameplay.entities.bonuses;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;
import java.util.Random;

/**
 *
 * @author igor
 */
public enum PowerUpType {
    HELMET,
    TIMER,
    SHOVEL,
    STAR,
    GRENADE,
    TANK,
    GUN;
    
    private static final Random random = new Random();
    
    public static PowerUpType randomType(){
        int randNumber = random.nextInt(PowerUpType.values().length);
        return PowerUpType.values()[randNumber];
    }
    
    private Rectangle sourceRect;
    private int score;
    

    private PowerUpType() {
        this.sourceRect = new Rectangle(this.ordinal() * Game.TILE_SIZE, 0,
                Game.TILE_SIZE, Game.TILE_SIZE);
        this.score = 500;
    }

    public Rectangle getSourceRect() {
        return this.sourceRect;
    }
    
    public int getScore(){
        return this.score;
    }
}
