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
            File file = new File("src/main/resources/"+type+".json");
            ObjectMapper mapper = new ObjectMapper();
            Deck deck;
            if (type.equals("weapons")) {
                Weapon[] array = mapper.readValue(file,Weapon[].class);
                ArrayList<Weapon> activeList = new ArrayList<>(Arrays.asList(array));
                deck = new Deck<>(activeList, new ArrayList<Weapon>());
            } else if (type.equals("ammotiles")) {
                AmmoTile[] array = mapper.readValue(file,AmmoTile[].class);
                ArrayList<AmmoTile> activeList = new ArrayList<>(Arrays.asList(array));
                deck = new Deck<>(activeList, new ArrayList<AmmoTile>());
            } else {        //powerups
                Powerup[] array = mapper.readValue(file,Powerup[].class);
                ArrayList<Powerup> activeList = new ArrayList<>(Arrays.asList(array));
                deck = new Deck<>(activeList, new ArrayList<Powerup>());
            }
            deck.shuffle();
            return deck;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
