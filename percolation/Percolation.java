/*
https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php
Score 95/100
*/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[] states;
    private final int side;
    private int openNum;
    private final WeightedQuickUnionUF sites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n)
    // 0-close, 1-open
    {
        if (n <= 0)
            throw new IllegalArgumentException();
        side = n;
        sites = new WeightedQuickUnionUF(n * n + 2);
        states = new boolean[n * n + 2];
        openNum = 0;
        for (int i = 0; i < n * n; i++)
            states[i] = false;
        // beginning cell
        states[n * n] = true;
        // end cell
        states[n * n + 1] = true;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkBound(row, col);
        if (isOpen(row, col))
            return;
        else {
            int curCell = convertToSimgleDim(row, col);
            states[curCell] = true;
            // connect to the beginning cell
            if (row == 1)
                sites.union(curCell, side * side);
            // connect to the end cell
            if (row == side)
                sites.union(curCell, side * side + 1);
            // border condition
            if (row != 1 && isOpen(row - 1, col))
                sites.union(curCell, convertToSimgleDim(row - 1, col));
            if (row != side && isOpen(row + 1, col))
                sites.union(curCell, convertToSimgleDim(row + 1, col));
            if (col != 1 && isOpen(row, col - 1))
                sites.union(curCell, convertToSimgleDim(row, col - 1));
            if (col != side && isOpen(row, col + 1))
                sites.union(curCell, convertToSimgleDim(row, col + 1));
        }
        openNum++;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkBound(row, col);
        return states[convertToSimgleDim(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkBound(row, col);
        return isOpen(row, col) && (sites.find(convertToSimgleDim(row, col)) == sites.find(side * side));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openNum;
    }

    // does the system percolate?
    public boolean percolates() {
        return sites.find(side * side) == sites.find(side * side + 1);
    }

    // check whether the args out of bound
    private void checkBound(int row, int col) {
        if (row < 1 || col < 1 || row > side || col > side)
            throw new IllegalArgumentException();
    }

    // convert 2 dimensions to 1
    private int convertToSimgleDim(int row, int col) {
        return (row - 1) * side + col - 1;
    }

    // test client (optional)
    // public static void main(String[] args)
}