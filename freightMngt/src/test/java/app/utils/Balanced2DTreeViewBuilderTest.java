package app.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Balanced2DTreeBuilder class.
 * Tests tree construction, balancing, bucket management, and complexity analysis.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class Balanced2DTreeViewBuilderTest {

    private Balanced2DTreeBuilder.Balanced2DTree tree;
    private List<Balanced2DTreeBuilder.Station> stations;

    /**
     * Sets up a fresh tree and station list before each test.
     */
    @BeforeEach
    void setUp() {
        tree = new Balanced2DTreeBuilder.Balanced2DTree();
        stations = new ArrayList<>();
    }

    /**
     * Tests that an empty station list results in a null root and zero size.
     */
    @Test
    @DisplayName("Should handle empty station list")
    void testEmptyStationList() {
        tree.bulkBuild(stations);

        assertEquals(0, tree.getSize());
        assertEquals(0, tree.getHeight());
        assertTrue(tree.getBucketSizes().isEmpty());
        assertTrue(tree.getAllStations().isEmpty());
    }

    /**
     * Tests that a null station list results in a null root and zero size.
     */
    @Test
    @DisplayName("Should handle null station list")
    void testNullStationList() {
        tree.bulkBuild(null);

        assertEquals(0, tree.getSize());
        assertEquals(0, tree.getHeight());
        assertTrue(tree.getAllStations().isEmpty());
    }

    /**
     * Tests tree construction with a single station.
     */
    @Test
    @DisplayName("Should build tree with single station")
    void testSingleStation() {
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "Station A"));
        tree.bulkBuild(stations);

        assertEquals(1, tree.getSize());
        assertEquals(1, tree.getHeight());
        assertEquals(1, tree.getBucketSizes().size());
        assertEquals(1, tree.getBucketSizes().get(0));

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(1, allStations.size());
        assertEquals("Station A", allStations.get(0).name);
    }

    /**
     * Tests tree construction with multiple unique stations.
     */
    @Test
    @DisplayName("Should build balanced tree with multiple unique stations")
    void testMultipleUniqueStations() {
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "Station A"));
        stations.add(new Balanced2DTreeBuilder.Station(41.0, -73.0, "Station B"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "Station C"));
        stations.add(new Balanced2DTreeBuilder.Station(42.0, -72.0, "Station D"));
        stations.add(new Balanced2DTreeBuilder.Station(38.0, -76.0, "Station E"));

        tree.bulkBuild(stations);

        assertEquals(5, tree.getSize());
        assertTrue(tree.getHeight() <= 4); // Balanced tree property
        assertEquals(5, tree.getBucketSizes().size());

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(5, allStations.size());
    }

    /**
     * Tests bucket creation when multiple stations share the same coordinates.
     */
    @Test
    @DisplayName("Should group stations with identical coordinates in bucket")
    void testStationsWithSameCoordinates() {
        stations.add(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Lisbon Santa Apolónia"));
        stations.add(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Lisbon Oriente"));
        stations.add(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Station A"));
        tree.bulkBuild(stations);

        assertEquals(1, tree.getSize());
        assertEquals(1, tree.getBucketSizes().size());
        assertEquals(3, tree.getBucketSizes().get(0));

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(3, allStations.size());
    }

    /**
     * Tests that stations in a bucket are sorted alphabetically by name.
     */
    @Test
    @DisplayName("Should sort stations alphabetically within bucket")
    void testBucketAlphabeticalSorting() {
        Balanced2DTreeBuilder.StationBucket bucket = new Balanced2DTreeBuilder.StationBucket();
        bucket.addStation(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Lisbon Santa Apolónia"));
        bucket.addStation(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Lisbon Oriente"));
        bucket.addStation(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Station A"));

        assertEquals(3, bucket.size());
        assertEquals("Lisbon Oriente", bucket.stations.get(0).name);
        assertEquals("Lisbon Santa Apolónia", bucket.stations.get(1).name);
        assertEquals("Station A", bucket.stations.get(2).name);
    }

    /**
     * Tests that getAllStations returns stations in alphabetical order within buckets.
     */
    @Test
    @DisplayName("Should return all stations in correct order from tree")
    void testGetAllStationsAlphabeticalOrder() {
        stations.add(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Lisbon Santa Apolónia"));
        stations.add(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Lisbon Oriente"));
        stations.add(new Balanced2DTreeBuilder.Station(38.71387, -9.122271, "Station A"));
        tree.bulkBuild(stations);

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(3, allStations.size());
        assertEquals("Lisbon Oriente", allStations.get(0).name);
        assertEquals("Lisbon Santa Apolónia", allStations.get(1).name);
        assertEquals("Station A", allStations.get(2).name);
    }

    /**
     * Tests that the tree maintains balance with a larger dataset.
     */
    @Test
    @DisplayName("Should maintain balance with larger dataset")
    void testTreeBalanceWithLargeDataset() {
        for (int i = 0; i < 100; i++) {
            stations.add(new Balanced2DTreeBuilder.Station(
                    40.0 + i * 0.1,
                    -74.0 + i * 0.1,
                    "Station " + i
            ));
        }

        tree.bulkBuild(stations);

        assertEquals(100, tree.getSize());
        int expectedMaxHeight = (int) Math.ceil(Math.log(100) / Math.log(2)) + 2;
        assertTrue(tree.getHeight() <= expectedMaxHeight);

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(100, allStations.size());
    }

    /**
     * Tests the temporal complexity analysis output.
     */
    @Test
    @DisplayName("Should provide temporal complexity analysis")
    void testTemporalComplexityAnalysis() {
        for (int i = 0; i < 10; i++) {
            stations.add(new Balanced2DTreeBuilder.Station(
                    40.0 + i,
                    -74.0 + i,
                    "Station " + i
            ));
        }

        tree.bulkBuild(stations);
        Balanced2DTreeBuilder.TemporalComplexity complexity = tree.getTemporalComplexity();

        assertNotNull(complexity);
        assertEquals(10, complexity.totalNodes);
        assertTrue(complexity.buildComplexity.contains("O(N log N)"));
        assertTrue(complexity.searchComplexity.contains("O(log N)"));
        assertTrue(complexity.rangeSearchComplexity.contains("O(√N + k)"));
    }

    /**
     * Tests node height calculation.
     */
    @Test
    @DisplayName("Should calculate node height correctly")
    void testNodeHeightCalculation() {
        Balanced2DTreeBuilder.Node2D node = new Balanced2DTreeBuilder.Node2D(
                40.0,
                true,
                new Balanced2DTreeBuilder.StationBucket()
        );

        assertEquals(1, node.height);

        node.left = new Balanced2DTreeBuilder.Node2D(39.0, false, new Balanced2DTreeBuilder.StationBucket());
        node.right = new Balanced2DTreeBuilder.Node2D(41.0, false, new Balanced2DTreeBuilder.StationBucket());
        node.updateHeight();

        assertEquals(2, node.height);
    }

    /**
     * Tests tree construction with mixed duplicate and unique coordinates.
     */
    @Test
    @DisplayName("Should handle mixed duplicate and unique coordinates")
    void testMixedDuplicateAndUniqueCoordinates() {
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "A1"));
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "A2"));
        stations.add(new Balanced2DTreeBuilder.Station(41.0, -73.0, "B1"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "C1"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "C2"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "C3"));

        tree.bulkBuild(stations);

        assertEquals(3, tree.getSize()); // 3 unique coordinate pairs
        List<Integer> bucketSizes = tree.getBucketSizes();
        assertTrue(bucketSizes.contains(2)); // 2 stations at (40.0, -74.0)
        assertTrue(bucketSizes.contains(1)); // 1 station at (41.0, -73.0)
        assertTrue(bucketSizes.contains(3)); // 3 stations at (39.0, -75.0)

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(6, allStations.size()); // Total of 6 stations
    }

    /**
     * Tests that bucket sizes are collected in order.
     */
    @Test
    @DisplayName("Should collect bucket sizes in traversal order")
    void testBucketSizesCollection() {
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "Station A"));
        stations.add(new Balanced2DTreeBuilder.Station(41.0, -73.0, "Station B"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "Station C"));

        tree.bulkBuild(stations);
        List<Integer> bucketSizes = tree.getBucketSizes();

        assertEquals(3, bucketSizes.size());
        assertEquals(3, bucketSizes.stream().mapToInt(Integer::intValue).sum());
    }

    /**
     * Tests that getAllStations returns the correct total count with mixed buckets.
     */
    @Test
    @DisplayName("Should return correct total station count from getAllStations")
    void testGetAllStationsTotalCount() {
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "A1"));
        stations.add(new Balanced2DTreeBuilder.Station(40.0, -74.0, "A2"));
        stations.add(new Balanced2DTreeBuilder.Station(41.0, -73.0, "B1"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "C1"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "C2"));
        stations.add(new Balanced2DTreeBuilder.Station(39.0, -75.0, "C3"));

        tree.bulkBuild(stations);

        List<Balanced2DTreeBuilder.Station> allStations = tree.getAllStations();
        assertEquals(6, allStations.size());

        // Verify bucket sizes sum matches total stations
        List<Integer> bucketSizes = tree.getBucketSizes();
        int totalFromBuckets = bucketSizes.stream().mapToInt(Integer::intValue).sum();
        assertEquals(allStations.size(), totalFromBuckets);
    }
}