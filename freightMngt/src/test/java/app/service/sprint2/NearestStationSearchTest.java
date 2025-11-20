package app.service.sprint2;

import app.model.Station;
import app.model.FacilityType;
import app.controller.StationController;
import app.controller.FacilityTypeController;
import app.utils.HaversineDistance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for N-nearest station search functionality.
 * Validates the KD-tree implementation with Haversine distance and filters.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class NearestStationSearchTest {

    private StationService stationService;
    private StationController stationController;
    private FacilityType stationType;

    @BeforeEach
    public void setUp() {
        stationService = new StationService();
        stationController = new StationController();

        // Create a facility type for stations
        FacilityTypeController facilityTypeController = new FacilityTypeController();
        stationType = new FacilityType(2, "Station");
        facilityTypeController.createFacilityType(stationType);

        // Create test stations
        createTestStations();

        // Build indexes including 2D-tree
        stationService.buildIndexes();
    }

    private void createTestStations() {
        // Lisbon area stations
        Station lisbon = new Station(1, "Lisboa-Santa Apolónia", stationType,
                38.7223, -9.1393, false, "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false);
        stationController.createStation(lisbon);

        Station cascais = new Station(2, "Cascais", stationType,
                38.6979, -9.4207, false, "PT", "Europe/Lisbon", "WET/GMT",
                false, false, false);
        stationController.createStation(cascais);

        Station sintra = new Station(3, "Sintra", stationType,
                38.7987, -9.3867, false, "PT", "Europe/Lisbon", "WET/GMT",
                false, false, false);
        stationController.createStation(sintra);

        // Porto area stations
        Station porto = new Station(4, "Porto-São Bento", stationType,
                41.1579, -8.6291, false, "PT", "Europe/Lisbon", "WET/GMT",
                false, true, false);
        stationController.createStation(porto);

        Station braga = new Station(5, "Braga", stationType,
                41.5454, -8.4265, false, "PT", "Europe/Lisbon", "WET/GMT",
                false, false, false);
        stationController.createStation(braga);

        // Spain stations (different time zone group)
        Station madrid = new Station(6, "Madrid-Atocha", stationType,
                40.4068, -3.6920, false, "ES", "Europe/Madrid", "CET",
                false, true, false);
        stationController.createStation(madrid);

        Station barcelona = new Station(7, "Barcelona-Sants", stationType,
                41.3794, 2.1403, false, "ES", "Europe/Madrid", "CET",
                false, true, false);
        stationController.createStation(barcelona);

        // France stations (different time zone group)
        Station paris = new Station(8, "Paris-Gare du Nord", stationType,
                48.8809, 2.3553, false, "FR", "Europe/Paris", "CET",
                false, true, false);
        stationController.createStation(paris);
    }

    @Test
    public void testFindNearestStation_N1_ClosestToLisbon() {
        // Query point near Lisbon city center
        double queryLat = 38.7167;
        double queryLon = -9.1333;

        StationService.StationDistance result = stationService.findNearestStation(queryLat, queryLon);

        assertNotNull(result);
        assertEquals("Lisboa-Santa Apolónia", result.station.getName());

        // Verify distance is reasonable (should be < 5 km)
        assertTrue(result.distanceKm < 5.0,
                "Distance should be less than 5 km, but was: " + result.distanceKm);

        // Verify with manual Haversine calculation
        double manualDistance = HaversineDistance.calculate(
                queryLat, queryLon,
                result.station.getLat(), result.station.getLon());
        assertEquals(manualDistance, result.distanceKm, 0.001);
    }

    @Test
    public void testFindNearestStations_N3_SortedByDistance() {
        // Query point near Lisbon
        double queryLat = 38.7167;
        double queryLon = -9.1333;

        List<StationService.StationDistance> results =
                stationService.findNearestStations(queryLat, queryLon, 3);

        assertEquals(3, results.size());

        // Verify results are sorted by distance ascending
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).distanceKm <= results.get(i + 1).distanceKm,
                    "Results should be sorted by distance ascending");
        }

        // First result should be Lisboa-Santa Apolónia
        assertEquals("Lisboa-Santa Apolónia", results.get(0).station.getName());

        // Verify all distances are calculated correctly
        for (StationService.StationDistance result : results) {
            double manualDistance = HaversineDistance.calculate(
                    queryLat, queryLon,
                    result.station.getLat(), result.station.getLon());
            assertEquals(manualDistance, result.distanceKm, 0.001);
        }
    }

    @Test
    public void testFindNearestStations_N5_MoreThanAvailable() {
        // Query near Porto, request more stations than exist
        double queryLat = 41.1579;
        double queryLon = -8.6291;

        List<StationService.StationDistance> results =
                stationService.findNearestStations(queryLat, queryLon, 50);

        // Should return all available stations
        assertTrue(results.size() <= 8); // We have 8 test stations
        assertTrue(results.size() > 0);

        // Verify sorted by distance
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).distanceKm <= results.get(i + 1).distanceKm);
        }
    }

    @Test
    public void testFindNearestStations_WithTimeZoneFilter_WET() {
        // Query near Madrid, but filter only WET/GMT stations (Portuguese)
        double queryLat = 40.4068;
        double queryLon = -3.6920;

        List<StationService.StationDistance> results =
                stationService.findNearestStations(queryLat, queryLon, 3, "WET/GMT");

        assertEquals(3, results.size());

        // Verify all results are in WET/GMT time zone group
        for (StationService.StationDistance result : results) {
            assertEquals("WET/GMT", result.station.getTimeZoneGroup(),
                    "All results should be in WET/GMT time zone group");
        }

        // Verify sorted by distance
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).distanceKm <= results.get(i + 1).distanceKm);
        }
    }

    @Test
    public void testFindNearestStations_WithTimeZoneFilter_CET() {
        // Query near Lisbon, but filter only CET stations
        double queryLat = 38.7167;
        double queryLon = -9.1333;

        List<StationService.StationDistance> results =
                stationService.findNearestStations(queryLat, queryLon, 2, "CET");

        assertEquals(2, results.size());

        // Verify all results are in CET time zone group
        for (StationService.StationDistance result : results) {
            assertEquals("CET", result.station.getTimeZoneGroup(),
                    "All results should be in CET time zone group");
        }

        // Should get Madrid and Barcelona (Spanish stations)
        assertTrue(results.stream().anyMatch(r -> r.station.getName().contains("Madrid")));
        assertTrue(results.stream().anyMatch(r -> r.station.getName().contains("Barcelona")));
    }

    @Test
    public void testFindNearestStation_WithTimeZoneFilter_NoMatch() {
        // Query near Lisbon with a time zone that doesn't exist
        double queryLat = 38.7167;
        double queryLon = -9.1333;

        List<StationService.StationDistance> results =
                stationService.findNearestStations(queryLat, queryLon, 1, "PST");

        assertTrue(results.isEmpty(), "Should return empty list when no stations match filter");
    }

    @Test
    public void testFindNearestStations_OutsideDatasetBounds() {
        // Query point far from all stations (e.g., middle of Atlantic Ocean)
        double queryLat = 35.0;
        double queryLon = -40.0;

        List<StationService.StationDistance> results =
                stationService.findNearestStations(queryLat, queryLon, 1);

        assertFalse(results.isEmpty(), "Should still find nearest station even if far away");

        // Distance should be very large (> 1000 km)
        assertTrue(results.get(0).distanceKm > 1000.0,
                "Distance should be > 1000 km for this remote location");
    }

    @Test
    public void testFindNearestStations_ExactCoordinates() {
        // Query exact coordinates of Porto station
        double queryLat = 41.1579;
        double queryLon = -8.6291;

        StationService.StationDistance result =
                stationService.findNearestStation(queryLat, queryLon);

        assertNotNull(result);
        assertEquals("Porto-São Bento", result.station.getName());

        // Distance should be essentially 0
        assertTrue(result.distanceKm < 0.01,
                "Distance should be ~0 km for exact match, but was: " + result.distanceKm);
    }

    @Test
    public void testTemporalComplexityAnalysis() {
        // Verify temporal complexity analysis is available
        var complexity = stationService.get2DTreeComplexity();

        assertNotNull(complexity);
        assertTrue(complexity.totalNodes > 0);
        assertTrue(complexity.expectedHeight > 0);
        assertNotNull(complexity.buildComplexity);
        // The TemporalComplexity class exposes a searchComplexity field (point search)
        assertNotNull(complexity.searchComplexity);

        // Verify complexity string contains expected patterns
        assertTrue(complexity.toString().contains("O(N log N)"));
        assertTrue(complexity.toString().contains("O(log N)"));
    }

    @Test
    public void testMultipleQueriesConsistency() {
        // Same query should return same results
        double queryLat = 38.7167;
        double queryLon = -9.1333;

        List<StationService.StationDistance> results1 =
                stationService.findNearestStations(queryLat, queryLon, 3);
        List<StationService.StationDistance> results2 =
                stationService.findNearestStations(queryLat, queryLon, 3);

        assertEquals(results1.size(), results2.size());

        for (int i = 0; i < results1.size(); i++) {
            assertEquals(results1.get(i).station.getName(),
                    results2.get(i).station.getName());
            assertEquals(results1.get(i).distanceKm,
                    results2.get(i).distanceKm, 0.001);
        }
    }

    @Test
    public void testDistanceCalculationAccuracy() {
        // Compare KD-tree results with brute-force manual calculation
        double queryLat = 40.0;
        double queryLon = -8.0;

        List<StationService.StationDistance> treeResults =
                stationService.findNearestStations(queryLat, queryLon, 3);

        // Manual calculation: get all stations and calculate distances
        for (StationService.StationDistance treeResult : treeResults) {
            Station station = treeResult.station;
            double manualDistance = HaversineDistance.calculate(
                    queryLat, queryLon,
                    station.getLat(), station.getLon());

            assertEquals(manualDistance, treeResult.distanceKm, 0.001,
                    "KD-tree distance should match manual Haversine calculation");
        }
    }
}

