package com.igorternyuk.tanks.gameplay.entities.tank.enemytank;

import com.igorternyuk.tanks.gameplay.entities.tank.Alliance;
import com.igorternyuk.tanks.gameplay.entities.tank.Heading;
import com.igorternyuk.tanks.gameplay.entities.tank.TankColor;
import java.util.Objects;

/**
 *
 * @author igor
 */
public class EnemyTankIdentifier {
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
