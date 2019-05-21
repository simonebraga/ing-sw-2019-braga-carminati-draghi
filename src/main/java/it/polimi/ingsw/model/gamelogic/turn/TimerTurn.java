package it.polimi.ingsw.model.gamelogic.turn;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Support class that represents the timer for the turn.
 * <p>If the timer stops before that the player finishes his turn, the player gets disconnected.</p>
 */
public class TimerTurn extends Thread{

    private AtomicBoolean stop;

    private int time;

    private Controller controller;

    private Player playerOfTurn;

    public TimerTurn(Controller controller, Integer time, Player playerOfTurn) {
        this.controller = controller;
        this.time = time;
        this.playerOfTurn = playerOfTurn;
    }

    public void setStop(boolean stop) {
        this.stop.set(stop);
    }

    @Override
    public void run() {
        stop.set(false);
        while (!stop.get() && time>0){
            try {
                this.wait(500);
                time-=500;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!stop.get()){
            controller.forceLogout(playerOfTurn);
        }
    }
}