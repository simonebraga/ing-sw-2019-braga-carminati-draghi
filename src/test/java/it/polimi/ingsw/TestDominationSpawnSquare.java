package it.polimi.ingsw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        TestDominationSpawnSquareAddDamage8thDamage.class,
        TestDominationSpawnSquareAddDamageAfter8Damage.class,
        TestDominationSpawnSquareAddDamageBefore8Damage.class
})

class TestDominationSpawnSquare {
}