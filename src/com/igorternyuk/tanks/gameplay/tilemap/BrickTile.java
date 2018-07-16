package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
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

    private WallQuarter[][] wall = new WallQuarter[SUBTILE_COUNT][SUBTILE_COUNT];

    public BrickTile(Point position, BufferedImage image, double scale) {
        super(TileType.BRICKS, position, image, scale);
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

    public boolean checkCollision(Entity entity) {
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                if (entity.getBoundingRect().intersects(
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

    public void handleTankCollision(Tank tank) {
        for (int row = 0; row < this.wall.length; ++row) {
            for (int col = 0; col < this.wall[row].length; ++col) {
                Rectangle tankBoundingRect = tank.getBoundingRect();
                Rectangle currWallQuarterBoundingRect =
                        this.wall[row][col].boundingRect;
                if (tankBoundingRect.intersects(currWallQuarterBoundingRect)) {
                    this.wall[row][col].exists = false;
                    Rectangle intersection = tankBoundingRect.intersection(
                            currWallQuarterBoundingRect);
                    Direction currTankDirection = tank.getDirection();
                    Direction oppositeDirection = currTankDirection.
                            getOpposite();
                    if (currTankDirection.isVertical()) {
                        tank.setPosition(tank.getX(), tank.getY()
                                + oppositeDirection.getVy() * intersection.y);
                    } else if (currTankDirection.isHorizontal()) {
                        tank.setPosition(tank.getX() + oppositeDirection.getVx()
                                * intersection.x, tank.getY());
                    }
                    break;
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
