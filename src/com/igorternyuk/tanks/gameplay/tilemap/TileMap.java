package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.pathfinder.Pathfinder.Spot;
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

    private static final double EAGLE_PROTECTION_LIFE_TIME = 20;
    private static final double EAGLE_PROTECTION_BLINKING_TIME = 5;
    private static final double EAGLE_PROTECTION_BLINK_PERIOD = 0.25;
    
    public static class FiringSpot{
        private Spot spot;
        private Direction fireDirection;

        public FiringSpot(Spot spot, Direction fireDirection) {
            this.spot = spot;
            this.fireDirection = fireDirection;
        }

        public Spot getSpot() {
            return this.spot;
        }

        public void setSpot(Spot spot) {
            this.spot = spot;
        }

        public Direction getFireDirection() {
            return this.fireDirection;
        }

        public void setFireDirection(Direction fireDirection) {
            this.fireDirection = fireDirection;
        }
    }
    
    private double scale;
    private Tile[][] tiles = new Tile[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
    private int[][] clearanceMap =
            new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
    private boolean clearanceMapChanged = true;
    private List<Tile> bushTiles = new ArrayList<>();
    private List<WaterTile> waterTiles = new ArrayList<>();
    private Tile lastCollided;
    private List<Point> enemyTankAppearancePositions = new ArrayList<>();
    private List<Point> eagleProtectionTilePositions = new ArrayList<>();
    private List<FiringSpot> firePoints = new ArrayList<>();
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
        specifyEnemyTankAppearancePositions();
        specifyEagleProtectionPositions();
        specifyFireSpots();
        loadSpriteSheet();
        this.lastCollided = Tile.createTile(TileType.EMPTY, new Point(),
                this.tileTypeImageMap.get(TileType.EMPTY), this.scale);
    }

    public List<Point> getEnemyTankAppearencePositions() {
        return Collections.unmodifiableList(this.enemyTankAppearancePositions);
    }

    public List<Point> getEagleProtectionPositions() {
        return Collections.unmodifiableList(this.eagleProtectionTilePositions);
    }

    public List<FiringSpot> getFiringSpots() {
        return Collections.unmodifiableList(this.firePoints);
    }

    public Tile getLastCollided() {
        return this.lastCollided;
    }

    public int[][] getClearanceMap() {
        if (this.clearanceMapChanged) {
            updateClearanceMap();
        }
        return this.clearanceMap;
    }

    public List<Tile> getIntersectedTiles(Entity entity) {
        List<Tile> intersectedTiles = new ArrayList<>(9);

        final int rowMin = fixRowIndex((int) entity.top() / Game.HALF_TILE_SIZE);
        final int rowMax = fixRowIndex((int) (entity.bottom() - 1)
                / Game.HALF_TILE_SIZE);
        final int colMin = fixColumnIndex((int) entity.left()
                / Game.HALF_TILE_SIZE);
        final int colMax = fixColumnIndex((int) (entity.right() - 1)
                / Game.HALF_TILE_SIZE);

        for (int row = rowMin; row <= rowMax; ++row) {
            for (int col = colMin; col <= colMax; ++col) {
                intersectedTiles.add(getTile(row, col));
            }
        }
        return intersectedTiles;
    }

    public boolean checkIfOnTheIce(Entity entity) {
        List<Tile> intersectedTiles = getIntersectedTiles(entity);
        return intersectedTiles.stream().anyMatch(tile -> tile.getType()
                == TileType.ICE);
    }

    public boolean hasCollision(Entity entity) {

        List<Tile> intersectedTiles = getIntersectedTiles(entity);
        for (int i = 0; i < intersectedTiles.size(); ++i) {
            Tile currTile = intersectedTiles.get(i);
            if (currTile.checkIfCollision(entity)) {
                this.lastCollided = currTile;
                return true;
            }
        }
        return false;
    }

    public int getTilesInWidth() {
        return Game.TILES_IN_WIDTH;
    }

    public int getTilesInHeight() {
        return Game.TILES_IN_HEIGHT;
    }

    public int fixRowIndex(int row) {
        if (row < 0) {
            row = 0;
        }
        if (row > getTilesInHeight() - 1) {
            row = getTilesInHeight() - 1;
        }
        return row;
    }

    public int fixColumnIndex(int col) {
        if (col < 0) {
            col = 0;
        }
        if (col > getTilesInWidth() - 1) {
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
        return checkIfPointIsInTheMapBounds(position.x, position.y);
    }

    public boolean checkIfPointIsInTheMapBounds(int x, int y) {
        int row = x / Game.HALF_TILE_SIZE;
        int col = y / Game.HALF_TILE_SIZE;
        return areCoordinatesValid(row, col);
    }

    public boolean areCoordinatesValid(int row, int col) {
        return row >= 0 && row < this.tiles.length
                && col >= 0 && col < this.tiles[row].length;
    }

    public boolean isOutOfBounds(Entity entity) {
        return (entity.left() < 0
                || entity.right() > getTilesInWidth() * Game.HALF_TILE_SIZE
                || entity.top() < 0
                || entity.bottom() > getTilesInHeight() * Game.HALF_TILE_SIZE);
    }

    public void loadMap(String pathToMapFile) {
        int[][] map = Files.loadMapFromFile(pathToMapFile);
        this.bushTiles.clear();
        this.waterTiles.clear();
        System.out.println("map[25][0] = " + map[25][0]);
        createTilesFromMap(map);
        System.out.println("Number of bush tiles = " + this.bushTiles.size());

        pathToTheCurrentMapFile = pathToMapFile;
        this.mapLoaded = true;
        System.out.println("The tile map was successfully loaded");
    }

    public void saveMapToFile() {
        Files.writeMapToFile(this.pathToTheCurrentMapFile, getCurrentMap());
    }

    public void print() {
        if (!this.mapLoaded) {
            return;
        }
        for (int row = 0; row < this.tiles.length; ++row) {
            for (int col = 0; col < this.tiles[row].length; ++col) {
                if (!getTileType(row, col).isTraversable()) {
                    System.out.print("X");
                } else {
                    System.out.print("_");
                }
            }
            System.out.println("");
        }
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
                    this.clearanceMapChanged = true;
                }
            }
        }
        for(int i = this.waterTiles.size() - 1; i >= 0; --i){
            this.waterTiles.get(i).update(keyboardState, frameTime);
        }
        updateProtection(keyboardState, frameTime);
    }

    private void updateProtection(KeyboardState keyboardState, double frameTime) {
        if (this.eagleProtectionActive) {
            this.eagleProtectionTimer += frameTime;
            if (this.eagleProtectionTimer >= EAGLE_PROTECTION_LIFE_TIME) {
                this.eagleProtectionTimer = 0;
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

    private void specifyEnemyTankAppearancePositions() {
        for (int i = 0; i < 3; ++i) {
            this.enemyTankAppearancePositions.add(new Point(6 * i
                    * Game.TILE_SIZE, 0));
        }
    }

    private void specifyFireSpots() {
        this.firePoints.add(new FiringSpot(new Spot(24, 6, true), Direction.EAST));
        this.firePoints.add(new FiringSpot(new Spot(23, 6, true), Direction.EAST));
        this.firePoints.add(new FiringSpot(new Spot(24, 7, true), Direction.EAST));
        this.firePoints.add(new FiringSpot(new Spot(23, 7, true), Direction.EAST));
        this.firePoints.add(new FiringSpot(new Spot(24, 17, true), Direction.WEST));
        this.firePoints.add(new FiringSpot(new Spot(23, 17, true), Direction.WEST));
        this.firePoints.add(new FiringSpot(new Spot(24, 18, true), Direction.WEST));
        this.firePoints.add(new FiringSpot(new Spot(23, 18, true), Direction.WEST));
        this.firePoints.add(new FiringSpot(new Spot(20, 11, true), Direction.SOUTH));
        this.firePoints.add(new FiringSpot(new Spot(20, 12, true), Direction.SOUTH));
        this.firePoints.add(new FiringSpot(new Spot(20, 13, true), Direction.SOUTH));
    }

    private void specifyEagleProtectionPositions() {
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

    private void updateClearanceMap() {
        for (int row = 0; row < this.tiles.length; ++row) {
            for (int col = 0; col < this.tiles[row].length; ++col) {
                //System.out.println("row = " + row + " col = " + col);
                if (!getTile(row, col).getType().isTraversable()) {
                    continue;
                }
                int currentClearance = 1;
                expansion:
                while (currentClearance < this.tiles.length) {
                    //System.out.println("currentClearance = " + currentClearance);
                    for (int i = 0; i < currentClearance; ++i) {
                        for (int j = 0; j < currentClearance; ++j) {
                            //System.out.println("i = " + i + " j = " + j);
                            int r = row + i;
                            int c = col + j;
                            if (!areCoordinatesValid(r, c)) {
                                break expansion;
                            }
                            if (getTile(r, c).getType().isTraversable()) {
                                this.clearanceMap[r][c] = currentClearance - 1;
                            } else {
                                break expansion;
                            }
                        }
                    }
                    ++currentClearance;
                }
            }
        }

        this.clearanceMapChanged = false;
    }
}
