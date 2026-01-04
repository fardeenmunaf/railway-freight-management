package app.db.dao.freight;

import app.model.Freight;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Freight.
 * Defines methods for manipulating Freight data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface FreightDAO {

    /**
     * Creates a new Freight record in the database.
     *
     * @param freight the Freight object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Freight freight) throws SQLException;

    /**
     * Retrieves a Freight based on its unique identifier.
     *
     * @param id the identifier of the Freight
     * @return the Freight found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Freight get(int id) throws SQLException;

    /**
     * Retrieves all Freights stored in the database.
     *
     * @return list with all Freights
     * @throws SQLException if an error occurs during the operation
     */
    List<Freight> getAll() throws SQLException;

    /**
     * Updates the data of an existing Freight in the database.
     *
     * @param freight the Freight object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Freight freight) throws SQLException;

    /**
     * Removes a Freight from the database based on its unique identifier.
     *
     * @param id the identifier of the Freight to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all Freights with a specific status.
     *
     * @param status the status to filter by (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)
     * @return list with Freights matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<Freight> getByStatus(String status) throws SQLException;

    /**
     * Retrieves all pending Freights (not yet assigned to a route).
     *
     * @return list with pending Freights
     * @throws SQLException if an error occurs during the operation
     */
    List<Freight> getPendingFreights() throws SQLException;

    /**
     * Retrieves all Freights from a specific origin facility.
     *
     * @param originFacilityId the identifier of the origin facility
     * @return list with Freights from the specified origin
     * @throws SQLException if an error occurs during the operation
     */
    List<Freight> getByOrigin(int originFacilityId) throws SQLException;

    /**
     * Retrieves all Freights with a specific destination facility.
     *
     * @param destinationFacilityId the identifier of the destination facility
     * @return list with Freights to the specified destination
     * @throws SQLException if an error occurs during the operation
     */
    List<Freight> getByDestination(int destinationFacilityId) throws SQLException;

    /**
     * Retrieves all Freights assigned to a specific route.
     *
     * @param routeId the identifier of the route
     * @return list with Freights assigned to the specified route
     * @throws SQLException if an error occurs during the operation
     */
    List<Freight> getByRoute(int routeId) throws SQLException;

    /**
     * Checks if a Freight exists with the specified ID.
     *
     * @param id the identifier of the Freight
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

