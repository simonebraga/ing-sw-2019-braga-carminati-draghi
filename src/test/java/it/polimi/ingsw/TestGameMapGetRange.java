package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method getRange.
 */
class TestGameMapGetRange {
    private GameMap gamemap;
    private ArrayList<Square> expectedRange;
    private ArrayList<Square> range;

    @BeforeEach
    void setUp() {
        Square[][] grid;
        grid = new Square[4][4];
        expectedRange = new ArrayList<>();
        grid[0][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING);
        grid[0][1] = new Square(Border.WALL, Border.DOOR, Border.NOTHING, Border.NOTHING);
        grid[0][2] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.NOTHING);
        grid[0][3] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.WALL);
        grid[1][0] = new Square(Border.NOTHING, Border.NOTHING, Border.WALL, Border.WALL);
        grid[1][1] = new Square(Border.DOOR, Border.NOTHING, Border.WALL, Border.WALL);
        grid[1][2] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.WALL);
        grid[1][3] = new Square(Border.NOTHING, Border.NOTHING, Border.WALL, Border.WALL);
        grid[2][0] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.WALL);
        grid[2][1] = new Square(Border.NOTHING, Border.DOOR, Border.WALL, Border.NOTHING);
        grid[2][2] = new Square(Border.NOTHING, Border.DOOR, Border.NOTHING, Border.WALL);
        grid[2][3] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.WALL);
        grid[3][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING);
        grid[3][1] = new Square(Border.DOOR, Border.WALL, Border.NOTHING, Border.WALL);
        grid[3][2] = new Square(Border.DOOR, Border.WALL, Border.WALL, Border.NOTHING);
        grid[3][3] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.WALL);
        gamemap = new GameMap(grid, new ArrayList<>(), new ArrayList<>());
        expectedRange.add(grid[0][1]);
        expectedRange.add(grid[1][1]);
        expectedRange.add(grid[1][2]);
        expectedRange.add(grid[2][1]);
        expectedRange.add(grid[2][2]);
        expectedRange.add(grid[3][0]);
        expectedRange.add(grid[3][1]);
        expectedRange.add(grid[3][2]);
        expectedRange.add(grid[3][3]);
        range = gamemap.getRange(grid[2][2], 3);
    }

    @AfterEach
    void tearDown() {
        gamemap = null;
        expectedRange = null;
        range = null;
    }

    @Test
    void getRange() {
        assertTrue(range.containsAll(expectedRange));
        assertTrue(expectedRange.containsAll(range));
    }
}