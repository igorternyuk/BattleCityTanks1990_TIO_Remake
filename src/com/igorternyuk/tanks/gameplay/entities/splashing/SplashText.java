package com.igorternyuk.tanks.gameplay.entities.splashing;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author igor
 */
public class SplashText extends Entity {

    private String text;
    private double duration;
    private Font font;
    private Color color;
    private Color currentColor;
    private int alpha;
    private int width;
    private int height;
    private double timer;
    private boolean drawCenteredText = false;
    private boolean textCentered = false;

    public SplashText(LevelState level, String text, Font font, Color color,
            double x, double y, double duration) {
        super(level, EntityType.SPLASH_TEXT, x, y, 0, Direction.NORTH);
        this.text = text;
        this.duration = duration;
        this.font = font;
        this.color = color;
        this.currentColor = color;
    }
    
    public SplashText(LevelState level, String text, Font font, Color color,
            double duration) {
        super(level, EntityType.SPLASH_TEXT, 0, 0, 0, Direction.NORTH);
        this.text = text;
        this.duration = duration;
        this.font = font;
        this.color = color;
        this.currentColor = color;
        this.drawCenteredText = true;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        this.timer += frameTime;
        if (this.timer >= this.duration) {
            destroy();
        }
        this.alpha =
                (int) (255 * Math.sin(Math.PI * this.timer / this.duration));
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 255) {
            alpha = 255;
        }
        this.currentColor =
                new Color(this.color.getRed(), this.color.getGreen(),
                        this.color.getBlue(), this.alpha);

    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.setFont(this.font);
        g.setColor(this.currentColor);
        Rectangle2D textBounds = g.getFontMetrics().getStringBounds(this.text, g);
        this.height = (int)textBounds.getHeight();
        this.width = (int)textBounds.getWidth();
        if(this.drawCenteredText && !this.textCentered){
            int tx = (Game.WIDTH - Game.RIGHT_PANEL_WIDTH - this.width) / 2;
            int ty = (Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT - this.height) / 2;
            setPosition(tx, ty);
            textCentered = true;
        }        
        g.drawString(this.text, (int)getX(), (int)getY());
    }

}
