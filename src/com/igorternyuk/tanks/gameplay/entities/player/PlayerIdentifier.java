package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;

/**
 *
 * @author igor
 */
public enum PlayerIdentifier {
    FIRST(1, TankColor.YELLOW),
    SECOND(2, TankColor.GREEN);
    
    private int id;
    private TankColor color;

    private PlayerIdentifier(int id, TankColor color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return this.id;
    }

    public TankColor getColor() {
        return this.color;
    }
    
    public static PlayerIdentifier getFromNumeric(int number){
        if(number < 1){
            number = 1;
        }
        if(number > 2){
            number = 2;
        }
        return PlayerIdentifier.values()[number - 1];
    }
}
