package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.graphics.animations.AnimationPlayMode;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Point;
import java.awt.event.KeyEvent;

/**
 *
 * @author igor
 */
public class Player extends Tank {

    private PlayerTankIdentifier identifier;

    public Player(LevelState level, PlayerTankType type, double x, double y,
            Direction direction) {
        super(level, EntityType.PLAYER_TANK, x, y, type.getSpeed(), direction);
        loadAnimations();
        this.identifier = new PlayerTankIdentifier(TankColor.YELLOW,
                Heading.getHeading(direction), type);
        updateAnimation();
    }

    public PlayerTankIdentifier getIdentifier() {
        return this.identifier;
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
    public void chooseDirection() {
    }

    @Override
    public void fire() {
        if (!this.canFire) {
            return;
        }
        Point departure = calcPointOfProjectileDeparture();
        this.level.getEntities().add(
                new Projectile(level, ProjectileType.ENEMY, departure.x,
                        departure.y,
                        this.identifier.getType().getProjectileSpeed(),
                        this.direction));
        this.canFire = false;
    }

    private void updateAnimation() {
        this.animationManager.setCurrentAnimation(this.identifier);
        this.animationManager.getCurrentAnimation().start(
                AnimationPlayMode.LOOP);
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        this.moving = false;
        if (keyboardState.isKeyPressed(KeyEvent.VK_A)
                || keyboardState.isKeyPressed(KeyEvent.VK_LEFT)) {
            setDirection(Direction.WEST);
            this.moving = true;
            this.identifier.setHeading(Heading.WEST);
            updateAnimation();
        } else if (keyboardState.isKeyPressed(KeyEvent.VK_D)
                || keyboardState.isKeyPressed(KeyEvent.VK_RIGHT)) {
            setDirection(Direction.EAST);
            this.moving = true;
            this.identifier.setHeading(Heading.EAST);
            updateAnimation();
        } else if (keyboardState.isKeyPressed(KeyEvent.VK_W)
                || keyboardState.isKeyPressed(KeyEvent.VK_UP)) {
            setDirection(Direction.NORTH);
            this.moving = true;
            this.identifier.setHeading(Heading.NORTH);
            updateAnimation();
        } else if (keyboardState.isKeyPressed(KeyEvent.VK_S)
                || keyboardState.isKeyPressed(KeyEvent.VK_DOWN)) {
            setDirection(Direction.SOUTH);
            this.moving = true;
            this.identifier.setHeading(Heading.SOUTH);
            updateAnimation();
        } else {
            this.moving = false;
            this.animationManager.getCurrentAnimation().stop();
        }

        if (keyboardState.isKeyPressed(KeyEvent.VK_F)) {
            fire();
        }
        if (this.moving) {
            move(frameTime);
            fixBounds();
        }
        this.animationManager.update(frameTime);
    }
}
