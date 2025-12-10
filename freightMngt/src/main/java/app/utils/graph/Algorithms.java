package app.utils.graph;

import app.utils.graph.matrix.MatrixGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.BinaryOperator;

/**
 *
 * @author DEI-ISEP
 *
 */
public class Algorithms {

    /** Performs breadth-first search of a Graph starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    // BreadthFirstSearch
    public static <V, E> LinkedList<V> BreadthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert)) {
            return null;
        }

        LinkedList<V> qbfs = new LinkedList<>();
        LinkedList<V> qaux = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];

        qbfs.add(vert);
        qaux.add(vert);
        int vKey = g.key(vert);
        visited[vKey] = true;

        while (!qaux.isEmpty()) {
            V vInf = qaux.remove();
            for (V vAdj : g.adjVertices(vInf)) {
                vKey = g.key(vAdj);
                if (!visited[vKey]) {
                    qbfs.add(vAdj);
                    qaux.add(vAdj);
                    visited[vKey] = true;
                }
            }
        }
        return qbfs;
    }

    // DepthFirstSearch (privado)
    private static <V, E> void DepthFirstSearch(Graph<V, E> g, V vOrig, boolean[] visited, LinkedList<V> qdfs) {
        int vKey = g.key(vOrig);
        qdfs.add(vOrig);
        visited[vKey] = true;

        for (V vAdj : g.adjVertices(vOrig)) {
            vKey = g.key(vAdj);
            if (!visited[vKey]) {
                DepthFirstSearch(g, vAdj, visited, qdfs);
            }
        }
    }

    // DepthFirstSearch (público)
    public static <V, E> LinkedList<V> DepthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert)) {
            return null;
        }

        LinkedList<V> qdfs = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];
        DepthFirstSearch(g, vert, visited, qdfs);
        return qdfs;
    }

    // allPaths (privado)
    private static <V, E> void allPaths(Graph<V, E> g, V vOrig, V vDest, boolean[] visited,
                                        LinkedList<V> path, ArrayList<LinkedList<V>> paths) {
        int vKey = g.key(vOrig);
        path.push(vOrig);
        visited[vKey] = true;

        if (vOrig.equals(vDest)) {
            paths.add(new LinkedList<>(path));
        } else {
            for (V vAdj : g.adjVertices(vOrig)) {
                vKey = g.key(vAdj);
                if (!visited[vKey]) {
                    allPaths(g, vAdj, vDest, visited, path, paths);
                }
            }
        }

        path.pop();
        visited[g.key(vOrig)] = false;
    }

    // allPaths (público)
    public static <V, E> ArrayList<LinkedList<V>> allPaths(Graph<V, E> g, V vOrig, V vDest) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) {
            return null;
        }

        ArrayList<LinkedList<V>> paths = new ArrayList<>();
        boolean[] visited = new boolean[g.numVertices()];
        LinkedList<V> path = new LinkedList<>();

        allPaths(g, vOrig, vDest, visited, path, paths);
        return paths;
    }

    // shortestPathDijkstra
    private static <V, E> void shortestPathDijkstra(Graph<V, E> g, V vOrig,
                                                    Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                                    boolean[] visited, V[] pathKeys, E[] dist) {
        int vKey = g.key(vOrig);
        dist[vKey] = zero;
        pathKeys[vKey] = vOrig;

        while (vOrig != null) {
            vKey = g.key(vOrig);
            visited[vKey] = true;

            for (Edge<V, E> edge : g.outgoingEdges(vOrig)) {
                int vKeyAdj = g.key(edge.getVDest());
                if (!visited[vKeyAdj]) {
                    E s = sum.apply(dist[vKey], edge.getWeight());
                    if (dist[vKeyAdj] == null || ce.compare(s, dist[vKeyAdj]) < 0) {
                        dist[vKeyAdj] = s;
                        pathKeys[vKeyAdj] = vOrig;
                    }
                }
            }

            E minDist = null;
            vOrig = null;
            for (V vert : g.vertices()) {
                int i = g.key(vert);
                if (!visited[i] && dist[i] != null && (minDist == null || ce.compare(dist[i], minDist) < 0)) {
                    minDist = dist[i];
                    vOrig = vert;
                }
            }
        }
    }

    // getPath
    private static <V, E> void getPath(Graph<V, E> g, V vOrig, V vDest,
                                       V[] pathKeys, LinkedList<V> path) {
        if (!vOrig.equals(vDest)) {
            path.push(vDest);
            int vKey = g.key(vDest);
            vDest = pathKeys[vKey];
            getPath(g, vOrig, vDest, pathKeys, path);
        } else {
            path.push(vOrig);
        }
    }

    // shortestPath
    public static <V, E> E shortestPath(Graph<V, E> g, V vOrig, V vDest,
                                        Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                        LinkedList<V> shortPath) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) {
            return null;
        }

        shortPath.clear();
        int numVerts = g.numVertices();
        boolean[] visited = new boolean[numVerts];
        @SuppressWarnings("unchecked")
        V[] pathKeys = (V[]) new Object[numVerts];
        @SuppressWarnings("unchecked")
        E[] dist = (E[]) new Object[numVerts];

        for (int i = 0; i < numVerts; i++) {
            dist[i] = null;
            pathKeys[i] = null;
        }

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        int vKey = g.key(vDest);
        if (dist[vKey] == null) {
            return null;
        }

        getPath(g, vOrig, vDest, pathKeys, shortPath);
        return dist[vKey];
    }

    // shortestPaths
    public static <V, E> boolean shortestPaths(Graph<V, E> g, V vOrig,
                                               Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                               ArrayList<LinkedList<V>> paths, ArrayList<E> dists) {
        if (!g.validVertex(vOrig)) {
            return false;
        }

        paths.clear();
        dists.clear();
        int numVerts = g.numVertices();
        boolean[] visited = new boolean[numVerts];
        @SuppressWarnings("unchecked")
        V[] pathKeys = (V[]) new Object[numVerts];
        @SuppressWarnings("unchecked")
        E[] dist = (E[]) new Object[numVerts];

        for (int i = 0; i < numVerts; i++) {
            dist[i] = null;
            pathKeys[i] = null;
        }

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        for (V vDest : g.vertices()) {
            LinkedList<V> shortPath = new LinkedList<>();
            int vKey = g.key(vDest);

            if (dist[vKey] != null) {
                getPath(g, vOrig, vDest, pathKeys, shortPath);
                paths.add(shortPath);
                dists.add(dist[vKey]);
            } else {
                paths.add(null);
                dists.add(null);
            }
        }
        return true;
    }

    // minDistGraph (Floyd-Warshall)
    public static <V, E> MatrixGraph<V, E> minDistGraph(Graph<V, E> g, Comparator<E> ce, BinaryOperator<E> sum) {
        if (g == null) {
            return null;
        }

        // Cria uma cópia do grafo
        MatrixGraph<V, E> result;
        if (g instanceof MatrixGraph) {
            result = ((MatrixGraph<V, E>) g).clone();
        } else {
            // Converte para MatrixGraph se não for
            result = new MatrixGraph<>(g.isDirected());
            for (V v : g.vertices()) {
                result.addVertex(v);
            }
            for (Edge<V, E> edge : g.edges()) {
                result.addEdge(edge.getVOrig(), edge.getVDest(), edge.getWeight());
            }
        }

        int n = g.numVertices();

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (i != k && result.edge(i, k) != null) {
                    for (int j = 0; j < n; j++) {
                        if (i != j && k != j && result.edge(k, j) != null) {
                            E newDist = sum.apply(result.edge(i, k).getWeight(), result.edge(k, j).getWeight());
                            Edge<V, E> existing = result.edge(i, j);

                            if (existing == null || ce.compare(newDist, existing.getWeight()) < 0) {
                                result.addEdge(result.vertex(i), result.vertex(j), newDist);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}