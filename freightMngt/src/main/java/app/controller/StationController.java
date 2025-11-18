package app.controller;

import app.model.Station;
import app.repositories.FacilityRepository;
import app.repositories.Repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Station entities.
 * Provides specialized operations for Station objects beyond basic Facility operations.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class StationController {
    private final FacilityRepository facilityRepository;

    /**
     * Default constructor that initializes the controller with the facility repository.
     */
    public StationController() {
        this.facilityRepository = Repositories.getInstance().getFacilityRepository();
    }

    /**
     * Creates and adds a new station to the system.
     *
     * @param station the Station object to be created
     * @return the created Station object
     */
    public Station createStation(Station station) {
        if (station != null) {
            facilityRepository.addFacility(station);
        }
        return station;
    }

    /**
     * Retrieves all stations from the system.
     *
     * @return a list of all Station objects
     */
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        facilityRepository.getAllFacilities().forEach(facility -> {
            if (facility instanceof Station) {
                stations.add((Station) facility);
            }
        });
        return stations;
    }

    /**
     * Retrieves a station by its unique identifier.
     *
     * @param stationId the unique identifier of the station
     * @return an Optional containing the Station if found, or empty if not found
     */
    public Optional<Station> getStationById(Integer stationId) {
        var facility = facilityRepository.getFacilityById(stationId);
        if (facility instanceof Station) {
            return Optional.of((Station) facility);
        }
        return Optional.empty();
    }

    /**
     * Retrieves a station by its name.
     *
     * @param name the name of the station
     * @return an Optional containing the Station if found, or empty if not found
     */
    public Optional<Station> getStationByName(String name) {
        var facility = facilityRepository.getFacilityByName(name);
        if (facility instanceof Station) {
            return Optional.of((Station) facility);
        }
        return Optional.empty();
    }

    /**
     * Updates an existing station in the system.
     *
     * @param stationId      the unique identifier of the station to update
     * @param updatedStation the Station object with updated information
     * @return true if the station was updated successfully, false otherwise
     */
    public boolean updateStation(Integer stationId, Station updatedStation) {
        return facilityRepository.updateFacility(stationId, updatedStation);
    }

    /**
     * Deletes a station from the system.
     *
     * @param stationId the unique identifier of the station to delete
     * @return true if the station was deleted successfully, false otherwise
     */
    public boolean deleteStation(Integer stationId) {
        return facilityRepository.deleteFacility(stationId);
    }

    /**
     * Retrieves stations by country.
     *
     * @param country the country code
     * @return list of stations in the specified country
     */
    public List<Station> getStationsByCountry(String country) {
        List<Station> result = new ArrayList<>();
        for (Station station : getAllStations()) {
            if (country.equalsIgnoreCase(station.getCountry())) {
                result.add(station);
            }
        }
        return result;
    }

    /**
     * Retrieves main stations only.
     *
     * @return list of stations where isMainStation is true
     */
    public List<Station> getMainStations() {
        List<Station> result = new ArrayList<>();
        for (Station station : getAllStations()) {
            if (Boolean.TRUE.equals(station.getIsMainStation())) {
                result.add(station);
            }
        }
        return result;
    }

    /**
     * Retrieves airport stations only.
     *
     * @return list of stations where isAirport is true
     */
    public List<Station> getAirportStations() {
        List<Station> result = new ArrayList<>();
        for (Station station : getAllStations()) {
            if (Boolean.TRUE.equals(station.getIsAirport())) {
                result.add(station);
            }
        }
        return result;
    }
}
