package app.db.dao.line;

import app.model.Line;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on Line.
 * Defines methods for manipulating Line data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface LineDAO {

    /**
     * Creates a new Line record in the database.
     *
     * @param line the Line object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(Line line) throws SQLException;

    /**
     * Retrieves a Line based on its unique identifier.
     *
     * @param id the identifier of the Line
     * @return the Line found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    Line get(int id) throws SQLException;

    /**
     * Retrieves all Lines stored in the database.
     *
     * @return list with all Lines
     * @throws SQLException if an error occurs during the operation
     */
    List<Line> getAll() throws SQLException;

    /**
     * Updates the data of an existing Line in the database.
     *
     * @param line the Line object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(Line line) throws SQLException;

    /**
     * Removes a Line from the database based on its unique identifier.
     *
     * @param id the identifier of the Line to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves Lines by owner (Company).
     *
     * @param ownerId the identifier of the owner (Company)
     * @return list with Lines belonging to the owner
     * @throws SQLException if an error occurs during the operation
     */
    List<Line> getByOwner(int ownerId) throws SQLException;

    /**
     * Checks if a Line exists with the specified ID.
     *
     * @param id the identifier of the Line
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}



