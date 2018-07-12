package com.igorternyuk.tanks.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

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

    public static Image makeColorTransparent(final BufferedImage image,
            final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public int filterRGB(int x, int y, int rgb) {
                //System.out.println("x = " + x + " y = " + y + " rgb = " + String.format("0x%08X", rgb));
                //System.out.println("marker = " + String.format("0x%08X", markerRGB));
                //System.out.println("(rgb | 0xFF000000) = " + String.format("0x%08X", (rgb | 0xFF000000)));
                if ((rgb | 0xFF000000) == markerRGB) {
                    //System.out.println("making transparent marker = " + String.format("0x%08X", markerRGB));
                    return 0x00FFFFFF & rgb;
                } else {
                    //System.out.println("NOthing to do");
                    return rgb;
                }
            }
        };

        final ImageProducer ip = new FilteredImageSource(image.getSource(),
                filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static BufferedImage imageToBufferedImage(final Image image) {
        final BufferedImage bufferedImage = new BufferedImage(image.getWidth(
                null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }
}
