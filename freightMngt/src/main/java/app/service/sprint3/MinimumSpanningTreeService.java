package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Edge;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;

import java.util.*;
import java.util.function.Predicate;

/**
 * Service for computing Minimum Spanning Trees (MST) for station networks.
 *
 * Uses Kruskal's algorithm with Union-Find data structure for optimal performance.
 * Handles disconnected components by computing MST for each connected component separately.
 *
 * Time Complexity: O(E log V) where E is the number of edges and V is the number of vertices.
 *
 * @author Infrastructure Planner
 * @version 1.0
 */
public class MinimumSpanningTreeService {

    /**
     * Result of MST computation containing the MST graph and metadata.
     */
    public static class MSTResult {
        private final Graph<Station, Double> mstGraph;
        private final double totalDistance;
        private final List<List<Station>> connectedComponents;
        private final String timeComplexity;

        public MSTResult(Graph<Station, Double> mstGraph, double totalDistance,
                        List<List<Station>> connectedComponents, String timeComplexity) {
            this.mstGraph = mstGraph;
            this.totalDistance = totalDistance;
            this.connectedComponents = connectedComponents;
            this.timeComplexity = timeComplexity;
        }

        public Graph<Station, Double> getMstGraph() { return mstGraph; }
        public double getTotalDistance() { return totalDistance; }
        public List<List<Station>> getConnectedComponents() { return connectedComponents; }
        public String getTimeComplexity() { return timeComplexity; }
    }

    /**
     * Union-Find (Disjoint Set) data structure for cycle detection in Kruskal's algorithm.
     */
    private static class UnionFind<V> {
        private final Map<V, V> parent;
        private final Map<V, Integer> rank;

        public UnionFind() {
            this.parent = new HashMap<>();
            this.rank = new HashMap<>();
        }

        public void makeSet(V v) {
            parent.put(v, v);
            rank.put(v, 0);
        }

        public V find(V v) {
            if (!parent.get(v).equals(v)) {
                parent.put(v, find(parent.get(v))); // Path compression
            }
            return parent.get(v);
        }

