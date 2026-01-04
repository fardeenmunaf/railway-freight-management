package app.service.sprint3;

import app.model.EdgeData;
import app.model.Station;
import app.utils.graph.Graph;
import app.utils.graph.map.MapGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for {@link RailHubCentralityAnalysis}.
 *
 * Covers:
 *  - Constructor and metrics calculation
 *  - Ranked results retrieval
 *  - Top N hubs retrieval
 *  - Temporal complexity analysis
 *  - HubAnalysisResult getters and formatting
 *  - End-to-end flow with test data
 *
 * NOTE: Uses a small test graph (10 stations) created directly in setUp for fast execution.
 *
 * @author Diogo Remelgado
 * @version 1.0
 */
public class RailHubCentralityAnalysisTest {

    private RailHubCentralityAnalysis analysis;
    private Graph<Station, EdgeData> testGraph;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a small test graph with 10 stations for fast testing
        testGraph = new MapGraph<>(false);

        // Create 10 test stations
        Station s1 = createStation(1, "Station A");
        Station s2 = createStation(2, "Station B");
        Station s3 = createStation(3, "Station C");
        Station s4 = createStation(4, "Station D");
        Station s5 = createStation(5, "Station E");
        Station s6 = createStation(6, "Station F");
        Station s7 = createStation(7, "Station G");
        Station s8 = createStation(8, "Station H");
        Station s9 = createStation(9, "Station I");
        Station s10 = createStation(10, "Station J");

        // Add vertices to graph
        testGraph.addVertex(s1);
        testGraph.addVertex(s2);
        testGraph.addVertex(s3);
        testGraph.addVertex(s4);
        testGraph.addVertex(s5);
        testGraph.addVertex(s6);
        testGraph.addVertex(s7);
        testGraph.addVertex(s8);
        testGraph.addVertex(s9);
        testGraph.addVertex(s10);

        // Create edges to form a connected graph
        // Hub structure: s1 is central hub connected to most stations
        testGraph.addEdge(s1, s2, new EdgeData(10.0, 15, 12.0));
        testGraph.addEdge(s1, s3, new EdgeData(15.0, 12, 18.0));
        testGraph.addEdge(s1, s4, new EdgeData(20.0, 10, 22.0));
        testGraph.addEdge(s1, s5, new EdgeData(12.0, 14, 15.0));
        testGraph.addEdge(s1, s6, new EdgeData(18.0, 11, 20.0));

        // s2 is secondary hub
        testGraph.addEdge(s2, s3, new EdgeData(8.0, 16, 10.0));
        testGraph.addEdge(s2, s7, new EdgeData(14.0, 13, 16.0));
        testGraph.addEdge(s2, s8, new EdgeData(11.0, 15, 13.0));

        // Connect remaining stations
        testGraph.addEdge(s3, s4, new EdgeData(9.0, 14, 11.0));
        testGraph.addEdge(s4, s5, new EdgeData(7.0, 17, 9.0));
        testGraph.addEdge(s5, s6, new EdgeData(13.0, 12, 15.0));
        testGraph.addEdge(s6, s7, new EdgeData(16.0, 10, 18.0));
        testGraph.addEdge(s7, s8, new EdgeData(6.0, 18, 8.0));
        testGraph.addEdge(s8, s9, new EdgeData(10.0, 15, 12.0));
        testGraph.addEdge(s9, s10, new EdgeData(12.0, 14, 14.0));
        testGraph.addEdge(s10, s1, new EdgeData(25.0, 9, 28.0)); // Close the loop

