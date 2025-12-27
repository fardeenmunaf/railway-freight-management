package app.service.sprint3;

import app.model.Station;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for MinimumSpanningTreeService.
 *
 * Tests cover all acceptance criteria:
 * - Connected graphs
 * - Disconnected components
 * - Isolated nodes
 * - Empty graphs
 * - Graphs with no edges
 * - Time complexity validation
 *
 * @author Test Infrastructure
 */
class MinimumSpanningTreeServiceTest {

    private MinimumSpanningTreeService mstService;

    @BeforeEach
    void setUp() {
        mstService = new MinimumSpanningTreeService();
    }

    @Nested
    @DisplayName("Basic MST Computation Tests")
    class BasicMSTTests {

        @Test
        @DisplayName("Should compute MST for connected graph with minimum total distance")
        void testConnectedGraphMST() {
            // Arrange: Create a connected graph with 4 stations
            Graph<Station, Double> graph = new MapGraph<>(false);

            Station s1 = createStation(1, "Station1", 0.0, 0.0);
            Station s2 = createStation(2, "Station2", 1.0, 0.0);
            Station s3 = createStation(3, "Station3", 0.0, 1.0);
            Station s4 = createStation(4, "Station4", 1.0, 1.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);
            graph.addVertex(s4);

            // Create edges forming a complete graph with specific weights
            graph.addEdge(s1, s2, 10.0);  // Will be in MST
            graph.addEdge(s1, s3, 15.0);  // Will be in MST
            graph.addEdge(s1, s4, 20.0);  // Won't be in MST (redundant)
            graph.addEdge(s2, s3, 25.0);  // Won't be in MST (higher weight)
            graph.addEdge(s2, s4, 12.0);  // Will be in MST
            graph.addEdge(s3, s4, 30.0);  // Won't be in MST (redundant)

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert
            assertNotNull(result);
            assertEquals(4, result.getMstGraph().numVertices());
            assertEquals(3, result.getMstGraph().numEdges()); // n-1 edges for connected graph
            assertEquals(37.0, result.getTotalDistance(), 0.001); // 10 + 15 + 12
            assertEquals(1, result.getConnectedComponents().size());
            assertEquals("O(E log V)", result.getTimeComplexity());

            // Verify all vertices are in the same component
            List<Station> component = result.getConnectedComponents().get(0);
            assertEquals(4, component.size());
            assertTrue(component.contains(s1));
            assertTrue(component.contains(s2));
            assertTrue(component.contains(s3));
            assertTrue(component.contains(s4));
        }

        @Test
        @DisplayName("Should handle single vertex graph")
        void testSingleVertexGraph() {
            // Arrange
            Graph<Station, Double> graph = new MapGraph<>(false);
            Station s1 = createStation(1, "OnlyStation", 0.0, 0.0);
            graph.addVertex(s1);

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getMstGraph().numVertices());
            assertEquals(0, result.getMstGraph().numEdges());
            assertEquals(0.0, result.getTotalDistance());
            assertEquals(1, result.getConnectedComponents().size());
            assertEquals(1, result.getConnectedComponents().get(0).size());
        }

