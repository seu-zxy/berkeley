import java.util.*;

/**
 *  A weighted graph.
 *  @author josiath
 */
public class Graph {

    /** Adjacency lists by vertex number. */
    private LinkedList<Edge>[] adjLists;
    /** Number of vertices in me. */
    private int vertexCount;
    private TreeSet<node> fringe = new TreeSet<>();
    private int[] distance;
    private boolean[] inspect;
    /** A graph with NUMVERTICES vertices and no edges. */
    @SuppressWarnings("unchecked")
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        distance = new int[numVertices];
        inspect = new boolean[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
            distance[k] = Integer.MAX_VALUE;
            inspect[k] = false;
        }
        vertexCount = numVertices;
    }

    /** Add to the graph a directed edge from vertex V1 to vertex V2,
     *  with weight EDGEWEIGHT. If the edge already exists, replaces
     *  the weight of the current edge EDGEWEIGHT. */
    public void addEdge(int v1, int v2, int edgeWeight) {
        if (!isAdjacent(v1, v2)) {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            v1Neighbors.add(new Edge(v1, v2, edgeWeight));
        } else {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            for (Edge e : v1Neighbors) {
                if (e.to() == v2) {
                    e.edgeWeight = edgeWeight;
                }
            }
        }
    }

    /** Add to the graph an undirected edge from vertex V1 to vertex V2,
     *  with weight EDGEWEIGHT. If the edge already exists, replaces
     *  the weight of the current edge EDGEWEIGHT. */
    public void addUndirectedEdge(int v1, int v2, int edgeWeight) {
        addEdge(v1, v2, edgeWeight);
        addEdge(v2, v1, edgeWeight);
    }

    /** Returns true iff there is an edge from vertex FROM to vertex TO. */
    public boolean isAdjacent(int from, int to) {
        for (Edge e : adjLists[from]) {
            if (e.to() == to) {
                return true;
            }
        }
        return false;
    }

    /** Returns a list of all the neighboring vertices u
     *  such that the edge (VERTEX, u) exists in this graph. */
    public List<Integer> neighbors(int vertex) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (Edge e : adjLists[vertex]) {
            neighbors.add(e.to());
        }
        return neighbors;
    }

    /** Runs Dijkstra's algorithm starting from vertex STARTVERTEX and returns
     *  an integer array consisting of the shortest distances
     *  from STARTVERTEX to all other vertices. */
    public int[] dijkstras(int startVertex) {
        //add all vertices to fringe
        distance[startVertex] = 0;
        fringe.add(new node(startVertex,startVertex,0));
        while (!fringe.isEmpty()) {
            node node1 = fringe.pollFirst();
            int v = node1.vetice;
            LinkedList<Edge> vneigh = adjLists[v];
            if (inspect[v] == false) {
                inspect[v] = true;
            }
            for (Edge temp:vneigh) {
                if (temp != null) {
                    int to = temp.to;
                    int vadd = temp.edgeWeight;
                    if (inspect[to] == false) {
                        int newkey = distance[v] + vadd;
                        int oldkey = distance[to];
                        if (newkey < oldkey) {
                            node kk = new node(to, v, newkey);
                            fringe.add(kk);
                            distance[to] = newkey;
                        }
                    }
                }
            }
        }
        return distance;
    }

    /** Returns the edge (V1, V2). (ou may find this helpful to implement!) */
    private Edge getEdge(int v1, int v2) {
        return null;
    }

    /** Represents an edge in this graph. */
    private class Edge {

        /** End points of this edge. */
        private int from, to;
        /** Weight label of this edge. */
        private int edgeWeight;

        /** The edge (V0, V1) with weight WEIGHT. */
        Edge(int v0, int v1, int weight) {
            this.from = v0;
            this.to = v1;
            this.edgeWeight = weight;
        }

        /** Return neighbor vertex along this edge. */
        public int to() {
            return to;
        }

        /** Return weight of this edge. */
        public int info() {
            return edgeWeight;
        }

        @Override
        public String toString() {
            return "(" + from + "," + to + ",dist=" + edgeWeight + ")";
        }

    }

    class node implements Comparable<node>{
        public int vetice;
        public int before;
        public int val;
        node(int _vetice, int _before,int _val) {
            vetice = _vetice;
            before = _before;
            val = _val;
        }
        @Override
        public int compareTo(node a) {
            if (val < a.val) {
                return -1;
            }
            if (val == a.val && a.vetice == vetice) {
                return 0;
            }
            return 1;
        }
    }
    /** Tests of Graph. */
    public static void main(String[] unused) {
        // Put some tests here!

        Graph g1 = new Graph(5);
        g1.addEdge(0, 1, 1);
        g1.addEdge(0, 2, 1);
        g1.addEdge(0, 4, 1);
        g1.addEdge(1, 2, 1);
        g1.addEdge(2, 0, 1);
        g1.addEdge(2, 3, 1);
        g1.addEdge(4, 3, 1);
        int[] val1 = g1.dijkstras(2);
        Graph g2 = new Graph(5);
        g2.addEdge(0, 1, 1);
        g2.addEdge(0, 2, 1);
        g2.addEdge(0, 4, 1);
        g2.addEdge(1, 2, 1);
        g2.addEdge(2, 3, 1);
        g2.addEdge(4, 3, 1);
    }
}
