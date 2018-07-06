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
        System.out.println("cutting the image fragment out...");
        if(this.atlas == null){
            System.out.println("Atlas not loaded");
        }
        System.out.println("rect = " + boundingRect);
        int x = boundingRect.x;
        int y = boundingRect.y;
        int w = boundingRect.width;
        int h = boundingRect.height;
        System.out.println("x = " + x + " y = " + y + " w = " + w +" h = " + h);
        return this.atlas.getSubimage(x,y,w,h);
    }

    public BufferedImage cutOut(int topLeftX, int topLeftY, int fragmentWidth,
            int fragmentHeight) {
        return this.atlas.getSubimage(topLeftX, topLeftY, fragmentWidth,
                fragmentHeight);
    }
}
