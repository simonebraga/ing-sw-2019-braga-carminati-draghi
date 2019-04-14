package it.polimi.ingsw;

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

    public GameTable(StartingPlayerMarker startingPlayerMarker, KillshotTrack killshotTrack, DoubleKillCounter doubleKillCounter, GameMap gameMap, ArrayList<Player> players, Deck weaponDeck, Deck powerupDeck, Deck ammoTileDeck) {
        this.startingPlayerMarker = startingPlayerMarker;
        this.killshotTrack = killshotTrack;
        this.doubleKillCounter = doubleKillCounter;
        this.gameMap = gameMap;
        this.players = players;
        this.weaponDeck = weaponDeck;
        this.powerupDeck = powerupDeck;
        this.ammoTileDeck = ammoTileDeck;
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

    public void setWeaponDeck(Deck weaponDeck) {
        this.weaponDeck = weaponDeck;
    }

    public void setPowerupDeck(Deck powerupDeck) {
        this.powerupDeck = powerupDeck;
    }

    public void setAmmoTileDeck(Deck ammoTileDeck) {
        this.ammoTileDeck = ammoTileDeck;
    }

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

    public Deck getWeaponDeck() {
        return weaponDeck;
    }

    public Deck getPowerupDeck() {
        return powerupDeck;
    }

    public Deck getAmmoTileDeck() {
        return ammoTileDeck;
    }

}
