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
 * Tests the normal functioning of the method getVisibility.
 * In this case the Square has two doors.
 */
class TestGameMapGetVisibilityTwoDoors {
    private ArrayList<Square> expectedVisibility;
    private ArrayList<Square> visibility;

    @BeforeEach
    void setUp() {
        GameMap gamemap;
        Square[][] grid;
        grid = new Square[3][3];
        expectedVisibility = new ArrayList<>();
        grid[0][0] = new Square(Border.WALL, Border.WALL, Border.WALL, Border.NOTHING,0,0);
        grid[0][1] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.NOTHING,0,1);
        grid[0][2] = new Square(Border.WALL, Border.WALL, Border.NOTHING, Border.WALL,0,2);
        grid[1][0] = new Square(Border.WALL, Border.NOTHING, Border.WALL, Border.NOTHING,1,0);
        expectedVisibility.add(grid[1][0]);
        grid[1][1] = new Square(Border.WALL, Border.NOTHING, Border.NOTHING, Border.WALL,1,1);
        expectedVisibility.add(grid[1][1]);
        grid[1][2] = new Square(Border.WALL, Border.DOOR, Border.WALL, Border.WALL,1,2);
        expectedVisibility.add(grid[1][2]);
        grid[2][0] = new Square(Border.NOTHING, Border.WALL, Border.WALL, Border.NOTHING,2,0);
        expectedVisibility.add(grid[2][0]);
        grid[2][1] = new Square(Border.NOTHING, Border.WALL, Border.NOTHING, Border.DOOR,2,1);
        expectedVisibility.add(grid[2][1]);
        grid[2][2] = new Square(Border.DOOR, Border.WALL, Border.DOOR, Border.WALL,2,2);
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