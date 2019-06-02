package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Deck;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.playerclasses.DoubleKillCounter;
import it.polimi.ingsw.model.playerclasses.KillshotTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.playerclasses.StartingPlayerMarker;

import java.util.ArrayList;

/**
 * This class contains all the necessary references to the elements of the game model
 *
 * @author simonebraga
 */
public class GameTable {

    StartingPlayerMarker startingPlayerMarker;
    KillshotTrack killshotTrack;
    DoubleKillCounter doubleKillCounter;
    GameMap gameMap;
    ArrayList<Player> players;
    Deck<Weapon> weaponDeck;
    Deck<Powerup> powerupDeck;
    Deck<AmmoTile> ammoTileDeck;

    /**
     * This attribute indicates which player that has the current turn.
     *
     * @author Draghi96
     */
    private Player currentTurnPlayer;

    /**
     * This attribute indicates if this match is a domination mode or normal mode.
     *
     * @author Draghi96
     */
    private Boolean isDomination;

    /**
     * This attribute indicates in which phase the game currently is.
     * <p>"ff" means that the game is in final frenzy.<br>
     *     "ft" means that the game is in its first turns.<br>
     *     "rll" means that the game is in the normal turn stage.</p>
     *
     * @author Draghi96
     */
    private String gamePhase;

    /**
     * This attribute contains the name of the save file associated with this match.
     *
     * <p>This is a String that is the save file name to be used to store the game table.<br>
     * Use "new" as parameter to create a new file when saving.<br>
     * Only names formatted as "saveX" where 'X' is an integer are accepted as well as "new".</p>
     *
     * @author Draghi96
     */
    private String saveFileName;

    /**
     * This attribute marks the player that started the final frenzy.
     *
     * @author Draghi96
     */
    private Player frenzyBeginner;

    /**
     * This constructor initializes all attributes to null.
     *
     * @author Draghi96
     */
    public GameTable() {
        this.startingPlayerMarker=null;
        this.killshotTrack=null;
        this.doubleKillCounter=null;
        this.gameMap=null;
        this.players=null;
        this.weaponDeck=null;
        this.powerupDeck=null;
        this.ammoTileDeck=null;
        this.currentTurnPlayer=null;
        this.isDomination=false;
        this.gamePhase=null;
        this.saveFileName=null;
        this.frenzyBeginner=null;
    }

    public GameTable(StartingPlayerMarker startingPlayerMarker, KillshotTrack killshotTrack, DoubleKillCounter doubleKillCounter, GameMap gameMap, ArrayList<Player> players, Deck weaponDeck, Deck powerupDeck, Deck ammoTileDeck, Player currentTurnPlayer,Boolean isDomination) {
        this.startingPlayerMarker = startingPlayerMarker;
        this.killshotTrack = killshotTrack;
        this.doubleKillCounter = doubleKillCounter;
        this.gameMap = gameMap;
        this.players = players;
        this.weaponDeck = weaponDeck;
        this.powerupDeck = powerupDeck;
        this.ammoTileDeck = ammoTileDeck;
        this.currentTurnPlayer = currentTurnPlayer;
        this.isDomination=isDomination;
        this.gamePhase = "ff";
        this.saveFileName = "new";
        this.frenzyBeginner = null;
    }

    public GameTable(StartingPlayerMarker startingPlayerMarker, KillshotTrack killshotTrack, DoubleKillCounter doubleKillCounter, GameMap gameMap, ArrayList<Player> players, Deck weaponDeck, Deck powerupDeck, Deck ammoTileDeck) {
        this.startingPlayerMarker = startingPlayerMarker;
        this.killshotTrack = killshotTrack;
        this.doubleKillCounter = doubleKillCounter;
        this.gameMap = gameMap;
        this.players = players;
        this.weaponDeck = weaponDeck;
        this.powerupDeck = powerupDeck;
        this.ammoTileDeck = ammoTileDeck;
        this.currentTurnPlayer = startingPlayerMarker.getTarget();
        this.isDomination=false;
        this.gamePhase = "ff";
        this.saveFileName = "new";
        this.frenzyBeginner = null;
    }

