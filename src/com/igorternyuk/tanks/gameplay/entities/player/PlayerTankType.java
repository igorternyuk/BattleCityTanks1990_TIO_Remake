package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;

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
    ARMORED(3, 40, 200, 50, 3 * Game.TILE_SIZE) {
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
    private ProjectileType projectileType;

    private PlayerTankType(int starNumber, double speed, double projectileSpeed,
            int projectileDamage, int spriteSheetPositionY) {
        this.starNumber = starNumber;
        this.speed = speed;
        this.projectileSpeed = projectileSpeed;
        this.projectileDamage = projectileDamage;
        this.spriteSheetPositionY = spriteSheetPositionY;
        this.projectileType = ProjectileType.PLAYER;
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

    public ProjectileType getProjectileType() {
        return this.projectileType;
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getSpriteSheetPositionY() {
        return this.spriteSheetPositionY;
    }
}
