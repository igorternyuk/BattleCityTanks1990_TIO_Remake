package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.utils.Files;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author igor
 */
public class TileMap {

    private static final double EAGLE_PROTECTION_LIFE_TIME = 23;
    private static final double EAGLE_PROTECTION_BLINKING_TIME = 5;
    private static final double EAGLE_PROTECTION_BLINK_PERIOD = 0.25;
    private double scale;
    private Tile[][] tiles = new Tile[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
    private List<Tile> bushTiles = new ArrayList<>();
    private List<WaterTile> waterTiles = new ArrayList<>();
    private List<Point> enemyTankAppearancePositions = new ArrayList<>();
    private List<Point> eagleProtectionTilePositions = new ArrayList<>();
    private BufferedImage spriteSheet;
    private Map<TileType, BufferedImage> tileTypeImageMap = new HashMap<>();
    private String pathToTheCurrentMapFile;
    private boolean mapLoaded = false;
    private boolean eagleProtectionActive = false;
    private TileType currProtectionTileType = TileType.BRICK;
    private double eagleProtectionTimer = 0;
    private boolean eagleProtectionBlinking = false;
    private double eagleProtectionBlinkingTimer = 0;
    private double eagleProtectionBlinkTimer = 0;

    public TileMap(double scale) {
        this.scale = scale;
        setEnemyTankAppearancePositions();
        setEagleProtectionPositions();
        loadSpriteSheet();
    }
    
    public int getTilesInWidth(){
        return Game.TILES_IN_WIDTH;
    }
    
    public int getTilesInHeight(){
        return Game.TILES_IN_HEIGHT;
    }
    
    public int fixRowIndex(int row){
        if(row < 0){
            row = 0;
        }
        if(row > getTilesInHeight() - 1){
            row = getTilesInHeight() - 1;
        }
        return row;
    }
    
    public int fixColumnIndex(int col){
        if(col < 0){
            col = 0;
        }
        if(col > getTilesInWidth() - 1){
            col = getTilesInWidth() - 1;
        }
        return col;
    }

    public Map<TileType, BufferedImage> getTileTypeImageMap() {
        return Collections.unmodifiableMap(this.tileTypeImageMap);
    }

    public boolean isEagleProtectionActive() {
        return this.eagleProtectionActive;
    }

    public boolean checkIfPointIsInTheMapBounds(Point position) {
        int row = position.x / Game.HALF_TILE_SIZE;
        int col = position.y / Game.HALF_TILE_SIZE;
        return areCoordinatesValid(row, col);
    }

    public List<Point> getEnemyTankAppearencePositions() {
        return Collections.unmodifiableList(this.enemyTankAppearancePositions);
    }

    public List<Point> getEagleProtectionPositions() {
        return Collections.unmodifiableList(this.eagleProtectionTilePositions);
    }

    public void loadMap(String pathToMapFile) {
        int[][] map = Files.loadMapFromFile(pathToMapFile);
        createTilesFromMap(map);
        pathToTheCurrentMapFile = pathToMapFile;
        this.mapLoaded = true;
        System.out.println("The tile map was successfully loaded");
    }

    private void createTilesFromMap(int[][] map) {
        for (int row = 0; row < map.length; ++row) {
            for (int col = 0; col < map[row].length; ++col) {
                TileType currTileType = TileType.getFromNumber(map[row][col]);
                Point position = new Point(col * Game.HALF_TILE_SIZE, row
                        * Game.HALF_TILE_SIZE);
                Tile newTile = Tile.createTile(currTileType,
                        position, this.tileTypeImageMap.get(currTileType),
                        this.scale);
                this.tiles[row][col] = newTile;
                if (currTileType == TileType.BUSH) {
                    this.bushTiles.add(newTile);
                } else if (currTileType == TileType.WATER) {
                    this.waterTiles.add((WaterTile) newTile);
                }
            }
        }
    }

    private int[][] getCurrentMap() {
        int[][] currMap = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
        for (int row = 0; row < this.tiles.length; ++row) {
            for (int col = 0; col < this.tiles[row].length; ++col) {
                currMap[row][col] = this.tiles[row][col].getType().getNumber();
            }
        }
        return currMap;
    }

    public void saveMapToFile() {
        Files.writeMapToFile(this.pathToTheCurrentMapFile, getCurrentMap());
    }

    public TileType getTileType(int row, int col) {
        return getTile(row, col).getType();
    }

    public Tile getTile(int row, int col) {
        if (!this.mapLoaded) {
            throw new RuntimeException("Tile map was not loaded");
        }
        if (!areCoordinatesValid(row, col)) {
            throw new IllegalArgumentException(
                    "Row or column index is out of game field bounds");
        }
        return this.tiles[row][col];
    }

    public void destroyTile(int row, int col) {
        set(row, col, TileType.EMPTY);
    }

    public void set(int row, int col, TileType tileType) {
        if (!this.mapLoaded) {
            throw new RuntimeException("Tile map was not loaded");
        }
        if (!areCoordinatesValid(row, col)) {
            throw new IllegalArgumentException(
                    "Row or column index is out of game field bounds");
        }

        Point selectedPoint = new Point(col * Game.HALF_TILE_SIZE,
                row * Game.HALF_TILE_SIZE);
        Tile tile = Tile.createTile(tileType, selectedPoint,
                this.tileTypeImageMap.get(tileType), this.scale);

        if (tileType == TileType.BUSH) {
            this.bushTiles.add(tile);
        } else if (tileType == TileType.WATER) {
            this.waterTiles.add((WaterTile) tile);
        } else {
            if (this.tiles[row][col].getType() == TileType.BUSH) {
                this.bushTiles.remove(this.tiles[row][col]);
            } else if (this.tiles[row][col].getType() == TileType.WATER) {
                this.waterTiles.remove((WaterTile) this.tiles[row][col]);
            }
        }
        this.tiles[row][col] = tile;
    }

    public boolean areCoordinatesValid(int row, int col) {
        return row >= 0 && row < this.tiles.length
                && col >= 0 && col < this.tiles[row].length;
    }

    public void activateEagleProtection() {
        buildMetalWallsAroundEagle();
        this.eagleProtectionActive = true;
    }

    private void buildMetalWallsAroundEagle() {
        this.eagleProtectionTilePositions.forEach(point -> {
            int row = point.y / Game.HALF_TILE_SIZE;
            int col = point.x / Game.HALF_TILE_SIZE;
            tiles[row][col] = Tile.createTile(TileType.METAL, point,
                    this.tileTypeImageMap.get(TileType.METAL), this.scale);
        });
        this.currProtectionTileType = TileType.METAL;
    }

    public void deactivateEagleProtection() {
        restoreRegularEagleProtection();
        this.eagleProtectionActive = false;
    }

    private void restoreRegularEagleProtection() {
        this.eagleProtectionTilePositions.forEach(point -> {
            int row = point.y / Game.HALF_TILE_SIZE;
            int col = point.x / Game.HALF_TILE_SIZE;
            tiles[row][col] = Tile.createTile(TileType.BRICK, point,
                    this.tileTypeImageMap.get(TileType.BRICK), this.scale);
        });
        this.currProtectionTileType = TileType.BRICK;
    }

    private void flipProtectingTiles() {
        if (this.currProtectionTileType == TileType.BRICK) {
            buildMetalWallsAroundEagle();
        } else {
            restoreRegularEagleProtection();
        }
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        for (int row = 0; row < this.tiles.length; ++row) {
            for (int col = 0; col < this.tiles[row].length; ++col) {
                Tile currTile = this.tiles[row][col];
                boolean needToBeReplaced = false;
                if (currTile.getType().isDestroyable()) {
                    if (currTile.getType() == TileType.BRICK) {
                        BrickTile brickTile = (BrickTile) currTile;
                        needToBeReplaced = !brickTile.isAlive();
                    } else if (currTile.getType() == TileType.METAL) {
                        MetalTile brickTile = (MetalTile) currTile;
                        needToBeReplaced = !brickTile.isAlive();
                    }
                }
                if (needToBeReplaced) {
                    Point currTilePosition = new Point((int) currTile.getX(),
                            (int) currTile.getY());
                    this.tiles[row][col] = Tile.createTile(TileType.EMPTY,
                            currTilePosition, this.tileTypeImageMap.get(
                                    TileType.EMPTY),
                            this.scale);
                }
            }
        }
        this.waterTiles.forEach(waterTile -> waterTile.update(keyboardState,
                frameTime));
        updateProtection(keyboardState, frameTime);
    }

    private void updateProtection(KeyboardState keyboardState, double frameTime) {
        if (this.eagleProtectionActive) {
            this.eagleProtectionTimer += frameTime;
            if (this.eagleProtectionTimer >= EAGLE_PROTECTION_LIFE_TIME) {
                this.eagleProtectionBlinkingTimer = 0;
                this.eagleProtectionActive = false;
                this.eagleProtectionBlinking = true;
            }
        }

        if (this.eagleProtectionBlinking) {
            this.eagleProtectionBlinkTimer += frameTime;
            if (this.eagleProtectionBlinkTimer >= EAGLE_PROTECTION_BLINK_PERIOD) {
                flipProtectingTiles();
                this.eagleProtectionBlinkTimer = 0;
            }

            this.eagleProtectionBlinkingTimer += frameTime;
            if (this.eagleProtectionBlinkingTimer
                    >= EAGLE_PROTECTION_BLINKING_TIME) {
                this.eagleProtectionBlinkingTimer = 0;
                this.eagleProtectionBlinking = false;
                this.eagleProtectionActive = false;
                restoreRegularEagleProtection();
            }
        }
    }

    public void draw(Graphics2D g) {
        if (!this.mapLoaded) {
            return;
        }
        for (int row = 0; row < this.tiles.length; ++row) {
            for (int col = 0; col < this.tiles[row].length; ++col) {
                TileType currTileType = getTileType(row, col);
                if (currTileType == TileType.BUSH) {
                    continue;
                }
                this.tiles[row][col].draw(g);
            }
        }
    }

    public void drawBushes(Graphics2D g) {
        for (int i = this.bushTiles.size() - 1; i >= 0; --i) {
            this.bushTiles.get(i).draw(g);
        }
    }

    private void setEnemyTankAppearancePositions() {
        for (int i = 0; i < 3; ++i) {
            this.enemyTankAppearancePositions.add(new Point(6 * i
                    * Game.TILE_SIZE, 0));
        }
    }

    private void setEagleProtectionPositions() {
        for (int row = 23; row < 26; ++row) {
            for (int col = 11; col < 15; ++col) {
                if (row > 23 && (col == 12 || col == 13)) {
                    continue;
                }
                this.eagleProtectionTilePositions.add(
                        new Point(col * Game.HALF_TILE_SIZE, row
                                * Game.HALF_TILE_SIZE));
            }
        }
    }

    private void loadSpriteSheet() {
        SpriteSheetManager spriteSheetManager = SpriteSheetManager.getInstance();
        this.spriteSheet = spriteSheetManager.get(
                SpriteSheetIdentifier.SMALL_TILES);
        fillTileTypeImageMap();
    }

    private void fillTileTypeImageMap() {
        for (TileType tileType : TileType.values()) {
            Rectangle imageFragmentBoundingRect = tileType.getBoundingRect();
            BufferedImage currTileImageFragment = this.spriteSheet.getSubimage(
                    imageFragmentBoundingRect.x, imageFragmentBoundingRect.y,
                    imageFragmentBoundingRect.width,
                    imageFragmentBoundingRect.height);
            tileTypeImageMap.put(tileType, currTileImageFragment);
        }
    }
}
