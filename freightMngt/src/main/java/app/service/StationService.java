package app.service;

import app.model.Station;
import app.controller.StationController;
import app.utils.AVLTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Service class responsible for efficient station search operations.
 * Uses AVL Tree indexing for optimized queries by name, country, and time zone.
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
     * Builds all search indexes from the database.
     * Must be called after stations are loaded.
     */
    public void buildIndexes() {
        List<Station> allStations = stationController.getAllStations();

        for (Station station : allStations) {
            indexStation(station);
        }
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
}
