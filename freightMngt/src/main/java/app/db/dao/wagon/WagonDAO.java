package app.db.dao.wagon;

import app.model.Wagon;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Wagon.
 * Defines methods for manipulating Wagon data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface WagonDAO {

    /**
     * Creates a new Wagon record in the database.
     *
     * @param wagon the Wagon object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Wagon wagon) throws SQLException;

    /**
     * Retrieves a Wagon based on its unique identifier.
     *
     * @param id the identifier of the Wagon
     * @return the Wagon found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Wagon get(int id) throws SQLException;

    /**
     * Retrieves all Wagons stored in the database.
     *
     * @return list with all Wagons
     * @throws SQLException if an error occurs during the operation
     */
    List<Wagon> getAll() throws SQLException;

    /**
     * Updates the data of an existing Wagon in the database.
     *
     * @param wagon the Wagon object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Wagon wagon) throws SQLException;

    /**
     * Removes a Wagon from the database based on its unique identifier.
     *
     * @param id the identifier of the Wagon to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all Wagons of a specific model.
     *
     * @param wagonModelId the identifier of the wagon model
     * @return list with Wagons of the specified model
     * @throws SQLException if an error occurs during the operation
     */
    List<Wagon> getByModel(int wagonModelId) throws SQLException;

    /**
     * Retrieves all Wagons operated by a specific operator.
     *
     * @param operatorId the identifier of the operator
     * @return list with Wagons for the specified operator
     * @throws SQLException if an error occurs during the operation
     */
    List<Wagon> getByOperator(int operatorId) throws SQLException;

    /**
     * Retrieves all Wagons that entered service in a specific year.
     *
     * @param serviceYear the service year
     * @return list with Wagons from the specified service year
     * @throws SQLException if an error occurs during the operation
     */
    List<Wagon> getByServiceYear(int serviceYear) throws SQLException;

    /**
     * Checks if a Wagon exists with the specified ID.
     *
     * @param id the identifier of the Wagon
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

