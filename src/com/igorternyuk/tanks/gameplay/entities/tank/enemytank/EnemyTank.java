package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUpType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gameplay.entities.text.ScoreIcrementText;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author igor
 */
public class EnemyTank extends Tank<EnemyTankIdentifier> {

    private static final double COLOR_CHANGING_PERIOD = 0.1;
    private static final int[] BONUS_TANKS_NUMBERS = {4, 11, 18};
    private int number;
    private EnemyTankIdentifier identifier;
    private boolean bonus = false;
    private boolean gleaming = false;
    private double colorPlayingTimer;
    private final Random random = new Random();
    private Point currTarget;

    public EnemyTank(LevelState level, int number, EnemyTankType type, double x,
            double y, Direction direction) {
        super(level, EntityType.ENEMY_TANK, x, y, type.getSpeed(), direction);
        this.number = number;
        this.health = type.getHealth();
        checkIfBonus();
        loadAnimations();
        this.identifier = new EnemyTankIdentifier(calcColorDependingOnHealth(),
                Heading.getHeading(direction), type);
        updateAnimation();
        this.currTarget = this.level.getPlayer().getPosition();
        this.moving = true;
    }

    public EnemyTankType getType() {
        return this.identifier.getType();
    }

    private void checkIfBonus() {
        for (int num : BONUS_TANKS_NUMBERS) {
            if (this.number == num) {
                this.bonus = true;
                this.gleaming = true;
                break;
            }
        }
    }

    public int getScore() {
        return this.identifier.getType().getScore();
    }

    public boolean isGleaming() {
        return gleaming;
    }

    public void setGleaming(boolean gleaming) {
        this.gleaming = gleaming;
    }

    public EnemyTankIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public final void loadAnimations() {

        this.level.getEnemyTankSpriteSheetMap().keySet().forEach(key -> {
            this.animationManager.addAnimation(key, new Animation(
                    this.level.getEnemyTankSpriteSheetMap().get(key), 0.5,
                    0, 0, Game.TILE_SIZE, Game.TILE_SIZE, 2, Game.TILE_SIZE
            ));
        });
    }

    @Override
    public void fire() {
        Point departure = calcProjectileDeparturePosition();
        int px = departure.x;
        int py = departure.y;
        Projectile projectile = new Projectile(level, ProjectileType.ENEMY, px,
                py,
                this.identifier.getType().getProjectileSpeed(),
                this.direction);
        projectile.setDamage(this.identifier.getType().getProjectileDamage());
        if (this.identifier.getType() == EnemyTankType.ARMORED) {
            projectile.setAntiarmour(true);
        }
        this.level.getEntities().add(projectile);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        if (isAlive()) {
            if (!this.bonus && this.identifier.getType()
                    == EnemyTankType.ARMORED) {
                this.identifier.setColor(calcColorDependingOnHealth());
                updateAnimation();
            }
        } else {
            explode();
        }
    }

    protected void explodeWithGrenade() {
        super.explode();
        destroy();
    }

    @Override
    protected void explode() {
        super.explode();
        ScoreIcrementText text = new ScoreIcrementText(this.level, this.
                getScore(), getX(), getY());
        text.startInfiniteBlinking(0.2);
        int dx = (getWidth() - text.getWidth()) / 2;
        int dy = (getHeight() - text.getHeight()) / 2;
        text.setPosition(getX() + dx, getY() + dy);
        this.level.getEntityManager().addEntity(text);
        if (this.bonus) {
            createPowerUp();
        }
        destroy();
    }

    private void createPowerUp() {
        int randX = this.random.nextInt(Game.TILES_IN_WIDTH)
                * Game.HALF_TILE_SIZE;
        int randY = this.random.nextInt(Game.TILES_IN_HEIGHT)
                * Game.HALF_TILE_SIZE;
        PowerUp powerUp = new PowerUp(this.level, PowerUpType.randomType(),
                randX, randY);
        powerUp.startInfiniteBlinking(0.4);
        this.level.getEntityManager().addEntity(powerUp);
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        this.identifier.setHeading(Heading.getHeading(direction));
    }

