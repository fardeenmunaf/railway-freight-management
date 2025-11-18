package app.service.sprint1;

import app.service.sprint1.PickRouteOptimizationService.BayLocation;
import app.service.sprint1.PickRouteOptimizationService.RouteResult;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test class for PickRouteOptimizationService.
 * Tests route optimization strategies (Deterministic Sweep and Nearest Neighbour)
 * and distance calculations for warehouse picking operations.
 */
public class PickRouteOptimizationServiceTest {

    private PickRouteOptimizationService service;

    @Before
    public void setUp() {
        service = new PickRouteOptimizationService();
    }

    /**
     * Helper method to create a list of bay locations for testing.
     */
    private List<BayLocation> createBayLocations(String[][] bayData) {
        List<BayLocation> bays = new ArrayList<>();
        for (String[] data : bayData) {
            String aisle = data[0];
            int bay = Integer.parseInt(data[1]);
            double quantity = Double.parseDouble(data[2]);
            bays.add(new BayLocation(aisle, bay, quantity));
        }
        return bays;
    }

    // ======================== BayLocation Tests ========================

    /**
     * Test BayLocation constructor and getters.
     */
    @Test
    public void testBayLocation_ConstructorAndGetters() {
        BayLocation bay = new BayLocation("3", 5, 10.5);

        assertEquals("Aisle should be 3", "3", bay.getAisle());
        assertEquals("Bay should be 5", 5, bay.getBay());
        assertEquals("Quantity should be 10.5", 10.5, bay.getQuantity(), 0.01);
    }

    /**
     * Test BayLocation setQuantity method.
     */
    @Test
    public void testBayLocation_SetQuantity() {
        BayLocation bay = new BayLocation("2", 4, 5.0);

        bay.setQuantity(15.0);

        assertEquals("Quantity should be updated to 15.0", 15.0, bay.getQuantity(), 0.01);
    }

    /**
     * Test BayLocation toString method.
     */
    @Test
    public void testBayLocation_ToString() {
        BayLocation bay = new BayLocation("1", 3, 7.5);

        String result = bay.toString();

        assertEquals("ToString should match expected format", "(1,3) qty:7.5", result);
    }

    /**
     * Test BayLocation getBayName method.
     */
    @Test
    public void testBayLocation_GetBayName() {
        BayLocation bay = new BayLocation("2", 6, 10.0);

        String bayName = bay.getBayName();

        assertEquals("Bay name should match expected format", "W1-A2-B6", bayName);
    }

    // ======================== RouteResult Tests ========================

    /**
     * Test RouteResult constructor and getters.
     */
    @Test
    public void testRouteResult_ConstructorAndGetters() {
        List<BayLocation> path = createBayLocations(new String[][]{
            {"1", "2", "5.0"},
            {"2", "3", "10.0"}
        });

        RouteResult result = new RouteResult("Test Strategy", path, 15);

        assertEquals("Strategy name should match", "Test Strategy", result.getStrategyName());
        assertEquals("Path should match", path, result.getPath());
        assertEquals("Total distance should be 15", 15, result.getTotalDistance());
    }

    /**
     * Test RouteResult toString method.
     */
    @Test
    public void testRouteResult_ToString() {
        List<BayLocation> path = createBayLocations(new String[][]{
            {"1", "2", "5.0"}
        });

        RouteResult result = new RouteResult("Test Strategy", path, 10);
        String str = result.toString();

        assertTrue("ToString should contain strategy name", str.contains("Test Strategy"));
        assertTrue("ToString should contain distance", str.contains("10"));
    }

    // ======================== Deterministic Sweep Tests ========================

