/* https://coursera.cs.princeton.edu/algs4/assignments/boggle/specification.php
*  Dependency: BoggleBoard.java, algs4.jar
*  score: 100/100 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BoggleSolver {

    private final AugmentedTrieST dictTrie;
    private final String[] dict;
    private static final int R = 26;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (Assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dictTrie = new AugmentedTrieST();
        dict = dictionary.clone();
        for (int w = 0; w < dictionary.length; w++)
            dictTrie.put(dict[w], w);
    }

    private class AugmentedTrieST {

        private Node root;

        private class Node {
            private int val = -1;
            private Node[] next = new Node[R];
        }

        private AugmentedTrieST() {
        }

        private void put(String key, int val) {
            if (key == null) throw new IllegalArgumentException("first argument to put() is null");
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, int val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                x.val = val;
                return x;
            }
            int c = key.charAt(d) - 'A';
            x.next[c] = put(x.next[c], key, val, d + 1);
            return x;
        }

        private Node getRootNode(char singleChar) {
            return root.next[singleChar - 'A'];
        }

        private Node getNextNode(Node x, char singleChar) {
            if (x == null) return null;
            return x.next[singleChar - 'A'];
        }

        private boolean get(String query) {
            return get(root, query, 0);
        }

        private boolean get(Node x, String query, int d) {
            if (x == null) return false;
            if (query.length() == d) {
                if (x.val != -1)
                    return true;
                return false;
            }
            return get(x.next[query.charAt(d) - 'A'], query, d + 1);
        }
    }

    private class Pair {
        private final int i;
        private final AugmentedTrieST.Node node;

        private Pair(int ii, AugmentedTrieST.Node nn) {
            i = ii;
            node = nn;
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException();
        SET<String> validWords = new SET<>();
        int maxRow = board.rows();
        int maxCol = board.cols();
        int maxPos = maxCol * maxRow;
        final char[] boardChar = new char[maxPos];
        for (int i = 0; i < maxRow; i++)
            for (int j = 0; j < maxCol; j++)
                boardChar[i * maxCol + j] = board.getLetter(i, j);
        HashMap<Integer, Bag<Integer>> adjacentHM = new HashMap<>();
        for (int i = 0; i < maxPos; i++)
            adjacentHM.put(i, getAdjacent(i, maxRow, maxCol));
        boolean[] status;
        for (int p = 0; p < maxPos; p++) {
            char initLetter = boardChar[p];
            Pair newPair;
            if (initLetter == 'Q') {
                newPair = new Pair(p, dictTrie.getNextNode(dictTrie.getRootNode(initLetter), 'U'));
            }
            else
                newPair = new Pair(p, dictTrie.getRootNode(initLetter));
            status = new boolean[maxPos];
            traverse(newPair, validWords, status, boardChar, adjacentHM);
        }
        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (Assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException();
        if (!dictTrie.get(word)) return 0;
        int len = word.length();
        if (len < 3) return 0;
        if (len < 5) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 5;
        else return 11;
    }

    private void traverse(Pair pair, SET<String> validWords, boolean[] status, char[] boardChar,
                          HashMap<Integer, Bag<Integer>> adjacentHM) {
        int popInt = pair.i;
        AugmentedTrieST.Node popNode = pair.node;
        if (popNode == null) return;
        status[popInt] = true;
        if (popNode.val != -1 && dict[popNode.val].length() > 2)
            validWords.add(dict[popNode.val]);
        for (int adj : adjacentHM.get(popInt)) {
            if (!status[adj]) {
                char letter = boardChar[adj];
                Pair newPair;
                if (letter == 'Q') {
                    AugmentedTrieST.Node tempNode = dictTrie.getNextNode(popNode, letter);
                    newPair = new Pair(adj, dictTrie.getNextNode(tempNode, 'U'));
                }
                else
                    newPair = new Pair(adj, dictTrie.getNextNode(popNode, letter));
                traverse(newPair, validWords, status, boardChar, adjacentHM);
            }
        }
        status[popInt] = false;
    }

    private Bag<Integer> getAdjacent(int pos, int maxRow, int maxCol) {
        Bag<Integer> positionSet = new Bag<>();
        int r = pos / maxCol;
        int c = pos % maxCol;
        if (r != 0)
            positionSet.add(pos - maxCol);
        if (r != maxRow - 1)
            positionSet.add(pos + maxCol);
        if (c != 0)
            positionSet.add(pos - 1);
        if (c != maxCol - 1)
            positionSet.add(pos + 1);
        if (r != 0 && c != 0)
            positionSet.add(pos - maxCol - 1);
        if (r != 0 && c != maxCol - 1)
            positionSet.add(pos - maxCol + 1);
        if (r != maxRow - 1 && c != 0)
            positionSet.add(pos + maxCol - 1);
        if (r != maxRow - 1 && c != maxCol - 1)
            positionSet.add(pos + maxCol + 1);
        return positionSet;
    }

    public static void main(String[] args) {

        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);

    }
}
