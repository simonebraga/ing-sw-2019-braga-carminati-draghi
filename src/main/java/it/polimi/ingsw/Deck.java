package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represent a generic pair of lists of cards.
 * <p>These lists can be made up of Weapon cards, Powerup cards or AmmoTile cards.</p>
 *
 * @author Draghi96
 * @see Weapon
 * @see Powerup
 * @see AmmoTile
 */
public class Deck {

    /**
     * This attribute is the deck from which you draw new cards.
     */
    private ArrayList<Card> activeCards;

    /**
     * This attribute is the deck of discarded cards.
     * <p>When there are no more cards to be drawn this deck will be shuffled
     * and reused to generate a new list for activeCards.</p>
     */
    private ArrayList<Card> inactiveCards;

    /**
     * This constructor initializes the pair of decks.
     * <p>This constructor should be invoked only in a game setup phase.</p>
     *
     * @param activeCards a list of cards ready to be drawn.
     * @param inactiveCards a list of discarded cards. During the game setup it is initialized to null.
     */
    public Deck(ArrayList<Card> activeCards, ArrayList<Card> inactiveCards) {
        this.activeCards = activeCards;
        this.inactiveCards = inactiveCards;
    }

    /**
     * This method returns the arraylist of Cards of the deck from which all players draw new cards.
     *
     * @return an arraylist of Cards representing the cards deck.
     */
    public ArrayList<Card> getActiveCards() {
        return activeCards;
    }

    /**
     * This method returns the arraylist of Cards from the deck of discarded cards.
     *
     * @return an arraylist of Cards of the discarded cards deck.
     */
    public ArrayList<Card> getInactiveCards() {
        return inactiveCards;
    }

    /**
     * This method randomizes the activeCards list order.
     *
     * @throws EmptyDeckException if the activeCards list is empty.
     */
    public void shuffle() throws EmptyDeckException {
        if(activeCards.isEmpty()){
            throw new EmptyDeckException("Active cards list is empty!");
        }
        Collections.shuffle(activeCards);
    }

    /**
     * This method takes inactiveCards deck and reuses it to generate a new deck for activeCards,
     * then it clears the inactiveCards list.
     * <p>It should only be invoked when the activeCards list is empty and the inactiveCards list is not empty.</p>
     *
     * @throws EmptyDeckException if the inactiveCards list is empty.
     * @throws NotEmptyDeckException if the activeCards list is not empty.
     */
    public void reset() throws EmptyDeckException, NotEmptyDeckException {
        if(inactiveCards.isEmpty()){
            throw new EmptyDeckException("Inactive cards deck is empty!");
        }
        if(!activeCards.isEmpty()){
            throw new NotEmptyDeckException("There are still cards in the active deck!");
        }
        activeCards.addAll(inactiveCards);
        inactiveCards.clear();
    }

    /**
     * This recursive method allows to draw a new card from the activeCards list.
     *
     * @return The card on top of the activeCards deck.
     */
    public Card draw() throws EmptyDeckException, NotEmptyDeckException {

        if (activeCards==null){
            reset();
            return draw();
        }
        else{
           return activeCards.remove(0);
        }
    }

    /**
     * This method add one given card to the inactiveCards deck to be discarded.
     *
     * @param card Card to be discarded.
     */
    public void discard(Card card){
        inactiveCards.add(card);
    }
}
