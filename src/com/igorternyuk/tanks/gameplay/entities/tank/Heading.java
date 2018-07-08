package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;

/**
 *
 * @author igor
 */
public enum Heading {
    NORTH(0),
    WEST(2 * Game.TILE_SIZE),
    SOUTH(4 * Game.TILE_SIZE),
    EAST(6 * Game.TILE_SIZE);
    
    private int animationPosX;

    private Heading(int positionX) {
        this.animationPosX = positionX;
    }

    public int getSpriteSheetPositionX() {
        return this.animationPosX;
    }
    
}
