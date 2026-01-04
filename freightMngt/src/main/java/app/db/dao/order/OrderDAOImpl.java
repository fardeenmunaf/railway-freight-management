package app.db.dao.order;

import app.db.dao.BaseDAOImpl;
import app.model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl extends BaseDAOImpl implements OrderDAO {

    public OrderDAOImpl() {
        super();
    }

    @Override
    public void create(Order order) throws SQLException {
        String sql = "INSERT INTO " + getTableName("\"ORDER\"") + " (ORDER_ID, STATUS, PRIORITY, DUE_DATE, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (order.getOrderId() == null) {
                order.setOrderId(getNextId());
            }

            pstmt.setInt(1, order.getOrderId());
            pstmt.setString(2, order.getStatus());
            pstmt.setInt(3, order.getPriority());

            if (order.getDueDate() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(order.getDueDate()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public Order get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("\"ORDER\"") + " WHERE ORDER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Order> getAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("\"ORDER\"") + " ORDER BY ORDER_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    @Override
    public void update(Order order) throws SQLException {
        String sql = "UPDATE " + getTableName("\"ORDER\"") + " SET STATUS = ?, PRIORITY = ?, DUE_DATE = ? WHERE ORDER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, order.getStatus());
            pstmt.setInt(2, order.getPriority());

            if (order.getDueDate() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(order.getDueDate()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            pstmt.setInt(4, order.getOrderId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("\"ORDER\"") + " WHERE ORDER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Order> getByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("\"ORDER\"") + " WHERE STATUS = ? ORDER BY ORDER_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> getByPriority(int priority) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("\"ORDER\"") + " WHERE PRIORITY = ? ORDER BY ORDER_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, priority);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("\"ORDER\"") + " WHERE ORDER_ID = ?";

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
     * Gets the next available ID for a new Order.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(ORDER_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("\"ORDER\"");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to an Order object.
     *
     * @param rs the ResultSet containing the data
     * @return the Order object
     * @throws SQLException if an error occurs during the operation
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        // Simplified - returns basic Order object
        // TODO: Complete mapping with all fields
        return new Order();
    }
}