        // Initialize analysis service with test graph
        analysis = new RailHubCentralityAnalysis(testGraph);
    }

    private Station createStation(int id, String name) {
        Station station = new Station();
        station.setFacilityId(id);
        station.setName(name);
        return station;
    }

    @Test
    public void testConstructor_CalculatesMetricsAutomatically() throws IOException {
        // Use the test graph
        RailHubCentralityAnalysis service = new RailHubCentralityAnalysis(testGraph);

        assertNotNull(service, "Service should be initialized");

        // Verify that metrics are calculated by checking results
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = service.getRankedResults();
        assertNotNull(results, "Results should be calculated");
        assertFalse(results.isEmpty(), "Results should not be empty");
        assertEquals(10, results.size(), "Should have 10 test stations");
    }

    @Test
    public void testGetRankedResults_ReturnsAllStations() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        assertNotNull(results, "Ranked results should not be null");
        assertFalse(results.isEmpty(), "Ranked results should not be empty");

        // Verify results are sorted by hub score (descending)
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getHubScore() >= results.get(i + 1).getHubScore(),
                      "Results should be sorted by hub score in descending order");
        }
    }

    @Test
    public void testGetRankedResults_ContainsAllMetrics() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        // Take first result and verify all metrics are present
        if (!results.isEmpty()) {
            RailHubCentralityAnalysis.HubAnalysisResult first = results.get(0);

            assertNotNull(first.getStationName(), "Station name should not be null");
            assertTrue(first.getStationId() > 0, "Station ID should be positive");
            assertTrue(first.getDegree() >= 0, "Degree should be non-negative");
            assertTrue(first.getStrength() >= 0, "Strength should be non-negative");
            assertTrue(first.getBetweenness() >= 0, "Betweenness should be non-negative");
            assertTrue(first.getHarmonicCloseness() >= 0, "Harmonic closeness should be non-negative");
            assertTrue(first.getHubScore() >= 0, "Hub score should be non-negative");
        }
    }

    @Test
    public void testGetTopNHubs_ReturnsCorrectCount() {
        int n = 5;
        List<RailHubCentralityAnalysis.HubAnalysisResult> topN = analysis.getTopNHubs(n);

        assertNotNull(topN, "Top N results should not be null");
        assertTrue(topN.size() <= n, "Should return at most N results");

        // Verify they are the top ones
        List<RailHubCentralityAnalysis.HubAnalysisResult> allResults = analysis.getRankedResults();
        if (allResults.size() >= n) {
            assertEquals(n, topN.size(), "Should return exactly N results when available");

            // Verify top N matches the first N from ranked results
            for (int i = 0; i < n; i++) {
                assertEquals(allResults.get(i).getStationId(), topN.get(i).getStationId(),
                           "Top N should match first N from ranked results");
            }
        }
    }

    @Test
    public void testGetTopNHubs_WithZero_ReturnsEmpty() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> topN = analysis.getTopNHubs(0);

        assertNotNull(topN, "Result should not be null");
        assertTrue(topN.isEmpty(), "Should return empty list for N=0");
    }

    @Test
    public void testGetTopNHubs_WithNegative_ReturnsEmpty() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> topN = analysis.getTopNHubs(-5);

        assertNotNull(topN, "Result should not be null");
        assertTrue(topN.isEmpty(), "Should return empty list for negative N");
    }

    @Test
    public void testGetTopNHubs_WithLargeN_ReturnsAllAvailable() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> allResults = analysis.getRankedResults();
        List<RailHubCentralityAnalysis.HubAnalysisResult> topN = analysis.getTopNHubs(10000);

        assertNotNull(topN, "Result should not be null");
        assertEquals(allResults.size(), topN.size(),
                    "Should return all available results when N > total");
    }



    @Test
    public void testHubAnalysisResult_GettersWork() {
        RailHubCentralityAnalysis.HubAnalysisResult result =
            new RailHubCentralityAnalysis.HubAnalysisResult(
                123, "TestStation", 5, 100.5, 0.75, 0.85, 0.90
            );

        assertEquals(123, result.getStationId(), "Station ID should match");
        assertEquals("TestStation", result.getStationName(), "Station name should match");
        assertEquals(5, result.getDegree(), "Degree should match");
        assertEquals(100.5, result.getStrength(), 0.001, "Strength should match");
        assertEquals(0.75, result.getBetweenness(), 0.001, "Betweenness should match");
        assertEquals(0.85, result.getHarmonicCloseness(), 0.001, "Harmonic closeness should match");
        assertEquals(0.90, result.getHubScore(), 0.001, "Hub score should match");
    }

    @Test
    public void testHubAnalysisResult_ToStringFormat() {
        RailHubCentralityAnalysis.HubAnalysisResult result =
            new RailHubCentralityAnalysis.HubAnalysisResult(
                123, "TestStation", 5, 100.5, 0.75, 0.85, 0.90
            );

        String formatted = result.toString();

        assertNotNull(formatted, "toString should not return null");
        assertTrue(formatted.contains("TestStation"), "Should contain station name");
        assertTrue(formatted.contains("123"), "Should contain station ID");
    }

    @Test
    public void testCalculateAllMetrics_ProducesConsistentResults() {
        // Get results twice and verify they are consistent
        List<RailHubCentralityAnalysis.HubAnalysisResult> results1 = analysis.getRankedResults();
        List<RailHubCentralityAnalysis.HubAnalysisResult> results2 = analysis.getRankedResults();

        assertEquals(results1.size(), results2.size(),
                    "Should return same number of results");

        // Verify first result is the same
        if (!results1.isEmpty()) {
            assertEquals(results1.get(0).getStationId(), results2.get(0).getStationId(),
                       "Top hub should be consistent");
            assertEquals(results1.get(0).getHubScore(), results2.get(0).getHubScore(), 0.001,
                       "Hub scores should be consistent");
        }
    }

    @Test
    public void testMetrics_DegreeIsValid() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        for (RailHubCentralityAnalysis.HubAnalysisResult result : results) {
            assertTrue(result.getDegree() >= 0,
                      "Degree must be non-negative for station " + result.getStationName());
        }
    }

    @Test
    public void testMetrics_StrengthIsValid() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        for (RailHubCentralityAnalysis.HubAnalysisResult result : results) {
            assertTrue(result.getStrength() >= 0,
                      "Strength must be non-negative for station " + result.getStationName());
        }
    }

    @Test
    public void testMetrics_BetweennessIsNormalized() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        for (RailHubCentralityAnalysis.HubAnalysisResult result : results) {
            assertTrue(result.getBetweenness() >= 0 && result.getBetweenness() <= 1.0,
                      "Betweenness should be normalized between 0 and 1 for station " + result.getStationName());
        }
    }

    @Test
    public void testMetrics_HarmonicClosenessIsNormalized() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        for (RailHubCentralityAnalysis.HubAnalysisResult result : results) {
            assertTrue(result.getHarmonicCloseness() >= 0 && result.getHarmonicCloseness() <= 1.0,
                      "Harmonic closeness should be normalized between 0 and 1 for station " + result.getStationName());
        }
    }

    @Test
    public void testDataIntegrity_AllStationsHaveMetrics() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        // Verify we have results for all test stations (10)
        assertEquals(10, results.size(),
                "Should have metrics for all 10 test stations");
    }

    @Test
    public void testDataIntegrity_TopHubIsStationA() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        assertFalse(results.isEmpty(), "Results should not be empty");

        // Station A (s1) should be the top hub as it has the most connections (5 direct connections)
        RailHubCentralityAnalysis.HubAnalysisResult topHub = results.get(0);
        assertEquals("Station A", topHub.getStationName(),
                "Station A should be the top hub");
    }

    @Test
    public void testMetrics_HubScoreIsNormalized() {
        List<RailHubCentralityAnalysis.HubAnalysisResult> results = analysis.getRankedResults();

        for (RailHubCentralityAnalysis.HubAnalysisResult result : results) {
            assertTrue(result.getHubScore() >= 0 && result.getHubScore() <= 1.0,
                      "Hub score should be normalized between 0 and 1 for station " + result.getStationName());
        }
    }

    @Test
    public void testFullFlow_CompleteAnalysisPipeline() throws IOException {
        // Step 1: Use test graph (already initialized in analysis)
        assertNotNull(analysis, "Service should be initialized");

        // Step 2: Get all ranked results
        List<RailHubCentralityAnalysis.HubAnalysisResult> allResults = analysis.getRankedResults();
        assertNotNull(allResults, "Results should not be null");
        assertFalse(allResults.isEmpty(), "Results should not be empty");
        assertEquals(10, allResults.size(), "Should have 10 test stations");

        // Step 3: Verify results are sorted
        for (int i = 0; i < allResults.size() - 1; i++) {
            assertTrue(allResults.get(i).getHubScore() >= allResults.get(i + 1).getHubScore(),
                      "Results should be sorted by hub score");
        }

        // Step 4: Get top 5 hubs
        List<RailHubCentralityAnalysis.HubAnalysisResult> top5 = analysis.getTopNHubs(5);
        assertNotNull(top5, "Top 5 should not be null");
        assertEquals(5, top5.size(), "Should return exactly 5 results");

        // Step 5: Verify top hub has highest score
        assertEquals(allResults.get(0).getStationId(), top5.get(0).getStationId(),
                   "Top hub should match first ranked result");
        assertEquals(allResults.get(0).getHubScore(), top5.get(0).getHubScore(), 0.001,
                   "Top hub score should match");

        // Step 6: Verify all metrics are present in top hub
        RailHubCentralityAnalysis.HubAnalysisResult topHub = top5.get(0);

        assertNotNull(topHub.getStationName(), "Top hub should have name");
        assertTrue(topHub.getStationId() > 0, "Top hub should have valid ID");
        assertTrue(topHub.getDegree() >= 0, "Top hub should have valid degree");
        assertTrue(topHub.getStrength() >= 0, "Top hub should have valid strength");
        assertTrue(topHub.getBetweenness() >= 0, "Top hub should have valid betweenness");
        assertTrue(topHub.getHarmonicCloseness() >= 0, "Top hub should have valid harmonic closeness");
        assertTrue(topHub.getHubScore() >= 0, "Top hub should have valid hub score");

        // Step 7: Verify formatting
        String formatted = top5.get(0).toString();
        assertNotNull(formatted, "Formatted output should not be null");
        assertTrue(formatted.contains(top5.get(0).getStationName()),
                  "Formatted output should contain station name");

        // Step 8: Verify consistency - call getRankedResults again
        List<RailHubCentralityAnalysis.HubAnalysisResult> allResults2 = analysis.getRankedResults();
        assertEquals(allResults.size(), allResults2.size(), "Results should be consistent");

        // Step 9: Verify edge cases
        List<RailHubCentralityAnalysis.HubAnalysisResult> emptyTop = analysis.getTopNHubs(0);
        assertTrue(emptyTop.isEmpty(), "Top 0 should return empty list");

        List<RailHubCentralityAnalysis.HubAnalysisResult> allTop = analysis.getTopNHubs(10000);
        assertEquals(allResults.size(), allTop.size(),
                    "Large N should return all results");
    }
}

