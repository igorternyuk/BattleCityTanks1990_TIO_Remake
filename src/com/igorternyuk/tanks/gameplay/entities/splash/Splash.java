package com.igorternyuk.tanks.gameplay.entities.splash;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Splash extends AnimatedEntity<SplashType>{
    private SplashType splashType;
    public Splash(LevelState level, SplashType splashType, double x, double y) {
        super(level, EntityType.SPLASH, x, y, 0, Direction.NORTH);
        this.splashType = splashType;
        loadAnimations();
        this.animationManager.setCurrentAnimation(SplashType.BONUS);
        this.animationManager.getCurrentAnimation().start(AnimationPlayMode.LOOP);
    }
    
    public SplashType getSplashType(){
        return this.splashType;
    }

    @Override
    public final void loadAnimations() {
        System.out.println("Loading splash animation...");
        BufferedImage spriteSheet;
        if (this.splashType == SplashType.BONUS
             || this.splashType == SplashType.NEW_ENEMY_TANK) {
            spriteSheet = this.level.getSpriteSheetManager().get(
                    SpriteSheetIdentifier.SPLASH);
        } else {
            spriteSheet = this.level.getSpriteSheetManager().get(
                    SpriteSheetIdentifier.EMPTY);
        }
        for (SplashType animType : SplashType.values()) {
            System.out.println("animation");
            this.animationManager.addAnimation(animType, new Animation(
                    spriteSheet, animType.getAnimationSpeed(), animType.
                    getFrames()));
        }
    }
}
