package com.igorternyuk.tanks.gameplay.entities.eagle;

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
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Eagle extends Entity{
    
    private EagleState state = EagleState.ALIVE;
    private Sprite sprite;

    public Eagle(LevelState level, double x, double y) {
        super(level, EntityType.EAGLE, x, y, 0, Direction.NORTH);
        SpriteSheetManager spriteSheetManager = SpriteSheetManager.getInstance();
        BufferedImage spriteSheet = spriteSheetManager.get(
                SpriteSheetIdentifier.EAGLE);
        this.sprite = new Sprite(spriteSheet, x, y, Game.SCALE);
        this.sprite.setSourceRect(this.state.getSourceRect());
    }

    public EagleState getState() {
        return this.state;
    }
    
    public void kill(){
        this.state = EagleState.DEAD;
        this.sprite.setSourceRect(this.state.getSourceRect());
        this.explode(ExplosionType.BIG);
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
    public void update(KeyboardState keyboardState, double frameTime){
        super.update(keyboardState, frameTime);
        this.sprite.update(keyboardState, frameTime);
    }
    
    @Override
    public void draw(Graphics2D g){
        super.draw(g);
        this.sprite.draw(g);
    }
}
