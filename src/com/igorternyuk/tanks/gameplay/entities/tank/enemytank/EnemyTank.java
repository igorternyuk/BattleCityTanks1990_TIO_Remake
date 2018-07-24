package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUpType;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gameplay.entities.text.ScoreIcrementText;
import com.igorternyuk.tanks.gameplay.pathfinder.Pathfinder;
import com.igorternyuk.tanks.gameplay.pathfinder.Pathfinder.Spot;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap.FiringSpot;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author igor
 */
public class EnemyTank extends Tank<EnemyTankIdentifier> {

    private static final int[] BONUS_TANKS_NUMBERS = {4, 11, 18};
    private static final int TANK_DIMENSION = 2;
    private static final double COLOR_CHANGING_PERIOD = 0.1;
    private static final double TARGET_CHANGING_PERIOD = 20;
    private static final double FROZEN_TIME = 10;
    private static final double SHOOTING_PERIOD = 2;
    
    private int number;
    private EnemyTankIdentifier identifier;
    private boolean bonus = false;
    private boolean gleaming = false;
    private double gleamingTimer;
    
    private boolean movingAlongShortestPath = false;
    private List<Spot> shortestPath = new ArrayList<>();
    private Spot nextPosition;
    private Spot currTarget;
    private double targetTimer = 0;
    private List<FiringSpot> firingSpots;
    private boolean frozen = false;
    private double freezeTimer = 0;
    private boolean gotStuck = false;
    private double shootingTimer = 0;
    private boolean firingSpotReached = false;
    private final Random random = new Random();
    
    public EnemyTank(LevelState level, int number, EnemyTankType type, double x,
            double y, Direction direction) {
        super(level, EntityType.ENEMY_TANK, x, y, type.getSpeed(), direction);
        this.number = number;
        this.health = type.getHealth();

        if (checkIfBonus()) {
            destroyExistingPowerUps();
        }

        loadAnimations();

        this.identifier = new EnemyTankIdentifier(getTankColor(number, type),
                Heading.getHeading(direction), type);
        updateAnimation();
        this.firingSpots = getFiringSpotsToAttackTheEagle();
        selectRandomFiringPointToAttackEagle();
        this.moving = true;
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        updateGleamingColor(frameTime);
        executeMovementLogic(frameTime);
        updateShootingTimer(frameTime);
        updateAnimation();
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.shortestPath.forEach(spot -> spot.draw(g));
    }
    
    public void executeMovementLogic(double frameTime){
        if (this.frozen) {
            handleIfFrozen(frameTime);
            return;
        }
        
        if(this.firingSpotReached){
            return;
        }
        
        if (!this.moving && this.gotStuck) {
            checkMapCollision();
            selectRandomDirrection();
            return;
        }
        
        /*if (!this.moving && !this.firingSpotReached && this.gotStuck) {
            selectRandomDirrection();
            return;
        }*/

        if (this.moving) {
            move(frameTime);
            fixBounds();

            if (!checkCollisions(frameTime)) {
                updateTarget(frameTime);
                updateDirection();
            } else {
                this.movingAlongShortestPath = false;
            }

            checkIfFiringSpotToAttackEagleReached();
        }
    }
    
    public void freeze() {
        this.frozen = true;
    }

    private void handleIfFrozen(double frameTime) {
        this.freezeTimer += frameTime;
        if (this.freezeTimer >= FROZEN_TIME) {
            this.freezeTimer = 0;
            this.frozen = false;
        }
    }
    
