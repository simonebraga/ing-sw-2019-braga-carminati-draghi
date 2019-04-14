package it.polimi.ingsw;

/**
 * This class represents all exceptions related to a deck being empty.
 *
 * @author Draghi96
 */
public class EmptyDeckException extends Exception{

    /**
     * This method is used to create a new exception that is related to a deck being empty.
     */
    public EmptyDeckException() {
    }

    /**
     * This method is used to create a new exception that is related to a deck being empty that also
     * generates a message.
     *
     * @param message a string containing the error message.
     */
    public EmptyDeckException(String message) {
        super(message);
    }
}
