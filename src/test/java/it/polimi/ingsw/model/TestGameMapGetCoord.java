package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.Square;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method getCoord: it has to return the coordinates of the square
 * passed by parameter.
 */
class TestGameMapGetCoord {

    private GameMap gamemap;
    private Square squareToFindCoords;

    @BeforeEach
    void setUp() {
        Square[][] grid;
        grid = new Square[3][3];
        grid[0][0] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[0][1] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[0][2] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[1][0] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[1][1] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[1][2] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[2][2] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        squareToFindCoords = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        grid[2][0] = squareToFindCoords;
        gamemap = new GameMap(grid, new ArrayList<>(), new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        gamemap = null;
        squareToFindCoords = null;
    }

    @Test
    void getCoord() {
        ArrayList<Integer> coordinates = gamemap.getCoord(squareToFindCoords);
        assertEquals(coordinates.get(0), 2);
        assertEquals(coordinates.get(1), 0);
    }
}