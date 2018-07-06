package com.igorternyuk.tanks.graphics.animations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author igor
 */
public class Animation {

    private double currentFrameTime;
    private int currentFrame = 0;
    private double speed;
    private int frameCount;
    private BufferedImage image;
    private List<Rectangle> frames;
    private List<Rectangle> flippedFrames;
    private AnimationFacing facing = AnimationFacing.RIGHT;
    private boolean playing = false;
    private AnimationPlayMode playMode = AnimationPlayMode.LOOP;
    private boolean playedOnce = false;

    public Animation(BufferedImage image, double speed,
            int x, int y, int width, int height,
            int frameCount, int frameStep) {
        this.image = image;
        this.speed = speed;
        this.frameCount = frameCount;
        this.frames = new ArrayList<>(this.frameCount);
        this.flippedFrames = new ArrayList<>(this.frameCount);

        for (int i = 0; i < frameCount; ++i) {
            Rectangle rect = new Rectangle(x + i * frameStep, y, width, height);
            this.frames.add(rect);
            Rectangle flippedRect = new Rectangle(x + i * frameStep + width, y,
                    -width, height);
            this.flippedFrames.add(flippedRect);
        }
    }

    public Animation(BufferedImage image, double speed, List<Rectangle> frames) {
        this.image = image;
        this.speed = speed;
        this.frameCount = frames.size();
        this.frames = new ArrayList<>(this.frameCount);
        this.flippedFrames = new ArrayList<>(this.frameCount);
        this.frames.addAll(frames);
        frames.stream().map((frame) -> new Rectangle(frame.x + frame.width,
                frame.y, -frame.width, frame.height)).forEachOrdered((
                flippedFrame) -> {
            this.flippedFrames.add(flippedFrame);
        });
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public AnimationPlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(AnimationPlayMode playMode) {
        this.playMode = playMode;
    }

    public boolean hasBeenPlayedOnce() {
        return this.playedOnce;
    }

    public AnimationFacing getFacing() {
        return this.facing;
    }

    public void setFacing(AnimationFacing facing) {
        this.facing = facing;
    }

    public Rectangle getCurrentRect() {
        return this.facing == AnimationFacing.LEFT ?
                 this.flippedFrames.get(this.currentFrame) :
                 this.frames.get(this.currentFrame);
    }

    public int getCurrentFrameWidth() {
        return Math.abs(getCurrentRect().width);
    }

    public int getCurrentFrameHeight() {
        return Math.abs(getCurrentRect().height);
    }

    public void start() {
        this.playing = true;
        this.currentFrame = 0;
        this.currentFrameTime = 0;
        if (this.playMode.equals(AnimationPlayMode.ONCE)) {
            this.playedOnce = false;
        }
    }

    public void start(AnimationPlayMode playMode) {
        this.playing = true;
        this.currentFrame = 0;
        this.currentFrameTime = 0;
        this.playMode = playMode;
        if (this.playMode.equals(AnimationPlayMode.ONCE)) {
            this.playedOnce = false;
        }
    }

    public void stop() {
        this.playing = false;
        this.currentFrame = 0;
        this.currentFrameTime = 0;
    }

    public void update(double frameTime) {
        if (!this.playing) {
            return;
        }
        this.currentFrameTime += frameTime;
        if (this.currentFrameTime >= speed) {
            ++this.currentFrame;
            if (this.currentFrame >= this.frames.size()) {
                this.currentFrame = 0;
                if (this.playMode.equals(AnimationPlayMode.ONCE)) {
                    this.playing = false;
                    this.playedOnce = true;
                }
            }
            this.currentFrameTime = 0;
        }
    }

    public void draw(Graphics2D g, int destX, int destY, double scaleX,
            double scaleY) {
        Rectangle currentRect = getCurrentRect();
        int dx1 = (int) (destX * scaleX);
        int dy1 = (int) (destY * scaleY);
        int dx2 = dx1 + (int) (Math.abs(currentRect.width) * scaleX);
        int dy2 = dy1 + (int) (Math.abs(currentRect.height) * scaleY);
        g.drawImage(this.image, dx1, dy1, dx2, dy2,
                 currentRect.x, currentRect.y,
                 currentRect.x + currentRect.width,
                 currentRect.y + currentRect.height,
                 null);
    }
}
