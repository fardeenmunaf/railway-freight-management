package app.db.dao.warehouse;

import app.model.Warehouse;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Warehouse.
 * Defines methods for manipulating Warehouse data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface WarehouseDAO {

    /**
     * Creates a new Warehouse record in the database.
     *
     * @param warehouse the Warehouse object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Warehouse warehouse) throws SQLException;

    /**
     * Retrieves a Warehouse based on its unique identifier.
     *
     * @param id the identifier of the Warehouse
     * @return the Warehouse found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Warehouse get(int id) throws SQLException;

    /**
     * Retrieves all Warehouses stored in the database.
     *
     * @return list with all Warehouses
     * @throws SQLException if an error occurs during the operation
     */
    List<Warehouse> getAll() throws SQLException;

    /**
     * Updates the data of an existing Warehouse in the database.
     *
     * @param warehouse the Warehouse object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Warehouse warehouse) throws SQLException;

    /**
     * Removes a Warehouse from the database based on its unique identifier.
     *
     * @param id the identifier of the Warehouse to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves a Warehouse by facility ID.
     *
     * @param facilityId the identifier of the facility
     * @return the Warehouse found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Warehouse getByFacility(int facilityId) throws SQLException;

    /**
     * Retrieves a Warehouse by name.
     *
     * @param name the name of the Warehouse
     * @return the Warehouse found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Warehouse getByName(String name) throws SQLException;

    /**
     * Checks if a Warehouse exists with the specified ID.
     *
     * @param id the identifier of the Warehouse
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

