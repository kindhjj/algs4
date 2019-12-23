/*
https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php
Score 95/100
*/

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private final int[] openSitesNum;
    private final int scale;
    private final static double confInt_95 = 1.96;
    private final double mean;
    private final double stdDev;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException();
        openSitesNum = new int[trials];
        scale = n * n;
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            int row;
            int col;
            while (!perc.percolates()) {
                row = StdRandom.uniform(n) + 1;
                col = StdRandom.uniform(n) + 1;
                perc.open(row, col);
            }
            openSitesNum[i] = perc.numberOfOpenSites();
        }
        mean = StdStats.mean(openSitesNum) / scale;
        stdDev = StdStats.stddev(openSitesNum) / scale;
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stdDev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - confInt_95 * stdDev / Math.sqrt(openSitesNum.length);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + confInt_95 * stdDev / Math.sqrt(openSitesNum.length);
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(n, t);
        StdOut.println("mean                    = " + ps.mean());
        StdOut.println("std dev                 = " + ps.stddev());
        StdOut.println("95% confidence interval = " + ps.confidenceLo() + ", " + ps.confidenceHi());
    }

}