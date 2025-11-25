package app.service.sprint2;

import app.model.Station;
import app.controller.StationController;
import app.utils.AVLTree;
import app.utils.Balanced2DTreeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for efficient station search operations.
 * Uses AVL Tree indexing for optimized queries by name, country, and time zone.
 * Uses 2D-tree for spatial nearest neighbor searches.
 *
 * @author G113 2DK
 * @version 1.0
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
        List<Station> validStations = new ArrayList<>();

        for (Station station : allStations) {
            if (station.getLat() != null && station.getLon() != null) {
                validStations.add(station);
            }
        }

        spatial2DTree = new Balanced2DTreeBuilder.Balanced2DTree();
        spatial2DTree.bulkBuild(validStations);
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
     * Searches stations by exact coordinates.
     * Handles duplicates sorted by name ASC.
     * Complexity: O(log n + k) where k = stations at coordinates
     */
    public List<Station> searchByCoordinates(Double lat, Double lon) {
        String keyPrefix = String.format("%.6f_%.6f_", lat, lon);
        String keyMin = keyPrefix + "A";
        String keyMax = keyPrefix + "z";
        return stationsByCoordinates.rangeSearch(keyMin, keyMax);
    }

    /**
     * Searches stations in a time-zone group, ordered by country ASC.
     * Complexity: O(log n + k*log k) where k = results
     */
    public List<Station> searchByTimeZoneGroupOrderedByCountry(String group) {
        List<Station> results = searchByTimeZoneGroupRange(group, group);
        results.sort((a, b) -> {
            int countryCompare = a.getCountry().compareToIgnoreCase(b.getCountry());
            return countryCompare != 0 ? countryCompare :
                    a.getName().compareToIgnoreCase(b.getName());
        });
        return results;
    }

    /**
     * Searches stations in a time-zone group range.
     * Example: searchByTimeZoneGroupRange("CET", "WET/GMT")
     * Complexity: O(log n + k) where k = results
     */
    public List<Station> searchByTimeZoneGroupRange(String fromGroup, String toGroup) {
        return stationsByTimeZoneGroup.rangeSearch(fromGroup, toGroup);
    }

    /**
     * Searches stations by latitude range.
     * Complexity: O(log n + k)
     */
    public List<Station> searchByLatitudeRange(Double minLat, Double maxLat) {
        return stationsByLatitude.rangeSearch(
                String.format("%.6f", minLat),
                String.format("%.6f", maxLat)
        );
    }

    /**
     * Searches stations by exact name match.
     */
    public List<Station> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return new ArrayList<>();
        }
        return stationsByName.search(name.toLowerCase());
    }

    /**
     * Searches stations by country code.
     */
    public List<Station> searchByCountry(String country) {
        if (country == null || country.isBlank()) {
            return new ArrayList<>();
        }
        return stationsByCountry.search(country.toLowerCase());
    }

    /**
     * Searches stations by time zone.
     */
    public List<Station> searchByTimeZone(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            return new ArrayList<>();
        }
        return stationsByTimeZone.search(timeZone.toLowerCase());
    }

    /**
     * Range search for stations by time zone.
     */
    public List<Station> searchByTimeZoneRange(String fromZone, String toZone) {
        return stationsByTimeZone.rangeSearch(fromZone, toZone);
    }

    /**
     * Returns all main stations.
     */
    public List<Station> getMainStations() {
        return stationController.getMainStations();
    }

    /**
     * Returns all airport stations.
     */
    public List<Station> getAirportStations() {
        return stationController.getAirportStations();
    }

    /**
     * Returns all stations sorted by name.
     */
    public List<Station> getAllStationsSortedByName() {
        return stationsByName.inOrderTraversal();
    }

    /**
     * Returns total number of indexed stations.
     */
    public int getStationCount() {
        return stationController.getAllStations().size();
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
     * Uses KD-tree for efficient spatial search with branch pruning.
     * Results are ordered by distance ascending.
     *
     * Temporal Complexity: O(log N) average case, O(N) worst case
     * where N is the total number of stations in the tree.
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

        List<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results =
                spatial2DTree.findNearestStations(latitude, longitude, n);

        // Convert to StationDistance with full Station objects
        List<StationDistance> stationDistances = new ArrayList<>();
        for (Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance result : results) {
            // Find the full Station object by name and coordinates
            Station station = findStationByNameAndCoordinates(
                    result.station.getName(),
                    result.station.getLat(),
                    result.station.getLon()
            );
            if (station != null) {
                stationDistances.add(new StationDistance(station, result.distance));
            }
        }

        return stationDistances;
    }

    /**
     * Finds the N nearest stations to a given coordinate with optional time-zone group filter.
     *
     * If timeZoneGroup is provided, only stations in that time-zone group are returned.
     * Results are ordered by distance ascending.
     *
     * Temporal Complexity: O(log N) average case for spatial search,
     * plus O(k) for filtering where k is the number of candidates.
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

        if (spatial2DTree == null) {
            build2DTree();
        }

        // Get more candidates than needed to account for filtering
        int candidateMultiplier = 5;
        int maxCandidates = Math.min(n * candidateMultiplier, spatial2DTree.getSize());

        List<StationDistance> filteredResults = new ArrayList<>();
        int candidatesChecked = n;
        int previousCandidatesChecked = 0;

        // Keep searching until we have enough results or checked all stations
        while (filteredResults.size() < n && candidatesChecked <= spatial2DTree.getSize()) {
            // Break if we're not making progress (prevents infinite loop when no matches exist)
            if (candidatesChecked == previousCandidatesChecked) {
                break;
            }
            previousCandidatesChecked = candidatesChecked;

            List<Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance> results =
                    spatial2DTree.findNearestStations(latitude, longitude, candidatesChecked);

            filteredResults.clear();

            // Filter by time-zone group
            for (Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance result : results) {
                Station station = findStationByNameAndCoordinates(
                        result.station.getName(),
                        result.station.getLat(),
                        result.station.getLon()
                );

                if (station != null && timeZoneGroup.equals(station.getTimeZoneGroup())) {
                    filteredResults.add(new StationDistance(station, result.distance));
                    if (filteredResults.size() >= n) {
                        break;
                    }
                }
            }

            // Increase candidates if we didn't find enough
            if (filteredResults.size() < n) {
                candidatesChecked = Math.min(candidatesChecked * 2, spatial2DTree.getSize());
            }
        }

        // Return only the requested number
        return filteredResults.size() > n ?
                filteredResults.subList(0, n) : filteredResults;
    }

    /**
     * Finds the single nearest station to a given coordinate.
     *
     * @param latitude Target latitude in degrees
     * @param longitude Target longitude in degrees
     * @return The nearest station with distance, or null if no stations available
     */
    public StationDistance findNearestStation(double latitude, double longitude) {
        List<StationDistance> results = findNearestStations(latitude, longitude, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Finds the single nearest station with time-zone group filter.
     *
     * @param latitude Target latitude in degrees
     * @param longitude Target longitude in degrees
     * @param timeZoneGroup Time-zone group filter
     * @return The nearest station with distance, or null if no matching stations
     */
    public StationDistance findNearestStation(double latitude, double longitude, String timeZoneGroup) {
        List<StationDistance> results = findNearestStations(latitude, longitude, 1, timeZoneGroup);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Helper method to find a Station object by name and coordinates.
     */
    private Station findStationByNameAndCoordinates(String name, double lat, double lon) {
        List<Station> candidates = stationsByName.search(name.toLowerCase());

        for (Station station : candidates) {
            if (station.getLat() != null && station.getLon() != null) {
                if (Math.abs(station.getLat() - lat) < 0.0001 &&
                        Math.abs(station.getLon() - lon) < 0.0001) {
                    return station;
                }
            }
        }

        return null;
    }

    /**
     * Returns temporal complexity analysis for the 2D-tree.
     */
    public Balanced2DTreeBuilder.TemporalComplexity get2DTreeComplexity() {
        if (spatial2DTree == null) {
            build2DTree();
        }
        return spatial2DTree.getTemporalComplexity();
    }

    /**
     * Returns the spatial 2D-tree for nearest-neighbor and radius searches.
     * Lazily builds it if not yet constructed.
     */
    public Balanced2DTreeBuilder.Balanced2DTree getSpatial2DTree() {
        if (spatial2DTree == null) {
            build2DTree();
        }
        return spatial2DTree;
    }
}