package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Creates and sets the effect that makes the player pay.
 */
public class PayCreator implements EffectsCreator{

    /**
     * The player that has to pay.
     */
    private Player player;

    /**
     * The price to pay.
     */
    private ArrayList<Color> price;

    /**
     * Constructor used when the price can be payed with any color.
     */
    public PayCreator(Player player) {
        this.player = player;
    }

    /**
     * Default constructor. Sets all the attributes.
     */
    public PayCreator(Player player, ArrayList<Color> price) {
        this.player = player;
        this.price = price;
    }

    public PayCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Powerup> powerUps;
        ArrayList<Color> priceReduced = new ArrayList<>(price);
        ArrayList<FunctionalEffect> effects =new ArrayList<>();

        powerUps = new ArrayList<>(player.getPowerupPocket().getPowerups());
        powerUps = powerUps.stream().filter(powerUp -> price.contains(powerUp.getColor())).collect(Collectors.toCollection(ArrayList::new)); //Remove powerUps that cannot be used to pay
        if(!powerUps.isEmpty()){
            server.sendMessage(player, new MessageRetriever().retrieveMessage("powerUpsToPay"));
            powerUps = server.chooseMultiplePowerup(player, powerUps);

            for (Powerup powerUp : powerUps) {
                if (!priceReduced.remove(powerUp.getColor())){
                    throw new IllegalActionException();
                }
                effects.add(() ->
                    table.getPowerupDeck().discard(
                            player.getPowerupPocket().removePowerup(
                                    player.getPowerupPocket().getPowerups().indexOf(powerUp))));
            }
        }

        if( priceReduced.stream().filter(color -> color.equals(Color.BLUE)).count() <= player.getAmmoPocket().getAmmo(Color.BLUE)
        && priceReduced.stream().filter(color -> color.equals(Color.RED)).count() <= player.getAmmoPocket().getAmmo(Color.RED)
        && priceReduced.stream().filter(color -> color.equals(Color.YELLOW)).count() <= player.getAmmoPocket().getAmmo(Color.YELLOW)){
            effects.add(new FunctionalFactory().createPay(player, priceReduced));
        }else{
            throw new IllegalActionException();
        }

        return effects;
    }

    public ArrayList<FunctionalEffect> payAnyColor(Server server, GameTable table, ArrayList<Powerup> powerUpsCaller, Integer price) throws IllegalActionException, UnavailableUserException {
        ArrayList<Powerup> powerUps;
        ArrayList<FunctionalEffect> effects =new ArrayList<>();
        powerUps = new ArrayList<>(player.getPowerupPocket().getPowerups());
        powerUps.removeAll(powerUpsCaller);

        server.sendMessage(player, new MessageRetriever().retrieveMessage("powerUpsToPay"));
        powerUps = server.chooseMultiplePowerup(player, powerUps);

        if(powerUps.size()>price) {
            throw new IllegalActionException();
        }
        for (Powerup powerUp : powerUps) {
            effects.add(() ->
                    table.getPowerupDeck().discard(
                            player.getPowerupPocket().removePowerup(
                                    player.getPowerupPocket().getPowerups().indexOf(powerUp))));
        }
        price = price - powerUps.size();


        Color choice;
        for(; price>0; price--){
            choice = server.chooseColor(player);
            if(player.getAmmoPocket().getAmmo(choice)<0){
                throw new IllegalActionException();
            }
            ArrayList<Color> payment = new ArrayList<>();
            payment.add(choice);
            effects.add(new FunctionalFactory().createPay(player, payment));
        }
        return effects;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Color> getPrice() {
        return price;
    }

    public void setPrice(ArrayList<Color> price) {
        this.price = price;
    }
}
