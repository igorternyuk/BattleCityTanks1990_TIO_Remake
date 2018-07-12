package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igor
 */
public class TileMap {

    private int[][] map;
    private int tileSize;
    private int numRows, numCols;
    private int width, height;


    public TileMap(ResourceManager resourceManager, int tileSize) {
        this.tileSize = tileSize;
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

    public void loadMap(String pathToMapFile) {

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(
                        pathToMapFile)));) {
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
                    
                }
            }

        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(TileMap.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void loadTileSet(String pathToTileSetFile) {
    }

    public void draw(Graphics2D g) {
    }
}
