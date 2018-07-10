package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.Game;
import com.igorternyuk.tanks.gameplay.entities.tank.Alliance;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetIdentifier;
import com.igorternyuk.tanks.graphics.spritesheets.SpriteSheetManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author igor
 */
public class PlayerTankIdentifier {
private static final Map<PlayerTankIdentifier, BufferedImage> SPRITE_SHEET_MAP =
            getAllPossibleIdentifiers();

    private static Map<PlayerTankIdentifier, BufferedImage> getAllPossibleIdentifiers() {
        Map<PlayerTankIdentifier, BufferedImage> map = new HashMap<>();
        for (TankColor color : TankColor.values()) {
            for (Alliance alliance : Alliance.values()) {
                Point topLeft = color.
                        getOffsetFromTankSpriteSheetTopLeftCorner();
                topLeft.x += alliance.
                        getOffsetFromSameColorTankSpriteSheetTopLeftCorner().x;
                topLeft.y += alliance.
                        getOffsetFromSameColorTankSpriteSheetTopLeftCorner().y;
                for (PlayerTankType type : PlayerTankType.values()) {
                    for (Heading heading : Heading.values()) {
                        int dx = heading.getSpriteSheetPositionX();
                        int dy = type.getSpriteSheetPositionY();
                        PlayerTankIdentifier key = new PlayerTankIdentifier(color,
                                heading, type);
                        SpriteSheetManager manager = SpriteSheetManager.
                                getInstance();
                        BufferedImage spriteSheet = manager.get(
                                SpriteSheetIdentifier.TANK);
                        BufferedImage sprite = spriteSheet.getSubimage(topLeft.x
                                + dx, topLeft.y + dy,
                                2 * Game.TILE_SIZE, Game.TILE_SIZE);
                        map.put(key, sprite);
                    }
                }
            }
        }

        return map;
    }
    
    public static Map<PlayerTankIdentifier, BufferedImage> getSpriteSheetMap(){
        return Collections.unmodifiableMap(SPRITE_SHEET_MAP);
    }
    Alliance alliance;
    TankColor color;
    Heading heading;
    PlayerTankType type;
    
    public PlayerTankIdentifier(){
        this(TankColor.YELLOW, Heading.NORTH, PlayerTankType.REGULAR);
    }
    
    public PlayerTankIdentifier(TankColor color, Heading heading,
            PlayerTankType type) {
        this.alliance = Alliance.PLAYER;
        this.color = color;
        this.heading = heading;
        this.type = type;
    }

    public Alliance getAlliance() {
        return this.alliance;
    }

    public TankColor getColor() {
        return this.color;
    }

    public void setColor(TankColor color) {
        this.color = color;
    }

    public Heading getHeading() {
        return this.heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public PlayerTankType getType() {
        return this.type;
    }

    public void setType(PlayerTankType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.alliance);
        hash = 37 * hash + Objects.hashCode(this.color);
        hash = 37 * hash + Objects.hashCode(this.heading);
        hash = 37 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        final PlayerTankIdentifier other = (PlayerTankIdentifier) obj;
        
        return Objects.equals(this.alliance, other.alliance)
                && Objects.equals(this.color, other.color)
                && Objects.equals(this.heading, other.heading)
                && Objects.equals(this.type, other.type);
    }
    
}
