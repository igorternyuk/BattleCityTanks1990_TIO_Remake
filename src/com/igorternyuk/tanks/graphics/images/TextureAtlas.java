package com.igorternyuk.tanks.graphics.images;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class TextureAtlas {

    private BufferedImage atlas;

    public TextureAtlas(BufferedImage atlas) {
        this.atlas = atlas;
    }

    public BufferedImage getAtlas() {
        return atlas;
    }

    public BufferedImage cutOut(Rectangle boundingRect) {
        int x = boundingRect.x;
        int y = boundingRect.y;
        int w = boundingRect.width;
        int h = boundingRect.height;
        return this.atlas.getSubimage(x,y,w,h);
    }

    public BufferedImage cutOut(int topLeftX, int topLeftY, int fragmentWidth,
            int fragmentHeight) {
        return this.atlas.getSubimage(topLeftX, topLeftY, fragmentWidth,
                fragmentHeight);
    }
}
