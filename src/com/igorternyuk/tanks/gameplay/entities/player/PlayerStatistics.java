package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankType;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author igor
 */
public class PlayerStatistics {

    private static final Color COLOR_KILLED_TANKS_POINTS =
            new Color(0, 148, 255);
    private Player player;
    private int score = 0;
    private int stageScore = 0;
    private int killedTankCount = 0;
    private Map<EnemyTankType, Integer> killedEnemyTanks = new HashMap<>();
    private final Font fontSmaller;
    private final Font fontLarger;

    public PlayerStatistics(Player player) {
        this.player = player;
        resetKilledTanksMap();
        Font font = ResourceManager.getInstance().getFont(
                FontIdentifier.BATTLE_CITY);
        this.fontSmaller = font.deriveFont(Font.BOLD, 12);
        this.fontLarger = font.deriveFont(Font.BOLD, 18);
    }

    public void resetToNextStage() {
        this.killedTankCount = 0;
        this.stageScore = 0;
        resetKilledTanksMap();
    }

    public void resetToNewGame() {
        resetToNextStage();
        this.score = 0;
    }

    public int getScore() {
        return this.score;
    }
    
    public int getStageScore(){
        return this.stageScore;
    }

    public int getKilledTankCount() {
        return this.killedTankCount;
    }

    public Map<EnemyTankType, Integer> getKilledEnemyTanks() {
        return Collections.unmodifiableMap(this.killedEnemyTanks);
    }

    public void addKilledTank(EnemyTank enemyTank) {
        int killedTanksEithSuchType = this.killedEnemyTanks.get(enemyTank.
                getType());
        this.killedEnemyTanks.put(enemyTank.getType(), killedTanksEithSuchType
                + 1);
        this.score += enemyTank.getScore();
        this.stageScore += enemyTank.getScore();
        ++this.killedTankCount;
    }

    public void addPowerUp(PowerUp powerUp) {
        this.score += powerUp.getScore();
        this.stageScore += powerUp.getScore();
    }

    public void update(KeyboardState keyboardState, double frameTime) {
    }

    public void draw(Graphics2D g) {

        g.setColor(Color.black);
        g.fillRect(0, Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT,
                Game.WIDTH, Game.STATISTICS_PANEL_HEIGHT);

        g.setColor(Color.white);
        g.fillRect(0, Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT,
                Game.WIDTH, 3);

        g.setColor(COLOR_KILLED_TANKS_POINTS);
        g.setFont(this.fontSmaller);

        EnemyTankType[] enemyTankTypes = EnemyTankType.values();

        for (int i = 0; i < enemyTankTypes.length; ++i) {
            EnemyTankType currEnemyTankType = enemyTankTypes[i];
            BufferedImage currTankTypeImage = SpriteSheetManager.getInstance().
                    fetchStatisticsTankImage(currEnemyTankType);
            int killedTanksWithCurrType = this.killedEnemyTanks.get(
                    currEnemyTankType);
            int pointsForCurrTankType = killedTanksWithCurrType
                    * currEnemyTankType.getScore();
            int currY = (int) ((Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE + i
                    * Game.TILE_SIZE) * Game.SCALE);
            g.drawImage(currTankTypeImage, 5, currY + 8, null);
            g.drawString(killedTanksWithCurrType + "(PTS: "
                    + pointsForCurrTankType + ") ", 32 + 16, currY + 30);

        }

        int y =
                (int) ((Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE) * Game.SCALE);

        g.setColor(Color.yellow);
        g.fillRect(200, y + 16, this.player.getHealth() / 20 * 32, 32);
        g.setColor(Color.yellow.darker());
        g.setStroke(new BasicStroke(3));
        for (int i = 0; i < 5; ++i) {
            g.drawRect(200 + i * 32, y + 16, 32, 32);
        }
        g.setStroke(new BasicStroke(1));

        g.setFont(this.fontLarger);
        g.setColor(Color.red);

        g.drawString("TOTAL: " + this.killedTankCount, 250, y + 90);
        g.setColor(Color.green);
        g.drawString("SCORE: " + this.score, 250, y + 120);
    }

    private void resetKilledTanksMap() {
        for (EnemyTankType type : EnemyTankType.values()) {
            this.killedEnemyTanks.put(type, 0);
        }
    }
}
