package it.polimi.ingsw.controller;

import java.util.ArrayList;

/**
 * This class is a stream of strings. It allows simultaneous access from reader and writer
 * The writer can write strings without limitations
 * The reader can read one string per access, and if the stream is empty it waits for the writer to write at least one string before going on
 * @author simonebraga
 */
public class CustomStream {

    /**
     * This attribute is the ArrayList that contains the strings of the stream
     */
    private ArrayList<String> buffer = new ArrayList<>();

    /**
     * This method lets the users of the class write strings in the buffer
     * @param s the string to be written
     */
    public synchronized void putLine(String s) {
        buffer.add(s);
        notifyAll();
    }

    /**
     * This method lets the users of the class read the oldest string of the buffer
     * @return the oldest string of the buffer
     */
    public synchronized String getLine() {

        while (buffer.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return buffer.remove(0);
    }

    /**
     * This method lets the users of the class reset the buffer to clean the stream
     */
    public synchronized void resetBuffer() {
        buffer = new ArrayList<>();
        notifyAll();
    }
}
