package com.igorternyuk.tanks.graphics.spritesheets;

import java.awt.Color;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum SpriteSheetIdentifier {
    EAGLE(new Rectangle(304,32,32,16), new Color(0xFFFFFFFF)),
    TANK(new Rectangle(0,0,256,256), new Color(0xFF000001)),
    GRAY_STATISTICS_TANKS(new Rectangle(128,64,16,64), new Color(0xFF000001)),
    SMALL_TILES(new Rectangle(256, 64, 32, 24), new Color(0xFF000000)),
    LARGE_TILES(new Rectangle(256, 0, 48, 64), new Color(0xFF000001)),
    PROJECTILE(new Rectangle(260, 166, 27, 4), new Color(0xFF000001)),
    PROJECTILE_EXPLOSION(new Rectangle(256, 128, 48, 16), new Color(0xFF000001)),
    TANK_EXPLOSION(new Rectangle(304, 128, 64, 32), new Color(0xFF000001)),
    TANK_PROTECTION(new Rectangle(256, 144, 32, 16), new Color(0xFF000001)),
    BONUS(new Rectangle(256, 96, 112, 32), new Color(0xFF000001)),
    SPLASH(new Rectangle(256, 208, 96, 16), new Color(0xFF000001)),
    SCORES(new Rectangle(288, 160, 80, 16), new Color(0xFF000001)),
    DIGITS(new Rectangle(328, 184, 40, 16), new Color(0xFFFFFFFF)),
    PAUSE(new Rectangle(288,176,40,8), new Color(0xFF000001)),
    GAME_OVER(new Rectangle(288,184,32,16), new Color(0xFF000001)),
    STAGE_FLAG(new Rectangle(376,184,16,16), new Color(0xFF000001)),
    ENEMY_TANK_SIGN(new Rectangle(320,192,8,8), new Color(0xFFFFFFFF)),
    RIGHT_PANEL(new Rectangle(368,0,32,240), new Color(0xFF000001)),
    EMPTY(new Rectangle(288, 48, 16, 16), new Color(0xFFFFFFFF)),
    GRAY_TILE(new Rectangle(368, 208, 16, 16), new Color(0xFFFFFFFF)),
    LEVEL_FLAG(new Rectangle(376, 184, 16, 16), new Color(0x636363)),
    BRICK(new Rectangle(256, 64, 8, 8), new Color(0xFFFFFFFF)),
    ROCKET(new Rectangle(256, 224, 64, 16), new Color(0xFF000001)),
    DYNAMITE(new Rectangle(288, 144, 16, 16), new Color(0xFFFFFFFF));

    private Rectangle boundingRect;
    private Color colorToFilter;
    
    private SpriteSheetIdentifier(Rectangle rect, Color colorToFilter){
        this.boundingRect = rect;
        this.colorToFilter = colorToFilter;
    }
    
    public Rectangle getBoundingRect(){
        return this.boundingRect;
    }

    public Color getColorToFilter() {
        return this.colorToFilter;
    }
}
