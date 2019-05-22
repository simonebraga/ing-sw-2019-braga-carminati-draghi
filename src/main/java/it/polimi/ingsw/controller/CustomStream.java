package it.polimi.ingsw.controller;

import java.util.ArrayList;

/**
 * This class contains is a basic implementation of a stream, which lets multiple classes to use of a single InputStream.
 * More specifically, the listener class reads from the stream, and depending on the read string, pushes it in a new stream, available to other classes
 */
public class CustomStream {

    private ArrayList<String> buffer;

    public CustomStream() {
        buffer = new ArrayList<>();
    }

    /**
     * This method is used to put a new string in the buffer
     * @param s is the string to be put in the buffer
     */
    public synchronized void putLine(String s) {
        buffer.add(s);
        notifyAll();
    }

    /**
     * This method is used to get the strings in the buffer according to FIFO logic
     * @return the oldest string of the buffer
     */
    public synchronized String getLine() {

        while (buffer.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String line = buffer.get(0);
        buffer.remove(0);
        return line;
    }

    /**
     * This method is used to reset the buffer to an empty state
     */
    public synchronized void resetBuffer() {
        buffer = new ArrayList<>();
        notifyAll();
    }
}
