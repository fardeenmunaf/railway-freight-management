package app.db.dao.locomotivetype;

import app.model.LocomotiveType;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on LocomotiveType.
 * Defines methods for manipulating LocomotiveType data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface LocomotiveTypeDAO {

    /**
     * Creates a new LocomotiveType record in the database.
     *
     * @param locomotiveType the LocomotiveType object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(LocomotiveType locomotiveType) throws SQLException;

    /**
     * Retrieves a LocomotiveType based on its unique identifier.
     *
     * @param id the identifier of the LocomotiveType
     * @return the LocomotiveType found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    LocomotiveType get(int id) throws SQLException;

    /**
     * Retrieves all LocomotiveTypes stored in the database.
     *
     * @return list with all LocomotiveTypes
     * @throws SQLException if an error occurs during the operation
     */
    List<LocomotiveType> getAll() throws SQLException;

    /**
     * Updates the data of an existing LocomotiveType in the database.
     *
     * @param locomotiveType the LocomotiveType object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(LocomotiveType locomotiveType) throws SQLException;

    /**
     * Removes a LocomotiveType from the database based on its unique identifier.
     *
     * @param id the identifier of the LocomotiveType to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves a LocomotiveType by name.
     *
     * @param name the name of the LocomotiveType
     * @return the LocomotiveType found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    LocomotiveType getByName(String name) throws SQLException;

    /**
     * Checks if a LocomotiveType exists with the specified ID.
     *
     * @param id the identifier of the LocomotiveType
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}



