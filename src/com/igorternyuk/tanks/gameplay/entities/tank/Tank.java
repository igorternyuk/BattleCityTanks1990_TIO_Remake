package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.splash.Splash;
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
    
    public void reverse(){
        setDirection(direction.getOpposite());
    }
    
    protected void fitToTiles(){
        if(this.direction.isVertical()){
            double dx = (int)getX() / Game.QUARTER_TILE_SIZE - getX();
            System.out.println("dx = " + dx);
            if(Math.abs(dx) < Game.QUARTER_TILE_SIZE / 2){
                setPosition(getX() + dx, getY());
            }
        } else if(this.direction.isHorizontal()){
            double dy = (int)getY() / Game.QUARTER_TILE_SIZE - getY();
            System.out.println("dy = " + dy);
            if(Math.abs(dy) < Game.QUARTER_TILE_SIZE / 2){
                setPosition(getX(), getY() + dy);
            }
        }
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
    
    protected boolean handleCollisionsWithSplashes() {
        List<Entity> splashes = this.level.getEntityManager().getEntitiesByType(
                EntityType.SPLASH);
        for (int i = splashes.size() - 1; i >= 0; --i) {
            Splash currSplash = (Splash) splashes.get(i);
            if (collides(currSplash)) {
                Rectangle intersection = getBoundingRect().intersection(
                        currSplash.getBoundingRect());
                correctPositionAfterIntersection(intersection);
                return true;
            }
        }
        return false;
    }

    protected List<Tank> getOtherTanks() {
        List<Tank> otherTanks = this.level.getEntityManager()
                .getEntitiesByType(EntityType.ENEMY_TANK).stream()
                .filter(tank -> !tank.equals(this)).map(tank -> (Tank) tank)
                .collect(Collectors.toList());
        return otherTanks;
    }

    protected boolean handleCollisionsWithOtherTanks(double frameTime) {
        List<Tank> otherTanks = getOtherTanks();
        for (int i = 0; i < otherTanks.size(); ++i) {
            Tank currTank = otherTanks.get(i);
            if (checkCollisionWithOtherTank(currTank, frameTime)) {
                return true;
            }
        }
        return false;
    }

    protected boolean checkCollisionWithOtherTank(Tank other, double frameTime) {
        Rectangle thisBoundingRect = this.getBoundingRect();
        Rectangle otherBoundingRect = other.getBoundingRect();
        if(thisBoundingRect.intersects(otherBoundingRect)){
            Rectangle intersection = thisBoundingRect.
                intersection(otherBoundingRect);
            correctPositionAfterIntersection(intersection);
            reverse();
            move(frameTime);
            return true;
        }
        return false;        
    }

    private void correctPositionAfterIntersection(Rectangle intersection) {
        if (this.direction.isVertical()) {
            setPosition(this.x, this.y + intersection.height * this.direction.
                    getOpposite().getDy());
        } else if (this.direction.isHorizontal()) {
            setPosition(this.x + intersection.width * this.direction.
                    getOpposite().getDx(), this.y);
        }
    }
    
    protected Point calcProjectileDeparturePosition() {
        Point departure = new Point();
        int projectileWidth = ProjectileType.getSourceRect(this.direction).width;
        int projectileHeight =
                ProjectileType.getSourceRect(this.direction).height;
        switch (this.direction) {
            case NORTH:
                departure.x = (int) ((left() + right() - projectileWidth) / 2);
                departure.y = (int) top()/* - projectileHeight*/;
                break;
            case SOUTH:
                departure.x = (int) ((left() + right() - projectileWidth) / 2);
                departure.y = (int) bottom() - projectileHeight;
                break;
            case EAST:
                departure.y = (int) ((top() + bottom() - projectileHeight) / 2);
                departure.x = (int) right() - projectileWidth;
                break;
            case WEST:
                departure.y = (int) ((top() + bottom() - projectileHeight) / 2);
                departure.x = (int) left()/* - projectileWidth*/;
                break;
            default:
                break;
        }
        return departure;
    }
}
