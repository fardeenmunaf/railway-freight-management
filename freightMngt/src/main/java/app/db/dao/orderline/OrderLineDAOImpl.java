package app.db.dao.orderline;

import app.db.DatabaseConnection;
import app.model.OrderLine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderLineDAOImpl implements OrderLineDAO {

    private final Connection connection;

    public OrderLineDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(OrderLine orderLine) throws SQLException {
        String sql = "INSERT INTO ORDER_LINE (ORDER_LINE_ID, ORDER_ID, SKU, QTY) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (orderLine.getOrderLineId() == null) {
                orderLine.setOrderLineId(getNextId());
            }

            pstmt.setInt(1, orderLine.getOrderLineId());
            pstmt.setInt(2, orderLine.getOrder() != null ? orderLine.getOrder().getOrderId() : null);
            pstmt.setString(3, orderLine.getSku());
            pstmt.setInt(4, orderLine.getQty());

            pstmt.executeUpdate();
        }
    }

    @Override
    public OrderLine get(int id) throws SQLException {
        String sql = "SELECT * FROM ORDER_LINE WHERE ORDER_LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderLine(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<OrderLine> getAll() throws SQLException {
        List<OrderLine> orderLines = new ArrayList<>();
        String sql = "SELECT * FROM ORDER_LINE ORDER BY ORDER_LINE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orderLines.add(mapResultSetToOrderLine(rs));
            }
        }
        return orderLines;
    }

    @Override
    public void update(OrderLine orderLine) throws SQLException {
        String sql = "UPDATE ORDER_LINE SET ORDER_ID = ?, SKU = ?, QTY = ? WHERE ORDER_LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderLine.getOrder() != null ? orderLine.getOrder().getOrderId() : null);
            pstmt.setString(2, orderLine.getSku());
            pstmt.setInt(3, orderLine.getQty());
            pstmt.setInt(4, orderLine.getOrderLineId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ORDER_LINE WHERE ORDER_LINE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<OrderLine> getByOrder(int orderId) throws SQLException {
        List<OrderLine> orderLines = new ArrayList<>();
        String sql = "SELECT * FROM ORDER_LINE WHERE ORDER_ID = ? ORDER BY ORDER_LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderLines.add(mapResultSetToOrderLine(rs));
                }
            }
        }
        return orderLines;
    }

    @Override
    public List<OrderLine> getBySku(String sku) throws SQLException {
        List<OrderLine> orderLines = new ArrayList<>();
        String sql = "SELECT * FROM ORDER_LINE WHERE SKU = ? ORDER BY ORDER_LINE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sku);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderLines.add(mapResultSetToOrderLine(rs));
                }
            }
        }
        return orderLines;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(ORDER_LINE_ID), 0) + 1 AS NEXT_ID FROM ORDER_LINE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    private OrderLine mapResultSetToOrderLine(ResultSet rs) throws SQLException {
        return new OrderLine();
    }
}

