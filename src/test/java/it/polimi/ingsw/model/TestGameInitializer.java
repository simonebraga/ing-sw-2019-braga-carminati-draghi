package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

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
     * This attribute represents five hypothetical users. It's used to create a GameInitializer object.
     */
    private String[] nicks = {"User1","Users2","User3","User4","User5"};

    /**
     * This method initializes all objects that will be used for the test.
     */
    @BeforeEach
    void setup() {
        Set<String> nickSet = new HashSet<>(Arrays.asList(nicks));
        gameInitializer = new GameInitializer('n',2,nickSet);
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
                gameInitializer.setIndex(i);
                returnedGameTable =  gameInitializer.run();
                assertEquals(actualGameMaps.get(i), returnedGameTable.getGameMap());
            }

            gameInitializer.setGameMode('d');
            for (int i = 0;i < actualGameMaps.size()/2; i++) {
                gameInitializer.setIndex(i);
                returnedGameTable = gameInitializer.run();
                assertEquals(actualGameMaps.get(i),returnedGameTable.getGameMap());
            }
        } catch (IOException e) {
            fail("ObjectMapper cannot interact with maps.json.");
            e.printStackTrace();
        }
    }

    /**
     * This test verifies that run() associates each connected user to a player and its figure correctly.
     */
    @Test
    void runBindsUsersToPlayersCorrectly() {
        GameTable returnedGameTable = gameInitializer.run();
        for (Player player:returnedGameTable.getPlayers()) {
            boolean flag=false;
            for (String nick : nicks) {
                if (player.getUsername().equals(nick)) {
                    flag = true;
                }
                assertNotNull(player.getFigure());
            }
            assertTrue(flag);
        }
    }
    
    @Test
    void runFetchesOldGameSavesCorrectly() {
        //TODO (save files non existing yet)
    }

    /**
     * This method frees all object used in this test suit.
     */
    @AfterEach
    void teardown() {
        gameInitializer = null;
        nicks=null;
    }
}