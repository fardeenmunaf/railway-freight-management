package app.db.dao.locomotivetype;

import app.db.DatabaseConnection;
import app.model.LocomotiveType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for LocomotiveType management in the Oracle database.
 * Implements CRUD operations for the LocomotiveType entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveTypeDAOImpl implements LocomotiveTypeDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public LocomotiveTypeDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(LocomotiveType locomotiveType) throws SQLException {
        String sql = "INSERT INTO LOCOMOTIVE_TYPE (LOCOMOTIVE_TYPE_ID, NAME) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (locomotiveType.getLocomotiveTypeId() == null) {
                locomotiveType.setLocomotiveTypeId(getNextId());
            }

            pstmt.setInt(1, locomotiveType.getLocomotiveTypeId());
            pstmt.setString(2, locomotiveType.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public LocomotiveType get(int id) throws SQLException {
        String sql = "SELECT LOCOMOTIVE_TYPE_ID, NAME FROM LOCOMOTIVE_TYPE WHERE LOCOMOTIVE_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocomotiveType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<LocomotiveType> getAll() throws SQLException {
        List<LocomotiveType> types = new ArrayList<>();
        String sql = "SELECT LOCOMOTIVE_TYPE_ID, NAME FROM LOCOMOTIVE_TYPE ORDER BY LOCOMOTIVE_TYPE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(mapResultSetToLocomotiveType(rs));
            }
        }

        return types;
    }

    @Override
    public void update(LocomotiveType locomotiveType) throws SQLException {
        String sql = "UPDATE LOCOMOTIVE_TYPE SET NAME = ? WHERE LOCOMOTIVE_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, locomotiveType.getName());
            pstmt.setInt(2, locomotiveType.getLocomotiveTypeId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM LOCOMOTIVE_TYPE WHERE LOCOMOTIVE_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public LocomotiveType getByName(String name) throws SQLException {
        String sql = "SELECT LOCOMOTIVE_TYPE_ID, NAME FROM LOCOMOTIVE_TYPE WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocomotiveType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM LOCOMOTIVE_TYPE WHERE LOCOMOTIVE_TYPE_ID = ?";

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
     * Gets the next available ID for a new LocomotiveType.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LOCOMOTIVE_TYPE_ID), 0) + 1 AS NEXT_ID FROM LOCOMOTIVE_TYPE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a LocomotiveType object.
     *
     * @param rs the ResultSet containing the data
     * @return the LocomotiveType object
     * @throws SQLException if an error occurs during the operation
     */
    private LocomotiveType mapResultSetToLocomotiveType(ResultSet rs) throws SQLException {
        return new LocomotiveType(
                rs.getInt("LOCOMOTIVE_TYPE_ID"),
                rs.getString("NAME")
        );
    }
}

