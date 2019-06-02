package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the normal functioning of the method addDamage after the SpawnPoint has been killed: the Player mark must be added to the list of Players marks, the other Players mark must not be modified.
 */
class TestDominationSpawnSquareAddDamageAfter8Damage {
    DominationSpawnSquare square;
    Player shooter;
    Player anotherShooter;
    Player anotherShooter2;
    ArrayList<Player> damages;

    @BeforeEach
    void setUp() {
        square = new DominationSpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, 0,0,Color.BLUE);
        shooter = new Player(Figure.DESTRUCTOR, "nick1");
        anotherShooter = new Player(Figure.DOZER, "nick2");
        anotherShooter2 = new Player(Figure.VIOLET, "nick3");
        damages = new ArrayList<Player>();
        damages.add(anotherShooter);
        damages.add(anotherShooter);
        damages.add(anotherShooter);
        damages.add(anotherShooter2);
        damages.add(shooter);
        damages.add(shooter);
        damages.add(anotherShooter);
        damages.add(anotherShooter2);
        damages.add(shooter);
        square.setDamage((ArrayList<Player>) damages.clone());
        damages.add(shooter);
    }

    @Test
    void addDamage() {

        square.addDamage(shooter);
        while(!damages.isEmpty()){
            assertTrue(square.getDamage().remove(damages.get(0)));
            damages.remove(0);
        }
        assertTrue(square.getDamage().isEmpty());
    }

    @AfterEach
    void tearDown() {
        square = null;
        shooter = null;
        anotherShooter = null;
        anotherShooter2 = null;
        damages = null;
    }
}