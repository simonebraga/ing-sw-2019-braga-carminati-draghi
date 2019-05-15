package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            square.addDamage(player);
        }

    }

    @Test
    void createDamageSpawn() {

        effect = new FunctionalFactory().createDamageSpawn(player,square);

        effect.doAction();

        effect.doAction();

        assertEquals(8, square.getDamage().size());
    }
}