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
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

    private static final double RESPAWN_ROTECTION_DURATION = 5;
    private static final double SLIDING_DURATION = 0.25;
    private static final double SHOT_DELAY = 0.15;

    private PlayerIdentifier id;
    private PlayerTankIdentifier tankId;
    private double respawnX, respawnY;
    private boolean hasProtection = false;
    private double protectionTimer;
    private double protectionTime = 0;
    private int lives = 2;
    private double lastShootTimer;
    private boolean onIce = false;
    private boolean sliding = false;
    private double slidingTimer = 0;
    private int maxHealth = 100;
    private int collecredGunCount = 0;
    private Font font;
    private PlayerStatistics statistics = new PlayerStatistics();

    public Player(LevelState level, int id, PlayerTankType type, double x,
            double y,
            Direction direction) {
        super(level, EntityType.PLAYER_TANK, x, y, type.getSpeed(), direction);
        this.id = PlayerIdentifier.getFromNumeric(id);
        this.respawnX = x;
        this.respawnY = y;
        addProtection(RESPAWN_ROTECTION_DURATION);
        loadAnimations();
        TankColor color = (id == 1) ? TankColor.YELLOW : TankColor.GREEN;
        this.tankId = new PlayerTankIdentifier(color,
                Heading.getHeading(direction), type);
        setProperAnimation();
        this.font = ResourceManager.getInstance().getFont(
                FontIdentifier.BATTLE_CITY).deriveFont(Font.BOLD, 14);
    }

    public PlayerIdentifier getId() {
        return this.id;
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {

        if (this.frozen) {
            handleIfFrozen(frameTime);
            return;
        }

        updateSlidingTimer(frameTime);
        handleUserInput(keyboardState);

        if (this.moving) {
            move(frameTime);
            if (checkMapCollision()) {
                fitToTiles();
            }
            handleCollisionsWithSplashes();
            //handleCollisionsWithOtherTanks(frameTime);
            fixBounds();
        }
        super.update(keyboardState, frameTime);
        updateProtectionTimer(frameTime);
        updateShootingTimer(frameTime);
    }

    private void updateSlidingTimer(double frameTime) {
        checkIfIce();
        if (this.sliding) {
            this.slidingTimer += frameTime;
            if (this.slidingTimer >= SLIDING_DURATION) {
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
        drawPlayerData(g);
    }

    private void drawPlayerData(Graphics2D g) {
        int gameFieldBottom = Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT;
        g.setColor(Color.white);
        g.fillRect(0, gameFieldBottom, Game.WIDTH, 3);
        
        Color currTankColor = this.id.getTankColor().getColor();
        g.setColor(currTankColor);
        g.setFont(this.font);
        int dy = (this.id.getId() - 1) * Game.TILE_SIZE * 2;
        g.drawString("SCORE" + this.id.getId() + ": " + this.statistics.
                getTotalScore(), 5, gameFieldBottom + 3 * Game.HALF_TILE_SIZE
                + dy);

        g.drawString("HEALTH" + this.id.getId(), Game.WIDTH / 2, gameFieldBottom
                + 3 * Game.HALF_TILE_SIZE + dy);
        g.fillRect(380, gameFieldBottom + Game.HALF_TILE_SIZE + dy,
                (int) (this.health * 5 * Game.TILE_SIZE / this.maxHealth),
                Game.TILE_SIZE
        );
        g.setColor(currTankColor.darker());
        g.setStroke(new BasicStroke(3));
        for (int i = 0; i < 5; ++i) {
            g.drawRect(380 + i * Game.TILE_SIZE, gameFieldBottom
                    + Game.HALF_TILE_SIZE + dy, Game.TILE_SIZE, Game.TILE_SIZE);
        }
        g.setStroke(new BasicStroke(1));
    }

    public void setSliding(boolean sliding) {
        this.sliding = this.onIce && sliding;
    }

    public PlayerTankIdentifier getTankId() {
        return this.tankId;
    }

    public PlayerStatistics getStatistics() {
        return this.statistics;
    }

    public void registerKilledTank(EnemyTank enemyTank) {
        this.statistics.addKilledTank(enemyTank);
    }

    public void collectPowerUp(PowerUp powerup) {
        this.statistics.addPowerUp(powerup);
    }

    public int getTotalScore() {
        return this.statistics.getTotalScore();
    }

    @Override
    public void promote() {
        PlayerTankType currType = this.tankId.getType();
        if (currType == PlayerTankType.ARMORED) {
            return;
        }
        setTankType(currType.next());
    }

    @Override
    public void promoteToHeavy() {
        setTankType(PlayerTankType.ARMORED);
        this.health = 200;
        this.maxHealth = 200;
        ++this.collecredGunCount;
        if (this.collecredGunCount >= 2) {
            this.canClearBushes = true;
        }
    }

    private void setTankType(PlayerTankType tankType) {
        this.tankId.setType(tankType);
        this.speed = tankType.getSpeed();
    }

    public final void addProtection(double duration) {
        this.protectionTime = duration;
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
                this.tankId.getType().getProjectileSpeed(),
                this.direction);
        projectile.setDamage(this.tankId.getType().getProjectileDamage());
        projectile.setOwnerId(this.id.getId());
        if (this.tankId.getType() == PlayerTankType.ARMORED) {
            projectile.setAntiarmour(true);
        }

        projectile.setBushCrearing(this.canClearBushes);
        this.level.getEntityManager().addEntity(projectile);
        this.canFire = false;
        ResourceManager.getInstance().getAudio(AudioIdentifier.SHOT).play();
    }

    @Override
    public void hit(int damage) {
        if (!this.hasProtection) {
            super.hit(damage);
            if (this.health <= 0) {
                explode();
            }
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
        this.maxHealth = 100;
        this.canTraverseWater = false;
        this.canClearBushes = false;
        this.collecredGunCount = 0;
        setPosition(this.respawnX, this.respawnY);
        addProtection(RESPAWN_ROTECTION_DURATION);
        this.tankId.setType(PlayerTankType.BASIC);
    }

    public void reset() {
        this.health = 100;
        setPosition(this.respawnX, this.respawnY);
        addProtection(RESPAWN_ROTECTION_DURATION);
        this.statistics.resetToNextStage();
    }

    private void setProperAnimation() {
        this.animationManager.setCurrentAnimation(this.tankId);
        this.animationManager.getCurrentAnimation().start(
                AnimationPlayMode.LOOP);
    }

    private void steer(KeyboardState keyboardState) {
        this.moving = false;
        boolean turnWest = (this.id == PlayerIdentifier.FIRST
                && keyboardState.isKeyPressed(KeyEvent.VK_LEFT))
                || (this.id == PlayerIdentifier.SECOND
                && keyboardState.isKeyPressed(KeyEvent.VK_A));

        boolean turnEast = (this.id == PlayerIdentifier.FIRST
                && keyboardState.isKeyPressed(KeyEvent.VK_RIGHT))
                || (this.id == PlayerIdentifier.SECOND
                && keyboardState.isKeyPressed(KeyEvent.VK_D));

        boolean turnNorth = (this.id == PlayerIdentifier.FIRST
                && keyboardState.isKeyPressed(KeyEvent.VK_UP))
                || (this.id == PlayerIdentifier.SECOND
                && keyboardState.isKeyPressed(KeyEvent.VK_W));

        boolean turnSouth = (this.id == PlayerIdentifier.FIRST
                && keyboardState.isKeyPressed(KeyEvent.VK_DOWN))
                || (this.id == PlayerIdentifier.SECOND
                && keyboardState.isKeyPressed(KeyEvent.VK_S));

        if (turnWest) {
            turn(Direction.WEST);
        } else if (turnEast) {
            turn(Direction.EAST);
        } else if (turnNorth) {
            turn(Direction.NORTH);
        } else if (turnSouth) {
            turn(Direction.SOUTH);
        } else {
            stop();
        }
    }

    private void turn(Direction direcion) {
        setDirection(direcion);
        this.moving = true;
        this.tankId.setHeading(Heading.getHeading(direction));
        setProperAnimation();
    }

    private void stop() {
        this.moving = false;
        this.animationManager.getCurrentAnimation().stop();
    }

    private void handleUserInput(KeyboardState keyboardState) {

        if ((this.id == PlayerIdentifier.FIRST
                && keyboardState.isKeyPressed(KeyEvent.VK_F))
                || (this.id == PlayerIdentifier.SECOND
                && keyboardState.isKeyPressed(KeyEvent.VK_E))) {
            fire();
        }

        boolean canSteer = false;

        if (this.sliding) {
            if (!this.moving) {
                canSteer = true;
            }
        } else {
            canSteer = true;
        }

        if (canSteer) {
            steer(keyboardState);
        }
    }

    private void updateProtectionTimer(double frameTime) {
        if (this.hasProtection) {
            this.protectionTimer += frameTime;
            if (this.protectionTimer >= protectionTime) {
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
        if (this.tankId.getType() == PlayerTankType.MIDDLE && !this.canFire) {
            this.lastShootTimer += frameTime;
            if (this.lastShootTimer >= SHOT_DELAY) {
                this.lastShootTimer = 0;
                this.canFire = true;
            }
        }
    }

    private void checkIfIce() {
        TileMap tileMap = this.level.getTileMap();
        this.onIce = tileMap.checkIfOnTheIce(this);
        if (!this.onIce) {
            this.sliding = false;
        }
    }

    @Override
    public TankColor getTankColor() {
        return this.tankId.getColor();
    }
}
