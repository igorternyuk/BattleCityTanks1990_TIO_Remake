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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
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
    protected boolean canTraverseWater = false;
    protected boolean frozen = false;
    protected double freezeTimer = 0;
    protected double frozenTime = 0;
    protected boolean canClearBushes = false;

    public Tank(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
    }

    public abstract void promote();

    public abstract void promoteToHeavy();

    public abstract void fire();

    public void freeze(double duration) {
        this.frozenTime = duration;
        this.frozen = true;
    }

    protected void handleIfFrozen(double frameTime) {
        this.freezeTimer += frameTime;
        if (this.freezeTimer >= this.frozenTime) {
            this.freezeTimer = 0;
            this.frozen = false;
        }
    }

    public boolean isCanTraverseWater() {
        return this.canTraverseWater;
    }

    public void setCanTraverseWater(boolean canTraverseWater) {
        this.canTraverseWater = canTraverseWater;
    }

    public void explode() {
        super.explode(ExplosionType.BIG);
    }

    public boolean isCanFire() {
        return this.canFire;
    }

    public void setCanFire(boolean canFire) {
        this.canFire = canFire;
    }

    public void reverse() {
        setDirection(direction.getOpposite());
    }

    protected void fitToTiles() {
        if (this.direction.isVertical()) {
            int c = (int) getX() / Game.QUARTER_TILE_SIZE;
            double dx = c * Game.QUARTER_TILE_SIZE - getX();
            if (Math.abs(dx) < Game.QUARTER_TILE_SIZE) {
                setPosition(getX() + dx, getY());
            }
        } else if (this.direction.isHorizontal()) {
            int r = (int) getY() / Game.QUARTER_TILE_SIZE;
            double dy = r * Game.QUARTER_TILE_SIZE - getY();
            if (Math.abs(dy) < Game.QUARTER_TILE_SIZE) {
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
        if (thisBoundingRect.intersects(otherBoundingRect)) {
            handleCollisionWithOtherTank(other, frameTime);
            return true;
        }
        return false;
    }

    protected void handleCollisionWithOtherTank(Tank other, double frameTime) {
        Rectangle thisBoundingRect = this.getBoundingRect();
        Rectangle otherBoundingRect = other.getBoundingRect();
        Rectangle intersection = thisBoundingRect.
                intersection(otherBoundingRect);
        correctPositionAfterIntersection(intersection);
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
                departure.y = (int) top();
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
                departure.x = (int) left();
                break;
            default:
                break;
        }
        return departure;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (this.canTraverseWater) {
            drawOutline(g);
        }
    }

    protected void drawOutline(Graphics2D g) {
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(3));
        Rectangle boundingRect = getBoundingRect();
        Rectangle ouline = new Rectangle(
                (int)(Game.SCALE * boundingRect.x - 1),
                (int)(Game.SCALE * boundingRect.y - 1),
                (int)(Game.SCALE * boundingRect.width + 1),
                (int)(Game.SCALE * boundingRect.height + 1));
        g.drawRoundRect(ouline.x, ouline.y, ouline.width,
                ouline.height, 5, 5);
        g.setStroke(new BasicStroke(1));
    }
}
