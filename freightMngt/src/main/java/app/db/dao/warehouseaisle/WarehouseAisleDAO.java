package app.db.dao.warehouseaisle;

import app.model.WarehouseAisle;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on WarehouseAisle.
 * Defines methods for manipulating WarehouseAisle data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface WarehouseAisleDAO {

    /**
     * Creates a new WarehouseAisle record in the database.
     *
     * @param aisle the WarehouseAisle object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(WarehouseAisle aisle) throws SQLException;

    /**
     * Retrieves a WarehouseAisle based on its unique identifier.
     *
     * @param id the identifier of the WarehouseAisle
     * @return the WarehouseAisle found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    WarehouseAisle get(int id) throws SQLException;

    /**
     * Retrieves all WarehouseAisles stored in the database.
     *
     * @return list with all WarehouseAisles
     * @throws SQLException if an error occurs during the operation
     */
    List<WarehouseAisle> getAll() throws SQLException;

    /**
     * Updates the data of an existing WarehouseAisle in the database.
     *
     * @param aisle the WarehouseAisle object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(WarehouseAisle aisle) throws SQLException;

    /**
     * Removes a WarehouseAisle from the database based on its unique identifier.
     *
     * @param id the identifier of the WarehouseAisle to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all WarehouseAisles for a specific warehouse.
     *
     * @param warehouseId the identifier of the warehouse
     * @return list with WarehouseAisles for the specified warehouse
     * @throws SQLException if an error occurs during the operation
     */
    List<WarehouseAisle> getByWarehouse(int warehouseId) throws SQLException;

    /**
     * Checks if a WarehouseAisle exists with the specified ID.
     *
     * @param id the identifier of the WarehouseAisle
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

