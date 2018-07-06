package com.igorternyuk.tanks.graphics.images;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Background extends Image {

    private static final int SCROLLING_SPEED = 5;

    public Background(BufferedImage image, double x, double y, double dx,
            double dy) {
        super(image, x, y, dx, dy);
    }

    public Background(BufferedImage image) {
        super(image, 0, 0, 0, 0);
    }

    @Override
    public void update(KeyboardState keyBoardState, double frameTime) {
        this.x += this.dx * SCROLLING_SPEED;
        this.y += this.dy * SCROLLING_SPEED;
        fixCoordinateBounds();
    }

    private void fixCoordinateBounds() {
        if (this.x < -Game.WIDTH || this.x > Game.WIDTH) {
            setPosition(0, this.y);
        }

        if (this.y < -Game.HEIGHT || this.y > Game.HEIGHT) {
            setPosition(0, this.y);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(image, (int) this.x, (int) this.y, Game.WIDTH,
                Game.HEIGHT, null);
        if (this.x < 0) {
            g.drawImage(image, (int) this.x + Game.WIDTH, (int) this.y,
                    Game.WIDTH,
                    Game.HEIGHT, null);
        }

        if (this.x > 0) {
            g.drawImage(image, (int) this.x - Game.WIDTH, (int) this.y,
                    Game.WIDTH,
                    Game.HEIGHT, null);
        }
    }
}
