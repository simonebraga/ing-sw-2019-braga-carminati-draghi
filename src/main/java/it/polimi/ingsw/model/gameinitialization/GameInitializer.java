package it.polimi.ingsw.model.gameinitialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Deck;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.playerclasses.DoubleKillCounter;
import it.polimi.ingsw.model.playerclasses.KillshotTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.playerclasses.StartingPlayerMarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

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
     * In case of a new match this attribute indicates the maps list index from which fetch the map to will be used.<br>
     *     In case of an old match loading this attribute indicates the file index in a save file list.
     *     <p>This attribute is chosen by users.</p>
     */
    private Integer index;

    /**
     * This attribute contains all connected players as Player objects.
     */
    private ArrayList<Player> connectedPlayers;

    /**
     * This attribute represent the chosen StartingPlayerMarker object.
     */
    private StartingPlayerMarker chosenStartingPlayerMarker;

    /**
     * This method is the class constructor.
     *
     * @param gameMode is a Character that indicates the game mode chosen by users.
     * @param index is an integer chosen by users that indicates the maps list index or the save file index to be loaded.
     * @param connectedPlayers is an ArrayList of strings that represents all connected players nicknames.
     * @param chosenStartingPlayerMarker is a StartingPlayerMarker object that represents the starting player.
     */
    public GameInitializer(Character gameMode, Integer index, ArrayList<Player> connectedPlayers,
                           StartingPlayerMarker chosenStartingPlayerMarker) {
        this.gameMode = gameMode;
        this.index = index;
        this.connectedPlayers= new ArrayList<>(connectedPlayers);
        this.chosenStartingPlayerMarker = chosenStartingPlayerMarker;
    }

    public void setGameMode(Character gameMode) {
        this.gameMode = gameMode;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
                return fetchSavedGame(this.index);    //fetching old match from "save.json" file
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //else (new match)
        try {

            //fetching GameMaps from JSON
            GameMap chosenGameMap = fetchGameMap(this.index,this.gameMode);

            //initializing killShotTrack
            KillshotTrack chosenKillshotTrack = new KillshotTrack(this.maxKills,this.bountyValues);

            //initializing doubleKillCounter
            DoubleKillCounter chosenDoubleKillCounter = new DoubleKillCounter(this.doubleKillValue);

            //initializing decks
            DecksInitializer decksInitializer = new DecksInitializer();
            Deck<AmmoTile> loadedAmmoTileDeck = decksInitializer.initDeck("ammotiles");
            Deck<Weapon> loadedWeaponDeck = decksInitializer.initDeck("weapons");
            Deck<Powerup> loadedPowerupDeck = decksInitializer.initDeck("powerups");

            //draw 1 powerup card each player before starting the game
            for (Player player: connectedPlayers) {
                player.getPowerupPocket().addPowerup(loadedPowerupDeck.draw());
            }

            if (gameMode=='d') {
                return new GameTable(this.chosenStartingPlayerMarker,chosenKillshotTrack,
                            chosenDoubleKillCounter,chosenGameMap,this.connectedPlayers,loadedWeaponDeck,
                            loadedPowerupDeck,loadedAmmoTileDeck,this.chosenStartingPlayerMarker.getTarget(),true);
            } else {    //gameMode == 'n' true
                return new GameTable(this.chosenStartingPlayerMarker,chosenKillshotTrack,
                        chosenDoubleKillCounter,chosenGameMap,this.connectedPlayers,loadedWeaponDeck,
                        loadedPowerupDeck,loadedAmmoTileDeck,this.chosenStartingPlayerMarker.getTarget(),false);

            }

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
     * @param fileIndex an Integer that represents the index of the selected save file.
     * @return a GameTable loaded from "save.json" file.
     * @throws IOException if "save.json" is not found or can't be opened.
     */
    private GameTable fetchSavedGame(Integer fileIndex) throws IOException {
        File file = new File("src/main/resources/save_list.json");
        ObjectMapper objectMapper = new ObjectMapper();
        String[] saveFileNameList = objectMapper.readValue(file,String[].class);
        String chosenFileName = saveFileNameList[fileIndex];

        file = new File("src/main/resources/"+chosenFileName+".json");
        return objectMapper.readValue(file,GameTable.class);
    }
}