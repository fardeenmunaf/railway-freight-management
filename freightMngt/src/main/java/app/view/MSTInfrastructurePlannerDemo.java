package app.view;

import app.controller.MSTController;
import app.model.Station;
import app.service.sprint3.StationLoaderService;
import app.service.sprint3.MSTValidationService;
import app.utils.graph.Edge;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;

import java.io.IOException;
import java.util.List;

/**
 * User Interface / Demonstration for MST (Minimum Spanning Tree) Infrastructure Planning.
 *
 * USEI12 — Minimal Backbone Network
 *
 * This demonstration shows how to use the MST services to compute a minimum network
 * over the Belgian rail network to identify a minimal backbone that connects all stations
 * with minimum total track length.
 *
 * Based on Belgian railway data from Infrabel Open Data:
 * https://opendata.infrabel.be/explore/dataset/station_to_station/
 *
 * Usage:
 * - Load Belgian railway network from CSV files
 * - Compute MST using MSTController
 * - Export results to DOT and SVG formats
 * - Validate against acceptance criteria
 *
 * @author Infrastructure Planning Team
 * @version 2.0 - Belgian Railway Network Integration
 */
public class MSTInfrastructurePlannerDemo {

    private final MSTController controller;
    private final StationLoaderService loaderService;
    private final MSTValidationService validationService;

    public MSTInfrastructurePlannerDemo() {
        this.controller = new MSTController();
        this.loaderService = new StationLoaderService();
        this.validationService = new MSTValidationService();
    }

    /**
     * Main entry point for MST infrastructure planning demonstration.
     */
    public static void main(String[] args) {
        try {
            MSTInfrastructurePlannerDemo demo = new MSTInfrastructurePlannerDemo();

            System.out.println("=== USEI12: Minimal Backbone Network - Belgian Railway System ===\n");

            // Run demonstration scenarios
            demo.demonstrateConnectedNetwork();
            demo.demonstrateDisconnectedNetwork();
            demo.demonstrateIsolatedStations();

        } catch (Exception e) {
            System.err.println("Error running MST demonstration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates MST computation for the complete Belgian railway network.
     */
    public void demonstrateConnectedNetwork() throws IOException {
        System.out.println("--- Scenario 1: Belgian Railway Network (Minimal Backbone) ---\n");

        // Load network from CSV
        Graph<Station, Double> network = loaderService.loadNetwork(
            "src/main/java/app/sampleDataset/stations.csv",
            "src/main/java/app/sampleDataset/lines.csv"
        );

        System.out.println("Loaded network: " + network.numVertices() + " stations, " +
                          network.numEdges() + " connections\n");

        // Compute MST and export
        MSTController.WorkflowResult result = controller.computeAndExportMST(
            network, "outputs/belgian_railway_mst", true);

        // Display results
        System.out.println(result.getSummaryReport());

        // Validate acceptance criteria
        MSTValidationService.ValidationResult validation =
            validationService.validateAcceptanceCriteria(result);
        System.out.println(validation.getReport());

        System.out.println("✓ Scenario 1 completed!\n");
    }

    /**
     * Demonstrates MST for disconnected regional networks.
     */
    public void demonstrateDisconnectedNetwork() throws IOException {
        System.out.println("--- Scenario 2: Disconnected Regional Networks ---\n");

        // Load full network
        Graph<Station, Double> fullNetwork = loaderService.loadNetwork(
            "src/main/java/app/sampleDataset/stations.csv",
            "src/main/java/app/sampleDataset/lines.csv"
        );

        // Create subset with limited connections
        Graph<Station, Double> subset = createLimitedSubset(fullNetwork, 10, 4);

        System.out.println("Subset network: " + subset.numVertices() + " stations, " +
                          subset.numEdges() + " connections\n");

        // Compute MST
        MSTController.WorkflowResult result = controller.computeAndExportMST(
            subset, "outputs/regional_networks_mst", false);

        System.out.println(result.getSummaryReport());
        System.out.println("✓ Scenario 2 completed!\n");
    }

    /**
     * Demonstrates handling of isolated stations (no connections).
     */
    public void demonstrateIsolatedStations() throws IOException {
        System.out.println("--- Scenario 3: Isolated Stations (No Edges) ---\n");

        // Load stations only
        List<Station> stations = loaderService.loadStations(
            "src/main/java/app/sampleDataset/stations.csv");

        // Create graph with first 5 stations, no edges
        Graph<Station, Double> isolatedGraph = new MapGraph<>(false);
        for (int i = 0; i < Math.min(5, stations.size()); i++) {
            isolatedGraph.addVertex(stations.get(i));
        }

        System.out.println("Isolated network: " + isolatedGraph.numVertices() + " stations, " +
                          isolatedGraph.numEdges() + " connections\n");

        // Compute MST
        MSTController.WorkflowResult result = controller.computeAndExportMST(
            isolatedGraph, "outputs/isolated_stations_mst", false);

        System.out.println(result.getSummaryReport());
        System.out.println("✓ Scenario 3 completed!\n");
    }

    /**
     * Creates a limited subset of the network for testing disconnected components.
     * This is a helper method for demonstration purposes.
     */
    private Graph<Station, Double> createLimitedSubset(Graph<Station, Double> fullNetwork,
                                                       int maxVertices, int maxEdgesPerVertex) {
        Graph<Station, Double> subset = new MapGraph<>(false);

        // Add limited vertices
        int count = 0;
        for (Station station : fullNetwork.vertices()) {
            if (count++ >= maxVertices) break;
            subset.addVertex(station);
        }

        // Add limited edges
        for (Station station : subset.vertices()) {
            int edgeCount = 0;
            for (Station adj : fullNetwork.adjVertices(station)) {
                if (subset.validVertex(adj) && edgeCount++ < maxEdgesPerVertex) {
                    Edge<Station, Double> existingEdge = subset.edge(station, adj);
                    if (existingEdge == null) {
                        Double weight = fullNetwork.edge(station, adj).getWeight();
                        if (weight != null) {
                            subset.addEdge(station, adj, weight);
                        }
                    }
                }
            }
        }

        return subset;
    }
}

