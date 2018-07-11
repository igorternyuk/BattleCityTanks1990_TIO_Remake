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
import java.awt.Point;

/**
 *
 * @author igor
 */
public class EnemyTank extends Tank<EnemyTankIdentifier> {
    private static final double COLOR_CHANGING_PERIOD = 0.1;
    private EnemyTankIdentifier identifier;
    private boolean gleaming = false;
    private double colorPlayingTimer;
    

    public EnemyTank(LevelState level, EnemyTankType type, double x, double y,
            Direction direction) {
        super(level, EntityType.ENEMY_TANK, x, y, type.getSpeed(), direction);
        loadAnimations();
        this.identifier = new EnemyTankIdentifier(TankColor.YELLOW,
                Heading.getHeading(direction), type);
        updateAnimation();
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
    public void chooseDirection() {
    }

    @Override
    public void fire() {
        Point departure = calcPointOfProjectileDeparture();
        int px = departure.x;
        int py = departure.y;
        this.level.getEntities().add(
                new Projectile(level, ProjectileType.ENEMY, px, py,
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
        if(this.gleaming){
            //System.out.println("this.colorPlayingTimer = " + this.colorPlayingTimer);
            this.colorPlayingTimer += frameTime;
            if(this.colorPlayingTimer >= COLOR_CHANGING_PERIOD){
                TankColor currColor = this.identifier.getColor();
                this.identifier.setColor(currColor.next());
                this.colorPlayingTimer = 0;
            }
        }
        updateAnimation();
        
    }

    /* @Override
    public void draw(Graphics2D g) {
        this.animationManager.draw(g, this.x, health, speed, speed);
    }*/
}
