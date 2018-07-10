package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;

/**
 *
 * @author igor
 */
public enum Heading {
    NORTH(0) {
        @Override
        public Direction getDirection() {
            return Direction.NORTH;
        }
    },
    WEST(2 * Game.TILE_SIZE) {
        @Override
        public Direction getDirection() {
            return Direction.WEST;
        }
    },
    SOUTH(4 * Game.TILE_SIZE) {
        @Override
        public Direction getDirection() {
            return Direction.SOUTH;
        }
    },
    EAST(6 * Game.TILE_SIZE) {
        @Override
        public Direction getDirection() {
            return Direction.EAST;
        }
    };
    
    private int animationPosX;

    private Heading(int positionX) {
        this.animationPosX = positionX;
    }

    public int getSpriteSheetPositionX() {
        return this.animationPosX;
    }
    
    public abstract Direction getDirection();
    public static Heading getHeading(Direction direction){
        switch (direction) {
            case NORTH:
                return Heading.NORTH;
            case SOUTH:
                return Heading.SOUTH;
            case EAST:
                return Heading.EAST;
            case WEST:
                return Heading.WEST;
            default:
                break;
        }
        return Heading.NORTH;
    }
    
}
