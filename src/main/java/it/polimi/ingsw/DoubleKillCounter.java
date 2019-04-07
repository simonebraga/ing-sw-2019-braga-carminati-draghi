package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class contains the list of the players who made double kills during the game, and the value of the double kills
 *
 * @author simonebraga
 */
public class DoubleKillCounter {

    /**
     * This attribute contains the list of double killers
     */
    private ArrayList<Player> list;

    /**
     * This attribute contains the value of the double kill
     */
    private Integer doubleKillValue;

    /**
     * This method is the constructor of the class
     * @param doubleKillValue is the value of the double kill
     */
    public DoubleKillCounter(Integer doubleKillValue) {

        this.list = new ArrayList<>();
        this.doubleKillValue = doubleKillValue;

    }

    public ArrayList<Player> getList() {
        return list;
    }

    public Integer getDoubleKillValue() {
        return doubleKillValue;
    }

    /**
     * This method adds a player to the list of double killers
     * @param killer is the player who made the double kill
     */
    public void add(Player killer) {

        list.add(killer);

    }
}
