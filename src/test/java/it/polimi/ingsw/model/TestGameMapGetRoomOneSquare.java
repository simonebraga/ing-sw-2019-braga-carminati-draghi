package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method getRoom: it returns all the Squares in the room.
 * In this case we have a room that is composed by only one Square.
 */
class TestGameMapGetRoomOneSquare {
    private GameMap gamemap;
    private ArrayList<Square> expectedRoom;
    private ArrayList<Square> room;

    @BeforeEach
    void setUp() {
        Square[][] grid;
        grid = new Square[3][3];
        expectedRoom = new ArrayList<>();
        grid[0][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING,0,0);
        grid[0][1] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.NOTHING,0,1);
        grid[0][2] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.WALL,0,2);
        grid[1][0] = new Square(Border.WALL, Border.NOTHING, Border.WALL, Border.NOTHING,1,0);
        grid[1][1] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.NOTHING,1,1);
        grid[1][2] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.WALL,1,2);
        grid[2][0] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.NOTHING,2,0);
        grid[2][1] = new Square(Border.NOTHING, Border.WALL, Border.NOTHING, Border.WALL,2,1);
        grid[2][2] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.WALL,2,2);
        expectedRoom.add(grid[2][2]);
        gamemap = new GameMap(grid, new ArrayList<>(), new ArrayList<>());
        room = gamemap.getRoom(grid[2][2]);
    }

    @AfterEach
    void tearDown() {
        gamemap = null;
        expectedRoom = null;
        room = null;
    }

    @Test
    void getRoom() {
        assertTrue(room.containsAll(expectedRoom));
        assertTrue(expectedRoom.containsAll(room));
    }
}