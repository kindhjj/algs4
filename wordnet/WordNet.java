/* https://coursera.cs.princeton.edu/algs4/assignments/wordnet/specification.php
 *  score: 100/100 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class WordNet {

    private final SAP wnSAP;
    // private Stack<String>[] synsetsNumToStr;
    private final HashMap<Integer, Queue<String>> synsetsNumToStr;
    private final HashMap<String, Queue<Integer>> synsetsStrToNum;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        In synsetsFile = new In(synsets);
        synsetsNumToStr = new HashMap<>();
        synsetsStrToNum = new HashMap<>();
        while (!synsetsFile.isEmpty()) {
            String read = synsetsFile.readLine();
            String[] item = read.split(",");
            int id = Integer.parseInt(item[0]);
            String[] str = item[1].split(" ");
            Queue<String> strStack = new Queue<>();
            for (String ss : str) {
                strStack.enqueue(ss);
                Queue<Integer> si;
                if (!synsetsStrToNum.containsKey(ss)) {
                    si = new Queue<>();
                    si.enqueue(id);
                    synsetsStrToNum.put(ss, si);
                }
                else synsetsStrToNum.get(ss).enqueue(id);
            }
            synsetsNumToStr.put(id, strStack);
        }
        In hypernymsFile = new In(hypernyms);
        Digraph wnDigraph = new Digraph(synsetsNumToStr.size());
        while (!hypernymsFile.isEmpty()) {
            String[] hypID = hypernymsFile.readLine().split(",");
            int rootID = Integer.parseInt(hypID[0]);
            for (int i = 1; i < hypID.length; i++)
                wnDigraph.addEdge(rootID, Integer.parseInt(hypID[i]));
        }
        Stack<Integer> root = new Stack<>();
        for (int v = 0; v < wnDigraph.V(); v++)
            if (wnDigraph.outdegree(v) == 0) {
                root.push(v);
            }
        if (root.size() > 1) throw new IllegalArgumentException("More than 1 roots in DAG");
        DirectedCycle dcGraph = new DirectedCycle(wnDigraph);
        if (dcGraph.hasCycle()) throw new IllegalArgumentException("Cycles detected in the graph");
        wnSAP = new SAP(wnDigraph);
    }


    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Stack<String> iter = new Stack<>();
        for (String ss : synsetsStrToNum.keySet())
            iter.push(ss);
        return iter;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return synsetsStrToNum.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkValid(nounA, nounB);
        Queue<Integer> pA = synsetsStrToNum.get(nounA);
        Queue<Integer> pB = synsetsStrToNum.get(nounB);
        return wnSAP.length(pA, pB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkValid(nounA, nounB);
        Queue<Integer> pA = synsetsStrToNum.get(nounA);
        Queue<Integer> pB = synsetsStrToNum.get(nounB);
        int common = wnSAP.ancestor(pA, pB);
        if (common == -1) return null;
        String result = "";
        for (String str : synsetsNumToStr.get(common))
            result = result.concat(str + " ");
        return result.substring(0, result.length() - 1);
    }

    private void checkValid(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();
            int length = wordnet.distance(v, w);
            String ancestor = wordnet.sap(v, w);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }


}
