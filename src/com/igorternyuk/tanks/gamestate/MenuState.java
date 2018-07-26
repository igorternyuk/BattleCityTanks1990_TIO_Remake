package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.graphics.images.Background;
import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ImageIdentifier;
import com.igorternyuk.tanks.utils.Painter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public class MenuState extends GameState {

    private static final Color COLOR_MENU_ITEM = new Color(0, 148, 255);
    private static final Color COLOR_CURRENT_CHOICE = Color.yellow;

    private class MenuItem {

        int index;
        String title;
        Rectangle bounds;
        Runnable action;

        public MenuItem(int index, String title, Rectangle bounds,
                Runnable action) {
            this.index = index;
            this.title = title;
            this.bounds = bounds;
            this.action = action;
        }
        
        public void draw(Graphics2D g){
            Color color = (this.index == currentChoice) ?
                    COLOR_CURRENT_CHOICE :
                    COLOR_MENU_ITEM;
            Painter.drawCenteredString(g, this.title,
                    fontMenuItem, color, this.bounds.y + this.bounds.height);
        }
    }

    private Font fontMenuItem;

    private Background background;
    private int currentChoice = 0;
    private List<MenuItem> menuItems = new ArrayList<>();

    public MenuState(GameStateManager gsm) {
        super(gsm);
        createMenuItems();
    }

    @Override
    public void load() {
        System.out.println("Menu state loading...");
        this.resourceManager.loadImage(ImageIdentifier.MENU_BACKGROUND,
                "/images/menuScreen.png");
        this.resourceManager.loadFont(FontIdentifier.BATTLE_CITY,
                "/fonts/prstart.ttf");
        Font font = this.resourceManager.getFont(FontIdentifier.BATTLE_CITY);
        this.fontMenuItem = font.deriveFont(Font.BOLD, 24);
        this.background = new Background(
                this.resourceManager.getImage(ImageIdentifier.MENU_BACKGROUND));
        this.background.setPosition(0, 0);
    }

    @Override
    public void unload() {
        this.resourceManager.unloadImage(ImageIdentifier.MENU_BACKGROUND);
        this.resourceManager.unloadFont(FontIdentifier.BATTLE_CITY);
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        this.background.update(keyboardState, frameTime);
    }

    @Override
    public void draw(Graphics2D g) {
        this.background.draw(g);
        this.menuItems.forEach(menuItem -> menuItem.draw(g));
    }

    private void correctIndex() {
        if (this.currentChoice < 0) {
            this.currentChoice = this.menuItems.size() - 1;
        } else if (this.currentChoice >= this.menuItems.size()) {
            this.currentChoice = 0;
        }
    }

    private void select(int index) {
        if (index < 0 || index > this.menuItems.size() - 1) {
            throw new RuntimeException("Menu item index " + index
                    + " is out of bounds");
        }
        this.menuItems.get(index).action.run();
    }

    @Override
    public void onKeyPressed(int keyCode) {
    }

    @Override
    public void onKeyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                --this.currentChoice;
                break;
            case KeyEvent.VK_DOWN:
                ++this.currentChoice;
                break;
            case KeyEvent.VK_ENTER:
                select(this.currentChoice);
                break;
            default:
                break;
        }
        correctIndex();
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        boolean hoverMenuArea = this.menuItems.stream().anyMatch(menuItem -> { 
            return menuItem.bounds.contains(e.getX(), e.getY());
        });
        if(hoverMenuArea){
            select(this.currentChoice);
        }
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        System.out.println("currY = " + e.getY());
        for(int i = 0; i < this.menuItems.size(); ++i){
            MenuItem currMenuItem = this.menuItems.get(i);
            if(currMenuItem.bounds.contains(e.getX(), e.getY())){
                this.currentChoice = currMenuItem.index;
            }
        }
    }
    
    private void createMenuItems() {
        addMenuItem("PLAY", () -> {
            this.gameStateManager.setGameState(GameStateManager.LEVEL_STATE);
        });

        addMenuItem("CONSTRUCTION", () -> {
            this.gameStateManager.setGameState(
                    GameStateManager.CONSTRUCTION_STATE);
        });

        addMenuItem("QUIT", () -> {
            this.gameStateManager.getGame().onWindowCloseRequest();
        });
    }

    private void addMenuItem(String title, Runnable action) {
        int lastIndex = this.menuItems.size() - 1;
        int currY = Game.HEIGHT / 2 + (lastIndex + 1) * Game.HEIGHT / 12;
        MenuItem menuItem = new MenuItem(lastIndex + 1, title,
                new Rectangle(0, currY, Game.WIDTH, 32), action);
        this.menuItems.add(menuItem);
    }
}
