package com.igorternyuk.tanks.gameplay.entities.text;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
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
public class ScoreIcrementText extends Entity {
    
    private static final double LIFE_TIME = 5;
    private int score;
    private Sprite sprite;
    private double timer;

    public static Rectangle getSourceRectByScore(int score) {
        boolean isScoreAcceptable = false;
        for (int i = 100; i <= 500; i += 100) {
            if (score == i) {
                isScoreAcceptable = true;
                break;
            }
        }
        if (!isScoreAcceptable) {
            score = 100;
        }
        Rectangle rect = new Rectangle((score / 100 - 1) * Game.TILE_SIZE, 0,
                Game.TILE_SIZE, Game.TILE_SIZE);
        return rect;
    }

    public ScoreIcrementText(LevelState level, int score, double x, double y) {
        super(level, EntityType.SCRORE_TEXT, x, y, 0, Direction.NORTH);
        this.score = score;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.SCORES);
        this.sprite = new Sprite(image, this.x, this.y, LevelState.SCALE);
        this.sprite.setSourceRect(getSourceRectByScore(this.score));
    }

    public int getScore() {
        return this.score;
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
        this.sprite.setPosition(this.x, this.y);
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
