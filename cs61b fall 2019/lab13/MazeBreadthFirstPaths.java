import java.util.Queue;
import java.util.LinkedList;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits visible fields:
    protected int[] distTo;
    protected int[] edgeTo;
    protected boolean[] marked;
    */
    private int s;
    private int t;
    private boolean targetfound = false;
    private Queue<Integer> q = new LinkedList<>();
    /** A breadth-first search of paths in M from (SOURCEX, SOURCEY) to
     *  (TARGETX, TARGETY). */
    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY,
                                 int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
        q.add(s);
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        while(!q.isEmpty() && !targetfound) {
            int temp = q.peek();
            marked[temp] = true;
            announce();
            for (int i:maze.adj(temp)) {
                if (!marked[i]) {
                    edgeTo[i] = temp;
                    announce();
                    distTo[i] = distTo[temp] + 1;
                    q.add(i);
                    if (i == t) {
                        break;
                    }
                }
            }
            temp = q.remove();
            if (temp == t) {
                targetfound = true;
            }
        }
        // TODO: Your code here. Don't forget to update distTo, edgeTo,
        // and marked, as well as call announce()
    }


    @Override
    public void solve() {
        bfs();
    }
}

