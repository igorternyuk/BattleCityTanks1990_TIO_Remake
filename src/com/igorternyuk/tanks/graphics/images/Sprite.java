package com.igorternyuk.tanks.graphics.images;

import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.utils.Images;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Sprite extends Image {

    private double scale;
    private Rectangle sourceRect;
    private BufferedImage currentImageFragment;
    private int destX, destY;

    public Sprite(BufferedImage image, double x, double y, double scale) {
        super(image, x, y);
        this.scale = scale;
        setSourceRect(new Rectangle(0, 0, this.image.getWidth(), this.image.
                getHeight()));
        setPosition(x, y);
        
        setScale(scale);
    }

    @Override
    public final void setPosition(double x, double y) {
        super.setPosition(x, y);
        this.destX = (int) (x * this.scale);
        this.destY = (int) (y * this.scale);
    }

    public Rectangle getSourceRect() {
        return this.sourceRect;
    }

    public final void setSourceRect(Rectangle sourceRect) {
        this.sourceRect = sourceRect;
        this.currentImageFragment = this.image.getSubimage(sourceRect.x,
                sourceRect.y, sourceRect.width, sourceRect.height);
        scaleCurrentImageFragment(scale);
    }

    public final void setScale(double scale) {
        this.scale = scale;
        scaleCurrentImageFragment(scale);
    }
    
    private void scaleCurrentImageFragment(double scale){
        this.currentImageFragment = Images.
                resizeImage(this.currentImageFragment, scale);
    }

    public double getScale() {
        return this.scale;
    }

    public int getWidth() {
        return this.sourceRect.width;
    }

    public int getHeight() {
        return this.sourceRect.height;
    }

    @Override
    public void update(KeyboardState keyBoardState, double frameTime) {

    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(this.currentImageFragment, this.destX, this.destY, null);
    }
}
