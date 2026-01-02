package app.db.dao.returnline;

import app.model.ReturnLine;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on ReturnLine.
 * Defines methods for manipulating ReturnLine data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface ReturnLineDAO {

    /**
     * Creates a new ReturnLine record in the database.
     *
     * @param returnLine the ReturnLine object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(ReturnLine returnLine) throws SQLException;

    /**
     * Retrieves a ReturnLine based on its unique identifier.
     *
     * @param id the identifier of the ReturnLine
     * @return the ReturnLine found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    ReturnLine get(int id) throws SQLException;

    /**
     * Retrieves all ReturnLines stored in the database.
     *
     * @return list with all ReturnLines
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnLine> getAll() throws SQLException;

    /**
     * Updates the data of an existing ReturnLine in the database.
     *
     * @param returnLine the ReturnLine object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(ReturnLine returnLine) throws SQLException;

    /**
     * Removes a ReturnLine from the database based on its unique identifier.
     *
     * @param id the identifier of the ReturnLine to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all ReturnLines for a specific return order.
     *
     * @param returnOrderId the identifier of the return order
     * @return list with ReturnLines for the specified return order
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnLine> getByReturnOrder(int returnOrderId) throws SQLException;

    /**
     * Retrieves all ReturnLines for a specific SKU.
     *
     * @param sku the SKU identifier
     * @return list with ReturnLines for the specified SKU
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnLine> getBySku(String sku) throws SQLException;

    /**
     * Retrieves all ReturnLines with a specific condition.
     *
     * @param condition the condition to filter by
     * @return list with ReturnLines matching the condition
     * @throws SQLException if an error occurs during the operation
     */
    List<ReturnLine> getByCondition(String condition) throws SQLException;

    /**
     * Checks if a ReturnLine exists with the specified ID.
     *
     * @param id the identifier of the ReturnLine
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

