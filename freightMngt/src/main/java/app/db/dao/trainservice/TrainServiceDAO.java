package app.db.dao.trainservice;

import app.model.TrainService;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO interface for CRUD operations on TrainService.
 * Defines methods for manipulating TrainService data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface TrainServiceDAO {

    /**
     * Creates a new TrainService record in the database.
     *
     * @param service the TrainService object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(TrainService service) throws SQLException;

    /**
     * Retrieves a TrainService based on its unique identifier.
     *
     * @param id the identifier of the TrainService
     * @return the TrainService found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    TrainService get(int id) throws SQLException;

    /**
     * Retrieves all TrainServices stored in the database.
     *
     * @return list with all TrainServices
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainService> getAll() throws SQLException;

    /**
     * Updates the data of an existing TrainService in the database.
     *
     * @param service the TrainService object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(TrainService service) throws SQLException;

    /**
     * Removes a TrainService from the database based on its unique identifier.
     *
     * @param id the identifier of the TrainService to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all TrainServices for a specific train.
     *
     * @param trainId the identifier of the train
     * @return list with TrainServices for the specified train
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainService> getByTrain(int trainId) throws SQLException;

    /**
     * Retrieves all TrainServices with a specific status.
     *
     * @param status the status to filter by
     * @return list with TrainServices matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainService> getByStatus(String status) throws SQLException;

    /**
     * Retrieves all TrainServices for a specific train driver.
     *
     * @param driverId the identifier of the train driver
     * @return list with TrainServices for the specified driver
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainService> getByDriver(int driverId) throws SQLException;

    /**
     * Retrieves all TrainServices in transit (currently active).
     *
     * @return list with TrainServices currently in transit
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainService> getInTransit() throws SQLException;

    /**
     * Retrieves all TrainServices scheduled for a specific date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list with TrainServices in the specified date range
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainService> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;

    /**
     * Checks if a TrainService exists with the specified ID.
     *
     * @param id the identifier of the TrainService
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

