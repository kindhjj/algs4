/*
https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
Total score: 97/100
 */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> perm = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String read = StdIn.readString();
            perm.enqueue(read);
        }
        for (int i = 0; i < k; i++)
            StdOut.println(perm.dequeue());
    }
}
