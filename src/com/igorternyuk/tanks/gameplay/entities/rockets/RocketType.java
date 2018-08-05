package com.igorternyuk.tanks.gameplay.entities.rockets;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum RocketType {
    PLAYER {
        @Override
        public int getSpeed() {
            return 160;
        }
    },
    ENEMY {
        @Override
        public int getSpeed() {
            return 160;
        }
    };

    public static Rectangle getSourceRect(Direction direction) {
        Rectangle rect = new Rectangle();
        if (direction != null) {
            return new Rectangle(direction.ordinal() * Game.TILE_SIZE, 0,
                    Game.TILE_SIZE, Game.TILE_SIZE);
        }
        return rect;
    }
    
    public abstract int getSpeed();
        
}
