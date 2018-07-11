package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public class GameStateManager {
    public static final int MENU_STATE = 0;
    public static final int LEVEL_STATE = 1;
    public static final int CONSTRUCTION_STATE = 2;
    
    private static GameStateManager instance;
    
    public static synchronized GameStateManager create(){
        if(instance == null){
            instance = new GameStateManager();
        }
        return instance;
    }

    private Game game;
    private List<GameState> gameStates;
    private GameState currentGameState;

    public GameStateManager() {
        //this.game = game;
        this.gameStates = new ArrayList<>();
        this.gameStates.add(MENU_STATE, new MenuState(this));
        this.gameStates.add(LEVEL_STATE, new LevelState(this));
        this.gameStates.add(CONSTRUCTION_STATE, new ConstructionState(this));
        this.currentGameState = this.gameStates.get(MENU_STATE);
        this.currentGameState.load();
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    public Game getGame() {
        return this.game;
    }

    public void setGameState(int index) {
        if(this.currentGameState != null){
            this.currentGameState.unload();
        }
        this.currentGameState = this.gameStates.get(index);
        this.currentGameState.load();
    }

    public void unloadAllGameStates() {
        this.gameStates.forEach((state) -> {
            state.unload();
        });
    }

    public void onKeyPressed(int keyCode) {
        this.currentGameState.onKeyPressed(keyCode);
    }

    public void onKeyReleased(int keyCode) {
        this.currentGameState.onKeyReleased(keyCode);
    }
    
    public void onMouseReleased(MouseEvent e){
        this.currentGameState.onMouseReleased(e);
    }
    
    public void onMouseMoved(MouseEvent e){
        this.currentGameState.onMouseMoved(e);
    }
    
    
    public void update(KeyboardState keyboardState, double frameTime) {
        this.currentGameState.update(keyboardState, frameTime);
    }

    public void draw(Graphics2D g) {
        this.currentGameState.draw(g);
    }
}
