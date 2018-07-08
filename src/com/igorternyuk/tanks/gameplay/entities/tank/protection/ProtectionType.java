package com.igorternyuk.tanks.gameplay.entities.tank.protection;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public enum ProtectionType {
    REGULAR(0.3, new Rectangle(0, 0, Game.TILE_SIZE, Game.TILE_SIZE),
            Game.TILE_SIZE, 2);

    private List<Rectangle> frames = new ArrayList<>();
    private double animationSpeed;

    private ProtectionType(double speed, Rectangle firstFrame, int frameStep,
            int frameCount) {
        this.animationSpeed = speed;
        for (int i = 0; i < frameCount; ++i) {
            Rectangle nextFrame = (Rectangle) firstFrame.clone();
            nextFrame.x = firstFrame.x + i * frameStep;
            this.frames.add(nextFrame);
        }

    }

    public List<Rectangle> getFrames() {
        return this.frames;
    }

    public double getAnimationSpeed() {
        return this.animationSpeed;
    }

}
