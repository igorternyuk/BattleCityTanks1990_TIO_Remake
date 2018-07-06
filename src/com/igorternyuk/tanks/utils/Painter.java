package com.igorternyuk.tanks.utils;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author igor
 */
public class Painter {

    public static void drawCenteredString(Graphics2D g, String text, Font font,
            Color color, int height) {
        g.setFont(font);
        g.setColor(color);
        int textWidth = (int) g.getFontMetrics().getStringBounds(text, g).
                getWidth();
        g.drawString(text, (Game.WIDTH - textWidth) / 2, height);
    }
}
