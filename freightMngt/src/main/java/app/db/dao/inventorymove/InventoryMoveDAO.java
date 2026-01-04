package app.db.dao.inventorymove;

import app.model.InventoryMove;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on InventoryMove entity.
 * Defines methods for data manipulation of InventoryMove in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface InventoryMoveDAO {

    /**
     * Creates a new InventoryMove record in the database.
     *
     * @param inventoryMove the InventoryMove object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(InventoryMove inventoryMove) throws SQLException;

    /**
     * Retrieves an InventoryMove based on its unique identifier.
     *
     * @param id the identifier of the InventoryMove
     * @return the found InventoryMove or null if it doesn't exist
     * @throws SQLException if an error occurs during the operation
     */
    InventoryMove get(int id) throws SQLException;

    /**
     * Retrieves all InventoryMoves stored in the database.
     *
     * @return list with all InventoryMoves
     * @throws SQLException if an error occurs during the operation
     */
    List<InventoryMove> getAll() throws SQLException;

    /**
     * Updates the data of an existing InventoryMove in the database.
     *
     * @param inventoryMove the InventoryMove object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(InventoryMove inventoryMove) throws SQLException;

    /**
     * Removes an InventoryMove from the database based on its unique identifier.
     *
     * @param id the identifier of the InventoryMove to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all InventoryMoves for a specific lot.
     *
     * @param lotId the identifier of the lot
     * @return list with all InventoryMoves for the lot
     * @throws SQLException if an error occurs during the operation
     */
    List<InventoryMove> getByLot(int lotId) throws SQLException;

    /**
     * Retrieves all InventoryMoves from a specific bay.
     *
     * @param fromBayId the identifier of the source bay
     * @return list with all InventoryMoves from the bay
     * @throws SQLException if an error occurs during the operation
     */
    List<InventoryMove> getByFromBay(int fromBayId) throws SQLException;

    /**
     * Retrieves all InventoryMoves to a specific bay.
     *
     * @param toBayId the identifier of the destination bay
     * @return list with all InventoryMoves to the bay
     * @throws SQLException if an error occurs during the operation
     */
    List<InventoryMove> getByToBay(int toBayId) throws SQLException;
}

