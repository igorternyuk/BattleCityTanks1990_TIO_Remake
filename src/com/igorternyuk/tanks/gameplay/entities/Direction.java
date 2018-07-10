package com.igorternyuk.tanks.gameplay.entities;

import java.awt.Point;

/**
 *
 * @author igor
 */
public enum Direction {
    NORTH(new Point(0, -1)),
    WEST(new Point(-1, 0)),
    SOUTH(new Point(0, +1)),
    EAST(new Point(+1, 0)),
    NULL(new Point(0,0));
    
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
}
