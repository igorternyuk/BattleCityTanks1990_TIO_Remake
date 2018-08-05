package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Rocket extends Entity{
    private RocketType type;
    private Sprite sprite;
    private int ownerId = 0;
    private int damage = 1000;

    public Rocket(LevelState level, RocketType type, double x, double y,
            double speed, Direction direction) {
        super(level, EntityType.ROCKET, x, y, speed, direction);
        this.type = type;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.ROCKET);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        updateSprite();
    }

    public RocketType getType() {
        return type;
    }
    
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getDamage() {
        return damage;
    }
    
    @Override
    public boolean isAlive() {
        return super.isAlive() && !isOutOfBounds();
    }

    public void explode() {
        super.explode(ExplosionType.BIG);
        destroy();
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        updateSprite();
    }

    private void updateSprite() {
        this.sprite.setSourceRect(RocketType.getSourceRect(this.direction));
        this.sprite.setPosition(this.x, this.y);
    }

    @Override
    public int getWidth() {
        return this.sprite.getWidth();
    }

    @Override
    public int getHeight() {
        return this.sprite.getHeight();
    }
    
    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        move(frameTime);
        this.sprite.setPosition(getX(), getY());
    }
    
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.sprite.draw(g);
    }
}
