package com.igorternyuk.tanks.utils;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Painter {

    private static final Color DIGIT_DEFAULT_COLOR = new Color(0, 0, 1);
    private static final Color DIGIT_BACKGROUND_COLOR = new Color(99, 99, 99);

    public static void drawCenteredString(Graphics2D g, String text, Font font,
            Color color, int height) {
        g.setFont(font);
        g.setColor(color);
        int textWidth = (int) g.getFontMetrics().getStringBounds(text, g).
                getWidth();
        g.drawString(text, (Game.WIDTH - textWidth) / 2, height);
    }

    public static void drawNumber(Graphics2D g, int number, Color color, int x,
            int y, double scale) {
        SpriteSheetManager spriteSheetManager = SpriteSheetManager.getInstance();
        String numberAsText = String.valueOf(number);
        char[] chars = numberAsText.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            int digit = Integer.valueOf(String.valueOf(chars[i]));
            BufferedImage digitImage = Images.resizeImage(spriteSheetManager.
                    fetchDigitSprite(digit), scale);

            BufferedImage imageWithTransparentDigits = Images.
                    imageToBufferedImage(Images.makeColorTransparent(digitImage,
                            DIGIT_DEFAULT_COLOR));
            BufferedImage imageWithColoredDigits = Images.imageToBufferedImage(
                    Images.changeTransparentColor(imageWithTransparentDigits,
                            color));
            BufferedImage imageWithTransparentBackground = Images.
                    imageToBufferedImage(Images.makeColorTransparent(
                            imageWithColoredDigits, DIGIT_BACKGROUND_COLOR));
            BufferedImage desiredImage = Images.imageToBufferedImage(Images.
                    changeTransparentColor(imageWithTransparentBackground,
                            Color.black));
            g.drawImage(desiredImage, x + i
                    * (int) (Game.HALF_TILE_SIZE * scale), y, null);
        }
        spriteSheetManager.fetchDigitSprite(y);
    }
}
