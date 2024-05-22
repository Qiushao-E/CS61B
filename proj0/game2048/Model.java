package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author 41p
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */

    public void movetilehelper (int c ,int rfrom ,int rto){
        Tile t = board.tile(c,rfrom);
        board.move(c,rto,t);
    }
    public boolean dealonec (int c){
        boolean ifchanged = false;
        if (board.tile(c,3) != null){
            if (board.tile(c,2) != null){
                if (board.tile(c,1) != null){
                    if (board.tile(c,0) != null){
                        if (board.tile(c,3).value() == board.tile(c,2).value()){
                            score += 2*board.tile(c,3).value();
                            if (board.tile(c,1).value() == board.tile(c,0).value()){
                                score += 2*board.tile(c,1).value();
                                movetilehelper(c,2,3);
                                movetilehelper(c,0,2);
                                movetilehelper(c,1,2);
                            }
                            else {
                                movetilehelper(c,2,3);
                                movetilehelper(c,1,2);
                                movetilehelper(c,0,1);
                            }
                            ifchanged = true;
                        }
                        else if (board.tile(c,1).value() == board.tile(c,2).value()){
                            score += 2*board.tile(c,2).value();
                            movetilehelper(c,1,2);
                            movetilehelper(c,0,1);
                            ifchanged = true;
                        }
                        else if (board.tile(c,1).value() == board.tile(c,0).value()){
                            score += 2*board.tile(c,0).value();
                            movetilehelper(c,0,1);
                            ifchanged = true;
                        }
                        else
                            return false;

                    } /*1 1 1 1*/
                    else {
                        if (board.tile(c,3).value() == board.tile(c,2).value()){
                            score += 2*board.tile(c,3).value();
                            movetilehelper(c,2,3);
                            movetilehelper(c,1,2);
                            ifchanged = true;
                        }
                        else if (board.tile(c,2).value() == board.tile(c,1).value()){
                            score += 2*board.tile(c,2).value();
                            movetilehelper(c,1,2);
                            ifchanged = true;
                        }
                        else
                            return false;
                    } /*1 1 1 0*/
                } /*1 1 1 x*/
                else {
                    if (board.tile(c,0) != null){
                        if (board.tile(c,3).value() == board.tile(c,2).value()){
                            score += 2*board.tile(c,3).value();
                            movetilehelper(c,2,3);
                            movetilehelper(c,0,2);
                        }
                        else if (board.tile(c,2).value() == board.tile(c,0).value()) {
                            score += 2*board.tile(c,2).value();
                            movetilehelper(c,0,2);
                        }
                        else
                            movetilehelper(c,0,1);
                        ifchanged = true;
                    } /*1 1 0 1*/
                    else {
                        if (board.tile(c, 3).value() == board.tile(c, 2).value()){
                            score += 2*board.tile(c,3).value();
                            movetilehelper(c, 2, 3);
                            ifchanged = true;
                        }
                        else
                            return false;
                    } /*1 1 0 0*/
                } /*1 1 0 x*/
            } /*1 1 x x*/
            else {
                if (board.tile(c,1) != null){
                    if (board.tile(c,0) != null){
                        if (board.tile(c,3).value() == board.tile(c,1).value()){
                            score += 2*board.tile(c,3).value();
                            movetilehelper(c,1,3);
                            movetilehelper(c,0,2);
                        }
                        else if (board.tile(c,1).value() == board.tile(c,0).value()) {
                            score += 2*board.tile(c,1).value();
                            movetilehelper(c,1,2);
                            movetilehelper(c,0,2);
                        }
                        else {
                            movetilehelper(c,1,2);
                            movetilehelper(c,0,1);
                        }
                    } /*1 0 1 1*/
                    else {
                        if (board.tile(c,3).value() == board.tile(c,1).value()) {
                            score += 2*board.tile(c,3).value();
                            movetilehelper(c, 1, 3);
                        }
                        else
                            movetilehelper(c,1,2);
                    } /*1 0 1 0*/
                    ifchanged = true;
                } /*1 0 1 x*/
                else {
                    if (board.tile(c,0) != null){
                        if (board.tile(c,3).value() == board.tile(c,0).value()) {
                            score += 2*board.tile(c,3).value();
                            movetilehelper(c, 0, 3);
                        }
                        else
                            movetilehelper(c,0,2);
                        ifchanged = true;
                    } /*1 0 0 1*/
                    else {
                        return false;
                    } /*1 0 0 0*/
                } /*1 0 0 x*/
            } /*1 0 x x*/
        } /*1 x x x*/
        else if (board.tile(c,2) != null) {
            if (board.tile(c,1) != null){
                if (board.tile(c,0) != null){
                    if (board.tile(c,2).value() == board.tile(c,1).value()){
                        score += 2*board.tile(c,2).value();
                        movetilehelper(c,2,3);
                        movetilehelper(c,1,3);
                        movetilehelper(c,0,2);
                        ifchanged =true;
                    }
                    else if (board.tile(c,0).value() == board.tile(c,1).value()){
                        score += 2*board.tile(c,1).value();
                        movetilehelper(c,2,3);
                        movetilehelper(c,1,2);
                        movetilehelper(c,0,2);
                        ifchanged = true;
                    }
                    else {
                        movetilehelper(c,2,3);
                        movetilehelper(c,1,2);
                        movetilehelper(c,0,1);
                        ifchanged = true;
                    }
                } /*0 1 1 1*/
                else {
                    if (board.tile(c,2).value() == board.tile(c,1).value()){
                        score += 2*board.tile(c,2).value();
                        movetilehelper(c,2,3);
                        movetilehelper(c,1,3);
                    }
                    else {
                        movetilehelper(c,2,3);
                        movetilehelper(c,1,2);
                    }
                    ifchanged = true;
                } /*0 1 1 0*/
            } /*0 1 1 x*/
            else {
                if (board.tile(c,0) != null){
                    if(board.tile(c,2).value() == board.tile(c,0).value()){
                        score += 2*board.tile(c,2).value();
                        movetilehelper(c,2,3);
                        movetilehelper(c,0,3);
                    }
                    else {
                        movetilehelper(c,2,3);
                        movetilehelper(c,0,2);
                    }
                } /*0 1 0 1*/
                else {
                    movetilehelper(c,2,3);
                } /*0 1 0 0*/
                ifchanged = true;
            } /*0 1 0 x*/
        } /*0 1 x x*/
        else if (board.tile(c,1) != null) {
            if (board.tile(c,0) != null){
                if (board.tile(c,1).value() == board.tile(c,0).value()){
                    score += 2*board.tile(c,1).value();
                    movetilehelper(c,1,3);
                    movetilehelper(c,0,3);
                }
                else {
                    movetilehelper(c,1,3);
                    movetilehelper(c,0,2);
                }
            } /*0 0 1 1*/
            else {
                movetilehelper(c,1,3);
                ifchanged = true;
            } /*0 0 1 0*/
        } /*0 0 1 x*/
        else if (board.tile(c,0) != null) {
            movetilehelper(c,0,3);
            ifchanged = true;
        } /*0 0 0 1*/
        return ifchanged;
    }
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        board.setViewingPerspective(side);
        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        for (int c = 0; c < board.size(); c++){
            if (dealonec(c)){
                changed =true;
            }
        }
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // DONE: Fill in this function.
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (b.tile(i,j) == null)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // DONE: Fill in this function.
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (b.tile(i,j) != null){
                    if (b.tile(i,j).value() == MAX_PIECE)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // DONE: Fill in this function.
        if (emptySpaceExists(b)||maxTileExists(b))
            return true;
        /*遍历所有瓷砖，并分情况判断*/
        for(int i = 0; i < b.size(); i++){
            for(int j = 0; j<b.size(); j++){
                /*四个角*/
                if (i+j == 0){
                    if (b.tile(i,j).value() == b.tile(1,0).value() ||
                            b.tile(i,j).value() == b.tile(1,0).value())
                        return true;
                }
                else if (i == 0 && j == b.size()-1){
                    if (b.tile(i,j).value() == b.tile(1,j).value() ||
                            b.tile(i,j).value() == b.tile(0,j-1).value())
                        return true;
                }
                else if (j == 0 && i == b.size()-1){
                    if (b.tile(i,j).value() == b.tile(i,1).value() ||
                            b.tile(i,j).value() == b.tile(i-1,0).value())
                        return true;
                }
                else if (i + j + 2 == 2*b.size()){
                    if (b.tile(i,j).value() == b.tile(i-1,j).value() ||
                            b.tile(i,j).value() == b.tile(i,j-1).value())
                        return true;
                }
                /*四条边*/
                else if (i == 0 || i == b.size()-1) {
                    if(b.tile(i,j).value() == b.tile(i,j+1).value() ||
                            b.tile(i,j).value() == b.tile(i,j-1).value())
                        return true;
                }
                else if (j == 0 || j == b.size()-1) {
                    if(b.tile(i,j).value() == b.tile(i+1,j).value() ||
                            b.tile(i,j).value() == b.tile(i-1,j).value())
                        return true;
                }
                /*大肚皮*/
                else {
                    if(b.tile(i,j).value() == b.tile(i+1,j).value() ||
                            b.tile(i,j).value() == b.tile(i-1,j).value() ||
                            b.tile(i,j).value() == b.tile(i,j+1).value() ||
                            b.tile(i,j).value() == b.tile(i,j-1).value())
                        return true;
                }
            }
        }
        return false;
    }


    @Override
    /* Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /* Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /* Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
