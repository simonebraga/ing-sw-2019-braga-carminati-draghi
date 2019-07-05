package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Support class that calculates all the squares that are visible in a certain direction
 * and have a certain distance.
 */

class SquaresVisibleInADirection {

    /**
     * The direction where the square must be.
     */
    private Character direction;

    /**
     * The exact distance where the squares must be.
     */
    private Integer distance;

    /**
     * The player from where start the visibility.
     */
    private Player from;

    SquaresVisibleInADirection(Character direction, Integer distance, Player from) {
        this.direction = direction;
        this.distance = distance;
        this.from = from;
    }

    public ArrayList<Square> run(GameTable table){
        ArrayList<Square> squaresAssigned = new ArrayList<>();
        Square start = from.getPosition();

        for(int i=0; i<=distance; i++) {
            ArrayList<Square> map = new ArrayList<>(table.getGameMap().getVisibility(start));
            switch (direction) {
                case 'N':
                    for (Square square : map) {
                        if(table.getGameMap().getCoord(square).get(0) == -i + table.getGameMap().getCoord(start).get(0) &&
                                table.getGameMap().getCoord(square).get(1) == table.getGameMap().getCoord(start).get(1))
                            squaresAssigned.add(square);
                    }
                    break;
                case 'S':
                    for (Square square : map) {
                        if(table.getGameMap().getCoord(square).get(0) == +i + table.getGameMap().getCoord(start).get(0) &&
                                table.getGameMap().getCoord(square).get(1) == table.getGameMap().getCoord(start).get(1))
                            squaresAssigned.add(square);
                    }
                    break;
                case 'W':
                    for (Square square : map) {
                        if(table.getGameMap().getCoord(square).get(0) == table.getGameMap().getCoord(start).get(0) &&
                                table.getGameMap().getCoord(square).get(1) == -i + table.getGameMap().getCoord(start).get(1))
                            squaresAssigned.add(square);
                    }
                    break;
                default:
                    for (Square square : map) {
                        if(table.getGameMap().getCoord(square).get(0) == table.getGameMap().getCoord(start).get(0) &&
                                table.getGameMap().getCoord(square).get(1) == i + table.getGameMap().getCoord(start).get(1))
                            squaresAssigned.add(square);

                    }
                    break;
            }
            start = squaresAssigned.get(0);
        }
        return squaresAssigned;
    }
}
