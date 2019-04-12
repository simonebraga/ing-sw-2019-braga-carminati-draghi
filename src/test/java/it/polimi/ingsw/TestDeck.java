package it.polimi.ingsw;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
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
     * This attribute is the list of cards that can be drawn that composes half of a deck.
     */
    private ArrayList<Card> activeList;

    /**
     * This attribute is the list of cards that cannot be drawn that composes half of a deck.
     */
    private ArrayList<Card> inactiveList;

    /**
     * This is a deck used for all tests.
     */
    private Deck<Weapon> deck1;

    /**
     * This is a Color array list that represents a weapon price.
     */
    private ArrayList<Color> price;

    /**
     * This method sets up all required object to test all methods in this suit.
     */
    @BeforeEach
    void setUp(){

        //creating a list for activeCards
        price = new ArrayList<>();
        price.clear();
        activeList = new ArrayList<>();
        activeList.add(new Weapon(price,WeaponName.LOCKRIFLE,true));
        activeList.add(new Weapon(price,WeaponName.MACHINEGUN,true));
        activeList.add(new Weapon(price,WeaponName.THOR,true));
        activeList.add(new Weapon(price,WeaponName.PLASMAGUN,true));
        activeList.add(new Weapon(price,WeaponName.WHISPER,true));
        activeList.add(new Weapon(price,WeaponName.ELECTROSCYTHE,true));
        activeList.add(new Weapon(price,WeaponName.TRACTORBEAM,true));
        activeList.add(new Weapon(price,WeaponName.VORTEXCANNON,true));
        activeList.add(new Weapon(price,WeaponName.FURNACE,true));
        activeList.add(new Weapon(price,WeaponName.HEATSEEKER,true));
        activeList.add(new Weapon(price,WeaponName.HELLION,true));
        activeList.add(new Weapon(price,WeaponName.FLAMETHROWER,true));
        activeList.add(new Weapon(price,WeaponName.GRENADELAUNCHER,true));
        activeList.add(new Weapon(price,WeaponName.ROCKETLAUNCHER,true));
        activeList.add(new Weapon(price,WeaponName.RAILGUN,true));
        activeList.add(new Weapon(price,WeaponName.CYBERBLADE,true));

        //creating a list for inactiveCards
        inactiveList = new ArrayList<>();
        inactiveList.add(new Weapon(price,WeaponName.ZX2,true));
        inactiveList.add(new Weapon(price,WeaponName.SHOTGUN,true));
        inactiveList.add(new Weapon(price,WeaponName.POWERGLOVE,true));
        inactiveList.add(new Weapon(price,WeaponName.SHOCKWAVE,true));
        inactiveList.add(new Weapon(price,WeaponName.SLEDGEHAMMER,true));

        //creating deck
        deck1= new Deck(activeList,inactiveList);
    }

    /**
     * This test verifies that shuffle() changes at least one card position inside the list.
     */
    @Test
    void shuffleChangesOrder() {

        //creating second deck
        ArrayList<Card> inactiveList2 = new ArrayList<>(inactiveList);
        ArrayList<Card> activeList2 = new ArrayList<>(activeList);

        Deck deck2 = new Deck(activeList2,inactiveList2);

        deck1.shuffle(); //method usage
        
        assertEquals(deck2.getActiveCards().size(),deck1.getActiveCards().size());
        assertEquals(deck2.getInactiveCards().size(),deck1.getInactiveCards().size());
        assertNotEquals(deck1.getActiveCards(), deck2.getActiveCards());
        tearDown();
    }

    /**
     * This test verifies if reset() add two decks together.
     * <p>activeCards list must be empty.<p>
     */
    @Test
    void resetAddsTwoDecks(){

        int oldDimActive = deck1.getActiveCards().size();
        int oldDimInactive = deck1.getInactiveCards().size();

        deck1.reset();   //method usage

        assertTrue(deck1.getInactiveCards().isEmpty());
        assertEquals(oldDimActive + oldDimInactive,deck1.getActiveCards().size());
        tearDown();
    }

    /**
     * This test verifies if draw() returns a card from deck.
     */
    @Test
    void drawReturnsCardAndDeckSizeIsDecreasing() {

        int oldActiveDim=activeList.size();
        int oldInactiveDim=inactiveList.size();

        Card output = null;

        try{
            output = deck1.draw();  //method usage
        } catch (EmptyDeckException e){
            fail();
        }

        assertNotNull(output);
        assertEquals(oldActiveDim-1,deck1.getActiveCards().size());
        assertEquals(oldInactiveDim,deck1.getInactiveCards().size());
        tearDown();
    }

    /**
     * This test verifies if draw() handles EmptyDeckException correctly.
     */
    @Test
    void drawHandlesExceptionCorrectly(){
        deck1.getActiveCards().clear(); //empty active cards list

        Card output = null;
        try{
            output=deck1.draw();
        }
        catch (EmptyDeckException e){
            assertTrue(true);
        }
    }

    /**
     *This test verifies if discard() adds a card passed as parameter into the inactiveCards
     * list of a deck.
     */
    @Test
    void discardAddsCardToInactiveList() {

        int oldActiveDim = activeList.size();
        int oldInactiveDim = inactiveList.size();

        deck1.discard(new Weapon(price,WeaponName.ZX2,true)); //method usage

        assertEquals(oldActiveDim, deck1.getActiveCards().size());
        assertEquals(oldInactiveDim + 1,deck1.getInactiveCards().size());

        deck1.discard(new Weapon(price,WeaponName.SLEDGEHAMMER,false)); //method usage

        assertEquals(oldActiveDim, deck1.getActiveCards().size());
        assertEquals(oldInactiveDim + 2,deck1.getInactiveCards().size());
        tearDown();
    }

    /**
     * This method frees all object created for this test suit.
     */
    @After
    void tearDown(){
        activeList=null;
        inactiveList=null;
        deck1=null;
        price=null;
    }
}