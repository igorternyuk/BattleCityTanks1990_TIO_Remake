package com.igorternyuk.tanks.gameplay.tilemap;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.tank.Tank;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.utils.Images;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Tile {

    public static Tile createTile(TileType type, Point position,
            BufferedImage image, double scale) {
        if (type == TileType.BRICK) {
            return new BrickTile(position, image, scale);
        } else if (type == TileType.METAL) {
            return new MetalTile(position, image, scale);
        } else if (type == TileType.WATER) {
            return new WaterTile(position, image, scale);
        }
        return new Tile(type, position, image, scale);
    }

    protected TileType type;
    protected Point position;
    protected Rectangle boundingRect;
    protected BufferedImage image;
    protected double scale;

    protected Tile(TileType type, Point position, BufferedImage image,
            double scale) {
        this.type = type;
        this.position = position;
        this.boundingRect = new Rectangle(position, new Dimension(
                Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE));
        this.image = Images.resizeImage(image, scale);
        this.scale = scale;
    }

    public Point getPosition() {
        return this.position;
    }

    public final double getX() {
        return this.position.x;
    }

    public final double getY() {
        return this.position.y;
    }

    public int getWidth() {
        return this.image.getWidth();
    }

    public int getHeight() {
        return this.image.getHeight();
    }

    public int getRow() {
        return (int) (this.position.y / Game.HALF_TILE_SIZE);
    }

    public int getColumn() {
        return (int) (this.position.x / Game.HALF_TILE_SIZE);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public TileType getType() {
        return this.type;
    }

    public double getScale() {
        return this.scale;
    }

    public void update(KeyboardState keyboardState, double frameTime) {

    }
    
    public boolean checkIfCollision(Entity entity){
        if (this.type.isTraversable(entity) && this.type != TileType.BUSH) {
            return false;
        }
        Rectangle tankBoundingRect = entity.getBoundingRect();
        return this.boundingRect.intersects(tankBoundingRect);
    }

    public void handleTankCollision(Tank tank) {
        if (this.type.isTraversable(tank)) {
            return;
        }
        Rectangle tankBoundingRect = tank.getBoundingRect();
        if (this.boundingRect.intersects(tankBoundingRect)) {
            Rectangle intersection = this.boundingRect.intersection(
                    tankBoundingRect);
            resetCollidingEntityPosition(intersection, tank);
        }
    }
    
    protected void resetCollidingEntityPosition(Rectangle intersection,
            Entity entity){
        Direction currTankDirection = entity.getDirection();
            Direction oppositeDirection = currTankDirection.getOpposite();
            if (currTankDirection.isVertical()) {
                entity.setPosition(entity.getX(), entity.getY()
                        + oppositeDirection.getDy() * intersection.height);
            } else if (currTankDirection.isHorizontal()) {
                entity.setPosition(entity.getX() + oppositeDirection.getDx()
                        * intersection.width, entity.getY());
            }
    }

    public void draw(Graphics2D g) {
        g.drawImage(this.image, (int) (getX() * this.scale), (int) (getY()
                * this.scale), null);
    }

}
