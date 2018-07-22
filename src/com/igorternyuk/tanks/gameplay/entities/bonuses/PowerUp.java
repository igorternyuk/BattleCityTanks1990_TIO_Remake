package com.igorternyuk.tanks.gameplay.entities.bonuses;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.text.ScoreIcrementText;
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
public class PowerUp extends Entity {

    private PowerUpType type;
    private Sprite sprite;

    public PowerUp(LevelState level, PowerUpType type, double x, double y) {
        super(level, EntityType.POWER_UP, x, y, 0, Direction.NORTH);
        this.type = type;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.BONUS);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        this.sprite.setSourceRect(this.type.getSourceRect());
    }

    public void collect() {
        ScoreIcrementText text = new ScoreIcrementText(this.level, this.type.
                getScore(), this.x, this.y);
        text.startInfiniteBlinking(0.2);
        text.startInfiniteBlinking(0.2);
        int dx = (getWidth() - text.getWidth()) / 2;
        int dy = (getHeight()- text.getHeight()) / 2;
        text.setPosition(getX() + dx, getY() + dy);
        this.level.getEntityManager().addEntity(text);
        destroy();
    }

    public PowerUpType getType() {
        return this.type;
    }
    
    public int getScore(){
        return this.type.getScore();
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
        this.sprite.setPosition(getX(), getY());
        updateBlinkTimer(frameTime);
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.needToDraw) {
            return;
        }
        super.draw(g);
        this.sprite.draw(g);
    }

}
