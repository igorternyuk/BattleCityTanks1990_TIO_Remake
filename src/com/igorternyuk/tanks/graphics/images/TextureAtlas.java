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

    public BufferedImage cutOut(Rectangle boundingRect) {
        return this.atlas.getSubimage(boundingRect.x, boundingRect.y,
                boundingRect.width, boundingRect.height);
    }

    public BufferedImage cutOut(int topLeftX, int topLeftY, int fragmentWidth,
            int fragmentHeight) {
        return this.atlas.getSubimage(topLeftX, topLeftY, fragmentWidth,
                fragmentHeight);
    }
}
