package com.igorternyuk.tanks.gameplay.entities.player;

import com.igorternyuk.tanks.gameplay.entities.tank.Alliance;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import java.util.Objects;

/**
 *
 * @author igor
 */
public class PlayerTankIdentifier {

    Alliance alliance;
    TankColor color;
    Heading heading;
    PlayerTankType type;

    public PlayerTankIdentifier() {
        this(TankColor.YELLOW, Heading.NORTH, PlayerTankType.BASIC);
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
