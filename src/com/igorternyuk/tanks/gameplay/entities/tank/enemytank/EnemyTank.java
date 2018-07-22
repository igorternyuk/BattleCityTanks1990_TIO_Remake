package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUpType;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gameplay.entities.text.ScoreIcrementText;
import com.igorternyuk.tanks.gameplay.pathfinder.Pathfinder;
import com.igorternyuk.tanks.gameplay.pathfinder.Pathfinder.Spot;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author igor
 */
public class EnemyTank extends Tank<EnemyTankIdentifier> {

    private static final double COLOR_CHANGING_PERIOD = 0.1;
    private static final double TARGET_CHANGING_PERIOD = 10;
    private static final int MIN_TARGET_POSITION_CHANGE_TO_RECALCULATE_PATH = 10
            * Game.HALF_TILE_SIZE;
    private static final double FROZEN_TIME = 10;
    private static final int[] BONUS_TANKS_NUMBERS = {4, 11, 18};
    private int number;
    private EnemyTankIdentifier identifier;
    private boolean bonus = false;
    private boolean gleaming = false;
    private double colorPlayingTimer;
    private final Random random = new Random();
    private boolean movingAlongShortestPath = false;
    private Spot currTarget;
    private List<Spot> shortestPath = new ArrayList<>();
    private Spot nextPosition;
    private double targetTimer = 0;
    private List<Spot> fireSpots;
    private boolean frozen = false;
    private double freezeTimer = 0;
    private boolean gotStuck = false;

    public EnemyTank(LevelState level, int number, EnemyTankType type, double x,
            double y, Direction direction) {
        super(level, EntityType.ENEMY_TANK, x, y, type.getSpeed(), direction);
        this.number = number;
        this.health = type.getHealth();
        
        checkIfBonus();
        
        if (this.bonus) {
            destroyExistingBonuses();
        }
        
        loadAnimations();

        TankColor color;

        if (type == EnemyTankType.ARMORED) {
            color = calcColorDependingOnHealth();
        } else {
            if (this.number % 2 == 0) {
                color = TankColor.GREEN;
            } else {
                color = TankColor.GRAY;
            }
        }
        this.identifier = new EnemyTankIdentifier(color,
                Heading.getHeading(direction), type);
        updateAnimation();
        this.fireSpots = getFireSpotsToAttackTheEagle();
        selectRandomFirePointToAttackEagle();
        this.moving = true;
    }

    private void destroyExistingBonuses() {
        this.level.getEntityManager().getEntitiesByType(EntityType.BONUS).
                forEach(powerUp -> powerUp.destroy());
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        if (this.frozen) {
            handleIfFrozen(frameTime);
            return;
        }
        if (!this.moving) {
            if (this.gotStuck) {
                handleCollisions();
            }
            return;
        }
        updateTarget(frameTime);
        updateDirection();
        move(frameTime);
        handleCollisions();
        updateGleamingColor(frameTime);
        updateAnimation();
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        //this.shortestPath.forEach(spot -> spot.draw(g));
    }

    private void handleIfFrozen(double frameTime) {
        this.freezeTimer += frameTime;
        if (this.freezeTimer >= FROZEN_TIME) {
            this.freezeTimer = 0;
            this.frozen = false;
        }
    }

