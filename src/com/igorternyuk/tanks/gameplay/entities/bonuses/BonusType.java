package com.igorternyuk.tanks.gameplay.entities.bonuses;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public enum BonusType {
    TANK_PROTECTION,
    CLOCK,
    SHOVEL,
    STAR,
    GRENADE,
    EXTRA_LIFE,
    GUN;

    private Rectangle sourceRect;

    private BonusType() {
        this.sourceRect = new Rectangle(this.ordinal() * Game.TILE_SIZE, 0,
                Game.TILE_SIZE, Game.TILE_SIZE);
    }

    public Rectangle getSourceRect() {
        return this.sourceRect;
    }
}
