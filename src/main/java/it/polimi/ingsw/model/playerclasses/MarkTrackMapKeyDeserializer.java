package it.polimi.ingsw.model.playerclasses;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This class is used to deserialize a MarkTrack object from a JSON file.
 *
 * @author Draghi96
 */
public class MarkTrackMapKeyDeserializer extends KeyDeserializer {

    /**
     * This attribute is the Jackson object used to read JSON files.
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * This method implements how to deserialize a MarkTrack object from a JSON file.
     *
     * @param s a String object that is the serialized MarkTrack object.
     * @param deserializationContext a DeserializationContext object containing all info about this deserialization file.
     * @return a Object which is the deserialized MarkTrack object.
     * @throws IOException if the file cannot be accessed correctly.
     */
    @Override
    public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
        return mapper.readValue(s,Player.class);
    }
}
