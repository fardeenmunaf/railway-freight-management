package app.db.dao.item;

import app.model.Item;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Item.
 * Defines methods for manipulating Item data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface ItemDAO {

    /**
     * Creates a new Item record in the database.
     *
     * @param item the Item object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Item item) throws SQLException;

    /**
     * Retrieves an Item based on its unique identifier.
     *
     * @param id the identifier of the Item
     * @return the Item found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Item get(int id) throws SQLException;

    /**
     * Retrieves all Items stored in the database.
     *
     * @return list with all Items
     * @throws SQLException if an error occurs during the operation
     */
    List<Item> getAll() throws SQLException;

    /**
     * Updates the data of an existing Item in the database.
     *
     * @param item the Item object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Item item) throws SQLException;

    /**
     * Removes an Item from the database based on its unique identifier.
     *
     * @param id the identifier of the Item to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves an Item based on its SKU.
     *
     * @param sku the SKU of the Item
     * @return the Item found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Item getBySku(String sku) throws SQLException;

    /**
     * Retrieves all Items in a specific category.
     *
     * @param categoryId the identifier of the category
     * @return list with all Items in the specified category
     * @throws SQLException if an error occurs during the operation
     */
    List<Item> getByCategory(int categoryId) throws SQLException;

    /**
     * Checks if an Item exists with the specified ID.
     *
     * @param id the identifier of the Item
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