    public void freeze() {
        this.frozen = true;
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

    public void explodeWithGrenade() {
        super.explode();
        destroy();
    }

    @Override
    public void explode() {
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

    private Spot getCurrentSpot() {
        int currRow = (int) getY() / Game.HALF_TILE_SIZE;
        int currCol = (int) getX() / Game.HALF_TILE_SIZE;
        Spot currSpot = new Spot(currRow, currCol, true);
        return currSpot;
    }

    private Spot getPlayerSpot() {
        Player player = this.level.getPlayer();
        int targetCol = (int) player.getX() / Game.HALF_TILE_SIZE;
        int targetRow = (int) player.getY() / Game.HALF_TILE_SIZE;
        return new Spot(targetRow, targetCol, true);
    }

    private List<Spot> getFireSpotsToAttackTheEagle() {
        return this.level.getTileMap().getFireSpots();
    }

    private boolean checkIfNextPositionReached() {
        return this.movingAlongShortestPath
                && getCurrentSpot().equals(this.nextPosition);
    }

    private void selectRandomFirePointToAttackEagle() {
        int rand = this.random.nextInt(this.fireSpots.size());
        this.currTarget = this.fireSpots.get(rand);
    }

    private void updateTarget(double frameTime) {
        this.targetTimer += frameTime;
        if (this.targetTimer < TARGET_CHANGING_PERIOD) {
            if (this.number % 2 == 0) {
                targetEagle();
            } else {
                targetPlayer();
            }
        } else if (this.targetTimer < 2 * TARGET_CHANGING_PERIOD) {
            if (this.number % 2 == 0) {
                targetPlayer();
            } else {
                targetEagle();
            }
        } else {
            this.targetTimer = 0;
        }
    }

    private void targetPlayer() {
        Spot playerSpot = getPlayerSpot();
        if (this.currTarget.distanceManhattan(playerSpot)
                > MIN_TARGET_POSITION_CHANGE_TO_RECALCULATE_PATH) {
            this.currTarget = playerSpot;
            this.movingAlongShortestPath = false;
        }
    }

    private void targetEagle() {
        if (!fireSpots.contains(this.currTarget)) {
            selectRandomFirePointToAttackEagle();
            this.movingAlongShortestPath = false;
        }
    }

    private void updateDirection() {
        if (this.movingAlongShortestPath) {
            moveAlongShortestPath();
        } else {
            if (checkIfNeedRecalculateShortestPath()) {
                selectShortestPathDirection();
            }
        }
    }

    private void selectShortestPathDirection() {

        if (this.currTarget == null) {
            selectRandomDirrection();
            return;
        }

        Pathfinder pathfinder = new Pathfinder(this.level.getTileMap());
        if (pathfinder.calcPath(getCurrentSpot(), this.currTarget, 2)) {
            this.shortestPath = pathfinder.getOptimalPath();
            if (!this.shortestPath.isEmpty()) {
                this.movingAlongShortestPath = true;
                this.nextPosition = this.shortestPath.get(0);
                Direction selectedDir = this.nextPosition.getDirFromPrev();
                setDirection(selectedDir);
            } else {
                selectRandomDirrection();
            }

        } else {
            selectRandomDirrection();
        }
    }

    private void selectRandomDirrection() {
        List<Direction> allPossibleDirections = new ArrayList<>(4);
        for (Direction dir : Direction.values()) {
            if (canMoveInDirection(dir)) {
                allPossibleDirections.add(dir);
            }
        }

        if (allPossibleDirections.isEmpty()) {
            System.out.println("No possible directions!The tank got stuck!");
            this.gotStuck = false;
            this.moving = false;
            return;
        }
        int rand = this.random.nextInt(allPossibleDirections.size());
        setDirection(Direction.values()[rand]);
        this.moving = true;
    }

    private void moveAlongShortestPath() {
        if (checkIfNextPositionReached()) {
            this.shortestPath.remove(this.nextPosition);
            if (!this.shortestPath.isEmpty()) {
                this.nextPosition = this.shortestPath.get(0);
                setDirection(this.nextPosition.getDirFromPrev());
            } else {
                selectRandomDirrection();
                this.movingAlongShortestPath = false;
            }
        }
    }

    private boolean checkIfNeedRecalculateShortestPath() {
        if (this.direction.isHorizontal()) {
            if ((int) getX() % Game.TILE_SIZE == 0) {
                return true;
            }
        } else if (this.direction.isVertical()) {
            if ((int) getY() % Game.TILE_SIZE == 0) {
                return true;
            }
        }
        return false;
    }

    private void handleCollisions() {
        boolean collidedMap = checkMapCollision();
        boolean collidedOtherTanks = handleCollisionsWithOtherTanks();
        boolean boundsFixed = fixBounds();
        if (collidedMap || collidedOtherTanks || boundsFixed) {
            selectRandomDirrection();
            this.movingAlongShortestPath = false;
        }
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
