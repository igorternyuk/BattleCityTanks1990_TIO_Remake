package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;

/**
 *
 * @author igor
 */
public enum EnemyTankType {
    BASIC(24, 96, 20, 25, 100, 0 * Game.TILE_SIZE),
    FAST(48, 144, 20, 25, 200, 1 * Game.TILE_SIZE),
    POWER(32, 144, 20, 25, 300, 2 * Game.TILE_SIZE),
    ARMORED(32, 200, 50, 100, 400, 3 * Game.TILE_SIZE);

    private double speed;
    private double projectileSpeed;
    private int projectileDamage;
    private int health;
    private int score;
    private int spriteSheetPositionY;
    private ProjectileType projectileType;

    private EnemyTankType(double speed, double projectileSpeed,
            int projectileDamage, int health, int score, int positionY) {
        this.speed = speed;
        this.projectileSpeed = projectileSpeed;
        this.projectileDamage = projectileDamage;
        this.health = health;
        this.score = score;
        this.spriteSheetPositionY = positionY;
        this.projectileType = ProjectileType.ENEMY;
    }
    
    public EnemyTankType next(){
        int nextOrdinal = ordinal() + 1;
        if(nextOrdinal > EnemyTankType.values().length - 1){
            nextOrdinal = 0;
        }
        return EnemyTankType.values()[nextOrdinal];
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

    public ProjectileType getProjectileType() {
        return this.projectileType;
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
