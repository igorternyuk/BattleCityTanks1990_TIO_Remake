package com.igorternyuk.tanks.gameplay.entities.explosion;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Explosion extends AnimatedEntity<ExplosionType> {

    private ExplosionType explosionType;

    public Explosion(LevelState level, ExplosionType explosionType, double x,
            double y) {
        super(level, EntityType.EXPLOSION, x, y, 0, Direction.NORTH);
        this.explosionType = explosionType;
        loadAnimations();
        this.animationManager.setCurrentAnimation(explosionType);
        this.animationManager.getCurrentAnimation().start(AnimationPlayMode.ONCE);
    }

    @Override
    public final void loadAnimations() {
        System.out.println("Loading explosion animation...");
        BufferedImage spriteSheet;
        if (this.explosionType == ExplosionType.PROJECTILE) {
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.PROJECTILE_EXPLOSION);
        } else if (this.explosionType == ExplosionType.TANK) {
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.TANK_EXPLOSION);
        } else {
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.EMPTY);
        }
        for (ExplosionType animType : ExplosionType.values()) {
            this.animationManager.addAnimation(animType, new Animation(
                    spriteSheet, animType.getAnimationSpeed(), animType.
                    getFrames()));
        }
    }
    
    @Override
    public void update(KeyboardState keyboardState, double frameTime){
        super.update(keyboardState, frameTime);
        if(this.animationManager.getCurrentAnimation().hasBeenPlayedOnce()){
            destroy();
        }
    }

}
