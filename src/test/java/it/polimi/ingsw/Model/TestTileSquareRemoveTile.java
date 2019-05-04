package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.CardClasses.AmmoTile;
import it.polimi.ingsw.Model.EnumeratedClasses.Border;
import it.polimi.ingsw.Model.EnumeratedClasses.Color;
import it.polimi.ingsw.Model.MapClasses.TileSquare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method removeTile: it has to return the AmmoTile contained in the Square and the Square has to remain without a AmmoTile.
 */
class TestTileSquareRemoveTile {
    TileSquare square;
    AmmoTile tile;

    @BeforeEach
    void setUp() {
        tile = new AmmoTile(new ArrayList<Color>(), 0);
        square = new TileSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        square.addTile(tile);
    }

    @Test
    void removeTile() {
        assertSame(square.removeTile(), tile);
        assertNull(square.getTile());
    }

    @AfterEach
    void tearDown() {
        square = null;
        tile = null;
    }
}