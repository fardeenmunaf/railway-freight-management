package app.controller;

import app.model.EdgeData;
import app.model.Station;
import app.service.sprint3.MinimumSpanningTreeService;
import app.service.sprint3.MSTDotExportService;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MSTController.
 *
 * Tests the integration of MST computation and DOT export services.
 * Validates the complete workflow and controller functionality.
 *
 * @author Test Infrastructure
 */
class MSTControllerTest {

    private MSTController controller;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        controller = new MSTController();
    }

    @Nested
    @DisplayName("MST Computation Tests")
    class MSTComputationTests {

        @Test
        @DisplayName("Should compute MST through controller")
        void testComputeMST() {
            // Arrange
            Graph<Station, EdgeData> graph = createTestGraph();

            // Act
            MinimumSpanningTreeService.MSTResult result = controller.computeMinimumSpanningTree(graph);

            // Assert
            assertNotNull(result);
            assertEquals(4, result.getMstGraph().numVertices());
            assertEquals(6, result.getMstGraph().numEdges());
            assertEquals(1, result.getConnectedComponents().size());
            assertEquals("O(E log V)", result.getTimeComplexity());
            assertTrue(result.getTotalDistance() > 0);
        }

        @Test
        @DisplayName("Should reject null graph in controller")
        void testNullGraphRejection() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.computeMinimumSpanningTree(null)
            );

            assertEquals("Station graph cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Visualization Export Tests")
    class VisualizationExportTests {

        @Test
        @DisplayName("Should export MST visualization")
        void testExportVisualization() throws IOException {
            // Arrange
            Graph<Station, EdgeData> graph = createTestGraph();
            MinimumSpanningTreeService.MSTResult mstResult = controller.computeMinimumSpanningTree(graph);
            String outputPath = tempDir.resolve("test_export").toString();

            // Act
            MSTDotExportService.ExportResult exportResult = controller.exportMSTVisualization(
                mstResult, outputPath, false);

            // Assert
            assertNotNull(exportResult);
            assertNotNull(exportResult.getDotContent());
            assertTrue(exportResult.getDotFilePath().endsWith(".dot"));
            assertFalse(exportResult.isSvgGenerated()); // SVG not requested
        }

        @Test
        @DisplayName("Should reject null MST result in export")
        void testNullMSTResultRejection() {
            // Arrange
            String outputPath = tempDir.resolve("test").toString();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.exportMSTVisualization(null, outputPath, false)
            );

            assertEquals("MST result cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Complete Workflow Tests")
    class WorkflowTests {

        @Test
        @DisplayName("Should execute complete workflow successfully")
        void testCompleteWorkflow() throws IOException {
            // Arrange
            Graph<Station, EdgeData> graph = createTestGraph();
            String outputPath = tempDir.resolve("workflow_test").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(graph, outputPath, false);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getMstResult());
            assertNotNull(result.getExportResult());

            // Verify MST computation
            MinimumSpanningTreeService.MSTResult mstResult = result.getMstResult();
            assertEquals(4, mstResult.getMstGraph().numVertices());
            assertEquals(6, mstResult.getMstGraph().numEdges());

            // Verify export
            MSTDotExportService.ExportResult exportResult = result.getExportResult();
            assertNotNull(exportResult.getDotContent());
            assertTrue(exportResult.getDotFilePath().endsWith(".dot"));
        }

        @Test
        @DisplayName("Should generate comprehensive summary report")
        void testSummaryReport() throws IOException {
            // Arrange
            Graph<Station, EdgeData> graph = createDisconnectedTestGraph();
            String outputPath = tempDir.resolve("summary_test").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(graph, outputPath, false);
            String summary = result.getSummaryReport();

            // Assert
            assertNotNull(summary);
            assertFalse(summary.isEmpty());

            // Verify report contains expected sections
            assertTrue(summary.contains("=== MST Computation Summary ==="));
            assertTrue(summary.contains("=== Export Summary ==="));
            assertTrue(summary.contains("Total Distance:"));
            assertTrue(summary.contains("Connected Components:"));
            assertTrue(summary.contains("Time Complexity: O(E log V)"));
            assertTrue(summary.contains("Vertices in MST:"));
            assertTrue(summary.contains("Edges in MST:"));
            assertTrue(summary.contains("DOT file:"));

            // For disconnected graph, should show component details
            if (result.getMstResult().getConnectedComponents().size() > 1) {
                assertTrue(summary.contains("=== Connected Components ==="));
            }
        }

        @Test
        @DisplayName("Should handle large components in summary report")
        void testLargeComponentsSummary() throws IOException {
            // Arrange - Create graph with one large connected component
            Graph<Station, EdgeData> graph = createLargeConnectedGraph();
            String outputPath = tempDir.resolve("large_summary_test").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(graph, outputPath, false);
            String summary = result.getSummaryReport();

            // Assert
            assertNotNull(summary);

            // For large components (>5 stations), should show truncated list
            if (result.getMstResult().getConnectedComponents().get(0).size() > 5) {
                assertFalse(summary.contains("... (") && summary.contains(" more)"));
            }
        }

        @Test
        @DisplayName("Should handle SVG generation request in workflow")
        void testWorkflowWithSVG() throws IOException {
            // Arrange
            Graph<Station, EdgeData> graph = createTestGraph();
            String outputPath = tempDir.resolve("svg_workflow_test").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(graph, outputPath, true);

            // Assert
            assertNotNull(result);
            MSTDotExportService.ExportResult exportResult = result.getExportResult();
            assertNotNull(exportResult.getSvgFilePath());
            assertTrue(exportResult.getSvgFilePath().endsWith(".svg"));

            // Summary should mention SVG status
            String summary = result.getSummaryReport();
            assertTrue(summary.contains("SVG"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty graph in complete workflow")
        void testEmptyGraphWorkflow() throws IOException {
            // Arrange
            Graph<Station, EdgeData> emptyGraph = new MapGraph<>(false);
            String outputPath = tempDir.resolve("empty_workflow").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(emptyGraph, outputPath, false);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getMstResult().getMstGraph().numVertices());
            assertEquals(0.0, result.getMstResult().getTotalDistance());
            assertTrue(result.getMstResult().getConnectedComponents().isEmpty());

            String summary = result.getSummaryReport();
            assertTrue(summary.contains("Total Distance: 0.00"));
            assertTrue(summary.contains("Connected Components: 0"));
        }

        @Test
        @DisplayName("Should handle isolated nodes workflow")
        void testIsolatedNodesWorkflow() throws IOException {
            // Arrange
            Graph<Station, EdgeData> graph = new MapGraph<>(false);
            Station s1 = createStation(1, "Isolated1", 1.0, 1.0);
            Station s2 = createStation(2, "Isolated2", 2.0, 2.0);
            Station s3 = createStation(3, "Isolated3", 3.0, 3.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);
            // No edges - all isolated

            String outputPath = tempDir.resolve("isolated_workflow").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(graph, outputPath, false);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.getMstResult().getMstGraph().numVertices());
            assertEquals(0, result.getMstResult().getMstGraph().numEdges());
            assertEquals(3, result.getMstResult().getConnectedComponents().size());
            assertEquals(0.0, result.getMstResult().getTotalDistance());

            String summary = result.getSummaryReport();
            assertTrue(summary.contains("Connected Components: 3"));
            assertTrue(summary.contains("Component 1 (1 stations):"));
        }

        @Test
        @DisplayName("Should handle stations without names in workflow")
        void testStationsWithoutNamesWorkflow() throws IOException {
            // Arrange
            Graph<Station, EdgeData> graph = new MapGraph<>(false);
            Station s1 = createStation(1, null, 1.0, 1.0); // null name
            Station s2 = createStation(2, "", 2.0, 2.0);   // empty name

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addEdge(s1, s2, new EdgeData(10.0, 100, 10.0));

            String outputPath = tempDir.resolve("no_names_workflow").toString();

            // Act
            MSTController.WorkflowResult result = controller.computeAndExportMST(graph, outputPath, false);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getMstResult().getConnectedComponents().size());

            String summary = result.getSummaryReport();
            // Should handle stations without names gracefully
            assertFalse(summary.contains("Station_1") || summary.contains("Station_2"));
        }
    }

    /**
     * Helper method to create a test graph with 4 connected stations.
     */
    private Graph<Station, EdgeData> createTestGraph() {
        Graph<Station, EdgeData> graph = new MapGraph<>(false);

        Station s1 = createStation(1, "StationA", 0.0, 0.0);
        Station s2 = createStation(2, "StationB", 1.0, 0.0);
        Station s3 = createStation(3, "StationC", 0.0, 1.0);
        Station s4 = createStation(4, "StationD", 1.0, 1.0);

        graph.addVertex(s1);
        graph.addVertex(s2);
        graph.addVertex(s3);
        graph.addVertex(s4);

        graph.addEdge(s1, s2, new EdgeData(10.0, 100, 10.0));
        graph.addEdge(s2, s3, new EdgeData(15.0, 100, 15.0));
        graph.addEdge(s3, s4, new EdgeData(12.0, 100, 12.0));
        graph.addEdge(s1, s4, new EdgeData(20.0, 100, 20.0)); // Higher weight, should not be in MST

        return graph;
    }

    /**
     * Helper method to create a disconnected test graph.
     */
    private Graph<Station, EdgeData> createDisconnectedTestGraph() {
        Graph<Station, EdgeData> graph = new MapGraph<>(false);

        // Component 1
        Station s1 = createStation(1, "StationA", 0.0, 0.0);
        Station s2 = createStation(2, "StationB", 1.0, 0.0);

        // Component 2
        Station s3 = createStation(3, "StationC", 10.0, 10.0);
        Station s4 = createStation(4, "StationD", 11.0, 10.0);

        // Isolated
        Station s5 = createStation(5, "StationE", 20.0, 20.0);

        graph.addVertex(s1);
        graph.addVertex(s2);
        graph.addVertex(s3);
        graph.addVertex(s4);
        graph.addVertex(s5);

        graph.addEdge(s1, s2, new EdgeData(5.0, 100, 5.0));
        graph.addEdge(s3, s4, new EdgeData(8.0, 100, 8.0));
        // s5 is isolated

        return graph;
    }

    /**
     * Helper method to create a large connected graph (>5 stations).
     */
    private Graph<Station, EdgeData> createLargeConnectedGraph() {
        Graph<Station, EdgeData> graph = new MapGraph<>(false);

        // Create 8 stations in a connected graph
        Station[] stations = new Station[8];
        for (int i = 0; i < 8; i++) {
            stations[i] = createStation(i + 1, "Station" + (char)('A' + i), i * 1.0, i * 1.0);
            graph.addVertex(stations[i]);
        }

        // Create a path connecting all stations
        for (int i = 0; i < 7; i++) {
            graph.addEdge(stations[i], stations[i + 1], new EdgeData((i + 1) * 5.0, 100, (i + 1) * 5.0));
        }

        return graph;
    }

    /**
     * Helper method to create test stations.
     */
    private Station createStation(int id, String name, double lat, double lon) {
        Station station = new Station();
        station.setFacilityId(id);
        station.setName(name);
        station.setLat(lat);
        station.setLon(lon);
        return station;
    }
}
