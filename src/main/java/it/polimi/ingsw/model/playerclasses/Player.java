package it.polimi.ingsw.model.playerclasses;

import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.Square;

import java.util.ArrayList;

/**
 * This class all the elements necessary to a player to play a match. Every element must be accessed directly and with the specific methods provided to be modified
 *
 * @author simonebraga
 */
public class Player {

    private Figure figure;
    private String username;
    private Square position;
    private Integer points;
    private DamageTrack damageTrack;
    private MarkTrack markTrack;
    private PointTrack pointTrack;
    private WeaponPocket weaponPocket;
    private PowerupPocket powerupPocket;
    private AmmoPocket ammoPocket;

    /**
     * This constructor sets all attributes to null.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @author Draghi96
     */
    public Player() {
        this.figure=null;
        this.username=null;
        this.position=null;
        this.points=0;
        this.damageTrack=new DamageTrack();
        this.markTrack=new MarkTrack();
        this.pointTrack=new PointTrack(new ArrayList<Integer>());
        this.weaponPocket=new WeaponPocket();
        this.powerupPocket=new PowerupPocket();
        this.ammoPocket=new AmmoPocket();
    }

    /**
     * This method is the constructor of the class
     * @param figure is the character associated to the player
     * @param username is the username of the player using this class
     */
    public Player(Figure figure, String username) {
        this.figure = figure;
        this.username = username;
        this.position = null;
        this.points = 0;
        this.damageTrack = new DamageTrack();
        this.markTrack = new MarkTrack();
        this.pointTrack = new PointTrack( new ArrayList<Integer>());
        this.weaponPocket = new WeaponPocket();
        this.powerupPocket = new PowerupPocket();
        this.ammoPocket = new AmmoPocket();
    }

    public Figure getFigure() {
        return figure;
    }

    public String getUsername() {
        return username;
    }

    public Square getPosition() {
        return position;
    }

    public Integer getPoints() {
        return points;
    }

    public DamageTrack getDamageTrack() {
        return damageTrack;
    }

    public MarkTrack getMarkTrack() {
        return markTrack;
    }

    public PointTrack getPointTrack() {
        return pointTrack;
    }

    public WeaponPocket getWeaponPocket() {
        return weaponPocket;
    }

    public PowerupPocket getPowerupPocket() {
        return powerupPocket;
    }

    public AmmoPocket getAmmoPocket() {
        return ammoPocket;
    }

    /**
     * This method sets a new value for figure attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param figure a Figure object that will be the new attribute value.
     * @author Draghi96
     */
    public void setFigure(Figure figure) {
        this.figure=figure;
    }

    /**
     * This method sets a new value for username attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param username a String object that will be the new attribute value.
     * @author Draghi96
     */
    public void setUsername(String username) {
        this.username=username;
    }

    /**
     * This method sets a new value for position attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param position a Square object that will be the new attribute value.
     * @author Draghi96
     */
    public void setPosition(Square position) {
        this.position = position;
    }

    /**
     * This method sets a new value for points attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param points an Integer object that will be the new attribute value.
     * @author Draghi96
     */
    public void setPoints(Integer points) {
        this.points = points;
    }

    /**
     * This method sets a new value for damageTrack attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param damageTrack a DamageTrack object that will be the new attribute value.
     * @author Draghi96
     */
    public void setDamageTrack(DamageTrack damageTrack) {
        this.damageTrack = damageTrack;
    }

    /**
     * This method sets a new value for markTrack attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param markTrack a MarkTrack object that will be the new attribute value.
     * @author Draghi96
     */
    public void setMarkTrack(MarkTrack markTrack) {
        this.markTrack = markTrack;
    }

    /**
     * This method sets a new value for pointTrack attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param pointTrack a PointTrack object that will be the new attribute value.
     * @author Draghi96
     */
    public void setPointTrack(PointTrack pointTrack) {
        this.pointTrack = pointTrack;
    }

    /**
     * This method sets a new value for weaponPocket attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param weaponPocket a WeaponPocket object that will be the new attribute value.
     * @author Draghi96
     */
    public void setWeaponPocket(WeaponPocket weaponPocket) {
        this.weaponPocket = weaponPocket;
    }

    /**
     * This method sets a new value for powerupPocket attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param powerupPocket a PowerupPocket object that will be the new attribute value.
     * @author Draghi96
     */
    public void setPowerupPocket(PowerupPocket powerupPocket) {
        this.powerupPocket = powerupPocket;
    }

    /**
     * This method sets a new value for ammoPocket attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param ammoPocket a AmmoPocket object that will be the new attribute value.
     * @author Draghi96
     */
    public void setAmmoPocket(AmmoPocket ammoPocket) {
        this.ammoPocket = ammoPocket;
    }

    /**
     * This method moves the player in another position in the map
     * @param position Square in which player is collocated
     */
    public void move(Square position) {

        this.position = position;

    }

    /**
     * This method adds points to the player
     * @param points is the amount of points to be added
     */
    public void addPoints(Integer points) {

        this.points += points;

    }

    /**
     * This method compares two Player objects and returns true if they are to be considered equals.
     *
     * @param obj a Player to be confronted with the Player object that called this method.
     * @return true if two Players have same nickname and same Figure.
     * @author Draghi96
     */
    @Override
    public boolean equals(Object obj) {
        Player player = (Player) obj;
        return player.getFigure()==this.figure && player.getUsername().equals(this.username);
    }
}