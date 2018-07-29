package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author igor
 */
public enum TankColor {
    YELLOW(new Point(0,0), new Color(0xE79C21)) {
        @Override
        public TankColor next() {
            return TankColor.GRAY;
        }
    },
    GRAY(new Point(8 * Game.TILE_SIZE,0), new Color(0xADADAD)) {
        @Override
        public TankColor next() {
            return TankColor.GREEN;
        }
    },
    GREEN(new Point(0, 8 * Game.TILE_SIZE), new Color(0x008C31)) {
        @Override
        public TankColor next() {
            return TankColor.RED;
        }
    },
    RED(new Point(8 * Game.TILE_SIZE, 8 * Game.TILE_SIZE), new Color(0xB53121)) {
        @Override
        public TankColor next() {
            return TankColor.YELLOW;
        }
    };
    
    private Point offsetFromTankSpriteSheetTopLeftCorner;
    private Color color;
    
    private TankColor(Point offsetFromTankSpriteSheetTopLeftCorner, Color color) {
        this.offsetFromTankSpriteSheetTopLeftCorner =
                offsetFromTankSpriteSheetTopLeftCorner;
        this.color = color;
        
    }

    public Point getOffsetFromTankSpriteSheetTopLeftCorner() {
        return this.offsetFromTankSpriteSheetTopLeftCorner;
    }
    
    public abstract TankColor next();
    public Color getColor(){
        return this.color;
    }
}
