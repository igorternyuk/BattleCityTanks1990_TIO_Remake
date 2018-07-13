package com.igorternyuk.tanks.gameplay.entities.eagle;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum EagleState {
    ALIVE(new Rectangle(0, 0, Game.TILE_SIZE, Game.TILE_SIZE)),
    DEAD(new Rectangle(Game.TILE_SIZE, 0, Game.TILE_SIZE, Game.TILE_SIZE));

    private Rectangle sourceRect;

    private EagleState(Rectangle sourceRect) {
        this.sourceRect = sourceRect;
    }

    public Rectangle getSourceRect() {
        return sourceRect;
    }
}