    public void setStartingPlayerMarker(StartingPlayerMarker startingPlayerMarker) {
        this.startingPlayerMarker = startingPlayerMarker;
    }

    public void setKillshotTrack(KillshotTrack killshotTrack) {
        this.killshotTrack = killshotTrack;
    }

    public void setDoubleKillCounter(DoubleKillCounter doubleKillCounter) {
        this.doubleKillCounter = doubleKillCounter;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setWeaponDeck(Deck<Weapon> weaponDeck) {
        this.weaponDeck = weaponDeck;
    }

    public void setPowerupDeck(Deck<Powerup> powerupDeck) {
        this.powerupDeck = powerupDeck;
    }

    public void setAmmoTileDeck(Deck<AmmoTile> ammoTileDeck) {
        this.ammoTileDeck = ammoTileDeck;
    }

    /**
     * This method sets which player that has the current turn.
     *
     * @param player the Player that has the current turn.
     * @author Draghi96
     */
    public void setCurrentTurnPlayer(Player player) {this.currentTurnPlayer=player; }

    /**
     * This method sets the domination mode flag.
     *
     * @param isDomination a Boolean that is true if the match is in domination mode.
     * @author Draghi96
     */
    public void setIsDomination(Boolean isDomination) {
        this.isDomination=isDomination;
    }

    /**
     * This method sets a new string for gamePhase.
     *
     * @param gamePhase a String that indicates in which phase the game was left.
     * @author Draghi96
     */
    public void setGamePhase(String gamePhase) { this.gamePhase=gamePhase; }

    /**
     * This method sets a new file name to be associated with this match save file.
     *
     * @param saveFileName a String that contains the save file name to be associated with this match save.
     * @author Draghi96
     */
    public void setSaveFileName(String saveFileName) { this.saveFileName=saveFileName; }

    /**
     * This method sets a new player to be the frenzy beginner.
     * @param frenzyBeginner a Player that will be the frenzy beginner.
     */
    public void setFrenzyBeginner(Player frenzyBeginner) { this.frenzyBeginner=frenzyBeginner; }

    public StartingPlayerMarker getStartingPlayerMarker() {
        return startingPlayerMarker;
    }

    public KillshotTrack getKillshotTrack() {
        return killshotTrack;
    }

    public DoubleKillCounter getDoubleKillCounter() {
        return doubleKillCounter;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Deck<Weapon> getWeaponDeck() {
        return weaponDeck;
    }

    public Deck<Powerup> getPowerupDeck() {
        return powerupDeck;
    }

    public Deck<AmmoTile> getAmmoTileDeck() {
        return ammoTileDeck;
    }

    /**
     * This method returns the player that has the current turn.
     *
     * @return a Player that has the current turn.
     * @author Draghi96
     */
    public Player getCurrentTurnPlayer() { return this.currentTurnPlayer; }

    /**
     * This method returns the isDomination attribute value.
     *
     * @return true if the match is in domination mode.
     * @author Draghi96
     */
    public Boolean getIsDomination() {
        return isDomination;
    }

    /**
     * This method returns the gamePhase attribute value.
     *
     * @return a String that indicates in which phase the game currently is.
     * @author Draghi96
     */
    public String getGamePhase() { return this.gamePhase; }

    /**
     * This method returns the name of the save file associated with this match.
     *
     * @return a String that contains the name of the save file associated with this match.
     * @author Draghi96
     */
    public String getSaveFileName() {
        return this.saveFileName;
    }

    /**
     * This method returns the player who began the final frenzy.
     *
     * @return a Player reference of the player that started final frenzy.
     * @author Draghi96
     */
    public Player getFrenzyBeginner() { return this.frenzyBeginner; }

}