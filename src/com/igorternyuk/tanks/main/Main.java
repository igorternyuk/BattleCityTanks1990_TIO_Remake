package com.igorternyuk.tanks.main;

import com.igorternyuk.tanks.gameplay.Game;
import javax.swing.JOptionPane;

/**
 *
 * @author igor
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Game game = new Game();
            game.start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error", ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
