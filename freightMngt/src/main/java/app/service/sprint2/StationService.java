package app.service.sprint2;

import app.model.Station;
import app.controller.StationController;
import app.utils.AVLTree;
import app.utils.Balanced2DTreeBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for efficient station search operations.
 * Uses AVL Tree indexing for optimized queries by name, country, and time zone.
 * Uses 2D-tree for spatial nearest neighbor searches.
 *
 * @author G113 2DK
 * @version 1.2 (Refactored to use domain Station in 2D-Tree and fixed service methods)
 */
public class StationService {
    private StationController stationController;
    private AVLTree<String, Station> stationsByName;
    private AVLTree<String, Station> stationsByCountry;
    private AVLTree<String, Station> stationsByTimeZone;
    private AVLTree<String, Station> stationsByTimeZoneGroup;
    private AVLTree<String, Station> stationsByLatitude;
    private AVLTree<String, Station> stationsByLongitude;
    private AVLTree<String, Station> stationsByCoordinates;
    private Balanced2DTreeBuilder.Balanced2DTree spatial2DTree;

    public StationService() {
        this.stationController = new StationController();
        this.stationsByName = new AVLTree<>();
        this.stationsByCountry = new AVLTree<>();
        this.stationsByTimeZone = new AVLTree<>();
        this.stationsByTimeZoneGroup = new AVLTree<>();
        this.stationsByLatitude = new AVLTree<>();
        this.stationsByLongitude = new AVLTree<>();
        this.stationsByCoordinates = new AVLTree<>();
    }

    /**
     * Builds all search indexes from the database, including the 2D-tree for spatial searches.
     * Must be called after stations are loaded.
     */
    public void buildIndexes() {
        List<Station> allStations = stationController.getAllStations();

        for (Station station : allStations) {
            indexStation(station);
        }

        // Build 2D-tree for spatial searches
        build2DTree();
    }

    /**
     * Builds the 2D-tree index for spatial nearest neighbor searches.
     * Complexity: O(N log N) where N is the number of stations.
     */
    private void build2DTree() {
        List<Station> allStations = stationController.getAllStations();

        // Filter stations that have valid coordinates
        List<Station> stationsWithCoords = allStations.stream()
                .filter(s -> s.getLat() != null && s.getLon() != null)
                .collect(Collectors.toList());

        spatial2DTree = new Balanced2DTreeBuilder.Balanced2DTree();
        // Pass the list of domain objects directly
        spatial2DTree.bulkBuild(stationsWithCoords);
    }

    /**
     * Adds a station to all indexes.
     */
    private void indexStation(Station station) {
        if (station.getName() != null) {
            stationsByName.insert(station.getName().toLowerCase(), station);
        }
        if (station.getCountry() != null) {
            stationsByCountry.insert(station.getCountry().toLowerCase(), station);
        }
        if (station.getTimeZone() != null) {
            stationsByTimeZone.insert(station.getTimeZone().toLowerCase(), station);
        }
        if (station.getTimeZoneGroup() != null) {
            stationsByTimeZoneGroup.insert(station.getTimeZoneGroup(), station);
        }
        if (station.getLat() != null && station.getLon() != null) {
            String coordKey = String.format("%.6f_%.6f_%s",
                    station.getLat(), station.getLon(), station.getName());
            stationsByCoordinates.insert(coordKey, station);
            stationsByLatitude.insert(String.format("%.6f", station.getLat()), station);
            stationsByLongitude.insert(String.format("%.6f", station.getLon()), station);
        }
    }

