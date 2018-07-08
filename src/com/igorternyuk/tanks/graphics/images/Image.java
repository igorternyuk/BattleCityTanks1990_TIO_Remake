package com.igorternyuk.tanks.graphics.images;

import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public abstract class Image {

    protected BufferedImage image;
    protected double x;
    protected double y;

    public Image(BufferedImage image, double x, double y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    public Image(BufferedImage image) {
        this(image, 0, 0);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update(KeyboardState keyBoardState, double frameTime);

    public void draw(Graphics2D g) {
        g.drawImage(image, (int) this.x, (int) this.y, null);
    }

}