    @Override
    public void chooseDirection() {
        List<Direction> possibleDirections = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (canMoveInDirection(dir)) {
                possibleDirections.add(dir);
            }
        }
        this.moving = !possibleDirections.isEmpty();
        /*Direction randDirection = possibleDirections.get(this.random.nextInt(
                possibleDirections.size()));*/
        this.currTarget = this.level.getPlayer().getPosition();
        Multimap<Double, Direction> distanceDirectionMap = TreeMultimap
                .create(Ordering.from(Double::compare), Ordering.arbitrary());
        for (int i = 0; i < possibleDirections.size(); ++i) {
            Direction currDirection = possibleDirections.get(i);
            int nextX = (int) (getX() + 2 * currDirection.getVx());
            int nextY = (int) (getY() + 2 * currDirection.getVy());
            Point nextPosition = new Point(nextX, nextY);
            double distance = calcDistance(nextPosition, this.currTarget);
            distanceDirectionMap.put(distance, currDirection);
        }

        Iterator<Double> distDirIterator =
                distanceDirectionMap.asMap().keySet().iterator();
        if (distDirIterator.hasNext()) {
            double key = distDirIterator.next();
            Collection<Direction> directions = distanceDirectionMap.asMap().get(
                    key);
            int dirCount = directions.size();
            int randDirNumber = this.random.nextInt(dirCount);
            if (!directions.isEmpty()) {
                Iterator<Direction> dirIterator = directions.iterator();
                Direction dir = this.direction;
                for(int i = 0; i <= randDirNumber; ++i){
                    dir = dirIterator.next();
                }
                Direction choosenDir = dir;
                setDirection(choosenDir);
            }
        }
    }

    protected double calcDistance(Point2D source, Point2D target) {
        double dx = Math.abs(source.getX() - target.getX());
        double dy = Math.abs(source.getY() - target.getY());
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected List<Tank> getOtherTanks() {
        List<Tank> otherTanks = super.getOtherTanks();
        otherTanks.add((Tank) this.level.getPlayer());
        return otherTanks;
    }

    @Override
    protected void handleCollisionWithOtherTank(Tank other) {
        super.handleCollisionWithOtherTank(other);
        setDirection(this.direction.getOpposite());
    }

    private void updateAnimation() {
        this.animationManager.setCurrentAnimation(this.identifier);
        this.animationManager.getCurrentAnimation().start(
                AnimationPlayMode.LOOP);
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);

        if (this.moving) {

            if (checkMapCollision()) {
                chooseDirection();
            }
            if (fixBounds()) {
                chooseDirection();
            }

            if (this.direction.isHorizontal()) {
                if ((int) getX() % Game.TILE_SIZE == 0) {
                    chooseDirection();
                }
            } else if (this.direction.isVertical()) {
                if ((int) getY() % Game.TILE_SIZE == 0) {
                    chooseDirection();
                }
            }
            handleCollisionsWithOtherTanks();
            move(frameTime);
        }

        updateGleamingColor(frameTime);
        updateAnimation();

    }

    private void updateGleamingColor(double frameTime) {
        if (this.gleaming) {
            this.colorPlayingTimer += frameTime;
            if (this.colorPlayingTimer >= COLOR_CHANGING_PERIOD) {
                TankColor currColor = this.identifier.getColor();
                this.identifier.setColor(currColor.next());
                this.colorPlayingTimer = 0;
            }
        }
    }

    private TankColor calcColorDependingOnHealth() {
        if (this.health < 25) {
            return TankColor.GRAY;
        } else if (this.health < 50) {
            return TankColor.YELLOW;
        } else if (this.health < 75) {
            return TankColor.RED;
        } else {
            return TankColor.GREEN;
        }
    }
}
