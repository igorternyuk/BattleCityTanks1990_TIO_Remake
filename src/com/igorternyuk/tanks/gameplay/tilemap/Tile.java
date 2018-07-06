package com.igorternyuk.tanks.gameplay.tilemap;

import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Tile {

    private BufferedImage image;
    private TileType type;

    public Tile(BufferedImage image, TileType type) {
        this.image = image;
        this.type = type;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public TileType getType() {
        return this.type;
    }

}
