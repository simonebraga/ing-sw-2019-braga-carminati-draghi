package it.polimi.ingsw.model.mapclasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.cardclasses.AmmoTile;

/**
 * Represents the squares that can contains AmmoTile.
 */
public class TileSquare extends Square {

    /**
     * The AmmoTile contained in the Square. Can be null if there are no AmmoTiles in that Square.
     */
    private AmmoTile tile;

    /**
     * This constructor sets all attributes to null.
     */
    public TileSquare() {
        super(null,null,null,null, null, null);
        this.tile=null;
    }

    public TileSquare(Border up, Border down, Border left, Border right, Integer x, Integer y) {
        super(up, down, left, right, x ,y);
        tile = null;
    }

    @JsonCreator
    public TileSquare(@JsonProperty("up") Border up, @JsonProperty("down") Border down, @JsonProperty("left") Border left, @JsonProperty("right") Border right,
                      @JsonProperty("x") Integer x, @JsonProperty("y") Integer y,
                      @JsonProperty("tile") AmmoTile tile) {
        super(up,down,left,right, x, y);
        this.tile=tile;
    }

    public AmmoTile getTile() {
        return tile;
    }

    public void setTile(AmmoTile tile) {
        this.tile = tile;
    }

    /**
     * Removes the AmmoTile from the Square. Must be called only when the Square contains a AmmoTile.
     * @return The AmmoTile that has been removed.
     */
    public AmmoTile removeTile(){
        AmmoTile tileRemoved;
        tileRemoved = getTile();
        setTile(null);
        return tileRemoved;
    }

    /**
     * Adds a AmmoTile to the square. Must be called only when the Square doesn't contain an AmmoTile.
     * @param tile The AmmoTile to add to the Square.
     */
    public void addTile(AmmoTile tile){
        setTile(tile);
    }

    /**
     * This method compares two TileSquare objects and returns true if they are to be considered equals.
     *
     * @param obj a TileSquare object to be compared with the TileSquare object that calls this method.
     * @return true if the two objects have same Border values and same players lists.
     * @author Draghi96
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
