package it.polimi.ingsw.model.smartmodel;

import it.polimi.ingsw.model.enumeratedclasses.Color;

import java.util.ArrayList;

public class SmartTile {
    private int powerup;
    private ArrayList<Color> ammo;
    private int posX;
    private int posY;

    public int getPowerup() {
        return powerup;
    }

    public ArrayList<Color> getAmmo() {
        return ammo;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPowerup(int powerup) {
        this.powerup = powerup;
    }

    public void setAmmo(ArrayList<Color> ammo) {
        this.ammo = ammo;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    @Override
    public boolean equals(Object obj) {
        SmartTile smartTile = (SmartTile) obj;
        return ((smartTile.getPowerup() == this.getPowerup()) && (smartTile.getAmmo().equals(this.getAmmo())));
    }
}
