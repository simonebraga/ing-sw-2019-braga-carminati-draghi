package it.polimi.ingsw.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.gamelogic.actions.SpawnAction;
import it.polimi.ingsw.model.gamelogic.turn.TurnManager;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.playerclasses.StartingPlayerMarker;
import it.polimi.ingsw.network.UnavailableUserException;

import java.io.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains the main method.<br>
 *     It regulates network setup, game initialization, player turns and final scoring.
 *
 * @author Draghi96
 */
public class ControllerMain {

    /**
     * This attribute is the GameTable object containing all current match information.
     */
    private GameTable gameTable;

    /**
     * This method is the main method of this program.
     * <p>It quickly instantiates a Controller object and runs its network setup.
     * When the controller is done with the login phase it calls the second part of the main, which is goOn().</p>
     *
     * @param args an array of strings containing hypothetical caller arguments.
     */
    public static void main(String[] args) {
        try {

            //sets up network
            Controller controller = new Controller();
            controller.startLoginPhase();
            //expecting goOn() to be called by controller...

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called by the controller when it's done with the login phase and it goes on with the game initialization and turn management.<br>
     *     The final frenzy is executed when a FrenzyModeException is thrown by a turn execution.
     *
     * @param controller is a Controller object to get access to network related methods.
     */
    public void goOn(Controller controller) {
        try {

            //binds each user to a unique player
            ArrayList<Player> players = new ArrayList<>();
            Figure[] allFigures = Figure.values();
            Integer i=0;
            for (String nick : controller.getNicknameSet()) {
                players.add(new Player(allFigures[i],nick));    //should not overflow because users are never more than figures
                i++;
            }

            //elect a first player and choose game mode
            int startingPlayerIndex;
            StartingPlayerMarker chosenStartingPlayerMarker = null;
            do {
                startingPlayerIndex = ThreadLocalRandom.current().nextInt(0, players.size());
                chosenStartingPlayerMarker = new StartingPlayerMarker(players.get(startingPlayerIndex));
            } while (!controller.isConnected(chosenStartingPlayerMarker.getTarget()));

            //ask gameMode to first player
            Character gameMode = controller.chooseMode(chosenStartingPlayerMarker.getTarget());

            //ask more...
            Integer index;
            if(gameMode == 'n' || gameMode == 'd'){

                //ask first player which map to load
                index = controller.chooseMap(chosenStartingPlayerMarker.getTarget(),0,3);

            } else {    //gameMode == 's' true

                //read save files list
                ObjectMapper objectMapper = new ObjectMapper();

                InputStream saveList = ControllerMain.class.getClassLoader().getResourceAsStream("save_list.json");
                String[] fileNamesArray = objectMapper.readValue(saveList,String[].class);
                ArrayList<String> fileNames = new ArrayList<>(Arrays.asList(fileNamesArray));

                //ask first player which save file to load
                String saveName = controller.chooseSave(chosenStartingPlayerMarker.getTarget(),fileNames);

                //calculate save file index
                i=0;
                while (i<fileNames.size() && fileNames.get(i).equals(saveName)) i++;
                index=i;
            }

            //initiate a new match
            GameInitializer gameInitializer = new GameInitializer(gameMode,index,players,chosenStartingPlayerMarker);
            gameTable = gameInitializer.run();

            //make each player spawn and first turn
            firstTurnEach(controller,startingPlayerIndex);  //would never throw FrenzyModeException

            //match rolling...
            rollMatch(controller,startingPlayerIndex);      //would throw FrenzyModeException at some point or game will stop for loss of players

            //game has ended because too many people disconnected
            System.out.println("Not enough players to continue the game.");
            save(gameTable);
            //restart program
            main(null);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FrenzyModeException e) {   //match is now in final frenzy

            //change bounty value to each undamaged player
            Integer[] points = {2,1,1,1};
            for (Player player : gameTable.getPlayers()) {
                if (player.getDamageTrack().getDamage().isEmpty()) {
                    player.getPointTrack().setValue(new ArrayList<>(Arrays.asList(points)));
                }
            }

            //this will contain all frenzy turns already in the correct order
            ArrayList<TurnManager> frenzyTurns = new ArrayList<>();

            //find current player inside players list
            int currentPlayerIndex;
            for (currentPlayerIndex = 0; !gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer()); currentPlayerIndex++);
            //currentPlayerIndex is the current player index

            //find starting player inside players list
            int startingPlayerIndex;
            for (startingPlayerIndex = 0; !gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget()) ; startingPlayerIndex++);
            //startingPlayerIndex is the starting player index

            //create frenzy turns considering players position relative to first player in the turn sequence
            int i;
            //from current player to starting player -1, all before
            for (i = currentPlayerIndex; !gameTable.getPlayers().get(i).equals(gameTable.getStartingPlayerMarker().getTarget()); i++) {
                if (controller.isConnected(gameTable.getPlayers().get(i))) {
                    frenzyTurns.add(new TurnManager(gameTable.getPlayers().get(i),true,true));
                }

                //cycling array and updating current player
                if (i==gameTable.getPlayers().size()) {
                    gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(0));
                    i=-1;
                } else {
                    gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(i+1));
                }
            }