    /**
     * Test Deterministic Sweep with single aisle.
     * Expected: Bays should be sorted by bay number within the aisle.
     */
    @Test
    public void testDeterministicSweep_SingleAisle() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "5", "10.0"},
            {"1", "2", "5.0"},
            {"1", "8", "15.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        assertEquals("Strategy name should be correct", "Strategy A - Deterministic Sweep",
                     result.getStrategyName());
        assertEquals("Should have 3 locations", 3, result.getPath().size());

        // Verify sorting: bay 2, then 5, then 8
        assertEquals("First bay should be 2", 2, result.getPath().get(0).getBay());
        assertEquals("Second bay should be 5", 5, result.getPath().get(1).getBay());
        assertEquals("Third bay should be 8", 8, result.getPath().get(2).getBay());
    }

    /**
     * Test Deterministic Sweep with multiple aisles.
     * Expected: Sorted by aisle first, then by bay number.
     */
    @Test
    public void testDeterministicSweep_MultipleAisles() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"2", "3", "5.0"},
            {"1", "5", "10.0"},
            {"3", "2", "8.0"},
            {"1", "1", "3.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        List<BayLocation> path = result.getPath();
        assertEquals("Should have 4 locations", 4, path.size());

        // Verify sorting: Aisle 1 (bays 1,5), then Aisle 2 (bay 3), then Aisle 3 (bay 2)
        assertEquals("First should be aisle 1, bay 1", "1", path.get(0).getAisle());
        assertEquals("First bay number should be 1", 1, path.get(0).getBay());

        assertEquals("Second should be aisle 1, bay 5", "1", path.get(1).getAisle());
        assertEquals("Second bay number should be 5", 5, path.get(1).getBay());

        assertEquals("Third should be aisle 2, bay 3", "2", path.get(2).getAisle());
        assertEquals("Third bay number should be 3", 3, path.get(2).getBay());

        assertEquals("Fourth should be aisle 3, bay 2", "3", path.get(3).getAisle());
        assertEquals("Fourth bay number should be 2", 2, path.get(3).getBay());
    }

    /**
     * Test Deterministic Sweep with empty list.
     */
    @Test
    public void testDeterministicSweep_EmptyList() {
        List<BayLocation> bays = new ArrayList<>();

        RouteResult result = service.computeDeterministicSweep(bays);

        assertEquals("Path should be empty", 0, result.getPath().size());
        assertEquals("Distance should be 0", 0, result.getTotalDistance());
    }

    /**
     * Test Deterministic Sweep with single location.
     */
    @Test
    public void testDeterministicSweep_SingleLocation() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"2", "4", "12.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        assertEquals("Should have 1 location", 1, result.getPath().size());
        assertEquals("Aisle should be 2", "2", result.getPath().get(0).getAisle());
        assertEquals("Bay should be 4", 4, result.getPath().get(0).getBay());
        // Distance from (0,0) to (2,4) = 0 + 2*3 + 4 = 10
        assertEquals("Distance should be 10", 10, result.getTotalDistance());
    }

    /**
     * Test Deterministic Sweep distance calculation.
     * Scenario: From entrance (0,0) → (1,2) → (1,5) → (2,3)
     */
    @Test
    public void testDeterministicSweep_DistanceCalculation() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "2", "5.0"},
            {"1", "5", "10.0"},
            {"2", "3", "8.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        // (0,0) → (1,2): 0 + 1*3 + 2 = 5
        // (1,2) → (1,5): |2-5| = 3
        // (1,5) → (2,3): 5 + 1*3 + 3 = 11
        // Total = 5 + 3 + 11 = 19
        assertEquals("Total distance should be 19", 19, result.getTotalDistance());
    }

    // ======================== Nearest Neighbour Tests ========================

    /**
     * Test Nearest Neighbour with single location.
     */
    @Test
    public void testNearestNeighbor_SingleLocation() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "3", "7.0"}
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        assertEquals("Strategy name should be correct", "Strategy B - Nearest Neighbour",
                     result.getStrategyName());
        assertEquals("Should have 1 location", 1, result.getPath().size());
        assertEquals("Bay should be 3", 3, result.getPath().get(0).getBay());
    }

    /**
     * Test Nearest Neighbour with multiple locations in same aisle.
     */
    @Test
    public void testNearestNeighbor_SameAisle() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "8", "10.0"},
            {"1", "2", "5.0"},
            {"1", "5", "8.0"}
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        assertEquals("Should have 3 locations", 3, result.getPath().size());
        // From (0,0), nearest is (1,2) with distance 5
        assertEquals("First should be bay 2", 2, result.getPath().get(0).getBay());
    }

    /**
     * Test Nearest Neighbour greedy selection.
     * Verifies that algorithm picks nearest location at each step.
     */
    @Test
    public void testNearestNeighbor_GreedySelection() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"3", "5", "10.0"},  // Far away
            {"1", "1", "5.0"},   // Very close to entrance
            {"1", "2", "8.0"}    // Close to entrance
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        // First pick should be (1,1) as it's nearest to (0,0)
        assertEquals("First pick should be aisle 1", "1", result.getPath().get(0).getAisle());
        assertEquals("First pick should be bay 1", 1, result.getPath().get(0).getBay());
    }

    /**
     * Test Nearest Neighbour with empty list.
     */
    @Test
    public void testNearestNeighbor_EmptyList() {
        List<BayLocation> bays = new ArrayList<>();

        RouteResult result = service.computeNearestNeighbor(bays);

        assertEquals("Path should be empty", 0, result.getPath().size());
        assertEquals("Distance should be 0", 0, result.getTotalDistance());
    }

    /**
     * Test Nearest Neighbour with multiple aisles.
     */
    @Test
    public void testNearestNeighbor_MultipleAisles() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "2", "5.0"},
            {"2", "4", "10.0"},
            {"1", "6", "8.0"}
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        assertEquals("Should have 3 locations", 3, result.getPath().size());
        assertNotNull("Result should not be null", result);
        assertTrue("Distance should be positive", result.getTotalDistance() > 0);
    }

    /**
     * Test Nearest Neighbour distance calculation.
     */
    @Test
    public void testNearestNeighbor_DistanceCalculation() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "1", "5.0"},
            {"1", "2", "10.0"}
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        // From (0,0) → (1,1): 0 + 1*3 + 1 = 4
        // From (1,1) → (1,2): |1-2| = 1
        // Total = 4 + 1 = 5
        assertEquals("Total distance should be 5", 5, result.getTotalDistance());
    }

    /**
     * Test Nearest Neighbour with locations at same distance.
     * Verifies that algorithm handles ties (first found is selected).
     */
    @Test
    public void testNearestNeighbor_TieBreaking() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "3", "5.0"},   // Distance from (0,0) = 6
            {"2", "0", "10.0"}   // Distance from (0,0) = 6 (0 + 2*3 + 0)
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        assertEquals("Should have 2 locations", 2, result.getPath().size());
        // First found with minimum distance will be selected
        assertNotNull("First location should exist", result.getPath().get(0));
    }

    // ======================== OptimizeRoute Tests ========================

    /**
     * Test optimizeRoute method that runs both strategies.
     */
    @Test
    public void testOptimizeRoute_BothStrategies() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "2", "5.0"},
            {"2", "3", "10.0"},
            {"1", "5", "8.0"}
        });

        Map<String, RouteResult> results = service.optimizeRoute(bays);

        assertEquals("Should have 2 results", 2, results.size());
        assertTrue("Should contain strategyA", results.containsKey("strategyA"));
        assertTrue("Should contain strategyB", results.containsKey("strategyB"));

        RouteResult strategyA = results.get("strategyA");
        RouteResult strategyB = results.get("strategyB");

        assertNotNull("Strategy A result should not be null", strategyA);
        assertNotNull("Strategy B result should not be null", strategyB);

        assertEquals("Strategy A should have 3 locations", 3, strategyA.getPath().size());
        assertEquals("Strategy B should have 3 locations", 3, strategyB.getPath().size());
    }

    /**
     * Test optimizeRoute with empty list.
     */
    @Test
    public void testOptimizeRoute_EmptyList() {
        List<BayLocation> bays = new ArrayList<>();

        Map<String, RouteResult> results = service.optimizeRoute(bays);

        assertEquals("Should have 2 results", 2, results.size());
        assertEquals("Strategy A distance should be 0", 0, results.get("strategyA").getTotalDistance());
        assertEquals("Strategy B distance should be 0", 0, results.get("strategyB").getTotalDistance());
    }

    /**
     * Test optimizeRoute comparing strategy results.
     */
    @Test
    public void testOptimizeRoute_CompareStrategies() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "1", "5.0"},
            {"1", "2", "10.0"},
            {"1", "3", "8.0"}
        });

        Map<String, RouteResult> results = service.optimizeRoute(bays);

        RouteResult strategyA = results.get("strategyA");
        RouteResult strategyB = results.get("strategyB");

        // Both strategies should process all 3 locations
        assertEquals("Strategy A should visit all 3 bays", 3, strategyA.getPath().size());
        assertEquals("Strategy B should visit all 3 bays", 3, strategyB.getPath().size());

        // Both should have positive distances
        assertTrue("Strategy A distance should be positive", strategyA.getTotalDistance() > 0);
        assertTrue("Strategy B distance should be positive", strategyB.getTotalDistance() > 0);
    }

    // ======================== Distance Calculation Edge Cases ========================

    /**
     * Test distance calculation for same aisle movement.
     */
    @Test
    public void testDistanceCalculation_SameAisle() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"2", "3", "5.0"},
            {"2", "7", "10.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        // (0,0) → (2,3): 0 + 2*3 + 3 = 9
        // (2,3) → (2,7): |3-7| = 4
        // Total = 9 + 4 = 13
        assertEquals("Distance should be 13", 13, result.getTotalDistance());
    }

    /**
     * Test distance calculation for different aisle movement.
     */
    @Test
    public void testDistanceCalculation_DifferentAisle() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "4", "5.0"},
            {"3", "2", "10.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        // (0,0) → (1,4): 0 + 1*3 + 4 = 7
        // (1,4) → (3,2): 4 + 2*3 + 2 = 12
        // Total = 7 + 12 = 19
        assertEquals("Distance should be 19", 19, result.getTotalDistance());
    }

    /**
     * Test distance calculation starting from entrance.
     */
    @Test
    public void testDistanceCalculation_FromEntrance() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "0", "5.0"}  // Bay at entrance of aisle 1
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        // (0,0) → (1,0): 0 + 1*3 + 0 = 3
        assertEquals("Distance should be 3", 3, result.getTotalDistance());
    }

    /**
     * Test with complex multi-aisle scenario.
     */
    @Test
    public void testComplexScenario_MultipleAislesAndBays() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "2", "5.0"},
            {"1", "8", "10.0"},
            {"2", "1", "3.0"},
            {"3", "5", "12.0"},
            {"2", "6", "7.0"}
        });

        Map<String, RouteResult> results = service.optimizeRoute(bays);

        RouteResult strategyA = results.get("strategyA");
        RouteResult strategyB = results.get("strategyB");

        // Both should visit all 5 locations
        assertEquals("Strategy A should visit 5 locations", 5, strategyA.getPath().size());
        assertEquals("Strategy B should visit 5 locations", 5, strategyB.getPath().size());

        // Distances should be positive and reasonable
        assertTrue("Strategy A distance should be positive", strategyA.getTotalDistance() > 0);
        assertTrue("Strategy B distance should be positive", strategyB.getTotalDistance() > 0);
    }

    /**
     * Test deterministic sweep maintains original quantities.
     */
    @Test
    public void testDeterministicSweep_PreservesQuantities() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "3", "5.5"},
            {"2", "1", "10.25"},
            {"1", "1", "7.75"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        // Verify quantities are preserved
        double totalQuantity = 0;
        for (BayLocation bay : result.getPath()) {
            totalQuantity += bay.getQuantity();
        }
        assertEquals("Total quantity should be preserved", 23.5, totalQuantity, 0.01);
    }

    /**
     * Test nearest neighbor maintains original quantities.
     */
    @Test
    public void testNearestNeighbor_PreservesQuantities() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"1", "2", "3.0"},
            {"2", "4", "6.0"},
            {"3", "1", "9.0"}
        });

        RouteResult result = service.computeNearestNeighbor(bays);

        // Verify all locations are included
        assertEquals("Should have all 3 locations", 3, result.getPath().size());

        // Verify quantities sum correctly
        double totalQuantity = 0;
        for (BayLocation bay : result.getPath()) {
            totalQuantity += bay.getQuantity();
        }
        assertEquals("Total quantity should be 18.0", 18.0, totalQuantity, 0.01);
    }

    /**
     * Test with large aisle numbers.
     */
    @Test
    public void testLargeAisleNumbers() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"10", "5", "5.0"},
            {"15", "3", "10.0"}
        });

        Map<String, RouteResult> results = service.optimizeRoute(bays);

        assertNotNull("Results should not be null", results);
        assertEquals("Should have 2 strategies", 2, results.size());

        RouteResult strategyA = results.get("strategyA");
        assertEquals("Should handle large aisle numbers", 2, strategyA.getPath().size());
    }

    /**
     * Test alphabetic aisle ordering.
     */
    @Test
    public void testAlphabeticAisleOrdering() {
        List<BayLocation> bays = createBayLocations(new String[][]{
            {"3", "1", "5.0"},
            {"1", "1", "10.0"},
            {"2", "1", "8.0"}
        });

        RouteResult result = service.computeDeterministicSweep(bays);

        // Should be sorted as: "1", "2", "3"
        assertEquals("First should be aisle 1", "1", result.getPath().get(0).getAisle());
        assertEquals("Second should be aisle 2", "2", result.getPath().get(1).getAisle());
        assertEquals("Third should be aisle 3", "3", result.getPath().get(2).getAisle());
    }
}

