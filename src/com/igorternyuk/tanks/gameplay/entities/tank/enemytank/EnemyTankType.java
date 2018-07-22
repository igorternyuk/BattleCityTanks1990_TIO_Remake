package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum EnemyTankType {
    BASIC(16, 32, 25, 25, 100, 0 * Game.TILE_SIZE),
    FAST(32, 64, 25, 25, 200, 1 * Game.TILE_SIZE),
    POWER(16, 96, 25, 25, 300, 2 * Game.TILE_SIZE),
    ARMORED(16, 64, 50, 100, 400, 3 * Game.TILE_SIZE);

    private double speed;
    private double projectileSpeed;
    private int projectileDamage;
    private int health;
    private int score;
    private int spriteSheetPositionY;

    private EnemyTankType(double speed, double projectileSpeed,
            int projectileDamage, int health, int score, int positionY) {
        this.speed = speed;
        this.projectileSpeed = projectileSpeed;
        this.projectileDamage = projectileDamage;
        this.health = health;
        this.score = score;
        this.spriteSheetPositionY = positionY;
    }
    
    public double getSpeed() {
        return this.speed;
    }

    public double getProjectileSpeed() {
        return this.projectileSpeed;
    }
    
    public int getProjectileDamage(){
        return this.projectileDamage;
    }

    public int getHealth() {
        return this.health;
    }

    public int getScore() {
        return this.score;
    }

    public int getSpriteSheetPositionY() {
        return this.spriteSheetPositionY;
    }
}