            //from starting player to current player -1, all not before
            while (!gameTable.getPlayers().get(i).equals(gameTable.getCurrentTurnPlayer())) {
                if (controller.isConnected(gameTable.getPlayers().get(i))) {
                    frenzyTurns.add(new TurnManager(gameTable.getPlayers().get(i),true,false));
                }

                //cycling array and updating current player
                if (i==gameTable.getPlayers().size()) {
                    gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(0));
                    i=-1;
                } else {
                    gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(i+1));
                }
                i++;
            }

            //execute final turns
            for (TurnManager turn : frenzyTurns) {
                try {
                    turn.runTurn(controller,gameTable);
                } catch (FrenzyModeException ex) {
                    //should never end up here
                    ex.printStackTrace();
                }
            }

            //calculate winner
            Player winner = proclaimWinner(gameTable);

            System.out.println(winner.getUsername() + "won the game!\n");

            //program ends

        } catch (UnavailableUserException e) {
            //if first player disconnects while the game is still initializing redo all previous procedure, randomly choosing another first player
            goOn(controller);
        }
    }

    /**
     * This private method serializes all match information into a json file giving it a date and time as name.<br>
     *     It also updates the save_list.json file to show the latest save.
     *
     * @param gameTable a GameTable object that captures all match information.
     */
    private void save(GameTable gameTable) {
        try {

            //get date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-dd_(HH-mm-ss)");
            LocalDateTime time = LocalDateTime.now();

            //create a new save file in /resources/savefiles
            FileOutputStream file = new FileOutputStream("src/main/resources/savefiles/save_"+formatter.format(time)+".json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file,gameTable);
            file.close();

            //update save_list.json to show new save in list
            //retrieve save_list.json
            InputStream file1 = ControllerMain.class.getClassLoader().getResourceAsStream("save_list.json");
            String[] fileNames = mapper.readValue(file1,String[].class);
            //add the new save name to the list
            ArrayList<String> fileNameList = new ArrayList<>(Arrays.asList(fileNames));
            fileNameList.add("save_"+formatter.format(time));
            String[] newFileNames = (String[]) fileNameList.toArray();
            //rewrite list in save_list.json as an array of strings
            file = new FileOutputStream("src/main/resources/save_list.json");
            mapper.writeValue(file,newFileNames);
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This private method creates and executes all first spawn actions and turn, considering the first player.
     *
     * @param controller a Controller object that ensures communication with users about choices on their turn options.
     * @param startingPlayerIndex an Integer that marks the first player index inside the players list.
     * @throws FrenzyModeException when a turn execution sets all conditions for the final frenzy.
     */
    private void firstTurnEach(Controller controller, Integer startingPlayerIndex) throws FrenzyModeException {

        //execute first player turn, if he is still connected
        if (!controller.isConnected(gameTable.getStartingPlayerMarker().getTarget())) {

            SpawnAction spawnAction0 = new SpawnAction(gameTable.getStartingPlayerMarker().getTarget());
            spawnAction0.run(controller,gameTable);
            TurnManager turn0 = new TurnManager(gameTable.getStartingPlayerMarker().getTarget(),false,false);
            turn0.runTurn(controller,gameTable);
        }

        //cycling array
        if(startingPlayerIndex==gameTable.getPlayers().size()){
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(0));
        } else {
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(startingPlayerIndex + 1));
        }

        //execute other first turns, from starting player +1 to starting player -1, if they are connected
        for (int i = startingPlayerIndex + 1;
             !gameTable.getPlayers().get(i).equals(gameTable.getStartingPlayerMarker().getTarget()); i++) {

            if (controller.isConnected(gameTable.getPlayers().get(i))) {

                SpawnAction spawnAction = new SpawnAction(gameTable.getPlayers().get(i));
                spawnAction.run(controller,gameTable);
                TurnManager turn = new TurnManager(gameTable.getPlayers().get(i),false,false);
                turn.runTurn(controller,gameTable);
            }

            if (i==gameTable.getPlayers().size() - 1) {  //cycling array
                i = -1;
                gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(0));
            } else {
                gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(i));
            }
        }
    }

    /**
     * This private method executes all turns following the players list order until a turn execution throws a FrenzyModeException.
     *
     * @param controller a Controller object that ensures communication with users about choices on their turn options.
     * @param startingPlayerIndex an Integer that marks the first player index inside the players list.
     * @throws FrenzyModeException when a turn execution sets all conditions for the final frenzy.
     */
    private void rollMatch(Controller controller, Integer startingPlayerIndex) throws FrenzyModeException {
        //this while at some point will break because of FrenzyModeException throw
        int i=startingPlayerIndex;
        while(gameTable.getPlayers().size()>=3) {   //there are at least 3 players still connected

            //execute turn if player is still connected
            if (controller.isConnected(gameTable.getPlayers().get(i))) {

                TurnManager turn = new TurnManager(gameTable.getPlayers().get(i),false,true);
                turn.runTurn(controller,gameTable);
            }

            //circular list approach for players
            i++;
            if (i==gameTable.getPlayers().size()) i=0;

            //pass turn to next player
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(i));
        }
        //if this while stops it's because there are less than 3 players still connected
    }

    /**
     * This private method calculates final points for each player and returns the player with the highest score.
     *
     * @param gameTable a GameTable object which contains all match information.
     * @return the player which has the highest score.
     */
    private Player proclaimWinner(GameTable gameTable) {

        return null;
    }
}
