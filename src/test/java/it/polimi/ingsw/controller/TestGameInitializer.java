package it.polimi.ingsw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.mapclasses.GameMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestGameInitializer {

    private GameInitializer gameInitializer;
    private String[] nicks = {"User1","Users2","User3","User4","User5"};

    @BeforeEach
    void setup() {
        Set<String> nickSet = new HashSet<>();
        for (int i = 0; i < nicks.length; i++) {
            nickSet.add(nicks[i]);
        }
        gameInitializer = new GameInitializer('n',2,nickSet);
    }

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
                gameInitializer.setMap(i);
                returnedGameTable =  gameInitializer.run();
                assertTrue(actualGameMaps.get(i).equals(returnedGameTable.getGameMap()));
            }

            gameInitializer.setGameMode('d');
            for (int i = 0;i < actualGameMaps.size()/2; i++) {
                gameInitializer.setMap(i);
                returnedGameTable = gameInitializer.run();
                assertEquals(actualGameMaps.get(i),returnedGameTable.getGameMap());
            }
        } catch (IOException e) {
            fail("ObjectMapper cannot interact with maps.json.");
            e.printStackTrace();
        }
    }

    @Test
    void runBindsUsersToPlayersCorrectly() {

    }

    @AfterEach
    void teardown() {
        gameInitializer = null; }
}