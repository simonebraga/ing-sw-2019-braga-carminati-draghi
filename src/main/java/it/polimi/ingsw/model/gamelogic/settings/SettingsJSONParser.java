package it.polimi.ingsw.model.gamelogic.settings;

/**
 * This class represents all object to be used to parse from JSON file all game settings.
 *
 * @author Draghi96
 */
public class SettingsJSONParser {

    /**
     * This attribute is the number of kills needed to end the game.
     */
    private Integer maxKills;

    /**
     * This attribute is the amount of points a player get for damaging another player considering the number of times he hit him.
     */
    private Integer[] bounties;

    /**
     * This attribute is the amount of points a player get for damaging another player considering the number of times he hit him when in final frenzy.
     */
    private Integer[] frenzyBounties;

    /**
     * This attribute is the amount of extra points a player get for arrange a double kill.
     */
    private Integer doubleKillValue;

    /**
     * This method is the class constructor.
     *
     * @param maxKills an Integer representing the number of kills needed to end the game.
     * @param bounties an array of Integer representing the amount of points a player get for damaging another player considering the number of times he hit him.
     * @param doubleKillValue an Integer representing the amount of extra points a player get for arrange a double kill.
     * @param frenzyBounties an array of Integer representing the final frenzy bounties.
     */
    public SettingsJSONParser(Integer maxKills, Integer[] bounties, Integer[] frenzyBounties, Integer doubleKillValue) {
        this.maxKills=maxKills;
        this.bounties=bounties;
        this.doubleKillValue=doubleKillValue;
        this.frenzyBounties=frenzyBounties;
    }

    /**
     * This method is a class constructor which initializes all attributes to null.
     */
    public SettingsJSONParser() {
        this.maxKills=null;
        this.bounties=null;
        this.doubleKillValue=null;
        this.frenzyBounties=null;
    }

    /**
     * This method returns the maxKills attribute value.
     */
    public Integer getMaxKills() {
        return maxKills;
    }

    /**
     * This method sets a new value for the maxKills attribute.
     *
     * @param maxKills an Integer representing the number of kills needed to end the game.
     */
    public void setMaxKills(Integer maxKills) {
        this.maxKills = maxKills;
    }

    /**
     * This method returns the bounties attribute value.
     *
     * @return an array of Integer representing the bounties values.
     */
    public Integer[] getBounties() {
        return bounties;
    }

    /**
     * This method sets a new value for the bounties attribute.
     *
     * @param bounties an array of Integer representing the amount of points a player get for damaging another player considering the number of times he hit him.
     */
    public void setBounties(Integer[] bounties) {
        this.bounties = bounties;
    }

    /**
     * This method returns the doubleKillValue attribute value.
     */
    public Integer getDoubleKillValue() {
        return doubleKillValue;
    }

    /**
     * This method sets a new value for the doubleKillValue attribute.
     *
     * @param doubleKillValue an Integer representing the amount of extra points a player get for arrange a double kill.
     */
    public void setDoubleKillValue(Integer doubleKillValue) {
        this.doubleKillValue = doubleKillValue;
    }

    /**
     * This method returns the frenzyBounties attribute values.
     *
     * @return an array of Integer representing the final frenzy bounties.
     */
    public Integer[] getFrenzyBounties() {
        return frenzyBounties;
    }

    /**
     * This method sets a new value for the frenzyBounties attribute.
     *
     * @param frenzyBounties an array of Integer representing the amount of points a player get for damaging
     *                      another player considering the number of times he hit him while in final frenzy.
     */
    public void setFrenzyBounties(Integer[] frenzyBounties) {
        this.frenzyBounties = frenzyBounties;
    }
}
