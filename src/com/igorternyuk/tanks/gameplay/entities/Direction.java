package com.igorternyuk.tanks.gameplay.entities;

import java.awt.Point;

/**
 *
 * @author igor
 */
public enum Direction {
    NORTH(new Point(0, -1)) {
        @Override
        public Direction getOpposite() {
            return Direction.SOUTH;
        }
    },
    WEST(new Point(-1, 0)) {
        @Override
        public Direction getOpposite() {
            return Direction.EAST;
        }
    },
    SOUTH(new Point(0, +1)) {
        @Override
        public Direction getOpposite() {
            return Direction.NORTH;
        }
    },
    EAST(new Point(+1, 0)) {
        @Override
        public Direction getOpposite() {
            return Direction.WEST;
        }
    };
    
    private Point vector;

    private Direction(Point vector) {
        this.vector = vector;
    }

    public Point getVector() {
        return vector;
    }
    
    public int getVx(){
        return this.vector.x;
    }
    
    public int getVy(){
        return this.vector.y;
    }
    
    public abstract Direction getOpposite();
}
