package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gamestate.LevelState;
import java.awt.Point;

/**
 *
 * @author igor
 */
public abstract class Tank<T> extends AnimatedEntity<T>{
    protected boolean canFire = true;
    public Tank(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
    }
    
    public abstract void chooseDirection();    
    public abstract void fire();

    public boolean isCanFire() {
        return this.canFire;
    }

    public void setCanFire(boolean canFire) {
        this.canFire = canFire;
    }
    
    public void explode(){
        
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
    
}
