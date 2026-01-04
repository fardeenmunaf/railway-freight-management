package app.service.sprint3;

import app.model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for {@link ShortestPathService}.
 *
 * Covers:
 *  - Constructor initialization
 *  - Path calculation between valid stations
 *  - Invalid station handling
 *  - Same station path calculation
 *  - PathResult formatting (normal and negative cycle)
 *  - PathResultWithAlgorithm getters
 *  - End-to-end flow using actual CSV data
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ShortestPathServiceTest {

    private ShortestPathService pathService;

    @BeforeEach
    public void setUp() throws IOException {
        // Reset singleton to ensure clean state
        StationLoaderService.resetInstance();

        // Initialize service (loads data from CSV)
        pathService = new ShortestPathService();
    }

    @Test
    public void testConstructor_InitializesService() throws IOException {
        ShortestPathService service = new ShortestPathService();
        assertNotNull(service, "Service should be initialized");

        // Verify that data is loaded
        assertNotNull(StationLoaderService.getGraph(), "Graph should be loaded");
        assertNotNull(StationLoaderService.getStationMap(), "Station map should be loaded");
    }

    @Test
    public void testCalculateBestPath_ValidStations_ReturnsPath() throws IOException {
        // Get two connected stations from the actual graph
        Station start = null;
        Station end = null;

        var graph = StationLoaderService.getGraph();
        for (Station station : graph.vertices()) {
            if (graph.outgoingEdges(station).iterator().hasNext()) {
                start = station;
                var edge = graph.outgoingEdges(station).iterator().next();
                end = edge.getVDest();
                break;
            }
        }

        assertNotNull(start, "Should find at least one connected station");
        assertNotNull(end, "Should find at least one destination");

        ShortestPathService.PathResultWithAlgorithm result =
            pathService.calculateBestPath(start.getFacilityId(), end.getFacilityId());

        assertNotNull(result, "Path should exist between connected stations");
        assertNotNull(result.getAlgorithmUsed(), "Algorithm should be selected");
        assertTrue(result.getAlgorithmUsed().equals("Dijkstra") ||
                  result.getAlgorithmUsed().equals("Bellman-Ford"),
                  "Algorithm should be either Dijkstra or Bellman-Ford");
    }

    @Test
    public void testCalculateBestPath_InvalidDeparture_ReturnsNull() throws IOException {
        var stationMap = StationLoaderService.getStationMap();
        Integer validId = stationMap.keySet().stream().findFirst().orElse(null);
        assertNotNull(validId);

        ShortestPathService.PathResultWithAlgorithm result =
            pathService.calculateBestPath(-9999, validId);

        assertNull(result, "Should return null for invalid departure station");
    }

    @Test
    public void testCalculateBestPath_InvalidArrival_ReturnsNull() throws IOException {
        var stationMap = StationLoaderService.getStationMap();
        Integer validId = stationMap.keySet().stream().findFirst().orElse(null);
        assertNotNull(validId);

        ShortestPathService.PathResultWithAlgorithm result =
            pathService.calculateBestPath(validId, -9999);

        assertNull(result, "Should return null for invalid arrival station");
    }

    @Test
    public void testCalculateBestPath_BothInvalid_ReturnsNull() {
        ShortestPathService.PathResultWithAlgorithm result =
            pathService.calculateBestPath(-9999, -8888);

        assertNull(result, "Should return null for both invalid stations");
    }

    @Test
    public void testCalculateBestPath_SameStation_ReturnsZeroCost() throws IOException {
        var stationMap = StationLoaderService.getStationMap();
        Integer stationId = stationMap.keySet().stream().findFirst().orElse(null);
        assertNotNull(stationId);

        ShortestPathService.PathResultWithAlgorithm result =
            pathService.calculateBestPath(stationId, stationId);

        assertNotNull(result, "Should return result for same station path");
        assertNotNull(result.getPathResult(), "PathResult should exist");
        assertEquals(0.0, result.getPathResult().totalCost, 0.001,
                    "Cost to same station should be zero");
    }

    @Test
    public void testPathResult_FormatPath_ContainsStationNames() throws IOException {
        // Get two connected stations
        Station start = null;
        Station end = null;

        var graph = StationLoaderService.getGraph();
        for (Station station : graph.vertices()) {
            if (graph.outgoingEdges(station).iterator().hasNext()) {
                start = station;
                var edge = graph.outgoingEdges(station).iterator().next();
                end = edge.getVDest();
                break;
            }
        }

        if (start != null && end != null) {
            ShortestPathService.PathResultWithAlgorithm result =
                pathService.calculateBestPath(start.getFacilityId(), end.getFacilityId());

            if (result != null) {
                String formatted = result.getPathResult().formatPath();

                assertNotNull(formatted, "Formatted path should not be null");
                assertTrue(formatted.contains(start.getName()),
                          "Formatted path should contain start station name");
                assertTrue(formatted.contains(end.getName()),
                          "Formatted path should contain end station name");
                assertTrue(formatted.contains("Total cost:"),
                          "Formatted path should contain total cost");
            }
        }
    }

    @Test
    public void testPathResult_FormatPath_EmptyPath_ContainsCost() {
        LinkedList<Station> emptyPath = new LinkedList<>();

        ShortestPathService.PathResult pathResult = pathService.new PathResult(
            emptyPath, 0.0, false, java.util.Set.of(), java.util.Set.of()
        );

        String formatted = pathResult.formatPath();
        assertNotNull(formatted);
        assertTrue(formatted.contains("Total cost:"),
                  "Empty path should still show total cost");
    }

    @Test
    public void testPathResult_FormatPath_WithNegativeCycle() throws IOException {
        var stationMap = StationLoaderService.getStationMap();
        LinkedList<Station> path = new LinkedList<>();

        Station station1 = stationMap.values().stream().findFirst().orElse(null);
        Station station2 = stationMap.values().stream().skip(1).findFirst().orElse(null);

        if (station1 != null && station2 != null) {
            java.util.Set<Station> cycleStations = java.util.Set.of(station1, station2);
            java.util.Set<app.utils.graph.Edge<Station, app.model.EdgeData>> cycleEdges = java.util.Set.of();

            ShortestPathService.PathResult pathResult = pathService.new PathResult(
                path, null, true, cycleStations, cycleEdges
            );

            String formatted = pathResult.formatPath();
            assertNotNull(formatted, "Formatted path should not be null");
            assertTrue(formatted.contains("Negative cycle detected"),
                      "Formatted path should contain negative cycle message");
            assertTrue(formatted.contains("Stations:"),
                      "Formatted path should contain stations section");
        }
    }

    @Test
    public void testPathResultWithAlgorithm_GettersWork() throws IOException {
        var stationMap = StationLoaderService.getStationMap();
        LinkedList<Station> path = new LinkedList<>();
        Station station = stationMap.values().stream().findFirst().orElse(null);
        if (station != null) {
            path.add(station);
        }

        ShortestPathService.PathResult pathResult = pathService.new PathResult(
            path, 10.0, false, java.util.Set.of(), java.util.Set.of()
        );
        ShortestPathService.PathResultWithAlgorithm result =
            new ShortestPathService.PathResultWithAlgorithm(pathResult, "Dijkstra", false);

        assertEquals("Dijkstra", result.getAlgorithmUsed(),
                    "getAlgorithmUsed should return correct algorithm");
        assertEquals(pathResult, result.getPathResult(),
                    "getPathResult should return correct PathResult");
        assertFalse(result.hasNegativeCycle(),
                   "hasNegativeCycle should return false");
    }

    @Test
    public void testPathResultWithAlgorithm_NegativeCycleFlag() {
        LinkedList<Station> path = new LinkedList<>();

        ShortestPathService.PathResult pathResult = pathService.new PathResult(
            path, null, true, java.util.Set.of(), java.util.Set.of()
        );
        ShortestPathService.PathResultWithAlgorithm result =
            new ShortestPathService.PathResultWithAlgorithm(pathResult, "Bellman-Ford", true);

        assertTrue(result.hasNegativeCycle(),
                  "hasNegativeCycle should return true when negative cycle exists");
    }

    @Test
    public void testGraphDataIntegrity_NotNull() throws IOException {
        var graph = StationLoaderService.getGraph();
        var stationMap = StationLoaderService.getStationMap();

        assertNotNull(graph, "Graph should be loaded");
        assertNotNull(stationMap, "Station map should be loaded");
    }

    @Test
    public void testGraphDataIntegrity_HasVerticesAndEdges() throws IOException {
        var graph = StationLoaderService.getGraph();

        assertTrue(graph.numVertices() > 0, "Graph should have vertices");
        assertTrue(graph.numEdges() > 0, "Graph should have edges");
    }

    @Test
    public void testGraphDataIntegrity_StationsInMapAreInGraph() throws IOException {
        var graph = StationLoaderService.getGraph();
        var stationMap = StationLoaderService.getStationMap();

        for (Station station : stationMap.values()) {
            assertTrue(graph.validVertex(station),
                      "All stations in map should exist in graph");
        }
    }

    @Test
    public void testFullFlow_PathCalculationAndFormatting() throws IOException {
        // Step 1: Find two connected stations
        Station start = null;
        Station end = null;

        var graph = StationLoaderService.getGraph();
        for (Station station : graph.vertices()) {
            if (graph.outgoingEdges(station).iterator().hasNext()) {
                start = station;
                var edge = graph.outgoingEdges(station).iterator().next();
                end = edge.getVDest();
                break;
            }
        }

        assertNotNull(start, "Should find connected stations for full flow test");
        assertNotNull(end, "Should find connected stations for full flow test");

        // Step 2: Calculate path
        ShortestPathService.PathResultWithAlgorithm result =
            pathService.calculateBestPath(start.getFacilityId(), end.getFacilityId());

        // Step 3: Verify result structure
        assertNotNull(result, "Path should exist between connected stations");
        assertNotNull(result.getAlgorithmUsed(), "Algorithm should be selected");
        assertNotNull(result.getPathResult(), "PathResult should exist");

        // Step 4: Verify path properties
        ShortestPathService.PathResult pathResult = result.getPathResult();
        assertNotNull(pathResult.path, "Path should not be null");
        assertTrue(pathResult.path.size() >= 2, "Path should contain at least start and end");
        assertEquals(start, pathResult.path.getFirst(), "Path should start with departure station");
        assertEquals(end, pathResult.path.getLast(), "Path should end with arrival station");

        // Step 5: Verify cost
        assertNotNull(pathResult.totalCost, "Total cost should be calculated");

        // Step 6: Verify no negative cycle
        assertFalse(result.hasNegativeCycle(), "Should not have negative cycle for normal path");

        // Step 7: Verify path continuity
        for (int i = 0; i < pathResult.path.size() - 1; i++) {
            Station current = pathResult.path.get(i);
            Station next = pathResult.path.get(i + 1);

            boolean connected = false;
            for (var edge : graph.outgoingEdges(current)) {
                if (edge.getVDest().equals(next)) {
                    connected = true;
                    break;
                }
            }
            assertTrue(connected, "Each station in path should be connected to the next");
        }

        // Step 8: Verify formatting
        String formatted = pathResult.formatPath();
        assertNotNull(formatted, "Formatted output should not be null");
        assertTrue(formatted.contains(start.getName()), "Formatted output should contain start station");
        assertTrue(formatted.contains(end.getName()), "Formatted output should contain end station");
        assertTrue(formatted.contains("Total cost:"), "Formatted output should contain total cost");
    }
}

