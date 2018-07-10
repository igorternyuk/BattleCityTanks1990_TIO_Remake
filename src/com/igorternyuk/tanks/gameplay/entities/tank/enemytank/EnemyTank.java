package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.animations.Animation;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 *
 * @author igor
 */
public class EnemyTank extends Tank<EnemyTankIdentifier> {

    public EnemyTank(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
        loadAnimations();
    }

    @Override
    public final void loadAnimations() {
        Map<EnemyTankIdentifier, BufferedImage> spriteSheetMap =
                EnemyTankIdentifier.getSpriteSheetMap();
        spriteSheetMap.keySet().forEach(key -> {
            this.animationManager.addAnimation(key, new Animation(
                    spriteSheetMap.get(key), 0.25,
                    0, 0, Game.TILE_SIZE, Game.TILE_SIZE, 2, Game.TILE_SIZE                    
            ));
        });
    }

    @Override
    public void chooseDirection() {
    }

    @Override
    public void fire() {
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {

    }

    @Override
    public void draw(Graphics2D g) {

    }

}
