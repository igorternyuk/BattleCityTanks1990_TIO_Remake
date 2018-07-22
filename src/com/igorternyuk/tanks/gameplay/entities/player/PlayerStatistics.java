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
     private static final Font FONT_PLAYER_STATISTICS = new Font("Verdana",
            Font.BOLD, 24);
    private Player player;
    private int score = 0;
    private Map<EnemyTankType, Integer> killedEnemyTanks = new HashMap<>();

    public PlayerStatistics(Player player) {
        this.player = player;
        resetKilledTanksMap();
    }
    
    public void reset(){
        this.score = 0;
        resetKilledTanksMap();
    }

    public int getScore() {
        return this.score;
    }

    public Map<EnemyTankType, Integer> getKilledEnemyTanks() {
        return Collections.unmodifiableMap(this.killedEnemyTanks);
    }
    
    public void addKilledTank(EnemyTank enemyTank) {
        int killedTanksEithSuchType = this.killedEnemyTanks.get(enemyTank.
                getType());
        this.killedEnemyTanks.put(enemyTank.getType(), killedTanksEithSuchType
                + 1);
        this.score = enemyTank.getScore();
    }
    
    public void addPowerUp(PowerUp powerUp){
        this.score += powerUp.getScore();
    }
    
    public void update(KeyboardState keyboardState, double frameTime){
    }
    
    public void draw(Graphics2D g){
        g.setColor(Color.red);
        g.setFont(FONT_PLAYER_STATISTICS);

        EnemyTankType[] enemyTankTypes = EnemyTankType.values();

        for (int i = 0; i < enemyTankTypes.length; ++i) {
            EnemyTankType currEnemyTankType = enemyTankTypes[i];
            BufferedImage currTankTypeImage = SpriteSheetManager.getInstance().
                    fetchStatisticsTankImage(currEnemyTankType);
            int killedTanksWithCurrType = this.killedEnemyTanks.get(currEnemyTankType);
            g.drawImage(currTankTypeImage, 5 + i * 64, 27 * Game.HALF_TILE_SIZE,
                    null);
            g.drawString(killedTanksWithCurrType + "", 69 + i * 64, 27
                    * Game.HALF_TILE_SIZE + 10);
        }

        String playerStatistics = "Score: " + this.player.getScore();
        g.drawString(playerStatistics, 5, (int) (27 * Game.HALF_TILE_SIZE
                * Game.SCALE + 5));
    }
    
    private void resetKilledTanksMap(){
        for(EnemyTankType type: EnemyTankType.values()){
            this.killedEnemyTanks.put(type, 0);
        }
    }
}
