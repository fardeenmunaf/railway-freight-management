package app.db.dao.quarantinehold;

import app.model.QuarantineHold;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on QuarantineHold.
 * Defines methods for manipulating QuarantineHold data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface QuarantineHoldDAO {

    /**
     * Creates a new QuarantineHold record in the database.
     *
     * @param hold the QuarantineHold object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(QuarantineHold hold) throws SQLException;

    /**
     * Retrieves a QuarantineHold based on its unique identifier.
     *
     * @param id the identifier of the QuarantineHold
     * @return the QuarantineHold found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    QuarantineHold get(int id) throws SQLException;

    /**
     * Retrieves all QuarantineHolds stored in the database.
     *
     * @return list with all QuarantineHolds
     * @throws SQLException if an error occurs during the operation
     */
    List<QuarantineHold> getAll() throws SQLException;

    /**
     * Updates the data of an existing QuarantineHold in the database.
     *
     * @param hold the QuarantineHold object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(QuarantineHold hold) throws SQLException;

    /**
     * Removes a QuarantineHold from the database based on its unique identifier.
     *
     * @param id the identifier of the QuarantineHold to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all QuarantineHolds for a specific return order.
     *
     * @param returnOrderId the identifier of the return order
     * @return list with QuarantineHolds for the specified return order
     * @throws SQLException if an error occurs during the operation
     */
    List<QuarantineHold> getByReturnOrder(int returnOrderId) throws SQLException;

    /**
     * Retrieves all QuarantineHolds with a specific status.
     *
     * @param status the status to filter by
     * @return list with QuarantineHolds matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<QuarantineHold> getByStatus(String status) throws SQLException;

    /**
     * Checks if a QuarantineHold exists with the specified ID.
     *
     * @param id the identifier of the QuarantineHold
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

