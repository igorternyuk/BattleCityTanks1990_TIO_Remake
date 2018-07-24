package com.igorternyuk.tanks.gameplay.entities.indicators;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gamestate.LevelState;
import com.igorternyuk.tanks.graphics.images.Sprite;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.utils.Painter;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author igor
 */
public class GameInfoPanel extends Entity {

    private Player player;
    private EnemyTankCountIndicator enemyIndicator;
    private Sprite sprite;

    public GameInfoPanel(LevelState level, double x, double y) {
        super(level, EntityType.RIGHT_PANEL, x, y, 0, Direction.NORTH);
        this.player = this.level.getPlayer();
        BufferedImage image = SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.RIGHT_PANEL);
        this.sprite = new Sprite(image, x, y, Game.SCALE);
        this.sprite.setSourceRect(new Rectangle(0, 0, 32, 240));
        this.enemyIndicator = new EnemyTankCountIndicator(level, 0, 0);
        this.enemyIndicator.setTankCount(20);
        this.attachChild(this.enemyIndicator);
        this.enemyIndicator.setPosition(Game.HALF_TILE_SIZE, Game.HALF_TILE_SIZE
                * 3);
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
        this.sprite.update(keyboardState, frameTime);
        super.update(keyboardState, frameTime);
    }

    @Override
    public void draw(Graphics2D g) {
        this.sprite.draw(g);
        super.draw(g);
        int playerLives = this.player.getLives();
        if(playerLives < 0){
            playerLives = 0;
        }
        Painter.drawNumber(g, playerLives,
                Painter.DIGIT_DEFAULT_COLOR,
                (int) (28 * Game.HALF_TILE_SIZE * Game.SCALE),
                (int) (18 * Game.HALF_TILE_SIZE * Game.SCALE), Game.SCALE);
        g.drawImage(SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.GRAY_TILE), (int) (28
                * Game.HALF_TILE_SIZE * Game.SCALE), (int) (21
                * Game.HALF_TILE_SIZE * Game.SCALE), null);
        Painter.drawNumber(g, this.level.getStageNumber(),
                Painter.DIGIT_DEFAULT_COLOR,
                (int) (27 * Game.HALF_TILE_SIZE * Game.SCALE),
                (int) (25 * Game.HALF_TILE_SIZE * Game.SCALE), Game.SCALE);
        if(this.level.getStageNumber() < 10){
          g.drawImage(SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.GRAY_TILE), (int) (28
                * Game.HALF_TILE_SIZE * Game.SCALE), (int) (25
                * Game.HALF_TILE_SIZE * Game.SCALE), null);  
        }
    }

}
