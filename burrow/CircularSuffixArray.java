/* https://coursera.cs.princeton.edu/algs4/assignments/burrows/faq.php
 *  score: 100/100 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {

    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        String str = s;
        char[] cstr = str.toCharArray();
        Suffix[] suffixesArray = new Suffix[cstr.length];
        char[] charList = new char[cstr.length * 2];
        System.arraycopy(cstr, 0, charList, 0, cstr.length);
        System.arraycopy(cstr, 0, charList, cstr.length, cstr.length);
        for (int i = 0; i < cstr.length; i++)
            suffixesArray[i] = new Suffix(charList, i, cstr.length);
        Arrays.sort(suffixesArray);
        this.index = new int[cstr.length];
        for (int i = 0; i < cstr.length; i++)
            this.index[i] = suffixesArray[i].index;
    }

    // a nested class that store index of the suffixes and customize compare function to enable circular
    private static class Suffix implements Comparable<Suffix> {
        private final char[] text;
        private final int index;
        private final int charLength;

        private Suffix(char[] t, int i, int cl) {
            text = t;
            index = i;
            charLength = cl;
        }

        private char charAt(int i) {
            return text[index + i];
        }

        public int compareTo(Suffix that) {
            if (this == that) return 0;  // optimization
            for (int i = 0; i < charLength; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return +1;
            }
            return 0;
        }
    }

    // length of s
    public int length() {
        return index.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= index.length) throw new IllegalArgumentException();
        return index[i];
    }

    public static void main(String[] args) {

        In in = new In(args[0]);
        CircularSuffixArray csr = new CircularSuffixArray(in.readAll());
        for (int i : csr.index) {
            StdOut.print(i);
            StdOut.print(" ");
        }
    }
}
