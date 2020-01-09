/* https://coursera.cs.princeton.edu/algs4/assignments/burrows/faq.php
*  score: 100/100 */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] charList = generateInitChar();
        while (!BinaryStdIn.isEmpty()) {
            char readChar = BinaryStdIn.readChar();
            char iter = 0;
            while (true) {
                if (charList[iter] == readChar) {
                    BinaryStdOut.write(iter);
                    charList = moveTofront(charList, iter);
                    break;
                }
                iter++;
            }
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] charList = generateInitChar();
        while (!BinaryStdIn.isEmpty()) {
            char readChar = BinaryStdIn.readChar();
            BinaryStdOut.write(charList[readChar]);
            charList = moveTofront(charList, readChar);
        }
        BinaryStdOut.close();
    }

    // generate an ascii array
    private static char[] generateInitChar() {
        char[] initChar = new char[R];
        for (int i = 0; i < R; i++)
            initChar[i] = (char) i;
        return initChar;
    }

    // perform move-to-from
    private static char[] moveTofront(char[] charList, char iter) {
        char[] tempChar = new char[R];
        tempChar[0] = charList[iter];
        System.arraycopy(charList, 0, tempChar, 1, iter);
        System.arraycopy(charList, iter + 1, tempChar, iter + 1, R - iter - 1);
        return tempChar;
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-"))
            encode();
        else if (args.length > 0 && args[0].equals("+"))
            decode();
    }
}
