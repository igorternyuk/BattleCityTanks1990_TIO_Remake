package com.igorternyuk.tanks.gameplay.entities.splash;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Splash extends AnimatedEntity<SplashType>{
    private SplashType splashType;
    BufferedImage spriteSheet = null;
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
        if (this.splashType == SplashType.BONUS
             || this.splashType == SplashType.NEW_ENEMY_TANK) {
            spriteSheet = this.level.getSpriteSheetManager().get(
                    SpriteSheetIdentifier.SPLASH);
        } else {
            System.out.println("Empty tile loaded");
            spriteSheet = this.level.getSpriteSheetManager().get(
                    SpriteSheetIdentifier.EMPTY);
        }
        for (SplashType animType : SplashType.values()) {
            System.out.println("animation");
            this.animationManager.addAnimation(animType, new Animation(
                    spriteSheet, animType.getAnimationSpeed(), animType.
                    getFrames()));
            for(Rectangle r: animType.getFrames()){
                System.out.println("frame = " + r);
            }
        }
    }

    /*@Override
    public void draw(Graphics2D g){
        g.drawImage(spriteSheet, 0, 0, null);
    }*/
}
