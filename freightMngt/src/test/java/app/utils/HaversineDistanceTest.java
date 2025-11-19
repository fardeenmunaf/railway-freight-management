package app.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HaversineDistance utility.
 * Validates distance calculations with known reference values.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class HaversineDistanceTest {

    private static final double DELTA = 0.1; // 100 meters tolerance

    @Test
    public void testDistanceBetweenLisbonAndPorto() {
        // Lisbon coordinates: 38.7223° N, 9.1393° W
        // Porto coordinates: 41.1579° N, 8.6291° W
        // Expected distance: ~274 km
        double distance = HaversineDistance.calculate(38.7223, -9.1393, 41.1579, -8.6291);
        assertEquals(274.0, distance, 5.0); // 5 km tolerance
    }

    @Test
    public void testDistanceBetweenNewYorkAndLondon() {
        // New York: 40.7128° N, 74.0060° W
        // London: 51.5074° N, 0.1278° W
        // Expected distance: ~5570 km
        double distance = HaversineDistance.calculate(40.7128, -74.0060, 51.5074, -0.1278);
        assertEquals(5570.0, distance, 50.0); // 50 km tolerance
    }

    @Test
    public void testDistanceSamePoint() {
        // Distance from a point to itself should be 0
        double distance = HaversineDistance.calculate(40.0, -8.0, 40.0, -8.0);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    public void testDistanceEquator() {
        // 1 degree of longitude at equator ≈ 111 km
        double distance = HaversineDistance.calculate(0.0, 0.0, 0.0, 1.0);
        assertEquals(111.2, distance, 1.0);
    }

    @Test
    public void testDistanceNorthSouth() {
        // 1 degree of latitude ≈ 111 km (constant)
        double distance = HaversineDistance.calculate(0.0, 0.0, 1.0, 0.0);
        assertEquals(111.2, distance, 1.0);
    }

    @Test
    public void testDistanceAntipodes() {
        // Opposite sides of Earth (should be ~20,000 km or half circumference)
        double distance = HaversineDistance.calculate(0.0, 0.0, 0.0, 180.0);
        assertEquals(20015.0, distance, 100.0);
    }

    @Test
    public void testConversionKmToMiles() {
        double km = 100.0;
        double miles = HaversineDistance.toMiles(km);
        assertEquals(62.1371, miles, 0.01);
    }

    @Test
    public void testConversionMilesToKm() {
        double miles = 100.0;
        double km = HaversineDistance.toKilometers(miles);
        assertEquals(160.934, km, 0.01);
    }

    @Test
    public void testEuclideanSquared() {
        // Test that euclidean squared gives consistent comparison
        double dist1 = HaversineDistance.euclideanSquared(0.0, 0.0, 1.0, 1.0);
        double dist2 = HaversineDistance.euclideanSquared(0.0, 0.0, 2.0, 2.0);
        assertTrue(dist2 > dist1);
        assertEquals(2.0, dist1, 0.001); // 1^2 + 1^2 = 2
        assertEquals(8.0, dist2, 0.001); // 2^2 + 2^2 = 8
    }

    @Test
    public void testNegativeCoordinates() {
        // Southern and Western hemispheres
        double distance = HaversineDistance.calculate(-23.5505, -46.6333, -34.6037, -58.3816);
        // São Paulo to Buenos Aires ≈ 1680 km
        assertEquals(1680.0, distance, 50.0);
    }
}

