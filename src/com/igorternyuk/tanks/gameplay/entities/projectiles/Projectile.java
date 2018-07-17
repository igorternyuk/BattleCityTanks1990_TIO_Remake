package com.igorternyuk.tanks.gameplay.entities.projectiles;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.explosion.ExplosionType;
import com.igorternyuk.tanks.gameplay.tilemap.BrickTile;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gameplay.tilemap.TileType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class Projectile extends Entity {

    private ProjectileType type;
    private int damage = 25;
    private boolean antiarmour = false;
    private Sprite sprite;

    public Projectile(LevelState level, ProjectileType projectileType, double x,
            double y, double speed, Direction direction) {
        super(level, EntityType.PROJECTILE, x, y, speed, direction);
        this.type = projectileType;
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.PROJECTILE);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        updateSprite();
    }

    public ProjectileType getType() {
        return this.type;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

    public boolean isAntiarmour() {
        return this.antiarmour;
    }

    public void setAntiarmour(boolean antiarmour) {
        this.antiarmour = antiarmour;
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !isOutOfBounds();
    }

    public void explode() {
        super.explode(ExplosionType.SMALL);
        destroy();
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        updateSprite();
    }

    private void updateSprite() {
        this.sprite.setSourceRect(ProjectileType.getSourceRect(this.direction));
        this.sprite.setPosition(this.x, this.y);
    }

    @Override
    public int getWidth() {
        return this.sprite.getWidth();
    }

    @Override
    public int getHeight() {
        return this.sprite.getHeight();
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        move(frameTime);
        handleMapCollision();
        this.sprite.setPosition(getX(), getY());
    }

    private void handleMapCollision() {
        TileMap tileMap = this.level.getTileMap();
        final int rowMin = tileMap.fixRowIndex((int) (top()
                / Game.HALF_TILE_SIZE));
        final int rowMax = tileMap.fixRowIndex((int) (bottom() - 1)
                / Game.HALF_TILE_SIZE);
        final int colMin = tileMap.fixColumnIndex((int) (left()
                / Game.HALF_TILE_SIZE));
        final int colMax = tileMap.fixColumnIndex((int) (right() - 1)
                / Game.HALF_TILE_SIZE);

        boolean collision = false;

        for (int row = rowMin; row <= rowMax; ++row) {
            for (int col = colMin; col <= colMax; ++col) {
                Tile tile = tileMap.getTile(row, col);
                if (tile.getType() == TileType.BRICK) {
                    BrickTile brickTile = (BrickTile) tile;
                    if (brickTile.checkIfCollision(this)) {
                        brickTile.handleProjectileCollision(this);
                        collision = true;
                    }

                } else if (tile.getType() == TileType.METAL) {

                }
            }
        }

        if (collision) {
            this.explode();
        }
    }

    /*
    protected void handleMapCollision(Direction direction) {
        final int rowMin = (int) this.top() / this.tileSize;
        final int rowMax = (int) (this.bottom() - 1) / this.tileSize;
        final int colMin = (int) this.left() / this.tileSize;
        final int colMax = (int) (this.right() - 1) / this.tileSize;

        outer:
        for (int row = rowMin; row <= rowMax; ++row) {
            for (int col = colMin; col <= colMax; ++col) {
                if (this.tileMap.getTileType(row, col).equals(TileType.BLOCKED)) {
                    if (direction == Direction.VERTICAL) {
                        handleVerticalCollision(row, col);
                    } else if (direction == Direction.HORIZONTAL) {
                        handleHorizontalCollision(row, col);
                    }
                    //If we've got a collision we can terminate the further checking
                    break outer;
                }
            }
        }
    }
     */
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        this.sprite.draw(g);
    }

}
