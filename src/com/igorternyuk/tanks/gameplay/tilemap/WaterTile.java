package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class WaterTile extends AnimatedTile<WaterAnimationType> {
    
    public WaterTile(TileType type, BufferedImage image, double scale) {
        super(type, image, scale);
        loadAnimations();
        this.animationManager.setCurrentAnimation(WaterAnimationType.REGULAR);
        this.animationManager.getCurrentAnimation().
                start(AnimationPlayMode.LOOP);
    }
    
    @Override
    public final void loadAnimations() {
        for (WaterAnimationType animationType : WaterAnimationType.values()) {
            this.animationManager.addAnimation(animationType, new Animation(
                    this.image, animationType.getSpeed(), animationType.
                    getFrames()));
        }
    }
}
