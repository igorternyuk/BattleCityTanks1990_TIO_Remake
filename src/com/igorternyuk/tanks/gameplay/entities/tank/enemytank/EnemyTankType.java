package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum EnemyTankType {
    REGULAR(64, 80, 25, 25, 100, 0 * Game.TILE_SIZE),
    ARMORED_TROOP_CARRIER(80, 128, 25, 25, 200, 1 * Game.TILE_SIZE),
    RAPID_FIRING(80, 128, 25, 25, 300, 2 * Game.TILE_SIZE),
    HEAVY(48, 80, 400, 100, 100, 3 * Game.TILE_SIZE);

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
