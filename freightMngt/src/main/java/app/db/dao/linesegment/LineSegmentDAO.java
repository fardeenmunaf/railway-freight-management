package app.db.dao.linesegment;

import app.model.LineSegment;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for CRUD operations on LineSegment.
 * Defines methods for manipulating LineSegment data in the database.
 *
 * @author G113 2DK
 * @version 1.0
 */
public interface LineSegmentDAO {

    /**
     * Creates a new LineSegment record in the database.
     *
     * @param segment the LineSegment object to be created
     * @throws SQLException if an error occurs during the operation
     */
    void create(LineSegment segment) throws SQLException;

    /**
     * Retrieves a LineSegment based on its unique identifier.
     *
     * @param id the identifier of the LineSegment
     * @return the LineSegment found or null if it does not exist
     * @throws SQLException if an error occurs during the operation
     */
    LineSegment get(int id) throws SQLException;

    /**
     * Retrieves all LineSegments stored in the database.
     *
     * @return list with all LineSegments
     * @throws SQLException if an error occurs during the operation
     */
    List<LineSegment> getAll() throws SQLException;

    /**
     * Updates the data of an existing LineSegment in the database.
     *
     * @param segment the LineSegment object with updated data
     * @throws SQLException if an error occurs during the operation
     */
    void update(LineSegment segment) throws SQLException;

    /**
     * Removes a LineSegment from the database based on its unique identifier.
     *
     * @param id the identifier of the LineSegment to remove
     * @throws SQLException if an error occurs during the operation
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves all LineSegments that belong to a specific Line.
     *
     * @param lineId the identifier of the Line
     * @return list with all LineSegments for the specified Line
     * @throws SQLException if an error occurs during the operation
     */
    List<LineSegment> getByLine(int lineId) throws SQLException;

    /**
     * Checks if a LineSegment exists with the specified ID.
     *
     * @param id the identifier of the LineSegment
     * @return true if it exists, false otherwise
     * @throws SQLException if an error occurs during the operation
     */
    boolean exists(int id) throws SQLException;
}

