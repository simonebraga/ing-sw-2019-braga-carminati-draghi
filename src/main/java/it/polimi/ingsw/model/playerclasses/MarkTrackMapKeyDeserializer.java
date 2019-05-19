package it.polimi.ingsw.model.playerclasses;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MarkTrackMapKeyDeserializer extends KeyDeserializer {

    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
        return mapper.readValue(s,Player.class);
    }
}
