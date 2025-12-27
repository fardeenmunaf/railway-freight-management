package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Graph;

import java.io.IOException;
import java.util.*;

/**
 * Service for Rail Hub Centrality Analysis (USEI13).
 *
 * Computes centrality metrics to identify critical stations:
 * - Degree: number of direct connections
 * - Strength: sum of connection weights
 * - Betweenness: intermediation in shortest paths (Brandes algorithm)
 * - Harmonic Closeness: average accessibility
 * - HubScore: normalized composite metric
 *
 * Formula: hubScore = 0.35 * betweenness + 0.35 * harmonicCloseness + 0.30 * strengthNorm
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RailHubCentralityAnalysis {

    private final Graph<Station, Double> graph;
    private final Map<Integer, Station> stationMap;

    // Computed metrics
    private Map<Station, Integer> degreeMap;
    private Map<Station, Double> strengthMap;
    private Map<Station, Double> betweennessMap;
    private Map<Station, Double> harmonicClosenessMap;
    private Map<Station, Double> hubScoreMap;

    // Normalized values [0,1]
    private Map<Station, Double> normalizedBetweenness;
    private Map<Station, Double> normalizedHarmonicCloseness;
    private Map<Station, Double> normalizedStrength;

    /**
     * Constructor that initializes the service with data from StationLoaderService.
     *
     * @throws IOException if there is an error loading data
     */
    public RailHubCentralityAnalysis() throws IOException {
        this.graph = StationLoaderService.getGraph();
        this.stationMap = StationLoaderService.getStationMap();

        // Initialize maps
        this.degreeMap = new HashMap<>();
        this.strengthMap = new HashMap<>();
        this.betweennessMap = new HashMap<>();
        this.harmonicClosenessMap = new HashMap<>();
        this.hubScoreMap = new HashMap<>();
        this.normalizedBetweenness = new HashMap<>();
        this.normalizedHarmonicCloseness = new HashMap<>();
        this.normalizedStrength = new HashMap<>();
    }

    /**
     * Computes all centrality metrics and HubScore.
     *
     * Complexity: O(V + E) + O(VE) + O(V^2 * E) + O(V) = O(V^2 * E)
     */
    public void calculateAllMetrics() {
        computeDegreeAndStrength();       // O(V + E)
        computeBetweenness();             // O(VE) - Brandes
        computeHarmonicCloseness();       // O(V^2 * E) - Floyd-Warshall or APSP
        normalizeMetrics();               // O(V)
        computeHubScore();                // O(V)
    }

    /**
     * Computes degree and strength for all stations.
     *
     * Degree: number of edges connected to the station
     * Strength: sum of weights of all connected edges
     *
     * Complexity: O(V + E)
     */
    private void computeDegreeAndStrength() {
        // TODO: Implement degree and strength computation
        // For each vertex in the graph:
        //   - degree = number of edges (inDegree + outDegree for undirected graph)
        //   - strength = sum of edge weights
        // Use graph.outgoingEdges(vertex) and graph.incomingEdges(vertex)
    }

    /**
     * Computes betweenness centrality using Brandes algorithm (weighted version).
     *
     * Betweenness: measures how often a station appears in shortest paths
     * between other pairs of stations.
     *
     * Uses the WEIGHTED version because our graph has costs/distances on edges.
     * This ensures shortest paths consider actual costs, not just hop count.
     *
     * Brandes Algorithm with Dijkstra:
     * - For each vertex s, executes Dijkstra to find shortest paths
     * - Accumulates dependencies along the paths
     *
     * Complexity: O(VE + V^2 log V) for weighted graphs
     */
    private void computeBetweenness() {
        // TODO: Implement Brandes algorithm (WEIGHTED version)
        // Our graph has Double weights (costs/distances), so we MUST use the weighted version:
        //
        // Map<Station, Double> result = Algorithms.brandesBetweennessWeighted(
        //     graph,
        //     Double::compare,   // Compare doubles
        //     Double::sum,        // Sum doubles
        //     0.0                 // Zero element
        // );
        // betweennessMap.putAll(result);
    }

    /**
     * Computes harmonic closeness centrality.
     *
     * Harmonic Closeness: inverse of average distance to all other stations.
     * Advantage over traditional closeness: works with disconnected graphs.
     *
     * Formula: HC(v) = Σ(1/d(v,u)) for all u ≠ v, where d is distance
     *
     * Complexity: O(V^2 * E) using Floyd-Warshall or APSP with Dijkstra
     */
    private void computeHarmonicCloseness() {
        // TODO: Implement harmonic closeness computation
        // Option 1: Floyd-Warshall O(V^3) - simpler
        // Option 2: Dijkstra/BFS from each vertex O(V * (E + V log V)) - more efficient for sparse graphs
        //
        // For each vertex v:
        //   harmonicCloseness[v] = Σ(1 / distance(v, u)) for all u ≠ v
        //   If there is no path from v to u, contribution is 0
    }

    /**
     * Normalizes all metrics to the interval [0,1] per connected component.
     *
     * Normalization: (value - min) / (max - min)
     * If max == min, assigns 0 (or 1 if all are equal)
     *
     * Complexity: O(V)
     */
    private void normalizeMetrics() {
        // TODO: Implement normalization
        // For each metric (betweenness, harmonicCloseness, strength):
        //   1. Find min and max
        //   2. For each vertex: normalized = (value - min) / (max - min)
        //   3. Special case: if max == min, all receive 1.0 (or 0.0)
    }

    /**
     * Computes the composite HubScore for each station.
     *
     * Formula: HubScore = 0.35 * betweennessNorm + 0.35 * harmonicClosenessNorm + 0.30 * strengthNorm
     *
     * Weights chosen to balance:
     * - Betweenness (35%): importance in paths
     * - Harmonic Closeness (35%): global accessibility
     * - Strength (30%): capacity/importance of connections
     *
     * Complexity: O(V)
     */
    private void computeHubScore() {
        // TODO: Implement HubScore computation
        // For each vertex:
        //   hubScore[v] = 0.35 * normalizedBetweenness[v]
        //               + 0.35 * normalizedHarmonicCloseness[v]
        //               + 0.30 * normalizedStrength[v]
    }

    /**
     * Returns analysis results sorted by HubScore in descending order.
     *
     * @return list of results with all metrics
     */
    public List<HubAnalysisResult> getRankedResults() {
        List<HubAnalysisResult> results = new ArrayList<>();

        for (Station station : graph.vertices()) {
            HubAnalysisResult result = new HubAnalysisResult(
                station.getFacilityId(),
                station.getName(),
                degreeMap.getOrDefault(station, 0),
                strengthMap.getOrDefault(station, 0.0),
                betweennessMap.getOrDefault(station, 0.0),
                harmonicClosenessMap.getOrDefault(station, 0.0),
                hubScoreMap.getOrDefault(station, 0.0)
            );
            results.add(result);
        }

        // Sort by HubScore in descending order
        results.sort((a, b) -> Double.compare(b.getHubScore(), a.getHubScore()));

        return results;
    }

    /**
     * Returns only the top N hubs.
     *
     * @param n number of hubs to return
     * @return list with the N most important hubs
     */
    public List<HubAnalysisResult> getTopNHubs(int n) {
        List<HubAnalysisResult> allResults = getRankedResults();
        return allResults.subList(0, Math.min(n, allResults.size()));
    }

    /**
     * Exports results in CSV format.
     *
     * @return CSV string with header and all stations
     */
    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("StationID,StationName,Degree,Strength,Betweenness,HarmonicCloseness,HubScore\n");

        for (HubAnalysisResult result : getRankedResults()) {
            csv.append(String.format("%d,%s,%d,%.4f,%.4f,%.4f,%.4f\n",
                result.getStationId(),
                result.getStationName(),
                result.getDegree(),
                result.getStrength(),
                result.getBetweenness(),
                result.getHarmonicCloseness(),
                result.getHubScore()
            ));
        }

        return csv.toString();
    }

    /**
     * Returns temporal complexity analysis.
     *
     * @return string with detailed complexity analysis
     */
    public String getTemporalComplexityAnalysis() {
        int V = graph.numVertices();
        int E = graph.numEdges();

        return String.format("""
            === TEMPORAL COMPLEXITY ANALYSIS (USEI13) ===
            
            Graph size:
            - Vertices (V): %d stations
            - Edges (E): %d connections
            
            Complexity per operation:
            1. Degree & Strength:          O(V + E) = O(%d + %d)
            2. Betweenness (Brandes):      O(VE) = O(%d × %d)
            3. Harmonic Closeness (APSP):  O(V² × E) = O(%d² × %d)
            4. Normalization:              O(V) = O(%d)
            5. HubScore:                   O(V) = O(%d)
            
            Total complexity: O(V² × E) = O(%d² × %d) = O(%d)
            
            Note: For sparse graphs (E ≈ V), complexity ≈ O(V³)
                  For dense graphs (E ≈ V²), complexity ≈ O(V⁴)
            """,
            V, E,
            V, E,
            V, E,
            V, E,
            V,
            V,
            V, E, V * V * E
        );
    }

    /**
     * Class to store hub analysis results.
     */
    public static class HubAnalysisResult {
        private final int stationId;
        private final String stationName;
        private final int degree;
        private final double strength;
        private final double betweenness;
        private final double harmonicCloseness;
        private final double hubScore;

        public HubAnalysisResult(int stationId, String stationName, int degree,
                                double strength, double betweenness,
                                double harmonicCloseness, double hubScore) {
            this.stationId = stationId;
            this.stationName = stationName;
            this.degree = degree;
            this.strength = strength;
            this.betweenness = betweenness;
            this.harmonicCloseness = harmonicCloseness;
            this.hubScore = hubScore;
        }

        // Getters
        public int getStationId() { return stationId; }
        public String getStationName() { return stationName; }
        public int getDegree() { return degree; }
        public double getStrength() { return strength; }
        public double getBetweenness() { return betweenness; }
        public double getHarmonicCloseness() { return harmonicCloseness; }
        public double getHubScore() { return hubScore; }

        @Override
        public String toString() {
            return String.format(
                "Station: %s (ID:%d) | Degree: %d | Strength: %.2f | " +
                "Betweenness: %.4f | Harmonic Closeness: %.4f | HubScore: %.4f",
                stationName, stationId, degree, strength,
                betweenness, harmonicCloseness, hubScore
            );
        }
    }
}