    /**
     * Searches stations by exact name match.
     * Complexity: O(log n) where n = total stations
     */
    public List<Station> searchByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ArrayList<>();
        }
        return stationsByName.search(name.toLowerCase());
    }

    /**
     * Searches stations by country code.
     * Complexity: O(log n + k) where k = results
     */
    public List<Station> searchByCountry(String country) {
        if (country == null || country.isEmpty()) {
            return new ArrayList<>();
        }
        return stationsByCountry.search(country.toLowerCase());
    }

    /**
     * Searches stations by time zone.
     * Complexity: O(log n + k) where k = results
     */
    public List<Station> searchByTimeZone(String timeZone) {
        if (timeZone == null || timeZone.isEmpty()) {
            return new ArrayList<>();
        }
        return stationsByTimeZone.search(timeZone.toLowerCase());
    }

    /**
     * Searches stations in a time-zone group, ordered by country ASC.
     * Complexity: O(log n + k*log k) where k = results
     */
    public List<Station> searchByTimeZoneGroupOrderedByCountry(String timeZoneGroup) {
        if (timeZoneGroup == null || timeZoneGroup.isBlank()) {
            return new ArrayList<>();
        }
        List<Station> results = stationsByTimeZoneGroup.search(timeZoneGroup);
        results.sort(Comparator.comparing(Station::getCountry));
        return results;
    }

    /**
     * Searches for stations in a time zone group range.
     * Complexity: O(N) worst case.
     */
    public List<Station> searchByTimeZoneGroupRange(String startGroup, String endGroup) {
        if (startGroup == null || endGroup == null) {
            return new ArrayList<>();
        }
        return stationsByTimeZoneGroup.rangeSearch(startGroup, endGroup);
    }

    /**
     * Searches stations by exact coordinates.
     * Handles duplicates sorted by name ASC.
     * Complexity: O(log n + k) where k = stations at coordinates
     */
    public List<Station> searchByCoordinates(double lat, double lon) {
        String coordKey = String.format("%.6f_%.6f_", lat, lon);
        String keyMin = coordKey + "A";
        String keyMax = coordKey + "z";
        return stationsByCoordinates.rangeSearch(keyMin, keyMax);
    }

    /**
     * Searches stations by latitude range.
     * Complexity: O(log n + k)
     */
    public List<Station> searchByLatitudeRange(double minLat, double maxLat) {
        return stationsByLatitude.rangeSearch(
                String.format("%.6f", minLat),
                String.format("%.6f", maxLat)
        );
    }

    /**
     * Gets all main stations (stations with isMain = true).
     *
     * @return list of main stations
     */
    public List<Station> getMainStations() {
        return stationController.getMainStations();
    }

    /**
     * Gets all airport stations (stations with isAirport = true).
     *
     * @return list of airport stations
     */
    public List<Station> getAirportStations() {
        return stationController.getAirportStations();
    }

    /**
     * Result wrapper for a station with its distance from query point.
     */
    public static class StationDistance {
        public final Station station;
        public final double distanceKm;

        public StationDistance(Station station, double distanceKm) {
            this.station = station;
            this.distanceKm = distanceKm;
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f km)", station.getName(), distanceKm);
        }
    }

    /**
     * Finds the N nearest stations to a given coordinate using Haversine distance.
     *
     * @param latitude Target latitude in degrees
     * @param longitude Target longitude in degrees
     * @param n Number of nearest neighbors to find
     * @return List of N nearest stations with distances, sorted by distance ASC
     */
    public List<StationDistance> findNearestStations(double latitude, double longitude, int n) {
        if (spatial2DTree == null) {
            build2DTree();
        }

        // Get nearest stations with distances from 2D tree (already sorted by distance)
        List<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results =
                spatial2DTree.findNearestStations(latitude, longitude, n);

        // Convert to service's StationDistance wrapper
        List<StationDistance> stationDistances = new ArrayList<>();
        for (Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance result : results) {
            stationDistances.add(new StationDistance(result.station, result.distance));
        }

        return stationDistances;
    }

    /**
     * Finds the N nearest stations to a given coordinate with optional time-zone group filter.
     *
     * @param latitude Target latitude in degrees
     * @param longitude Target longitude in degrees
     * @param n Number of nearest neighbors to find
     * @param timeZoneGroup Optional time-zone group filter (null for no filter)
     * @return List of N nearest stations with distances, sorted by distance ASC
     */
    public List<StationDistance> findNearestStations(double latitude, double longitude,
                                                     int n, String timeZoneGroup) {
        if (timeZoneGroup == null || timeZoneGroup.isBlank()) {
            return findNearestStations(latitude, longitude, n);
        }

        // Get all stations sorted by distance (using a large number for 'n' to get all candidates)
        List<StationDistance> nearest = findNearestStations(latitude, longitude, getStationCount());

        return nearest.stream()
                .filter(sd -> timeZoneGroup.equalsIgnoreCase(sd.station.getTimeZoneGroup()))
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Finds the single nearest station to a given point.
     *
     * @param latitude Target latitude in degrees
     * @param longitude Target longitude in degrees
     * @return The nearest station with distance, or null if no stations exist
     */
    public StationDistance findNearestStation(double latitude, double longitude) {
        List<StationDistance> results = findNearestStations(latitude, longitude, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Gets the temporal complexity analysis of the 2D tree.
     *
     * @return TemporalComplexity object containing size and height information
     */
    public Balanced2DTreeBuilder.TemporalComplexity get2DTreeComplexity() {
        if (spatial2DTree == null) {
            build2DTree();
        }
        return spatial2DTree.getTemporalComplexity();
    }

    /**
     * Returns total number of indexed stations in the 2D-tree.
     */
    public int getStationCount() {
        if (spatial2DTree == null) {
            build2DTree();
        }
        return spatial2DTree.getSize();
    }
}
