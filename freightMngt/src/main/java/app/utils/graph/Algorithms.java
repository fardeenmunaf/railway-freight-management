package app.utils.graph;

import app.utils.graph.matrix.MatrixGraph;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.Comparator;

/**
 *
 * @author DEI-ISEP
 *
 */
public class Algorithms {

    /**
     * Result para Topological Sort + deteção de ciclos.
     */
    public static class TopologicalSortResult<V, E> {
        private final LinkedList<V> topoOrder;
        private final Set<V> cycleStations;
        private final Set<Edge<V, E>> cycleEdges;

        public TopologicalSortResult(LinkedList<V> topoOrder,
                                     Set<V> cycleStations,
                                     Set<Edge<V, E>> cycleEdges) {
            this.topoOrder = topoOrder;
            this.cycleStations = cycleStations;
            this.cycleEdges = cycleEdges;
        }

        public LinkedList<V> getTopoOrder() { return topoOrder; }
        public Set<V> getCycleStations() { return cycleStations; }
        public Set<Edge<V, E>> getCycleEdges() { return cycleEdges; }
        public boolean hasCycles() { return !cycleStations.isEmpty(); }
    }

    /**
     * Performs breadth-first search of a graph.Graph starting in a vertex
     *
     * @param g    graph.Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    public static <V, E> LinkedList<V> BreadthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert))
            return null;

        LinkedList<V> qbfs = new LinkedList<>();
        LinkedList<V> qaux = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];

        qbfs.add(vert);
        qaux.add(vert);
        visited[g.key(vert)] = true;

        while (!qaux.isEmpty()) {
            V v = qaux.removeFirst();
            for (V vAdj : g.adjVertices(v)) {
                if (!visited[g.key(vAdj)]) {
                    qbfs.add(vAdj);
                    qaux.add(vAdj);
                    visited[g.key(vAdj)] = true;
                }
            }
        }
        return qbfs;
    }

    /**
     * Performs depth-first search starting in a vertex
     *
     * @param g       graph.Graph instance
     * @param vOrig   vertex of graph g that will be the source of the search
     * @param visited set of previously visited vertices
     * @param qdfs    return LinkedList with vertices of depth-first search
     */
    private static <V, E> void DepthFirstSearch(Graph<V, E> g, V vOrig, boolean[] visited, LinkedList<V> qdfs) {
        qdfs.add(vOrig);
        visited[g.key(vOrig)] = true;

        for (V vAdj : g.adjVertices(vOrig)) {
            if (!visited[g.key(vAdj)])
                DepthFirstSearch(g, vAdj, visited, qdfs);
        }
    }

