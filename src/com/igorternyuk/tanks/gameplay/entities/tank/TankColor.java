package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Point;

/**
 *
 * @author igor
 */
public enum TankColor {
    YELLOW(new Point(0,0)),
    GRAY(new Point(8 * Game.TILE_SIZE,0)),
    GREEN(new Point(0, 8 * Game.TILE_SIZE)),
    RED(new Point(8 * Game.TILE_SIZE, 8 * Game.TILE_SIZE));
    
    private Point offsetFromTankSpriteSheetTopLeftCorner;

    private TankColor(Point offsetFromTankSpriteSheetTopLeftCorner) {
        this.offsetFromTankSpriteSheetTopLeftCorner =
                offsetFromTankSpriteSheetTopLeftCorner;
    }

    public Point getOffsetFromTankSpriteSheetTopLeftCorner() {
        return offsetFromTankSpriteSheetTopLeftCorner;
    }
}
