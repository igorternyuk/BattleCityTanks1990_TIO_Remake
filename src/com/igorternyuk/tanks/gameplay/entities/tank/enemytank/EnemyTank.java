package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

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
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 *
 * @author igor
 */
public class EnemyTank extends Tank<EnemyTankIdentifier> {

    private EnemyTankIdentifier identifier;

    public EnemyTank(LevelState level, EnemyTankType type, double x, double y,
            double speed, Direction direction) {
        super(level, EntityType.ENEMY_TANK, x, y, speed, direction);
        loadAnimations();
        this.identifier = new EnemyTankIdentifier(TankColor.YELLOW,
                Heading.NORTH, type);
        updateAnimation();
    }

    public EnemyTankIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public final void loadAnimations() {
        Map<EnemyTankIdentifier, BufferedImage> spriteSheetMap =
                EnemyTankIdentifier.getSpriteSheetMap();
        spriteSheetMap.keySet().forEach(key -> {
            this.animationManager.addAnimation(key, new Animation(
                    spriteSheetMap.get(key), 0.5,
                    0, 0, Game.TILE_SIZE, Game.TILE_SIZE, 2, Game.TILE_SIZE
            ));
        });
    }

    @Override
    public void chooseDirection() {
    }

    @Override
    public void fire() {
        this.level.getEntities().add(
                new Projectile(level, ProjectileType.ENEMY, x, y,
                        this.identifier.getType().getProjectileSpeed(),
                        this.direction));
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
        
        if(keyboardState.isKeyPressed(KeyEvent.VK_F)){
            fire();
        }
        if(this.moving){
            move(frameTime);
            fixBounds();
        }
        this.animationManager.update(frameTime);
    }

    /* @Override
    public void draw(Graphics2D g) {
        this.animationManager.draw(g, this.x, health, speed, speed);
    }*/
}
