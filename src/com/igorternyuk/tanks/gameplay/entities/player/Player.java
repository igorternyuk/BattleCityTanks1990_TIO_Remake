package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import com.igorternyuk.tanks.gameplay.entities.tank.protection.Protection;
import com.igorternyuk.tanks.gameplay.entities.tank.protection.ProtectionType;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author igor
 */
public class Player extends Tank {

    private static final double PROTECTION_TIME = 23;
    private static final double SLIDING_DURATION = 2;
    private PlayerTankIdentifier identifier;
    private double respawnX, respawnY;
    private boolean hasProtection = false;
    private double protectionTimer;
    private int lives = 5;
    private double lastShootTimer;
    private final double shotDelay = 0.15;
    private boolean onIce = false;
    private boolean sliding = false;
    private double slidingTimer = 0;
    
    private PlayerStatistics playerStatistics = new PlayerStatistics(this);

    public Player(LevelState level, PlayerTankType type, double x, double y,
            Direction direction) {
        super(level, EntityType.PLAYER_TANK, x, y, type.getSpeed(), direction);
        this.respawnX = x;
        this.respawnY = y;
        loadAnimations();
        this.identifier = new PlayerTankIdentifier(TankColor.YELLOW,
                Heading.getHeading(direction), type);
        setProperAnimation();
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        
        updateSlidingTimer(frameTime);
        
        if(!this.sliding){
            this.moving = false;
            handleUserInput(keyboardState);
        }
        
        if (this.moving) {
            move(frameTime);
            checkMapCollision();
            checkCollisionsWithSplashes();
            fixBounds();
        }
        super.update(keyboardState, frameTime);
        updateProtectionTimer(frameTime);
        updateShootingTimer(frameTime);
    }
    
