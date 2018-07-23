package com.igorternyuk.tanks.gameplay.pathfinder;

import com.google.common.base.Objects;
import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author igor
 */
public class Pathfinder {

    private TileMap tileMap;
    private Spot[][] grid;
    private List<Spot> optimalPath = new ArrayList<>();

    public Pathfinder(TileMap tileMap) {
        setTileMap(tileMap);
    }
    
    public final void setTileMap(TileMap tileMap){
        this.tileMap = tileMap;
        this.grid = new Spot[this.tileMap.getTilesInHeight()][this.tileMap.
                getTilesInWidth()];
        fillGrid();
        //this.tileMap.print();
    }

    public TileMap getTileMap() {
        return this.tileMap;
    }

    public Spot[][] getGrid() {
        return this.grid;
    }

    public List<Spot> getOptimalPath() {
        return this.optimalPath;
    }
    
    public Map<Direction, Spot> getNeighbours(Spot spot) {
        Map<Direction, Spot> neighbours = new HashMap<>();
        for (Direction dir : Direction.values()) {
            int newCol = spot.col + dir.getDx();
            int newRow = spot.row + dir.getDy();
            if (areCoordinatesValid(newRow, newCol)
                    && grid[newRow][newCol].traversable) {
                neighbours.put(dir, grid[newRow][newCol]);
            }
        }
        return neighbours;
    }
    
    public boolean calcPath(Spot start, Spot end, int agentDimension) {
        
        int[][] clearanceMap = this.tileMap.getClearanceMap();        
        Set<Spot> closedSet = new HashSet<>();        
        Queue<Spot> openSet = new PriorityQueue<>();

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Spot current = openSet.poll();
            
            if (current.equals(end)) {
                restoreOptimalPath(current);
                return true;
            }

            closedSet.add(current);
            Map<Direction, Spot> neighbours = getNeighbours(current);
            neighbours.forEach((direction, currNeighbour) -> {
                
                boolean alreadyClosed = closedSet.contains(currNeighbour);
                int currGap = clearanceMap[currNeighbour.row][currNeighbour.col];
                boolean clearanceSufficient = currGap >= agentDimension;
                
                if (!alreadyClosed && clearanceSufficient) {
                    
                    double tmpCost = current.cost + 1;
                    
                    if (openSet.contains(currNeighbour)) {
                        if (tmpCost < currNeighbour.cost) {
                            currNeighbour.cost = tmpCost;
                            currNeighbour.evaluation = currNeighbour.cost
                                    + currNeighbour.heuristic;
                            currNeighbour.prev = current;
                            currNeighbour.dirFromPrev = direction;
                        }
                    } else {
                        currNeighbour.cost = tmpCost;
                        currNeighbour.heuristic = heuristicFunction(
                                currNeighbour, end);
                        currNeighbour.evaluation = currNeighbour.cost
                                + currNeighbour.heuristic;
                        currNeighbour.prev = current;
                        currNeighbour.dirFromPrev = direction;
                        openSet.add(currNeighbour);
                    }
                }
            });
        }

        return false;
    }

    private void restoreOptimalPath(Spot end) {
        this.optimalPath.clear();
        Spot current = end;
        while(current.prev != null){
            this.optimalPath.add(current);
            current = current.prev;
        }
        Collections.reverse(this.optimalPath);
    }

    private boolean areCoordinatesValid(int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col
                < grid[row].length;
    }

    private double heuristicFunction(Spot source, Spot target) {
        return Math.abs(source.col - target.col) + Math.abs(source.row
                - target.row);
    }
    
    private void fillGrid() {
        for (int row = 0; row < this.grid.length; ++row) {
            for (int col = 0; col < this.grid[row].length; ++col) {
                boolean traversable = this.tileMap.getTileType(row, col).
                        isTraversable();
                this.grid[row][col] = new Spot(row, col, traversable);
            }
        }
    }
    
    public static class Spot implements Comparable<Spot>{

        private int row;
        private int col;
        private boolean traversable;
        private double evaluation;
        private double cost;
        private double heuristic;
        private Spot prev;
        private Direction dirFromPrev;

        public Spot(int row, int col, boolean traversable) {
            this.row = row;
            this.col = col;
            this.traversable = traversable;
        }

        public int getRow() {
            return this.row;
        }

        public int getCol() {
            return this.col;
        }

        public boolean isTraversable() {
            return this.traversable;
        }

        public Spot getPrev() {
            return this.prev;
        }

        public Direction getDirFromPrev() {
            return this.dirFromPrev;
        }
        
        public double distanceEuclidian(Spot target){
            int dx = (this.col - target.col) * Game.HALF_TILE_SIZE;
            int dy = (this.row - target.row) * Game.HALF_TILE_SIZE;
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        public double distanceManhattan(Spot target){
            int dx = (this.col - target.col) * Game.HALF_TILE_SIZE;
            int dy = (this.row - target.row) * Game.HALF_TILE_SIZE;
            return dx + dy;
        }

        public void draw(Graphics2D g){
            g.setColor(Color.cyan);
            g.fillRect((int)(col * Game.HALF_TILE_SIZE * Game.SCALE)
                    , (int)(row * Game.HALF_TILE_SIZE * Game.SCALE)
                    , (int)(Game.HALF_TILE_SIZE * Game.SCALE)
                    , (int)(Game.HALF_TILE_SIZE * Game.SCALE));
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + this.row;
            hash = 47 * hash + this.col;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final Spot other = (Spot) obj;
            
            return Objects.equal(this.row, other.row)
                    && Objects.equal(this.col, other.col);
        }

        @Override
        public int compareTo(Spot other) {
            return Double.compare(this.evaluation, other.evaluation);
        }

    }
}
