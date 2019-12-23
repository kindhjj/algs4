/* *****************************************************************************
 * https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 * Score: 97/100
 * Dependencies: Point2D, RectHV, StdDraw, KdTreeGenerator
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class KdTree {

    private treeNode kdRoot;
    private int count;
    private double distance;

    private class treeNode {
        private final Point2D key;
        private final RectHV rec;
        private treeNode left = null;
        private treeNode right = null;
        private final boolean isX;

        public treeNode(Point2D k, boolean coor, double lx, double hx, double ly, double hy) {
            key = k;
            isX = coor;
            rec = new RectHV(lx, ly, hx, hy);
        }

    }

    // construct an empty set of points
    public KdTree() {
        kdRoot = null;
        count = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return count == 0;
    }


    // number of points in the set
    public int size() {
        return count;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        kdRoot = insertNote(p, kdRoot, true, 0.0, 1.0, 0.0, 1.0);
    }

    private treeNode insertNote(Point2D p, treeNode thisNote, boolean coordinate, double lx,
                                double hx, double ly, double hy) {
        if (thisNote == null) {
            count++;
            return new treeNode(p, coordinate, lx, hx, ly, hy);
        }
        if (p.equals(thisNote.key)) return thisNote;
        if (thisNote.isX) {
            if (p.x() < thisNote.key.x())
                thisNote.left = insertNote(p, thisNote.left, !thisNote.isX, lx, thisNote.key.x(),
                                           ly, hy);
            else thisNote.right = insertNote(p, thisNote.right, !thisNote.isX, thisNote.key.x(), hx,
                                             ly, hy);
        }
        else {
            if (p.y() < thisNote.key.y())
                thisNote.left = insertNote(p, thisNote.left, !thisNote.isX, lx, hx, ly,
                                           thisNote.key.y());
            else thisNote.right = insertNote(p, thisNote.right, !thisNote.isX, lx, hx,
                                             thisNote.key.y(), hy);
        }
        return thisNote;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return findNote(p, kdRoot);
    }

    private boolean findNote(Point2D p, treeNode parentNote) {
        if (parentNote == null) return false;
        if (p.equals(parentNote.key)) return true;
        if (parentNote.isX) {
            if (p.x() < parentNote.key.x())
                return findNote(p, parentNote.left);
            else return findNote(p, parentNote.right);
        }
        else {
            if (p.y() < parentNote.key.y())
                return findNote(p, parentNote.left);
            else return findNote(p, parentNote.right);
        }
    }

    // draw all points to standard draw
    public void draw() {
        if (count != 0)
            subDraw(kdRoot);
    }

    private void subDraw(treeNode tn) {
        if (tn == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        tn.key.draw();
        StdDraw.setPenRadius();
        if (tn.isX) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(tn.key.x(), tn.rec.ymin(), tn.key.x(), tn.rec.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(tn.rec.xmin(), tn.key.y(), tn.rec.xmax(), tn.key.y());
        }
        subDraw(tn.left);
        subDraw(tn.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        ArrayList<Point2D> pointResult = new ArrayList<>();
        findPoint(rect, kdRoot, pointResult);
        return pointResult;
    }

    private void findPoint(RectHV rect, treeNode thisNode, ArrayList<Point2D> pointResult) {
        if (thisNode == null) return;
        if (rect.contains(thisNode.key)) pointResult.add(thisNode.key);
        if (thisNode.left != null && rect.intersects(thisNode.left.rec))
            findPoint(rect, thisNode.left, pointResult);
        if (thisNode.right != null && rect.intersects(thisNode.right.rec))
            findPoint(rect, thisNode.right, pointResult);
    }


    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (count == 0) return null;
        Point2D tempPoint = kdRoot.key;
        distance = 2.0;
        return findNearest(p, tempPoint, kdRoot);
    }

    private Point2D findNearest(Point2D p, Point2D tempPoint, treeNode thisNode) {
        if (thisNode.key.distanceSquaredTo(p) < distance) {
            tempPoint = thisNode.key;
            distance = thisNode.key.distanceSquaredTo(p);
        }
        if (thisNode.left == null && thisNode.right == null) return tempPoint;
        if (thisNode.left == null) {
            if (thisNode.right.rec.distanceSquaredTo(p) < distance)
                tempPoint = findNearest(p, tempPoint, thisNode.right);
        }
        else if (thisNode.right == null) {
            if (thisNode.left.rec.distanceSquaredTo(p) < distance)
                tempPoint = findNearest(p, tempPoint, thisNode.left);
        }
        else {
            if (thisNode.left.rec.contains(p)) {
                tempPoint = findNearest(p, tempPoint, thisNode.left);
                if (thisNode.right.rec.distanceSquaredTo(p) < distance)
                    tempPoint = findNearest(p, tempPoint, thisNode.right);
            }
            else {
                if (thisNode.right.rec.distanceSquaredTo(p) < distance)
                    tempPoint = findNearest(p, tempPoint, thisNode.right);
                if (thisNode.left.rec.distanceSquaredTo(p) < distance)
                    tempPoint = findNearest(p, tempPoint, thisNode.left);
            }
        }
        return tempPoint;
    }

    public static void main(String[] args) {

        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        StdDraw.clear();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        kdtree.draw();
        StdDraw.show();

        Point2D query = KdTreeGenerator.generatePoint();
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        Point2D nearPoint = kdtree.nearest(query);
        nearPoint.draw();
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.line(query.x(), query.y(), nearPoint.x(), nearPoint.y());
        StdOut.printf("%8.6f %8.6f\n", query.x(), query.y());
        StdOut.printf("%8.6f %8.6f\n", nearPoint.x(), nearPoint.y());

    }
}
