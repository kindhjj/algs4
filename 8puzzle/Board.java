/* https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 * score: 100/100 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {

    private final int[][] tiles;
    private final int boardSize;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = copyTiles(tiles);
        boardSize = tiles.length;
    }

    // string representation of this board
    public String toString() {
        String str = String.valueOf(boardSize);
        str = str.concat("\n");
        for (int[] rowTiles : tiles) {
            for (int tl : rowTiles)
                str = str.concat(" " + tl);
            str = str.concat("\n");
        }
        return str;
    }

    // board dimension n
    public int dimension() {
        return boardSize;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0, count = 1;
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (tiles[i][j] != count++)
                    hamming++;
        return hamming - 1;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0, count = 1;
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                manhattan = manhattan + distance(tiles[i][j], count++);
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        int count = 1;
        final int endPoint = boardSize * boardSize;
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (count < endPoint && tiles[i][j] != count++) return false;
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (boardSize != that.dimension()) return false;
        return Arrays.deepEquals(this.tiles, that.tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> neiborBoards = new ArrayList<>();
        int[][] neiborTiles;
        int row = 0, col = 0;
        while (true) {
            if (col == boardSize) {
                row++;
                col = 0;
            }
            if (tiles[row][col++] == 0) {
                col--;
                break;
            }
        }
        if (row > 0) {
            neiborTiles = copyTiles(this.tiles);
            swichTile(neiborTiles, row, col, row - 1, col);
            neiborBoards.add(new Board(neiborTiles));
        }
        if (row < boardSize - 1) {
            neiborTiles = copyTiles(this.tiles);
            swichTile(neiborTiles, row, col, row + 1, col);
            neiborBoards.add(new Board(neiborTiles));
        }
        if (col > 0) {
            neiborTiles = copyTiles(this.tiles);
            swichTile(neiborTiles, row, col, row, col - 1);
            neiborBoards.add(new Board(neiborTiles));
        }
        if (col < boardSize - 1) {
            neiborTiles = copyTiles(this.tiles);
            swichTile(neiborTiles, row, col, row, col + 1);
            neiborBoards.add(new Board(neiborTiles));
        }
        return neiborBoards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinTiles = copyTiles(this.tiles);
        if (tiles[0][0] == 0 || tiles[0][1] == 0) swichTile(twinTiles, 1, 0, 1, 1);
        else swichTile(twinTiles, 0, 0, 0, 1);
        return new Board(twinTiles);
    }

    // calculate the distance between 2 points
    private int distance(int pointA, int pointB) {
        if (pointA == 0 || pointB == 0) return 0;
        int lo, hi;
        if (pointB > pointA) {
            lo = pointA - 1;
            hi = pointB - 1;
        }
        else {
            lo = pointB - 1;
            hi = pointA - 1;
        }
        int verticalDist = hi / boardSize - lo / boardSize;
        int horizontalDist = Math.abs(hi % boardSize - lo % boardSize);
        return verticalDist + horizontalDist;
    }

    private void swichTile(int[][] tl, int row1, int col1, int row2, int col2) {
        int temp = tl[row1][col1];
        tl[row1][col1] = tl[row2][col2];
        tl[row2][col2] = temp;
    }

    private int[][] copyTiles(int[][] tileArray) {
        int arrLength = tileArray.length;
        int[][] newTiles = new int[arrLength][arrLength];
        for (int i = 0; i < arrLength; i++)
            newTiles[i] = tileArray[i].clone();
        return newTiles;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tilesIn = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tilesIn[i][j] = in.readInt();
                }
            }
            Board board = new Board(tilesIn);
            StdOut.print(board.toString());
            StdOut.println(board.dimension());
            StdOut.println(board.hamming());
            StdOut.println(board.manhattan());
            for (Board nb : board.neighbors())
                StdOut.print(nb.toString());
            StdOut.print(board.twin().toString());
        }
    }
}
