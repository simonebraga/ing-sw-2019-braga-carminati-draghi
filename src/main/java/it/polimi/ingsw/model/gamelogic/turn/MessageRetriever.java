package it.polimi.ingsw.model.gamelogic.turn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Support class that gets all the messages useful for the communication with the player.
 */
public class MessageRetriever {

    private static final String PATH = "src/main/resources/messages.properties";

    public String retrieveMessage(String message){
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(message);
    }
}
