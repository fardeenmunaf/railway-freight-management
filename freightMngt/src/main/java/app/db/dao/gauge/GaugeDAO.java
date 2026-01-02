package app.db.dao.gauge;

import app.model.Gauge;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Gauge.
 * Defines methods for manipulating Gauge data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface GaugeDAO {

    /**
     * Creates a new Gauge record in the database.
     *
     * @param gauge the Gauge object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Gauge gauge) throws SQLException;

    /**
     * Retrieves a Gauge based on its unique identifier.
     *
     * @param id the identifier of the Gauge
     * @return the Gauge found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Gauge get(int id) throws SQLException;

    /**
     * Retrieves all Gauges stored in the database.
     *
     * @return list with all Gauges
     * @throws SQLException if an error occurs during the operation
     */
    List<Gauge> getAll() throws SQLException;

    /**
     * Updates the data of an existing Gauge in the database.
     *
     * @param gauge the Gauge object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Gauge gauge) throws SQLException;

    /**
     * Removes a Gauge from the database based on its unique identifier.
     *
     * @param id the identifier of the Gauge to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves a Gauge by name.
     *
     * @param name the name of the Gauge
     * @return the Gauge found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Gauge getByName(String name) throws SQLException;

    /**
     * Checks if a Gauge exists with the specified ID.
     *
     * @param id the identifier of the Gauge
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

