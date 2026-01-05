package app.db.dao.line;

import app.db.dao.BaseDAOImpl;
import app.model.Line;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Line management in the Oracle database.
 * Implements CRUD operations for the Line entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineDAOImpl extends BaseDAOImpl implements LineDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public LineDAOImpl() {
        super();
    }

    @Override
    public void create(Line line) throws SQLException {
        String sql = "INSERT INTO " + getTableName("LINE") + " (LINE_ID, OWNER_ID) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (line.getLineId() == null) {
                line.setLineId(getNextId());
            }

            pstmt.setInt(1, line.getLineId());

            if (line.getOwner() != null && line.getOwner().getUserId() != null) {
                pstmt.setInt(2, line.getOwner().getUserId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public Line get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("LINE") + " WHERE LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLine(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Line> getAll() throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("LINE") + " ORDER BY LINE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lines.add(mapResultSetToLine(rs));
            }
        }

        return lines;
    }

    @Override
    public void update(Line line) throws SQLException {
        String sql = "UPDATE " + getTableName("LINE") + " SET OWNER_ID = ? WHERE LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (line.getOwner() != null && line.getOwner().getUserId() != null) {
                pstmt.setInt(1, line.getOwner().getUserId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setInt(2, line.getLineId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("LINE") + " WHERE LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Line> getByOwner(int ownerId) throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("LINE") + " WHERE OWNER_ID = ? ORDER BY LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lines.add(mapResultSetToLine(rs));
                }
            }
        }

        return lines;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("LINE") + " WHERE LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("COUNT") > 0;
                }
            }
        }

        return false;
    }

    /**
     * Gets the next available ID for a new Line.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LINE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("LINE");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Line object.
     *
     * @param rs the ResultSet containing the data
     * @return the Line object
     * @throws SQLException if an error occurs during the operation
     */
    private Line mapResultSetToLine(ResultSet rs) throws SQLException {
        Line line = new Line();
        line.setLineId(rs.getInt("LINE_ID"));

        // Set Owner if available
        if (rs.getObject("OWNER_ID") != null) {
            User owner = new User();
            owner.setUserId(rs.getInt("OWNER_ID"));
            line.setOwner(owner);
        }

        return line;
    }
}