    public EnemyTankType getType() {
        return this.identifier.getType();
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
    
    private boolean checkCollisions(double frameTime) {

        if (checkMapCollision()) {
            selectRandomDirrection();
            move(frameTime);
            return true;
        }

        if (handleCollisionsWithSplashes()) {
            selectRandomDirrection();
            move(frameTime);
            return true;
        }

        return handleCollisionsWithOtherTanks(frameTime);
    }

    private void createPowerUp() {
        int randX = this.random.nextInt(Game.TILES_IN_WIDTH - 1)
                * Game.HALF_TILE_SIZE;
        int randY = this.random.nextInt(Game.TILES_IN_HEIGHT - 1)
                * Game.HALF_TILE_SIZE;
        PowerUp powerUp = new PowerUp(this.level, PowerUpType.randomType(),
                randX, randY);
        powerUp.startInfiniteBlinking(0.4);
        this.level.getEntityManager().addEntity(powerUp);
    }

    @Override
    protected List<Tank> getOtherTanks() {
        List<Tank> otherTanks = super.getOtherTanks();
        otherTanks.add((Tank) this.level.getPlayer());
        return otherTanks;
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

    private void updateDirection() {
        if (this.movingAlongShortestPath) {
            moveAlongShortestPath();
        } else {
            if (checkIfNeedRecalculateShortestPath()) {
                selectShortestPathDirection();
            }
        }
    }
    
    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        this.identifier.setHeading(Heading.getHeading(direction));
    }
    
    private List<Direction> getAllPossibleDirections() {
        List<Direction> allPossibleDirections = new ArrayList<>(Direction.
                values().length);
        for (Direction dir : Direction.values()) {
            if (canMoveInDirection(dir)) {
                allPossibleDirections.add(dir);
            }
        }
        return allPossibleDirections;
    }

    private void selectRandomDirrection() {
        List<Direction> allPossibleDirections = getAllPossibleDirections();
        
        if (allPossibleDirections.isEmpty()) {
            this.gotStuck = true;
            System.out.println("Got stuck!!!");
            
            this.moving = false;
            return;
        } else {
            this.gotStuck = false;
            System.out.println("No stuck!!!");
        }
        int rand = this.random.nextInt(allPossibleDirections.size());
        setDirection(allPossibleDirections.get(rand));
        this.moving = true;
    }

    private void selectShortestPathDirection() {

        if (this.currTarget == null) {
            selectRandomDirrection();
            return;
        }

        Pathfinder pathfinder = new Pathfinder(this.level.getTileMap());
        if (pathfinder.calcPath(getCurrentSpot(), this.currTarget,
                TANK_DIMENSION)) {
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
    
    private boolean checkIfNextPositionReached() {
        return this.movingAlongShortestPath
                && getCurrentSpot().equals(this.nextPosition);
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
        /*if (this.currTarget.distanceManhattan(playerSpot)
                > MIN_TARGET_POSITION_CHANGE_TO_RECALCULATE_PATH) {
            this.currTarget = playerSpot;
            this.movingAlongShortestPath = false;
        }*/
        
        this.currTarget = playerSpot;
        this.movingAlongShortestPath = false;
    }

    private void targetEagle() {
        boolean isCurrTargetEagle = this.firingSpots.stream().anyMatch(
                firingSpot -> {
            return firingSpot.getSpot().equals(this.currTarget);
        });

        if (!isCurrTargetEagle) {
            selectRandomFiringPointToAttackEagle();
            this.movingAlongShortestPath = false;
        }
    }
    
    private void updateShootingTimer(double frameTime) {
        this.shootingTimer += frameTime;
        if (this.shootingTimer >= SHOOTING_PERIOD) {
            this.shootingTimer = 0;
            if (this.firingSpotReached || isFireLineFreeOfPartnerTanks()) {
                fire();
            }
        }
    }

    @Override
    public void fire() {
        Point departure = calcProjectileDeparturePosition();
        int px = departure.x;
        int py = departure.y;
        Projectile projectile = new Projectile(level, this.getType().
                getProjectileType(), px, py,
                this.identifier.getType().getProjectileSpeed(),
                this.direction);
        projectile.setDamage(this.identifier.getType().getProjectileDamage());
        if (this.identifier.getType() == EnemyTankType.ARMORED) {
            projectile.setAntiarmour(true);
        }
        this.level.getEntityManager().addEntity(projectile);
        this.canFire = false;
    }
    
    private void checkIfFiringSpotToAttackEagleReached() {
        for (int i = 0; i < this.firingSpots.size(); ++i) {
            if (firingSpots.get(i).getSpot().equals(getCurrentSpot())) {
                this.firingSpotReached = true;
                setDirection(firingSpots.get(i).getFireDirection());
                this.moving = false;
            }
        }
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
    
    private List<FiringSpot> getFiringSpotsToAttackTheEagle() {
        return this.level.getTileMap().getFiringSpots();
    }

    private void selectRandomFiringPointToAttackEagle() {
        int rand = this.random.nextInt(this.firingSpots.size());
        this.currTarget = this.firingSpots.get(rand).getSpot();
    }
    
    private boolean isFireLineFreeOfPartnerTanks() {
        List<Entity> partners = this.level.getEntityManager().getEntitiesByType(
                EntityType.ENEMY_TANK);
        Rectangle damageArea = calcDamageArea();
        return partners.stream().noneMatch(entity -> entity.getBoundingRect().
                intersects(damageArea));
    }

    private Rectangle calcDamageArea() {
        switch (this.direction) {
            case NORTH:
                return new Rectangle((int) left() - getWidth(),
                        0,
                        3 * getWidth(),
                        (int) top());
            case SOUTH:
                return new Rectangle((int) left() - getWidth(),
                        (int) bottom(),
                        3 * getWidth(),
                        this.level.getMapHeight() - (int) top());
            case EAST:
                return new Rectangle((int) right(),
                        (int) top() - getHeight(),
                        this.level.getMapWidth() - (int) left(),
                        3 * getHeight());
            default:
                return new Rectangle(0,
                        (int) top() - getHeight(),
                        (int) left(),
                        3 * getHeight());
        }
    }

    private void destroyExistingPowerUps() {
        this.level.getEntityManager().getEntitiesByType(EntityType.POWER_UP).
                forEach(powerUp -> powerUp.destroy());
    }

    private TankColor getTankColor(int number, EnemyTankType tankType) {

        TankColor color;

        if (tankType == EnemyTankType.ARMORED) {
            color = TankColor.GREEN;
        } else {
            if (number % 2 == 0) {
                color = TankColor.GREEN;
            } else {
                color = TankColor.GRAY;
            }
        }

        return color;
    }
    
    private void updateGleamingColor(double frameTime) {
        if (this.gleaming) {
            this.gleamingTimer += frameTime;
            if (this.gleamingTimer >= COLOR_CHANGING_PERIOD) {
                TankColor currColor = this.identifier.getColor();
                this.identifier.setColor(currColor.next());
                this.gleamingTimer = 0;
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
    
    private boolean checkIfBonus() {
        for (int num : BONUS_TANKS_NUMBERS) {
            if (this.number == num) {
                this.bonus = true;
                this.gleaming = true;
                return true;
            }
        }
        return false;
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
}
