package it.polimi.ingsw.model.smartmodel;

import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;

public class SmartPowerup {
    private PowerupName powerupName;
    private Color color;

    public PowerupName getPowerupName() {
        return powerupName;
    }

    public Color getColor() {
        return color;
    }

    public void setPowerupName(PowerupName powerupName) {
        this.powerupName = powerupName;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        SmartPowerup smartPowerup = (SmartPowerup) obj;
        return ((smartPowerup.getPowerupName() == this.powerupName) && (smartPowerup.getColor() == this.color));
    }
}