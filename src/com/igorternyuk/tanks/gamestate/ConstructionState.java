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
import com.igorternyuk.tanks.utils.Images;
import com.igorternyuk.tanks.utils.Painter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
    private static final Font TILE_BUTTON_LABEL_FONT = new Font("Verdana",
            Font.ITALIC, 12);
    private static final Color TILE_BUTTON_LABEL_COLOR = new Color(0, 148, 255);
    private static final Color GRID_COLOR = new Color(127, 127, 127);

    private class TileButton {

        private Tile tile;
        private Rectangle boundingRect;
        private String label;

        public TileButton(Tile tile) {
            this.tile = tile;
            this.boundingRect = new Rectangle((int) tile.getX(), (int) tile.
                    getY(),
                    Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE);
            this.label = this.tile.getType().getDescription();
        }

        public void draw(Graphics2D g) {
            g.setColor(TILE_BUTTON_LABEL_COLOR);
            g.setFont(TILE_BUTTON_LABEL_FONT);
            g.drawString(label, (int) (this.tile.getX() * Game.SCALE
                    - Game.HALF_TILE_SIZE),
                    (int) (this.tile.getY() * Game.SCALE - Game.TILE_SIZE));
            if (this.tile.getType() == TileType.EMPTY) {
                g.setColor(Color.red);
                g.drawRect((int) (this.tile.getX() * Game.SCALE - 1),
                        (int) (this.tile.getY() * Game.SCALE - 1),
                        (int) (Game.HALF_TILE_SIZE * Game.SCALE) + 1,
                        (int) (Game.HALF_TILE_SIZE * Game.SCALE) + 1);
            }
            this.tile.draw(g);
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
    private List<Point> forbiddenPositions = new ArrayList<>(6);
    private Point selectedTileDrawPosition = new Point();
    private BufferedImage flagImage;
    private int currLevel = 1;

    public ConstructionState(GameStateManager gsm) {
        super(gsm);
    }

    private void fillTileButtonArray() {
        Map<TileType, BufferedImage> tileTypeImageMap = this.tileMap.
                getTileTypeImageMap();
        TileType[] allTypes = TileType.values();

        int x = Game.TILES_IN_WIDTH * Game.HALF_TILE_SIZE
                + Game.HALF_TILE_SIZE;

        for (int i = 0; i < allTypes.length; ++i) {
            TileType currTileType = allTypes[i];
            int y = 2 * Game.TILE_SIZE * (i + 1);
            Point position = new Point(x, y);
            Tile tile = Tile.createTile(currTileType, position,
                    tileTypeImageMap.get(currTileType), Game.SCALE);
            tileButtons.add(new TileButton(tile));
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
                if(lvl >= 1 && lvl < LevelState.STAGE_MAX){
                      this.currLevel = lvl;
                      this.tileMap.loadMap("/tilemap/level" + lvl + ".map");
                }
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
                "/images/texture_atlas.png");
        this.atlas = new TextureAtlas(this.resourceManager.getImage(
                ImageIdentifier.TEXTURE_ATLAS));
        this.spriteSheetManager = SpriteSheetManager.getInstance();
        for (SpriteSheetIdentifier identifier : SpriteSheetIdentifier.values()) {
            this.spriteSheetManager.put(identifier, this.atlas);
        }
        this.flagImage = Images.resizeImage(this.spriteSheetManager.get(
                SpriteSheetIdentifier.LEVEL_FLAG), Game.SCALE);
        tileMap = new TileMap(Game.SCALE);
        tileMap.loadMap("/tilemap/level1.map");
        this.forbiddenPositions.add(this.tileMap.getCastlePosition());
        this.forbiddenPositions.addAll(this.tileMap.
                getEnemyTankAppearencePositions());
        this.forbiddenPositions.addAll(this.tileMap.getCastleProtectionPositions());
        this.forbiddenPositions.addAll(this.tileMap.getPlayerRespawnPositions());
        fillTileButtonArray();
        fillButtonArray();
        this.loaded = true;
    }

    @Override
    public void unload() {
        this.resourceManager.unloadImage(ImageIdentifier.TEXTURE_ATLAS);
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
        int releasedButton = e.getButton();
        if (releasedButton == MouseEvent.BUTTON3) {
            this.tileSelected = false;
            return;
        }
        for (int i = 0; i < this.buttons.size(); ++i) {
            Button btn = this.buttons.get(i);
            if (btn.boundingRect.contains(e.getX(), e.getY())) {
                btn.onClick.run();
                return;
            }
        }

        Point clickedPoint = new Point((int) (e.getX() / Game.SCALE),
                (int) (e.getY() / Game.SCALE));

        if (!this.tileSelected) {
            for (int i = 0; i < this.tileButtons.size(); ++i) {
                TileButton currButton = this.tileButtons.get(i);
                if (currButton.boundingRect.inside(clickedPoint.x,
                        clickedPoint.y)) {
                    TileType currButtonTileType = currButton.tile.getType();
                    this.selectedTileType = currButtonTileType;
                    this.tileSelected = true;
                    break;
                }
            }
        } else {
            if (!checkIfClickPositionAcceptable(clickedPoint)) {
                return;
            }
            int row = (int) (e.getY() / Game.SCALE / Game.HALF_TILE_SIZE);
            int col = (int) (e.getX() / Game.SCALE / Game.HALF_TILE_SIZE);
            if (releasedButton == MouseEvent.BUTTON1) {
                this.tileMap.set(row, col, this.selectedTileType);
            } else if (releasedButton == MouseEvent.BUTTON2) {
                this.tileMap.set(row, col, this.selectedTileType);
                this.tileMap.set(row + 1, col, this.selectedTileType);
                this.tileMap.set(row, col + 1, this.selectedTileType);
                this.tileMap.set(row + 1, col + 1, this.selectedTileType);
            }

        }
    }

    private boolean checkIfClickPositionAcceptable(Point clickPosition) {
        return this.tileMap.checkIfPointIsInTheMapBounds(clickPosition)
                && !checkIfInForbiddenPosition(clickPosition);
    }

    private boolean checkIfInForbiddenPosition(Point pos) {
        for (int i = 0; i < this.forbiddenPositions.size(); ++i) {
            Point point = this.forbiddenPositions.get(i);
            Rectangle currTileBoundingRect = new Rectangle(
                    point.x, point.y, Game.TILE_SIZE, Game.TILE_SIZE);
            if (currTileBoundingRect.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    private void saveMapToFile() {
        this.tileMap.saveMapToFile();
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        if (this.tileSelected) {
            selectedTileDrawPosition.x = (int) (e.getX() / Game.SCALE);
            selectedTileDrawPosition.y = (int) (e.getY() / Game.SCALE);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.loaded) {
            return;
        }
        drawTileMap(g);
        highlightForbiddenTiles(g);
        drawGrid(g);
        drawSelectedTile(g);
        drawAllButtons(g);
        drawCurrentLevel(g);

    }

    private void drawCurrentLevel(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawImage(this.flagImage, Game.WIDTH - 48, Game.HEIGHT - 64, null);
        Painter.drawNumber(g, currLevel, Color.white, Game.WIDTH - 48,
                Game.HEIGHT - 32, 2);
    }

    private void drawTileMap(Graphics2D g) {
        tileMap.draw(g);
        tileMap.drawBushes(g);
    }

    private void drawSelectedTile(Graphics2D g) {
        if (this.tileSelected) {
            BufferedImage currTileImage = this.tileMap.getTileTypeImageMap().
                    get(
                            this.selectedTileType);
            g.drawImage(currTileImage,
                    (int) (this.selectedTileDrawPosition.x * Game.SCALE),
                    (int) (this.selectedTileDrawPosition.y * Game.SCALE),
                    Game.TILE_SIZE, Game.TILE_SIZE, null);
        }
    }

    private void highlightForbiddenTiles(Graphics2D g) {
        g.setColor(Color.white);
        this.forbiddenPositions.forEach((p) -> {
            int size;
            if(this.tileMap.getCastleProtectionPositions().contains(p)){
                size = Game.HALF_TILE_SIZE;
            } else {
                size = 2 * Game.HALF_TILE_SIZE;
            }
            g.fillRect((int) (p.x * Game.SCALE), (int) (p.y * Game.SCALE),
                    (int) (size * Game.SCALE),
                    (int) (size * Game.SCALE));
        });
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        for (int i = 0; i <= Game.TILES_IN_WIDTH; ++i) {
            int x = (int) (i * Game.HALF_TILE_SIZE * Game.SCALE);
            if(x % (int)(Game.TILE_SIZE * Game.SCALE) == 0){
                g.setColor(Color.green.darker());
            } else {
                g.setColor(GRID_COLOR);
            }
            g.drawLine(x, 0, x, (int) (Game.TILES_IN_HEIGHT
                    * Game.HALF_TILE_SIZE * Game.SCALE));
        }
        for (int i = 0; i <= Game.TILES_IN_HEIGHT; ++i) {
            int y = (int) (i * Game.HALF_TILE_SIZE * Game.SCALE);
            if(y % (int)(Game.TILE_SIZE * Game.SCALE) == 0){
                g.setColor(Color.green.darker());
            } else {
                g.setColor(GRID_COLOR);
            }
            g.drawLine(0, y, (int) (Game.TILES_IN_WIDTH * Game.HALF_TILE_SIZE
                    * Game.SCALE), y);
        }
    }

    private void drawAllButtons(Graphics2D g) {
        this.tileButtons.forEach(btn -> btn.draw(g));
        this.buttons.forEach(btn -> btn.draw(g));
    }
}
