/* *****************************************************************************
 * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 * Score: 97/100
 * Dependencies: Point2D, RectHV, SET, StdDraw
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private final SET<Point2D> pointSet;

    // construct an empty set of points
    public PointSET() {
        pointSet = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointSet.isEmpty();
    }


    // number of points in the set
    public int size() {
        return pointSet.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        pointSet.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return pointSet.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : pointSet) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius();
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        SET<Point2D> resultSet = new SET<>();
        if (pointSet.isEmpty()) return resultSet;
        SET<Point2D> dupPointSet = new SET<>(pointSet);
        final Point2D lowerleft = new Point2D(rect.xmin(), rect.ymin());
        final Point2D upperright = new Point2D(rect.xmax(), rect.ymax());
        while (dupPointSet.max().compareTo(lowerleft) >= 0) {
            Point2D tempPoint = dupPointSet.ceiling(lowerleft);
            if (tempPoint.compareTo(upperright) > 0) break;
            if (tempPoint.x() >= lowerleft.x() && tempPoint.x() <= upperright.x())
                resultSet.add(tempPoint);
            dupPointSet.delete(tempPoint);
            if (dupPointSet.isEmpty()) break;
        }
        return resultSet;
    }


    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (pointSet.isEmpty()) return null;
        double segmentLength = -1.0;
        Point2D targetPoint = null;
        for (Point2D ps : pointSet) {
            double tempLength = ps.distanceSquaredTo(p);
            if (segmentLength == -1 || segmentLength > tempLength) {
                segmentLength = tempLength;
                targetPoint = ps;
            }
        }
        return targetPoint;
    }

}
