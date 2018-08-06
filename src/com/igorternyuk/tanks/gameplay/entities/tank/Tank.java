package com.igorternyuk.tanks.gameplay.entities.tank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.AnimatedEntity;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.rockets.RocketType;
import com.igorternyuk.tanks.gameplay.entities.splash.Splash;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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
    protected boolean canTwinShot = false;
    protected boolean canFourWayShot = false;
    protected boolean canLaunchRockets = false;
    protected boolean canRepeateFire = false;
    protected boolean canDynamite = false;
    protected ShootingMode shootingMode = ShootingMode.SINGLE_SHOT;
    protected int rocketsLaunched = 0;
    protected final int rocketsMax = 10;

    public Tank(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
    }

    public abstract TankColor getTankColor();

    public abstract void promote();

    public abstract void promoteToHeavy();

    public abstract void fire();
    
    public boolean isCanTwinShot() {
        return canTwinShot;
    }

    public void setCanTwinShot(boolean canTwinShot) {
        this.canTwinShot = canTwinShot;
    }

    public boolean isCanFourWayShot() {
        return canFourWayShot;
    }

    public void setCanFourWayShot(boolean canFourWayShot) {
        this.canFourWayShot = canFourWayShot;
    }

    public boolean isCanLaunchRockets() {
        return canLaunchRockets;
    }

    public void setCanLaunchRockets(boolean canLaunchRockets) {
        this.canLaunchRockets = canLaunchRockets;
    }
    
    public void gainAbilityToLaunchRockets(){
        this.rocketsLaunched = 0;
        this.canLaunchRockets = true;
        this.shootingMode = ShootingMode.ROCKET;
    }

    public boolean isCanRepeateFire() {
        return canRepeateFire;
    }

    public void setCanRepeateFire(boolean canRepeateFire) {
        this.canRepeateFire = canRepeateFire;
    }

    public boolean isCanDynamite() {
        return canDynamite;
    }

    public void setCanDynamite(boolean canDynamite) {
        this.canDynamite = canDynamite;
    }

    public void freeze(double duration) {
        this.frozenTime = duration;
        this.frozen = true;
    }

    public void unfreeze() {
        this.freezeTimer = 0;
        this.frozen = false;
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    protected void handleIfFrozen(double frameTime) {
        this.freezeTimer += frameTime;
        if (this.freezeTimer >= this.frozenTime) {
            unfreeze();
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

    protected class Departure {

        private Point point;
        private Direction direction;

        public Departure(Point point, Direction direction) {
            this.point = point;
            this.direction = direction;
        }

        public Point getPoint() {
            return point;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    protected List<Departure> calcDeparturePoints(ShootingMode shootingMode) {
        List<Departure> departures = new ArrayList<>(4);
        if (this.shootingMode == ShootingMode.SINGLE_SHOT
                || this.shootingMode == ShootingMode.ROCKET) {
            Point p = calcProjectileDeparturePosition(this.direction);
            departures.add(new Departure(p, this.direction));
        } else if (this.shootingMode == ShootingMode.TWIN_SHOT) {
            Point basePoint = calcProjectileDeparturePosition(this.direction);
            if (this.direction.isVertical()) {
                Point pointLeft = new Point(basePoint.x - Game.QUARTER_TILE_SIZE
                        / 2,
                        basePoint.y);
                Point pointRight = new Point(basePoint.x
                        + Game.QUARTER_TILE_SIZE / 2,
                        basePoint.y);
                departures.add(new Departure(pointLeft, this.direction));
                departures.add(new Departure(pointRight, this.direction));
            } else if (this.direction.isHorizontal()) {
                Point pointUp = new Point(basePoint.x, basePoint.y
                        - Game.QUARTER_TILE_SIZE / 2);
                Point pointDown = new Point(basePoint.x, basePoint.y
                        + Game.QUARTER_TILE_SIZE / 2);
                departures.add(new Departure(pointUp, this.direction));
                departures.add(new Departure(pointDown, this.direction));
            }
        } else if (this.shootingMode == ShootingMode.FOUR_WAY_SHOT) {
            Direction[] allDirections = Direction.values();
            for (Direction dir : allDirections) {
                departures.add(new Departure(calcProjectileDeparturePosition(
                        dir), dir));
            }
        }
        return departures;
    }

    protected Point calcProjectileDeparturePosition(Direction direction) {
        Point departure = new Point();
        int projectileWidth = ProjectileType.getSourceRect(direction).width;
        int projectileHeight =
                ProjectileType.getSourceRect(direction).height;
        int rocketWidth = RocketType.getSourceRect(direction).width;
        int rocketHeight = RocketType.getSourceRect(direction).height;
        int weaponWidth =
                this.shootingMode == ShootingMode.ROCKET ? rocketWidth :
                        projectileWidth;
        int weaponHeight =
                this.shootingMode == ShootingMode.ROCKET ? rocketHeight :
                        projectileHeight;
        switch (direction) {
            case NORTH:
                departure.x = (int) ((left() + right() - weaponWidth) / 2);
                departure.y = (int) top();
                break;
            case SOUTH:
                departure.x = (int) ((left() + right() - weaponWidth) / 2);
                departure.y = (int) bottom() - projectileHeight;
                break;
            case EAST:
                departure.y = (int) ((top() + bottom() - weaponHeight) / 2);
                departure.x = (int) right() - projectileWidth;
                break;
            case WEST:
                departure.y = (int) ((top() + bottom() - weaponHeight) / 2);
                departure.x = (int) left();
                break;
            default:
                break;
        }
        return departure;
    }
    
    @Override
    public void update(KeyboardState keyboardState, double frameTime){
        super.update(keyboardState, frameTime);
        if(this.shootingMode == ShootingMode.ROCKET
                && this.rocketsLaunched >= this.rocketsMax){
            if(this.canFourWayShot){
                this.shootingMode = ShootingMode.FOUR_WAY_SHOT;
            } else if(this.canTwinShot){
                this.shootingMode = ShootingMode.TWIN_SHOT;
            } else {
                this.shootingMode = ShootingMode.SINGLE_SHOT;
            }
            this.canLaunchRockets = false;
        }            
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (this.canTraverseWater) {
            drawOutline(g);
        }
    }

    protected void drawOutline(Graphics2D g) {
        g.setColor(getTankColor().getColor());
        g.setStroke(new BasicStroke(3));
        Rectangle boundingRect = getBoundingRect();
        Rectangle ouline = new Rectangle(
                (int) (Game.SCALE * boundingRect.x - 1),
                (int) (Game.SCALE * boundingRect.y - 1),
                (int) (Game.SCALE * boundingRect.width + 1),
                (int) (Game.SCALE * boundingRect.height + 1));
        g.drawRoundRect(ouline.x, ouline.y, ouline.width,
                ouline.height, 5, 5);
        g.setStroke(new BasicStroke(1));
    }
}
