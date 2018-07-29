package com.igorternyuk.tanks.gameplay;

import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author igor
 */
public class GameOverMessage {

    private Sprite sprite;
    private final int initialRow = 20;
    private final int initialCol = 10;
    private final double slidingSpeed = 20;

    public GameOverMessage() {
        this.sprite = new Sprite(SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.GAME_OVER),
                this.initialCol * Game.HALF_TILE_SIZE,
                this.initialRow * Game.HALF_TILE_SIZE, Game.SCALE);
        Rectangle rect = SpriteSheetIdentifier.GAME_OVER.getBoundingRect();
        this.sprite.setSourceRect(new Rectangle(0, 0, rect.width, rect.height));
        this.sprite.setScale(Game.SCALE);
    }

    public void reset() {
        this.sprite.setPosition(this.initialCol * Game.HALF_TILE_SIZE,
                this.initialRow * Game.HALF_TILE_SIZE);
    }

    public void update(double frameTime) {
        double newX = this.sprite.getX();
        double newY = this.sprite.getY() - this.slidingSpeed * frameTime;
        if (newY > 0) {
            this.sprite.setPosition(newX, newY);
        }
    }

    public void draw(Graphics2D g) {
        this.sprite.draw(g);
    }
}
