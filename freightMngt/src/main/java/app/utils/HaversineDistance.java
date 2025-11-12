package app.utils;

/**
 * Utility class for calculating distances between geographic coordinates
 * using the Haversine formula.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class HaversineDistance {

    /**
     * Earth's radius in kilometers.
     */
    public static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the distance between two geographic points using the Haversine formula.
     *
     * The Haversine formula determines the great-circle distance between two points
     * on a sphere given their longitudes and latitudes.
     *
     * Formula:
     * a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
     * c = 2 ⋅ atan2(√a, √(1−a))
     * d = R ⋅ c
     *
     * where φ is latitude, λ is longitude, R is earth's radius
     *
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Distance in kilometers
     */
    public static double calculate(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the squared Euclidean distance between two points in 2D space.
     * This is used for preliminary filtering in KD-tree searches (faster than Haversine).
     *
     * Note: This is NOT the actual geographic distance, but useful for quick comparisons.
     *
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Squared Euclidean distance (for comparison purposes only)
     */
    public static double euclideanSquared(double lat1, double lon1, double lat2, double lon2) {
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        return deltaLat * deltaLat + deltaLon * deltaLon;
    }

    /**
     * Converts kilometers to miles.
     *
     * @param kilometers Distance in kilometers
     * @return Distance in miles
     */
    public static double toMiles(double kilometers) {
        return kilometers * 0.621371;
    }

    /**
     * Converts miles to kilometers.
     *
     * @param miles Distance in miles
     * @return Distance in kilometers
     */
    public static double toKilometers(double miles) {
        return miles / 0.621371;
    }
}

