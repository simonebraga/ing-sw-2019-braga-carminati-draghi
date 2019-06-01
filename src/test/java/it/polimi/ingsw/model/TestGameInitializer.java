package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies that a GameInitializer object works correctly.
 *
 * @author Draghi96
 */
class TestGameInitializer {

    /**
     * This attribute is an object of the class to be tested.
     */
    private GameInitializer gameInitializer;

    /**
     * This attribute represents five hypothetical players. It's used to create a GameInitializer object.
     */
    private ArrayList<Player> players;

    /**
     * This method initializes all objects that will be used for the test.
     */
    @BeforeEach
    void setup() {
        players = new ArrayList<>();
        players.add(new Player(Figure.DOZER,"User1"));
        players.add(new Player(Figure.BANSHEE,"User2"));
        players.add(new Player(Figure.DESTRUCTOR,"User3"));
        players.add(new Player(Figure.VIOLET,"User4"));
        players.add(new Player(Figure.SPROG,"User5"));
        gameInitializer = new GameInitializer('n',2,players);
    }

    /**
     * This test verifies that run() loads it's properties file correctly.
     */
    @Test
    void runLoadsPropertiesCorrectly() {
        gameInitializer.run();
        try {
            Properties properties = new Properties();
            FileReader fileReader = new FileReader("src/main/resources/game_settings.properties");
            properties.load(fileReader);
            Integer actualMaxKills = Integer.valueOf(properties.getProperty("maxKills"));
            Integer actualDoubleKillValue = Integer.valueOf(properties.getProperty("doubleKillValue"));
            ArrayList<Integer> actualBountyValues = new ArrayList<>();
            actualBountyValues.add(Integer.valueOf(properties.getProperty("bountyFirst")));
            actualBountyValues.add(Integer.valueOf(properties.getProperty("bountySecond")));
            actualBountyValues.add(Integer.valueOf(properties.getProperty("bountyThird")));
            actualBountyValues.add(Integer.valueOf(properties.getProperty("bountyFourth")));
            actualBountyValues.add(Integer.valueOf(properties.getProperty("bountyFifth")));
            actualBountyValues.add(Integer.valueOf(properties.getProperty("bountySixth")));
            fileReader.close();

            assertEquals(gameInitializer.getBountyValues(),actualBountyValues);
            assertEquals(gameInitializer.getDoubleKillValue(),actualDoubleKillValue);
            assertEquals(gameInitializer.getMaxKills(),actualMaxKills);
            teardown();
        } catch (FileNotFoundException e) {
            fail("game_settings.properties file is unreachable.");
            e.printStackTrace();
        } catch (IOException e) {
            fail("cannot close game_settings.properties file.");
            e.printStackTrace();
        }
    }

    /**
     * This test verifies that run() loads all maps from "maps.json" file correctly.
     */
    @Test
    void runFetchesMapsFromJSONCorrectly() {

        File file = new File("src/main/resources/maps.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            GameMap[] array = mapper.readValue(file,GameMap[].class);
            ArrayList<GameMap> actualGameMaps = new ArrayList<>(Arrays.asList(array));

            GameTable returnedGameTable;

            gameInitializer.setGameMode('n');
            for (int i = 0; i < actualGameMaps.size()/2; i++) {
                gameInitializer.setMapIndex(i);
                returnedGameTable =  gameInitializer.run();
                assertEquals(actualGameMaps.get(i), returnedGameTable.getGameMap());
            }

            gameInitializer.setGameMode('d');
            for (int i = 0;i < actualGameMaps.size()/2; i++) {
                gameInitializer.setMapIndex(i);
                returnedGameTable = gameInitializer.run();
                assertEquals(actualGameMaps.get(i),returnedGameTable.getGameMap());
            }
        } catch (IOException e) {
            fail("ObjectMapper cannot interact with maps.json.");
            e.printStackTrace();
        }
    }

    /**
     * This test verifies that run() returns a GameTable objects in every case if the player list is not empty.
     */
    @Test
    void runDoesntReturnNullIfPlayersListIsNotEmpty() {

        for (int i = 0; i < 3; i++) {
            gameInitializer = new GameInitializer('n',i,players);
            assertNotNull(gameInitializer.run());
            gameInitializer = new GameInitializer('d',i,players);
            assertNotNull(gameInitializer.run());
        }
    }

    /**
     * This method frees all object used in this test suit.
     */
    @AfterEach
    void teardown() {
        gameInitializer = null;
        players=null;
    }
}