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
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class PowerUp extends Entity {

    private static final double LIFE_TIME = 20;
    private PowerUpType type;
    private Sprite sprite;
    private double timer = 0;

    public PowerUp(LevelState level, PowerUpType type, double x, double y) {
        super(level, EntityType.POWER_UP, x, y, 0, Direction.NORTH);
        this.type = type;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.BONUS);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        this.sprite.setSourceRect(this.type.getSourceRect());
        this.sprite.setPosition(getX(), getY());
    }

    public void collect() {
        ScoreIcrementText text = new ScoreIcrementText(this.level, this.type.
                getScore(), this.x, this.y);
        text.startInfiniteBlinking(0.2);
        int dx = (getWidth() - text.getWidth()) / 2;
        int dy = (getHeight()- text.getHeight()) / 2;
        text.setPosition(getX() + dx, getY() + dy);
        this.level.getEntityManager().addEntity(text);
        ResourceManager.getInstance().getAudio(AudioIdentifier.BONUS_COLLECTED).
                play();
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
        updateBlinkTimer(frameTime);
        this.timer += frameTime;
        if(this.timer > LIFE_TIME){
            destroy();
        }
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
