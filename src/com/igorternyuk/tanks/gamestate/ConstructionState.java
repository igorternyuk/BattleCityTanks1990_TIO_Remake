package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

/**
 *
 * @author igor
 */
public class ConstructionState extends GameState {

    private int[][] tileMap = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
    public ConstructionState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
    }

    @Override
    public void onKeyPressed(int keyCode) {
    }

    @Override
    public void onKeyReleased(int keyCode) {
    }

    @Override
    public void onMouseReleased(MouseEvent e) {

    }

    @Override
    public void onMouseMoved(MouseEvent e) {

    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        for (int i = 0; i < 2 * Game.TILES_IN_WIDTH; ++i) {
            g.drawLine(i * Game.HALF_TILE_SIZE * 2, 0, i * Game.HALF_TILE_SIZE
                    * 2,
                    Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE * 2 * 2);
        }
        for (int i = 0; i < 2 * Game.TILES_IN_HEIGHT; ++i) {
            g.drawLine(0, i * Game.HALF_TILE_SIZE * 2, Game.TILES_IN_WIDTH
                    * Game.HALF_TILE_SIZE * 2 * 2, i * Game.HALF_TILE_SIZE * 2);
        }
    }

}
