package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is the test suite for createDamagePlayer in FunctionalFactory class
 *
 * @author simonebraga
 */
class TestFunctionalFactoryCreateDamagePlayer {

    Player marker;
    Player killer;
    Player player;
    Player playerAboutToDie;
    FunctionalEffect effect;

    @BeforeEach
    void setUp() {

        marker = new Player(Figure.VIOLET,"nickname0");
        killer = new Player(Figure.DOZER,"nickname1");
        player = new Player(Figure.BANSHEE,"nickname2");
        playerAboutToDie = new Player(Figure.DESTRUCTOR,"nickname3");

        playerAboutToDie.getDamageTrack().addDamage(killer,10);


    }

    /**
     * This test case checks if the addDamage FunctionalEffects works correctly
     */
    @Test
    void createDamagePlayer() {

        effect = new FunctionalFactory().createDamagePlayer(killer,player,1,0);

        effect.doAction();

        assertEquals(1,player.getDamageTrack().getDamage().size());

        effect = new FunctionalFactory().createDamagePlayer(killer,playerAboutToDie,1,0);

        effect.doAction();

        assertEquals(11,playerAboutToDie.getDamageTrack().getDamage().size());

        effect.doAction();

        assertEquals(12,playerAboutToDie.getDamageTrack().getDamage().size());

    }

    /**
     * This test case checks if the damage to a player with marks is handled correctly
     */
    @Test
    void createDamagePlayerWithMarks() {

        playerAboutToDie.getMarkTrack().addMarks(marker,2);
        player.getMarkTrack().addMarks(marker,2);

        effect = new FunctionalFactory().createDamagePlayer(marker,player,1,0);

        effect.doAction();

        assertEquals(3 , player.getDamageTrack().getDamage().size());

        effect = new FunctionalFactory().createDamagePlayer(marker,playerAboutToDie,0,0);

        effect.doAction();

        assertEquals(10 , playerAboutToDie.getDamageTrack().getDamage().size());

        effect = new FunctionalFactory().createDamagePlayer(marker,playerAboutToDie,1,0);

        effect.doAction();

        assertEquals(12 , playerAboutToDie.getDamageTrack().getDamage().size());

    }

}