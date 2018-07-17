package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum PlayerTankType {
    BASIC(0, 64, 96, 25, 0 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return false;
        }

        @Override
        public PlayerTankType next() {
            return PlayerTankType.LIGHT;
        }
    },
    LIGHT(1, 80, 144, 25, 1 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return false;
        }

        @Override
        public PlayerTankType next() {
            return PlayerTankType.MIDDLE;
        }
    },
    MIDDLE(2, 80, 144, 25, 2 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return true;
        }

        @Override
        public PlayerTankType next() {
            return PlayerTankType.ARMORED;
        }
    },
    ARMORED(3, 48, 96, 100, 3 * Game.TILE_SIZE) {
        @Override
        public boolean canRepeatFire() {
            return false;
        }

        @Override
        public PlayerTankType next() {
            return PlayerTankType.LIGHT;
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
    public abstract PlayerTankType next();

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
