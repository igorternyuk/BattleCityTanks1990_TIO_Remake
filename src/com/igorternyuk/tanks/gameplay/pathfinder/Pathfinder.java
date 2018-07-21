package com.igorternyuk.tanks.gameplay.pathfinder;

import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private class Spot implements Comparable<Spot> {

        private int row;
        private int col;
        private boolean traversable;
        private double evaluation;
        private double cost;
        private double heuristic;
        private Spot prev;
        private Direction dirToNext;

        public Spot(int row, int col, boolean traversable) {
            this.row = row;
            this.col = col;
            this.traversable = traversable;
        }

        public List<Spot> getNeighbours() {
            List<Spot> neighbours = new ArrayList<>(4);
            for (Direction dir : Direction.values()) {
                int newCol = col + dir.getVx();
                int newRow = row + dir.getVy();
                if (areCoordinatesValid(newRow, newCol)) {
                    neighbours.add(grid[newRow][newCol]);
                }
            }
            return neighbours;
        }

        @Override
        public int compareTo(Spot other) {
            return Double.compare(this.evaluation, other.evaluation);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + this.row;
            hash = 47 * hash + this.col;
            hash = 47 * hash + (this.traversable ? 1 : 0);
            hash =
                    47 * hash + (int) (Double.doubleToLongBits(this.evaluation)
                    ^ (Double.doubleToLongBits(this.evaluation) >>> 32));
            hash =
                    47 * hash + (int) (Double.doubleToLongBits(this.cost)
                    ^ (Double.doubleToLongBits(this.cost) >>> 32));
            hash =
                    47 * hash + (int) (Double.doubleToLongBits(this.heuristic)
                    ^ (Double.doubleToLongBits(this.heuristic) >>> 32));
            hash = 47 * hash + Objects.hashCode(this.prev);
            hash = 47 * hash + Objects.hashCode(this.dirToNext);
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
            return Double.doubleToLongBits(this.evaluation) == Double.doubleToLongBits(
                    other.evaluation);
        }

    }

    public Pathfinder(TileMap tileMap) {
        this.tileMap = tileMap;
        this.grid = new Spot[this.tileMap.getTilesInHeight()][this.tileMap.
                getTilesInWidth()];
    }

    public boolean calcPath() {
        List<Spot> closedSet = new ArrayList<>();
        SortedSet<Spot> openSet = new TreeSet<>();
        Spot s1 = new Spot(0, 0, true);
        s1.evaluation = 145.2;
        openSet.add(s1);
        Spot s2 = new Spot(0, 0, true);
        s2.evaluation = 14.7;
        openSet.add(s2);
        Spot s3 = new Spot(0, 0, true);
        s3.evaluation = 1.2;
        openSet.add(s3);
        Spot s4 = new Spot(0, 0, true);
        s4.evaluation = 18.2;
        openSet.add(s4);
        Spot s5 = new Spot(0, 0, true);
        s5.evaluation = 7.2;
        openSet.add(s5);
        System.out.println("openSet.first() = " + openSet.first().evaluation);
        System.out.println("openSet.last() = " + openSet.last().evaluation);
        openSet.forEach(spot -> System.out.println("Spot.eval = " + spot.evaluation));
        return false;
    }

    private boolean areCoordinatesValid(int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col
                < grid[row].length;
    }

    private double heuristicFunction(Spot source, Spot target) {
        return Math.abs(source.col - target.col) + Math.abs(source.row
                - target.row);
    }

    /*
if(!openSet.isEmpty()){
		let current = openSet.pop();

		if(current === target){
			console.log("Done!");		
			noLoop();
		}

		closedSet.push(current);
		//console.log("current spot x = " + current.x + " y = " + current.y);
		//console.log("--------------------------");
		//console.log("neighbours.length = " + neighbours.length);
		//let neighbours = current.getNeighboursVonNeumann(grid);
		let neighbours = considerDiagonals
						 ? current.getNeighboursMoor(grid)
						 : current.getNeighboursVonNeumann(grid);
		neighbours.forEach(neighbour => {
			if(!closedSet.includes(neighbour)){
				//console.log("currNeighbour x = " + neighbour.x + " y = " + neighbour.y);
				var tmpCost = current.cost + 1;
				if(openSet.includes(neighbour)){
					if(tmpCost < neighbour.cost){
						neighbour.cost = tmpCost;
						neighbour.f = neighbour.cost + neighbour.heuristic;
						neighbour.prev = current;
						//console.log("improving cost");
					}
				} else {
					neighbour.cost = tmpCost;
					neighbour.heuristic = heuristicFunc(neighbour, target);
					neighbour.f = neighbour.cost + neighbour.heuristic;
					neighbour.prev = current;
					openSet.push(neighbour);					
				}
			}		
		});	

		restoreOptimalPath(current);
		openSet.heapify();
		//console.log("--------------------------");		

	} else {
		console.log("No solution!");
		noLoop();
	}
     */
}
