package com.igorternyuk.tanks.gamestate;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.GameStatus;
import com.igorternyuk.tanks.gameplay.entities.Direction;
import com.igorternyuk.tanks.gameplay.entities.Entity;
import com.igorternyuk.tanks.gameplay.entities.EntityManager;
import com.igorternyuk.tanks.gameplay.entities.EntityType;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUp;
import com.igorternyuk.tanks.gameplay.entities.bonuses.PowerUpType;
import com.igorternyuk.tanks.gameplay.entities.eagle.Eagle;
import com.igorternyuk.tanks.gameplay.entities.indicators.GameInfoPanel;
import com.igorternyuk.tanks.gameplay.entities.player.Player;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankIdentifier;
import com.igorternyuk.tanks.gameplay.entities.player.PlayerTankType;
import com.igorternyuk.tanks.gameplay.entities.projectiles.Projectile;
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
import com.igorternyuk.tanks.resourcemanager.ImageIdentifier;
import com.igorternyuk.tanks.utils.Painter;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author igor
 */
public class LevelState extends GameState {
    protected static final Point EAGLE_POSITION = new Point(12
            * Game.HALF_TILE_SIZE, 24 * Game.HALF_TILE_SIZE);
    private static final Font FONT_GAME_STATUS = new Font("Verdana", Font.BOLD,
            48);    
    private static final Point PLAYER_RESPAWN_POSITION = new Point(8
            * Game.HALF_TILE_SIZE, 24 * Game.HALF_TILE_SIZE);
    private static final Point RIGHT_PANEL_POSITION = new Point(26
            * Game.HALF_TILE_SIZE, 0 * Game.HALF_TILE_SIZE);

    private TextureAtlas atlas;
    private SpriteSheetManager spriteSheetManager;
    private Map<EnemyTankIdentifier, BufferedImage> enemyTankSpriteSheetMap;
    private Map<PlayerTankIdentifier, BufferedImage> playerSpriteSheetMap;

    private TileMap tileMap;

    private Player player;
    private Eagle eagle;
    private EntityManager entityManager;
    private GameInfoPanel rightPanel;
    private GameStatus gameStatus = GameStatus.PLAY;
    private boolean loaded = false;
    int stageNumber = 1;

