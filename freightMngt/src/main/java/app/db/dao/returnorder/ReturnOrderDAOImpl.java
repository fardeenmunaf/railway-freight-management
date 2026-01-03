package app.db.dao.returnorder;

import app.db.DatabaseConnection;
import app.model.ReturnOrder;
import app.model.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for ReturnOrder management in the Oracle database.
 * Implements CRUD operations for the ReturnOrder entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ReturnOrderDAOImpl implements ReturnOrderDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public ReturnOrderDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(ReturnOrder returnOrder) throws SQLException {
        String sql = "INSERT INTO RETURN_ORDER (RETURN_ID, ORIG_ORDER_ID, REASON, STATUS, RECEIVED_AT) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (returnOrder.getReturnId() == null) {
                returnOrder.setReturnId(getNextId());
            }

            pstmt.setInt(1, returnOrder.getReturnId());

            if (returnOrder.getOrigOrder() != null && returnOrder.getOrigOrder().getOrderId() != null) {
                pstmt.setInt(2, returnOrder.getOrigOrder().getOrderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, returnOrder.getReason());
            pstmt.setString(4, returnOrder.getStatus());

            if (returnOrder.getReceivedAt() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(returnOrder.getReceivedAt()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public ReturnOrder get(int id) throws SQLException {
        String sql = "SELECT * FROM RETURN_ORDER WHERE RETURN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReturnOrder(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ReturnOrder> getAll() throws SQLException {
        List<ReturnOrder> returnOrders = new ArrayList<>();
        String sql = "SELECT * FROM RETURN_ORDER ORDER BY RETURN_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                returnOrders.add(mapResultSetToReturnOrder(rs));
            }
        }
        return returnOrders;
    }

    @Override
    public void update(ReturnOrder returnOrder) throws SQLException {
        String sql = "UPDATE RETURN_ORDER SET ORIG_ORDER_ID = ?, REASON = ?, STATUS = ?, " +
                     "RECEIVED_AT = ? WHERE RETURN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (returnOrder.getOrigOrder() != null && returnOrder.getOrigOrder().getOrderId() != null) {
                pstmt.setInt(1, returnOrder.getOrigOrder().getOrderId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, returnOrder.getReason());
            pstmt.setString(3, returnOrder.getStatus());

            if (returnOrder.getReceivedAt() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(returnOrder.getReceivedAt()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setInt(5, returnOrder.getReturnId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM RETURN_ORDER WHERE RETURN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ReturnOrder> getByOriginalOrder(int originalOrderId) throws SQLException {
        List<ReturnOrder> returnOrders = new ArrayList<>();
        String sql = "SELECT * FROM RETURN_ORDER WHERE ORIG_ORDER_ID = ? ORDER BY RETURN_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, originalOrderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    returnOrders.add(mapResultSetToReturnOrder(rs));
                }
            }
        }
        return returnOrders;
    }

    @Override
    public List<ReturnOrder> getByStatus(String status) throws SQLException {
        List<ReturnOrder> returnOrders = new ArrayList<>();
        String sql = "SELECT * FROM RETURN_ORDER WHERE STATUS = ? ORDER BY RETURN_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    returnOrders.add(mapResultSetToReturnOrder(rs));
                }
            }
        }
        return returnOrders;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM RETURN_ORDER WHERE RETURN_ID = ?";

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
     * Gets the next available ID for a new ReturnOrder.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(RETURN_ID), 0) + 1 AS NEXT_ID FROM RETURN_ORDER";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to a ReturnOrder object.
     *
     * @param rs the ResultSet containing the data
     * @return the ReturnOrder object
     * @throws SQLException if an error occurs during the operation
     */
    private ReturnOrder mapResultSetToReturnOrder(ResultSet rs) throws SQLException {
        ReturnOrder returnOrder = new ReturnOrder();

        returnOrder.setReturnId(rs.getInt("RETURN_ID"));

        // Set original Order if available
        if (rs.getObject("ORIG_ORDER_ID") != null) {
            Order order = new Order();
            order.setOrderId(rs.getInt("ORIG_ORDER_ID"));
            returnOrder.setOrigOrder(order);
        }

        returnOrder.setReason(rs.getString("REASON"));
        returnOrder.setStatus(rs.getString("STATUS"));

        if (rs.getObject("RECEIVED_AT") != null) {
            Timestamp timestamp = rs.getTimestamp("RECEIVED_AT");
            returnOrder.setReceivedAt(timestamp.toLocalDateTime());
        }

        return returnOrder;
    }
}

