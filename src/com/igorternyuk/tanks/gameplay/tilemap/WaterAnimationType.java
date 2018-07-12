package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public enum WaterAnimationType {
    REGULAR(0.2, 0, 0, Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE, 3,
            Game.HALF_TILE_SIZE);

    private List<Rectangle> frames = new ArrayList<>();
    private double speed;

    private WaterAnimationType(double speed, int x, int y, int width, int height,
            int frameCount, int frameStep) {
        this.speed = speed;
        
        for(int i = 0; i < frameCount; ++i){
            this.frames.add(new Rectangle(x + i * frameStep, y, width, height));                   
        }
    }

    public List<Rectangle> getFrames() {
        return this.frames;
    }

    public double getSpeed() {
        return this.speed;
    }
}