        @Test
        @DisplayName("Should handle empty graph")
        void testEmptyGraph() {
            // Arrange
            Graph<Station, Double> graph = new MapGraph<>(false);

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getMstGraph().numVertices());
            assertEquals(0, result.getMstGraph().numEdges());
            assertEquals(0.0, result.getTotalDistance());
            assertTrue(result.getConnectedComponents().isEmpty());
        }
    }

    @Nested
    @DisplayName("Disconnected Components Tests")
    class DisconnectedComponentsTests {

        @Test
        @DisplayName("Should compute MST for each connected component separately")
        void testDisconnectedComponents() {
            // Arrange: Create graph with two separate components
            Graph<Station, Double> graph = new MapGraph<>(false);

            // Component 1: 3 stations
            Station s1 = createStation(1, "Station1", 0.0, 0.0);
            Station s2 = createStation(2, "Station2", 1.0, 0.0);
            Station s3 = createStation(3, "Station3", 0.5, 1.0);

            // Component 2: 2 stations
            Station s4 = createStation(4, "Station4", 10.0, 10.0);
            Station s5 = createStation(5, "Station5", 11.0, 10.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);
            graph.addVertex(s4);
            graph.addVertex(s5);

            // Edges in component 1
            graph.addEdge(s1, s2, 5.0);
            graph.addEdge(s2, s3, 7.0);
            graph.addEdge(s1, s3, 10.0); // Higher weight, should not be in MST

            // Edges in component 2
            graph.addEdge(s4, s5, 3.0);

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert
            assertNotNull(result);
            assertEquals(5, result.getMstGraph().numVertices());
            assertEquals(3, result.getMstGraph().numEdges()); // 2 + 1 edges for two components
            assertEquals(15.0, result.getTotalDistance(), 0.001); // 5 + 7 + 3
            assertEquals(2, result.getConnectedComponents().size());

            // Verify component sizes
            List<List<Station>> components = result.getConnectedComponents();
            assertTrue((components.get(0).size() == 3 && components.get(1).size() == 2) ||
                      (components.get(0).size() == 2 && components.get(1).size() == 3));
        }

        @Test
        @DisplayName("Should handle isolated nodes as separate components")
        void testIsolatedNodes() {
            // Arrange: Graph with connected component and isolated nodes
            Graph<Station, Double> graph = new MapGraph<>(false);

            Station s1 = createStation(1, "Connected1", 0.0, 0.0);
            Station s2 = createStation(2, "Connected2", 1.0, 0.0);
            Station s3 = createStation(3, "Isolated1", 5.0, 5.0);
            Station s4 = createStation(4, "Isolated2", 10.0, 10.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);
            graph.addVertex(s4);

            // Only one edge connecting s1 and s2
            graph.addEdge(s1, s2, 8.0);

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert
            assertNotNull(result);
            assertEquals(4, result.getMstGraph().numVertices());
            assertEquals(1, result.getMstGraph().numEdges());
            assertEquals(8.0, result.getTotalDistance());
            assertEquals(3, result.getConnectedComponents().size());

            // Verify component sizes: one with 2 stations, two with 1 station each
            List<List<Station>> components = result.getConnectedComponents();
            int[] sizes = components.stream().mapToInt(List::size).sorted().toArray();
            assertArrayEquals(new int[]{1, 1, 2}, sizes);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should reject null graph")
        void testNullGraph() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mstService.computeMST(null)
            );
            assertEquals("Graph cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should reject directed graph")
        void testDirectedGraph() {
            // Arrange: Create directed graph
            Graph<Station, Double> graph = new MapGraph<>(true); // directed = true
            Station s1 = createStation(1, "Station1", 0.0, 0.0);
            graph.addVertex(s1);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mstService.computeMST(graph)
            );
            assertEquals("MST requires an undirected graph", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle graph with vertices but no edges")
        void testVerticesWithoutEdges() {
            // Arrange: Graph with 3 isolated vertices
            Graph<Station, Double> graph = new MapGraph<>(false);

            Station s1 = createStation(1, "Isolated1", 0.0, 0.0);
            Station s2 = createStation(2, "Isolated2", 1.0, 1.0);
            Station s3 = createStation(3, "Isolated3", 2.0, 2.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert: MST = nodes only (no edges possible)
            assertNotNull(result);
            assertEquals(3, result.getMstGraph().numVertices());
            assertEquals(0, result.getMstGraph().numEdges());
            assertEquals(0.0, result.getTotalDistance());
            assertEquals(3, result.getConnectedComponents().size());

            // Each vertex should be its own component
            for (List<Station> component : result.getConnectedComponents()) {
                assertEquals(1, component.size());
            }
        }

        @Test
        @DisplayName("Should handle edges with null weights")
        void testNullWeightEdges() {
            // Arrange
            Graph<Station, Double> graph = new MapGraph<>(false);

            Station s1 = createStation(1, "Station1", 0.0, 0.0);
            Station s2 = createStation(2, "Station2", 1.0, 0.0);
            Station s3 = createStation(3, "Station3", 0.0, 1.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);

            // Add edges with null weights and valid weights
            graph.addEdge(s1, s2, null);     // Should be ignored
            graph.addEdge(s2, s3, 10.0);     // Should be included
            graph.addEdge(s1, s3, null);     // Should be ignored

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert: Only the edge with valid weight should be in MST
            assertNotNull(result);
            assertEquals(3, result.getMstGraph().numVertices());
            assertEquals(1, result.getMstGraph().numEdges());
            assertEquals(10.0, result.getTotalDistance());
            assertEquals(2, result.getConnectedComponents().size()); // s1 isolated, s2-s3 connected
        }
    }

    @Nested
    @DisplayName("Algorithm Correctness Tests")
    class AlgorithmCorrectnessTests {

        @Test
        @DisplayName("Should select minimum weight edges (Kruskal property)")
        void testMinimalWeightSelection() {
            // Arrange: Create graph where MST selection matters
            Graph<Station, Double> graph = new MapGraph<>(false);

            Station s1 = createStation(1, "A", 0.0, 0.0);
            Station s2 = createStation(2, "B", 1.0, 0.0);
            Station s3 = createStation(3, "C", 2.0, 0.0);
            Station s4 = createStation(4, "D", 3.0, 0.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);
            graph.addVertex(s4);

            // Create edges with specific weights to test selection
            graph.addEdge(s1, s2, 1.0);  // Minimum - should be selected
            graph.addEdge(s2, s3, 2.0);  // Second minimum - should be selected
            graph.addEdge(s3, s4, 3.0);  // Third minimum - should be selected
            graph.addEdge(s1, s3, 10.0); // High weight - should NOT be selected
            graph.addEdge(s1, s4, 20.0); // High weight - should NOT be selected
            graph.addEdge(s2, s4, 15.0); // High weight - should NOT be selected

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert: Should pick the three minimum weight edges
            assertNotNull(result);
            assertEquals(4, result.getMstGraph().numVertices());
            assertEquals(3, result.getMstGraph().numEdges());
            assertEquals(6.0, result.getTotalDistance(), 0.001); // 1 + 2 + 3
            assertEquals(1, result.getConnectedComponents().size());
        }

        @Test
        @DisplayName("Should maintain acyclic property")
        void testAcyclicProperty() {
            // Arrange: Create graph with potential cycles
            Graph<Station, Double> graph = new MapGraph<>(false);

            Station s1 = createStation(1, "Station1", 0.0, 0.0);
            Station s2 = createStation(2, "Station2", 1.0, 0.0);
            Station s3 = createStation(3, "Station3", 0.0, 1.0);

            graph.addVertex(s1);
            graph.addVertex(s2);
            graph.addVertex(s3);

            // Create triangle (cycle)
            graph.addEdge(s1, s2, 1.0);
            graph.addEdge(s2, s3, 2.0);
            graph.addEdge(s1, s3, 5.0); // Highest weight, should be excluded

            // Act
            MinimumSpanningTreeService.MSTResult result = mstService.computeMST(graph);

            // Assert: Should exclude the highest weight edge to avoid cycle
            assertNotNull(result);
            assertEquals(3, result.getMstGraph().numVertices());
            assertEquals(2, result.getMstGraph().numEdges()); // n-1 edges
            assertEquals(3.0, result.getTotalDistance(), 0.001); // 1 + 2
            assertEquals(1, result.getConnectedComponents().size());
        }
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
