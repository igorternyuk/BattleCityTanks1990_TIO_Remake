package com.igorternyuk.tanks.utils;

import com.igorternyuk.tanks.gameplay.Game;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igor
 */
public class Files {

    public static void writeMapToFile(String pathToFile, int[][] map) {

        try (PrintWriter writer = new PrintWriter(new File(Files.class.
                getResource(pathToFile).getPath()))) {
            for (int row = 0; row < map.length; ++row) {
                for (int col = 0; col < map[row].length; ++col) {
                    writer.print(String.valueOf(map[row][col] + " "));
                }
                if (row != map.length - 1) {
                    writer.println("");
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int[][] loadMapFromFile(String pathToFile) {
        int[][] map = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Files.class.getResourceAsStream(pathToFile)))) {
            String currentLine;
            String delimeter = "\\s+";
            int row = 0;
            while ((currentLine = br.readLine()) != null) {
                String[] tokens = currentLine.split(delimeter);
                for (int col = 0; col < tokens.length; ++col) {
                    map[row][col] = Integer.parseInt(tokens[col]);
                }
                ++row;
                if (row >= Game.TILES_IN_HEIGHT) {
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE,
                    "Could not find the map file", ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE,
                    "Could not parse the map file", ex);
        }
        return map;
    }
}
