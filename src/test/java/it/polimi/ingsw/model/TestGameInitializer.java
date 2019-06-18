package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.gamelogic.settings.SettingsJSONParser;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
     * This test verifies that run() loads it's settings file correctly.
     */
    @Test
    void runLoadsSettingsCorrectly() {
        gameInitializer.run();
        FileInputStream file = null;
        try {
            file = new FileInputStream("src/main/resources/game_settings.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SettingsJSONParser settings = objectMapper.readValue(file, SettingsJSONParser.class);
            Integer actualMaxKills = settings.getMaxKills();
            ArrayList<Integer> actualBountyValues = new ArrayList<>(Arrays.asList(settings.getBounties()));
            Integer actualDoubleKillValue = settings.getDoubleKillValue();
            file.close();

            assertEquals(gameInitializer.getBountyValues(),actualBountyValues);
            assertEquals(gameInitializer.getDoubleKillValue(),actualDoubleKillValue);
            assertEquals(gameInitializer.getMaxKills(),actualMaxKills);
        } catch (IOException e) {
            e.printStackTrace();
        }
        teardown();
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
     * This test verifies that run() gives every tile square a tile.
     */
    @Test
    void runGivesEveryTileSquareATile() {
        GameTable gameTable = gameInitializer.run();
        ArrayList<Square> tileSquares = new ArrayList<>(gameTable.getGameMap().getGridAsList());

        tileSquares = new ArrayList<>(tileSquares.stream().filter(square->gameTable.getGameMap().getTileSquares().contains(square)).collect(Collectors.toList()));
        for (Square square: tileSquares){
            TileSquare tilesquare = (TileSquare) square;
            assertNotNull(tilesquare.getTile());
        }
    }

    /**
     * This test verifies that run() gives every spawn square three weapons.
     */
    @Test
    void runGivesEverySpawnSquareThreeWeapons() {
        GameTable gameTable = gameInitializer.run();
        ArrayList<Square> spawnSquares = new ArrayList<>(gameTable.getGameMap().getGridAsList());

        spawnSquares = new ArrayList<>(spawnSquares.stream().filter(square->gameTable.getGameMap().getSpawnSquares().contains(square)).collect(Collectors.toList()));
        for (Square square: spawnSquares){
            SpawnSquare spawnSquare = (SpawnSquare) square;
            assertNotNull(spawnSquare.getWeapons());
            assertEquals(3,spawnSquare.getWeapons().size());
        }
    }

    /**
     * This test verifies that players get one ammo for every color each.
     */
    @Test
    void runGivesOneAmmoOfEachColorToEachPlayer() {
        GameTable gameTable = gameInitializer.run();
        for (Player player : gameTable.getPlayers()) {
            assertNotEquals(0, player.getAmmoPocket().getAmmo(Color.RED));
            assertNotEquals(0, player.getAmmoPocket().getAmmo(Color.BLUE));
            assertNotEquals(0, player.getAmmoPocket().getAmmo(Color.YELLOW));
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