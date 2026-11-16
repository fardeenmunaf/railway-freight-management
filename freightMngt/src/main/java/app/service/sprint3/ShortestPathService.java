package app.service.sprint3;

import app.model.EdgeData;
import app.model.Station;
import app.utils.graph.Algorithms;
import app.utils.graph.Edge;
import app.utils.graph.Graph;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for calculating shortest paths between stations (USEI15).
 *
 * Implements pathfinding algorithms with automatic detection:
 * - Dijkstra for graphs without negative weights (more efficient)
 * - Bellman-Ford for graphs with negative weights (more robust)
 * - Negative cycle detection
 *
 * @author Fardeen
 * @version 1.0
 */
public class ShortestPathService {

    private final Graph<Station, EdgeData> graph;
    private final Map<Integer, Station> stationMap;

    /**
     * Constructor that initializes the service with data from StationLoaderService.
     *
     * @throws IOException if there is an error loading data
     */
    public ShortestPathService() throws IOException {
        this.graph = StationLoaderService.getGraph();
        this.stationMap = StationLoaderService.getStationMap();
    }

    /**
     * Calculates the shortest path between two stations automatically choosing
     * the appropriate algorithm (Dijkstra or Bellman-Ford).
     *
     * Strategy:
     * <ul>
     *   <li>Tries Dijkstra first (more efficient)</li>
     *   <li>If it fails or path has negative weights → Bellman-Ford</li>
     *   <li>Detects negative cycles and reports involved stations/edges</li>
     * </ul>
     *
     * @param departureId ID of the departure station
     * @param arrivalId ID of the arrival station
     * @return result with path, cost, algorithm used and cycle detection,
     *         or null if stations don't exist
     */
    public PathResultWithAlgorithm calculateBestPath(Integer departureId, Integer arrivalId) {
        Station start = stationMap.get(departureId);
        Station end = stationMap.get(arrivalId);
        if (start == null || end == null) return null;

        // 1. Find path with Dijkstra FIRST
        LinkedList<Station> path = new LinkedList<>();
        EdgeData costData = Algorithms.shortestPath(graph, start, end, Comparator.comparingDouble(EdgeData::getCost), // Compare by cost
            (e1, e2) -> new EdgeData(0, 0, e1.getCost() + e2.getCost()),  // Sum costs
            new EdgeData(0, 0, 0.0),  // Zero element
            path);

        if (costData == null) {
            // NO PATH - try Bellman-Ford for negative cycle
            LinkedList<Station> pathBF = new LinkedList<>();
            EdgeData costBF = Algorithms.bellmanFord(graph, start, end, Comparator.comparingDouble(EdgeData::getCost),
                (e1, e2) -> new EdgeData(0, 0, e1.getCost() + e2.getCost()),
                new EdgeData(0, 0, 0.0),
                pathBF);
            if (costBF == null) {
                Set<Station> cycleStations = findNegativeCycleStations(start);
                Set<Edge<Station, EdgeData>> cycleEdges = findNegativeCycleEdges(cycleStations);
                PathResult result = new PathResult(new LinkedList<>(), null, true, cycleStations, cycleEdges);
                return new PathResultWithAlgorithm(result, "Bellman-Ford", true);
            }
            // Bellman-Ford found path
            PathResult result = new PathResult(pathBF, costBF.getCost(), false, Collections.emptySet(), Collections.emptySet());
            return new PathResultWithAlgorithm(result, "Bellman-Ford", false);
        }

        // 2. Dijkstra found path - CHECK if it has negatives
        boolean hasNegativeInPath = hasNegativeEdgesInPath(path);

        PathResult result = new PathResult(path, costData.getCost(), false, Collections.emptySet(), Collections.emptySet());
        String algorithm = hasNegativeInPath ? "Bellman-Ford" : "Dijkstra";

        return new PathResultWithAlgorithm(result, algorithm, false);
    }

