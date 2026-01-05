package app.db.dao.picktask;

import app.model.PickTask;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on PickTask.
 * Defines methods for manipulating PickTask data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface PickTaskDAO {

    /**
     * Creates a new PickTask record in the database.
     *
     * @param pickTask the PickTask object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(PickTask pickTask) throws SQLException;

    /**
     * Retrieves a PickTask based on its unique identifier.
     *
     * @param id the identifier of the PickTask
     * @return the PickTask found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    PickTask get(int id) throws SQLException;

    /**
     * Retrieves all PickTasks stored in the database.
     *
     * @return list with all PickTasks
     * @throws SQLException if an error occurs during the operation
     */
    List<PickTask> getAll() throws SQLException;

    /**
     * Updates the data of an existing PickTask in the database.
     *
     * @param pickTask the PickTask object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(PickTask pickTask) throws SQLException;

    /**
     * Removes a PickTask from the database based on its unique identifier.
     *
     * @param id the identifier of the PickTask to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all PickTasks for a specific PickWave.
     *
     * @param waveId the identifier of the PickWave
     * @return list with PickTasks for the specified PickWave
     * @throws SQLException if an error occurs during the operation
     */
    List<PickTask> getByWave(int waveId) throws SQLException;

    /**
     * Checks if a PickTask exists with the specified ID.
     *
     * @param id the identifier of the PickTask
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

