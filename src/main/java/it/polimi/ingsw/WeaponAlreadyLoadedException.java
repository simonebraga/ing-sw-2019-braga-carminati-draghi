package it.polimi.ingsw;

/**
 * This class represents all exceptions related to a weapon being already loaded.
 *
 * @author Draghi96
 */
public class WeaponAlreadyLoadedException extends Exception {

    /**
     * This method is used to create a new exception related to the weapon being already loaded.
     */
    public WeaponAlreadyLoadedException() {
    }

    /**
     * This method is used to create a new exception related to the weapon being already loaded
     * that also includes a message.
     */
    public WeaponAlreadyLoadedException(String message) {
        super(message);
    }
}
