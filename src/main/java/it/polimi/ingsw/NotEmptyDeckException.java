package it.polimi.ingsw;

/**
 * This class represents all exceptions related to a deck not being empty.
 *
 * @author Draghi96
 */
public class NotEmptyDeckException extends Exception {

    /**
     * This method is used to create a new exception that is related to a deck not being empty.
     */
    public NotEmptyDeckException() {
    }

    /**
     * This method is used to create a new exception that is related to a deck not being empty
     * and also generates a message.
     */
    public NotEmptyDeckException(String message) {
        super(message);
    }
}
