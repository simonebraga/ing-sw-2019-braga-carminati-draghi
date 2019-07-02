package it.polimi.ingsw.model.smartmodel;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;

/**
 * This class represents objects containing the fundamental information to rebuild a game map topology from an index.
 *
 * @author Draghi96
 */
public class SmartMap {

    /**
     * This attribute represents the upper borders of the grid from left to right top down on the grid.
     */
    private Border[][] upperBorders;

    /**
     * This attribute represents the left borders of the grid from left to right top down on the grid.
     */
    private Border[][] leftBorders;

    /**
     * This attribute represents the last row borders top down on the grid.
     */
    private Border[] rightMostBorders;

    /**
     * This attribute represents the grid squares type from left to right top down on the grid.
     */
    private String[][] squareTypes;

    /**
     * This attribute represents the spawn colors from left to right top down on the grid.
     */
    private Color[] spawnColors;

    /**
     * This method is the void constructor that Jackson uses for JSON parsing.
     */
    public SmartMap() {
        this.upperBorders=null;
        this.leftBorders=null;
        this.rightMostBorders=null;
        this.squareTypes=null;
        this.spawnColors =null;
    }

    /**
     * This method returns the value for the upperBorders attribute.
     *
     * @return a Border matrix that is the value of upperBorders.
     */
    public Border[][] getUpperBorders() {
        return upperBorders;
    }

    /**
     * This method sets a new value for upperBorder attribute.
     *
     * @param upperBorders a Border matrix that will be the new value.
     */
    public void setUpperBorders(Border[][] upperBorders) {
        this.upperBorders = upperBorders;
    }

    /**
     * This method returns the value for the leftBorders attribute.
     *
     * @return a Border matrix that is the value of leftBorders.
     */
    public Border[][] getLeftBorders() {
        return leftBorders;
    }

    /**
     * This method sets a new value for lefBorders attribute.
     *
     * @param leftBorders a Border matrix that will be the new value.
     */
    public void setLeftBorders(Border[][] leftBorders) {
        this.leftBorders = leftBorders;
    }

    /**
     * This method returns the value for the rightMostBorder attribute.
     *
     * @return a Border array that is the value of rightMostBorder.
     */
    public Border[] getRightMostBorders() {
        return rightMostBorders;
    }

    /**
     * This method sets a new value for rightMostBorders attribute.
     *
     * @param rightMostBorders a Border array that will be the new value.
     */
    public void setRightMostBorders(Border[] rightMostBorders) {
        this.rightMostBorders = rightMostBorders;
    }

    /**
     * This method returns the value for the squareTypes attribute.
     *
     * @return a String matrix that is the value of squareTypes.
     */
    public String[][] getSquareTypes() {
        return squareTypes;
    }

    /**
     * This method sets a new value for squareTypes attribute.
     *
     * @param squareTypes a String matrix that will be the new value.
     */
    public void setSquareTypes(String[][] squareTypes) {
        this.squareTypes = squareTypes;
    }

    /**
     * This method returns the value for the spawnColors attribute.
     *
     * @return a Color array that is the value of spawnColors.
     */
    public Color[] getSpawnColors() {
        return spawnColors;
    }

    /**
     * This method sets a new value for spawnColors attribute.
     *
     * @param spawnColors a Color array that will be the new value.
     */
    public void setSpawnColors(Color[] spawnColors) {
        this.spawnColors = spawnColors;
    }
}
