package it.polimi.ingsw.model.enumeratedclasses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This enumeration will be use to encode ammunition type.
 *
 * @author Draghi96
 */
public enum  Color {

    @JsonProperty("RED")
    RED,
    @JsonProperty("BLUE")
    BLUE,
    @JsonProperty("YELLOW")
    YELLOW
}