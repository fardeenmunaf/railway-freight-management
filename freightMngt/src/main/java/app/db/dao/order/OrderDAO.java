package app.db.dao.order;

import app.model.Order;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Order.
 * Defines methods for manipulating Order data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface OrderDAO {

    /**
     * Creates a new Order record in the database.
     *
     * @param order the Order object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Order order) throws SQLException;

    /**
     * Retrieves an Order based on its unique identifier.
     *
     * @param id the identifier of the Order
     * @return the Order found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Order get(int id) throws SQLException;

    /**
     * Retrieves all Orders stored in the database.
     *
     * @return list with all Orders
     * @throws SQLException if an error occurs during the operation
     */
    List<Order> getAll() throws SQLException;

    /**
     * Updates the data of an existing Order in the database.
     *
     * @param order the Order object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Order order) throws SQLException;

    /**
     * Removes an Order from the database based on its unique identifier.
     *
     * @param id the identifier of the Order to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all Orders with a specific status.
     *
     * @param status the status to filter by
     * @return list with Orders matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<Order> getByStatus(String status) throws SQLException;

    /**
     * Retrieves all Orders with a specific priority.
     *
     * @param priority the priority to filter by
     * @return list with Orders matching the priority
     * @throws SQLException if an error occurs during the operation
     */
    List<Order> getByPriority(int priority) throws SQLException;

    /**
     * Checks if an Order exists with the specified ID.
     *
     * @param id the identifier of the Order
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}



