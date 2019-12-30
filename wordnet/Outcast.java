/* https://coursera.cs.princeton.edu/algs4/assignments/wordnet/specification.php
 *  score: 100/100 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String result = "";
        int maxDist = 0;
        for (int i = 0; i < nouns.length; i++) {
            int distance = 0;
            for (int j = 0; j < nouns.length; j++) {
                int dist = wordNet.distance(nouns[i], nouns[j]);
                if (i != j) {
                    distance += dist;
                }
                if (dist == -1) {
                    distance = Integer.MAX_VALUE;
                    break;
                }
            }
            if (distance > maxDist) {
                maxDist = distance;
                result = nouns[i];
            }
        }
        return result;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
