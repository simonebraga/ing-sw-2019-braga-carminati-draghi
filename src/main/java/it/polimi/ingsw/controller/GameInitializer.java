package it.polimi.ingsw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Deck;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.playerclasses.DoubleKillCounter;
import it.polimi.ingsw.model.playerclasses.KillshotTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.playerclasses.StartingPlayerMarker;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class creates an object that has the duty to generate a ready-to-play game table
 * accordingly to users settings choices.
 *
 * @author Draghi96
 */
public class GameInitializer {

    /**
     * This attribute indicates the maximum amount of players this game can be played with.<br>
     *     It's value in this game is constant.
     */
    private static int maxPlayers = 5;

    /**
     * This attribute indicates how many kills a match will require to end.<br>
     *     It's value is loaded from properties files.
     */
    private Integer maxKills;

    /**
     * This attribute indicates how many points each double-kill gives to players.<br>
     *     This value is loaded from properties files.
     */
    private Integer doubleKillValue;

    /**
     * This attribute indicates how many points each player will receive
     * for a kill accordingly to the times each player damaged the killed one.<br>
     *     This value is loaded from properties files.
     */
    private ArrayList<Integer> bountyValues = new ArrayList<>();

    /**
     * This attribute is a character that indicates which game mode users choose.
     * <p>'n' means users asked to load a new match in normal mode.<br>
     * 'd' means users asked to load a new match in domination mode.<br>
     * 's' means users asked to resume an old match from JSON file.</p>
     */
    private Character gameMode;     //'n': normal mode, 'd' : domination mode, 's' : resume old save

    /**
     * This attribute indicates the maps list index from which fetch the map that will be used.<br>
     *     This attribute is assigned by users.
     */
    private Integer map;

    /**
     * This attribute contains all connected users nicknames.<br>
     *     To each nickname will be associated a Player object.
     */
    private Set<String> nicknames;

    /**
     * This method is the class constructor.
     *
     * @param gameMode is a Character that indicates the game mode chosen by users.
     * @param map is an integer chosen by users that indicates the maps list index from which fetch the map.
     * @param nicknames is a Set of strings that represents all connected players nicknames.
     */
    public GameInitializer(Character gameMode, Integer map, Set<String> nicknames) {
        this.gameMode = gameMode;
        this.map = map;
        this.nicknames=nicknames;
    }

    public void setGameMode(Character gameMode) {
        this.gameMode = gameMode;
    }

    public void setMap(Integer map) {
        this.map = map;
    }

    public Integer getMaxKills() {
        return maxKills;
    }

    public Integer getDoubleKillValue() {
        return doubleKillValue;
    }

    public ArrayList<Integer> getBountyValues() {
        return bountyValues;
    }

    /**
     * This method initializes a game match that will be ready to play.
     *
     * @return a GameTable that contains all information to start a match.
     */
    public GameTable run() {

        //load game properties from "game_settings.properties" file
        try {
            Properties properties = new Properties();
            FileReader fileReader = new FileReader("src/main/resources/game_settings.properties");
            properties.load(fileReader);
            this.maxKills=Integer.valueOf(properties.getProperty("maxKills"));
            this.doubleKillValue=Integer.valueOf(properties.getProperty("doubleKillValue"));
            this.bountyValues.add(Integer.valueOf(properties.getProperty("bountyFirst")));
            this.bountyValues.add(Integer.valueOf(properties.getProperty("bountySecond")));
            this.bountyValues.add(Integer.valueOf(properties.getProperty("bountyThird")));
            this.bountyValues.add(Integer.valueOf(properties.getProperty("bountyFourth")));
            this.bountyValues.add(Integer.valueOf(properties.getProperty("bountyFifth")));
            this.bountyValues.add(Integer.valueOf(properties.getProperty("bountySixth")));
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.gameMode == 's') {         //users choose to load an old match
            try {
                return fetchSavedGame();    //fetching old match from "save.json" file
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //else (new match)
        try {

            //fetching GameMaps from JSON
            GameMap chosenGameMap = fetchGameMap(this.map,this.gameMode);

            //initializing killShotTrack
            KillshotTrack chosenKillshotTrack = new KillshotTrack(this.maxKills,this.bountyValues);

            //initializing doubleKillCounter
            DoubleKillCounter chosenDoubleKillCounter = new DoubleKillCounter(this.doubleKillValue);

            //initializing decks
            DecksInitializer decksInitializer = new DecksInitializer();
            Deck<AmmoTile> loadedAmmoTileDeck = decksInitializer.initDeck("ammotiles");
            Deck<Weapon> loadedWeaponDeck = decksInitializer.initDeck("weapons");
            Deck<Powerup> loadedPowerupDeck = decksInitializer.initDeck("powerups");

            //creating ArrayList<Player> and binding it with connected clients
            Figure[] allFigures = Figure.values();
            int i=0;
            ArrayList<Player> connectedPlayers = new ArrayList<>();
            for (String nick : this.nicknames) {
                connectedPlayers.add(new Player(allFigures[i],nick));    //nicknames never outnumbers figures
                i++;
            }

            //StartingPlayerMarker
            int randomNum = ThreadLocalRandom.current().nextInt(0, maxPlayers);
            StartingPlayerMarker chosenStartingPlayerMarker = new StartingPlayerMarker(connectedPlayers.get(randomNum));

            return new GameTable(chosenStartingPlayerMarker,chosenKillshotTrack,
                            chosenDoubleKillCounter,chosenGameMap,connectedPlayers,loadedWeaponDeck,
                            loadedPowerupDeck,loadedAmmoTileDeck,chosenStartingPlayerMarker.getTarget());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method fetches all maps from a JSON file and store them in array, then selects a map
     * accordingly to users choices and returns it.
     *
     * @param mapIndex an Integer index that represent selected map in the maps list.
     * @param gameMode a Character that indicates which type of map to load (normal or domination).
     * @return the GameMap selected by users from all maps loaded from "maps.json" file.
     * @throws IOException if "maps.json" is not found or it can't be opened.
     */
    private GameMap fetchGameMap(Integer mapIndex, Character gameMode) throws IOException {

        File file = new File("src/main/resources/maps.json");
        ObjectMapper objectMapper = new ObjectMapper();
        GameMap[] gameMaps = objectMapper.readValue(file,GameMap[].class);

        if (gameMode == 'n'){
            return gameMaps[mapIndex];
        } else {    //gameMode == 'd'
            return gameMaps[mapIndex + (gameMaps.length / 2)];    //normal mode maps will be formatted before domination mode maps in JSON
        }
    }

    /**
     * This method fetches a GameTable from a JSON file and returns it to be used for the current match.
     *
     * @return a GameTable loaded from "save.json" file.
     * @throws IOException if "save.json" is not found or can't be opened.
     */
    private GameTable fetchSavedGame() throws IOException {
        FileReader fileReader = new FileReader("src/main/resources/save.json");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        Gson gSon = new Gson();
        GameTable gameTable = gSon.fromJson(bufferedReader,GameTable.class);
        fileReader.close();
        bufferedReader.close();
        return gameTable;
    }
}