package app.db.dao.trainstop;

import app.model.TrainStop;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on TrainStop.
 * Defines methods for manipulating TrainStop data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface TrainStopDAO {

    /**
     * Creates a new TrainStop record in the database.
     *
     * @param stop the TrainStop object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(TrainStop stop) throws SQLException;

    /**
     * Retrieves a TrainStop based on its unique identifier.
     *
     * @param id the identifier of the TrainStop
     * @return the TrainStop found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    TrainStop get(int id) throws SQLException;

    /**
     * Retrieves all TrainStops stored in the database.
     *
     * @return list with all TrainStops
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainStop> getAll() throws SQLException;

    /**
     * Updates the data of an existing TrainStop in the database.
     *
     * @param stop the TrainStop object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(TrainStop stop) throws SQLException;

    /**
     * Removes a TrainStop from the database based on its unique identifier.
     *
     * @param id the identifier of the TrainStop to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all TrainStops for a specific train service, ordered by position.
     *
     * @param serviceId the identifier of the train service
     * @return list with TrainStops for the specified service, in sequence order
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainStop> getByService(int serviceId) throws SQLException;

    /**
     * Retrieves all TrainStops at a specific facility.
     *
     * @param facilityId the identifier of the facility
     * @return list with TrainStops at the specified facility
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainStop> getByFacility(int facilityId) throws SQLException;

    /**
     * Retrieves all TrainStops assigned to a specific parking track.
     *
     * @param parkingTrackId the identifier of the parking track
     * @return list with TrainStops for the specified parking track
     * @throws SQLException if an error occurs during the operation
     */
    List<TrainStop> getByParkingTrack(int parkingTrackId) throws SQLException;

    /**
     * Deletes all TrainStops for a specific train service.
     *
     * @param serviceId the identifier of the train service
     * @throws SQLException if an error occurs during the operation
     */
    void deleteByService(int serviceId) throws SQLException;

    /**
     * Checks if a TrainStop exists with the specified ID.
     *
     * @param id the identifier of the TrainStop
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

