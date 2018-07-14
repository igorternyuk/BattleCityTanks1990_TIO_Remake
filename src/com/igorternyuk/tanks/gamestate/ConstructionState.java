package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gameplay.tilemap.TileType;
import com.igorternyuk.tanks.graphics.images.TextureAtlas;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.ImageIdentifier;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author igor
 */
public class ConstructionState extends GameState {

    private static final Font BUTTON_TEXT_FONT =
            new Font("Arial", Font.BOLD, 18);
    private static final Color GRID_COLOR = new Color(127, 127, 127);

    private class TileButton {

        private Rectangle boundingRect;
        private Tile tile;

        public TileButton(Rectangle boundingRect, Tile tile) {
            this.boundingRect = boundingRect;
            this.tile = tile;
        }

        public void draw(Graphics2D g) {
            if (this.tile.getType() == TileType.EMPTY) {
                g.setColor(Color.red);
                g.drawRect(this.boundingRect.x * 2 - 1, this.boundingRect.y * 2
                        - 1,
                        this.boundingRect.width + 1, this.boundingRect.height
                        + 1);
            }
            this.tile.draw(g, this.boundingRect.x, this.boundingRect.y);
        }
    }

    private class Button {

        private Rectangle boundingRect;
        private String text;
        private Color color;
        private Runnable onClick;

        public Button(Rectangle boundingRect, String text, Color color,
                Runnable onClick) {
            this.boundingRect = boundingRect;
            this.text = text;
            this.color = color;
            this.onClick = onClick;
        }

        public void click() {
            this.onClick.run();
        }

        public void draw(Graphics2D g) {
            g.setColor(this.color);
            g.fillRect(this.boundingRect.x, this.boundingRect.y,
                    this.boundingRect.width, this.boundingRect.height);
            g.setColor(Color.black);
            g.setFont(BUTTON_TEXT_FONT);
            int textWidth = g.getFontMetrics().stringWidth(text);
            int textHeight = g.getFontMetrics().getHeight();
            int dx = (this.boundingRect.width - textWidth) / 2;
            int dy = (this.boundingRect.height - textHeight) / 2;
            g.drawString(text, boundingRect.x + dx, this.boundingRect.y
                    + this.boundingRect.height / 2 + dy);
        }
    }

    private TextureAtlas atlas;
    private SpriteSheetManager spriteSheetManager;
    private List<TileButton> tileButtons = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();
    private TileMap tileMap;
    private boolean loaded = false;
    private boolean tileSelected = false;
    private TileType selectedTileType;
    private Point selectedTileDrawPosition = new Point();

    public ConstructionState(GameStateManager gsm) {
        super(gsm);
        Rectangle r = new Rectangle(216, 92, 16, 16);
        if (r.inside(220, 96)) {
            System.out.println("INSIDE!!!");
        }

    }

    private void fillTileButtonArray() {
        Map<TileType, Tile> allTiles = this.tileMap.getAllTiles();
        TileType[] allTypes = TileType.values();
        for (int i = 0; i < allTypes.length; ++i) {
            int x = Game.TILES_IN_WIDTH * Game.HALF_TILE_SIZE
                    + Game.HALF_TILE_SIZE;
            int y = Game.HALF_TILE_SIZE + i * (Game.TILE_SIZE + 5);
            int w = Game.TILE_SIZE;
            int h = Game.TILE_SIZE;
            Rectangle rect = new Rectangle(x, y, w, h);
            System.out.println("rect " + i + " = " + rect);
            tileButtons.add(new TileButton(rect, allTiles.get(allTypes[i])));

        }
    }

    private void fillButtonArray() {
        String[] texts = {"Select level", "Save", "Back to menu"};
        Color[] colors = {Color.cyan.darker(), Color.green.darker(),
            Color.yellow.darker()};
        Runnable[] actions = {
            () -> {
                System.out.println("Selecting level");
                int lvl = Integer.parseInt(JOptionPane.showInputDialog(null,
                        "State number",
                        "Select the stage you would like to edit",
                        JOptionPane.INFORMATION_MESSAGE));
                System.out.println("lvl = " + lvl);
                this.tileMap.loadMap("/tilemap/level" + lvl + ".map");
            },
            () -> {
                saveMapToFile();
            },
            () -> {
                this.gameStateManager.setGameState(GameStateManager.MENU_STATE);
            }
        };
        for (int i = 0; i < 3; ++i) {
            Rectangle rect = new Rectangle(16 + i * 136, 436, 128, 32);
            this.buttons.add(new Button(rect, texts[i], colors[i], actions[i]));
        }
    }

