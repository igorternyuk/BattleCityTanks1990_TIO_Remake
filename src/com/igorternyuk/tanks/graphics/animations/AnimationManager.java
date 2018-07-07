package com.igorternyuk.tanks.graphics.animations;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author igor
 * @param <I> Animation identifier
 */
public class AnimationManager<I> {

    private Map<I, Animation> animations = new HashMap<>();
    private I currentAnimation, prevAnimation;
    private AnimationPlayMode prevAnimPlayMode;

    public void setCurrentAnimation(I identifier) {
        if(identifier == null || !this.animations.containsKey(identifier))
            return;
        Animation currAnim = getCurrentAnimation();
        if (currAnim != null) {
            currAnim.stop();
        }
        this.currentAnimation = identifier;
        this.prevAnimation = identifier;
        this.prevAnimPlayMode = getCurrentAnimation().getPlayMode();
    }
    
    public int getAnimationCount(){
        return this.animations.size();
    }

    public I getCurrentAnimationIdentifier() {
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

    public void addAnimation(I identifier, Animation animation) {
        this.animations.put(identifier, animation);
    }

    public void removeAnimation(I identifier) {
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
        if(currAnim == null)
            return;
        if(currAnim.getPlayMode() == AnimationPlayMode.ONCE
                    && currAnim.hasBeenPlayedOnce())
            return;
        currAnim.draw(g, destX, destY, scaleX, scaleY);            
    }
}
