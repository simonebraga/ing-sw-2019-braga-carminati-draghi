package it.polimi.ingsw;

import java.util.ArrayList;

public class GameMap {

    private Square grid[][];
    private ArrayList<Square> spawnSquares;
    private ArrayList<Square> tileSquares;

    public Square getSquare(int x, int y) {
        return grid[x][y];
    }

    public ArrayList<Square> getRoom(Square square){
        ArrayList<Square> result = new ArrayList<>();
        Square cursor=square;

        //go all the way right
        while(cursor.getRight()!=Border.NOTHING) {
            cursor=grid[cursor.getCoords().get(0)+1][cursor.getCoords().get(1)];
        }

        //go all the way down
        while (cursor.getDown()!=Border.NOTHING){
            cursor=grid[cursor.getCoords().get(0)][cursor.getCoords().get(1)+1];
        }

        //from bottom right corner up
        while (cursor.getUp()!=Border.NOTHING){
            result.add(cursor);
            cursor=grid[cursor.getCoords().get(0)][cursor.getCoords().get(1)-1];
        }

        //from top right corner to left
        while (cursor.getLeft()!=Border.NOTHING){
            result.add(cursor);
            cursor=grid[cursor.getCoords().get(0)-1][cursor.getCoords().get(1)];
        }
        result.add(cursor);

        return result;
    }

    public ArrayList<Square> getRoom(int x, int y){
        ArrayList<Square> result = new ArrayList<>();

        //go all the way right
        while (getSquare(x,y).getRight()!=Border.NOTHING){
            x++;
        }

        //go all the way down
        while (getSquare(x,y).getDown()!=Border.NOTHING){
            y++;
        }

        //from bottom right corner up
        while (getSquare(x,y).getUp()!=Border.NOTHING){
            result.add(getSquare(x,y));
            y--;
        }

        //from top right corner to left
        while (getSquare(x,y).getLeft()!=Border.NOTHING){
            result.add(getSquare(x,y));
            x--;
        }

        result.add(getSquare(x,y));

        return result;
    }
}
