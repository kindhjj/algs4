/* https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php
*  score: 100/100 */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;

public class BaseballElimination {

    private final int[] w, l, r;
    private final String[] names;
    private final int[][] g;
    private final int teamNum;
    private final HashMap<String, Integer> nameToNum;
    private int searchTeam = -1;
    private FordFulkerson fordFulkerson;
    private boolean ifElim;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException();
        In input = new In(filename);
        teamNum = input.readInt();
        names = new String[teamNum];
        w = new int[teamNum];
        l = new int[teamNum];
        r = new int[teamNum];
        g = new int[teamNum][teamNum];
        nameToNum = new HashMap<>();

        for (int i = 0; i < teamNum; i++) {
            names[i] = input.readString();
            nameToNum.put(names[i], i);
            w[i] = input.readInt();
            l[i] = input.readInt();
            r[i] = input.readInt();
            for (int j = 0; j < teamNum; j++)
                g[i][j] = input.readInt();
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teamNum;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(names);
    }

    // number of wins for given team
    public int wins(String team) {
        checkValid(team);
        return w[nameToNum.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        checkValid(team);
        return l[nameToNum.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkValid(team);
        return r[nameToNum.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkValid(team1);
        checkValid(team2);
        return g[nameToNum.get(team1)][nameToNum.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkValid(team);
        int tNum = nameToNum.get(team);
        if (tNum == -1) throw new IllegalArgumentException("team name not found.");
        if (tNum == searchTeam) return ifElim;
        if (checkTrivialElim(tNum) != -1) return true;
        resetCache();
        FlowNetwork flowNetwork = new FlowNetwork((teamNum * teamNum - teamNum) / 2 + 2);
        // assign s=tNum, t=teamNum
        for (int i = 0; i < teamNum; i++)
            if (i != tNum)
                flowNetwork.addEdge(new FlowEdge(i, teamNum, w[tNum] + r[tNum] - w[i]));
        int count = teamNum;
        for (int i = 0; i < teamNum - 1; i++)
            if (i != tNum) for (int j = i + 1; j < teamNum; j++) {
                if (j == tNum) continue;
                flowNetwork.addEdge(new FlowEdge(tNum, ++count, g[i][j]));
                flowNetwork.addEdge(new FlowEdge(count, i, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(count, j, Double.POSITIVE_INFINITY));
            }
        fordFulkerson = new FordFulkerson(flowNetwork, tNum, teamNum);
        int sumOutflow = 0;
        for (FlowEdge fe : flowNetwork.adj(tNum))
            sumOutflow += Math.round(fe.capacity());
        ifElim = (Math.round(fordFulkerson.value()) != sumOutflow);
        searchTeam = tNum;
        return ifElim;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) return null;
        Queue<String> subset = new Queue<>();
        int checkTrivial = checkTrivialElim(nameToNum.get(team));
        if (checkTrivial != -1) {
            subset.enqueue(names[checkTrivial]);
            return subset;
        }
        for (int i = 0; i < teamNum; i++)
            if (i != searchTeam && fordFulkerson.inCut(i))
                subset.enqueue(names[i]);
        return subset;
    }


    // check validity of the augument
    private void checkValid(String str) {
        if (str == null) throw new IllegalArgumentException();
        if (!nameToNum.containsKey(str)) throw new IllegalArgumentException();
    }

    private void resetCache() {
        fordFulkerson = null;
        ifElim = false;
    }

    // trivial elimination case
    private int checkTrivialElim(int team) {
        for (int i = 0; i < teamNum; i++)
            if (w[team] + r[team] < w[i])
                return i;
        return -1;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
