package it.polimi.ingsw.network;

/**
 * This exception is thrown when any class tries to communicate with a non connected user
 */
public class UnavailableUserException extends Exception {

    public UnavailableUserException() {
    }
}
