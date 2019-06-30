package it.polimi.ingsw.model.smartmodel;

import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;

import java.util.ArrayList;
import java.util.Map;

public class SmartPlayer {
    private Figure figure;
    private ArrayList<Figure> damage;
    private Map<Figure,Integer> marks;
    private Map<Color,Integer> ammo;
    private ArrayList<SmartWeapon> weapons;
    private ArrayList<SmartPowerup> powerups;
    private ArrayList<Integer> pointTrack;
    private int posX;
    private int posY;
    private int deaths;
    private int points;

    public ArrayList<Figure> getDamage() {
        return damage;
    }

    public Map<Figure, Integer> getMarks() {
        return marks;
    }

    public Map<Color, Integer> getAmmo() {
        return ammo;
    }

    public ArrayList<SmartWeapon> getWeapons() {
        return weapons;
    }

    public ArrayList<SmartPowerup> getPowerups() {
        return powerups;
    }

    public ArrayList<Integer> getPointTrack() {
        return pointTrack;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getPoints() {
        return points;
    }

    public void setDamage(ArrayList<Figure> damage) {
        this.damage = damage;
    }

    public void setMarks(Map<Figure,Integer> marks) {
        this.marks = marks;
    }

    public void setAmmo(Map<Color, Integer> ammo) {
        this.ammo = ammo;
    }

    public void setWeapons(ArrayList<SmartWeapon> weapons) {
        this.weapons = weapons;
    }

    public void setPowerups(ArrayList<SmartPowerup> powerups) {
        this.powerups = powerups;
    }

    public void setPointTrack(ArrayList<Integer> pointTrack) {
        this.pointTrack = pointTrack;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }
}
