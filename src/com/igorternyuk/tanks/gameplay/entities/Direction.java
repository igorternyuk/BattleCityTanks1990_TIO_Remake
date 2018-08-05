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

        @Override
        public boolean isVertical() {
            return true;
        }

        @Override
        public boolean isHorizontal() {
            return false;
        }
        
        @Override
        public Direction next() {
            return Direction.EAST;
        }
        
        @Override
        public Direction prev() {
            return Direction.WEST;
        }
    },
    WEST(new Point(-1, 0)) {
        @Override
        public Direction getOpposite() {
            return Direction.EAST;
        }

        @Override
        public boolean isVertical() {
            return false;
        }

        @Override
        public boolean isHorizontal() {
            return true;
        }
        
        @Override
        public Direction next() {
            return Direction.NORTH;
        }
        
        @Override
        public Direction prev() {
            return Direction.SOUTH;
        }
    },
    SOUTH(new Point(0, +1)) {
        @Override
        public Direction getOpposite() {
            return Direction.NORTH;
        }

        @Override
        public boolean isVertical() {
            return true;
        }

        @Override
        public boolean isHorizontal() {
            return false;
        }
        
        @Override
        public Direction next() {
            return Direction.WEST;
        }
        
        @Override
        public Direction prev() {
            return Direction.EAST;
        }
    },
    EAST(new Point(+1, 0)) {
        @Override
        public Direction getOpposite() {
            return Direction.WEST;
        }

        @Override
        public boolean isVertical() {
            return false;
        }

        @Override
        public boolean isHorizontal() {
            return true;
        }
        
        @Override
        public Direction next() {
            return Direction.SOUTH;
        }
        
        @Override
        public Direction prev() {
            return Direction.NORTH;
        }
    };
    
    private Point vector;

    private Direction(Point vector) {
        this.vector = vector;
    }

    public Point getVector() {
        return vector;
    }
    
    public int getDx(){
        return this.vector.x;
    }
    
    public int getDy(){
        return this.vector.y;
    }
    
    public abstract Direction next();
    public abstract Direction prev();
    public abstract boolean isVertical();
    public abstract boolean isHorizontal();
    public abstract Direction getOpposite();
}
