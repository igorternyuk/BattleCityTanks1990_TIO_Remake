package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.GameOverMessage;
import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.GameMode;
import com.igorternyuk.tanks.gameplay.GameStatus;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityManager;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.RenderingLayerIdentifier;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUpType;
import com.igorternyuk.tanks.gameplay.entities.castle.Castle;
import com.igorternyuk.tanks.gameplay.entities.castle.CastleState;
import com.igorternyuk.tanks.gameplay.entities.dynamite.Dynamite;
import com.igorternyuk.tanks.gameplay.entities.indicators.GameInfoPanel;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankIdentifier;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.rockets.Rocket;
import com.igorternyuk.tanks.gameplay.entities.splash.Splash;
import com.igorternyuk.tanks.gameplay.entities.splash.SplashType;
import com.igorternyuk.tanks.gameplay.entities.splashing.SplashText;
import com.igorternyuk.tanks.gameplay.entities.tank.Alliance;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.ShootingMode;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankIdentifier;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankType;
import com.igorternyuk.tanks.gameplay.tilemap.Tile;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.gameplay.tilemap.TileType;
import com.igorternyuk.tanks.graphics.images.TextureAtlas;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ImageIdentifier;
import com.igorternyuk.tanks.utils.BrickFont;
import com.igorternyuk.tanks.utils.Files;
import com.igorternyuk.tanks.utils.Time;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igor
 */
public class LevelState extends GameState {

    public static final int TANKS_TOTAL = 20;
    public static final int STAGE_MAX = 72;
    private static final double PLAYER_PROTECTION_DURATION = 10;
    private static final double PLAYER_TANK_FROZEN_DURATION = 5;
    private static final double ENEMY_TANK_FROZEN_DURATION = 13;
    private static final int TANKS_AFTER_FOURTY_STAGE_MAX = 26;
    private static final double POWERUP_TIMER_DELAY = 15;
    private static final double POWERUP_PROBABILITY = 0.6;
    private static final double NEXT_STAGE_SPLASH_DELAY = 6;
    private static final double GAME_OVER_SCREEN_DELAY = 3;
    private static final double GAME_OVER_MESSAGE_DURATION = 9;
    private static final double POWERUP_BLINKING_PERIOD = 0.4;
    private static final Point RIGHT_PANEL_POSITION = new Point(26
            * Game.HALF_TILE_SIZE, 0 * Game.HALF_TILE_SIZE);

    private int playerCount;
    private int tankOnFieldMax;
    private TextureAtlas atlas;
    private SpriteSheetManager spriteSheetManager;
    private Map<EnemyTankIdentifier, BufferedImage> enemyTankSpriteSheetMap;
    private Map<PlayerTankIdentifier, BufferedImage> playerSpriteSheetMap;
    private TileMap tileMap;
    private List<Player> players = new ArrayList<>();
    private Castle castle;
    private EntityManager entityManager;
    private GameInfoPanel rightPanel;
    private int highestScore = 0;
    private int stageNumber = 1;
    private int stageCount = 1;
    private Stack<EnemyTankType> hangar = new Stack<>();
    private Map<PowerUpType, Consumer<Player>> onPowerUpCollectedByPlayerHandlers =
            new HashMap<>();
    private Map<PowerUpType, Consumer<EnemyTank>> onPowerUpCollectedByEnemyHandlers =
            new HashMap<>();
    private Random random = new Random();
    private GameStatus gameStatus = GameStatus.PLAY;
    private boolean loaded = false;
    private boolean scoreScreenActive = false;
    private ScoreScreen scoreScreen;
    private boolean gameOverScreenActive = false;
    private double gameOverScreenTimer = 0;
    private final int[][] enemyGroups = {
        {18, 2, 0, 0}, {14, 4, 0, 2}, {14, 4, 0, 2}, {2, 5, 10, 3},
        {8, 5, 5, 2}, {9, 2, 7, 2}, {7, 4, 6, 3}, {7, 4, 7, 2},
        {6, 4, 7, 3}, {12, 2, 4, 2}, {5, 5, 4, 6}, {0, 6, 8, 6},
        {0, 8, 8, 4}, {0, 4, 10, 6}, {0, 2, 10, 8}, {16, 2, 0, 2},
        {8, 2, 8, 2}, {2, 8, 6, 4}, {4, 4, 4, 8}, {2, 8, 2, 8},
        {6, 2, 8, 4}, {6, 2, 8, 4}, {6, 8, 2, 4}, {0, 10, 4, 6},
        {10, 4, 4, 2}, {0, 8, 2, 10}, {4, 6, 4, 6}, {2, 8, 2, 8},
        {15, 2, 2, 1}, {0, 4, 10, 6}, {4, 8, 4, 4}, {3, 8, 3, 6},
        {6, 4, 2, 8}, {4, 4, 4, 8}, {0, 10, 4, 6}, {0, 6, 4, 10},
        {0, 4, 0, 16}, {0, 2, 1, 17}, {0, 1, 1, 18}, {0, 2, 2, 16}
    };

    private double respawnTimer = 0;
    private double respawnDelay = 0;
    private double powerUpTimer = 0;
    private boolean frozenModeAcive = false;
    private double freezeTimer = 0;
    private GameMode gameMode;
    private Font fontNextStageSplash = new Font("Verdana", Font.BOLD
            | Font.ITALIC, 48);

    private boolean gameOverMessageSliding = false;
    private double gameOverMessageTimer = 0;
    private GameOverMessage gameOverMessage;

