package com.igorternyuk.tanks.gameplay.tilemap;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class MetalTile extends Tile{
    
    protected int health;
    
    public MetalTile(TileType type, Point position, BufferedImage image,
            double scale) {
        super(type, position, image, scale);
    }
    
    public void hit(int damage){
        this.health -= damage;
    }
    
}
