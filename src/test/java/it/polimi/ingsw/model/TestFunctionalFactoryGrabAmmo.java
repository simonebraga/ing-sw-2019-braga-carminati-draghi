package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Deck;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;
import it.polimi.ingsw.model.mapclasses.GameMap;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.DoubleKillCounter;
import it.polimi.ingsw.model.playerclasses.KillshotTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.playerclasses.StartingPlayerMarker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the FunctionalEffect GrabAmmo either when the AmmoTile contain
 * a Powerup or not: the Player adds the Ammo to his AmmoPocket, the TileSquare has no more AmmoTiles
 * and the Player (if is needed) draws a PowerUp.
 */
class TestFunctionalFactoryGrabAmmo {

    Player player;
    GameTable table;
    TileSquare square;
    AmmoTile tile;
    Powerup powerup;
    Deck<Powerup> deck;
    ArrayList<Player> players;
    Square[][] squares;
    FunctionalEffect effect;
    ArrayList<Color> colors;

    @BeforeEach
    void setUp() {
        colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        tile = new AmmoTile(colors,1);

        squares = new Square[1][1];
        square = new TileSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,0,0);
        square.addTile(tile);
        squares[0][0] = square;

        player = new Player(Figure.BANSHEE, "Player1");
        players = new ArrayList<>();
        players.add(player);
        player.move(square);

        powerup = new Powerup(Color.BLUE, PowerupName.NEWTON);
        deck = new Deck<>(new ArrayList<>(), new ArrayList<>());
        deck.getActiveCards().add(powerup);
        table =new GameTable(new StartingPlayerMarker(player), new KillshotTrack(8, new ArrayList<>()),
                new DoubleKillCounter(0),
                new GameMap(squares, new ArrayList<>(), new ArrayList<>()),
                players, new Deck<Weapon>(new ArrayList<>(), new ArrayList<>()),
                deck, new Deck(new ArrayList<>(), new ArrayList<>()));
        effect = new FunctionalFactory().createGrabAmmo(player, table);
    }

    @AfterEach
    void tearDown() {
        player = null;
        table = null;
        square = null;
        powerup = null;
        deck = null;
        players = null;
        squares = null;
        effect = null;
        colors = null;
        tile = null;
    }

    @Test
    void createGrabAmmo() {
        effect.doAction();

        assertEquals(player.getAmmoPocket().getAmmo(Color.BLUE), 1);
        assertEquals(player.getAmmoPocket().getAmmo(Color.YELLOW), 1);
        assertEquals(player.getAmmoPocket().getAmmo(Color.RED), 1);
        assertEquals(player.getPowerupPocket().getPowerups().size(), 1);
        assertTrue(player.getPowerupPocket().getPowerups().contains(powerup));
        assertNull(square.getTile());

        colors = new ArrayList<>();
        colors.add(Color.BLUE);

        tile = new AmmoTile(colors, 0);
        square.addTile(tile);

        effect = new FunctionalFactory().createGrabAmmo(player, table);
        effect.doAction();

        assertEquals(player.getAmmoPocket().getAmmo(Color.BLUE), 2);
        assertEquals(player.getAmmoPocket().getAmmo(Color.YELLOW), 1);
        assertEquals(player.getAmmoPocket().getAmmo(Color.RED), 1);
        assertEquals(player.getPowerupPocket().getPowerups().size(), 1);
        assertTrue(player.getPowerupPocket().getPowerups().contains(powerup));
        assertNull(square.getTile());
        tearDown();
    }
}