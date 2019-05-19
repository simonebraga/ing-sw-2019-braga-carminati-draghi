package it.polimi.ingsw.model.mapclasses;

import it.polimi.ingsw.model.enumeratedclasses.Border;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the map of the game.
 */
public class GameMap {

    /**
     * Contains the Squares of the map and the position of each Square in the map.
     * <p>It can contain some Square null when there is no Square in that position</p>
     */
    private Square[][] grid;

    /**
     * Contains the list of Squares that are SpawnSquare. It makes faster to see which Squares
     * are SpawnSquare.
     */
    private ArrayList<SpawnSquare> spawnSquares;

    /**
     * Contains the list of Squares that are TileSquare. It makes faster to see which Squares
     * are TileSquare.
     */
    private ArrayList<TileSquare> tileSquares;

    /**
     * This constructors initializes all attributes to null.
     */
    public GameMap() {
        this.grid=new Square[3][4];
        this.spawnSquares=new ArrayList<>();
        this.tileSquares=new ArrayList<>();
    }

    public GameMap(Square[][] grid, ArrayList<SpawnSquare> spawnSquares, ArrayList<TileSquare> tileSquares) {
        this.grid = grid;
        this.spawnSquares = spawnSquares;
        this.tileSquares = tileSquares;
    }

    /**
     * This private method sets to null Square elements of the grid attribute that show borders equals to null.
     *
     * @param grid a grid to be formatted.
     * @author Draghi96
     */
    private void setValuesGrid(Square[][] grid) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid[i][j].getUp()==null) {
                    grid[i][j] = null;
                }
            }
        }
    }

    public Square[][] getGrid() {
        return grid;
    }

    public ArrayList<SpawnSquare> getSpawnSquares() {
        return spawnSquares;
    }

    public ArrayList<TileSquare> getTileSquares() {
        return tileSquares;
    }

    /**
     * This method sets a new Square[][] grid as attribute.
     *
     * @param grid a new Square[][] to which the grid attribute will be set.
     */
    public void setGrid(Square[][] grid) {
        setValuesGrid(grid);
        this.grid=grid;
    }

    /**
     * This method sets a new ArrayList of Square as spawnSquares attribute.
     *
     * @param spawnSquares a new ArrayList of Square to which this attribute will be set.
     */
    public void setSpawnSquares(ArrayList<SpawnSquare> spawnSquares) {this.spawnSquares=spawnSquares;}

    /**
     * This method sets a new ArrayList of Square as tileSquares attribute.
     *
     * @param tileSquares a new ArrayList of Square o which this attribute will be set.
     */
    public void setTileSquares(ArrayList<TileSquare> tileSquares) {this.tileSquares=tileSquares;}

    /**
     * Return the coordinates of the Square passed by parameter.
     * @param square The Square of which the method will find the coordinates.
     * @return A two-Integer Array, the first value is the x coordinate, the second value is
     * the y coordinate.
     */
    public ArrayList<Integer> getCoord(Square square){
        int i;
        int j;
        ArrayList<Integer> coordinates = new ArrayList<>(2);
        for(i = 0; i<getGrid()[0].length; i++){
            for(j = 0; j<getGrid().length; j++){
                if(square == getGrid()[i][j]){
                    coordinates.add(i);
                    coordinates.add(j);
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    /**
     * Returns the Square in the coordinates (x, y).
     * <p>It can return a null pointer if there is no Square in that coordinates.
     * The caller of the method must control that the coordinates are not negative and that they
     * are lower than the max coordinate possible</p>
     * @param x The first coordinate of the Square.
     * @param y The second coordinate of the Square.
     * @return The Square in the coordinates (x, y).
     */
    public Square getSquare(Integer x, Integer y){
        return getGrid()[x][y];
    }

    /**
     * Returns all the Square that are in the same room of the Square passed by parameter.
     * <p>The method returns also the Square passed by parameter.</p>
     * @param square The Square of which the method will find the room.
     * @return The List of the Squares that compose the room of the Square passed by parameter.
     */
    public ArrayList<Square> getRoom(Square square){
        Square squareAnalyzing;
        Square squareTemp;
        Integer xTemp;
        Integer yTemp;
        ArrayList<Square> squaresToAnalyze = new ArrayList<>();
        ArrayList<Square> squaresToReturn = new ArrayList<>();
        squaresToAnalyze.add(square);
        while(!squaresToAnalyze.isEmpty()){
            squareAnalyzing = squaresToAnalyze.get(0);
            xTemp = getCoord(squareAnalyzing).get(0);
            yTemp = getCoord(squareAnalyzing).get(1);
            if(squareAnalyzing.getUp() == Border.NOTHING){
                squareTemp = getSquare(xTemp - 1, yTemp);
                if(!squaresToAnalyze.contains(squareTemp) && !squaresToReturn.contains(squareTemp)){
                    squaresToAnalyze.add(squareTemp);
                }
            }
            if(squareAnalyzing.getDown() == Border.NOTHING){
                squareTemp = getSquare(xTemp + 1, yTemp);
                if(!squaresToAnalyze.contains(squareTemp) && !squaresToReturn.contains(squareTemp)){
                    squaresToAnalyze.add(squareTemp);
                }
            }
            if(squareAnalyzing.getLeft() == Border.NOTHING){
                squareTemp = getSquare(xTemp, yTemp - 1);
                if(!squaresToAnalyze.contains(squareTemp) && !squaresToReturn.contains(squareTemp)){
                    squaresToAnalyze.add(squareTemp);
                }
            }
            if(squareAnalyzing.getRight() == Border.NOTHING){
                squareTemp = getSquare(xTemp, yTemp + 1);
                if(!squaresToAnalyze.contains(squareTemp) && !squaresToReturn.contains(squareTemp)){
                    squaresToAnalyze.add(squareTemp);
                }
            }
            squaresToReturn.add(squareAnalyzing);
            squaresToAnalyze.remove(0);
        }
        return squaresToReturn;
    }

    /**
     * Return all the Squares that are visible from the Square passed by parameter.
     * <p>The method returns also the Square passed by parameter</p>
     * @param square The Square of which the method will find the visibility.
     * @return The List of the Square that are visible from the Square passed by parameter.
     */
    public ArrayList<Square> getVisibility(Square square){
        ArrayList<Square> squaresToReturn = new ArrayList<>();
        ArrayList<Integer> coordinates = getCoord(square);
        Integer xTemp = coordinates.get(0);
        Integer yTemp = coordinates.get(1);
        squaresToReturn.addAll(getRoom(square));
        if(square.getUp() == Border.DOOR){
            squaresToReturn.addAll(getRoom(getSquare(xTemp - 1, yTemp)));
        }
        if(square.getDown() == Border.DOOR){
            squaresToReturn.addAll(getRoom(getSquare(xTemp + 1, yTemp)));
        }
        if(square.getLeft() == Border.DOOR){
            squaresToReturn.addAll(getRoom(getSquare(xTemp, yTemp - 1)));
        }
        if(square.getRight() == Border.DOOR){
            squaresToReturn.addAll(getRoom(getSquare(xTemp, yTemp + 1)));
        }
        return squaresToReturn.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all the Squares that can be reached with the number of moves passed by the parameter
     * or less.
     * <p>Attention! The Squares can be not visible from the starting square.</p>
     * @param range The maximum number of moves that can be done.
     * @param square The starting Square to calculate the range.
     * @return The list of Squares that can be reached with the number of moves passed by
     * the parameter or less. This list contains no repetitions.
     */
    public ArrayList<Square> getRange(Square square, Integer range){
        ArrayList<Square> squaresToReturn = new ArrayList<>();
        ArrayList<Square> squaresToControl = new ArrayList<>();
        ArrayList<Square> squaresToControlNext = new ArrayList<>();
        Square squareTemp;
        Integer xTemp;
        Integer yTemp;
        int i = -1;
        squaresToControl.add(square);
        while(i < range){
            while(!squaresToControl.isEmpty()){
                squareTemp = squaresToControl.get(0);
                xTemp = getCoord(squareTemp).get(0);
                yTemp = getCoord(squareTemp).get(1);
                if(squareTemp.getUp()!=Border.WALL){
                    squaresToControlNext.add(getSquare(xTemp - 1, yTemp));
                }
                if(squareTemp.getDown()!=Border.WALL){
                    squaresToControlNext.add(getSquare(xTemp + 1, yTemp));
                }
                if(squareTemp.getLeft()!=Border.WALL){
                    squaresToControlNext.add(getSquare(xTemp, yTemp - 1));
                }
                if(squareTemp.getRight()!=Border.WALL){
                    squaresToControlNext.add(getSquare(xTemp, yTemp + 1));
                }
                squaresToReturn.add(squareTemp);
                squaresToControl.remove(0);
            }
            squaresToControlNext.removeAll(squaresToReturn);
            squaresToControlNext =
                    squaresToControlNext.stream().distinct()
                            .collect(Collectors.toCollection(ArrayList::new));
            squaresToControl.addAll(squaresToControlNext);
            squaresToControlNext = new ArrayList<>();
            i++;
        }
        return squaresToReturn;
    }

    /**
     * Returns all the Squares that can be reached only and exactly with the number of moves passed
     * by the parameter.
     * <p>Attention! The Squares can be not visible from the starting square.</p>
     * @param distance The exact number of moves that can be done.
     * @param square The starting Square to calculate the distance.
     * @return The list of Squares that can be reached only and exactly with the number of
     * moves passed by the parameter.
     */
    public ArrayList<Square> getDistance(Square square, Integer distance){
        ArrayList<Square> squaresToReturn = getRange(square, distance);
        squaresToReturn.removeAll(getRange(square, distance - 1));
        return squaresToReturn;
    }

    /**
     * This method compares two GameMap objects and returns true if they are to be considered equals.
     *
     * @param obj a GameMap object to be compared with the GameMap object that called this method.
     * @return true if the two objects have the same Square[][] grid.
     * @author Draghi96
     */
    @Override
    public boolean equals(Object obj) {
        GameMap gameMap = (GameMap) obj;
        for (int i = 0; i <this.grid.length ; i++) {
            if (!Arrays.equals(this.getGrid()[i],gameMap.getGrid()[i])) {
                return false;
            }
        }
        return true;
    }
}
