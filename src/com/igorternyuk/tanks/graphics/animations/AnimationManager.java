package com.igorternyuk.tanks.graphics.animations;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author igor
 * @param <Identifier> Animation identifier
 */
public class AnimationManager<Identifier> {

    private Map<Identifier, Animation> animations = new HashMap<>();
    private Identifier currentAnimation, prevAnimation;
    private AnimationPlayMode prevAnimPlayMode;

    public void setCurrentAnimation(Identifier identifier) {
        if (this.animations.containsKey(identifier)) {
            Animation currAnim = getCurrentAnimation();
            if (currAnim != null) {
                currAnim.stop();
            }
            this.currentAnimation = identifier;
            this.prevAnimation = identifier;
            this.prevAnimPlayMode = getCurrentAnimation().getPlayMode();
        }
    }

    public Identifier getCurrentAnimationIdentifier() {
        return this.currentAnimation;
    }

    public void setPreviousAnimation() {
        this.currentAnimation = this.prevAnimation;
        getCurrentAnimation().setPlayMode(this.prevAnimPlayMode);
    }

    public void setCurrentAnimationFacing(AnimationFacing facing) {
        getCurrentAnimation().setFacing(facing);
    }

    public void setAnimationsFacing(AnimationFacing facing) {
        this.animations.entrySet().forEach((a) -> {
            a.getValue().setFacing(facing);
        });
    }

    public Animation getCurrentAnimation() {
        return this.animations.get(this.currentAnimation);
    }

    public void addAnimation(Identifier identifier, Animation animation) {
        this.animations.put(identifier, animation);
    }

    public void removeAnimation(Identifier identifier) {
        this.animations.remove(identifier);
    }

    public void update(double frameTime) {
        Animation currAnim = getCurrentAnimation();
        if (currAnim != null) {
            currAnim.update(frameTime);
        }
    }

    public void draw(Graphics2D g, int destX, int destY, double scaleX,
            double scaleY) {
        Animation currAnim = getCurrentAnimation();
        if (currAnim != null) {
            currAnim.draw(g, destX, destY, scaleX, scaleY);
        }
    }
}
