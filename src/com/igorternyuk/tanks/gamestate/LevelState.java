package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.GameStatus;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityManager;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.RenderingLayerIdentifier;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUpType;
import com.igorternyuk.tanks.gameplay.entities.eagle.Eagle;
import com.igorternyuk.tanks.gameplay.entities.eagle.EagleState;
import com.igorternyuk.tanks.gameplay.entities.indicators.GameInfoPanel;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankIdentifier;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
import com.igorternyuk.tanks.gameplay.entities.projectiles.ProjectileType;
import com.igorternyuk.tanks.gameplay.entities.splash.Splash;
import com.igorternyuk.tanks.gameplay.entities.splash.SplashType;
import com.igorternyuk.tanks.gameplay.entities.splashing.SplashText;
import com.igorternyuk.tanks.gameplay.entities.tank.Alliance;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTank;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankIdentifier;
import com.igorternyuk.tanks.gameplay.entities.tank.enemytank.EnemyTankType;
import com.igorternyuk.tanks.gameplay.tilemap.TileMap;
import com.igorternyuk.tanks.graphics.images.TextureAtlas;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Graphics2D;
import com.igorternyuk.tanks.input.KeyboardState;
import com.igorternyuk.tanks.resourcemanager.AudioIdentifier;
import com.igorternyuk.tanks.resourcemanager.FontIdentifier;
import com.igorternyuk.tanks.resourcemanager.ImageIdentifier;
import com.igorternyuk.tanks.utils.Files;
import com.igorternyuk.tanks.utils.Painter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igor
 */
public class LevelState extends GameState {

    public static final int TANKS_TOTAL = 20;
    protected static final Point EAGLE_POSITION = new Point(12
            * Game.HALF_TILE_SIZE, 24 * Game.HALF_TILE_SIZE);
    private Font fontGameStatus = new Font("Verdana", Font.BOLD,
            48);
    private Font fontNextStageSplash =
            new Font("Verdana", Font.BOLD | Font.ITALIC, 48);
    private static final Point PLAYER_RESPAWN_POSITION = new Point(8
            * Game.HALF_TILE_SIZE, 24 * Game.HALF_TILE_SIZE);
    private static final Point RIGHT_PANEL_POSITION = new Point(26
            * Game.HALF_TILE_SIZE, 0 * Game.HALF_TILE_SIZE);

    private static final int TANKS_ON_FIELD_MAX = 4;
    private static final int STAGE_MAX = 10;
    private static final double NEXT_STAGE_SPLASH_DELAY = 6;

    private TextureAtlas atlas;
    private SpriteSheetManager spriteSheetManager;
    private Map<EnemyTankIdentifier, BufferedImage> enemyTankSpriteSheetMap;
    private Map<PlayerTankIdentifier, BufferedImage> playerSpriteSheetMap;
    private TileMap tileMap;
    private Player player;
    private Eagle eagle;
    private EntityManager entityManager;
    private GameInfoPanel rightPanel;
    private int highestScore = 0;

    private int stageNumber = 1;
    private Stack<EnemyTankType> hangar = new Stack<>();
    private Map<PowerUpType, Runnable> onPowerUpCollectedHandlers =
            new HashMap<>();
    private Random random = new Random();
    private GameStatus gameStatus = GameStatus.PLAY;
    private boolean loaded = false;
    private boolean scoreScreenActive = false;
    private ScoreScreen scoreScreen;

    public LevelState(GameStateManager gameStateManager) {
        super(gameStateManager);
        this.entityManager = new EntityManager(this);
        addRenderingLayers();
        createOnPowerUpCollectedHanlers();
    }

