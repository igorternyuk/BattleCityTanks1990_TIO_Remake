package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.resourcemanager.ImageIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igor
 */
public class TileMap {

    private static final double SCROLL_SPEED = 0.1;
    private static final int SNAIL_CODE = 55;
    private static final int SPIDER_CODE = 66;
    private static final int POWERUP_CODE = 77;
    private static final int PLAYER_CODE = 100;
    private double cameraX, cameraY;

    private int[][] map;
    private int tileSize;
    private int numRows, numCols;
    private int width, height;

    private ResourceManager resourceManager;
    private BufferedImage tileSet;
    private int numTilesX, numTilesY;
    private Tile[][] tiles;

    private int rowOffset, colOffset;
    private int numRowsToDraw, numColsToDraw;
    private Map<Integer, EntityType> entityCodeTypeMap = new HashMap<>();
    private Map<Point, EntityType> entityPositions = new HashMap<>();

    public TileMap(ResourceManager resourceManager, int tileSize) {
        this.tileSize = tileSize;
        this.resourceManager = resourceManager;
        this.numColsToDraw = Game.WIDTH / this.tileSize;
        this.numRowsToDraw = Game.HEIGHT / this.tileSize;
        this.cameraX = 0;
        this.cameraY = 0;
        putEntityCodesToTheMap();
    }

    private void putEntityCodesToTheMap() {
        /*this.entityCodeTypeMap.put(PLAYER_CODE, EntityType.PLAYER);
        this.entityCodeTypeMap.put(SNAIL_CODE, EntityType.SNAIL);
        this.entityCodeTypeMap.put(SPIDER_CODE, EntityType.SPIDER);
        this.entityCodeTypeMap.put(POWERUP_CODE, EntityType.POWERUP);*/
    }

    private boolean isEntityCode(int code) {
        return this.entityCodeTypeMap.containsKey(code);
    }

    public Map<Point, EntityType> getEntityPositions() {
        return this.entityPositions;
    }

    public int getCameraX() {
        return (int) this.cameraX;
    }

    public int getCameraY() {
        return (int) this.cameraY;
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private boolean areCoordinatesValid(int row, int col) {
        return row >= 0 && row < this.map.length
                && col >= 0 && col < this.map[row].length;
    }

    public TileType getTileType(int row, int col) {
        if (areCoordinatesValid(row, col)) {
            int val = this.map[row][col];
            if (isEntityCode(val)) {
                return TileType.BRICKS;
            }
            int r = val / this.numTilesX;
            int c = val % this.numTilesX;
            return this.tiles[r][c].getType();
        } else {
            return TileType.BRICKS;
        }
    }

    public void setCameraPositionX(double x) {
        this.cameraX += (x - this.cameraX) * SCROLL_SPEED;
        this.colOffset = (int) (this.cameraX / this.tileSize);
    }

    public void setCameraPositionY(double y) {
        this.cameraY += (y - this.cameraY) * SCROLL_SPEED;
        this.rowOffset = (int) (this.cameraY / this.tileSize);
    }

    public void loadMap(String pathToMapFile) {
        InputStream in = this.getClass().getResourceAsStream(pathToMapFile);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(in));
        try {
            this.numCols = Integer.parseInt(bufferedReader.readLine());
            this.numRows = Integer.parseInt(bufferedReader.readLine());
            this.width = this.numCols * this.tileSize;
            this.height = this.numRows * this.tileSize;
            this.map = new int[this.numRows][this.numCols];
            System.out.println("numRows = " + numRows);
            System.out.println("numCols = " + numCols);
            String delimeter = "\\s+";
            for (int row = 0; row < this.numRows; ++row) {
                String currentLine = bufferedReader.readLine();
                String[] values = currentLine.split(delimeter);
                for (int col = 0; col < this.numCols; ++col) {
                    int val = Integer.parseInt(values[col]);
                    this.map[row][col] = val;
                    if (isEntityCode(val)) {
                        Point pos = new Point(col * this.tileSize, row
                                * this.tileSize);
                        this.entityPositions.put(pos, this.entityCodeTypeMap.
                                get(val));
                    }
                }
            }

        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(TileMap.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void loadTileSet(String pathToTileSetFile) {
        /*if (this.resourceManager.loadImage(ImageIdentifier.TILE_SET,
                pathToTileSetFile)) {
            System.out.println("The tile set was successfully loaded");
        }
        this.tileSet = this.resourceManager.getImage(ImageIdentifier.TILE_SET);*/
        this.numTilesX = this.tileSet.getWidth() / this.tileSize;
        this.numTilesY = this.tileSet.getHeight() / this.tileSize;

        System.out.println("this.numTilesX = " + this.numTilesX);
        System.out.println("this.numTilesY = " + this.numTilesY);

        this.tiles = new Tile[this.numTilesY][this.numTilesX];

        for (int i = 0; i < this.numTilesY; ++i) {
            for (int j = 0; j < this.numTilesX; ++j) {
                BufferedImage image = this.tileSet.
                        getSubimage(j * this.tileSize,
                                i * this.tileSize, this.tileSize, this.tileSize);
                Tile tile = new Tile(image, getTileTypeForTheTileSet(i, j));
                this.tiles[i][j] = tile;
            }
        }
    }

    private TileType getTileTypeForTheTileSet(int row, int col) {
        return TileType.ICE;
    }

    public void draw(Graphics2D g) {
        final int rowMax = this.rowOffset + this.numRowsToDraw;
        final int colMax = this.colOffset + this.numColsToDraw;
        for (int row = this.rowOffset; row < rowMax; ++row) {
            if (row >= this.numRows) {
                break;
            }
            for (int col = this.colOffset; col < colMax; ++col) {
                if (col >= this.numCols) {
                    break;
                }
                int val = this.map[row][col];
                if (isEntityCode(val)) {
                    continue;
                }
                int r = val / this.numTilesX;
                int c = val % this.numTilesX;
                BufferedImage image = this.tiles[r][c].getImage();
                g.drawImage(image,
                        (int) Game.SCALE * (col * this.tileSize
                        - (int) cameraX),
                        (int) Game.SCALE * (row * this.tileSize
                        - (int) cameraY),
                        (int) Game.SCALE * this.tileSize,
                        (int) Game.SCALE * this.tileSize, null);
            }
        }
    }
}
