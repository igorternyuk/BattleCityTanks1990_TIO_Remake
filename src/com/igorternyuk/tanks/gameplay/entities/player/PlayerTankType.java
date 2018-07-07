package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum PlayerTankType {
    REGULAR(0, 8, 16, 25, 0 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return false;
        }
    },
    LIGHT(1, 8, 32, 25, 1 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return false;
        }
    },
    MIDDLE(2, 8, 32, 25, 2 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return true;
        }
    },
    HEAVY(3, 6, 16, 100, 3 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return false;
        }
    };

    private int starNumber;
    private double speed;
    private double projectileSpeed;
    private int projectileDamage;
    private int spriteSheetPositionY;

    private PlayerTankType(int starNumber, double speed, double projectileSpeed,
            int projectileDamage, int spriteSheetPositionY) {
        this.starNumber = starNumber;
        this.speed = speed;
        this.projectileSpeed = projectileSpeed;
        this.projectileDamage = projectileDamage;
        this.spriteSheetPositionY = spriteSheetPositionY;
    }

    public abstract boolean canRepeatFire();

    public int getStarNumber() {
        return this.starNumber;
    }

    public double getProjectileSpeed() {
        return this.projectileSpeed;
    }

    public int getProjectileDamage() {
        return this.projectileDamage;
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getSpriteSheetPositionY() {
        return this.spriteSheetPositionY;
    }
}
