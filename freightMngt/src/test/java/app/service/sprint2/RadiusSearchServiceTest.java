package app.service.sprint2;

import app.model.FacilityType;
import app.model.Station;
import app.utils.AVLTree;
import app.utils.Balanced2DTreeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for {@link RadiusSearchService}.
 *
 * Covers:
 *  - Pure radius search on a small hand-crafted tree (unit behaviour)
 *  - Density summary calculation from an AVL tree of StationWithDistance
 *  - End‑to‑end style test using {@link StationService} 2D tree as a source
 */
public class RadiusSearchServiceTest {

    private Balanced2DTreeBuilder.Balanced2DTree smallTree;
    private RadiusSearchService radiusServiceOnSmallTree;

    @BeforeEach
    public void setUp() {
        // Build a tiny in‑memory tree without hitting repositories
        FacilityType type = new FacilityType(1, "TestStation");

        Station s1 = new Station(1, "Center", type,
                40.0, -8.0, true,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        Station s2 = new Station(2, "North", type,
                40.5, -8.0, false,
                "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false);

        Station s3 = new Station(3, "FarAway", type,
                50.0, -8.0, false,
                "ES", "Europe/Madrid", "CET",
                false, true, false);

        smallTree = new Balanced2DTreeBuilder.Balanced2DTree();
        smallTree.bulkBuild(List.of(s1, s2, s3));

        radiusServiceOnSmallTree = new RadiusSearchService(smallTree);
    }

    @Test
    public void testRadiusSearchAsTree_FindsOnlyStationsInsideRadius() {
        // Query near "Center"; radius ~60km should include Center and North, but not FarAway
        double queryLat = 40.0;
        double queryLon = -8.0;
        double radiusKm = 60.0;

        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultTree =
                radiusServiceOnSmallTree.radiusSearchAsTree(queryLat, queryLon, radiusKm);

        var results = resultTree.inOrderTraversal();
        assertEquals(2, results.size(), "Should find exactly 2 stations inside radius");

        // Names should contain Center and North
        assertTrue(results.stream().anyMatch(r -> r.station.getName().equals("Center")));
        assertTrue(results.stream().anyMatch(r -> r.station.getName().equals("North")));

        // Distances should all be within radius
        results.forEach(r -> assertTrue(r.distance <= radiusKm + 1e-6));
    }

    @Test
    public void testCalculateDensitySummary_CountsByCountryAndCityFlag() {
        // Build result tree manually from smallTree stations
        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultTree =
                new AVLTree<>();

        for (Station station : smallTree.getAllStations()) {
            Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance swd =
                    new Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance(station, 10.0);
            resultTree.insert(swd, swd);
        }

        RadiusSearchService radiusService = new RadiusSearchService(smallTree);
        RadiusSearchService.DensitySummary summary = radiusService.calculateDensitySummary(resultTree);

        assertEquals(3, summary.totalCount);
        assertEquals(2, summary.byCountry.getOrDefault("PT", 0));
        assertEquals(1, summary.byCountry.getOrDefault("ES", 0));

        // In our setup: s1 isCity=true, s2/s3 are false
        assertEquals(1, summary.cityCount);
        assertEquals(2, summary.nonCityCount);
    }

    @Test
    public void testRadiusSearchWithAnalysis_ReturnsResultsAndTemporalAnalysis() {
        // Test the main method that returns both results and temporal analysis
        double queryLat = 40.0;
        double queryLon = -8.0;
        double radiusKm = 60.0;

        RadiusSearchService.RadiusSearchResult result =
                radiusServiceOnSmallTree.radiusSearchWithAnalysis(queryLat, queryLon, radiusKm);

        // Verify results tree is populated
        assertNotNull(result.resultsTree);
        var results = result.resultsTree.inOrderTraversal();
        assertEquals(2, results.size(), "Should find exactly 2 stations inside radius");

        // Verify temporal analysis is present
        assertNotNull(result.temporalAnalysis);
        assertEquals(3, result.temporalAnalysis.totalNodes, "Total nodes should match tree size");
        assertEquals(2, result.temporalAnalysis.resultCount, "Result count should match findings");

        // Verify complexity strings are populated
        assertNotNull(result.temporalAnalysis.radiusSearchComplexity);
        assertTrue(result.temporalAnalysis.radiusSearchComplexity.contains("O(√N + k · log k)"));
        assertTrue(result.temporalAnalysis.radiusSearchComplexity.contains("N = 3"));
        assertTrue(result.temporalAnalysis.radiusSearchComplexity.contains("k = 2"));

        assertNotNull(result.temporalAnalysis.densitySummaryComplexity);
        assertTrue(result.temporalAnalysis.densitySummaryComplexity.contains("O(k)"));

        assertNotNull(result.temporalAnalysis.totalUseCaseComplexity);
        assertTrue(result.temporalAnalysis.totalUseCaseComplexity.contains("O(√N + k · log k)"));
    }

    @Test
    public void testTemporalAnalysis_ToStringFormat() {
        // Test the toString method of TemporalAnalysis
        RadiusSearchService.TemporalAnalysis analysis =
                new RadiusSearchService.TemporalAnalysis(100, 10);

        String analysisString = analysis.toString();

        // Verify the format contains expected sections
        assertNotNull(analysisString);
        assertTrue(analysisString.contains("Temporal Analysis (USEI10)"));
        assertTrue(analysisString.contains("Radius Search: O(√N + k · log k) where N = 100 and k = 10"));
        assertTrue(analysisString.contains("Density Summary: O(k) where k = 10"));
        assertTrue(analysisString.contains("Total (USEI10): O(√N + k · log k) where N = 100 and k = 10"));
    }

    @Test
    public void testDensitySummary_ToStringFormat() {
        // Build a result tree with multiple countries
        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultTree =
                new AVLTree<>();

        for (Station station : smallTree.getAllStations()) {
            Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance swd =
                    new Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance(station, 10.0);
            resultTree.insert(swd, swd);
        }

        RadiusSearchService.DensitySummary summary =
                radiusServiceOnSmallTree.calculateDensitySummary(resultTree);

        String summaryString = summary.toString();

        // Verify the format contains expected sections
        assertNotNull(summaryString);
        assertTrue(summaryString.contains("Density Summary:"));
        assertTrue(summaryString.contains("Total Stations: 3"));
        assertTrue(summaryString.contains("City Stations: 1"));
        assertTrue(summaryString.contains("Non-City Stations: 2"));
        assertTrue(summaryString.contains("Distribution by Country:"));
        assertTrue(summaryString.contains("PT: 2 stations"));
        assertTrue(summaryString.contains("ES: 1 stations"));
    }

    @Test
    public void testRadiusSearchWithAnalysis_WithNullTree() {
        // Test with null tree to cover the null check branch
        RadiusSearchService serviceWithNullTree = new RadiusSearchService(null);

        // This should handle null tree gracefully
        assertThrows(NullPointerException.class, () -> {
            serviceWithNullTree.radiusSearchWithAnalysis(40.0, -8.0, 60.0);
        });
    }

    @Test
    public void testCalculateDensitySummary_WithNullCountry() {
        // Create a station with null country to test null handling
        FacilityType type = new FacilityType(1, "TestStation");
        Station stationNoCountry = new Station(10, "NoCountry", type,
                40.0, -8.0, null,
                null, "Europe/Lisbon", "WET/GMT",
                true, true, false);

        Balanced2DTreeBuilder.Balanced2DTree treeWithNull = new Balanced2DTreeBuilder.Balanced2DTree();
        treeWithNull.bulkBuild(List.of(stationNoCountry));

        RadiusSearchService service = new RadiusSearchService(treeWithNull);
        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultTree =
                service.radiusSearchAsTree(40.0, -8.0, 10.0);

        RadiusSearchService.DensitySummary summary = service.calculateDensitySummary(resultTree);

        // Should handle null country gracefully
        assertEquals(1, summary.totalCount);
        assertFalse(summary.byCountry.containsKey(null));
    }

    @Test
    public void testFullFlow_WithStationService2DTree() {
        // Seed the global repository with a few dummy stations
        app.controller.StationController seedController = new app.controller.StationController();
        FacilityType type = new FacilityType(99, "TestStationFullFlow");

        Station porto = new Station(1001, "Porto", type,
                41.1579, -8.6291, true,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        Station lisbon = new Station(1002, "Lisboa", type,
                38.7167, -9.1333, true,
                "PT", "Europe/Lisbon", "WET/GMT",
                true, true, false);

        Station madrid = new Station(1003, "Madrid", type,
                40.4168, -3.7038, true,
                "ES", "Europe/Madrid", "CET",
                true, true, false);

        seedController.createStation(porto);
        seedController.createStation(lisbon);
        seedController.createStation(madrid);

        // Use the real StationService to build indexes and 2D tree from the seeded data
        StationService stationService = new StationService();
        stationService.buildIndexes();

        Balanced2DTreeBuilder.Balanced2DTree tree = stationService.getSpatial2DTree();
        RadiusSearchService radiusService = new RadiusSearchService(tree);

        // Choose a point roughly near Portugal; radius big enough to get seeded stations
        double queryLat = 40.0;
        double queryLon = -8.0;
        double radiusKm = 500.0;

        AVLTree<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance,
                Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> resultTree =
                radiusService.radiusSearchAsTree(queryLat, queryLon, radiusKm);

        var results = resultTree.inOrderTraversal();
        assertFalse(results.isEmpty(), "Radius search should return at least one station");

        // And the density summary should be consistent with those results
        RadiusSearchService.DensitySummary summary = radiusService.calculateDensitySummary(resultTree);
        assertEquals(results.size(), summary.totalCount);

        // All countries present in results must appear in the summary map
        results.forEach(r -> assertTrue(
                summary.byCountry.containsKey(r.station.getCountry()),
                "Summary must contain entry for country " + r.station.getCountry()));
    }
}
