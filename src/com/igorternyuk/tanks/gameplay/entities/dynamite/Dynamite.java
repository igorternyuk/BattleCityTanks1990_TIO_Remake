package com.igorternyuk.tanks.gameplay.entities.dynamite;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Dynamite extends Entity{
    
    private Sprite sprite;
    
    public Dynamite(LevelState level, double x, double y) {
        super(level, EntityType.DYNAMITE, x, y, 0, Direction.NORTH);
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.DYNAMITE);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        this.sprite.setSourceRect(new Rectangle(0,0,Game.TILE_SIZE,Game.TILE_SIZE));
        updateSprite();
    }

    @Override
    public int getWidth() {
        return this.sprite.getWidth();
    }

    @Override
    public int getHeight() {
        return this.sprite.getHeight();
    }
    
    public void explode() {
        super.explode(ExplosionType.SMALL);
        destroy();
    }
    
    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        updateSprite();
    }
    
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.sprite.draw(g);
    }
    
    private void updateSprite() {
        this.sprite.setPosition(getX(), getY());
    }
}
