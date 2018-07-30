package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
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
        this.health = 100;
    }
    
    public void hit(Projectile projectile){
        ResourceManager.getInstance().getAudio(AudioIdentifier.STEEL).play();
        if(!projectile.isAntiarmour()){
            return;
        }
        this.health -= projectile.getDamage();
    }
    
    public boolean isAlive(){
        return this.health > 0;
    }
}
