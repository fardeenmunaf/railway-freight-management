package app.db.dao.itemcategory;

import app.model.ItemCategory;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on ItemCategory.
 * Defines methods for manipulating ItemCategory data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface ItemCategoryDAO {

    /**
     * Creates a new ItemCategory record in the database.
     *
     * @param itemCategory the ItemCategory object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(ItemCategory itemCategory) throws SQLException;

    /**
     * Retrieves an ItemCategory based on its unique identifier.
     *
     * @param id the identifier of the ItemCategory
     * @return the ItemCategory found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    ItemCategory get(int id) throws SQLException;

    /**
     * Retrieves all ItemCategories stored in the database.
     *
     * @return list with all ItemCategories
     * @throws SQLException if an error occurs during the operation
     */
    List<ItemCategory> getAll() throws SQLException;

    /**
     * Updates the data of an existing ItemCategory in the database.
     *
     * @param itemCategory the ItemCategory object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(ItemCategory itemCategory) throws SQLException;

    /**
     * Removes an ItemCategory from the database based on its unique identifier.
     *
     * @param id the identifier of the ItemCategory to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves an ItemCategory by name.
     *
     * @param name the name of the ItemCategory
     * @return the ItemCategory found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    ItemCategory getByName(String name) throws SQLException;

    /**
     * Checks if an ItemCategory exists with the specified ID.
     *
     * @param id the identifier of the ItemCategory
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

