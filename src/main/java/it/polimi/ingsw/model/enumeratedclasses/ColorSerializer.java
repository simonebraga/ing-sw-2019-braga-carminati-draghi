package it.polimi.ingsw.model.enumeratedclasses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.StringWriter;

/**
 * This class is used to serialize a Color object into a JSON file.
 *
 * @author Draghi96
 */
public class ColorSerializer extends JsonSerializer<Color> {

    /**
     * This attribute is the Jackson object that builds and reads JSON files.
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * This method implements how to serialize a Color object into a JSON file.
     *
     * @param color a Color object to be serialized.
     * @param jsonGenerator a JsonGenerator object that builds the JSON file.
     * @param serializerProvider a SerializerProvider object that writes on the JSON file.
     * @throws IOException if the file cannot be accessed correctly.
     */
    @Override
    public void serialize(Color color, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, color);
        jsonGenerator.writeFieldName(writer.toString());
    }
}