    public LevelState(GameStateManager gameStateManager, GameMode mode) {
        super(gameStateManager);
        this.gameMode = mode;
        this.playerCount = this.gameMode.getPlayerCount();
        this.tankOnFieldMax = this.gameMode.getTanksOnFieldMax();
        this.entityManager = new EntityManager(this);
        addRenderingLayers();
        createOnPowerUpCollectedByPlayerHandlers();
        createOnPowerUpCollectedByEnemyHandlers();
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(this.players);
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public int getHighestScore() {
        return this.highestScore;
    }

    public int getStageCount() {
        return this.stageCount;
    }

    @Override
    public void load() {
        System.out.println("Level state loading...");
        loadSounds();
        loadFonts();
        loadImages();
        loadTankSpriteSheetMaps();
        loadMap();
        loadHighestScore();
        startNewGame();
        this.scoreScreen = new ScoreScreen(this);
        this.loaded = true;
    }

    @Override
    public void unload() {
        saveHighestScore();
        for (SpriteSheetIdentifier identifier : SpriteSheetIdentifier.values()) {
            this.spriteSheetManager.remove(identifier);
        }
        this.resourceManager.unloadImage(ImageIdentifier.TEXTURE_ATLAS);
        this.resourceManager.unloadFont(FontIdentifier.BATTLE_CITY);
        unloadSounds();
        this.loaded = false;
    }

    private void unloadSounds() {
        AudioIdentifier[] identifiers = AudioIdentifier.values();
        for (AudioIdentifier identifier : identifiers) {
            this.resourceManager.unloadAudio(identifier);
        }
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {

        if (!this.loaded || this.gameStatus == GameStatus.PAUSED) {
            return;
        }

        checkPlayers();

        if (this.gameOverMessageSliding) {
            this.gameOverMessageTimer += frameTime;
            this.gameOverMessage.update(frameTime);
            if (this.gameOverMessageTimer > GAME_OVER_MESSAGE_DURATION) {
                this.gameOverScreenTimer = 0;
                this.gameOverMessageSliding = false;
            }
            return;
        }

        checkIfNextStage();
        updateGameOverScreenTimer(frameTime);

        if (this.scoreScreenActive) {
            this.scoreScreen.update(keyboardState, frameTime);
            return;
        }

        if (this.gameStatus == GameStatus.GAME_OVER) {
            if (this.scoreScreen.isReadyToNextStage()) {
                this.scoreScreenActive = false;
                this.gameOverScreenActive = true;
            }
            return;
        }

        updateSounds();
        updateFreezeTimer(frameTime);
        this.tileMap.update(keyboardState, frameTime);

        this.entityManager.update(keyboardState, frameTime);
        this.respawnTimer += frameTime;
        updatePowerUpTimer(frameTime);
        if (checkIfNeedThrowIntoBattleMoreTanks()) {
            tryToAddMoreTanksIntoBattle();
        }
        checkCollisions();
        checkPowerUps();
        checkGameStatus();
    }

    private void checkPlayers() {
        this.players.removeIf(player -> !player.isAlive());
    }

    public boolean isPowerUpOnField() {
        return !this.entityManager.getEntitiesByType(EntityType.POWER_UP).
                isEmpty();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.loaded) {
            return;
        }

        if (this.scoreScreenActive) {
            this.scoreScreen.draw(g);
        } else {
            if (this.gameStatus == GameStatus.PLAY
                    || (this.gameOverMessageSliding)) {
                this.tileMap.draw(g);
                this.entityManager.draw(g);
                this.tileMap.drawBushes(g);
                this.entityManager.getEntitiesByType(EntityType.SPLASH_TEXT).
                        forEach(
                                e -> e.draw(g));
                if (this.gameOverMessageSliding) {
                    this.gameOverMessage.draw(g);
                }
            } else {
                drawGameStatus(g);
            }
        }
    }

    private void updateFreezeTimer(double frameTime) {
        if (this.frozenModeAcive) {
            freezeAllEnenmyTanks();
            this.freezeTimer += frameTime;
            if (this.freezeTimer > ENEMY_TANK_FROZEN_DURATION) {
                this.freezeTimer = 0;

                unfreezeAllEnemyTanks();
            }
        }
    }

    private void updateGameOverScreenTimer(double frameTime) {
        if (this.gameOverScreenActive) {
            this.gameOverScreenTimer += frameTime;
            if (this.gameOverScreenTimer > GAME_OVER_SCREEN_DELAY) {
                this.gameOverScreenTimer = 0;
                this.gameOverScreenActive = false;
                this.gameStateManager.setGameState(GameStateManager.MENU_STATE);
            }
        }
    }

    private void updatePowerUpTimer(double frameTime) {
        this.powerUpTimer += frameTime;
        if (this.powerUpTimer > POWERUP_TIMER_DELAY) {
            this.powerUpTimer = 0;
            if (this.random.nextDouble() < POWERUP_PROBABILITY) {
                addRandomPowerUp();
            }
        }
    }

    private void updateSounds() {
        if (!this.loaded) {
            return;
        }

        if (this.gameStatus != GameStatus.PLAY) {
            stopPlayerSounds();
            return;
        }

        if (!this.resourceManager.getAudio(AudioIdentifier.NEXT_STAGE).
                isPlaying()) {
            if (this.players.stream().anyMatch(player -> player.isMoving())) {
                this.resourceManager.getAudio(AudioIdentifier.PLAYER_IDLE).
                        stop();
                this.resourceManager.getAudio(AudioIdentifier.PLAYER_MOVES).
                        loop();
            } else {
                this.resourceManager.getAudio(AudioIdentifier.PLAYER_MOVES).
                        stop();
                this.resourceManager.getAudio(AudioIdentifier.PLAYER_IDLE).
                        loop();
            }
        } else {
            stopPlayerSounds();
        }
    }

    private void stopPlayerSounds() {
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_IDLE).stop();
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_MOVES).stop();
    }

    private void stopAllSounds() {
        for (AudioIdentifier identifier : AudioIdentifier.values()) {
            this.resourceManager.getAudio(identifier).stop();
        }
    }

    public Stack<EnemyTankType> getHangar() {
        return this.hangar;
    }

    private boolean checkIfNeedThrowIntoBattleMoreTanks() {
        List<Entity> tanksOnTheField = this.entityManager
                .getEntitiesByType(EntityType.ENEMY_TANK);
        long splashCount = this.entityManager.getEntitiesByType(
                EntityType.SPLASH).size();
        return !this.hangar.isEmpty()
                && this.respawnTimer >= this.respawnDelay
                && (splashCount + tanksOnTheField.size() < this.tankOnFieldMax);
    }

    private void tryToAddMoreTanksIntoBattle() {
        List<Point> freeEnemyTankAppearancePoints =
                getFreeAppearancePoints();
        if (!freeEnemyTankAppearancePoints.isEmpty()) {
            int randIndex = this.random.nextInt(
                    freeEnemyTankAppearancePoints.size());
            Point randAppearencePoint = freeEnemyTankAppearancePoints.get(
                    randIndex);
            this.entityManager.addEntity(new Splash(this,
                    SplashType.NEW_ENEMY_TANK, randAppearencePoint.x,
                    randAppearencePoint.y));
            this.respawnTimer = 0;
        }
    }

    private double caclRespawnDelay() {
        /*Respawn time formula was taken from original game*/
        double respawnTime = (190 - 4 * this.stageNumber
                - (this.playerCount - 1) * 20) / Time.SECONDS_IN_MINUTE;
        if (respawnTime <= 0) {
            respawnTime = 0.1;
        }
        return respawnTime;
    }

    private void onBonusCollectedByPlayer(Player player, PowerUp powerUp) {
        this.onPowerUpCollectedByPlayerHandlers.get(powerUp.getType()).accept(
                player);
        player.collectPowerUp(powerUp);
        powerUp.collect();
    }

    private void onBonusCollectedByEnemy(PowerUp powerUp, EnemyTank enemyTank) {
        this.onPowerUpCollectedByEnemyHandlers.get(powerUp.getType()).accept(
                enemyTank);
        powerUp.collect();
    }

    public void addRandomPowerUp() {
        int randX = this.random.nextInt(Game.TILES_IN_WIDTH - 1)
                * Game.HALF_TILE_SIZE;
        int randY = this.random.nextInt(Game.TILES_IN_HEIGHT - 1)
                * Game.HALF_TILE_SIZE;
        PowerUp powerUp = new PowerUp(this, PowerUpType.randomType(),
                randX, randY);
        powerUp.startInfiniteBlinking(POWERUP_BLINKING_PERIOD);
        this.entityManager.removeEntitiesByType(EntityType.POWER_UP);
        this.entityManager.addEntity(powerUp);
        this.resourceManager.getAudio(AudioIdentifier.BONUS_APPEARES).play();
    }

    public int getStageNumber() {
        return this.stageNumber;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public Map<EnemyTankIdentifier, BufferedImage> getEnemyTankSpriteSheetMap() {
        return this.enemyTankSpriteSheetMap;
    }

    public Map<PlayerTankIdentifier, BufferedImage> getPlayerSpriteSheetMap() {
        return this.playerSpriteSheetMap;
    }

    public SpriteSheetManager getSpriteSheetManager() {
        return this.spriteSheetManager;
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public TileMap getTileMap() {
        return this.tileMap;
    }

    public int getMapWidth() {
        return Game.TILES_IN_WIDTH * Game.HALF_TILE_SIZE;
    }

    public int getMapHeight() {
        return Game.TILES_IN_HEIGHT * Game.HALF_TILE_SIZE;
    }

    public List<Entity> getEntities() {
        return this.entityManager.getAllEntities();
    }

    private void nextStage() {
        this.scoreScreen.reset();
        ++this.stageNumber;
        ++this.stageCount;
        if (this.stageNumber > STAGE_MAX) {
            this.stageNumber = 1;
        }

        this.respawnDelay = caclRespawnDelay();
        this.respawnTimer = 0;
        loadMap();
        this.entityManager.removeEntitiesExcepts(EntityType.PLAYER_TANK,
                EntityType.RIGHT_PANEL, EntityType.CASTLE);
        addNewStageSplashText();
        this.players.forEach(player -> player.reset());
        fillHangar();
        gameStatus = GameStatus.PLAY;
        this.loaded = true;
        this.resourceManager.getAudio(AudioIdentifier.NEXT_STAGE).play();
    }

    private int calcHighestScore() {
        int score = 0;
        for (int i = 0; i < this.players.size(); ++i) {
            int currPlayerScore = this.players.get(i).getStatistics().
                    getTotalScore();
            if (currPlayerScore > score) {
                score = currPlayerScore;
            }
        }
        return score;
    }

    private void addNewStageSplashText() {
        String nextStageMessage = "-STAGE-" + this.stageCount;
        this.entityManager.addEntity(new SplashText(
                this, nextStageMessage, fontNextStageSplash, Color.white,
                NEXT_STAGE_SPLASH_DELAY));
    }

    private void startNewGame() {
        this.entityManager.removeAllEntities();
        this.stageNumber = 1;
        this.stageCount = 1;
        loadMap();
        fillHangar();
        createMainEntities();
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_MOVES).stop();
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_IDLE).stop();
        this.resourceManager.getAudio(AudioIdentifier.NEXT_STAGE).play();
        this.respawnDelay = caclRespawnDelay();
        this.respawnTimer = 0;
        this.gameOverMessage = new GameOverMessage();
        this.gameStatus = GameStatus.PLAY;
    }

    private void fillHangar() {
        this.hangar.clear();
        int index = (this.stageNumber - 1) % STAGE_MAX;
        EnemyTankType[] allEnemyTankTypes = EnemyTankType.values();
        if (index < this.enemyGroups.length) {
            System.out.println("Filling hangar by pattern");
            int[] enemyTypes = this.enemyGroups[index];

            for (int i = 0; i < enemyTypes.length; ++i) {
                int tanksWithCurrentType = enemyTypes[i];
                for (int j = 0; j < tanksWithCurrentType; ++j) {
                    this.hangar.push(allEnemyTankTypes[i]);
                }
            }
            Collections.shuffle(hangar);
        } else {
            System.out.println("Random hangar filling");
            for (int i = 0; i < TANKS_AFTER_FOURTY_STAGE_MAX; ++i) {
                int randIndex = this.random.nextInt(allEnemyTankTypes.length);
                this.hangar.push(allEnemyTankTypes[randIndex]);
            }
        }
        System.out.println("Hangar size = " + this.hangar.size());
    }

    private List<Point> getFreeAppearancePoints() {
        List<Point> freeAppearancePoints = new ArrayList<>(3);
        List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                EntityType.ENEMY_TANK);
        List<Entity> splashes = this.entityManager.getEntitiesByType(
                EntityType.SPLASH);
        List<Point> appearancePoints = this.tileMap.
                getEnemyTankAppearencePositions();
        appearancePoints.forEach(point -> {
            Rectangle currPointBoundingRect = new Rectangle(point.x, point.y,
                    Game.TILE_SIZE, Game.TILE_SIZE);
            boolean collisionWithEnemies = enemyTanks.stream()
                    .map(entity -> (EnemyTank) entity)
                    .anyMatch(enemyTank -> enemyTank.getBoundingRect()
                            .intersects(currPointBoundingRect));

            boolean collisionWithSplashes = splashes.stream()
                    .map(entity -> (Splash) entity)
                    .anyMatch(splash -> splash.getBoundingRect()
                            .intersects(currPointBoundingRect));

            boolean collisionWithPlayers = this.players.stream().anyMatch(
                    player -> player.getBoundingRect().
                            intersects(currPointBoundingRect));

            boolean collisionWithMap = false;
            int row = point.y / Game.HALF_TILE_SIZE;
            int col = point.x / Game.HALF_TILE_SIZE;

            outer:
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    Tile tile = this.tileMap.getTile(row + i, col + j);
                    if (tile.getType().isDestroyable()
                            || tile.getType() == TileType.WATER) {
                        collisionWithMap = true;
                        break outer;
                    }
                }
            }
            if (!collisionWithPlayers
                    && !collisionWithEnemies
                    && !collisionWithSplashes
                    && !collisionWithMap) {
                freeAppearancePoints.add(point);
            }
        });
        return freeAppearancePoints;
    }

    private void createMainEntities() {
        List<Point> playerPositions = this.tileMap.getPlayerRespawnPositions();
        for (int i = 1; i <= this.playerCount; ++i) {
            Player player = new Player(this, i, PlayerTankType.BASIC,
                    playerPositions.get(i - 1).x,
                    playerPositions.get(i - 1).y,
                    Direction.NORTH
            );
            this.players.add(player);
            this.entityManager.addEntity(player);
        }

        Point castlePosition = this.tileMap.getCastlePosition();
        this.castle = new Castle(this, castlePosition.x, castlePosition.y);
        this.entityManager.addEntity(castle);
        this.rightPanel = new GameInfoPanel(this, RIGHT_PANEL_POSITION.x,
                RIGHT_PANEL_POSITION.y);
        this.entityManager.addEntity(this.rightPanel);
        addNewStageSplashText();
    }

    private void checkCollisions() {
        List<Entity> projectiles = this.entityManager.getEntitiesByType(
                EntityType.PROJECTILE);
        List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                EntityType.ENEMY_TANK);

        List<Entity> dynamits = this.entityManager.getEntitiesByType(
                EntityType.DYNAMITE);

        List<Entity> rockets = this.entityManager.getEntitiesByType(
                EntityType.ROCKET);

        checkCollisionsBetweenProjectiles(projectiles);
        checkProjectileCastleCollisions(projectiles);
        checkProjectilePlayerCollision(projectiles);
        checkProjectileEnemyTankCollision(projectiles, enemyTanks);
        checkRocketPlayerCollision(rockets);
        checkRocketEnemyTankCollisions(rockets, enemyTanks);
        checkEnemyTankDynamiteCollisions(dynamits, enemyTanks);
    }
    
    private void checkRocketEnemyTankCollisions(List<Entity> rockets,
            List<Entity> enemyTanks) {
        rocketLoop:
        for (int j = rockets.size() - 1; j >= 0; --j) {
            Rocket rocket = (Rocket) rockets.get(j);
            for (int i = enemyTanks.size() - 1; i >= 0; --i) {
                EnemyTank enemyTank = (EnemyTank) enemyTanks.get(i);
                if (!enemyTank.isAlive()) {
                    continue;
                }
                if (enemyTank.collides(rocket) && rocket.getOwnerId() != 0) {
                    enemyTank.explode();
                    int killerId = rocket.getOwnerId();
                    for (int k = 0; k < this.players.size(); ++k) {
                        Player currPlayer = this.players.get(k);
                        if (currPlayer.getId().getId() == killerId) {
                            currPlayer.registerKilledTank(enemyTank);
                            continue rocketLoop;
                        }
                    }
                    rocket.explode();
                    break;
                }
            }
        }
    }

    private void checkRocketPlayerCollision(List<Entity> rockets) {
        rocketLoop:
        for (int i = rockets.size() - 1; i >= 0; --i) {
            Rocket rocket = (Rocket) rockets.get(i);
            for (int j = 0; j < this.players.size(); ++j) {
                Player currPlayer = this.players.get(j);
                if (rocket.collides(currPlayer)) {
                    currPlayer.explode();
                    rocket.explode();
                    continue rocketLoop;
                }
            }
        }
    }
    
    private void checkEnemyTankDynamiteCollisions(List<Entity> dynamits,
            List<Entity> enemyTanks){
        tanksLoop:
        for (int i = enemyTanks.size() - 1; i >= 0; --i) {
            EnemyTank enemyTank = (EnemyTank) enemyTanks.get(i);
            if (!enemyTank.isAlive()) {
                continue;
            }
            for (int j = dynamits.size() - 1; j >= 0; --j) {
                Dynamite dynamite = (Dynamite) dynamits.get(j);
                if (enemyTank.collides(dynamite)) {
                    enemyTank.explode();
                    int killerId = dynamite.getOwnerId();
                    dynamite.explode();
                    for (int k = 0; k < this.players.size(); ++k) {
                        Player currPlayer = this.players.get(k);
                        if (currPlayer.getId().getId() == killerId) {
                            currPlayer.registerKilledTank(enemyTank);
                            continue tanksLoop;
                        }
                    }
                }
            }
        }
    }
    
    private void checkProjectileCastleCollisions(List<Entity> projectiles){
        for (int i = projectiles.size() - 1; i >= 0; --i) {
            Projectile projectile = (Projectile) projectiles.get(i);

            if (this.castle.getState() == CastleState.ALIVE
                    && projectile.collides(this.castle)) {
                this.castle.kill();
                return;
            }
        }
    }
    
    private void checkProjectilePlayerCollision(List<Entity> projectiles){
        projectileLoop:
        for (int i = projectiles.size() - 1; i >= 0; --i) {
            Projectile projectile = (Projectile) projectiles.get(i);
            for (int j = this.players.size() - 1; j >= 0; --j) {
                Player currPlayer = this.players.get(j);
                if (projectile.getType() == ProjectileType.ENEMY) {
                    if (projectile.collides(currPlayer)) {
                        currPlayer.hit(projectile.getDamage());
                        projectile.explode();
                        continue projectileLoop;
                    }
                } else if (projectile.getType() == ProjectileType.PLAYER) {
                    if (projectile.collides(currPlayer)
                            && projectile.getOwnerId() != currPlayer.getId().
                            getId()) {
                        this.players.forEach(player -> player.freeze(
                                PLAYER_TANK_FROZEN_DURATION));
                        projectile.explode();
                        continue projectileLoop;
                    }
                }

            }
        }
    }
    
    private void checkProjectileEnemyTankCollision(List<Entity> projectiles,
            List<Entity> enemyTanks){
        projectileLoop:
        for (int i = projectiles.size() - 1; i >= 0; --i) {
            Projectile projectile = (Projectile) projectiles.get(i);
            
            for (int j = enemyTanks.size() - 1; j >= 0; --j) {
                EnemyTank enemyTank = (EnemyTank) enemyTanks.get(j);
                if (!enemyTank.isAlive()) {
                    continue;
                }
                if (projectile.collides(enemyTank)) {
                    if (projectile.getType() == ProjectileType.PLAYER) {
                        enemyTank.hit(projectile.getDamage());
                        if (!enemyTank.isAlive()) {
                            int killerId = projectile.getOwnerId();
                            for (int k = 0; k < this.players.size(); ++k) {
                                Player currPlayer = this.players.get(k);
                                if (currPlayer.getId().getId() == killerId) {
                                    currPlayer.registerKilledTank(enemyTank);
                                    continue projectileLoop;
                                }
                            }
                        }
                        projectile.explode();
                    }
                }
            }
        }
    }
    
    private void checkCollisionsBetweenProjectiles(List<Entity> projectiles){
        projectileLoop:
        for (int i = projectiles.size() - 1; i >= 0; --i) {
            Projectile projectile = (Projectile) projectiles.get(i);
            for (int j = i - 1; j >= 0; --j) {
                Projectile otherProjectile = (Projectile) projectiles.get(j);
                if (projectile.getOwnerId() != otherProjectile.getOwnerId()
                        && projectile.collides(otherProjectile)) {
                    projectile.explode();
                    otherProjectile.explode();
                    continue projectileLoop;
                }

            }
        }
    }

    private void checkPowerUps() {
        List<Entity> powerUps = this.entityManager.getEntitiesByType(
                EntityType.POWER_UP);
        List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                EntityType.ENEMY_TANK);
        outer:
        for (int i = powerUps.size() - 1; i >= 0; --i) {
            PowerUp powerUp = (PowerUp) powerUps.get(i);
            for (int j = 0; j < this.players.size(); ++j) {
                Player currPlayer = this.players.get(j);
                if (currPlayer.collides(powerUp)) {
                    onBonusCollectedByPlayer(currPlayer, powerUp);
                    continue outer;
                }
            }
            for (int j = enemyTanks.size() - 1; j >= 0; --j) {
                EnemyTank enemyTank = (EnemyTank) enemyTanks.get(j);
                if (enemyTank.collides(powerUp)) {
                    onBonusCollectedByEnemy(powerUp, enemyTank);
                    break;
                }
            }
        }
    }

    private void checkGameStatus() {
        if (this.castle.getState() == CastleState.DEAD
                || this.players.stream().allMatch(player -> !player.isAlive())) {
            this.gameOverMessageSliding = true;
            this.gameStatus = GameStatus.GAME_OVER;
            this.highestScore = calcHighestScore();
            saveHighestScore();
            stopPlayerSounds();
            resourceManager.getAudio(AudioIdentifier.GAME_OVER).play();
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {
    }

    @Override
    public void onKeyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                this.players.get(0).setSliding(true);
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_S:
            case KeyEvent.VK_D:
            case KeyEvent.VK_W:
                if (this.playerCount > 1) {
                    this.players.get(1).setSliding(true);
                }
                break;
            case KeyEvent.VK_SPACE:
                togglePause();
                break;
            case KeyEvent.VK_F:
            case KeyEvent.VK_H:
            case KeyEvent.VK_G:
            case KeyEvent.VK_J:
                this.players.get(0).setCanFire(true);
                break;
            case KeyEvent.VK_E:
            case KeyEvent.VK_R:
            case KeyEvent.VK_T:
            case KeyEvent.VK_Y:
                if (this.playerCount > 1) {
                    this.players.get(1).setCanFire(true);
                }
                break;
            case KeyEvent.VK_Z:
                this.players.get(0).dynamite();
                break;
            case KeyEvent.VK_X:
                if (this.playerCount > 1) {
                    this.players.get(1).dynamite();
                }
                break;
            case KeyEvent.VK_M:
                stopAllSounds();
                this.gameStateManager.setGameState(GameStateManager.MENU_STATE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMouseReleased(MouseEvent e) {

    }

    @Override
    public void onMouseMoved(MouseEvent e) {

    }

    private void togglePause() {
        if (this.gameStatus == GameStatus.PLAY) {
            this.gameStatus = GameStatus.PAUSED;
            stopPlayerSounds();
        } else if (this.gameStatus == GameStatus.PAUSED) {
            this.gameStatus = GameStatus.PLAY;
        }
    }

    private void loadSprites() {
        this.spriteSheetManager = SpriteSheetManager.getInstance();
        for (SpriteSheetIdentifier identifier : SpriteSheetIdentifier.values()) {
            this.spriteSheetManager.put(identifier, this.atlas);
        }
    }

    private void loadTankSpriteSheetMaps() {

        this.enemyTankSpriteSheetMap = new HashMap<>();
        this.playerSpriteSheetMap = new HashMap<>();
        for (TankColor color : TankColor.values()) {
            for (Alliance alliance : Alliance.values()) {
                Point p = color.
                        getOffsetFromTankSpriteSheetTopLeftCorner();
                Point topLeft = new Point(p.x, p.y);
                topLeft.x += alliance.
                        getOffsetFromSameColorTankSpriteSheetTopLeftCorner().x;
                topLeft.y += alliance.
                        getOffsetFromSameColorTankSpriteSheetTopLeftCorner().y;

                for (Heading heading : Heading.values()) {
                    for (EnemyTankType type : EnemyTankType.values()) {
                        int dx = heading.getSpriteSheetPositionX();
                        int dy = type.getSpriteSheetPositionY();
                        EnemyTankIdentifier key = new EnemyTankIdentifier(color,
                                heading, type);
                        BufferedImage spriteSheet = spriteSheetManager.get(
                                SpriteSheetIdentifier.TANK);
                        BufferedImage sprite = spriteSheet.getSubimage(topLeft.x
                                + dx, topLeft.y + dy,
                                2 * Game.TILE_SIZE, Game.TILE_SIZE);
                        enemyTankSpriteSheetMap.put(key, sprite);
                    }

                    for (PlayerTankType type : PlayerTankType.values()) {
                        int dx = heading.getSpriteSheetPositionX();
                        int dy = type.getSpriteSheetPositionY();
                        PlayerTankIdentifier key = new PlayerTankIdentifier(
                                color,
                                heading, type);
                        SpriteSheetManager manager = SpriteSheetManager.
                                getInstance();
                        BufferedImage spriteSheet = manager.get(
                                SpriteSheetIdentifier.TANK);
                        BufferedImage sprite = spriteSheet.getSubimage(topLeft.x
                                + dx, topLeft.y + dy,
                                2 * Game.TILE_SIZE, Game.TILE_SIZE);
                        playerSpriteSheetMap.put(key, sprite);
                    }
                }
            }
        }
    }

    private void drawGameStatus(Graphics2D g) {
        if (this.gameStatus == GameStatus.PAUSED) {
            BrickFont.drawWithBricksCentralized(g, "GAME", Game.HEIGHT / 3);
            BrickFont.drawWithBricksCentralized(g, "PAUSED", Game.HEIGHT / 2);
        } else if (this.gameStatus == GameStatus.GAME_OVER) {
            BrickFont.drawWithBricksCentralized(g, "GAME", Game.HEIGHT / 3);
            BrickFont.drawWithBricksCentralized(g, "OVER", Game.HEIGHT / 2);
        }

    }

    private void createOnPowerUpCollectedByPlayerHandlers() {
        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.TANK,
                (player) -> {
            player.gainExtraLife();
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.STAR,
                (player) -> {
            player.promote();
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.GUN,
                (player) -> {
            player.promoteToHeavy();
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.HELMET,
                (player) ->
        {
            player.addProtection(PLAYER_PROTECTION_DURATION);
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.SHOVEL,
                (player) ->
        {
            tileMap.activateEagleProtection();
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.GRENADE,
                (player) ->
        {
            List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                    EntityType.ENEMY_TANK);
            enemyTanks.stream().map(entity -> (EnemyTank) entity).forEach(
                    enemyTank -> enemyTank.explodeWithGrenade());
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.TIMER,
                (player) -> {
            freezeAllEnenmyTanks();
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.SHIP,
                (player) -> {
            player.setCanTraverseWater(true);
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.TWIN_SHOT,
                (player) -> {
            player.setCanTwinShot(true);
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.FOUR_WAY_SHOT,
                (player) -> {
            player.setCanFourWayShot(true);
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.MACHINE_GUN,
                (player) -> {
            player.setCanRepeateFire(true);
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.ROCKET,
                (player) -> {
            player.gainAbilityToLaunchRockets();
        });

        this.onPowerUpCollectedByPlayerHandlers.put(PowerUpType.DYNAMITE,
                (player) -> {
            player.gainDynamiteAbility();
        });
    }

    private void freezeAllEnenmyTanks() {
        this.frozenModeAcive = true;
        List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                EntityType.ENEMY_TANK);
        enemyTanks.stream().map(entity -> (EnemyTank) entity).forEach(
                enemyTank -> enemyTank.freeze(ENEMY_TANK_FROZEN_DURATION));
    }

    private void unfreezeAllEnemyTanks() {
        this.frozenModeAcive = false;
        List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                EntityType.ENEMY_TANK);
        enemyTanks.stream().map(entity -> (EnemyTank) entity)
                .filter(enemyTank -> enemyTank.isFrozen())
                .forEach(enemyTank -> enemyTank.unfreeze());
    }

    private void createOnPowerUpCollectedByEnemyHandlers() {
        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.TANK, (tank) ->
        {
            List<Entity> enemies = this.entityManager.getEntitiesByType(
                    EntityType.ENEMY_TANK);
            enemies.stream().map(entity -> (EnemyTank) entity).forEach(
                    enemyTank -> {
                if (!enemyTank.isBonus()) {
                    enemyTank.turnRed();
                }
            });
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.STAR, (tank) ->
        {
            List<Entity> enemies = this.entityManager.getEntitiesByType(
                    EntityType.ENEMY_TANK);
            enemies.stream().map(entity -> (EnemyTank) entity).forEach(
                    enemyTank -> enemyTank.promoteToHeavy());
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.GUN, (tank) ->
        {
            this.onPowerUpCollectedByEnemyHandlers.get(PowerUpType.STAR).accept(
                    tank);
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.HELMET,
                (tank) -> {
            List<Entity> enemies = this.entityManager.getEntitiesByType(
                    EntityType.ENEMY_TANK);
            enemies.stream().map(entity -> (EnemyTank) entity).forEach(
                    enemyTank -> {
                if (enemyTank.getType() == EnemyTankType.ARMORED) {
                    if (!enemyTank.isBonus()) {
                        enemyTank.turnRed();
                    }
                } else {
                    enemyTank.promoteToHeavy();
                }
            });
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.SHOVEL,
                (tank) -> {
            if (this.tileMap.isEagleProtectionActive()) {
                this.tileMap.deactivateEagleProtection();
            } else {
                this.tileMap.destroyAllProtections();
            }
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.GRENADE,
                (tank) -> {
            this.players.forEach(player -> player.explode());
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.TIMER,
                (tank) -> {
            this.players.forEach(player -> player.freeze(
                    PLAYER_TANK_FROZEN_DURATION));
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.SHIP, (tank) ->
        {
            tank.setCanTraverseWater(true);
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.TWIN_SHOT,
                (tank) -> {
            tank.setShootingMode(ShootingMode.TWIN_SHOT);
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.FOUR_WAY_SHOT,
                (tank) -> {
            tank.setShootingMode(ShootingMode.FOUR_WAY_SHOT);
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.MACHINE_GUN,
                (tank) -> {
            tank.setCanRepeateFire(true);
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.ROCKET,
                (tank) -> {
            tank.gainAbilityToLaunchRockets();
        });

        this.onPowerUpCollectedByEnemyHandlers.put(PowerUpType.DYNAMITE,
                (tank) -> {
            this.entityManager.getEntitiesByType(EntityType.DYNAMITE)
                    .stream().map(entity -> (Dynamite) entity)
                    .forEach(dynamite -> dynamite.explode());
        });
    }

    private void checkIfNextStage() {
        if (this.gameStatus == GameStatus.GAME_OVER
                || (this.hangar.isEmpty()
                && this.entityManager.getEntitiesByType(EntityType.ENEMY_TANK).
                        isEmpty())) {
            stopPlayerSounds();
            checkIfScoreScreen();
        }
    }

    private void checkIfScoreScreen() {

        if (this.scoreScreenActive) {
            if (this.scoreScreen.isReadyToNextStage()) {
                this.scoreScreenActive = false;
                if (this.gameStatus != GameStatus.GAME_OVER) {
                    nextStage();
                }
            }
        } else {
            if (!this.scoreScreen.isReadyToNextStage()) {
                this.scoreScreenActive = true;
            } else {
                if (this.gameStatus != GameStatus.GAME_OVER) {
                    nextStage();
                }
            }
        }
    }

    private void addRenderingLayers() {
        this.entityManager.addRenderingLayer(RenderingLayerIdentifier.EAGLE,
                EntityType.CASTLE);
        this.entityManager.addRenderingLayer(
                RenderingLayerIdentifier.STATIC_ELEMENTS,
                EntityType.DYNAMITE);
        this.entityManager.addRenderingLayer(
                RenderingLayerIdentifier.PROJECTILES,
                EntityType.PROJECTILE, EntityType.ROCKET);
        this.entityManager.addRenderingLayer(RenderingLayerIdentifier.SPLASHES,
                EntityType.SPLASH);
        this.entityManager.addRenderingLayer(RenderingLayerIdentifier.TANKS,
                EntityType.PLAYER_TANK, EntityType.ENEMY_TANK);
        this.entityManager.addRenderingLayer(
                RenderingLayerIdentifier.SCORE_TEXTS,
                EntityType.SCRORE_TEXT);
        this.entityManager.addRenderingLayer(
                RenderingLayerIdentifier.PROTECTIONS,
                EntityType.PROTECTION);
        this.entityManager.addRenderingLayer(RenderingLayerIdentifier.POWERUPS,
                EntityType.POWER_UP);
        this.entityManager.
                addRenderingLayer(RenderingLayerIdentifier.EXPLOSIONS,
                        EntityType.EXPLOSION);
        this.entityManager.addRenderingLayer(RenderingLayerIdentifier.GAME_INFO,
                EntityType.INDICATOR, EntityType.RIGHT_PANEL);
    }

    private void loadHighestScore() {

        try (InputStream in = getClass().getResourceAsStream(
                "/statistics/highestScore");
                BufferedReader br =
                new BufferedReader(new InputStreamReader(in));) {
            String line = br.readLine();
            this.highestScore = Integer.parseInt(line);
            System.out.println("Highest score loaded = " + this.highestScore);

        } catch (IOException ex) {
            Logger.getLogger(LevelState.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    private void saveHighestScore() {
        try (PrintWriter writer = new PrintWriter(
                new File(Files.class.getResource("/statistics/highestScore").
                        getPath()))) {
            writer.print(this.highestScore);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LevelState.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    private void loadSounds() {
        this.resourceManager.loadAudio(AudioIdentifier.NEXT_STAGE,
                "/sounds/nextStage.wav");
        this.resourceManager.loadAudio(AudioIdentifier.PLAYER_MOVES,
                "/sounds/playerMoving.wav");
        this.resourceManager.loadAudio(AudioIdentifier.PLAYER_IDLE,
                "/sounds/playerIdle.wav");
        this.resourceManager.loadAudio(AudioIdentifier.SHOT, "/sounds/shot.wav");
        this.resourceManager.loadAudio(AudioIdentifier.EXPLOSION,
                "/sounds/explosion.wav");
        this.resourceManager.loadAudio(AudioIdentifier.BONUS_APPEARES,
                "/sounds/bonusAppeares.wav");
        this.resourceManager.loadAudio(AudioIdentifier.BONUS_COLLECTED,
                "/sounds/bonusCollected.wav");
        this.resourceManager.loadAudio(AudioIdentifier.SCORE_SCREEN,
                "/sounds/scoreScreen.wav");
        this.resourceManager.loadAudio(AudioIdentifier.BRICK,
                "/sounds/brick.wav");
        this.resourceManager.loadAudio(AudioIdentifier.STEEL,
                "/sounds/steel.wav");
        this.resourceManager.loadAudio(AudioIdentifier.GAME_OVER,
                "/sounds/gameOver.wav");
    }

    private void loadFonts() {
        this.resourceManager.loadFont(FontIdentifier.BATTLE_CITY,
                "/fonts/prstart.ttf");
        Font font = this.resourceManager.getFont(FontIdentifier.BATTLE_CITY);
        fontNextStageSplash = font.deriveFont(Font.BOLD | Font.ITALIC, 34);
    }

    private void loadImages() {
        this.resourceManager.loadImage(ImageIdentifier.TEXTURE_ATLAS,
                "/images/texture_atlas.png");
        this.atlas = new TextureAtlas(this.resourceManager.getImage(
                ImageIdentifier.TEXTURE_ATLAS));
        loadSprites();
    }

    private void loadMap() {
        this.tileMap = new TileMap(Game.SCALE);
        int mapIndex = this.stageNumber % (STAGE_MAX + 1);
        this.tileMap.loadMap("/tilemap/level" + mapIndex + ".map");
    }

}
