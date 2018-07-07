package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Point;

/**
 *
 * @author igor
 */
public enum Alliance {
    PLAYER(new Point(0, 0)),
    ENEMY(new Point(0, 2 * Game.TILE_SIZE));

    private Point offsetFromSameColorTankSpriteSheetTopLeftCorner;

    private Alliance(Point localPositionOnTheSpriteSheet) {
        this.offsetFromSameColorTankSpriteSheetTopLeftCorner =
                localPositionOnTheSpriteSheet;
    }

    public Point getOffsetFromSameColorTankSpriteSheetTopLeftCorner() {
        return this.offsetFromSameColorTankSpriteSheetTopLeftCorner;
    }
}
