package com.igorternyuk.tanks.graphics.spritesheets;

import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum SpriteSheetIdentifier {
    EAGLE(304,32,32,16),
    TANK(0,0,256,256),
    TILE(256, 0, 80, 88),
    PROJECTILE(320, 96, 32, 16),
    PROJECTILE_EXPLOSION(256, 128, 48, 16),
    TANK_EXPLOSION(304, 128, 64, 32),
    TANK_PROTECTION(256, 144, 32, 16),
    BONUS(256, 112, 112, 16),
    SPLASH(256, 96, 64, 16),
    SCORES(288, 160, 80, 16),
    NUMBERS(327, 183, 40, 16),
    PAUSE(288,176,40,8),
    GAME_OVER(288,184,32,16),
    LEVEL_FLAG(376,184,16,16),
    ENEMY_TANK_SIGN(320,192,8,8),
    RIGHT_PANEL(368,0,32,240),
    EMPTY(288, 48, 16, 16);

    private Rectangle boundingRect;
    
    private SpriteSheetIdentifier(int x, int y, int width, int height){
        this.boundingRect = new Rectangle(x, y, width, height);
    }
    
    private SpriteSheetIdentifier(Rectangle boundingRect){
        this.boundingRect = boundingRect;
    }
    
    public Rectangle getBoundingRect(){
        return this.boundingRect;
    }
}
