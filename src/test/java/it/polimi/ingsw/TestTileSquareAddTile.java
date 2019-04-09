package it.polimi.ingsw;

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
        tile = new AmmoTile(new ArrayList<>(), PowerupName.NEWTON);
    }

    @Test
    void addTile() {
        square.addTile(tile);
        assertSame(square.getTile(), tile);
    }
}