    @Override
    public void load() {
        this.resourceManager.loadImage(ImageIdentifier.TEXTURE_ATLAS,
                "/images/texture_atlas_black.png");
        this.atlas = new TextureAtlas(this.resourceManager.getImage(
                ImageIdentifier.TEXTURE_ATLAS));
        this.spriteSheetManager = SpriteSheetManager.getInstance();
        for (SpriteSheetIdentifier identifier : SpriteSheetIdentifier.values()) {
            this.spriteSheetManager.put(identifier, this.atlas);
        }
        tileMap = new TileMap();
        tileMap.loadMap("/tilemap/level1.map");
        fillTileButtonArray();
        fillButtonArray();
        loaded = true;
    }

    @Override
    public void unload() {
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        if (!this.loaded) {
            return;
        }
        tileMap.update(keyboardState, frameTime);
    }

    @Override
    public void onKeyPressed(int keyCode) {
    }

    @Override
    public void onKeyReleased(int keyCode) {
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        System.out.println("Mouse released!!!");
        System.out.println("clicked point p => { x = " + e.getX() + ", y = "
                + e.getY() + " }");
        int releasedButton = e.getButton();
        if (releasedButton == MouseEvent.BUTTON3) {
            this.tileSelected = false;
            return;
        }
        for (int i = 0; i < this.buttons.size(); ++i) {
            Button btn = this.buttons.get(i);
            if (btn.boundingRect.contains(e.getX(), e.getY())) {
                btn.onClick.run();
                break;
            }
        }

        if (!this.tileSelected) {
            System.out.println("Selecting tiles");
            Point clickedPoint = new Point(e.getX() / 2, e.getY() / 2);
            System.out.println("clicked point p = " + clickedPoint);
            for (int i = 0; i < this.tileButtons.size(); ++i) {
                TileButton currButton = this.tileButtons.get(i);
                System.out.println("Curr btn rect = " + currButton.boundingRect);
                int rx = currButton.boundingRect.x;
                int ry = currButton.boundingRect.y;
                int cpx = clickedPoint.x;
                int cpy = clickedPoint.y;
                /* System.out.println("cpx >= rx " + (cpx >= rx));
                System.out.println("cpx <= rx + 16 " + (cpx <= rx + 16));
                System.out.println("cpy >= ry " + (cpy >= ry));
                System.out.println("cpy <= ry + 16 " + (cpy <= ry + 16));*/
                if (cpx >= rx && cpx <= rx + 16 && cpy >= ry && cpy <= ry + 16) {
                    TileType currButtonTileType = currButton.tile.getType();
                    this.selectedTileType = currButtonTileType;
                    this.tileSelected = true;
                    break;
                }

                /*if (currButton.boundingRect.inside(clickedPoint.x, clickedPoint.y)) {
                    TileType currButtonTileType = currButton.tile.getType();
                    this.selectedTileType = currButtonTileType;
                    break;
                }*/
            }
        } else {
            int row = (int) (e.getY() / Game.SCALE / Game.HALF_TILE_SIZE);
            int col = (int) (e.getX() / Game.SCALE / Game.HALF_TILE_SIZE);
            this.tileMap.set(row, col, this.selectedTileType);

        }
    }

    private void saveMapToFile() {
        this.tileMap.saveMapToFile();
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        if (this.tileSelected) {
            selectedTileDrawPosition.x = e.getX();
            selectedTileDrawPosition.y = e.getY();
            ///System.out.println("tile seleceted pos = " + selectedTileDrawPosition);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.loaded) {
            return;
        }
        tileMap.draw(g);
        tileMap.drawBushes(g);
        drawGrid(g);
        this.tileButtons.forEach(btn -> btn.draw(g));
        this.buttons.forEach(btn -> btn.draw(g));
        //this.buttons.forEach(Button::draw);
        if (this.tileSelected) {
            Tile currTile = this.tileMap.getAllTiles().
                    get(this.selectedTileType);
            currTile.draw(g, this.selectedTileDrawPosition.x / 2,
                    this.selectedTileDrawPosition.y / 2);
            //g.setColor(Color.green);
            //g.fillRect(this.selectedTileDrawPosition.x, this.selectedTileDrawPosition.y, 16, 16);
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        for (int i = 0; i < Game.TILES_IN_WIDTH; ++i) {
            g.drawLine(i * Game.HALF_TILE_SIZE * 2, 0, i * Game.HALF_TILE_SIZE
                    * 2,
                    Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE * 2);
        }
        for (int i = 0; i < Game.TILES_IN_HEIGHT; ++i) {
            g.drawLine(0, i * Game.HALF_TILE_SIZE * 2, Game.TILES_IN_WIDTH
                    * Game.HALF_TILE_SIZE * 2, i * Game.HALF_TILE_SIZE * 2);
        }
    }
}
