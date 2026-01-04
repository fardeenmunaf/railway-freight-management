package app.db.dao.wagonmodel;

import app.model.WagonModel;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on WagonModel.
 * Defines methods for manipulating WagonModel data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface WagonModelDAO {

    /**
     * Creates a new WagonModel record in the database.
     *
     * @param wagonModel the WagonModel object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(WagonModel wagonModel) throws SQLException;

    /**
     * Retrieves a WagonModel based on its unique identifier.
     *
     * @param id the identifier of the WagonModel
     * @return the WagonModel found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    WagonModel get(int id) throws SQLException;

    /**
     * Retrieves all WagonModels stored in the database.
     *
     * @return list with all WagonModels
     * @throws SQLException if an error occurs during the operation
     */
    List<WagonModel> getAll() throws SQLException;

    /**
     * Updates the data of an existing WagonModel in the database.
     *
     * @param wagonModel the WagonModel object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(WagonModel wagonModel) throws SQLException;

    /**
     * Removes a WagonModel from the database based on its unique identifier.
     *
     * @param id the identifier of the WagonModel to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all WagonModels of a specific wagon type.
     *
     * @param wagonTypeId the identifier of the wagon type
     * @return list with WagonModels of the specified type
     * @throws SQLException if an error occurs during the operation
     */
    List<WagonModel> getByType(int wagonTypeId) throws SQLException;

    /**
     * Retrieves a WagonModel by name.
     *
     * @param name the name of the WagonModel
     * @return the WagonModel found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    WagonModel getByName(String name) throws SQLException;

    /**
     * Checks if a WagonModel exists with the specified ID.
     *
     * @param id the identifier of the WagonModel
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}



