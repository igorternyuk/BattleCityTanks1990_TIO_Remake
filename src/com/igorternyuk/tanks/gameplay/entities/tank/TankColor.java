package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Point;

/**
 *
 * @author igor
 */
public enum TankColor {
    YELLOW(new Point(0,0)) {
        @Override
        public TankColor next() {
            return TankColor.GRAY;
        }
    },
    GRAY(new Point(8 * Game.TILE_SIZE,0)) {
        @Override
        public TankColor next() {
            return TankColor.GREEN;
        }
    },
    GREEN(new Point(0, 8 * Game.TILE_SIZE)) {
        @Override
        public TankColor next() {
            return TankColor.RED;
        }
    },
    RED(new Point(8 * Game.TILE_SIZE, 8 * Game.TILE_SIZE)) {
        @Override
        public TankColor next() {
            return TankColor.YELLOW;
        }
    };
    
    private Point offsetFromTankSpriteSheetTopLeftCorner;

    private TankColor(Point offsetFromTankSpriteSheetTopLeftCorner) {
        this.offsetFromTankSpriteSheetTopLeftCorner =
                offsetFromTankSpriteSheetTopLeftCorner;
    }

    public Point getOffsetFromTankSpriteSheetTopLeftCorner() {
        return this.offsetFromTankSpriteSheetTopLeftCorner;
    }
    
    public abstract TankColor next();
}
