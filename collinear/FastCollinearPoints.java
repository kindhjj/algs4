/* https://coursera.cs.princeton.edu/algs4/assignments/collinear/specification.php
 *  score:  95/100 */

import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;

public class FastCollinearPoints {

    private LinkedList<LineSegment> lineSeg;
    private int lineCount;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
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
        lineSeg = new LinkedList<LineSegment>();
        lineCount = 0;
        for (int p = 0; p < length - 3; p++) {
            Point[] auxpts = pts.clone();
            Arrays.sort(auxpts, auxpts[p].slopeOrder());
            int q = 1;
            while (q < length - 2) {
                double base = auxpts[q].slopeTo(auxpts[0]);
                double comp1 = auxpts[q + 1].slopeTo(auxpts[0]);
                double comp2 = auxpts[q + 2].slopeTo(auxpts[0]);
                if (comp1 == base && comp2 == base) {
                    LinkedList<Point> tmpLine = new LinkedList<>();
                    tmpLine.add(auxpts[0]);
                    for (int i = 0; i < 3; i++)
                        tmpLine.add(auxpts[q + i]);
                    q = q + 2;
                    while (q < length - 1
                            && auxpts[q + 1].slopeTo(auxpts[0]) == base)
                        tmpLine.add(auxpts[++q]);
                    LinkedList<Point> tmpSortedLine = (LinkedList<Point>) tmpLine.clone();
                    Collections.sort(tmpSortedLine);
                    boolean isNew = true;
                    if (!tmpLine.peekFirst().equals(tmpSortedLine.peekFirst()))
                        isNew = false;
                    if (isNew) {
                        lineSeg.addLast(new LineSegment(tmpLine.peekFirst(),
                                                        tmpLine.peekLast()));
                        lineCount++;
                    }
                }
                q++;
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineCount;
    }

    // the line segments
    public LineSegment[] segments() {
        return lineSeg.toArray(new LineSegment[lineCount]);
    }

}
