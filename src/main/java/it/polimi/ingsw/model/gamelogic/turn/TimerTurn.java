package it.polimi.ingsw.model.gamelogic.turn;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Support class that represents the timer for the turn.
 * <p>If the timer stops before that the player finishes his turn, the player gets disconnected.</p>
 */
public class TimerTurn extends Thread{

    private AtomicBoolean stop;

    private int time;

    private Server server;

    private Player playerOfTurn;

    public TimerTurn(Server server, Integer time, Player playerOfTurn) {
        this.server = server;
        this.time = time;
        this.playerOfTurn = playerOfTurn;
        this.stop = new AtomicBoolean(false);
    }

    public void setStop(boolean stop) {
        this.stop.set(stop);
    }

    @Override
    public void run() {
        while (!stop.get() && time>0){
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                time-=500;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!stop.get()){
            server.forceLogout(playerOfTurn);
        }
    }
}