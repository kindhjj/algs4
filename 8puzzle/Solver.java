/* https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
* score: 100/100 */

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.ResizingArrayStack;

public class Solver {

    private final boolean solvable;
    private ResizingArrayStack<Board> solutionPath;
    private int moves = -1;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        SearchNote finalNote;
        if (initial == null) throw new IllegalArgumentException();
        MinPQ<SearchNote> minPQsn = new MinPQ<>();
        MinPQ<SearchNote> twinMinPQsn = new MinPQ<>();
        minPQsn.insert(new SearchNote(initial, 0, null));
        twinMinPQsn.insert(new SearchNote(initial.twin(), 0, null));
        while (true) {
            SearchNote popNote = minPQsn.delMin();
            SearchNote twinPopNote = twinMinPQsn.delMin();
            boolean popGoal = popNote.getBoard().isGoal();
            boolean twinPopGoal = twinPopNote.getBoard().isGoal();
            if (popGoal || twinPopGoal) {
                solvable = popGoal;
                finalNote = popNote;
                break;
            }
            for (Board neiborBoard : popNote.getBoard().neighbors())
                if (popNote.getPrev() == null || !neiborBoard.equals(popNote.getPrev().getBoard()))
                    minPQsn.insert(new SearchNote(neiborBoard, popNote.getMoves() + 1, popNote));
            for (Board neiborBoard : twinPopNote.getBoard().neighbors())
                if (twinPopNote.getPrev() == null || !neiborBoard
                        .equals(twinPopNote.getPrev().getBoard()))
                    twinMinPQsn.insert(new SearchNote(neiborBoard, twinPopNote.getMoves() + 1,
                                                      twinPopNote));
        }
        if (solvable) {
            SearchNote curr = finalNote;
            moves = finalNote.getMoves();
            solutionPath = new ResizingArrayStack<>();
            while (curr != null) {
                solutionPath.push(curr.getBoard());
                curr = curr.getPrev();
            }
        }
    }

    // define search notes implementing Manhattan priority
    private class SearchNote implements Comparable<SearchNote> {
        private final Board board;
        private final int moves;
        private final int priority;
        private final SearchNote prev;

        public SearchNote(Board init, int m, SearchNote prevSN) {
            board = init;
            moves = m;
            priority = moves + init.manhattan();
            prev = prevSN;
        }

        public int getMoves() {
            return moves;
        }

        public Board getBoard() {
            return board;
        }

        public int getPriority() {
            return priority;
        }

        public SearchNote getPrev() {
            return prev;
        }

        public int compareTo(SearchNote that) {
            if (that == null) throw new NullPointerException();
            return this.priority - that.getPriority();
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return solvable ? solutionPath : null;
    }

}
