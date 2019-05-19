package it.polimi.ingsw.model.playerclasses;

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
     * This constructor initializes all attributes to null.<br>
     *     It's mainly used in Jackson JSON files fetching.
     *
     * @author Draghi96
     */
    public DoubleKillCounter() {
        this.doubleKillValue=null;
        this.list=new ArrayList<Player>();
    }

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
     * This method sets a new value for list attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param list an ArrayList of Players that will represent the new list attribute.
     * @author Draghi96
     */
    public void setList(ArrayList<Player> list) {
        this.list=list;
    }

    /**
     * This method sets a new value for the doubleKillValue attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param doubleKillValue an Integer that will represent the new doubleKillValue attribute.
     * @author Draghi96
     */
    public void setDoubleKillValue(Integer doubleKillValue) {
        this.doubleKillValue=doubleKillValue;
    }

    /**
     * This method adds a player to the list of double killers
     * @param killer is the player who made the double kill
     */
    public void add(Player killer) {

        list.add(killer);

    }
}
