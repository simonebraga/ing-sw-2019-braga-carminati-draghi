package it.polimi.ingsw;

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
        tile = new AmmoTile(new ArrayList<Color>(), PowerupName.NEWTON);
        square = new TileSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
        square.addTile(tile);
    }

    @Test
    void removeTile() {
        assertSame(square.removeTile(), tile);
        assertNull(square.getTile());
    }
}