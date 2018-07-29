package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.GameMode;
import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author igor
 */
public class GameStateManager {

    public static final int MENU_STATE = 0;
    public static final int LEVEL_STATE_FOR_ONE_PLAYER = 1;
    public static final int LEVEL_STATE_FOR_TWO_PLAYERS = 2;
    public static final int CONSTRUCTION_STATE = 3;

    private static GameStateManager instance;

    public static synchronized GameStateManager create() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    private Game game;
    private GameState currentGameState;
    private Map<Integer, Supplier<GameState>> gameStateFactoty;

    public GameStateManager() {
        initGameStateFactory();
        setGameState(MENU_STATE);
    }

    private void initGameStateFactory() {
        this.gameStateFactoty = new HashMap<>();
        this.gameStateFactoty.put(MENU_STATE, () -> {
            return new MenuState(this);
        });
        this.gameStateFactoty.put(LEVEL_STATE_FOR_ONE_PLAYER, () -> {
            return new LevelState(this, GameMode.ONE_PLAYER);
        });
        this.gameStateFactoty.put(LEVEL_STATE_FOR_TWO_PLAYERS, () -> {
            return new LevelState(this, GameMode.TWO_PLAYERS);
        });
        this.gameStateFactoty.put(CONSTRUCTION_STATE, () -> {
            return new ConstructionState(this);
        });
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }

    public final void setGameState(int index) {
        if (!this.gameStateFactoty.containsKey(index)) {
            throw new IllegalArgumentException(
                    "There is no game state with such index = " + index);
        }
        if (this.currentGameState != null) {
            this.currentGameState.unload();
        }

        this.currentGameState = this.gameStateFactoty.get(index).get();
        this.currentGameState.load();
    }

    public void onKeyPressed(int keyCode) {
        this.currentGameState.onKeyPressed(keyCode);
    }

    public void onKeyReleased(int keyCode) {
        this.currentGameState.onKeyReleased(keyCode);
    }

    public void onMouseReleased(MouseEvent e) {
        this.currentGameState.onMouseReleased(e);
    }

    public void onMouseMoved(MouseEvent e) {
        this.currentGameState.onMouseMoved(e);
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        this.currentGameState.update(keyboardState, frameTime);
    }

    public void draw(Graphics2D g) {
        this.currentGameState.draw(g);
    }
}
