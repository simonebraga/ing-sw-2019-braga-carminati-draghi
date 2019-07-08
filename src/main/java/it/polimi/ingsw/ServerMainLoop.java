package it.polimi.ingsw;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.autocontroller.ServerRandom;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.gamelogic.actions.SpawnAction;
import it.polimi.ingsw.model.gamelogic.settings.SettingsJSONParser;
import it.polimi.ingsw.model.gamelogic.turn.TurnManager;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.network.UnavailableUserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ServerMainLoop {

    private static final int LOGIN_PHASE_TIMER = 1000;
    private static final int MAPS_NUMBER = 4;
    private static final String FIRST_TURNS_PHASE = "ft";
    private static final String ROLLING_TURNS_PHASE = "rll";
    private static final String FINAL_FRENZY_PHASE = "ff";
    private static final String NETWORK_PROPERTIES = "network_settings.properties";
    private static final String GAME_SETTINGS_PATH = "game_settings.json";

    public static void main(String[] args) {
        try {

            Properties networkProperties = new Properties();
            try {
                networkProperties.load(Objects.requireNonNull(ServerMainLoop.class.getClassLoader().getResourceAsStream(NETWORK_PROPERTIES)));
            } catch (IOException e) {
                System.err.println("Error reading " + NETWORK_PROPERTIES);
                throw new Exception();
            }

            System.out.println("Adrenaline Server running...\n");

            //sets up network
            Server server = new ServerRandom();
            server.startLoginPhase();

            while (server.isLoginPhase()) {
                Thread.sleep(LOGIN_PHASE_TIMER);
            }
            //server has now connected users

            goOn(server,args,0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void goOn(Server server, String[] args, int adminIndex) {

        Integer currentPlayerIndex = 0;
        Integer startingPlayerIndex = 0;

        GameTable gameTable;

        Integer mapIndex = null;
        Character gameMode = null;

        System.out.println("Creating new match...");

        //binds each user to a unique player
        ArrayList<Player> players = new ArrayList<>();
        Figure[] allFigures = Figure.values();
        Integer i = 0;
        for (String nick : server.getNicknameSet()) {
            players.add(new Player(allFigures[i], nick));    //should not overflow because users are never more than figures
            i++;
        }

        //ask game mode to administrator if he's still connected
        try {
            gameMode = server.chooseMode(players.get(adminIndex));
        } catch (UnavailableUserException e) {
            players.forEach(server::forceLogout);
            goOn(server,new String[0],0);
        }


        //ask map index to administrator if he's still connected
        try {
            mapIndex = server.chooseMap(players.get(adminIndex), 0, MAPS_NUMBER-1);
        } catch (UnavailableUserException e) {
            players.forEach(server::forceLogout);
            goOn(server,new String[0],0);
        }


        //initiate a new match
        GameInitializer gameInitializer = new GameInitializer(gameMode, mapIndex, players);
        gameTable = gameInitializer.run();

        //finally created usable match

        //calculate starting player and current player indexes
        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer())) currentPlayerIndex++;
        while (!gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) startingPlayerIndex++;

        //sync smart model with model
        SmartModel smartModel = new SmartModel();
        smartModel.setMapIndex(mapIndex);
        server.setSmartModel(smartModel);
        smartModel.update(gameTable);
        server.notifyModelUpdate();

        //start or continue match
        if (gameTable.getGamePhase().equals(FIRST_TURNS_PHASE)) {   //game is in first turns phase

            //make each player spawn and first turn
            try {
                firstTurns(server, gameTable, currentPlayerIndex);  //would never throw FrenzyModeException
            } catch (FrenzyModeException e) {

                finalFrenzy(server,gameTable);
            }
        }

        if (gameTable.getGamePhase().equals(ROLLING_TURNS_PHASE)) {  //game is in rolling turns phase

            //match rolling...
            try {
                rollMatch(server, gameTable, currentPlayerIndex);      //would throw FrenzyModeException at some point or game will stop for loss of players
            } catch (FrenzyModeException e) {

                finalFrenzy(server,gameTable);
            }

            //game has ended before final frenzy because too many people disconnected
            gameOver(server, gameTable);
        }

    }

    private static void firstTurns(Server server, GameTable gameTable, Integer currentPlayerIndex) throws FrenzyModeException {

        //execute first player turn, if he is still connected
        if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {

            try {
                server.sendMessage(gameTable.getPlayers().get(currentPlayerIndex),"Choose a Power up to discard. You will be spawned to the spawn square having its same color.");
            } catch (UnavailableUserException e) {
                e.printStackTrace();
            }

            SpawnAction spawnAction0 = new SpawnAction(gameTable.getStartingPlayerMarker().getTarget());
            spawnAction0.run(server,gameTable);

            server.getSmartModel().update(gameTable);
            server.notifyModelUpdate();

            TurnManager turn0 = new TurnManager(gameTable.getStartingPlayerMarker().getTarget(),false,false);
            turn0.runTurn(server,gameTable);
        }

        //cycling array
        currentPlayerIndex++;
        if(currentPlayerIndex==gameTable.getPlayers().size()) currentPlayerIndex = 0;
        gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        //execute other first turns, from current player to starting player -1, if they are connected
        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) {

            if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {

                SpawnAction spawnAction = new SpawnAction(gameTable.getPlayers().get(currentPlayerIndex));
                spawnAction.run(server,gameTable);

                server.getSmartModel().update(gameTable);
                server.notifyModelUpdate();

                TurnManager turn = new TurnManager(gameTable.getPlayers().get(currentPlayerIndex),false,false);
                turn.runTurn(server,gameTable);
            }

            //cycling array
            currentPlayerIndex++;
            if (currentPlayerIndex == gameTable.getPlayers().size()) currentPlayerIndex = 0;
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        }

        //transitioning match phase
        gameTable.setGamePhase(ROLLING_TURNS_PHASE);
    }

    private static void rollMatch(Server server, GameTable gameTable, Integer currentPlayerIndex) throws FrenzyModeException {

        //this while will break at some point because of FrenzyModeException throw
        int i=currentPlayerIndex;
        while(true) {   //there are at least 3 players still connected

            //execute turn if player is still connected
            if (server.isConnected(gameTable.getPlayers().get(i))) {

                TurnManager turn = new TurnManager(gameTable.getPlayers().get(i),false,true);
                turn.runTurn(server,gameTable);
            }

            //circular list approach for players
            i++;
            if (i==gameTable.getPlayers().size()) i=0;

            //pass turn to next player
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(i));
        }
    }

    private static void finalFrenzy(Server server, GameTable gameTable) {

        //if this match was never in final frenzy before...
        if (!gameTable.getGamePhase().equals(FINAL_FRENZY_PHASE)) {

            //change bounty value to each undamaged player
            Integer[] points = null;
            InputStream file = it.polimi.ingsw.ServerMain.class.getClassLoader().getResourceAsStream(GAME_SETTINGS_PATH);
            ObjectMapper mapper = new ObjectMapper();
            try {
                SettingsJSONParser parser = mapper.readValue(file, SettingsJSONParser.class);
                points = parser.getFrenzyBounties();
                file.close();
            } catch (IOException e) {
                System.err.println("Unable to load settings.");
                e.printStackTrace();
            }
            for (Player player : gameTable.getPlayers()) {
                if (player.getDamageTrack().getDamage().isEmpty()) {
                    player.setHasBoardFlipped(true);
                    player.getPointTrack().setValue(new ArrayList<>(Arrays.asList(points)));
                }
            }

            gameTable.setFrenzyBeginner(gameTable.getCurrentTurnPlayer());

            //match is now in final frenzy
            gameTable.setGamePhase(FINAL_FRENZY_PHASE);
        }


        //find frenzy beginner, current player and starting player indexes inside players list
        int frenzyBeginnerIndex = 0;
        int currentPlayerIndex = 0;
        int startingPlayerIndex = 0;
        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer())) currentPlayerIndex++;
        while (!gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) startingPlayerIndex++;
        while (!gameTable.getPlayers().get(frenzyBeginnerIndex).equals(gameTable.getFrenzyBeginner())) frenzyBeginnerIndex++;

        int tempPlayerIndex;
        for (tempPlayerIndex = currentPlayerIndex;
             !gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getFrenzyBeginner()) &&
                     !gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget());
             tempPlayerIndex++) {

            if (tempPlayerIndex == gameTable.getPlayers().size() - 1) tempPlayerIndex = -1; //cycling array
        }

        if(!gameTable.getStartingPlayerMarker().getTarget().equals(gameTable.getFrenzyBeginner())) {
            currentPlayerIndex = cycleHalfFrenzyTurn(gameTable, server, currentPlayerIndex, gameTable.getStartingPlayerMarker().getTarget(), true);
            cycleHalfFrenzyTurn(gameTable, server, currentPlayerIndex, gameTable.getFrenzyBeginner(), false);
        }else{
            if(currentPlayerIndex != 0) {
                currentPlayerIndex = cycleHalfFrenzyTurn(gameTable, server, currentPlayerIndex, gameTable.getPlayers().get(currentPlayerIndex - 1), false);
            }else{
                currentPlayerIndex = cycleHalfFrenzyTurn(gameTable, server, currentPlayerIndex, gameTable.getPlayers().get(gameTable.getPlayers().size() - 1), false);
            }
            cycleHalfFrenzyTurn(gameTable, server, currentPlayerIndex, gameTable.getFrenzyBeginner(), false);
        }

        //end game
        gameOver(server, gameTable);

    }

    private static Integer cycleHalfFrenzyTurn(GameTable gameTable, Server server, Integer currentPlayerIndex, Player stopPlayer, boolean isBefore) {

        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(stopPlayer)) {

            if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {
                TurnManager turn = new TurnManager(gameTable.getPlayers().get(currentPlayerIndex), true, isBefore);
                try {
                    turn.runTurn(server, gameTable);
                } catch (FrenzyModeException e) {
                }
            }


            //cycling array
            currentPlayerIndex++;
            if (currentPlayerIndex == gameTable.getPlayers().size()) currentPlayerIndex = 0;
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        }
        return currentPlayerIndex;
    }

    private static void gameOver(Server server, GameTable gameTable) {
        goOn(server,new String[0],0);
    }
}