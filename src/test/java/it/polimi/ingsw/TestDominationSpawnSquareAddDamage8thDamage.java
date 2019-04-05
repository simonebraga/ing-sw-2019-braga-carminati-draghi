package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the method addDamage throws an Exception when it gets the 8th damage and also adds the damage correctly.
 */
class TestDominationSpawnSquareAddDamage8thDamage {

    DominationSpawnSquare square;
    Player shooter;
    Player anotherShooter;
    Player anotherShooter2;
    ArrayList<Player> damages;

    @BeforeEach
    void setUp() {
        square = new DominationSpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, Color.BLUE);
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
        damages.add(anotherShooter2);
        square.setDamage((ArrayList<Player>) damages.clone());
        damages.add(shooter);
    }

    @Test
    void addDamage() {
        try {
            square.addDamage(shooter);
            fail();
        } catch (KilledSpawnSquareException e) {
            while (!damages.isEmpty()) {
                assertTrue(square.getDamage().remove(damages.get(0)));
                damages.remove(0);
            }
            assertTrue(square.getDamage().isEmpty());
        }
    }
}