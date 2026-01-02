package app.db.dao.servicelocomotive;

import app.model.ServiceLocomotive;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on ServiceLocomotive.
 * Defines methods for manipulating ServiceLocomotive data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface ServiceLocomotiveDAO {

    /**
     * Creates a new ServiceLocomotive record in the database.
     *
     * @param serviceLocomotive the ServiceLocomotive object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(ServiceLocomotive serviceLocomotive) throws SQLException;

    /**
     * Retrieves a ServiceLocomotive based on its unique identifier.
     *
     * @param id the identifier of the ServiceLocomotive
     * @return the ServiceLocomotive found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    ServiceLocomotive get(int id) throws SQLException;

    /**
     * Retrieves all ServiceLocomotives stored in the database.
     *
     * @return list with all ServiceLocomotives
     * @throws SQLException if an error occurs during the operation
     */
    List<ServiceLocomotive> getAll() throws SQLException;

    /**
     * Updates the data of an existing ServiceLocomotive in the database.
     *
     * @param serviceLocomotive the ServiceLocomotive object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(ServiceLocomotive serviceLocomotive) throws SQLException;

    /**
     * Removes a ServiceLocomotive from the database based on its unique identifier.
     *
     * @param id the identifier of the ServiceLocomotive to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all ServiceLocomotives for a specific train service, ordered by position.
     *
     * @param serviceId the identifier of the train service
     * @return list with ServiceLocomotives for the specified service, in position order
     * @throws SQLException if an error occurs during the operation
     */
    List<ServiceLocomotive> getByService(int serviceId) throws SQLException;

    /**
     * Retrieves all service assignments for a specific locomotive.
     *
     * @param locomotiveId the identifier of the locomotive
     * @return list with ServiceLocomotives for the specified locomotive
     * @throws SQLException if an error occurs during the operation
     */
    List<ServiceLocomotive> getByLocomotive(int locomotiveId) throws SQLException;

    /**
     * Deletes all ServiceLocomotives for a specific train service.
     *
     * @param serviceId the identifier of the train service
     * @throws SQLException if an error occurs during the operation
     */
    void deleteByService(int serviceId) throws SQLException;

    /**
     * Checks if a ServiceLocomotive exists with the specified ID.
     *
     * @param id the identifier of the ServiceLocomotive
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

