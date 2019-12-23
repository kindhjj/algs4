/*
https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
Total score: 97/100
 */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

// linked node method
public class Deque<Item> implements Iterable<Item> {

    // construct each node on a linked list
    private class Node {
        public Node prev;
        public Node next;
        private final Item item;

        public Node(Item item) {
            this.item = item;
            prev = null;
            next = null;
        }
    }

    private Node first;
    private Node last;
    private int n;

    // construct an empty deque
    public Deque() {
        first = null;
        last = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        Node newNode = new Node(item);
        if (first == null) {
            first = newNode;
            last = first;
        }
        else {
            newNode.next = first;
            first.prev = newNode;
            first = newNode;
        }
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null)
            throw new IllegalArgumentException();
        Node newNode = new Node(item);
        if (n==0) {
            first = newNode;
            last = first;
        }
        else {
            newNode.prev = last;
            last.next = newNode;
            last = newNode;
        }
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (n == 0)
            throw new java.util.NoSuchElementException();
        Node popNode = first;
        first = first.next;
        n--;
        if (!checkAndSetNull())
            first.prev = null;
        return popNode.item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (n == 0)
            throw new java.util.NoSuchElementException();
        Node popNode = last;
        last = last.prev;
        n--;
        if (!checkAndSetNull())
            last.next = null;
        return popNode.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // check whether the length is reduced to 0
    private boolean checkAndSetNull() {
        if (n == 0) {
            first = null;
            last = null;
            return true;
        }
        else return false;
    }

    // construct an iterator
    private class DequeIterator implements Iterator<Item> {
        private Node current;

        // constructor
        DequeIterator() {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext())
                throw new java.util.NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> dq = new Deque<Integer>();
        StdOut.println(dq.isEmpty());
        dq.addFirst(5);
        dq.addFirst(3);
        dq.addLast(7);
        dq.addLast(12);
        dq.addLast(18);
        StdOut.println(dq.isEmpty());
        StdOut.println(dq.removeFirst());
        StdOut.println(dq.removeLast());
        for (int d : dq)
            StdOut.print(d);
    }

}