    /**
     * Checks if the specific path contains edges with negative cost.
     *
     * @param path ordered list of stations in the path
     * @return true if any consecutive edge has negative cost
     */
    private boolean hasNegativeEdgesInPath(List<Station> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);
            for (Edge<Station, EdgeData> edge : graph.outgoingEdges(from)) {
                if (edge.getVDest().equals(to)) {
                    EdgeData edgeData = edge.getWeight();
                    if (edgeData != null && edgeData.getCost() < 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds reachable stations from an initial station using DFS.
     *
     * @param current initial station
     * @param reachable set of reachable stations (output)
     */
    private void dfsReachable(Station current, Set<Station> reachable) {
        if (reachable.contains(current)) return;
        reachable.add(current);
        for (Edge<Station, EdgeData> e : graph.outgoingEdges(current)) {
            dfsReachable(e.getVDest(), reachable);
        }
    }

    /**
     * Identifies stations involved in negative cycles reachable from the initial station.
     *
     * @param start initial station to determine connected component
     * @return set of stations involved in negative edges
     */
    private Set<Station> findNegativeCycleStations(Station start) {
        Set<Station> reachable = new HashSet<>();
        dfsReachable(start, reachable);

        Set<Station> cycleStations = new HashSet<>();
        for (Station v : reachable) {
            for (Edge<Station, EdgeData> e : graph.outgoingEdges(v)) {
                EdgeData edgeData = e.getWeight();
                if (edgeData != null && edgeData.getCost() < 0 && reachable.contains(e.getVDest())) {
                    cycleStations.add(v);
                    cycleStations.add(e.getVDest());
                }
            }
        }
        return cycleStations;
    }

    /**
     * Finds negative edges between the specified stations.
     *
     * @param cycleStations candidate stations for the negative cycle
     * @return set of negative edges that form the cycle
     */
    private Set<Edge<Station, EdgeData>> findNegativeCycleEdges(Set<Station> cycleStations) {
        Set<Edge<Station, EdgeData>> edges = new HashSet<>();
        for (Station v : cycleStations) {
            for (Edge<Station, EdgeData> e : graph.outgoingEdges(v)) {
                EdgeData edgeData = e.getWeight();
                if (cycleStations.contains(e.getVDest()) && edgeData != null && edgeData.getCost() < 0) {
                    edges.add(e);
                }
            }
        }
        return edges;
    }

    /**
     * Result of shortest path calculation with information about the algorithm used.
     */
    public static class PathResultWithAlgorithm {
        private final PathResult pathResult;
        private final String algorithmUsed;
        private final boolean hasNegativeCycle;

        public PathResultWithAlgorithm(PathResult pathResult, String algorithmUsed, boolean hasNegativeCycle) {
            this.pathResult = pathResult;
            this.algorithmUsed = algorithmUsed;
            this.hasNegativeCycle = hasNegativeCycle;
        }

        public PathResult getPathResult() { return pathResult; }
        public String getAlgorithmUsed() { return algorithmUsed; }
        public boolean hasNegativeCycle() { return hasNegativeCycle; }
    }

    /**
     * Detailed result of shortest path calculation.
     */
    public class PathResult {
        final LinkedList<Station> path;
        final Double totalCost;
        private final boolean hasNegativeCycle;
        private final Set<Station> cycleStations;
        private final Set<Edge<Station, EdgeData>> cycleEdges;

        public PathResult(LinkedList<Station> path, Double totalCost, boolean hasNegativeCycle,
                          Set<Station> cycleStations, Set<Edge<Station, EdgeData>> cycleEdges) {
            this.path = path;
            this.totalCost = totalCost;
            this.hasNegativeCycle = hasNegativeCycle;
            this.cycleStations = cycleStations;
            this.cycleEdges = cycleEdges;
        }

        /**
         * Formats the result for presentation.
         */
        public String formatPath() {
            if (hasNegativeCycle) {
                StringBuilder sb = new StringBuilder("Negative cycle detected involving:\n");
                sb.append("Stations: ");
                sb.append(cycleStations.stream().map(Station::getName).collect(Collectors.joining(", ")));
                sb.append("\n\nEdges: ");
                sb.append(cycleEdges.stream()
                        .map(e -> {
                            EdgeData edgeData = e.getWeight();
                            double cost = edgeData != null ? edgeData.getCost() : 0.0;
                            return String.format("%s(%d)->%s(%d):%.2f",
                                    e.getVOrig().getName(), e.getVOrig().getFacilityId(),
                                    e.getVDest().getName(), e.getVDest().getFacilityId(), cost);
                        })
                        .collect(Collectors.joining(", ")));
                return sb.toString();
            }

            StringBuilder route = new StringBuilder("Route: ");
            StringBuilder legs = new StringBuilder("Legs:\n");

            // Path with individual costs: each station appears once in the route,
            // and every leg is listed with its own cost
            double total = 0.0;
            if (!path.isEmpty()) {
                route.append(String.format("%s (%d)", path.get(0).getName(), path.get(0).getFacilityId()));
            }
            for (int i = 0; i < path.size() - 1; i++) {
                Station from = path.get(i);
                Station to = path.get(i + 1);

                // Find exact edge
                Edge<Station, EdgeData> edge = null;
                for (Edge<Station, EdgeData> e : graph.outgoingEdges(from)) {
                    if (e.getVDest().equals(to)) {
                        edge = e;
                        break;
                    }
                }

                double cost = 0.0;
                if (edge != null && edge.getWeight() != null) {
                    cost = edge.getWeight().getCost();
                }
                total += cost;

                route.append(String.format(" -> %s (%d)", to.getName(), to.getFacilityId()));
                legs.append(String.format(Locale.US, "  %2d. %-28s -> %-28s %8.2f%n",
                        i + 1,
                        from.getName() + " (" + from.getFacilityId() + ")",
                        to.getName() + " (" + to.getFacilityId() + ")",
                        cost));
            }

            StringBuilder sb = new StringBuilder();
            sb.append(route).append("\n\n");
            if (path.size() > 1) {
                sb.append(legs).append("\n");
            }
            sb.append("Stops: ").append(path.size())
              .append(" | Total cost: ").append(String.format(Locale.US, "%.2f", total));
            return sb.toString();
        }
    }
}