    public int getHighestScore() {
        return this.highestScore;
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
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_IDLE);
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
        this.tileMap = null;
    }

    private void loadHighestScore() {

        try (InputStream in = getClass().getResourceAsStream(
                "/statistics/highestScore");
                BufferedReader br =
                new BufferedReader(new InputStreamReader(in));) {
            String line = br.readLine();
            this.highestScore = Integer.parseInt(line);

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
    }

    private void loadFonts() {
        this.resourceManager.loadFont(FontIdentifier.BATTLE_CITY,
                "/fonts/prstart.ttf");
        Font font = this.resourceManager.getFont(FontIdentifier.BATTLE_CITY);
        this.fontGameStatus = font.deriveFont(Font.BOLD, 36);
        this.fontNextStageSplash = font.deriveFont(Font.BOLD | Font.ITALIC, 34);
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
        this.tileMap.loadMap("/tilemap/level" + this.stageNumber + ".map");
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        if (!this.loaded) {
            return;
        }

        if (this.gameStatus != GameStatus.PLAY) {
            return;
        }
        
        checkIfNextStage();
        
        if(this.scoreScreenActive){
            this.scoreScreen.update(keyboardState, frameTime);
            return;
        }
        
        updateSounds();

        this.tileMap.update(keyboardState, frameTime);
        this.entityManager.update(keyboardState, frameTime);

        if (needThrowIntoBattleMoreTanks()) {
            tryToAddMoreTanksIntoBattle();
        }

        checkCollisions();
        checkPowerUps();
        checkGameStatus();
        
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.loaded) {
            return;
        }
        
        if(this.scoreScreenActive){
            this.scoreScreen.draw(g);
            return;
        }
        
        this.tileMap.draw(g);
        this.entityManager.draw(g);
        this.tileMap.drawBushes(g);
        drawGameStatus(g);
    }

    private void updateSounds() {
        if (this.gameStatus != GameStatus.PLAY) {
            stopPlayerSounds();
            return;
        }

        if (!this.resourceManager.getAudio(AudioIdentifier.NEXT_STAGE).
                isPlaying()) {
            if (this.player.isMoving()) {
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

    public Stack<EnemyTankType> getHangar() {
        return this.hangar;
    }

    private boolean needThrowIntoBattleMoreTanks() {
        long tanksOnTheField = this.entityManager
                .getEntitiesByType(EntityType.ENEMY_TANK).size();
        long splashCount = this.entityManager.getEntitiesByType(
                EntityType.SPLASH).size();
        return !this.hangar.isEmpty()
                && (splashCount + tanksOnTheField < TANKS_ON_FIELD_MAX);
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
        }
    }

    private void onBonusCollected(PowerUp powerUp) {
        this.onPowerUpCollectedHandlers.get(powerUp.getType()).run();
        this.player.collectPowerUp(powerUp);
        powerUp.collect();
    }

    public int getStageNumber() {
        return this.stageNumber;
    }

    public Point getEaglePosition() {
        return EAGLE_POSITION;
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

    public Player getPlayer() {
        return this.player;
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
        ++this.stageNumber;
        if (this.stageNumber > STAGE_MAX) {
            this.stageNumber = 1;
        }
        loadMap();
        this.entityManager.removeEntitiesExcepts(EntityType.PLAYER_TANK,
                EntityType.RIGHT_PANEL, EntityType.EAGLE);
        addNewStageSplashText();
        this.player.reset();
        fillHangar();
        gameStatus = GameStatus.PLAY;
        this.loaded = true;
        this.resourceManager.getAudio(AudioIdentifier.NEXT_STAGE).play();
    }

    private void addNewStageSplashText() {
        String nextStageMessage = "-STAGE-" + this.stageNumber;
        this.entityManager.addEntity(new SplashText(
                this, nextStageMessage, fontNextStageSplash, Color.white,
                NEXT_STAGE_SPLASH_DELAY));
    }

    private void startNewGame() {
        this.entityManager.removeAllEntities();
        this.stageNumber = 1;
        loadMap();
        fillHangar();
        createEntities();
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_MOVES).stop();
        this.resourceManager.getAudio(AudioIdentifier.PLAYER_IDLE).stop();
        this.resourceManager.getAudio(AudioIdentifier.NEXT_STAGE).play();
        gameStatus = GameStatus.PLAY;
    }

    private void fillHangar() {
        this.hangar.clear();
        for (int i = TANKS_TOTAL; i > 0; --i) {
            EnemyTankType[] allEnemyTankTypes = EnemyTankType.values();
            EnemyTankType randomType = EnemyTankType.values()[this.random.
                    nextInt(allEnemyTankTypes.length)];
            this.hangar.push(randomType);
        }
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

            boolean collisionWithPlayer = this.player.getBoundingRect().
                    intersects(currPointBoundingRect);
            if (!collisionWithPlayer
                    && !collisionWithEnemies && !collisionWithSplashes) {
                freeAppearancePoints.add(point);
            }
        });
        return freeAppearancePoints;
    }

    private void createEntities() {
        Player playerTank = new Player(this, PlayerTankType.BASIC,
                PLAYER_RESPAWN_POSITION.x, PLAYER_RESPAWN_POSITION.y,
                Direction.NORTH
        );
        this.player = playerTank;
        this.player.addProtection();
        this.entityManager.addEntity(playerTank);
        this.eagle = new Eagle(this, EAGLE_POSITION.x, EAGLE_POSITION.y);
        this.entityManager.addEntity(eagle);
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
        for (int i = projectiles.size() - 1; i >= 0; --i) {
            Projectile projectile = (Projectile) projectiles.get(i);
            if (this.eagle.getState() == EagleState.ALIVE
                    && projectile.collides(this.eagle)) {
                this.eagle.kill();
                continue;
            }

            if (projectile.getType() == ProjectileType.ENEMY
                    && projectile.collides(this.player)) {
                this.player.hit(projectile.getDamage());
                projectile.explode();
            }
            for (int j = enemyTanks.size() - 1; j >= 0; --j) {
                EnemyTank enemyTank = (EnemyTank) enemyTanks.get(j);
                if (projectile.collides(enemyTank)) {
                    if (projectile.getType() == ProjectileType.PLAYER) {
                        enemyTank.hit(projectile.getDamage());
                        if (!enemyTank.isAlive()) {
                            this.player.registerKilledTank(enemyTank);
                        }
                        projectile.explode();
                    }
                }
            }
        }
    }

    private void checkPowerUps() {
        List<Entity> bonuses = this.entityManager.getEntitiesByType(
                EntityType.POWER_UP);
        for (int i = 0; i < bonuses.size(); ++i) {
            PowerUp bonus = (PowerUp) bonuses.get(i);
            if (this.player.collides(bonus)) {
                onBonusCollected(bonus);
                break;
            }
        }
    }

    private void checkGameStatus() {
        if (this.eagle.getState() == EagleState.DEAD
                || !this.player.isAlive()) {
            this.gameStatus = GameStatus.GAME_OVER;
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
            case KeyEvent.VK_A:
            case KeyEvent.VK_S:
            case KeyEvent.VK_D:
            case KeyEvent.VK_W:
                this.player.setSliding(true);
                break;
            case KeyEvent.VK_SPACE:
                togglePause();
                break;
            case KeyEvent.VK_N:
                startNewGame();
                break;
            case KeyEvent.VK_F:
                this.player.setCanFire(true);
                break;
            case KeyEvent.VK_Z:
                this.scoreScreenActive = !this.scoreScreenActive;
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
                Point topLeft = color.
                        getOffsetFromTankSpriteSheetTopLeftCorner();
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
        Painter.drawCenteredString(g, this.gameStatus.getDescription(),
                fontGameStatus, this.gameStatus.getColor(),
                (Game.HEIGHT - Game.STATISTICS_PANEL_HEIGHT) / 2);
    }

    private void createOnPowerUpCollectedHanlers() {
        this.onPowerUpCollectedHandlers.put(PowerUpType.TANK, () -> {
            this.player.gainExtraLife();
        });

        this.onPowerUpCollectedHandlers.put(PowerUpType.STAR, () -> {
            this.player.promote();
        });

        this.onPowerUpCollectedHandlers.put(PowerUpType.GUN, () -> {
            this.player.promoteToHeavy();
        });

        this.onPowerUpCollectedHandlers.put(PowerUpType.HELMET, () -> {
            this.player.addProtection();
        });

        this.onPowerUpCollectedHandlers.put(PowerUpType.SHOVEL, () -> {
            this.tileMap.activateEagleProtection();
        });

        this.onPowerUpCollectedHandlers.put(PowerUpType.GRENADE, () -> {
            List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                    EntityType.ENEMY_TANK);
            enemyTanks.stream().map(entity -> (EnemyTank) entity).forEach(
                    enemyTank -> enemyTank.explodeWithGrenade());
        });

        this.onPowerUpCollectedHandlers.put(PowerUpType.TIMER, () -> {
            List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                    EntityType.ENEMY_TANK);
            enemyTanks.stream().map(entity -> (EnemyTank) entity).forEach(
                    enemyTank -> enemyTank.freeze());
        });
    }

    private void checkIfNextStage() {
        if (this.gameStatus == GameStatus.PLAY
                && this.hangar.isEmpty()
                && this.entityManager.getEntitiesByType(EntityType.ENEMY_TANK).
                        isEmpty()) {
            /////////////////Show statistics screen//////////////
            stopPlayerSounds();
            if(!this.scoreScreenActive){
                this.scoreScreen.reset();
                this.scoreScreenActive = true;
            } else {
                if(this.scoreScreen.isReadyToNextStage()){
                    this.scoreScreenActive = false;
                    nextStage();
                }
            }
        }
    }

    private void addRenderingLayers() {
        this.entityManager.addRenderingLayer(RenderingLayerIdentifier.EAGLE,
                EntityType.EAGLE);
        this.entityManager.addRenderingLayer(
                RenderingLayerIdentifier.PROJECTILES,
                EntityType.PROJECTILE);
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
        this.entityManager.addRenderingLayer(
                RenderingLayerIdentifier.SPLASH_TEXTS,
                EntityType.SPLASH_TEXT);
    }
}
