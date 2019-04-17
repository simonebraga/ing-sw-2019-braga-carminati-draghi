package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for createDamageSpawn in FunctionalFactory class
 *
 * @author simonebraga
 */
class TestFunctionalFactoryCreateDamageSpawn {

    DominationSpawnSquare square;
    Player player = new Player(Figure.DOZER,"nickname");
    FunctionalEffect effect;

    @BeforeEach
    void setUp() {

        square = new DominationSpawnSquare(null,null,null,null,Color.YELLOW);
        for (int i = 0; i < 6; i++) {
            try {
                square.addDamage(player);
            } catch (KilledSpawnSquareException e) {
                fail();
            }
        }

    }

    @Test
    void createDamageSpawn() {

        effect = new FunctionalFactory().createDamageSpawn(player,square);

        try {
            effect.doAction();
        } catch (FullPocketException e) {
            fail();
        } catch (KilledPlayerException e) {
            fail();
        } catch (OverKilledPlayerException e) {
            fail();
        } catch (KilledSpawnSquareException e) {
            fail();
        }

        try {
            effect.doAction();
        } catch (FullPocketException e) {
            fail();
        } catch (KilledPlayerException e) {
            fail();
        } catch (OverKilledPlayerException e) {
            fail();
        } catch (KilledSpawnSquareException e) {
            assertTrue(true);
        }
    }
}