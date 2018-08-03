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
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import com.igorternyuk.tanks.utils.Painter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor
 */
public class GameInfoPanel extends Entity {

    private List<Player> players = new ArrayList<>();
    private EnemyTankCountIndicator enemyIndicator;
    private Sprite sprite;

    public GameInfoPanel(LevelState level, double x, double y) {
        super(level, EntityType.RIGHT_PANEL, x, y, 0, Direction.NORTH);
        this.players = this.level.getPlayers();
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
        int gameFieldBottom = Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT;
        g.setColor(Color.black);
        g.fillRect(0, gameFieldBottom, Game.WIDTH, Game.STATISTICS_PANEL_HEIGHT);
        super.draw(g);
        drawPlayerLives(g);
        drawStageNumber(g);
    }
    
    private void drawPlayerLives(Graphics2D g){
        for (int i = 0; i < this.players.size(); ++i) {
            int playerLives = this.players.get(i).getLives();
            if (playerLives < 0) {
                playerLives = 0;
            }
            Painter.drawNumber(g, playerLives,
                    Painter.DIGIT_DEFAULT_COLOR,
                    (int) (28 * Game.HALF_TILE_SIZE * Game.SCALE),
                    (int) ((18 + 3 * i) * Game.HALF_TILE_SIZE * Game.SCALE),
                    Game.SCALE);
        }
        
        if (this.players.size() == 1) {
            g.drawImage(SpriteSheetManager.getInstance().get(
                SpriteSheetIdentifier.GRAY_TILE), (int) (28
                * Game.HALF_TILE_SIZE * Game.SCALE), (int) (21
                * Game.HALF_TILE_SIZE * Game.SCALE), null);
        }
    }

    private void drawStageNumber(Graphics2D g) {
        Painter.drawNumber(g, this.level.getStageCount(),
                Painter.DIGIT_DEFAULT_COLOR,
                (int) (27 * Game.HALF_TILE_SIZE * Game.SCALE),
                (int) (25 * Game.HALF_TILE_SIZE * Game.SCALE), Game.SCALE);
        if (this.level.getStageNumber() < 10) {
            g.drawImage(SpriteSheetManager.getInstance().get(
                    SpriteSheetIdentifier.GRAY_TILE), (int) (28
                    * Game.HALF_TILE_SIZE * Game.SCALE), (int) (25
                    * Game.HALF_TILE_SIZE * Game.SCALE), null);
        }
    }
}
