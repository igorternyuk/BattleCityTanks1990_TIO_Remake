package tanks.display;

import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author igor
 */
public class Display {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE_OF_PROGRAM = "Tanks";
    private static Display instance = null;
    private JFrame window;
    private Canvas canvas;
    
    private Display(){
        this.window = new JFrame(TITLE_OF_PROGRAM);
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.window.getContentPane().add(this.canvas);
        this.window.setResizable(false);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.pack();
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
    }
    
    public synchronized static Display getInstance(){
        if(instance == null){
            instance = new Display();
        }
        return instance;
    }
}
