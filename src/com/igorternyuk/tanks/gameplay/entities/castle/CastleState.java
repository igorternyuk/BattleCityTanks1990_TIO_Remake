package com.igorternyuk.tanks.gameplay.entities.castle;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum CastleState {
    ALIVE(new Rectangle(0, 0, Game.TILE_SIZE, Game.TILE_SIZE)),
    DEAD(new Rectangle(Game.TILE_SIZE, 0, Game.TILE_SIZE, Game.TILE_SIZE));

    private Rectangle sourceRect;

    private CastleState(Rectangle sourceRect) {
        this.sourceRect = sourceRect;
    }

    public Rectangle getSourceRect() {
        return sourceRect;
    }
}