    public LevelState(GameStateManager gsm) {
        super(gsm);
        this.entityManager = new EntityManager(this);
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

    @Override
    public void load() {
        System.out.println("Level state loading...");
        this.resourceManager.loadImage(ImageIdentifier.TEXTURE_ATLAS,
                "/images/texture_atlas_black.png");
        this.atlas = new TextureAtlas(this.resourceManager.getImage(
                ImageIdentifier.TEXTURE_ATLAS));
        loadSprites();
        loadTankSpriteSheetMaps();
        this.tileMap = new TileMap();
        this.tileMap.loadMap("/tilemap/level1.map");

        startNewGame();
        this.loaded = true;
    }
    
    @Override
    public void unload() {
        for (SpriteSheetIdentifier identifier : SpriteSheetIdentifier.values()) {
            this.spriteSheetManager.remove(identifier);
        }
        this.resourceManager.unloadImage(ImageIdentifier.TEXTURE_ATLAS);
        this.tileMap = null;
    }

    private void startNewGame() {
        this.entityManager.removeAllEntities();
        createEntities();
        gameStatus = GameStatus.PLAY;
    }

    private void createEntities() {
        Player tanque = new Player(this, PlayerTankType.MIDDLE,
                PLAYER_RESPAWN_POSITION.x, PLAYER_RESPAWN_POSITION.y,
                Direction.NORTH
        );
        this.player = tanque;
        this.player.addProtection();
        this.entityManager.addEntity(tanque);
        this.eagle = new Eagle(this, EAGLE_POSITION.x, EAGLE_POSITION.y);
        this.entityManager.addEntity(eagle);
        this.rightPanel = new GameInfoPanel(this, RIGHT_PANEL_POSITION.x,
                RIGHT_PANEL_POSITION.y);
        this.entityManager.addEntity(this.rightPanel);
    }

    

    private void checkCollisions() {
        List<Entity> projectiles = this.entityManager.getEntitiesByType(
                EntityType.PROJECTILE);
        List<Entity> enemyTanks = this.entityManager.getEntitiesByType(
                EntityType.ENEMY_TANK);
        for (int i = projectiles.size() - 1; i >= 0; --i) {
            Projectile projectile = (Projectile) projectiles.get(i);
            if (projectile.collides(this.eagle)) {
                this.eagle.kill();
                continue;
            }
            for (int j = enemyTanks.size() - 1; j >= 0; --j) {
                EnemyTank enemyTank = (EnemyTank) enemyTanks.get(j);
                if (projectile.collides(enemyTank)) {
                    enemyTank.hit(25);
                    projectile.explode();
                }
            }
        }
    }

    private void checkBonuses() {
        List<Entity> bonuses = this.entityManager.getEntitiesByType(
                EntityType.BONUS);
        for (int i = 0; i < bonuses.size(); ++i) {
            PowerUp bonus = (PowerUp) bonuses.get(i);
            if (this.player.collides(bonus)) {
                onBonusCollected(bonus);
                break;
            }
        }
    }

    private void onBonusCollected(PowerUp bonus) {
        if (bonus.getType() == PowerUpType.TANK) {
            System.out.println("health = " + this.player.getHealth());
            this.player.gainExtraLife();
            System.out.println("Tank collected");
            System.out.println("Gained extra life health = " + this.player.
                    getHealth());
        } else if (bonus.getType() == PowerUpType.STAR) {
            this.player.promote();
            System.out.println("Star collected");
        } else if (bonus.getType() == PowerUpType.GUN) {
            System.out.println("Gun collected");
            this.player.promoteToHeavy();
            System.out.println("Promoted to heavy");
        } else if (bonus.getType() == PowerUpType.HELMET) {
            System.out.println("Helmet collected");
            this.player.addProtection();
            System.out.println("Protection added");
        } else if (bonus.getType() == PowerUpType.GRENADE) {

        } else if (bonus.getType() == PowerUpType.SHOVEL) {

        } else if (bonus.getType() == PowerUpType.TIMER) {

        }
        this.player.takeScore(bonus.getScore());
        bonus.collect();
    }

    private void checkGameStatus() {
        /*if(!this.player.isAlive()){
            this.gameStatus = GameStatus.PLAYER_LOST;
        }
        if(getEnemies().isEmpty()){
            this.gameStatus = GameStatus.PLAYER_WON;
        }*/
    }

    @Override
    public void onKeyPressed(int keyCode) {
    }

    @Override
    public void onKeyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                togglePause();
                break;
            case KeyEvent.VK_N:
                startNewGame();
                break;
            case KeyEvent.VK_F:
                this.player.setCanFire(true);
                break;
            case KeyEvent.VK_A:
                this.tileMap.activateEagleProtection();
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

    private void drawGameStatus(Graphics2D g) {
        Painter.drawCenteredString(g, this.gameStatus.getDescription(),
                FONT_GAME_STATUS, this.gameStatus.getColor(), Game.HEIGHT / 2);
    }

    @Override
    public void update(KeyboardState keyboardState, double frameTime) {
        if (!this.loaded || this.gameStatus != GameStatus.PLAY) {
            return;
        }
        //System.out.println("numEntities.size() = " + this.entities.size());
        this.tileMap.update(keyboardState, frameTime);
        this.entityManager.update(keyboardState, frameTime);
        checkCollisions();
        checkBonuses();
        checkGameStatus();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!this.loaded) {
            return;
        }
        this.tileMap.draw(g);
        this.entityManager.draw(g);
        this.tileMap.drawBushes(g);
        drawGameStatus(g);
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

}
