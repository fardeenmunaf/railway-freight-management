package app.db.dao.tracktype;

import app.model.TrackType;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on TrackType.
 * Defines methods for manipulating TrackType data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface TrackTypeDAO {

    /**
     * Creates a new TrackType record in the database.
     *
     * @param trackType the TrackType object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(TrackType trackType) throws SQLException;

    /**
     * Retrieves a TrackType based on its unique identifier.
     *
     * @param id the identifier of the TrackType
     * @return the TrackType found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    TrackType get(int id) throws SQLException;

    /**
     * Retrieves all TrackTypes stored in the database.
     *
     * @return list with all TrackTypes
     * @throws SQLException if an error occurs during the operation
     */
    List<TrackType> getAll() throws SQLException;

    /**
     * Updates the data of an existing TrackType in the database.
     *
     * @param trackType the TrackType object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(TrackType trackType) throws SQLException;

    /**
     * Removes a TrackType from the database based on its unique identifier.
     *
     * @param id the identifier of the TrackType to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves a TrackType by name.
     *
     * @param name the name of the TrackType
     * @return the TrackType found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    TrackType getByName(String name) throws SQLException;

    /**
     * Checks if a TrackType exists with the specified ID.
     *
     * @param id the identifier of the TrackType
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

