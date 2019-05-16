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
    }

    public GameTable(StartingPlayerMarker startingPlayerMarker, KillshotTrack killshotTrack, DoubleKillCounter doubleKillCounter, GameMap gameMap, ArrayList<Player> players, Deck weaponDeck, Deck powerupDeck, Deck ammoTileDeck, Player currentTurnPlayer) {
        this.startingPlayerMarker = startingPlayerMarker;
        this.killshotTrack = killshotTrack;
        this.doubleKillCounter = doubleKillCounter;
        this.gameMap = gameMap;
        this.players = players;
        this.weaponDeck = weaponDeck;
        this.powerupDeck = powerupDeck;
        this.ammoTileDeck = ammoTileDeck;
        this.currentTurnPlayer = currentTurnPlayer;
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

}
