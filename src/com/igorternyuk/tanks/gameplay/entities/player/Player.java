package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;

/**
 *
 * @author igor
 */
public class Player extends Tank{

    public Player(LevelState level, EntityType type, double x, double y,
            double speed, Direction direction) {
        super(level, type, x, y, speed, direction);
    }
    
    
    @Override
    public void loadAnimations() {
    }

    @Override
    public void chooseDirection() {
    }

    @Override
    public void fire() {
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime){
        
    }
    
    @Override
    public void draw(Graphics2D g){
        
    }
}
