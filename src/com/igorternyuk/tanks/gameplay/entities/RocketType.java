package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum RocketType {
    PLAYER,
    ENEMY;

    public static Rectangle getSourceRect(Direction direction) {
        Rectangle rect = new Rectangle();
        if (direction != null) {
            return new Rectangle(direction.ordinal() * Game.TILE_SIZE, 0,
                    Game.TILE_SIZE, Game.TILE_SIZE);
        }
        return rect;
    }
}
