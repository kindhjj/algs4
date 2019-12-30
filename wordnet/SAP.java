/* https://coursera.cs.princeton.edu/algs4/assignments/wordnet/specification.php
 *  score: 100/100 */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class SAP {

    private final Digraph digraph;

    // Key: [v,w], Value: [distance,common_ancester]
    private final HashMap<Long, int[]> storeVWint;
    private final HashMap<String, int[]> storeVWiterable;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        digraph = G;
        storeVWint = new HashMap<>();
        storeVWiterable = new HashMap<>();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        long tempHashKey = hashToLong(v, w);
        if (storeVWint.containsKey(tempHashKey))
            return storeVWint.get(tempHashKey)[0];
        BreadthFirstDirectedPaths vSearch = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wSearch = new BreadthFirstDirectedPaths(digraph, w);
        int[] found = findLength(vSearch, wSearch);
        storeVWint.put(tempHashKey, found);
        return found[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        long tempHashKey = hashToLong(v, w);
        if (storeVWint.containsKey(tempHashKey))
            return storeVWint.get(tempHashKey)[1];
        else length(v, w);
        return storeVWint.get(tempHashKey)[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkValid(v, w);
        String tempHashKey = hashToString(v, w);
        if (storeVWiterable.containsKey(tempHashKey))
            return storeVWiterable.get(tempHashKey)[0];
        BreadthFirstDirectedPaths vSearch = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wSearch = new BreadthFirstDirectedPaths(digraph, w);
        int[] found = findLength(vSearch, wSearch);
        storeVWiterable.put(tempHashKey, found);
        return found[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkValid(v, w);
        String tempHashKey = hashToString(v, w);
        if (storeVWiterable.containsKey(tempHashKey))
            return storeVWiterable.get(tempHashKey)[1];
        else {
            int dist = length(v, w);
            if (dist == -1) return -1;
        }
        return storeVWiterable.get(tempHashKey)[1];
    }

    // find shortest distance and common ancester and parse in int[distance,common_ancester]
    private int[] findLength(BreadthFirstDirectedPaths vSearch, BreadthFirstDirectedPaths wSearch) {
        int distance = Integer.MAX_VALUE;
        int common = -1;
        for (int vi = 0; vi < digraph.V(); vi++)
            if (vSearch.hasPathTo(vi) && wSearch.hasPathTo(vi)) {
                int d = vSearch.distTo(vi) + wSearch.distTo(vi);
                if (d < distance) {
                    distance = d;
                    common = vi;
                }
            }
        if (common != -1) return new int[] { distance, common };
        return new int[] { -1, -1 };
    }

    // Hash two integers to a unique id
    private long hashToLong(int n1, int n2) {
        long result = Math.min(n1, n2);
        result <<= 32;
        result += Math.max(n1, n2);
        return result;
    }

    // Hash two iterables to a unique id (String)
    private String hashToString(Iterable<Integer> i1, Iterable<Integer> i2) {
        String str1 = "";
        String str2 = "";
        int n1 = 0;
        int n2 = 0;
        for (int i : i1) {
            str1 = str1.concat(i + "+");
            n1++;
        }
        for (int i : i2) {
            str2 = str2.concat(i + "+");
            n2++;
        }
        String str;
        if (n1 < n2)
            str = str1.concat("+" + str2);
        else
            str = str2.concat("+" + str1);
        return str;

    }

    private void checkValid(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        for (Integer vv : v)
            if (vv == null) throw new IllegalArgumentException();
        for (Integer ww : w)
            if (ww == null) throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

}