    /**
     * Performs depth-first search starting in a vertex
     *
     * @param g    graph.Graph instance
     * @param vert vertex of graph g that will be the source of the search
     *
     * @return a LinkedList with the vertices of depth-first search
     */
    public static <V, E> LinkedList<V> DepthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert))
            return null;

        LinkedList<V> qdfs = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];

        DepthFirstSearch(g, vert, visited, qdfs);

        return qdfs;
    }

    /**
     * Returns all paths from vOrig to vDest
     *
     * @param g       graph.Graph instance
     * @param vOrig   Vertex that will be the source of the path
     * @param vDest   Vertex that will be the end of the path
     * @param visited set of discovered vertices
     * @param path    stack with vertices of the current path (the path is in
     *                reverse order)
     * @param paths   ArrayList with all the paths (in correct order)
     */
    private static <V, E> void allPaths(Graph<V, E> g, V vOrig, V vDest, boolean[] visited,
                                        LinkedList<V> path, ArrayList<LinkedList<V>> paths) {
        path.add(vOrig);
        visited[g.key(vOrig)] = true;

        for (V vAdj : g.adjVertices(vOrig)) {
            if (vAdj.equals(vDest)) {
                path.add(vDest);
                paths.add(new LinkedList<>(path));
                path.removeLast();
            } else {
                if (!visited[g.key(vAdj)]) {
                    allPaths(g, vAdj, vDest, visited, path, paths);
                }
            }
        }
        visited[g.key(vOrig)] = false;
        path.removeLast();
    }

    /**
     * Returns all paths from vOrig to vDest
     *
     * @param g     graph.Graph instance
     * @param vOrig information of the Vertex origin
     * @param vDest information of the Vertex destination
     * @return paths ArrayList with all paths from vOrig to vDest
     */
    public static <V, E> ArrayList<LinkedList<V>> allPaths(Graph<V, E> g, V vOrig, V vDest) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest))
            return null;

        ArrayList<LinkedList<V>> paths = new ArrayList<>();
        LinkedList<V> path = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];

        allPaths(g, vOrig, vDest, visited, path, paths);

        return paths;
    }

    /**
     * Computes shortest-path distance from a source vertex to all reachable
     * vertices of a graph g with non-negative edge weights
     * This implementation uses Dijkstra's algorithm
     *
     * @param g        graph.Graph instance
     * @param vOrig    Vertex that will be the source of the path
     * @param visited  set of previously visited vertices
     * @param pathKeys minimum path vertices keys
     * @param dist     minimum distances
     */
    private static <V, E> void shortestPathDijkstra(Graph<V, E> g, V vOrig,
                                                    Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                                    boolean[] visited, V[] pathKeys, E[] dist) {
        int vkey = g.key(vOrig);
        dist[vkey] = zero;
        pathKeys[vkey] = null;

        while (vOrig != null) {
            vkey = g.key(vOrig);
            visited[vkey] = true;
            for (Edge<V, E> edge : g.outgoingEdges(vOrig)) {
                int vkeyAdj = g.key(edge.getVDest());
                if (!visited[vkeyAdj]) {
                    E s = sum.apply(dist[vkey], edge.getWeight());
                    if (dist[vkeyAdj] == null || ce.compare(dist[vkeyAdj], s) > 0) {
                        dist[vkeyAdj] = s;
                        pathKeys[vkeyAdj] = vOrig;
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

    /**
     * Shortest-path between two vertices
     *
     * @param g         graph
     * @param vOrig     origin vertex
     * @param vDest     destination vertex
     * @param ce        comparator between elements of type E
     * @param sum       sum two elements of type E
     * @param zero      neutral element of the sum in elements of type E
     * @param shortPath returns the vertices which make the shortest path
     * @return if vertices exist in the graph and are connected, true, false
     *         otherwise
     */
    public static <V, E> E shortestPath(Graph<V, E> g, V vOrig, V vDest,
                                        Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                        LinkedList<V> shortPath) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest))
            return null;

        shortPath.clear();
        int numVerts = g.numVertices();
        boolean[] visited = new boolean[numVerts];

        @SuppressWarnings("unchecked")
        V[] pathKeys = (V[]) new Object[numVerts];

        @SuppressWarnings("unchecked")
        E[] dist = (E[]) new Object[numVerts];

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        E lengthPath = dist[g.key(vDest)];

        if (lengthPath != null) {
            getPath(g, vOrig, vDest, pathKeys, shortPath);
            return lengthPath;
        }
        return null;
    }

    /**
     * Shortest-path between a vertex and all other vertices
     *
     * @param g     graph
     * @param vOrig start vertex
     * @param ce    comparator between elements of type E
     * @param sum   sum two elements of type E
     * @param zero  neutral element of the sum in elements of type E
     * @param paths returns all the minimum paths
     * @param dists returns the corresponding minimum distances
     * @return if vOrig exists in the graph true, false otherwise
     */
    public static <V, E> boolean shortestPaths(Graph<V, E> g, V vOrig,
                                               Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                               ArrayList<LinkedList<V>> paths, ArrayList<E> dists) {
        if (!g.validVertex(vOrig))
            return false;

        paths.clear();
        dists.clear();
        int numVerts = g.numVertices();
        boolean[] visited = new boolean[numVerts];

        @SuppressWarnings("unchecked")
        V[] pathKeys = (V[]) new Object[numVerts];

        @SuppressWarnings("unchecked")
        E[] dist = (E[]) new Object[numVerts];

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        for (int i = 0; i < numVerts; i++) {
            paths.add(null);
            dists.add(null);
        }

        for (int i = 0; i < numVerts; i++) {
            V vDest = g.vertex(i);
            E tempDist = dist[i];
            if (tempDist != null) {
                dists.set(i, tempDist);
                LinkedList<V> shortPath = new LinkedList<>();
                getPath(g, vOrig, vDest, pathKeys, shortPath);
                paths.set(i, shortPath);
            }
        }
        return true;
    }

    /**
     * Computes shortest-path using Bellman-Ford algorithm (supports negative weights)
     * Detects negative cycles in the graph
     *
     * @param g         Graph instance
     * @param vOrig     Origin vertex
     * @param vDest     Destination vertex
     * @param ce        Comparator between elements of type E
     * @param sum       Binary operator to sum edges
     * @param zero      Neutral element for sum
     * @param shortPath Returns the vertices which make the shortest path (or cycle vertices if negative cycle detected)
     * @return path cost, or null if negative cycle detected or no path exists
     */
    public static <V, E> E bellmanFord(Graph<V, E> g, V vOrig, V vDest,
                                       Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                       LinkedList<V> shortPath) {
        // Validate origin and destination vertices
        if (!g.validVertex(vOrig) || !g.validVertex(vDest))
            return null;

        shortPath.clear();
        int numVerts = g.numVertices();

        // Arrays to store distances and path predecessors
        @SuppressWarnings("unchecked")
        E[] dist = (E[]) new Object[numVerts];
        @SuppressWarnings("unchecked")
        V[] pathKeys = (V[]) new Object[numVerts];

        // Initialize: distance to origin is zero
        dist[g.key(vOrig)] = zero;

        // Relax all edges |V|-1 times (main Bellman-Ford loop)
        for (int i = 0; i < numVerts - 1; i++) {
            for (V v : g.vertices()) {
                int vKey = g.key(v);
                // Only process vertices that have been reached
                if (dist[vKey] != null) {
                    // Check each outgoing edge for possible improvement
                    for (Edge<V, E> edge : g.outgoingEdges(v)) {
                        int adjKey = g.key(edge.getVDest());
                        E newDist = sum.apply(dist[vKey], edge.getWeight());

                        // Update if new distance is shorter
                        if (dist[adjKey] == null || ce.compare(newDist, dist[adjKey]) < 0) {
                            dist[adjKey] = newDist;
                            pathKeys[adjKey] = v;
                        }
                    }
                }
            }
        }

        // Check for negative cycles (one more relaxation iteration)
        for (V v : g.vertices()) {
            int vKey = g.key(v);
            if (dist[vKey] != null) {
                for (Edge<V, E> edge : g.outgoingEdges(v)) {
                    int adjKey = g.key(edge.getVDest());
                    E newDist = sum.apply(dist[vKey], edge.getWeight());

                    // If we can still improve, negative cycle exists
                    if (dist[adjKey] == null || ce.compare(newDist, dist[adjKey]) < 0) {
                        shortPath.add(v);
                        shortPath.add(edge.getVDest());
                        return null; // Negative cycle detected
                    }
                }
            }
        }

        // Retrieve the final distance to destination
        E lengthPath = dist[g.key(vDest)];

        // Reconstruct path if destination is reachable
        if (lengthPath != null) {
            getPath(g, vOrig, vDest, pathKeys, shortPath);
            return lengthPath;
        }

        return null; // No path exists
    }


    /**
     * Extracts from pathKeys the minimum path between voInf and vdInf
     * The path is constructed from the end to the beginning
     *
     * @param g        graph.Graph instance
     * @param vOrig    information of the Vertex origin
     * @param vDest    information of the Vertex destination
     * @param pathKeys minimum path vertices keys
     * @param path     stack with the minimum path (correct order)
     */
    private static <V, E> void getPath(Graph<V, E> g, V vOrig, V vDest,
                                       V[] pathKeys, LinkedList<V> path) {
        if (vOrig.equals(vDest))
            path.push(vOrig);
        else {
            path.push(vDest);
            int vKey = g.key(vDest);
            V vPrev = pathKeys[vKey];
            getPath(g, vOrig, vPrev, pathKeys, path);
        }
    }

    /**
     * Calculates the minimum distance graph using Floyd-Warshall
     *
     * @param g   initial graph
     * @param ce  comparator between elements of type E
     * @param sum sum two elements of type E
     * @return the minimum distance graph
     */
    public static <V, E> MatrixGraph<V, E> minDistGraph(Graph<V, E> g, Comparator<E> ce, BinaryOperator<E> sum) {
        int numVerts = g.numVertices();
        if (numVerts == 0)
            return null;

        @SuppressWarnings("unchecked")
        E[][] m = (E[][]) new Object[numVerts][numVerts];

        for (int i = 0; i < numVerts; i++) {
            for (int j = 0; j < numVerts; j++) {
                Edge<V, E> e = g.edge(i, j);
                if (e != null)
                    m[i][j] = e.getWeight();
            }
        }

        for (int k = 0; k < numVerts; k++) {
            for (int i = 0; i < numVerts; i++) {
                if (i != k && m[i][k] != null) {
                    for (int j = 0; j < numVerts; j++) {
                        if (j != i && j != k && m[k][j] != null) {
                            E s = sum.apply(m[i][k], m[k][j]);
                            if (m[i][j] == null || ce.compare(m[i][j], s) > 0)
                                m[i][j] = s;
                        }
                    }
                }
            }
        }
        return new MatrixGraph<>(g.isDirected(), g.vertices(), m);
    }


    /**
     * Topological sort + deteção de ciclos.
     * @param g grafo dirigido
     * @return resultado com ordem topológica ou ciclos
     */
    public static <V, E> TopologicalSortResult<V, E> topologicalSortWithCycles(Graph<V, E> g) {
        if (!g.isDirected())
            throw new IllegalArgumentException("Topological sort requires directed graph");

        LinkedList<V> topoOrder = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        Set<V> recStack = new HashSet<>();

        Set<V> cycleStations = new HashSet<>();
        Set<Edge<V, E>> cycleEdges = new HashSet<>();

        Deque<V> stack = new ArrayDeque<>();

        for (V v : g.vertices()) {
            if (!visited.contains(v)) {
                dfsTopological(g, v, visited, recStack, stack, topoOrder, cycleStations);
            }
        }

        if (!cycleStations.isEmpty()) {
            collectCycleEdges(g, cycleStations, cycleEdges);
            return new TopologicalSortResult<>(new LinkedList<>(), cycleStations, cycleEdges);
        }

        return new TopologicalSortResult<>(topoOrder, Collections.emptySet(), Collections.emptySet());
    }

    private static <V, E> void dfsTopological(Graph<V, E> g, V v,
                                              Set<V> visited,
                                              Set<V> recStack,
                                              Deque<V> stack,
                                              LinkedList<V> topoOrder,
                                              Set<V> cycleStations) {

        visited.add(v);
        recStack.add(v);
        stack.push(v);

        for (V adj : g.adjVertices(v)) {

            if (recStack.contains(adj)) {
                // Encontrou back-edge v -> adj: marcar todos os vértices do ciclo
                cycleStations.add(adj);
                for (V x : stack) {
                    cycleStations.add(x);
                    if (x.equals(adj)) {
                        break;
                    }
                }
            }

            if (!visited.contains(adj)) {
                dfsTopological(g, adj, visited, recStack, stack, topoOrder, cycleStations);
            }
        }

        stack.pop();
        recStack.remove(v);
        topoOrder.addFirst(v);
    }

    /**
     * Recolhe todas as arestas cujo destino e origem fazem parte de ciclos.
     */
    private static <V, E> void collectCycleEdges(Graph<V, E> g,
                                                 Set<V> cycleStations,
                                                 Set<Edge<V, E>> cycleEdges) {
        for (V v : cycleStations) {
            for (Edge<V, E> e : g.outgoingEdges(v)) {
                if (cycleStations.contains(e.getVDest())) {
                    cycleEdges.add(e);
                }
            }
        }
    }

    /**
     * Computes Betweenness Centrality using Brandes' algorithm (unweighted version).
     *
     * Betweenness centrality measures how often a vertex appears on shortest paths
     * between other pairs of vertices. A high betweenness indicates that a vertex
     * is a crucial intermediary in the network.
     *
     * Brandes' Algorithm (2001):
     * - Phase 1: Single-source shortest paths from each vertex using BFS
     * - Phase 2: Accumulation of dependencies in reverse order
     *
     * Complexity: O(VE) for unweighted graphs
     *
     * Reference: Brandes, U. (2001). "A faster algorithm for betweenness centrality"
     *
     * @param g Graph instance
     * @return Map with betweenness centrality values for each vertex
     */
    public static <V, E> Map<V, Double> brandesBetweenness(Graph<V, E> g) {
        Map<V, Double> betweenness = new HashMap<>();

        // Initialize betweenness to 0 for all vertices
        for (V v : g.vertices()) {
            betweenness.put(v, 0.0);
        }

        // For each vertex as source
        for (V source : g.vertices()) {
            // Data structures for single-source shortest paths
            Deque<V> stack = new ArrayDeque<>();
            Map<V, List<V>> predecessors = new HashMap<>();
            Map<V, Integer> sigma = new HashMap<>();  // number of shortest paths
            Map<V, Integer> distance = new HashMap<>();

            // Initialize
            for (V w : g.vertices()) {
                predecessors.put(w, new ArrayList<>());
                sigma.put(w, 0);
                distance.put(w, -1);
            }

            sigma.put(source, 1);
            distance.put(source, 0);

            // BFS phase - find shortest paths
            Queue<V> queue = new LinkedList<>();
            queue.offer(source);

            while (!queue.isEmpty()) {
                V v = queue.poll();
                stack.push(v);

                // For each neighbor of v
                for (V w : g.adjVertices(v)) {
                    // w found for the first time?
                    if (distance.get(w) < 0) {
                        queue.offer(w);
                        distance.put(w, distance.get(v) + 1);
                    }

                    // Is this a shortest path to w?
                    if (distance.get(w).equals(distance.get(v) + 1)) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        predecessors.get(w).add(v);
                    }
                }
            }

            // Accumulation phase - back-propagate dependencies
            Map<V, Double> dependency = new HashMap<>();
            for (V v : g.vertices()) {
                dependency.put(v, 0.0);
            }

            // Stack contains vertices in order of non-increasing distance from source
            while (!stack.isEmpty()) {
                V w = stack.pop();

                for (V v : predecessors.get(w)) {
                    double contribution = (sigma.get(v).doubleValue() / sigma.get(w).doubleValue())
                                        * (1.0 + dependency.get(w));
                    dependency.put(v, dependency.get(v) + contribution);
                }

                if (!w.equals(source)) {
                    betweenness.put(w, betweenness.get(w) + dependency.get(w));
                }
            }
        }

        // For undirected graphs, divide by 2 (each shortest path is counted twice)
        if (!g.isDirected()) {
            for (V v : g.vertices()) {
                betweenness.put(v, betweenness.get(v) / 2.0);
            }
        }

        return betweenness;
    }

    /**
     * Computes Betweenness Centrality using Brandes' algorithm (weighted version with Dijkstra).
     *
     * Similar to the unweighted version but uses Dijkstra instead of BFS for shortest paths.
     * This version properly handles weighted graphs where edge weights matter.
     *
     * Complexity: O(VE + V^2 log V)
     *
     * @param g    Graph instance
     * @param ce   Comparator between elements of type E
     * @param sum  Sum two elements of type E
     * @param zero Neutral element of the sum
     * @return Map with betweenness centrality values for each vertex
     */
    public static <V, E> Map<V, Double> brandesBetweennessWeighted(Graph<V, E> g, Comparator<E> ce, BinaryOperator<E> sum, E zero) {
        Map<V, Double> betweenness = new HashMap<>();

        // Initialize betweenness to 0 for all vertices
        for (V v : g.vertices()) {
            betweenness.put(v, 0.0);
        }

        // For each vertex as source
        for (V source : g.vertices()) {
            // Data structures for single-source shortest paths
            Deque<V> stack = new ArrayDeque<>();
            Map<V, List<V>> predecessors = new HashMap<>();
            Map<V, Integer> sigma = new HashMap<>();  // number of shortest paths
            Map<V, E> distance = new HashMap<>();

            // Initialize
            for (V w : g.vertices()) {
                predecessors.put(w, new ArrayList<>());
                sigma.put(w, 0);
                distance.put(w, null);
            }

            sigma.put(source, 1);
            distance.put(source, zero);

            // Priority queue for Dijkstra (using manual selection for simplicity)
            Set<V> visited = new HashSet<>();
            V current = source;

            // Dijkstra phase - find shortest paths
            while (current != null) {
                stack.push(current);
                visited.add(current);

                E distCurrent = distance.get(current);

                // For each neighbor
                for (Edge<V, E> edge : g.outgoingEdges(current)) {
                    V w = edge.getVDest();
                    E newDist = sum.apply(distCurrent, edge.getWeight());

                    // w found for the first time?
                    if (distance.get(w) == null) {
                        distance.put(w, newDist);
                        sigma.put(w, sigma.get(current));
                        predecessors.get(w).add(current);
                    }
                    // Shorter path to w found?
                    else if (ce.compare(newDist, distance.get(w)) < 0) {
                        distance.put(w, newDist);
                        sigma.put(w, sigma.get(current));
                        predecessors.get(w).clear();
                        predecessors.get(w).add(current);
                    }
                    // Another shortest path to w found?
                    else if (ce.compare(newDist, distance.get(w)) == 0) {
                        sigma.put(w, sigma.get(w) + sigma.get(current));
                        predecessors.get(w).add(current);
                    }
                }

                // Select next vertex with minimum distance
                current = null;
                E minDist = null;
                for (V v : g.vertices()) {
                    if (!visited.contains(v) && distance.get(v) != null) {
                        if (minDist == null || ce.compare(distance.get(v), minDist) < 0) {
                            minDist = distance.get(v);
                            current = v;
                        }
                    }
                }
            }

            // Accumulation phase - back-propagate dependencies
            Map<V, Double> dependency = new HashMap<>();
            for (V v : g.vertices()) {
                dependency.put(v, 0.0);
            }

            // Stack contains vertices in order of non-increasing distance from source
            while (!stack.isEmpty()) {
                V w = stack.pop();

                for (V v : predecessors.get(w)) {
                    double contribution = (sigma.get(v).doubleValue() / sigma.get(w).doubleValue())
                                        * (1.0 + dependency.get(w));
                    dependency.put(v, dependency.get(v) + contribution);
                }

                if (!w.equals(source)) {
                    betweenness.put(w, betweenness.get(w) + dependency.get(w));
                }
            }
        }

        // For undirected graphs, divide by 2 (each shortest path is counted twice)
        if (!g.isDirected()) {
            for (V v : g.vertices()) {
                betweenness.put(v, betweenness.get(v) / 2.0);
            }
        }

        return betweenness;
    }
}
