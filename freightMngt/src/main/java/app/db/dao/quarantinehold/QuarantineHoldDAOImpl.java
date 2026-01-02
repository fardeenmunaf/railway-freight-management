package app.db.dao.quarantinehold;

import app.db.DatabaseConnection;
import app.model.QuarantineHold;
import app.model.ReturnOrder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for QuarantineHold management in the Oracle database.
 * Implements CRUD operations for the QuarantineHold entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class QuarantineHoldDAOImpl implements QuarantineHoldDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public QuarantineHoldDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(QuarantineHold hold) throws SQLException {
        String sql = "INSERT INTO QUARANTINE_HOLD (QHOLD_ID, RETURN_ORDER_ID, STATUS, CREATED_AT, RELEASED_AT) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (hold.getQholdId() == null) {
                hold.setQholdId(getNextId());
            }

            pstmt.setInt(1, hold.getQholdId());

            if (hold.getReturnOrder() != null && hold.getReturnOrder().getReturnId() != null) {
                pstmt.setInt(2, hold.getReturnOrder().getReturnId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, hold.getStatus());

            if (hold.getCreatedAt() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(hold.getCreatedAt()));
            } else {
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            if (hold.getReleasedAt() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(hold.getReleasedAt()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public QuarantineHold get(int id) throws SQLException {
        String sql = "SELECT * FROM QUARANTINE_HOLD WHERE QHOLD_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQuarantineHold(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<QuarantineHold> getAll() throws SQLException {
        List<QuarantineHold> holds = new ArrayList<>();
        String sql = "SELECT * FROM QUARANTINE_HOLD ORDER BY QHOLD_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                holds.add(mapResultSetToQuarantineHold(rs));
            }
        }
        return holds;
    }

    @Override
    public void update(QuarantineHold hold) throws SQLException {
        String sql = "UPDATE QUARANTINE_HOLD SET RETURN_ORDER_ID = ?, STATUS = ?, CREATED_AT = ?, " +
                     "RELEASED_AT = ? WHERE QHOLD_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (hold.getReturnOrder() != null && hold.getReturnOrder().getReturnId() != null) {
                pstmt.setInt(1, hold.getReturnOrder().getReturnId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, hold.getStatus());

            if (hold.getCreatedAt() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(hold.getCreatedAt()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            if (hold.getReleasedAt() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(hold.getReleasedAt()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setInt(5, hold.getQholdId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM QUARANTINE_HOLD WHERE QHOLD_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<QuarantineHold> getByReturnOrder(int returnOrderId) throws SQLException {
        List<QuarantineHold> holds = new ArrayList<>();
        String sql = "SELECT * FROM QUARANTINE_HOLD WHERE RETURN_ORDER_ID = ? ORDER BY QHOLD_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, returnOrderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    holds.add(mapResultSetToQuarantineHold(rs));
                }
            }
        }
        return holds;
    }

    @Override
    public List<QuarantineHold> getByStatus(String status) throws SQLException {
        List<QuarantineHold> holds = new ArrayList<>();
        String sql = "SELECT * FROM QUARANTINE_HOLD WHERE STATUS = ? ORDER BY QHOLD_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    holds.add(mapResultSetToQuarantineHold(rs));
                }
            }
        }
        return holds;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM QUARANTINE_HOLD WHERE QHOLD_ID = ?";

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
     * Gets the next available ID for a new QuarantineHold.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(QHOLD_ID), 0) + 1 AS NEXT_ID FROM QUARANTINE_HOLD";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to a QuarantineHold object.
     *
     * @param rs the ResultSet containing the data
     * @return the QuarantineHold object
     * @throws SQLException if an error occurs during the operation
     */
    private QuarantineHold mapResultSetToQuarantineHold(ResultSet rs) throws SQLException {
        QuarantineHold hold = new QuarantineHold();

        hold.setQholdId(rs.getInt("QHOLD_ID"));

        // Set ReturnOrder if available
        if (rs.getObject("RETURN_ORDER_ID") != null) {
            ReturnOrder returnOrder = new ReturnOrder();
            returnOrder.setReturnId(rs.getInt("RETURN_ORDER_ID"));
            hold.setReturnOrder(returnOrder);
        }

        hold.setStatus(rs.getString("STATUS"));

        if (rs.getObject("CREATED_AT") != null) {
            Timestamp timestamp = rs.getTimestamp("CREATED_AT");
            hold.setCreatedAt(timestamp.toLocalDateTime());
        }

        if (rs.getObject("RELEASED_AT") != null) {
            Timestamp timestamp = rs.getTimestamp("RELEASED_AT");
            hold.setReleasedAt(timestamp.toLocalDateTime());
        }

        return hold;
    }
}

