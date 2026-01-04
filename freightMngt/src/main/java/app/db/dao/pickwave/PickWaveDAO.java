package app.db.dao.pickwave;

import app.model.PickWave;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on PickWave.
 * Defines methods for manipulating PickWave data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface PickWaveDAO {

    /**
     * Creates a new PickWave record in the database.
     *
     * @param pickWave the PickWave object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(PickWave pickWave) throws SQLException;

    /**
     * Retrieves a PickWave based on its unique identifier.
     *
     * @param id the identifier of the PickWave
     * @return the PickWave found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    PickWave get(int id) throws SQLException;

    /**
     * Retrieves all PickWaves stored in the database.
     *
     * @return list with all PickWaves
     * @throws SQLException if an error occurs during the operation
     */
    List<PickWave> getAll() throws SQLException;

    /**
     * Updates the data of an existing PickWave in the database.
     *
     * @param pickWave the PickWave object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(PickWave pickWave) throws SQLException;

    /**
     * Removes a PickWave from the database based on its unique identifier.
     *
     * @param id the identifier of the PickWave to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all PickWaves with a specific status.
     *
     * @param status the status to filter by
     * @return list with PickWaves matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<PickWave> getByStatus(String status) throws SQLException;

    /**
     * Checks if a PickWave exists with the specified ID.
     *
     * @param id the identifier of the PickWave
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

