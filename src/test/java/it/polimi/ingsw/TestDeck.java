package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies that all methods of Deck class work correctly.
 *
 * @author Draghi96
 */
class TestDeck {

    /**
     * This test verifies that shuffle() changes at least one card position inside the list.
     * <p>The list of cards cannot be empty.</p>
     */
    @Test
    void shuffleChangesOrder() throws EmptyDeckException {
        //creating deck
        ArrayList<Color> price = new ArrayList<>();
        price.clear();

        ArrayList<Card> inactiveList1 = new ArrayList<>();
        inactiveList1.add(new Powerup(Color.RED,PowerupName.TELEPORTER));
        inactiveList1.add(new Powerup(Color.YELLOW,PowerupName.TELEPORTER));
        inactiveList1.add(new Powerup(Color.BLUE,PowerupName.TARGETINGSCOPE));
        inactiveList1.add(new Powerup(Color.RED,PowerupName.NEWTON));
        inactiveList1.add(new Powerup(Color.RED,PowerupName.TAGBACKGRENADE));
        ArrayList<Card> inactiveList2 = new ArrayList<>(inactiveList1);

        ArrayList<Card> activeList1 = new ArrayList<>();
        activeList1.add(new Weapon(price,WeaponName.LOCKRIFLE,true));
        activeList1.add(new Weapon(price,WeaponName.MACHINEGUN,true));
        activeList1.add(new Weapon(price,WeaponName.THOR,true));
        activeList1.add(new Weapon(price,WeaponName.PLASMAGUN,true));
        activeList1.add(new Weapon(price,WeaponName.WHISPER,true));
        activeList1.add(new Weapon(price,WeaponName.ELECTROSCYTHE,true));
        activeList1.add(new Weapon(price,WeaponName.TRACTORBEAM,true));
        activeList1.add(new Weapon(price,WeaponName.VORTEXCANNON,true));
        activeList1.add(new Weapon(price,WeaponName.FURNACE,true));
        activeList1.add(new Weapon(price,WeaponName.HEATSEEKER,true));
        activeList1.add(new Weapon(price,WeaponName.HELLION,true));
        activeList1.add(new Weapon(price,WeaponName.FLAMETHROWER,true));
        activeList1.add(new Weapon(price,WeaponName.GRENADELAUNCHER,true));
        activeList1.add(new Weapon(price,WeaponName.ROCKETLAUNCHER,true));
        activeList1.add(new Weapon(price,WeaponName.RAILGUN,true));
        activeList1.add(new Weapon(price,WeaponName.CYBERBLADE,true));
        activeList1.add(new Weapon(price,WeaponName.ZX2,true));
        activeList1.add(new Weapon(price,WeaponName.SHOTGUN,true));
        activeList1.add(new Weapon(price,WeaponName.POWERGLOVE,true));
        activeList1.add(new Weapon(price,WeaponName.SHOCKWAVE,true));
        activeList1.add(new Weapon(price,WeaponName.SLEDGEHAMMER,true));
        ArrayList<Card> activeList2 = new ArrayList<>(activeList1);

        Deck deck1 = new Deck(activeList1,inactiveList1);
        Deck deck2 = new Deck(activeList2,inactiveList2);

        deck1.shuffle(); //method usage
        
        assertEquals(deck2.getActiveCards().size(),deck1.getActiveCards().size());
        assertEquals(deck2.getInactiveCards().size(),deck1.getInactiveCards().size());
        assertNotEquals(deck1.getActiveCards(), deck2.getActiveCards());
    }

    /**
     * This test verifies if reset() add two decks together.
     * <p>First list must be empty.<br/>
     * Second list cannot be empty.</p>
     */
    @Test
    void resetAddsTwoDecks() throws EmptyDeckException, NotEmptyDeckException {
        //creating 2 list of cards
        ArrayList<Card> inactivePowerups = new ArrayList<>();
        ArrayList<Card> activePowerups = new ArrayList<>();
        activePowerups.clear();
        inactivePowerups.add(new Powerup(Color.RED, PowerupName.NEWTON));
        inactivePowerups.add(new Powerup(Color.YELLOW, PowerupName.NEWTON));
        inactivePowerups.add(new Powerup(Color.BLUE,PowerupName.NEWTON));

        //creating deck
        Deck deck = new Deck(activePowerups,inactivePowerups);

        int oldDimActive = deck.getActiveCards().size();
        int oldDimInactive = deck.getInactiveCards().size();

        deck.reset();   //method usage

        assertEquals(deck.getActiveCards().size(),oldDimInactive);
        assertTrue(deck.getInactiveCards().isEmpty());
        assertEquals(deck.getActiveCards().size(),oldDimActive+oldDimInactive);
    }

    /**
     * This test verifies if draw() returns a card from deck.
     */
    @Test
    void drawReturnsCardAndDeckSizeIsDecreasing() throws EmptyDeckException, NotEmptyDeckException {
        //creating 5-powerup deck
        ArrayList<Card> activePowerups = new ArrayList<>();
        ArrayList<Card> inactivePowerups = new ArrayList<>();
        inactivePowerups.clear();
        activePowerups.add(new Powerup(Color.RED, PowerupName.NEWTON));
        activePowerups.add(new Powerup(Color.BLUE, PowerupName.NEWTON));
        activePowerups.add(new Powerup(Color.YELLOW, PowerupName.NEWTON));
        activePowerups.add(new Powerup(Color.YELLOW, PowerupName.TAGBACKGRENADE));
        activePowerups.add(new Powerup(Color.YELLOW, PowerupName.TELEPORTER));

        Deck deck = new Deck(activePowerups,inactivePowerups);

        int oldActiveDim=activePowerups.size();
        int oldInactiveDim=inactivePowerups.size();

        Card output = deck.draw();  //method usage

        assertNotNull(output);
        assertEquals(oldActiveDim-1,deck.getActiveCards().size());
        assertEquals(oldInactiveDim,deck.getInactiveCards().size());
    }

    /**
     *This test verifies if discard() adds a card passed as parameter into the inactiveCards list of a deck.
     */
    @Test
    void discardAddsCardToInactiveList() {
        //creating lists
        ArrayList<Card> activePowerups = new ArrayList<>();
        ArrayList<Card> inactivePowerups = new ArrayList<>();

        activePowerups.add(new Powerup(Color.RED,PowerupName.NEWTON));
        inactivePowerups.add(new Powerup(Color.BLUE,PowerupName.TELEPORTER));

        Deck deck = new Deck(activePowerups,inactivePowerups);

        int oldActiveDim = activePowerups.size();
        int oldInactiveDim = inactivePowerups.size();

        deck.discard(new Powerup(Color.YELLOW,PowerupName.TARGETINGSCOPE)); //method usage

        assertEquals(oldActiveDim, deck.getActiveCards().size());
        assertEquals(oldInactiveDim+1,deck.getInactiveCards().size());
    }
}