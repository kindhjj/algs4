/*
https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
Total score: 97/100
 */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

// array method
public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] randQueue;
    private int n;
    private int capacity;

    // construct an empty randomized queue
    public RandomizedQueue() {
        capacity = 5;
        randQueue = (Item[]) new Object[capacity];
        n = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        if (n == capacity - 1) {
            capacity = capacity * 2;
            Item[] newRQ = (Item[]) new Object[capacity];
            int i = 0;
            for (Item num : randQueue)
                newRQ[i++] = num;
            randQueue = newRQ;
        }
        randQueue[n++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (n == 0)
            throw new java.util.NoSuchElementException();
        int randn = StdRandom.uniform(n);
        Item pop = randQueue[randn];
        randQueue[randn] = randQueue[n - 1];
        randQueue[n - 1] = null;
        n--;
        if (n <= capacity / 4) {
            capacity = capacity / 2;
            Item[] newRQ = (Item[]) new Object[capacity];
            for (int i = 0; i < n; i++)
                newRQ[i] = randQueue[i];
            randQueue = newRQ;
        }
        return pop;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (n == 0)
            throw new java.util.NoSuchElementException();
        int randn = StdRandom.uniform(n);
        return randQueue[randn];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandQIterator();
    }

    // construct an iterator
    private class RandQIterator implements Iterator<Item> {
        private int count;
        private Item current;

        RandQIterator() {
            if (n > 0) {
                shuffle();
                current = randQueue[0];
            }
            else
                current = null;
            count = 0;
        }

        private void shuffle() {
            int i = n;
            while (i > 0) {
                int j = StdRandom.uniform(i--);
                Item swap = randQueue[j];
                randQueue[j] = randQueue[i];
                randQueue[i] = swap;
            }
        }

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext())
                throw new java.util.NoSuchElementException();
            Item item = current;
            current = randQueue[++count];
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> rdq = new RandomizedQueue<Integer>();
        StdOut.println(rdq.isEmpty());
        rdq.enqueue(5);
        rdq.enqueue(3);
        rdq.enqueue(7);
        rdq.enqueue(12);
        rdq.enqueue(18);
        StdOut.println(rdq.isEmpty());
        StdOut.println(rdq.dequeue());
        StdOut.println(rdq.dequeue());
        StdOut.println(rdq.sample());
        for (int d : rdq)
            StdOut.print(d);
    }

}