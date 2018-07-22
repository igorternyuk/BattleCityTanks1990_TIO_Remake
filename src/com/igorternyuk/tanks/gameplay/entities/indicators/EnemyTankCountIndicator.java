package com.igorternyuk.tanks.gameplay.entities.indicators;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class EnemyTankCountIndicator extends Entity {

    private static final Color BACKGROUND_COLOR = new Color(99, 99, 99);
    private static final int TANK_SIGNS_IN_ROW = 2;
    private static final int MAX_ROWS = LevelState.TANKS_TOTAL
            / TANK_SIGNS_IN_ROW;
    private int tankCount = 0;
    private int rows;
    private Sprite sprite;

    public EnemyTankCountIndicator(LevelState level, double x, double y) {
        super(level, EntityType.INDICATOR, x, y, 0, Direction.NORTH);
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.ENEMY_TANK_SIGN);
        this.sprite = new Sprite(image, this.x, this.y, Game.SCALE);
        this.sprite.setSourceRect(new Rectangle(0, 0, Game.HALF_TILE_SIZE,
                Game.HALF_TILE_SIZE));
    }

    public int getTankCount() {
        return tankCount;
    }

    public void setTankCount(int tankCount) {
        this.tankCount = tankCount;
    }

    @Override
    public int getWidth() {
        return this.tankCount > TANK_SIGNS_IN_ROW
                ? TANK_SIGNS_IN_ROW * Game.HALF_TILE_SIZE
                : Game.HALF_TILE_SIZE;
    }

    @Override
    public int getHeight() {
        if (this.tankCount % TANK_SIGNS_IN_ROW != 0) {
            ++rows;
        }
        return rows * Game.HALF_TILE_SIZE;
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        super.update(keyboardState, frameTime);
        this.tankCount = this.level.getHangar().size();
        this.rows = this.tankCount / TANK_SIGNS_IN_ROW;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        drawBackground(g);
        drawTankSigns(g);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect((int) (getX() * Game.SCALE),
                (int) (getY() * Game.SCALE),
                (int) (Game.HALF_TILE_SIZE * TANK_SIGNS_IN_ROW * Game.SCALE),
                (int) (Game.HALF_TILE_SIZE * MAX_ROWS * Game.SCALE));
    }
    
    private void drawTankSigns(Graphics2D g){
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < TANK_SIGNS_IN_ROW; ++j) {
                double posX = getX() + j * Game.HALF_TILE_SIZE;
                double posY = getY() + i * Game.HALF_TILE_SIZE;
                this.sprite.setPosition(posX, posY);
                this.sprite.draw(g);
            }
        }
        if (this.tankCount % TANK_SIGNS_IN_ROW == 1) {
            this.sprite.setPosition(getX(), getY() + this.rows
                    * Game.HALF_TILE_SIZE);
            this.sprite.draw(g);
        }
    }

}
