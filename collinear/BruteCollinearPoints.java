/* https://coursera.cs.princeton.edu/algs4/assignments/collinear/specification.php
 *  score:  95/100 */

import java.util.Arrays;

public class BruteCollinearPoints {

    private LineSegment[] lineSeg;
    private int lineCount;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        final Point[] pts = points.clone();
        for (int i = 0; i < pts.length; i++)
            if (pts[i] == null) throw new IllegalArgumentException();
        Arrays.sort(pts);
        // check duplicates
        for (int i = 0; i < pts.length - 1; i++)
            if (pts[i].compareTo(pts[i + 1]) == 0) throw new IllegalArgumentException();
        // generate segments
        int length = pts.length;
        lineSeg = new LineSegment[length];
        lineCount = 0;
        for (int p = 0; p < length - 3; p++) {
            for (int q = p + 1; q < length - 2; q++) {
                double pqSlope = pts[q].slopeTo(pts[p]);
                for (int r = q + 1; r < length - 1; r++) {
                    double qrSlope = pts[r].slopeTo(pts[q]);
                    for (int s = r + 1; s < length; s++) {
                        double rsSlope = pts[s].slopeTo((pts[r]));
                        if (pqSlope == qrSlope && qrSlope == rsSlope) {
                            lineSeg[lineCount++] = new LineSegment(pts[p], pts[s]);
                            if (lineCount == lineSeg.length)
                                expandSegment();
                        }
                    }
                }
            }
        }
    }

    // expand the sequence length
    private void expandSegment() {
        LineSegment[] newlineSeq = new LineSegment[lineCount * 2];
        for (int i = 0; i < lineSeg.length; i++)
            newlineSeq[i] = lineSeg[i];
        lineSeg = newlineSeq;
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineCount;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOfRange(lineSeg, 0, lineCount);
    }
}
