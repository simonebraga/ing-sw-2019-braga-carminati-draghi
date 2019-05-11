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

    private final Figure figure;
    private final String username;
    private Square position;
    private Integer points;
    private DamageTrack damageTrack;
    private MarkTrack markTrack;
    private PointTrack pointTrack;
    private WeaponPocket weaponPocket;
    private PowerupPocket powerupPocket;
    private AmmoPocket ammoPocket;

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

    @Override
    public boolean equals(Object obj) {
        Player player = (Player) obj;
        return player.getFigure()==this.figure && player.getUsername().equals(this.username);
    }
}