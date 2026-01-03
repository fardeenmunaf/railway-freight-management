package app.db.dao.tracktype;

import app.db.DatabaseConnection;
import app.model.TrackType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for TrackType management in the Oracle database.
 * Implements CRUD operations for the TrackType entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrackTypeDAOImpl implements TrackTypeDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public TrackTypeDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(TrackType trackType) throws SQLException {
        String sql = "INSERT INTO TRACK_TYPE (TRACK_TYPE_ID, NAME) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (trackType.getTrackTypeId() == null) {
                trackType.setTrackTypeId(getNextId());
            }

            pstmt.setInt(1, trackType.getTrackTypeId());
            pstmt.setString(2, trackType.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public TrackType get(int id) throws SQLException {
        String sql = "SELECT TRACK_TYPE_ID, NAME FROM TRACK_TYPE WHERE TRACK_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrackType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<TrackType> getAll() throws SQLException {
        List<TrackType> trackTypes = new ArrayList<>();
        String sql = "SELECT TRACK_TYPE_ID, NAME FROM TRACK_TYPE ORDER BY TRACK_TYPE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trackTypes.add(mapResultSetToTrackType(rs));
            }
        }

        return trackTypes;
    }

    @Override
    public void update(TrackType trackType) throws SQLException {
        String sql = "UPDATE TRACK_TYPE SET NAME = ? WHERE TRACK_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trackType.getName());
            pstmt.setInt(2, trackType.getTrackTypeId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM TRACK_TYPE WHERE TRACK_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public TrackType getByName(String name) throws SQLException {
        String sql = "SELECT TRACK_TYPE_ID, NAME FROM TRACK_TYPE WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrackType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM TRACK_TYPE WHERE TRACK_TYPE_ID = ?";

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
     * Gets the next available ID for a new TrackType.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(TRACK_TYPE_ID), 0) + 1 AS NEXT_ID FROM TRACK_TYPE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a TrackType object.
     *
     * @param rs the ResultSet containing the data
     * @return the TrackType object
     * @throws SQLException if an error occurs during the operation
     */
    private TrackType mapResultSetToTrackType(ResultSet rs) throws SQLException {
        return new TrackType(
                rs.getInt("TRACK_TYPE_ID"),
                rs.getString("NAME")
        );
    }
}

