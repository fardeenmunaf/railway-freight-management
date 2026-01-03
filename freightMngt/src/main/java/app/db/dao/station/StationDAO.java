package app.db.dao.station;

import app.model.Station;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Station.
 * Defines methods for manipulating Station data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface StationDAO {

    /**
     * Creates a new Station record in the database.
     *
     * @param station the Station object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Station station) throws SQLException;

    /**
     * Retrieves a Station based on its unique identifier.
     *
     * @param id the identifier of the Station
     * @return the Station found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Station get(int id) throws SQLException;

    /**
     * Retrieves all Stations stored in the database.
     *
     * @return list with all Stations
     * @throws SQLException if an error occurs during the operation
     */
    List<Station> getAll() throws SQLException;

    /**
     * Updates the data of an existing Station in the database.
     *
     * @param station the Station object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Station station) throws SQLException;

    /**
     * Removes a Station from the database based on its unique identifier.
     *
     * @param id the identifier of the Station to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all Stations for a specific country.
     *
     * @param country the country code to filter by
     * @return list with Stations in the specified country
     * @throws SQLException if an error occurs during the operation
     */
    List<Station> getByCountry(String country) throws SQLException;

    /**
     * Retrieves all main stations.
     *
     * @return list with all main stations
     * @throws SQLException if an error occurs during the operation
     */
    List<Station> getMainStations() throws SQLException;

    /**
     * Checks if a Station exists with the specified ID.
     *
     * @param id the identifier of the Station
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

