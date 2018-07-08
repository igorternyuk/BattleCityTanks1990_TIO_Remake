package com.igorternyuk.tanks.graphics.images;

import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Sprite extends Image {

    private Rectangle sourceRect;
    private Rectangle destRect;

    public Sprite(BufferedImage image, double x, double y) {
        super(image, x, y);
        this.sourceRect = new Rectangle(0, 0, this.image.getWidth(), this.image.
                getHeight());
        this.destRect = new Rectangle((int) this.x, (int) this.y, this.image.
                getWidth(), this.image.getHeight());
    }

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        this.destRect.x = (int) x;
        this.destRect.y = (int) y;
    }

    public Rectangle getSourceRect() {
        return this.sourceRect;
    }

    public void setSourceRect(Rectangle sourceRect) {
        this.sourceRect = sourceRect;
    }

    public Rectangle getDestRect() {
        return this.destRect;
    }

    public void setDestRect(Rectangle destRect) {
        this.destRect = destRect;
    }

    public int getWidth() {
        return this.destRect.width;
    }

    public int getHeight() {
        return this.destRect.height;
    }
    
    @Override
    public void update(KeyboardState keyBoardState, double frameTime) {
        
    }
    
    @Override
    public void draw(Graphics2D g) {

        g.drawImage(image, (int) (this.destRect.x * LevelState.SCALE),
                (int) (this.destRect.y * LevelState.SCALE),
                (int) ((this.destRect.x + this.destRect.width)
                * LevelState.SCALE),
                (int) ((this.destRect.y + this.destRect.height)
                * LevelState.SCALE),
                this.sourceRect.x, this.sourceRect.y,
                this.sourceRect.x + this.sourceRect.width,
                this.sourceRect.y + this.sourceRect.height, null);
    }
}
