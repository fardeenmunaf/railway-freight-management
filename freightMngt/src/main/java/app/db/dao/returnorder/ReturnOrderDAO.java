package app.db.dao.returnorder;

import app.model.ReturnOrder;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on ReturnOrder.
 * Defines methods for manipulating ReturnOrder data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface ReturnOrderDAO {

    /**
     * Creates a new ReturnOrder record in the database.
     *
     * @param returnOrder the ReturnOrder object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(ReturnOrder returnOrder) throws SQLException;

    /**
     * Retrieves a ReturnOrder based on its unique identifier.
     *
     * @param id the identifier of the ReturnOrder
     * @return the ReturnOrder found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    ReturnOrder get(int id) throws SQLException;

    /**
     * Retrieves all ReturnOrders stored in the database.
     *
     * @return list with all ReturnOrders
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnOrder> getAll() throws SQLException;

    /**
     * Updates the data of an existing ReturnOrder in the database.
     *
     * @param returnOrder the ReturnOrder object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(ReturnOrder returnOrder) throws SQLException;

    /**
     * Removes a ReturnOrder from the database based on its unique identifier.
     *
     * @param id the identifier of the ReturnOrder to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all ReturnOrders for a specific original order.
     *
     * @param originalOrderId the identifier of the original order
     * @return list with ReturnOrders for the specified order
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnOrder> getByOriginalOrder(int originalOrderId) throws SQLException;

    /**
     * Retrieves all ReturnOrders with a specific status.
     *
     * @param status the status to filter by
     * @return list with ReturnOrders matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnOrder> getByStatus(String status) throws SQLException;

    /**
     * Checks if a ReturnOrder exists with the specified ID.
     *
     * @param id the identifier of the ReturnOrder
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

