package com.igorternyuk.tanks.gameplay;

/**
 *
 * @author igor
 */
public enum GameMode {
    ONE_PLAYER(1, 4),
    TWO_PLAYERS(2, 6);
    
    private int playerCount;
    private int tanksOnFieldMax;
    
    private GameMode(int playerCount, int tankOnFieldMax) {
        this.playerCount = playerCount;
        this.tanksOnFieldMax = tankOnFieldMax;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public int getTanksOnFieldMax() {
        return this.tanksOnFieldMax;
    }
}
