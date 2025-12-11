package app.db.dao.order;

import app.db.DatabaseConnection;
import app.model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    private final Connection connection;

    public OrderDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Order order) throws SQLException {
        String sql = "INSERT INTO \"ORDER\" (ORDER_ID, STATUS, PRIORITY, DUE_DATE, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (order.getOrderId() == null) {
                order.setOrderId(getNextId());
            }

            pstmt.setInt(1, order.getOrderId());
            pstmt.setString(2, order.getStatus());
            pstmt.setInt(3, order.getPriority());
            pstmt.setDate(4, order.getDueDate() != null ? new java.sql.Date(order.getDueDate().getTime()) : null);

            pstmt.executeUpdate();
        }
    }

    @Override
    public Order get(int id) throws SQLException {
        String sql = "SELECT * FROM \"ORDER\" WHERE ORDER_ID = ?";

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
        String sql = "SELECT * FROM \"ORDER\" ORDER BY ORDER_ID";

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
        String sql = "UPDATE \"ORDER\" SET STATUS = ?, PRIORITY = ?, DUE_DATE = ? WHERE ORDER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, order.getStatus());
            pstmt.setInt(2, order.getPriority());
            pstmt.setDate(3, order.getDueDate() != null ? new java.sql.Date(order.getDueDate().getTime()) : null);
            pstmt.setInt(4, order.getOrderId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM \"ORDER\" WHERE ORDER_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Order> getByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM \"ORDER\" WHERE STATUS = ? ORDER BY ORDER_ID";

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
        String sql = "SELECT * FROM \"ORDER\" WHERE PRIORITY = ? ORDER BY ORDER_ID";

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

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(ORDER_ID), 0) + 1 AS NEXT_ID FROM \"ORDER\"";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        return new Order();
    }
}

