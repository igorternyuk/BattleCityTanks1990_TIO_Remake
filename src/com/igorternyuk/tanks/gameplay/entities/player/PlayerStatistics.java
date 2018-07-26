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
    private int totalScore = 0;
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
        this.totalScore = 0;
    }

    public int getTotalScore() {
        return this.totalScore;
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
        this.totalScore += enemyTank.getScore();
        this.stageScore += enemyTank.getScore();
        ++this.killedTankCount;
    }

    public void addPowerUp(PowerUp powerUp) {
        this.totalScore += powerUp.getScore();
        this.stageScore += powerUp.getScore();
    }

    private void resetKilledTanksMap() {
        for (EnemyTankType type : EnemyTankType.values()) {
            this.killedEnemyTanks.put(type, 0);
        }
    }
}
