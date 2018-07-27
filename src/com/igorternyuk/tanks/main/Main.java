package com.igorternyuk.tanks.main;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.utils.BrickFont;


/**
 *
 * @author igor
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
    
}
