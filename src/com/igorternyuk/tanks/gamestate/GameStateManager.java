package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.util.Stack;

/**
 *
 * @author igor
 */
public class GameStateManager {
    private static GameStateManager instance;
    
    public static synchronized GameStateManager create(Game game){
        if(instance == null){
            instance = new GameStateManager(game);
        }
        return instance;
    }

    private Game game;
    private Stack<GameState> gameStates;

    public GameStateManager(Game game) {
        this.game = game;
        this.gameStates = new Stack<>();
        this.gameStates.push(new LevelState(this));
        this.gameStates.push(new MenuState(this));
        this.gameStates.peek().load();
    }

    public Game getGame() {
        return this.game;
    }

    public void nextState() {
        if (this.gameStates.size() >= 2) {
            GameState currentState = this.gameStates.pop();
            currentState.unload();
            this.gameStates.peek().load();
        }
    }

    public void unloadAllGameStates() {
        while (!this.gameStates.empty()) {
            GameState currentState = this.gameStates.pop();
            currentState.unload();
        }
    }

    public void onKeyPressed(int keyCode) {
        this.gameStates.peek().onKeyPressed(keyCode);
    }

    public void onKeyReleased(int keyCode) {
        this.gameStates.peek().onKeyReleased(keyCode);
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        this.gameStates.peek().update(keyboardState, frameTime);
    }

    public void draw(Graphics2D g) {
        this.gameStates.peek().draw(g);
    }
}
