package app.db.dao.locomotive;

import app.model.Locomotive;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Locomotive.
 * Defines methods for manipulating Locomotive data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface LocomotiveDAO {

    /**
     * Creates a new Locomotive record in the database.
     *
     * @param locomotive the Locomotive object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Locomotive locomotive) throws SQLException;

    /**
     * Retrieves a Locomotive based on its unique identifier.
     *
     * @param id the identifier of the Locomotive
     * @return the Locomotive found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Locomotive get(int id) throws SQLException;

    /**
     * Retrieves all Locomotives stored in the database.
     *
     * @return list with all Locomotives
     * @throws SQLException if an error occurs during the operation
     */
    List<Locomotive> getAll() throws SQLException;

    /**
     * Updates the data of an existing Locomotive in the database.
     *
     * @param locomotive the Locomotive object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Locomotive locomotive) throws SQLException;

    /**
     * Removes a Locomotive from the database based on its unique identifier.
     *
     * @param id the identifier of the Locomotive to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all Locomotives operated by a specific operator (User).
     *
     * @param operatorId the identifier of the operator (User)
     * @return list with Locomotives operated by the specified operator
     * @throws SQLException if an error occurs during the operation
     */
    List<Locomotive> getByOperator(int operatorId) throws SQLException;

    /**
     * Retrieves all Locomotives of a specific type.
     *
     * @param locomotiveTypeId the identifier of the LocomotiveType
     * @return list with Locomotives of the specified type
     * @throws SQLException if an error occurs during the operation
     */
    List<Locomotive> getByType(int locomotiveTypeId) throws SQLException;

    /**
     * Checks if a Locomotive exists with the specified ID.
     *
     * @param id the identifier of the Locomotive
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

