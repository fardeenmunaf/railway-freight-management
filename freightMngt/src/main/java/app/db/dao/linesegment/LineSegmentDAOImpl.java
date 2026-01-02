package app.db.dao.linesegment;

import app.db.DatabaseConnection;
import app.model.LineSegment;
import app.model.Line;
import app.model.TrackType;
import app.model.Gauge;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for LineSegment management in the Oracle database.
 * Implements CRUD operations for the LineSegment entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineSegmentDAOImpl implements LineSegmentDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public LineSegmentDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(LineSegment segment) throws SQLException {
        String sql = "INSERT INTO LINE_SEGMENT (SEGMENT_ID, LINE_ID, LENGTH_KM, TRACK_TYPE_ID, " +
                     "ELECTRIF_TYPE, GAUGE_ID, MAX_WEIGHT_KG_M, SPEED_LIMIT_KMH, TRACK_NUMBER, POSITION) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (segment.getSegmentId() == null) {
                segment.setSegmentId(getNextId());
            }

            pstmt.setInt(1, segment.getSegmentId());

            if (segment.getLine() != null && segment.getLine().getLineId() != null) {
                pstmt.setInt(2, segment.getLine().getLineId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setDouble(3, segment.getLengthKm() != null ? segment.getLengthKm() : 0.0);

            if (segment.getTrackType() != null && segment.getTrackType().getTrackTypeId() != null) {
                pstmt.setInt(4, segment.getTrackType().getTrackTypeId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, segment.getElectrifType() != null && segment.getElectrifType() ? 1 : 0);

            if (segment.getGauge() != null && segment.getGauge().getGaugeId() != null) {
                pstmt.setInt(6, segment.getGauge().getGaugeId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            pstmt.setDouble(7, segment.getMaxWeightKgM() != null ? segment.getMaxWeightKgM() : 0.0);
            pstmt.setDouble(8, segment.getSpeedLimitKmh() != null ? segment.getSpeedLimitKmh() : 0.0);
            pstmt.setInt(9, segment.getTrackNumber() != null ? segment.getTrackNumber() : 1);
            pstmt.setInt(10, segment.getPosition() != null ? segment.getPosition() : 0);

            pstmt.executeUpdate();
        }
    }

    @Override
    public LineSegment get(int id) throws SQLException {
        String sql = "SELECT ls.SEGMENT_ID, ls.LINE_ID, ls.LENGTH_KM, ls.TRACK_TYPE_ID, " +
                     "ls.ELECTRIF_TYPE, ls.GAUGE_ID, ls.MAX_WEIGHT_KG_M, ls.SPEED_LIMIT_KMH, " +
                     "ls.TRACK_NUMBER, ls.POSITION, " +
                     "l.NAME AS LINE_NAME, " +
                     "tt.NAME AS TRACK_TYPE_NAME, " +
                     "g.GAUGE_NAME, g.GAUGE_SIZE " +
                     "FROM LINE_SEGMENT ls " +
                     "LEFT JOIN LINE l ON ls.LINE_ID = l.LINE_ID " +
                     "LEFT JOIN TRACK_TYPE tt ON ls.TRACK_TYPE_ID = tt.TRACK_TYPE_ID " +
                     "LEFT JOIN GAUGE g ON ls.GAUGE_ID = g.GAUGE_ID " +
                     "WHERE ls.SEGMENT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLineSegment(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<LineSegment> getAll() throws SQLException {
        List<LineSegment> segments = new ArrayList<>();
        String sql = "SELECT ls.SEGMENT_ID, ls.LINE_ID, ls.LENGTH_KM, ls.TRACK_TYPE_ID, " +
                     "ls.ELECTRIF_TYPE, ls.GAUGE_ID, ls.MAX_WEIGHT_KG_M, ls.SPEED_LIMIT_KMH, " +
                     "ls.TRACK_NUMBER, ls.POSITION, " +
                     "l.NAME AS LINE_NAME, " +
                     "tt.NAME AS TRACK_TYPE_NAME, " +
                     "g.GAUGE_NAME, g.GAUGE_SIZE " +
                     "FROM LINE_SEGMENT ls " +
                     "LEFT JOIN LINE l ON ls.LINE_ID = l.LINE_ID " +
                     "LEFT JOIN TRACK_TYPE tt ON ls.TRACK_TYPE_ID = tt.TRACK_TYPE_ID " +
                     "LEFT JOIN GAUGE g ON ls.GAUGE_ID = g.GAUGE_ID " +
                     "ORDER BY ls.LINE_ID, ls.POSITION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                segments.add(mapResultSetToLineSegment(rs));
            }
        }

        return segments;
    }

    @Override
    public void update(LineSegment segment) throws SQLException {
        String sql = "UPDATE LINE_SEGMENT SET LINE_ID = ?, LENGTH_KM = ?, TRACK_TYPE_ID = ?, " +
                     "ELECTRIF_TYPE = ?, GAUGE_ID = ?, MAX_WEIGHT_KG_M = ?, SPEED_LIMIT_KMH = ?, " +
                     "TRACK_NUMBER = ?, POSITION = ? WHERE SEGMENT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (segment.getLine() != null && segment.getLine().getLineId() != null) {
                pstmt.setInt(1, segment.getLine().getLineId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setDouble(2, segment.getLengthKm() != null ? segment.getLengthKm() : 0.0);

            if (segment.getTrackType() != null && segment.getTrackType().getTrackTypeId() != null) {
                pstmt.setInt(3, segment.getTrackType().getTrackTypeId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, segment.getElectrifType() != null && segment.getElectrifType() ? 1 : 0);

            if (segment.getGauge() != null && segment.getGauge().getGaugeId() != null) {
                pstmt.setInt(5, segment.getGauge().getGaugeId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setDouble(6, segment.getMaxWeightKgM() != null ? segment.getMaxWeightKgM() : 0.0);
            pstmt.setDouble(7, segment.getSpeedLimitKmh() != null ? segment.getSpeedLimitKmh() : 0.0);
            pstmt.setInt(8, segment.getTrackNumber() != null ? segment.getTrackNumber() : 1);
            pstmt.setInt(9, segment.getPosition() != null ? segment.getPosition() : 0);
            pstmt.setInt(10, segment.getSegmentId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM LINE_SEGMENT WHERE SEGMENT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<LineSegment> getByLine(int lineId) throws SQLException {
        List<LineSegment> segments = new ArrayList<>();
        String sql = "SELECT ls.SEGMENT_ID, ls.LINE_ID, ls.LENGTH_KM, ls.TRACK_TYPE_ID, " +
                     "ls.ELECTRIF_TYPE, ls.GAUGE_ID, ls.MAX_WEIGHT_KG_M, ls.SPEED_LIMIT_KMH, " +
                     "ls.TRACK_NUMBER, ls.POSITION, " +
                     "l.NAME AS LINE_NAME, " +
                     "tt.NAME AS TRACK_TYPE_NAME, " +
                     "g.GAUGE_NAME, g.GAUGE_SIZE " +
                     "FROM LINE_SEGMENT ls " +
                     "LEFT JOIN LINE l ON ls.LINE_ID = l.LINE_ID " +
                     "LEFT JOIN TRACK_TYPE tt ON ls.TRACK_TYPE_ID = tt.TRACK_TYPE_ID " +
                     "LEFT JOIN GAUGE g ON ls.GAUGE_ID = g.GAUGE_ID " +
                     "WHERE ls.LINE_ID = ? " +
                     "ORDER BY ls.POSITION";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lineId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    segments.add(mapResultSetToLineSegment(rs));
                }
            }
        }

        return segments;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM LINE_SEGMENT WHERE SEGMENT_ID = ?";

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
     * Gets the next available ID for a new LineSegment.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(SEGMENT_ID), 0) + 1 AS NEXT_ID FROM LINE_SEGMENT";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a LineSegment object.
     *
     * @param rs the ResultSet containing the data
     * @return the LineSegment object
     * @throws SQLException if an error occurs during the operation
     */
    private LineSegment mapResultSetToLineSegment(ResultSet rs) throws SQLException {
        LineSegment segment = new LineSegment();

        segment.setSegmentId(rs.getInt("SEGMENT_ID"));
        segment.setLengthKm(rs.getDouble("LENGTH_KM"));
        segment.setElectrifType(rs.getInt("ELECTRIF_TYPE") == 1);
        segment.setMaxWeightKgM(rs.getDouble("MAX_WEIGHT_KG_M"));
        segment.setSpeedLimitKmh(rs.getDouble("SPEED_LIMIT_KMH"));
        segment.setTrackNumber(rs.getInt("TRACK_NUMBER"));
        segment.setPosition(rs.getInt("POSITION"));

        // Set Line if available
        if (rs.getObject("LINE_ID") != null) {
            Line line = new Line();
            line.setLineId(rs.getInt("LINE_ID"));
            // Note: Line model doesn't have name field, only ID and owner
            segment.setLine(line);
        }

        // Set TrackType if available
        if (rs.getObject("TRACK_TYPE_ID") != null) {
            TrackType trackType = new TrackType();
            trackType.setTrackTypeId(rs.getInt("TRACK_TYPE_ID"));
            trackType.setName(rs.getString("TRACK_TYPE_NAME"));
            segment.setTrackType(trackType);
        }

        // Set Gauge if available
        if (rs.getObject("GAUGE_ID") != null) {
            Gauge gauge = new Gauge();
            gauge.setGaugeId(rs.getInt("GAUGE_ID"));
            gauge.setGaugeName(rs.getString("GAUGE_NAME"));
            gauge.setGaugeSize(rs.getDouble("GAUGE_SIZE"));
            segment.setGauge(gauge);
        }

        return segment;
    }
}

