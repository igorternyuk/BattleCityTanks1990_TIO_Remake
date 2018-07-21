package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gamestate.LevelState;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author igor
 * @param <I> Animation identifier
 */
public abstract class Tank<I> extends AnimatedEntity<I> {

    protected boolean canFire = true;

    public Tank(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
    }

    public abstract void fire();

    public void explode() {
        super.explode(ExplosionType.BIG);
    }

    public boolean isCanFire() {
        return this.canFire;
    }

    public void setCanFire(boolean canFire) {
        this.canFire = canFire;
    }

    protected Point calcProjectileDeparturePosition() {
        Point departure = new Point();
        int projectileWidth = ProjectileType.getSourceRect(this.direction).width;
        int projectileHeight =
                ProjectileType.getSourceRect(this.direction).height;
        switch (this.direction) {
            case NORTH:
                departure.x = (int) ((left() + right() - projectileWidth) / 2);
                departure.y = (int) top() - projectileHeight;
                break;
            case SOUTH:
                departure.x = (int) ((left() + right() - projectileWidth) / 2);
                departure.y = (int) bottom() + projectileHeight;
                break;
            case EAST:
                departure.y = (int) ((top() + bottom() - projectileHeight) / 2);
                departure.x = (int) left() + projectileWidth;
                break;
            case WEST:
                departure.y = (int) ((top() + bottom() - projectileHeight) / 2);
                departure.x = (int) left() - projectileWidth;
                break;
            default:
                break;
        }
        return departure;
    }

    protected boolean checkMapCollision() {
        TileMap tileMap = this.level.getTileMap();
        if (tileMap.hasCollision(this)) {
            Tile collided = tileMap.getLastCollided();
            collided.handleTankCollision(this);
            return true;
        }
        return false;
    }
    
    protected List<Tank> getOtherTanks(){
        List<Tank> otherTanks = this.level.getEntityManager()
                .getEntitiesByType(EntityType.ENEMY_TANK).stream()
                .filter(tank -> !tank.equals(this)).map(tank -> (Tank) tank)
                .collect(Collectors.toList());
        return otherTanks; 
    }

    protected boolean handleCollisionsWithOtherTanks() {
        List<Tank> otherTanks = getOtherTanks();
        for (int i = 0; i < otherTanks.size(); ++i) {
            Tank currTank = otherTanks.get(i);
            if (checkIfCollidesOtherTank(currTank)) {
                handleCollisionWithOtherTank(currTank);
                return true;
            }
        }
        return false;
    }

    protected boolean checkIfCollidesOtherTank(Tank other) {
        Rectangle thisBoundingRect = this.getBoundingRect();
        Rectangle otherBoundingRect = other.getBoundingRect();
        return thisBoundingRect.intersects(otherBoundingRect);
    }

    protected void handleCollisionWithOtherTank(Tank other) {
        Rectangle thisBoundingRect = this.getBoundingRect();
        Rectangle otherBoundingRect = other.getBoundingRect();
        Rectangle intersection = thisBoundingRect.
                intersection(otherBoundingRect);
        if (this.direction.isVertical()) {
            setPosition(this.x, this.y + intersection.height * this.direction.
                    getOpposite().getVy());
        } else if (this.direction.isHorizontal()) {
            setPosition(this.x + intersection.width * this.direction.
                    getOpposite().getVx(), this.y);
        }
    }
}
