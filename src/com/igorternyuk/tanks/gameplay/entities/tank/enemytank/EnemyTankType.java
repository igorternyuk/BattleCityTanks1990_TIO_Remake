package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum EnemyTankType {
    REGULAR(4, 128, 100, 0 * Game.TILE_SIZE),
    ARMORED_TROOP_CARRIER(8, 128, 200, 1 * Game.TILE_SIZE),
    RAPID_FIRING(4, 256, 300, 2 * Game.TILE_SIZE),
    HEAVY(4, 128, 400, 3 * Game.TILE_SIZE);

    private double speed;
    private double projectileSpeed;
    private int score;
    private int spriteSheetPositionY;

    private EnemyTankType(double speed, double projectileSpeed, int score,
            int positionY) {
        this.speed = speed;
        this.projectileSpeed = projectileSpeed;
        this.score = score;
        this.spriteSheetPositionY = positionY;
    }
    
    public double getSpeed() {
        return this.speed;
    }

    public double getProjectileSpeed() {
        return this.projectileSpeed;
    }

    public int getScore() {
        return this.score;
    }

    public int getSpriteSheetPositionY() {
        return this.spriteSheetPositionY;
    }
}
