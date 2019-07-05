package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;

/**
 * This interface contains all the methods used by the client-side application for the interaction with the user
 */
public interface ViewInterface {

    /**
     * Should be used to notify to the view of the disconnection from the server
     */
    void logout();

    /**
     * Should be used to display important messages to the user
     * (I.E. "You won")
     * @param s should be the message to be displayed
     */
    void sendMessage(String s);

    /**
     * Should be used to notify minor messages to the user
     * (I.E. "Player disconnected")
     * @param s should be the message to be notified
     */
    void notifyEvent(String s);

    /**
     * Should allow the user to make a choice in a set of player
     * @param f the set to choose from
     * @return the index of the value chosen by the user
     */
    int choosePlayer(Figure[] f);

    /**
     * Should allow the user to make a choice in a set of weapons
     * @param w the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseWeapon(WeaponName[] w);

    /**
     * Should allow the user to make a choice in a set of strings
     * @param s the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseString(String[] s);

    /**
     * Should allow the user to make a choice in a set of directions
     * @param c the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseDirection(Character[] c);

    /**
     * Should allow the user to make a choice in a set of colors
     * @param c the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseColor(Color[] c);

    /**
     * Should allow the user to make a choice in a set of powerups
     * @param p the set to choose from
     * @return the index of the value chosen by the user
     */
    int choosePowerup(Powerup[] p);

    /**
     * Should allow the user to make a choice in a set of maps
     * @param m the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseMap(int[] m);

    /**
     * Should allow the user to make a choice in a set of game modes
     * @param c the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseMode(Character[] c);

    /**
     * Should allow the user to make a choice in a set of squares identified by coordinates
     * @param s the set to choose from
     * @return the index of the value chosen by the user
     */
    int chooseSquare(int[][] s);

    /**
     * Should allow the user to answer a simple boolean question
     * @param s the simple boolean question
     * @return boolean value representing the answer to the question
     */
    int booleanQuestion(String s);

    /**
     * Should allow the user to make a multiple choice in a set of powerups
     * @param p the set to choose from
     * @return the indexes of the values chosen by the user
     */
    int[] chooseMultiplePowerup(Powerup[] p);

    /**
     * Should allow the user to make a multiple choice in a set of weapons
     * @param w the set to choose from
     * @return the indexes of the values chosen by the user
     */
    int[] chooseMultipleWeapon(WeaponName[] w);

    /**
     * Should be used to notify the view that the model updated
     */
    void notifyModelUpdate();
}
