package com.igorternyuk.tanks.gameplay.pathfinder;

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
import java.util.SortedSet;
import java.util.TreeSet;

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
        this.tileMap.print();
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
            int newCol = spot.col + dir.getVx();
            int newRow = spot.row + dir.getVy();
            if (areCoordinatesValid(newRow, newCol)
                    && grid[newRow][newCol].traversable) {
                neighbours.put(dir, grid[newRow][newCol]);
            }
        }
        return neighbours;
    }
    
    public boolean calcPath(Spot start, Spot end, int agentDimension) {
        System.out.println("Trying to calculate the shortest possible path...");
        int[][] clearanceMap = this.tileMap.getClearanceMap();
        
        Set<Spot> closedSet = new HashSet<>();
        
        Queue<Spot> openSet = new PriorityQueue<>((firstSpot, secondSpot) -> {
            return Double.compare(firstSpot.evaluation, secondSpot.evaluation);
        });

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
                boolean gapOK = currGap >= agentDimension;
                
                if (!alreadyClosed && gapOK) {
                    double tmpCost = current.cost + 1;
                    
                    if (openSet.contains(currNeighbour)) {
                        //System.out.println("Neighbour is already in the open list");
                        //System.out.println("tmpCost = " + tmpCost);
                        //System.out.println("currNeighbour.cost = " + currNeighbour.cost);
                        if (tmpCost < currNeighbour.cost) {
                            currNeighbour.cost = tmpCost;
                            currNeighbour.evaluation = currNeighbour.cost
                                    + currNeighbour.heuristic;
                            currNeighbour.prev = current;
                            currNeighbour.dirFromPrev = direction;
                            System.out.println("Improving the cost");
                        } else {
                            System.out.println("Nothing to improve");
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
    
    public static class Spot {

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

        public void draw(Graphics2D g){
            g.setColor(Color.cyan);
            g.fillRect(col * 8 * 2, row * 8 * 2, 8 * 2, 8 * 2);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + this.row;
            hash = 47 * hash + this.col;
            hash = 47 * hash + (this.traversable ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Spot other = (Spot) obj;
            if (this.row != other.row) {
                return false;
            }
            if (this.col != other.col) {
                return false;
            }
            if (this.traversable != other.traversable) {
                return false;
            }
            return true;
        }

    }
}
