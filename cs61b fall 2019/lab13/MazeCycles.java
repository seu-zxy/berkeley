import java.util.ArrayList;

/**
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {
    /* Inherits protected fields:
    protected int[] distTo;
    protected int[] edgeTo;
    protected boolean[] marked;
    */
    private boolean findcycle = false;
    private int[] temped;
    int begin,end;
    /** Set up to find cycles of M. */
    public MazeCycles(Maze m) {
        super(m);
        temped = new int[maze.V()];
        for (int i = 0; i < maze.V(); i += 1) {
            temped[i] = Integer.MAX_VALUE;
        }
        edgeTo[0] = 0;
        distTo[0] = 0;
    }

    @Override
    public void solve() {
        dfs(0);
        if (findcycle) {
            int t = end;
            while(t != begin) {
                edgeTo[t] = temped[t];
                t = temped[t];
            }
            edgeTo[begin] = end;
            announce();
        }
    }

    public void detectCycles(int v) {
        marked[v] = true;

    }

    public void dfs(int v) {
        marked[v] = true;
        announce();
        if (findcycle) {
            return;
        }
        for (int w:maze.adj(v)) {
            if (!marked[w]) {
                temped[w] = v;
                distTo[w] = distTo[v] + 1;
                dfs(w);
                if (findcycle) {
                    return;
                }
            }else if ( temped[v] != w) {
                findcycle = true;
                begin = w;
                end = v;
                return;
            }
        }
    }
    // Helper methods go here
}

