package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankType;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ResourceManager;
import com.igorternyuk.tanks.utils.Painter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author igor
 */
public class ScoreScreen {

    private static final Color COLOR_TOTAL_SCORE = new Color(238, 188, 96);
    private static final double DELAY = 0.1;
    private static final double DELAY_AFTER_ANIMATION = 5;
    private Font fontLarger;
    private Font fontSmaller;
    private LevelState level;
    private Player player;
    private Map<EnemyTankType, Integer> currMap = new HashMap<>();
    private EnemyTankType currTankType = EnemyTankType.BASIC;
    private double animationTimer = 0;
    private double afterTimer = 0;
    private boolean animationFinished = false;
    private boolean readyToNextStage = false;

    public ScoreScreen(LevelState level) {
        this.level = level;
        this.player = level.getPlayer();
        this.fontLarger = ResourceManager.getInstance().getFont(
                FontIdentifier.BATTLE_CITY).deriveFont(Font.BOLD, 22);
        this.fontSmaller = ResourceManager.getInstance().getFont(
                FontIdentifier.BATTLE_CITY).deriveFont(Font.BOLD, 18);
        for (EnemyTankType type : EnemyTankType.values()) {
            this.currMap.put(type, 0);
        }
    }

    public boolean isReadyToNextStage() {
        return this.readyToNextStage;
    }

    public void reset() {
        this.animationTimer = 0;
        this.animationFinished = false;
        this.currTankType = EnemyTankType.BASIC;
        this.currMap.keySet().forEach(key -> this.currMap.put(key, 0));
        this.afterTimer = 0;
        this.readyToNextStage = false;
    }

    public void update(KeyboardState keyboardState, double frameTime) {
        if (this.animationFinished && !this.readyToNextStage) {
            this.afterTimer += frameTime;
            System.out.println("this.afterTimer = " + this.afterTimer);
            if (this.afterTimer >= DELAY_AFTER_ANIMATION) {
                this.readyToNextStage = true;
                System.out.println("this.readyToNextStage = "
                        + this.readyToNextStage);
            }
        }

        if (this.animationFinished) {
            return;
        }

        this.animationTimer += frameTime;
        System.out.println("timer = " + this.animationTimer);
        if (this.animationTimer >= DELAY) {
            this.animationTimer = 0;
            int prev = this.currMap.get(this.currTankType);
            ++prev;
            int max = this.player.getStatistics().getKilledEnemyTanks().get(
                    this.currTankType);
            boolean next = false;
            if (prev >= max) {
                prev = max;
                next = true;
            }
            this.currMap.put(this.currTankType, prev);
            if (next) {
                this.currTankType = this.currTankType.next();
                if (this.currTankType == EnemyTankType.BASIC) {
                    this.animationFinished = true;
                }
            }
            ResourceManager.getInstance().getAudio(
                    AudioIdentifier.SCORE_SCREEN).
                    play();
        }
    }

    public void draw(Graphics2D g) {
        g.setFont(this.fontLarger);
        g.setColor(Color.red);
        g.drawString("HI-SCORE", 10, 50);
        g.setColor(COLOR_TOTAL_SCORE);
        g.drawString("" + this.level.getHighestScore(), 250, 50);
        Painter.drawCenteredString(g, "STAGE " + this.level.getStageNumber(),
                fontLarger, Color.white, 120);
        g.setColor(Color.red);
        g.drawString("I-PLAYER", 40, 180);
        g.setColor(COLOR_TOTAL_SCORE);
        g.drawString("" + this.player.getStatistics().getScore(), 250, 180);

        g.setColor(Color.white);
        g.setFont(this.fontSmaller);

        EnemyTankType[] enemyTankTypes = EnemyTankType.values();
        for (int i = 0; i < enemyTankTypes.length; ++i) {
            EnemyTankType currEnemyTankType = enemyTankTypes[i];
            int killedTanksWithCurrType = this.currMap.get(
                    currEnemyTankType);
            int pointsForCurrTankType = killedTanksWithCurrType
                    * currEnemyTankType.getScore();
            int currY = 200 + 48 * i;
            int textY = currY + 25;
            g.drawString(String.valueOf(pointsForCurrTankType), 10, textY);
            g.drawString(" PTS ", 80, textY);
            g.drawString(String.valueOf(killedTanksWithCurrType), 175, textY);
            g.drawString("<", 215, textY);

            BufferedImage currTankTypeImage = SpriteSheetManager.getInstance().
                    fetchStatisticsTankImage(currEnemyTankType);
            g.drawImage(currTankTypeImage, (Game.WIDTH - Game.TILE_SIZE) / 2,
                    currY, null);
        }
        int lineWidth = 6 * Game.TILE_SIZE;
        g.fillRect((Game.WIDTH - lineWidth) / 2, 400, lineWidth, 4);
        int totalTanks = 0;
        Collection<Integer> tankCounts = this.currMap.values();
        Iterator<Integer> it = tankCounts.iterator();
        while (it.hasNext()) {
            totalTanks += it.next();
        }
        g.drawString("TOTAL " + totalTanks, 80, 430);
    }
}
