package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class contains all the remote methods of the client that can be invoked from the server.
 * These methods are parameterized to keep a lightened network implementation. Any other method should be implemented using the ones of this class
 * (I.E. The request of an answer like "How are you?" should be implemented using genericWithResponse method passing a keyword as first parameter and the question as second parameter.
 * This keyword should then be correctly interpreted from the client and managed accordingly)
 */
public interface ClientRemote extends Remote {

    /**
     * This method should just return any value. It is used to check the connection
     */
    int ping() throws RemoteException;

    /**
     * This method should be used to implement the parsing of the parameters based on the id.
     * To every id should be associated a different behavior of the method
     * @param id should denote the way parameters should be interpreted
     * @param parameters should be parsed based on the id
     */
    void genericWithoutResponse(String id, String parameters) throws RemoteException;

    /**
     * This method should be used to implement the parsing of the parameters based on the id.
     * To every id should be associated a different behavior of the method
     * @param id should denote the way parameters should be interpreted
     * @param parameters should be parsed based on the id
     * @return a value coherent with the id and the parameters
     */
    String genericWithResponse(String id, String parameters) throws RemoteException;

    /**
     * This method should implement the interaction with the user that allows him to choose a single value in a given set
     * @param id should denote the type of the parameters
     * @param parameters should be parsed based in the id
     * @return a value chosen by the user
     */
    int singleChoice(String id, String parameters) throws RemoteException;

    /**
     * This method should implement the interaction with the user that allows him to choose multiple values in a given set
     * @param id should denote the type of the parameters
     * @param parameters should be parsed based in the id
     * @return set of values chosen by the user
     */
    int[] multipleChoice(String id, String parameters) throws RemoteException;

    /**
     * This method should implement a boolean interaction with the user, that allows him to answer with a boolean value to a simple question
     * @param parameters should be the question to be submitted to the user
     * @return the boolean answer to the question chosen by the user
     */
    boolean booleanQuestion(String parameters) throws RemoteException;
}