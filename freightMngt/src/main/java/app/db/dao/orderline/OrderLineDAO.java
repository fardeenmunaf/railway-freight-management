package app.db.dao.orderline;

import app.model.OrderLine;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on OrderLine.
 * Defines methods for manipulating OrderLine data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface OrderLineDAO {

    /**
     * Creates a new OrderLine record in the database.
     *
     * @param orderLine the OrderLine object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(OrderLine orderLine) throws SQLException;

    /**
     * Retrieves an OrderLine based on its unique identifier.
     *
     * @param id the identifier of the OrderLine
     * @return the OrderLine found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    OrderLine get(int id) throws SQLException;

    /**
     * Retrieves all OrderLines stored in the database.
     *
     * @return list with all OrderLines
     * @throws SQLException if an error occurs during the operation
     */
    List<OrderLine> getAll() throws SQLException;

    /**
     * Updates the data of an existing OrderLine in the database.
     *
     * @param orderLine the OrderLine object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(OrderLine orderLine) throws SQLException;

    /**
     * Removes an OrderLine from the database based on its unique identifier.
     *
     * @param id the identifier of the OrderLine to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all OrderLines that belong to a specific Order.
     *
     * @param orderId the identifier of the Order
     * @return list with OrderLines for the specified Order
     * @throws SQLException if an error occurs during the operation
     */
    List<OrderLine> getByOrder(int orderId) throws SQLException;

    /**
     * Retrieves all OrderLines for a specific SKU (Item).
     *
     * @param sku the SKU identifier
     * @return list with OrderLines for the specified SKU
     * @throws SQLException if an error occurs during the operation
     */
    List<OrderLine> getBySku(String sku) throws SQLException;

    /**
     * Checks if an OrderLine exists with the specified ID.
     *
     * @param id the identifier of the OrderLine
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}



