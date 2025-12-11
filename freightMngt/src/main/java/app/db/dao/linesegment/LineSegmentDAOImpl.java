package app.db.dao.linesegment;
import app.db.DatabaseConnection;
import app.model.LineSegment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LineSegmentDAOImpl implements LineSegmentDAO {
    private final Connection connection;
    public LineSegmentDAOImpl() { this.connection = DatabaseConnection.getInstance().getConnection(); }

    @Override
    public void create(LineSegment segment) throws SQLException {
        String sql = "INSERT INTO LINE_SEGMENT (SEGMENT_ID, LINE_ID, LENGTH_KM, ELECTRIF_TYPE, GAUGE_ID, MAX_WEIGHT_KG_M, SPEED_LIMIT_KMH, TRACK_NUMBER, POSITION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (segment.getSegmentId() == null) segment.setSegmentId(getNextId());
            pstmt.setInt(1, segment.getSegmentId());
            pstmt.setInt(2, segment.getLine() != null ? segment.getLine().getLineId() : null);
            pstmt.setDouble(3, segment.getLengthKm());
            pstmt.setInt(4, segment.getElectrifType());
            pstmt.setInt(5, segment.getGauge() != null ? segment.getGauge().getGaugeId() : null);
            pstmt.setDouble(6, segment.getMaxWeightKgM());
            pstmt.setDouble(7, segment.getSpeedLimitKmh());
            pstmt.setInt(8, segment.getTrackNumber());
            pstmt.setInt(9, segment.getPosition());
            pstmt.executeUpdate();
        }
    }

    @Override public LineSegment get(int id) throws SQLException { return null; }
    @Override public List<LineSegment> getAll() throws SQLException { return new ArrayList<>(); }
    @Override public void update(LineSegment segment) throws SQLException { }
    @Override public void delete(int id) throws SQLException { }
    @Override public List<LineSegment> getByLine(int lineId) throws SQLException { return new ArrayList<>(); }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(SEGMENT_ID), 0) + 1 AS NEXT_ID FROM LINE_SEGMENT";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NEXT_ID");
        }
        return 1;
    }
}

