package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Point;

/**
 *
 * @author igor
 */
public enum Alliance {
    PLAYER(new Point(0, 0)) {
        @Override
        public Alliance getOpponent() {
            return Alliance.ENEMY;
        }

        @Override
        public boolean isPlayer() {
            return true;
        }

        @Override
        public boolean isEnemy() {
            return false;
        }
    },
    ENEMY(new Point(0, 4 * Game.TILE_SIZE)) {
        @Override
        public Alliance getOpponent() {
            return Alliance.PLAYER;
        }

        @Override
        public boolean isPlayer() {
            return false;
        }

        @Override
        public boolean isEnemy() {
            return true;
        }
    };

    private Point offsetFromSameColorTankSpriteSheetTopLeftCorner;

    private Alliance(Point localPositionOnTheSpriteSheet) {
        this.offsetFromSameColorTankSpriteSheetTopLeftCorner =
                localPositionOnTheSpriteSheet;
    }
    
    public abstract Alliance getOpponent();
    public abstract boolean isPlayer();
    public abstract boolean isEnemy();

    public Point getOffsetFromSameColorTankSpriteSheetTopLeftCorner() {
        return this.offsetFromSameColorTankSpriteSheetTopLeftCorner;
    }
}
