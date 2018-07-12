package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.utils.Images;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Tile {

    private BufferedImage image;
    private TileType type;
    private double scale;

    public Tile(TileType type, BufferedImage image, double scale) {
        this.type = type;
        this.image = Images.resizeImage(image, scale);
        this.scale = scale;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public TileType getType() {
        return this.type;
    }

    public double getScale() {
        return this.scale;
    }

    public void draw(Graphics2D g, int x, int y) {
        g.drawImage(this.image, (int) (x * this.scale), (int) (y * this.scale),
                null);
    }

}
