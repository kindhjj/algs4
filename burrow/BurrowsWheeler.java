/* https://coursera.cs.princeton.edu/algs4/assignments/burrows/faq.php
 *  score: 100/100 */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

public class BurrowsWheeler {

    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String text = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(text);
        char[] t = new char[text.length()];
        for (int i = 0; i < text.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                t[i] = text.charAt(text.length() - 1);
            }
            else t[i] = text.charAt(csa.index(i) - 1);
        }
        for (char ch : t)
            BinaryStdOut.write(ch);
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] readChar = BinaryStdIn.readString().toCharArray();
        char[] sorted = new char[readChar.length];
        int[] next = new int[readChar.length];
        sortCharArrayAndNext(readChar, sorted, next);
        int count = 0;
        for (int i = first; count++ < readChar.length; i = next[i])
            BinaryStdOut.write(sorted[i]);
        BinaryStdOut.close();
    }

    // a nested class to store string indexes of each ascii char
    private static class AsciiInt {
        private final Queue<Integer> asc;

        private AsciiInt() {
            this.asc = new Queue<>();
        }
    }

    // sort the suffix string in order and calculate next[] array
    private static void sortCharArrayAndNext(char[] t, char[] sorted, int[] next) {
        AsciiInt[] ascii = new AsciiInt[R];
        for (int i = 0; i < t.length; i++) {
            if (ascii[t[i]] == null)
                ascii[t[i]] = new AsciiInt();
            ascii[t[i]].asc.enqueue(i);
        }
        int count = 0;
        for (int i = 0; i < R; i++)
            while (ascii[i]!=null && !ascii[i].asc.isEmpty()) {
                sorted[count] = (char) i;
                next[count++] = ascii[i].asc.dequeue();
            }
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-"))
            transform();
        else if (args.length > 0 && args[0].equals("+"))
            inverseTransform();
    }
}
