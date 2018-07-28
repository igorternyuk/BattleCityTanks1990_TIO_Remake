package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum TileType {
    EMPTY(0, new Rectangle(3 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE), "Empty") {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isTraversable(Entity entity) {
            return true;
        }
    },
    BRICK(1, new Rectangle(0 * Game.HALF_TILE_SIZE, 0 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE), "Bricks") {
        @Override
        public boolean isDestroyable() {
            return true;
        }

        @Override
        public boolean isTraversable(Entity entity) {
            return false;
        }
    },
    METAL(2, new Rectangle(0 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE), "Metal") {
        @Override
        public boolean isDestroyable() {
            return true;
        }

        @Override
        public boolean isTraversable(Entity entity) {
            return false;
        }
    },
    WATER(3, new Rectangle(0 * Game.HALF_TILE_SIZE, 2 * Game.HALF_TILE_SIZE,
            3 * Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE), "Water") {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isTraversable(Entity entity) {
            if(entity.getEntityType() == EntityType.ENEMY_TANK
                    || entity.getEntityType() == EntityType.PLAYER_TANK){
                Tank tank = (Tank)entity;
                return tank.isCanTraverseWater();
            }
            return false;
        }
    },
    BUSH(4, new Rectangle(1 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE), "Bushes") {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isTraversable(Entity entity) {
            return true;
        }
    },
    ICE(5, new Rectangle(2 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE), "Ice") {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isTraversable(Entity entity) {
            return true;
        }
    };

    public static TileType getFromNumber(int number) {
        if (number < 0 || number > TileType.values().length) {
            number = 0;
        }
        return TileType.values()[number];
    }

    public abstract boolean isDestroyable();

    public abstract boolean isTraversable(Entity entity);

    private int number;
    private Rectangle boundingRect;
    private String description;

    private TileType(int number, Rectangle boundingRect, String description) {
        this.number = number;
        this.boundingRect = boundingRect;
        this.description = description;
    }

    public int getNumber() {
        return this.number;
    }

    public Rectangle getBoundingRect() {
        return this.boundingRect;
    }
    
    public String getDescription(){
        return this.description;
    }
}
