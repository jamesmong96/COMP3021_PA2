package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.Exceptions.InvalidMapException;
import model.Map.Map;
import model.Map.Occupant.Crate;
import model.Map.Occupant.Player;
import model.Map.Occupiable.DestTile;
import model.Map.Occupiable.Occupiable;
import model.Map.Wall;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A class that loads, stores, modifies, and keeps track of the game map win/deadlock condition. Also keeps tracks
 * of information about this current level, e.g. how many moves the player has made.
 */
public class GameLevel {

    private final IntegerProperty numPushes = new SimpleIntegerProperty(0);
    private Map map;

    public IntegerProperty numPushesProperty() {
        return numPushes;
    }

    public Map getMap() {
        return map;
    }

    /**
     * Loads and reads the map line by line, instantiates and initializes map
     *
     * @param filename the map text filename
     * @throws InvalidMapException when the map is invalid
     */
    public void loadMap(String filename) throws InvalidMapException {
        File f = new File(filename);
        try (Scanner reader = new Scanner(f)) {
            int numRows = reader.nextInt();
            int numCols = reader.nextInt();
            reader.nextLine();

            char[][] rep = new char[numRows][numCols];
            for (int r = 0; r < numRows; r++) {
                String row = reader.nextLine();
                for (int c = 0; c < numCols; c++) {
                    rep[r][c] = row.charAt(c);
                }
            }

            map = new Map();
            map.initialize(numRows, numCols, rep);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Whether or not the win condition has been satisfied
     */
    public boolean isWin() {
        return map.getDestTiles().stream().allMatch(DestTile::isCompleted);
    }

    /**
     * When no crates can be moved but the game is not won, then deadlock has occurred
     *
     * @return Whether deadlock has occurred
     */
    //helper function for isDeadlocked()
    //return true if the cell contains a player
    public boolean isPlayer(int r, int c) {

        if (map.getCells()[r][c] instanceof Wall)
            return false;

        if (!((Occupiable) map.getCells()[r][c]).getOccupant().isPresent())
            return false;

        if (((Occupiable) map.getCells()[r][c]).getOccupant().get() instanceof Player)
            return true;
        else return false;

    }

    public boolean isDeadlocked() {
        //--------------------------original code by TA
        /*for (Crate c : map.getCrates()) {
            boolean canMoveLR = map.isOccupiableAndNotOccupiedWithCrate(c.getR(), c.getC() - 1)
                    && map.isOccupiableAndNotOccupiedWithCrate(c.getR(), c.getC() + 1);
            boolean canMoveUD = map.isOccupiableAndNotOccupiedWithCrate(c.getR() - 1, c.getC()) &&
                    map.isOccupiableAndNotOccupiedWithCrate(c.getR() + 1, c.getC());
            if (canMoveLR || canMoveUD)
                return false;
        }
        return true;*/


        //problematic
        int dead = 0;

        for (int i = 0; i < map.getCrates().size(); i++) {

            //reset flag
            dead = 0;

            int r = map.getCrates().get(i).getR();
            int c = map.getCrates().get(i).getC();

            //check for it is DestTile
            if (map.getCells()[r][c] instanceof DestTile) {
                continue;
            }
            //check all four directions
            if (map.isOccupiableAndNotOccupiedWithCrate(r + 1, c)) {
                if (map.isOccupiableAndNotOccupiedWithCrate(r - 1, c)) {
                    continue;
                }
                else if (isPlayer(r - 1, c)) {
                    continue;
                }
                else dead++;
            }
            else dead++;

            if (map.isOccupiableAndNotOccupiedWithCrate(r - 1, c)) {
                if (isPlayer(r + 1, c)) {
                    continue;
                }
                else dead++;
            }
            else dead++;

            if (map.isOccupiableAndNotOccupiedWithCrate(r, c + 1)) {
                if (map.isOccupiableAndNotOccupiedWithCrate(r, c - 1)) {
                    continue;
                }
                else if (isPlayer(r, c - 1)) {
                    continue;
                }
                else dead++;
            }
            else dead++;

            if (map.isOccupiableAndNotOccupiedWithCrate(r, c - 1)) {
                if (isPlayer(r, c + 1)) {
                    continue;
                }
                else dead++;
            }
            else dead++;

            //if a crate is deadlocked in all four directions, dead in all four cases
            if (dead == 4)
            {
                return true;
            }
        }

        return false; // You may also modify this line.
    }

    /**
     * @param c The char corresponding to a move from the user
     *          w: up
     *          a: left
     *          s: down
     *          d: right
     * @return Whether or not the move was successful
     */
    public boolean makeMove(char c) {
        boolean madeMove = false;
        switch (c) {
            case 'w':
                madeMove = map.movePlayer(Map.Direction.UP);
                break;
            case 'a':
                madeMove = map.movePlayer(Map.Direction.LEFT);
                break;
            case 's':
                madeMove = map.movePlayer(Map.Direction.DOWN);
                break;
            case 'd':
                madeMove = map.movePlayer(Map.Direction.RIGHT);
                break;
        }
        if (madeMove) {
            numPushes.setValue(numPushes.getValue() + 1);
        }
        return madeMove;
    }
}
