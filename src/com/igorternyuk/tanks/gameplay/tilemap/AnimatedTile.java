package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.graphics.animations.AnimationManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 * @param <I> Animation identifier
 */
public abstract class AnimatedTile<I> extends Tile{
    
    protected AnimationManager<I> animationManager = new AnimationManager<>();
    
    public AnimatedTile(TileType type, BufferedImage image, double scale) {
        super(type, image, scale);
    }
    
    public abstract void loadAnimations();
    
    @Override
    public void update(KeyboardState keyboardState, double frameTime){
        this.animationManager.update(frameTime);
    }
    
    @Override
    public void draw(Graphics2D g, int x, int y){
        this.animationManager.draw(g, x, y, this.scale, this.scale); 
    }
}
