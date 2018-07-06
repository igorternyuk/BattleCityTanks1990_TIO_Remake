package com.igorternyuk.tanks.graphics;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import javax.swing.JFrame;

/**
 *
 * @author igor
 */
public class Display {

    private static Display instance;
    private JFrame window;
    private Canvas canvas;
    private BufferedImage bufferImage;
    private int[] bufferData;
    private Graphics bufferGraphics;
    private int clearColor;
    private BufferStrategy bufferStrategy;

    private Display(int width, int height, String title, int numBuffers,
            int clearColor) {
        this.window = new JFrame(title);
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(new Dimension(width, height));
        this.canvas.setFocusable(true);
        this.canvas.requestFocus();

        this.window.setResizable(false);
        this.window.getContentPane().add(this.canvas);
        this.window.pack();
        this.window.setLocationRelativeTo(null);

        this.bufferImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        this.bufferData = ((DataBufferInt) this.bufferImage.getRaster()
                .getDataBuffer()).getData();
        this.bufferGraphics = this.bufferImage.getGraphics();
        this.clearColor = clearColor;
        this.canvas.createBufferStrategy(numBuffers);
        this.bufferStrategy = this.canvas.getBufferStrategy();

        Graphics2D g2 = (Graphics2D) this.bufferGraphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        this.window.setVisible(true);

    }

    public static Display create(int width, int height, String title,
            int numBuffers, int clearColor) {
        if (instance == null) {
            instance = new Display(width, height, title, numBuffers, clearColor);
        }
        return instance;
    }

    public JFrame getWindow() {
        return this.window;
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) this.bufferGraphics;
    }

    public void clear() {
        Arrays.fill(this.bufferData, this.clearColor);
    }

    public void swapBuffers() {
        Graphics g = this.bufferStrategy.getDrawGraphics();
        g.drawImage(this.bufferImage, 0, 0, Game.WIDTH, Game.HEIGHT, null);
        this.bufferStrategy.show();
    }

    public void destroy() {
        this.window.dispose();
        instance = null;
    }

    public void setTitle(String title) {
        this.window.setTitle(title);
    }

    public void addInputListener(KeyboardState keyboardState) {
        this.window.add(keyboardState);
    }

    public void addKeyListener(KeyListener listener) {
        this.canvas.addKeyListener(listener);
    }

    public void addWindowListener(WindowListener listener) {
        this.window.addWindowListener(listener);
    }
}
