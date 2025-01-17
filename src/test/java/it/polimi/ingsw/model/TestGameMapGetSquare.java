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
 * Tests the functioning of the method getSquare: it returns the Square in the position represented
 * by the x and y coordinates. If the coordinates are in the bounds but the Square doesn't exists,
 * it must return null.
 */
class TestGameMapGetSquare {
    private GameMap gamemap;
    private Square[][] grid;
    private Square squareToGet;
    private final Integer XCOORD = 1;
    private final Integer YCOORD = 0;
    private final Integer XCOORDNULL = 2;
    private final Integer YCOORDNULL = 1;

    @BeforeEach
    void setUp() {
        grid = new Square[3][3];
        grid[0][0] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,0,0);
        grid[0][1] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,0,1);
        grid[0][2] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,0,2);
        grid[1][0] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,1,0);
        grid[1][1] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,1,1);
        grid[1][2] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,1,2);
        grid[2][2] = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,2,2);
        gamemap = new GameMap(grid, new ArrayList<SpawnSquare>(), new ArrayList<TileSquare>());
        squareToGet = grid[XCOORD][YCOORD];
    }

    @Test
    void getSquare() {
        assertSame(squareToGet, gamemap.getSquare(XCOORD,YCOORD));
        assertNull(gamemap.getSquare(XCOORDNULL, YCOORDNULL));
    }

    @AfterEach
    void tearDown() {
        gamemap = null;
        grid = null;
        squareToGet = null;
    }
}