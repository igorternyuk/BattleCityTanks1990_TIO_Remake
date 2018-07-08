package com.igorternyuk.tanks.gamestate;

import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;

/**
 *
 * @author igor
 */
public abstract class GameState {

    protected GameStateManager gameStateManager;
    protected ResourceManager resourceManager;

    public GameState(GameStateManager gsm) {
        this.gameStateManager = gsm;
        this.resourceManager = ResourceManager.getInstance();
    }

    public GameStateManager getGameStateManager() {
        return this.gameStateManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public abstract void load();

    public abstract void unload();

    public abstract void update(KeyboardState keyboardState, double frameTime);

    public abstract void onKeyPressed(int keyCode);

    public abstract void onKeyReleased(int keyCode);

    public abstract void draw(Graphics2D g);

}
