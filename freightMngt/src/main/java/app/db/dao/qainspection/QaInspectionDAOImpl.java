package app.db.dao.qainspection;

import app.db.DatabaseConnection;
import app.model.QaInspection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for QaInspection management in the Oracle database.
 * Implements CRUD operations for the QaInspection entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class QaInspectionDAOImpl implements QaInspectionDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public QaInspectionDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(QaInspection inspection) throws SQLException {
        String sql = "INSERT INTO QA_INSPECTION (QA_ID, RETURN_LINE_ID, STATUS, INSPECTED_AT) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (inspection.getQaId() == null) {
                inspection.setQaId(getNextId());
            }

            pstmt.setInt(1, inspection.getQaId());

            if (inspection.getReturnLineId() != null) {
                pstmt.setInt(2, inspection.getReturnLineId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, inspection.getStatus());

            if (inspection.getInspectedAt() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(inspection.getInspectedAt()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public QaInspection get(int id) throws SQLException {
        String sql = "SELECT * FROM QA_INSPECTION WHERE QA_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQaInspection(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<QaInspection> getAll() throws SQLException {
        List<QaInspection> inspections = new ArrayList<>();
        String sql = "SELECT * FROM QA_INSPECTION ORDER BY QA_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                inspections.add(mapResultSetToQaInspection(rs));
            }
        }
        return inspections;
    }

    @Override
    public void update(QaInspection inspection) throws SQLException {
        String sql = "UPDATE QA_INSPECTION SET RETURN_LINE_ID = ?, STATUS = ?, INSPECTED_AT = ? " +
                     "WHERE QA_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (inspection.getReturnLineId() != null) {
                pstmt.setInt(1, inspection.getReturnLineId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, inspection.getStatus());

            if (inspection.getInspectedAt() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(inspection.getInspectedAt()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            pstmt.setInt(4, inspection.getQaId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM QA_INSPECTION WHERE QA_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<QaInspection> getByReturnLine(int returnLineId) throws SQLException {
        List<QaInspection> inspections = new ArrayList<>();
        String sql = "SELECT * FROM QA_INSPECTION WHERE RETURN_LINE_ID = ? ORDER BY QA_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, returnLineId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inspections.add(mapResultSetToQaInspection(rs));
                }
            }
        }
        return inspections;
    }

    @Override
    public List<QaInspection> getByStatus(String status) throws SQLException {
        List<QaInspection> inspections = new ArrayList<>();
        String sql = "SELECT * FROM QA_INSPECTION WHERE STATUS = ? ORDER BY QA_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inspections.add(mapResultSetToQaInspection(rs));
                }
            }
        }
        return inspections;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM QA_INSPECTION WHERE QA_ID = ?";

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
     * Gets the next available ID for a new QaInspection.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(QA_ID), 0) + 1 AS NEXT_ID FROM QA_INSPECTION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to a QaInspection object.
     *
     * @param rs the ResultSet containing the data
     * @return the QaInspection object
     * @throws SQLException if an error occurs during the operation
     */
    private QaInspection mapResultSetToQaInspection(ResultSet rs) throws SQLException {
        QaInspection inspection = new QaInspection();

        inspection.setQaId(rs.getInt("QA_ID"));

        if (rs.getObject("RETURN_LINE_ID") != null) {
            inspection.setReturnLineId(rs.getInt("RETURN_LINE_ID"));
        }

        inspection.setStatus(rs.getString("STATUS"));

        if (rs.getObject("INSPECTED_AT") != null) {
            Timestamp timestamp = rs.getTimestamp("INSPECTED_AT");
            inspection.setInspectedAt(timestamp.toLocalDateTime());
        }

        return inspection;
    }
}

