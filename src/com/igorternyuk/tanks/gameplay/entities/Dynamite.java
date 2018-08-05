package com.igorternyuk.tanks.gameplay.entities;

import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;

/**
 *
 * @author igor
 */
public class Dynamite extends Entity{
    
    private Sprite sprite;
    
    public Dynamite(LevelState level, double x, double y) {
        super(level, EntityType.DYNAMITE, x, y, 0, Direction.NORTH);
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
    }
    
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.sprite.draw(g);
    }
}
