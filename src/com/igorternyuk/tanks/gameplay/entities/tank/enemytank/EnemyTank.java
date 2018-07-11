package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankType;
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
    }

    private void checkIfBonus() {
        for(int num: BONUS_TANKS_NUMBERS){
            if(this.number == num){
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
        Point departure = calcPointOfProjectileDeparture();
        int px = departure.x;
        int py = departure.y;
        Projectile projectile = new Projectile(level, ProjectileType.ENEMY, px, py,
                        this.identifier.getType().getProjectileSpeed(),
                        this.direction);
        projectile.setDamage(this.identifier.getType().getProjectileDamage());
        if(this.identifier.getType() == EnemyTankType.HEAVY){
            projectile.setAntiarmour(true);
        }
        this.level.getEntities().add(projectile);
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        if(isAlive()){
            if(!this.bonus && this.identifier.getType() == EnemyTankType.HEAVY){
                this.identifier.setColor(calcColorDependingOnHealth());
                updateAnimation();
            }
        } else {
            explode();
        }
    }

    @Override
    protected void explode() {
        super.explode();
        ScoreIcrementText text = new ScoreIcrementText(this.level, this.
                getScore(), this.x, this.y);
        text.startInfiniteBlinking(0.2);
        int dx = (getWidth() - text.getWidth()) / 2;
        int dy = (getHeight()- text.getHeight()) / 2;
        text.setPosition(this.x + dx, this.y + dy);
        this.level.getEntityManager().addEntity(text);
        destroy();
    }

    @Override
    public void chooseDirection() {
    }

    private void updateAnimation() {
        this.animationManager.setCurrentAnimation(this.identifier);
        this.animationManager.getCurrentAnimation().start(
                AnimationPlayMode.LOOP);
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        if (this.gleaming) {
            //System.out.println("this.colorPlayingTimer = " + this.colorPlayingTimer);
            this.colorPlayingTimer += frameTime;
            if (this.colorPlayingTimer >= COLOR_CHANGING_PERIOD) {
                TankColor currColor = this.identifier.getColor();
                this.identifier.setColor(currColor.next());
                this.colorPlayingTimer = 0;
            }
        }
        updateAnimation();

    }

    private TankColor calcColorDependingOnHealth() {
        if (this.health < 25) {
            return TankColor.RED;
        } else if (this.health < 50) {
            return TankColor.YELLOW;
        } else if (this.health < 75) {
            return TankColor.GRAY;
        } else {
            return TankColor.GREEN;
        }
    }
}
