package com.igorternyuk.tanks.gameplay.entities.splash;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 *
 * @author igor
 */
public class Splash extends AnimatedEntity<SplashType> {

    private static final double SPLASH_DURATION = 2;
    private SplashType splashType;
    private double splashTimer = 0;

    public Splash(LevelState level, SplashType splashType, double x, double y) {
        super(level, EntityType.SPLASH, x, y, 0, Direction.NORTH);
        this.splashType = splashType;
        loadAnimations();
        this.animationManager.setCurrentAnimation(SplashType.BONUS);
        this.animationManager.getCurrentAnimation().
                start(AnimationPlayMode.LOOP);
    }

    public SplashType getSplashType() {
        return this.splashType;
    }

    @Override
    public final void loadAnimations() {
        System.out.println("Loading splash animation...");
        BufferedImage spriteSheet;
        if (this.splashType == SplashType.BONUS
                || this.splashType == SplashType.NEW_ENEMY_TANK) {
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.SPLASH);
        } else {
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.EMPTY);
        }
        for (SplashType animType : SplashType.values()) {
            this.animationManager.addAnimation(animType, new Animation(
                    spriteSheet, animType.getAnimationSpeed(), animType.
                    getFrames()));
        }
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        this.splashTimer += frameTime;
        if (this.splashTimer >= SPLASH_DURATION) {
            Stack<EnemyTankType> hangar = this.level.getHangar();
            int tankId = LevelState.TANKS_TOTAL - hangar.size() + 1;
            if (!hangar.isEmpty()) {
                this.level.getEntityManager().addEntity(
                        new EnemyTank(this.level,
                                tankId, hangar.pop(), getX(), getY(),
                                Direction.SOUTH));
            }
            destroy();
        }
    }
}
