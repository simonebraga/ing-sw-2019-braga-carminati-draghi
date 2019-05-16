package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Deck;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies that all DecksInitializer class methods work correctly.
 *
 * @author Draghi96
 */
public class TestDeckInitializer {

    /**
     * This attribute is a DecksInitializer object that will be used for testing.
     */
    private DecksInitializer decksInitializer;

    /**
     * This method initializes all objects that will be used for testing.
     */
    @BeforeEach
    void setUp() {
        decksInitializer = new DecksInitializer();
    }

    /**
     * This test verifies that initDeck() returns a deck which size
     * is the same amount of cards loaded from JSON.
     */
    @Test
    void initDeckReturnsTheSameAmountOfCardsStoredInJSON() {
        Deck<AmmoTile> ammoTileDeck = decksInitializer.initDeck("ammotiles");
        assertEquals(36,ammoTileDeck.getActiveCards().size());
        assertEquals(0,ammoTileDeck.getInactiveCards().size());

        Deck<Powerup> powerupDeck = decksInitializer.initDeck("powerups");
        assertEquals(24,powerupDeck.getActiveCards().size());
        assertEquals(0,powerupDeck.getInactiveCards().size());

        Deck<Weapon> weaponDeck = decksInitializer.initDeck("weapons");
        assertEquals(21,weaponDeck.getActiveCards().size());
        assertEquals(0,weaponDeck.getInactiveCards().size());
    }

    /**
     * This method frees all objects used in this test suit.
     */
    @AfterEach
    void tearDown() {
        decksInitializer=null;
    }
}
