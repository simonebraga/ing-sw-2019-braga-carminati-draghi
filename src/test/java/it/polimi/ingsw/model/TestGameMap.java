package it.polimi.ingsw.model;

import com.google.gson.Gson;
import it.polimi.ingsw.model.mapclasses.GameMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies that all methods for the GameMap class work correctly.
 */
public class TestGameMap {

    /**
     * This attribute is a GameMap array that will be loaded from a JSON file and used for testing.
     */
    private GameMap[] fetchedMaps;

    /**
     * This attribute is a GameMap object that will be used for testing.
     */
    private GameMap gameMap;

    /**
     * This method creates all objects that will be used for testing.
     */
    @BeforeEach
    void setUp() {
        try {
            FileReader fileReader = new FileReader("src/main/resources/maps.json");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Gson gson = new Gson();
            fetchedMaps = gson.fromJson(bufferedReader,GameMap[].class);
            gameMap=fetchedMaps[0];
            fileReader.close();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            fail();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This test verifies that equals() compares two GameMap objects correctly.
     */
    @Test
    void testEquals() {
        GameMap gameMap1 = fetchedMaps[0];
        assertTrue(gameMap.equals(gameMap1));
        gameMap1=fetchedMaps[1];
        assertFalse(gameMap.equals(gameMap1));
    }

    /**
     * This method frees all objects used in this test suit.
     */
    @AfterEach
    void tearDown() {
        fetchedMaps=null;
        gameMap=null;
    }
}
