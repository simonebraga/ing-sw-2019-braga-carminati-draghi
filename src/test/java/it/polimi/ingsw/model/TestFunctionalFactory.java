package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * This test suit verifies that createPay() and createMove() in FunctionalFactory class work correctly.
 *
 * @author Draghi96
 */
public class TestFunctionalFactory {

    /**
     * This is a player created for testing.
     */
    private Player player;

    /**
     * This is a player ammo inventory created for testing.
     */
    private ArrayList<Color> playerAmmo;

    /**
     * This is an ammo price a player is required to pay in these tests.
     */
    private ArrayList<Color> price;

    /**
     * This is a destination square designated for testing.
     */
    private Square destination;

    /**
     * This is a starting position square designated for testing.
     */
    private Square start;

    //map objects
    /**
     * This is a game map created for testing.
     */
    private GameMap gameMap;

    /**
     * This is a map grid for the game map created for testing.
     */
    private Square[][] grid;

    /**
     * This is a list of spawn squares created to fill the map grid.
     */
    private ArrayList<SpawnSquare> spawnSquares;

    /**
     * This is a list of tile squares created to fill the map grid.
     */
    private ArrayList<TileSquare> tileSquares;

    //effects
    /**
     * This FunctionalEffect object will be created with the FunctionalFactory method createMove().
     * It tests the movement action.
     */
    private FunctionalEffect move;

    /**
     * This FunctionalEffect object will be created with the FunctionalFactory method createPay().
     * It tests the payment action.
     */
    private FunctionalEffect pay;

    /**
     * This method creates all objects needed for each test.
     */
    @BeforeEach
    void setUp(){
        //player starts with RED,RED,YELLOW
        playerAmmo = new ArrayList<>();
        playerAmmo.add(Color.RED);
        playerAmmo.add(Color.RED);
        playerAmmo.add(Color.YELLOW);
        player = new Player(Figure.DOZER,"User");
        player.getAmmoPocket().addAmmo(playerAmmo);

        //price is RED,YELLOW
        price = new ArrayList<>();
        price.add(Color.RED);
        price.add(Color.YELLOW);

        //setting game map (all TileSquares, no SpawnSquares)
        grid = new Square[4][3];
        spawnSquares = new ArrayList<>();
        spawnSquares.clear();
        tileSquares = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            tileSquares.add(new TileSquare(Border.WALL,Border.WALL,Border.WALL,Border.WALL,i,0));
        }
        int k=0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j]=tileSquares.get(k);
                k++;
            }
        }
        gameMap = new GameMap(grid,spawnSquares,tileSquares);

        //picking destination
        destination=gameMap.getSquare(1,2);

        //choosing starting square
        start=gameMap.getSquare(0,0);

        //setting player on starting square
        player.move(start);
        start.addPlayer(player);

        //creating effects
        move = new FunctionalFactory().createMove(player,destination);
        pay = new FunctionalFactory().createPay(player,price);
    }

    /**
     * This test verifies that a move action changes player position correctly
     * and updates the two squares player lists.
     */
    @Test
    void moveChangesPlayerPositionAndUpdatesSquaresPlayerLists(){
        assertEquals(player.getPosition(),gameMap.getSquare(0,0));
        assertEquals(start,player.getPosition());
        assertNotEquals(destination,player.getPosition());

        int oldStartSquarePlayerNumber=player.getPosition().getPlayers().size();
        int oldDestinationSquarePlayerNumber=destination.getPlayers().size();


        move.doAction();

        assertEquals(destination,player.getPosition());
        assertEquals(oldStartSquarePlayerNumber-1,start.getPlayers().size());
        assertEquals(oldDestinationSquarePlayerNumber+1,destination.getPlayers().size());
        assertNotEquals(start,player.getPosition());
    }

    /**
     * This test verifies that a payment action removes the correct amount of ammo boxes
     * from a player inventory.
     */
    @Test
    void payRemovesAmmoFromPlayerPockets(){
        assertEquals(2, player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(1, player.getAmmoPocket().getAmmo(Color.YELLOW));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.BLUE));


        pay.doAction();

        assertEquals(1,player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.YELLOW));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.BLUE));

        ArrayList<Color> price1 = new ArrayList<>();
        price1.add(Color.RED);
        FunctionalEffect pay1 = new FunctionalFactory().createPay(player,price1);

        pay1.doAction();

        assertEquals(0,player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.YELLOW));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.BLUE));
    }

    /**
     * This method frees all objects used in this test suit.
     */
    @AfterEach
    void tearDown(){
        player=null;
        playerAmmo=null;
        price=null;
        destination=null;
        start=null;
        gameMap=null;
        grid=null;
        tileSquares=null;
        spawnSquares=null;
    }
}
