package com.igorternyuk.tanks.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Images {

    public static BufferedImage resizeImage(BufferedImage source, int width,
            int height) {
        BufferedImage resizedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = resizedImage.getGraphics();
        g.drawImage(source, 0, 0, width, height, null);
        return resizedImage;
    }

    public static BufferedImage resizeImage(BufferedImage source, double scale) {
        int newWidth = (int) (source.getWidth() * scale);
        int newHeight = (int) (source.getHeight() * scale);
        return resizeImage(source, newWidth, newHeight);
    }
}
