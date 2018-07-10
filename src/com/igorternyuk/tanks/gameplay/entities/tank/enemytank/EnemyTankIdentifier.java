package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

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
public class EnemyTankIdentifier {

    private static final Map<EnemyTankIdentifier, BufferedImage> SPRITE_SHEET_MAP =
            getAllPossibleIdentifiers();

    private static Map<EnemyTankIdentifier, BufferedImage> getAllPossibleIdentifiers() {
        Map<EnemyTankIdentifier, BufferedImage> map = new HashMap<>();
        for (TankColor color : TankColor.values()) {
            for (Alliance alliance : Alliance.values()) {
                Point topLeft = color.
                        getOffsetFromTankSpriteSheetTopLeftCorner();
                topLeft.x += alliance.
                        getOffsetFromSameColorTankSpriteSheetTopLeftCorner().x;
                topLeft.y += alliance.
                        getOffsetFromSameColorTankSpriteSheetTopLeftCorner().y;
                for (EnemyTankType type : EnemyTankType.values()) {
                    for (Heading heading : Heading.values()) {
                        int dx = heading.getSpriteSheetPositionX();
                        int dy = type.getSpriteSheetPositionY();
                        EnemyTankIdentifier key = new EnemyTankIdentifier(color,
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
    
    public static Map<EnemyTankIdentifier, BufferedImage> getSpriteSheetMap(){
        return Collections.unmodifiableMap(SPRITE_SHEET_MAP);
    }

    private Alliance alliance;
    private TankColor color;
    private Heading heading;
    private EnemyTankType type;

    public EnemyTankIdentifier(TankColor color, Heading heading,
            EnemyTankType type) {
        this.alliance = Alliance.ENEMY;
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

    public EnemyTankType getType() {
        return this.type;
    }

    public void setType(EnemyTankType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.alliance);
        hash = 59 * hash + Objects.hashCode(this.color);
        hash = 59 * hash + Objects.hashCode(this.heading);
        hash = 59 * hash + Objects.hashCode(this.type);
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

        final EnemyTankIdentifier other = (EnemyTankIdentifier) obj;

        return Objects.equals(this.alliance, other.alliance)
                && Objects.equals(this.color, other.color)
                && Objects.equals(this.heading, other.heading)
                && Objects.equals(this.type, other.type);
    }

}
