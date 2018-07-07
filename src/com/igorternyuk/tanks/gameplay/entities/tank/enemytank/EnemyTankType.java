package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum EnemyTankType {
    REGULAR(4, 16, 100, 0 * Game.TILE_SIZE),
    ARMORED_TROOP_CARRIER(8, 16, 200, 1 * Game.TILE_SIZE),
    RAPID_FIRING(4, 32, 300, 2 * Game.TILE_SIZE),
    HEAVY(4, 16, 400, 3 * Game.TILE_SIZE);

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
