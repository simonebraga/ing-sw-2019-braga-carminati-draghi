package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.EffectClasses.FunctionalEffect;
import it.polimi.ingsw.Model.EffectClasses.FunctionalFactory;
import it.polimi.ingsw.Model.EnumeratedClasses.Color;
import it.polimi.ingsw.Model.EnumeratedClasses.Figure;
import it.polimi.ingsw.Model.ExceptionClasses.FullPocketException;
import it.polimi.ingsw.Model.ExceptionClasses.KilledPlayerException;
import it.polimi.ingsw.Model.ExceptionClasses.KilledSpawnSquareException;
import it.polimi.ingsw.Model.ExceptionClasses.OverKilledPlayerException;
import it.polimi.ingsw.Model.MapClasses.DominationSpawnSquare;
import it.polimi.ingsw.Model.PlayerClasses.Player;
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

        square = new DominationSpawnSquare(null,null,null,null, Color.YELLOW);
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