package com.igorternyuk.tanks.gameplay.entities.bonuses;

import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Bonus extends Entity {

    private BonusType type;
    private Sprite sprite;
    
    public Bonus(LevelState level, BonusType type, double x, double y) {
        super(level, EntityType.BONUS, x, y, 0, Direction.NORTH);
        this.type = type;
        BufferedImage image = this.level.getSpriteSheetManager().get(
                SpriteSheetIdentifier.BONUS);
        this.sprite = new Sprite(image, this.x, this.y, LevelState.SCALE);
        this.sprite.setSourceRect(this.type.getSourceRect());
    }

    public BonusType getType() {
        return this.type;
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
    }

    @Override
    public void draw(Graphics2D g) {
        if(!this.needToDraw)
            return;
        super.draw(g);
        this.sprite.draw(g);
    }

}
