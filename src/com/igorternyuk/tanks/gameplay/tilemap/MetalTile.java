package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class MetalTile extends Tile{
    
    protected int health;
    
    protected MetalTile(Point position, BufferedImage image, double scale) {
        super(TileType.METAL, position, image, scale);
    }
    
    public void hit(Projectile projectile){
        if(!projectile.isAntiarmour()){
            return;
        }
        this.health -= projectile.getDamage();
    }
    
    public boolean isAlive(){
        return this.health > 0;
    }
}