package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class WaterTile extends AnimatedTile<WaterAnimationType> {
    
    protected WaterTile(Point position, BufferedImage image, double scale) {
        super(TileType.WATER, position, image, scale);
        loadAnimations();
        this.animationManager.setCurrentAnimation(WaterAnimationType.REGULAR);
        this.animationManager.getCurrentAnimation().
                start(AnimationPlayMode.LOOP);
    }
    
    @Override
    public boolean checkIfCollision(Entity entity){
        if(entity.getEntityType() == EntityType.PROJECTILE){
            return false;
        }
        return super.checkIfCollision(entity);
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
