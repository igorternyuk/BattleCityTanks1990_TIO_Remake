package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.utils.Images;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Tile {

    protected TileType type;
    protected Point position;
    protected BufferedImage image;
    protected double scale;

    public Tile(TileType type, Point position, BufferedImage image, double scale) {
        this.type = type;
        this.position = position;
        this.image = Images.resizeImage(image, scale);
        this.scale = scale;
    }

    public Point getPosition() {
        return this.position;
    }

    public final double getX() {
        return this.position.x;
    }

    public final double getY() {
        return this.position.y;
    }

    public int getRow() {
        return (int) (this.position.y / Game.HALF_TILE_SIZE);
    }

    public int getColumn() {
        return (int) (this.position.x / Game.HALF_TILE_SIZE);
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

    public void update(KeyboardState keyboardState, double frameTime) {

    }

    public void draw(Graphics2D g) {
        g.drawImage(this.image, (int) (getX() * this.scale), (int) (getY()
                * this.scale), null);
    }

}
