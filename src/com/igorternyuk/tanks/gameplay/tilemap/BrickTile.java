package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class BrickTile extends Tile {

    private static final int SUBTILE_COUNT = 4;
    private static final int DIMENSION = (int) (Math.sqrt(SUBTILE_COUNT));

    private class WallQuarter {

        private boolean exists;
        private Rectangle boundingRect;

        public WallQuarter(boolean exists, Rectangle boundingRect) {
            this.exists = exists;
            this.boundingRect = boundingRect;
        }

        public void draw(Graphics2D g) {
            if (this.exists) {
                return;
            }
            g.setColor(Color.black);
            g.fillRect((int) (boundingRect.x * scale),
                    (int) (boundingRect.y * scale),
                    (int) (boundingRect.width * scale),
                    (int) (boundingRect.height * scale));
        }
    }

    private WallQuarter[][] wall = new WallQuarter[DIMENSION][DIMENSION];

    protected BrickTile(Point position, BufferedImage image, double scale) {
        super(TileType.BRICK, position, image, scale);
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                this.wall[row][col] = new WallQuarter(true, new Rectangle(
                        (int) getX()
                        + col * Game.QUARTER_TILE_SIZE, (int) getY() + row
                        * Game.QUARTER_TILE_SIZE, Game.QUARTER_TILE_SIZE,
                        Game.QUARTER_TILE_SIZE));
            }
        }
    }

    public boolean isAlive() {
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                if (this.wall[row][col].exists) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkIfCollision(Entity entity) {
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                if (this.wall[row][col].exists
                        && entity.getBoundingRect().intersects(
                                this.wall[row][col].boundingRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handleProjectileCollision(Projectile projectile) {
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                if (projectile.getBoundingRect().intersects(
                        this.wall[row][col].boundingRect)) {
                    this.wall[row][col].exists = false;
                }
            }
        }
    }

    @Override
    public void handleTankCollision(Tank tank) {
        outer:
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                if(!this.wall[row][col].exists){
                    continue;
                }
                Rectangle tankBoundingRect = tank.getBoundingRect();
                Rectangle currWallQuarterBoundingRect =
                        this.wall[row][col].boundingRect;
                if (tankBoundingRect.intersects(currWallQuarterBoundingRect)) {
                    Rectangle intersection = tankBoundingRect.intersection(
                            currWallQuarterBoundingRect);
                    resetCollidingEntityPosition(intersection, tank);
                    break outer;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                this.wall[row][col].draw(g);
            }
        }
    }

}
