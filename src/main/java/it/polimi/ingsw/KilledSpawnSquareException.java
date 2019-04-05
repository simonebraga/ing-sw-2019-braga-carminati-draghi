package it.polimi.ingsw;

/**
 * Represents the Exception thrown when a SpawnSquare gets 8 damages (in domination mode).
 */
public class KilledSpawnSquareException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public KilledSpawnSquareException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public KilledSpawnSquareException(String message) {
        super(message);
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
