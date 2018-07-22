package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankType;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
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

    private static final Font FONT_SMALLER = new Font("Verdana",
            Font.BOLD, 18);
    private static final Font FONT_LARGER = new Font("Verdana",
            Font.BOLD, 28);
    private Player player;
    private int score = 0;
    private int killedTankCount = 0;
    private Map<EnemyTankType, Integer> killedEnemyTanks = new HashMap<>();

    public PlayerStatistics(Player player) {
        this.player = player;
        resetKilledTanksMap();
    }

    public void reset() {
        this.score = 0;
        resetKilledTanksMap();
    }

    public int getScore() {
        return this.score;
    }
    
    public int getKilledTankCount(){
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
        ++this.killedTankCount;
        System.out.println("Tank killed Score = " + this.score);
    }

    public void addPowerUp(PowerUp powerUp) {
        this.score += powerUp.getScore();
    }

    public void update(KeyboardState keyboardState, double frameTime) {
    }

    public void draw(Graphics2D g) {
        
        g.setColor(Color.black);
        g.fillRect(0, Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT,
                Game.WIDTH, Game.STATISTICS_PANEL_HEIGHT);
        g.setColor(Color.yellow);
        g.setFont(FONT_SMALLER);

        EnemyTankType[] enemyTankTypes = EnemyTankType.values();

        for (int i = 0; i < enemyTankTypes.length; ++i) {
            EnemyTankType currEnemyTankType = enemyTankTypes[i];
            BufferedImage currTankTypeImage = SpriteSheetManager.getInstance().
                    fetchStatisticsTankImage(currEnemyTankType);
            int killedTanksWithCurrType = this.killedEnemyTanks.get(
                    currEnemyTankType);
            int pointsForCurrTankType = killedTanksWithCurrType
                    * currEnemyTankType.getScore();
            int currY =  (int) ((Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE + i
                    * Game.TILE_SIZE) * Game.SCALE);
            g.drawImage(currTankTypeImage, 5, currY + 5, null);
            g.drawString(killedTanksWithCurrType + "(PTS: "
                    + pointsForCurrTankType + ") ", 32 + 5, currY + 23);
            
        }
        
        g.setFont(FONT_LARGER);
        g.setColor(Color.red);
        int y = (int) ((Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE + 3
                    * Game.TILE_SIZE) * Game.SCALE);
        g.drawString("TOTAL: " + this.killedTankCount, 200, y - 32);
        g.setColor(Color.green);
        g.drawString("SCORE: " + this.score, 200, y + 16);
    }

    private void resetKilledTanksMap() {
        for (EnemyTankType type : EnemyTankType.values()) {
            this.killedEnemyTanks.put(type, 0);
        }
    }
}
