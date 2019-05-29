package it.polimi.ingsw.controller;

import java.util.ArrayList;

public class CustomStream {

    private ArrayList<String> buffer = new ArrayList<>();

    public synchronized void putLine(String s) {
        buffer.add(s);
        notifyAll();
    }

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

    public synchronized void resetBuffer() {
        buffer = new ArrayList<>();
        notifyAll();
    }
}
