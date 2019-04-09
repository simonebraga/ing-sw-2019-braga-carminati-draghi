package it.polimi.ingsw;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies that all Movement class methods work correctly.
 *
 * @author Draghi96
 */
public class TestMove {

    /**
     * This attribute is a player for the test.
     */
    private Player player;

    /**
     * This attribute is a map for the test.
     */
    private GameMap map;

    /**
     * This attribute is an Arraylist of all spawn squares for the test map.
     */
    private ArrayList<Square> spawnSquares;

    /**
     * This attribute is an Arraylist of all tile squares for the test map.
     */
    private ArrayList<Square> tileSquares;

    /**
     * This attribute is the map squares grid for the test.
     */
    private Square[][] grid;

    /**
     * This attribute is a square destination for the test.
     */
    private Square destination;

    /**
     * This is the object to be tested.
     */
    private Move movement1;

    /**
     * This method sets up al objects needed for this test suit.
     */
    @BeforeEach
    void setUp(){

        grid = new Square[50][50];
        player=new Player(Figure.DOZER,"user");

        //initializing a 20x30 grid map
        tileSquares = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 30; j++) {
                grid[j][i] = new TileSquare(Border.WALL,Border.WALL,Border.WALL,Border.WALL);
                tileSquares.add(grid[j][i]);
            }
        }
        spawnSquares = new ArrayList<>();
        spawnSquares.clear();

        map= new GameMap(grid,spawnSquares,tileSquares);

        player.move(map.getSquare(0,0)); //player set to (0,0) square

        //a random destination
        destination = map.getSquare(4,7);
    }

    /**
     * This test verifies that move() actually changes a player position.
     */
    @Test
    void moveChangesPlayerPosition(){
        //effect usage
        movement1 = new Move(player,destination);

        assertNotEquals(destination, player.getPosition());

        movement1.doAction();

        assertEquals(destination,player.getPosition());
        tearDown();
    }

    /**
     * This method is used to free al objects used for this test suit.
     */
    @After
    void tearDown(){
        player=null;
        map=null;
        spawnSquares=null;
        tileSquares=null;
        grid=null;
        destination=null;
        movement1=null;
    }
}
