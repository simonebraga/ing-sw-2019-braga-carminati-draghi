package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method addTile: it has to put correctly the AmmoTile in the Square.
 */
class TestTileSquareAddTile {
    TileSquare square;
    AmmoTile tile;

    @BeforeEach
    void setUp() {
        square = new TileSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        tile = new AmmoTile(new ArrayList<>(), 0);
    }

    @Test
    void addTile() {
        square.addTile(tile);
        assertSame(square.getTile(), tile);
    }

    @AfterEach
    void tearDown() {
        square = null;
        tile = null;
    }
}