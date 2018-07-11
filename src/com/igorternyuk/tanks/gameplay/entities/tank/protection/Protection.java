package com.igorternyuk.tanks.gameplay.entities.tank.protection;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Protection extends AnimatedEntity<ProtectionType>{
    private ProtectionType type;
    
    public Protection(LevelState level, ProtectionType type, double x, double y) {
        super(level, EntityType.PROTECTION, x, y, 0, Direction.NORTH);
        this.type = type;
        loadAnimations();
        this.animationManager.setCurrentAnimation(ProtectionType.REGULAR);
        this.animationManager.getCurrentAnimation().start(AnimationPlayMode.LOOP);
    }

    @Override
    public final void loadAnimations() {
        BufferedImage spriteSheet;
        if(this.type == ProtectionType.REGULAR){
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.TANK_PROTECTION);
        } else {
            spriteSheet = SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.EMPTY);
        }
        
        for (ProtectionType animType : ProtectionType.values()) {
            this.animationManager.addAnimation(animType, new Animation(
                    spriteSheet, animType.getAnimationSpeed(), animType.
                    getFrames()));
        }
    }
}
