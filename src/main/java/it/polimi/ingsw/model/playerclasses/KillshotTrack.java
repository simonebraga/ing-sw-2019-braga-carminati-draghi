package it.polimi.ingsw.model.playerclasses;

import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;

import java.util.ArrayList;

/**
 * This class contains the killshot track, and throws exceptions to signal when the frenzy mod must start
 *
 * @author simonebraga
 */
public class KillshotTrack {

    /**
     * This attribute contains the list of the players who made a kill or an overkill
     */
    private ArrayList<Player> killTrack;

    /**
     * This attribute contains the number of kills necessary to start the frenzy mode
     */
    private Integer killCount;

    /**
     * This attribute contains the points for damage counted at the end of the match considering the killTrack
     */
    private ArrayList<Integer> value;

    /**
     * This constructor initializes all attributes to null.<br>
     *     It's mainly used in Jackson JSON files fetching.
     *
     * @author Draghi96
     */
    public KillshotTrack() {
        this.value=new ArrayList<>();
        this.killCount=null;
        this.killTrack=new ArrayList<>();
    }

    /**
     * This method is the constructor of the class
     * @param killCount is the number of kills necessary to finish the game
     * @param value is the points for damage track of the killshot track
     */
    public KillshotTrack(Integer killCount, ArrayList<Integer> value) {
        this.killTrack = new ArrayList<>();
        this.killCount = killCount;
        this.value = value;
    }

    public ArrayList<Player> getKillTrack() {
        return killTrack;
    }

    public Integer getKillCount() {
        return killCount;
    }

    public ArrayList<Integer> getValue() {
        return value;
    }

    /**
     * This method sets a new value for killTrack attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param killTrack an ArrayList of Players that will be the new killTrack attribute.
     * @author Draghi96
     */
    public void setKillTrack(ArrayList<Player> killTrack) {
        this.killTrack=killTrack;
    }

    /**
     * This method sets a new value for killCount attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param killCount an Integer that will represent the new killCount attribute.
     * @author Draghi96
     */
    public void setKillCount(Integer killCount) {
        this.killCount=killCount;
    }

    /**
     * This method sets a new value for value attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param value an ArrayList of Integer that will represent the new value attribute.
     * @author Draghi96
     */
    public void setValue(ArrayList<Integer> value) {
        this.value=value;
    }

    /**
     * This methods adds an element of type Player in the killshot track and reduces the killCounter
     * @param killer is the player who made the kill
     * @throws FrenzyModeException is thrown when the killCounter reaches 0
     */
    public void kill(Player killer) throws FrenzyModeException{

        killTrack.add(killer);
        killCount--;
        if (killCount == 0)
            throw new FrenzyModeException();

    }

    /**
     * This method adds an element of type player in the killshot track without counting the kill
     * @param killer is the player who made the overkill
     */
    public void overKill(Player killer) {

        killTrack.add(killer);

    }
}
