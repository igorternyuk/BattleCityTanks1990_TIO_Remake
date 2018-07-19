package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gamestate.LevelState;
import java.awt.Point;

/**
 *
 * @author igor
 * @param <I> Animation identifier
 */
public abstract class Tank<I> extends AnimatedEntity<I>{
    protected boolean canFire = true;
    public Tank(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
    }
    
    public abstract void chooseDirection();    
    public abstract void fire();
    
    protected void explode(){
        super.explode(ExplosionType.BIG);
    }

    public boolean isCanFire() {
        return this.canFire;
    }

    public void setCanFire(boolean canFire) {
        this.canFire = canFire;
    }
    
    protected Point calcPointOfProjectileDeparture(){
        Point departure = new Point();
        int projectileWidth = ProjectileType.getSourceRect(this.direction).width;
        int projectileHeight = ProjectileType.getSourceRect(this.direction).height;
        switch (this.direction) {
            case NORTH:
                departure.x = (int)((left() + right() - projectileWidth) / 2);
                departure.y = (int)top() - projectileHeight;
                break;
            case SOUTH:
                departure.x = (int)((left() + right() - projectileWidth) / 2);
                departure.y = (int)bottom() + projectileHeight;
                break;
            case EAST:
                departure.y = (int)((top() + bottom() - projectileHeight) / 2);
                departure.x = (int)left() + projectileWidth;
                break;
            case WEST:
                departure.y = (int)((top() + bottom() - projectileHeight) / 2);
                departure.x = (int)left() - projectileWidth;
                break;
            default:
                break;
        }
        return departure;
    }
    
    protected boolean checkMapCollision(){
        TileMap tileMap = this.level.getTileMap();
        if(tileMap.hasCollision(this)){
            Tile collided = tileMap.getLastCollided();
            collided.handleTankCollision(this);
            return true;
        }
        return false;
    }
}
