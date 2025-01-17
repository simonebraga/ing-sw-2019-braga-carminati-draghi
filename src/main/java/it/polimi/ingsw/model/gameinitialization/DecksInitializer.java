package it.polimi.ingsw.model.gameinitialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Deck;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class creates objects which duty is to load all cards from file and arrange them in decks.
 *
 * @author Draghi96
 */
public class DecksInitializer {

    /**
     * This final attribute represents the file name prefix from which the decks will be fetched.
     */
    private static final String DECK_FILE_PREFIX = "decks/";

    /**
     * This method is the object constructor.
     */
    public DecksInitializer() {}

    /**
     * This method load from JSON files the selected deck and initializes it.
     *
     * @param type is a String that represents which type of card the deck will be composed of.
     * @return an initialized deck.
     */
    public Deck initDeck(String type) {

        try {
            InputStream file = DecksInitializer.class.getClassLoader().getResourceAsStream(DECK_FILE_PREFIX + type + ".json");
            ObjectMapper mapper = new ObjectMapper();
            Deck deck;
            switch (type) {
                case "weapons": {
                    Weapon[] array = mapper.readValue(file, Weapon[].class);
                    ArrayList<Weapon> activeList = new ArrayList<>(Arrays.asList(array));
                    deck = new Deck<>(activeList, new ArrayList<Weapon>());
                    break;
                }
                case "ammotiles": {
                    AmmoTile[] array = mapper.readValue(file, AmmoTile[].class);
                    ArrayList<AmmoTile> activeList = new ArrayList<>(Arrays.asList(array));
                    deck = new Deck<>(activeList, new ArrayList<AmmoTile>());
                    break;
                }
                case "powerups": {        //powerups
                    Powerup[] array = mapper.readValue(file, Powerup[].class);
                    ArrayList<Powerup> activeList = new ArrayList<>(Arrays.asList(array));
                    deck = new Deck<>(activeList, new ArrayList<Powerup>());
                    break;
                }
                default:
                    deck = null;
                    break;
            }
            if (deck!=null) deck.shuffle();
            if (file!=null) file.close();
            return deck;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