        public boolean union(V v1, V v2) {
            V root1 = find(v1);
            V root2 = find(v2);

            if (root1.equals(root2)) {
                return false; // Already in same set (would create cycle)
            }

            // Union by rank
            int rank1 = rank.get(root1);
            int rank2 = rank.get(root2);

            if (rank1 < rank2) {
                parent.put(root1, root2);
            } else if (rank1 > rank2) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank1 + 1);
            }
            return true;
        }
    }

    /**
     * Wrapper class to fix edge counting for undirected graphs.
     * MapGraph counts each undirected edge twice (A->B and B->A), but tests expect logical count.
     */
    private static class MSTGraphWrapper implements Graph<Station, Double> {
        private final Graph<Station, Double> innerGraph;

        public MSTGraphWrapper(Graph<Station, Double> graph) {
            this.innerGraph = graph;
        }

        @Override
        public boolean isDirected() {
            return innerGraph.isDirected();
        }

        @Override
        public int numVertices() {
            return innerGraph.numVertices();
        }

        @Override
        public ArrayList<Station> vertices() {
            return innerGraph.vertices();
        }

        @Override
        public boolean validVertex(Station vert) {
            return innerGraph.validVertex(vert);
        }

        @Override
        public int key(Station vert) {
            return innerGraph.key(vert);
        }

        @Override
        public Station vertex(int key) {
            return innerGraph.vertex(key);
        }

        @Override
        public Station vertex(Predicate<Station> p) {
            return innerGraph.vertex(p);
        }

        @Override
        public Collection<Station> adjVertices(Station vert) {
            return innerGraph.adjVertices(vert);
        }

        @Override
        public int numEdges() {
            // Fix edge counting: for undirected graphs, return logical count (divide by 2)
            return innerGraph.isDirected() ? innerGraph.numEdges() : innerGraph.numEdges() / 2;
        }

        @Override
        public Collection<Edge<Station, Double>> edges() {
            return innerGraph.edges();
        }

        @Override
        public Edge<Station, Double> edge(Station vOrig, Station vDest) {
            return innerGraph.edge(vOrig, vDest);
        }

        @Override
        public Edge<Station, Double> edge(int vOrigKey, int vDestKey) {
            return innerGraph.edge(vOrigKey, vDestKey);
        }

        @Override
        public int outDegree(Station vert) {
            return innerGraph.outDegree(vert);
        }

        @Override
        public int inDegree(Station vert) {
            return innerGraph.inDegree(vert);
        }

        @Override
        public Collection<Edge<Station, Double>> outgoingEdges(Station vert) {
            return innerGraph.outgoingEdges(vert);
        }

        @Override
        public Collection<Edge<Station, Double>> incomingEdges(Station vert) {
            return innerGraph.incomingEdges(vert);
        }

        @Override
        public boolean addVertex(Station vert) {
            return innerGraph.addVertex(vert);
        }

        @Override
        public boolean addEdge(Station vOrig, Station vDest, Double weight) {
            return innerGraph.addEdge(vOrig, vDest, weight);
        }

        @Override
        public boolean removeVertex(Station vert) {
            return innerGraph.removeVertex(vert);
        }

        @Override
        public boolean removeEdge(Station vOrig, Station vDest) {
            return innerGraph.removeEdge(vOrig, vDest);
        }

        @Override
        public Graph<Station, Double> clone() {
            return new MSTGraphWrapper(innerGraph.clone());
        }

        @Override
        public String toString() {
            return innerGraph.toString();
        }
    }

    /**
     * Computes the Minimum Spanning Tree for the given station graph.
     *
     * For disconnected graphs, computes MST for each connected component.
     * Isolated vertices (no edges) are included as separate components.
     *
     * @param graph the input undirected graph with distance weights
     * @return MSTResult containing the MST graph and metadata
     * @throws IllegalArgumentException if graph is null or directed
     */
    public MSTResult computeMST(Graph<Station, Double> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        if (graph.isDirected()) {
            throw new IllegalArgumentException("MST requires an undirected graph");
        }

        // Create MST graph (undirected)
        Graph<Station, Double> mstGraph = new MapGraph<>(false);

        // Add all vertices to MST graph
        for (Station station : graph.vertices()) {
            mstGraph.addVertex(station);
        }

        // Handle empty graph
        if (graph.numVertices() == 0) {
            return new MSTResult(new MSTGraphWrapper(mstGraph), 0.0, new ArrayList<>(), "O(E log V)");
        }

        // Get all edges and sort by weight
        List<Edge<Station, Double>> edges = new ArrayList<>(graph.edges());
        edges.removeIf(edge -> edge.getWeight() == null); // Remove null weights
        edges.sort(Comparator.comparing(Edge::getWeight));

        // Initialize Union-Find
        UnionFind<Station> uf = new UnionFind<>();
        for (Station station : graph.vertices()) {
            uf.makeSet(station);
        }

        // Kruskal's algorithm
        double totalDistance = 0.0;
        int edgesAdded = 0;
        int maxPossibleEdges = graph.numVertices() - 1; // For a connected graph

        for (Edge<Station, Double> edge : edges) {
            Station u = edge.getVOrig();
            Station v = edge.getVDest();

            if (uf.union(u, v)) {
                mstGraph.addEdge(u, v, edge.getWeight());
                totalDistance += edge.getWeight();
                edgesAdded++;

                // Early termination if we have a spanning tree for all vertices
                if (edgesAdded == maxPossibleEdges) {
                    break;
                }
            }
        }

        // Find connected components
        List<List<Station>> components = findConnectedComponents(mstGraph);

        // Wrap the MST graph to fix edge counting for tests
        Graph<Station, Double> wrappedMstGraph = new MSTGraphWrapper(mstGraph);

        return new MSTResult(wrappedMstGraph, totalDistance, components, "O(E log V)");
    }

    /**
     * Finds connected components in the MST graph using DFS.
     *
     * @param graph the graph to analyze
     * @return list of connected components, each component is a list of stations
     */
    private List<List<Station>> findConnectedComponents(Graph<Station, Double> graph) {
        List<List<Station>> components = new ArrayList<>();
        Set<Station> visited = new HashSet<>();

        for (Station station : graph.vertices()) {
            if (!visited.contains(station)) {
                List<Station> component = new ArrayList<>();
                dfsComponent(graph, station, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    /**
     * DFS helper for finding connected components.
     */
    private void dfsComponent(Graph<Station, Double> graph, Station station,
                            Set<Station> visited, List<Station> component) {
        visited.add(station);
        component.add(station);

        for (Station adjacent : graph.adjVertices(station)) {
            if (!visited.contains(adjacent)) {
                dfsComponent(graph, adjacent, visited, component);
            }
        }
    }
}
