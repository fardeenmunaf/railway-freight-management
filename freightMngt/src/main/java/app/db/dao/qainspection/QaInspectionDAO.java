package app.db.dao.qainspection;

import app.model.QaInspection;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on QaInspection.
 * Defines methods for manipulating QaInspection data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface QaInspectionDAO {

    /**
     * Creates a new QaInspection record in the database.
     *
     * @param inspection the QaInspection object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(QaInspection inspection) throws SQLException;

    /**
     * Retrieves a QaInspection based on its unique identifier.
     *
     * @param id the identifier of the QaInspection
     * @return the QaInspection found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    QaInspection get(int id) throws SQLException;

    /**
     * Retrieves all QaInspections stored in the database.
     *
     * @return list with all QaInspections
     * @throws SQLException if an error occurs during the operation
     */
    List<QaInspection> getAll() throws SQLException;

    /**
     * Updates the data of an existing QaInspection in the database.
     *
     * @param inspection the QaInspection object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(QaInspection inspection) throws SQLException;

    /**
     * Removes a QaInspection from the database based on its unique identifier.
     *
     * @param id the identifier of the QaInspection to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all QaInspections for a specific return line.
     *
     * @param returnLineId the identifier of the return line
     * @return list with QaInspections for the specified return line
     * @throws SQLException if an error occurs during the operation
     */
    List<QaInspection> getByReturnLine(int returnLineId) throws SQLException;

    /**
     * Retrieves all QaInspections with a specific status.
     *
     * @param status the status to filter by
     * @return list with QaInspections matching the status
     * @throws SQLException if an error occurs during the operation
     */
    List<QaInspection> getByStatus(String status) throws SQLException;

    /**
     * Checks if a QaInspection exists with the specified ID.
     *
     * @param id the identifier of the QaInspection
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