    private void updateSlidingTimer(double frameTime){
        checkIfIce();
        if(this.sliding){
            this.slidingTimer += frameTime;
            if(this.slidingTimer >= SLIDING_DURATION){
                this.slidingTimer = 0;
                this.sliding = false;
            }
        } else {
            this.slidingTimer = 0;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.playerStatistics.draw(g);
    }

    public void setSliding(boolean sliding) {
        this.sliding = this.onIce && sliding;
    }

    public PlayerTankIdentifier getIdentifier() {
        return this.identifier;
    }

    public PlayerStatistics getPlayerStatistics() {
        return this.playerStatistics;
    }

    public void registerKilledTank(EnemyTank enemyTank) {
        this.playerStatistics.addKilledTank(enemyTank);
    }

    public void collectPowerUp(PowerUp powerup) {
        this.playerStatistics.addPowerUp(powerup);
    }

    public int getScore() {
        return this.playerStatistics.getScore();
    }

    public void promote() {
        PlayerTankType currType = this.identifier.getType();
        if (currType == PlayerTankType.ARMORED) {
            return;
        }
        setTankType(currType.next());
    }

    public void promoteToHeavy() {
        setTankType(PlayerTankType.ARMORED);
    }

    private void setTankType(PlayerTankType tankType) {
        this.identifier.setType(tankType);
        this.speed = tankType.getSpeed();
    }

    public void addProtection() {
        Protection protection = new Protection(this.level,
                ProtectionType.REGULAR, 0, 0);
        attachChild(protection);
        this.hasProtection = true;
    }

    public void gainExtraLife() {
        ++this.lives;
    }

    public int getLives() {
        return this.lives;
    }

    @Override
    public boolean isAlive() {
        return this.lives > 0;
    }

    @Override
    public final void loadAnimations() {
        this.level.getPlayerSpriteSheetMap().keySet().forEach(key -> {
            this.animationManager.addAnimation(key, new Animation(
                    this.level.getPlayerSpriteSheetMap().get(key), 0.5,
                    0, 0, Game.TILE_SIZE, Game.TILE_SIZE, 2, Game.TILE_SIZE
            ));
        });
    }

    @Override
    public void fire() {
        if (!this.canFire) {
            return;
        }
        Point departure = calcProjectileDeparturePosition();
        Projectile projectile = new Projectile(level, ProjectileType.PLAYER,
                departure.x, departure.y,
                this.identifier.getType().getProjectileSpeed(),
                this.direction);
        projectile.setDamage(this.identifier.getType().getProjectileDamage());
        if (this.identifier.getType() == PlayerTankType.ARMORED) {
            projectile.setAntiarmour(true);
        }
        this.level.getEntityManager().addEntity(projectile);
        this.canFire = false;
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        if (this.health <= 0) {
            explode();
        }
    }

    @Override
    public void explode() {
        super.explode(ExplosionType.BIG);
        --this.lives;
        if (isAlive()) {
            respawn();
        }
    }

    protected void respawn() {
        this.health = 100;
        setPosition(this.respawnX, this.respawnY);
        addProtection();
        this.identifier.setType(PlayerTankType.BASIC);
    }

    public void reset() {
        this.health = 100;
        setPosition(this.respawnX, this.respawnY);
        this.playerStatistics.reset();
        addProtection();
    }

    private void setProperAnimation() {
        this.animationManager.setCurrentAnimation(this.identifier);
        this.animationManager.getCurrentAnimation().start(
                AnimationPlayMode.LOOP);
    }

    private void handleUserInput(KeyboardState keyboardState) {

        if (keyboardState.isKeyPressed(KeyEvent.VK_A)
                || keyboardState.isKeyPressed(KeyEvent.VK_LEFT)) {
            setDirection(Direction.WEST);
            this.moving = true;
            this.identifier.setHeading(Heading.WEST);
            setProperAnimation();
        } else if (keyboardState.isKeyPressed(KeyEvent.VK_D)
                || keyboardState.isKeyPressed(KeyEvent.VK_RIGHT)) {
            setDirection(Direction.EAST);
            this.moving = true;
            this.identifier.setHeading(Heading.EAST);
            setProperAnimation();
        } else if (keyboardState.isKeyPressed(KeyEvent.VK_W)
                || keyboardState.isKeyPressed(KeyEvent.VK_UP)) {
            setDirection(Direction.NORTH);
            this.moving = true;
            this.identifier.setHeading(Heading.NORTH);
            setProperAnimation();
        } else if (keyboardState.isKeyPressed(KeyEvent.VK_S)
                || keyboardState.isKeyPressed(KeyEvent.VK_DOWN)) {
            setDirection(Direction.SOUTH);
            this.moving = true;
            this.identifier.setHeading(Heading.SOUTH);
            setProperAnimation();
        } else {
            this.moving = false;
            this.animationManager.getCurrentAnimation().stop();
        }

        if (keyboardState.isKeyPressed(KeyEvent.VK_F)) {
            fire();
        }
    }

    private void updateProtectionTimer(double frameTime) {
        if (this.hasProtection) {
            this.protectionTimer += frameTime;
            //System.out.println("this.protectionTimer = " + this.protectionTimer);
            if (this.protectionTimer >= PROTECTION_TIME) {
                this.protectionTimer = 0;
                this.hasProtection = false;
                List<Entity> protections = this.children.stream().filter(
                        child -> child.getEntityType()
                        == EntityType.PROTECTION).collect(Collectors.toList());
                protections.forEach(p -> detachChild(p));
            }
        }
    }

    private void updateShootingTimer(double frameTime) {
        if (this.identifier.getType() == PlayerTankType.MIDDLE && !this.canFire) {
            this.lastShootTimer += frameTime;
            if (this.lastShootTimer >= this.shotDelay) {
                this.lastShootTimer = 0;
                this.canFire = true;
            }
        }
    }
    
    private void checkIfIce(){
        TileMap tileMap = this.level.getTileMap();
        this.onIce = tileMap.checkIfOnTheIce(this);
        if(!this.onIce){
            this.sliding = false;
        }
    }
}
