package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the normal functioning of the method getVisibility.
 * In this case the Square has no doors.
 */
class TestGameMapGetVisibilityNoDoors {
    private ArrayList<Square> expectedVisibility;
    private ArrayList<Square> visibility;

    @BeforeEach
    void setUp() {
        GameMap gamemap;
        Square[][] grid;
        grid = new Square[3][3];
        expectedVisibility = new ArrayList<>();
        grid[0][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING);
        grid[0][1] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.NOTHING);
        grid[0][2] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.WALL);
        grid[1][0] = new Square(Border.WALL, Border.NOTHING, Border.WALL, Border.NOTHING);
        expectedVisibility.add(grid[1][0]);
        grid[1][1] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        expectedVisibility.add(grid[1][1]);
        grid[1][2] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.WALL);
        expectedVisibility.add(grid[1][2]);
        grid[2][0] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.NOTHING);
        expectedVisibility.add(grid[2][0]);
        grid[2][1] = new Square(Border.NOTHING, Border.WALL, Border.NOTHING, Border.NOTHING);
        expectedVisibility.add(grid[2][1]);
        grid[2][2] = new Square(Border.NOTHING, Border.WALL, Border.NOTHING, Border.WALL);
        expectedVisibility.add(grid[2][2]);
        gamemap = new GameMap(grid, new ArrayList<>(), new ArrayList<>());
        visibility = gamemap.getVisibility(grid[2][2]);
    }

    @AfterEach
    void tearDown() {
        expectedVisibility = null;
        visibility = null;
    }

    @Test
    void getVisibility() {
        assertTrue(visibility.containsAll(expectedVisibility));
        assertTrue(expectedVisibility.containsAll(visibility));
    }
}