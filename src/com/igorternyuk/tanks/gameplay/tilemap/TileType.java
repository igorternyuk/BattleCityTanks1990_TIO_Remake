package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum TileType {
    EMPTY(0, new Rectangle(3 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE)) {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isPassable() {
            return true;
        }
    },
    BRICKS(1, new Rectangle(0 * Game.HALF_TILE_SIZE, 0 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE)) {
        @Override
        public boolean isDestroyable() {
            return true;
        }

        @Override
        public boolean isPassable() {
            return false;
        }
    },
    METAL(2, new Rectangle(0 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE)) {
        @Override
        public boolean isDestroyable() {
            return true;
        }

        @Override
        public boolean isPassable() {
            return false;
        }
    },
    WATER(3, new Rectangle(0 * Game.HALF_TILE_SIZE, 2 * Game.HALF_TILE_SIZE,
            3 * Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE)) {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isPassable() {
            return false;
        }
    },
    BUSH(4, new Rectangle(1 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE)) {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isPassable() {
            return true;
        }
    },
    ICE(5, new Rectangle(2 * Game.HALF_TILE_SIZE, 1 * Game.HALF_TILE_SIZE,
            Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE)) {
        @Override
        public boolean isDestroyable() {
            return false;
        }

        @Override
        public boolean isPassable() {
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

    public abstract boolean isPassable();

    private int number;
    private Rectangle spriteSheet;

    private TileType(int number, Rectangle spriteSheet) {
        this.number = number;
        this.spriteSheet = spriteSheet;
    }

    public int getNumber() {
        return this.number;
    }

    public Rectangle getSpriteSheet() {
        return this.spriteSheet;
    }
}
