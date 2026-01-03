package app.db.dao.gauge;

import app.db.DatabaseConnection;
import app.model.Gauge;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Gauge management in the Oracle database.
 * Implements CRUD operations for the Gauge entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class GaugeDAOImpl implements GaugeDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public GaugeDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Gauge gauge) throws SQLException {
        String sql = "INSERT INTO GAUGE (GAUGE_ID, GAUGE_NAME, GAUGE_SIZE) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (gauge.getGaugeId() == null) {
                gauge.setGaugeId(getNextId());
            }

            pstmt.setInt(1, gauge.getGaugeId());
            pstmt.setString(2, gauge.getGaugeName());
            pstmt.setDouble(3, gauge.getGaugeSize());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Gauge get(int id) throws SQLException {
        String sql = "SELECT GAUGE_ID, GAUGE_NAME, GAUGE_SIZE FROM GAUGE WHERE GAUGE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGauge(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Gauge> getAll() throws SQLException {
        List<Gauge> gauges = new ArrayList<>();
        String sql = "SELECT GAUGE_ID, GAUGE_NAME, GAUGE_SIZE FROM GAUGE ORDER BY GAUGE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                gauges.add(mapResultSetToGauge(rs));
            }
        }

        return gauges;
    }

    @Override
    public void update(Gauge gauge) throws SQLException {
        String sql = "UPDATE GAUGE SET GAUGE_NAME = ?, GAUGE_SIZE = ? WHERE GAUGE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gauge.getGaugeName());
            pstmt.setDouble(2, gauge.getGaugeSize());
            pstmt.setInt(3, gauge.getGaugeId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM GAUGE WHERE GAUGE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Gauge getByName(String name) throws SQLException {
        String sql = "SELECT GAUGE_ID, GAUGE_NAME, GAUGE_SIZE FROM GAUGE WHERE GAUGE_NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGauge(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM GAUGE WHERE GAUGE_ID = ?";

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
     * Gets the next available ID for a new Gauge.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(GAUGE_ID), 0) + 1 AS NEXT_ID FROM GAUGE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Gauge object.
     *
     * @param rs the ResultSet containing the data
     * @return the Gauge object
     * @throws SQLException if an error occurs during the operation
     */
    private Gauge mapResultSetToGauge(ResultSet rs) throws SQLException {
        return new Gauge(
                rs.getInt("GAUGE_ID"),
                rs.getString("GAUGE_NAME"),
                rs.getDouble("GAUGE_SIZE")
        );
    }
}

