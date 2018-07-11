package com.igorternyuk.tanks.gameplay.entities.bonuses;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;
import java.util.Random;

/**
 *
 * @author igor
 */
public enum BonusType {
    TANK_PROTECTION,
    CLOCK,
    SHOVEL,
    STAR,
    GRENADE,
    EXTRA_LIFE,
    GUN;
    
    private static final Random random = new Random();
    
    public static BonusType randomType(){
        int randNumber = random.nextInt(BonusType.values().length);
        return BonusType.values()[randNumber];
    }
    
    private Rectangle sourceRect;
    private int score;
    

    private BonusType() {
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
