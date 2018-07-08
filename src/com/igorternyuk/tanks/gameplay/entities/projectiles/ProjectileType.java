package com.igorternyuk.tanks.gameplay.entities.projectiles;

import com.igorternyuk.tanks.gameplay.entities.Direction;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum ProjectileType {
    PLAYER,
    ENEMY;
    
    public static Rectangle getSourceRect(Direction direction){
        Rectangle rect = new Rectangle();
        if(direction != null){
            switch (direction) {
                case NORTH:
                    return new Rectangle(0, 0, 3, 4);
                case WEST:
                    return new Rectangle(7, 0, 4, 3);
                case SOUTH:
                    return new Rectangle(16, 0, 3, 4);
                case EAST:
                    return new Rectangle(23, 0, 4, 3);
            }
        }
        return rect;
    }
}
