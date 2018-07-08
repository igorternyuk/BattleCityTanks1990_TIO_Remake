package com.igorternyuk.tanks.gameplay.entities.projectiles;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Projectile extends Entity {

    private ProjectileType projectileType;
    private Sprite sprite;

    public Projectile(LevelState level, ProjectileType projectileType, double x,
            double y, double speed, Direction direction) {
        super(level, EntityType.PROJECTILE, x, y, speed, direction);
        this.projectileType = projectileType;
        BufferedImage image = this.level.getSpriteSheetManager().get(
                SpriteSheetIdentifier.PROJECTILE);
        this.sprite = new Sprite(image, this.x, this.y);
        updateSprite();
    }

    public ProjectileType getProjectileType() {
        return this.projectileType;
    }
    
    @Override
    public boolean isAlive(){
        return super.isAlive() && !isOutOfBounds();
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        updateSprite();
    }

    private void updateSprite() {
        Rectangle s = ProjectileType.getSourceRect(this.direction);
        System.out.println("s = " + s);
        this.sprite.setSourceRect(s);
        this.sprite.setPosition(this.x, this.y);
        this.sprite.getDestRect().width = this.sprite.getSourceRect().width;
        this.sprite.getDestRect().height = this.sprite.getSourceRect().height;
        System.out.println("d = " + this.sprite.getDestRect());
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
        move(frameTime);
        this.sprite.setPosition(this.x, this.y);
    }

    @Override
    public void draw(Graphics2D g) {
        this.sprite.draw(g);
    }

}
