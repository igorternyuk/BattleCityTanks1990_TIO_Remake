package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.utils.Files;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author igor
 */
public class TileMap {

    private int[][] map = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
    private List<Point> tankAppearancePositions = new ArrayList<>();
    private List<Point> eagleProtectionTilePositions = new ArrayList<>();
    private BufferedImage spriteSheet;
    private Map<TileType, Tile> tiles = new HashMap<>();
    private List<Point> bushTilePositions = new ArrayList<>();
    private Map<Point, Integer> metalTileHealthMap = new HashMap<>();
    private boolean hasWaterTiles = false;
    private String pathToTheCurrentMapFile;
    private boolean mapLoaded = false;

    public TileMap() {
        setEnemyTankAppearancePositions();
        setEagleProtectionPositions();
        loadSpriteSheet();
        createTilesOfAllTypes();
    }

    public Map<TileType, Tile> getAllTiles() {
        if (this.mapLoaded) {
            return this.tiles;
        } else {
            return new HashMap<>();
        }
    }

    

    public List<Point> getEnemyTankAppearencePositions() {
        return this.tankAppearancePositions;
    }

    public List<Point> getEaglePositions() {
        return this.eagleProtectionTilePositions;
    }

    public void loadMap(String pathToMapFile) {
        this.map = Files.loadMapFromFile(pathToMapFile);
        calculateBushTilePositions();
        pathToTheCurrentMapFile = pathToMapFile;
        this.mapLoaded = true;
        System.out.println("The tile map was successfully loaded");
    }

    public void saveMapToFile() {
        Files.writeMapToFile(this.pathToTheCurrentMapFile, this.map);
    }

    public TileType get(int row, int col) {
        if (!areCoordinatesValid(row, col)) {
            throw new IllegalArgumentException(
                    "Row or column index is out of game field bounds");
        }
        return TileType.getFromNumber(this.map[row][col]);
    }

    public void set(int row, int col, TileType tileType) {
        if (!areCoordinatesValid(row, col)) {
            throw new IllegalArgumentException(
                    "Row or column index is out of game field bounds");
        }
        Point selectedPoint = new Point(col * Game.HALF_TILE_SIZE,
                row * Game.HALF_TILE_SIZE);
        if(tileType == TileType.BUSH){
            this.bushTilePositions.add(selectedPoint);
        } else {
            if(this.map[col][row] == TileType.BUSH.getNumber()){
                this.bushTilePositions.remove(selectedPoint);
            }
        }
        //this.bushTilePositions;
        this.map[row][col] = tileType.getNumber();
    }

    public boolean areCoordinatesValid(int row, int col) {
        return row >= 0 && row < this.map.length
                && col >= 0 && col < this.map[row].length;
    }

    public void activateProtection() {

    }

    public void handleCollision(Entity entity) {
        EntityType entityType = entity.getEntityType();
        if (entityType == EntityType.PLAYER_TANK) {

        } else if (entityType == EntityType.ENEMY_TANK) {

        } else if (entityType == EntityType.PROJECTILE) {

        }
    }

    private void loadSpriteSheet() {
        SpriteSheetManager spriteSheetManager = SpriteSheetManager.getInstance();
        this.spriteSheet = spriteSheetManager.get(
                SpriteSheetIdentifier.SMALL_TILES);
    }

    private void createTilesOfAllTypes() {
        for (TileType tileType : TileType.values()) {
            Rectangle boundingRect = tileType.getBoundingRect();
            BufferedImage sprite = this.spriteSheet.getSubimage(boundingRect.x,
                    boundingRect.y, boundingRect.width, boundingRect.height);
            Tile tile;
            if (tileType == TileType.WATER) {
                tile = new WaterTile(tileType, sprite, Game.SCALE);
            } else {
                tile = new Tile(tileType, sprite, Game.SCALE);
            }
            this.tiles.put(tileType, tile);
        }
    }

    public void calculateBushTilePositions() {
        for (int row = 0; row < this.map.length; ++row) {
            for (int col = 0; col < this.map[row].length; ++col) {
                TileType currTileType = get(row, col);
                if (currTileType == TileType.BUSH) {
                    Point bushPosition = new Point(col * Game.HALF_TILE_SIZE,
                            row * Game.HALF_TILE_SIZE);
                    this.bushTilePositions.add(bushPosition);
                } else if (currTileType == TileType.WATER) {
                    this.hasWaterTiles = true;
                }
            }
        }
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        if (this.hasWaterTiles) {
            this.tiles.get(TileType.WATER).update(keyboardState, frameTime);
        }
    }

    public void draw(Graphics2D g) {
        if (!this.mapLoaded) {
            return;
        }
        for (int row = 0; row < this.map.length; ++row) {
            for (int col = 0; col < this.map[row].length; ++col) {
                TileType currTileType = get(row, col);
                Tile currTile = this.tiles.get(currTileType);
                if (currTileType == TileType.BUSH) {
                    continue;
                }
                currTile.draw(g, col * Game.HALF_TILE_SIZE, row
                        * Game.HALF_TILE_SIZE);
            }
        }
    }

    public void drawBushes(Graphics2D g) {
        final Tile bushTile = this.tiles.get(TileType.BUSH);
        for(int i = this.bushTilePositions.size() - 1; i >= 0; --i){
            Point pos = this.bushTilePositions.get(i);
            bushTile.draw(g, pos.x, pos.y);
        }
    }
    
    private void setEnemyTankAppearancePositions() {
        this.tankAppearancePositions.add(new Point(0, 0));
        this.tankAppearancePositions.add(new Point(6 * Game.TILE_SIZE, 0));
        this.tankAppearancePositions.add(new Point(12 * Game.TILE_SIZE, 0));
    }

    private void setEagleProtectionPositions() {
        this.eagleProtectionTilePositions.add(
                new Point(11 * Game.HALF_TILE_SIZE, 23 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(11 * Game.HALF_TILE_SIZE, 24 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(11 * Game.HALF_TILE_SIZE, 25 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(12 * Game.HALF_TILE_SIZE, 23 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(13 * Game.HALF_TILE_SIZE, 23 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(14 * Game.HALF_TILE_SIZE, 23 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(14 * Game.HALF_TILE_SIZE, 24 * Game.HALF_TILE_SIZE));
        this.eagleProtectionTilePositions.add(
                new Point(14 * Game.HALF_TILE_SIZE, 25 * Game.HALF_TILE_SIZE));
    }
}
