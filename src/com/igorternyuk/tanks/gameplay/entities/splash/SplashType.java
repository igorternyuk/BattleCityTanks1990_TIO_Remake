package com.igorternyuk.tanks.gameplay.entities.splash;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public enum SplashType {
    BONUS(0.2, new Rectangle(0, 0, Game.TILE_SIZE, Game.TILE_SIZE),
            Game.TILE_SIZE, 6),
    NEW_ENEMY_TANK(0.2, new Rectangle(0, 0, Game.TILE_SIZE, Game.TILE_SIZE),
            Game.TILE_SIZE, 6);

    private List<Rectangle> frames = new ArrayList<>();
    private double animationSpeed;

    private SplashType(double speed, Rectangle firstFrame, int frameStep,
            int frameCount) {
        this.animationSpeed = speed;
        for (int i = 0; i < frameCount; ++i) {
            Rectangle nextFrame = (Rectangle) firstFrame.clone();
            nextFrame.x = firstFrame.x + i * frameStep;
            this.frames.add(nextFrame);
        }
    }

    private void SplashType(double speed, List<Rectangle> frames) {
        this.frames.addAll(frames);
    }

    public List<Rectangle> getFrames() {
        return this.frames;
    }

    public double getAnimationSpeed() {
        return this.animationSpeed;
    }
}
