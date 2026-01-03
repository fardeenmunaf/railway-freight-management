package app.db.dao.orderline;

import app.db.DatabaseConnection;
import app.model.OrderLine;
import app.model.Order;
import app.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for OrderLine management in the Oracle database.
 * Implements CRUD operations for the OrderLine entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class OrderLineDAOImpl implements OrderLineDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
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

            if (orderLine.getOrder() != null && orderLine.getOrder().getOrderId() != null) {
                pstmt.setInt(2, orderLine.getOrder().getOrderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (orderLine.getSku() != null && orderLine.getSku().getSku() != null) {
                pstmt.setString(3, orderLine.getSku().getSku());
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }

            if (orderLine.getQty() != null) {
                pstmt.setDouble(4, orderLine.getQty());
            } else {
                pstmt.setNull(4, Types.DOUBLE);
            }

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
            if (orderLine.getOrder() != null && orderLine.getOrder().getOrderId() != null) {
                pstmt.setInt(1, orderLine.getOrder().getOrderId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (orderLine.getSku() != null && orderLine.getSku().getSku() != null) {
                pstmt.setString(2, orderLine.getSku().getSku());
            } else {
                pstmt.setNull(2, Types.VARCHAR);
            }

            if (orderLine.getQty() != null) {
                pstmt.setDouble(3, orderLine.getQty());
            } else {
                pstmt.setNull(3, Types.DOUBLE);
            }

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

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM ORDER_LINE WHERE ORDER_LINE_ID = ?";

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
     * Gets the next available ID for a new OrderLine.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
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

    /**
     * Maps a ResultSet row to an OrderLine object.
     *
     * @param rs the ResultSet containing the data
     * @return the OrderLine object
     * @throws SQLException if an error occurs during the operation
     */
    private OrderLine mapResultSetToOrderLine(ResultSet rs) throws SQLException {
        OrderLine orderLine = new OrderLine();

        orderLine.setOrderLineId(rs.getInt("ORDER_LINE_ID"));

        // Set Order if available
        if (rs.getObject("ORDER_ID") != null) {
            Order order = new Order();
            order.setOrderId(rs.getInt("ORDER_ID"));
            orderLine.setOrder(order);
        }

        // Set Item/SKU if available
        if (rs.getObject("SKU") != null) {
            Item item = new Item();
            item.setSku(rs.getString("SKU"));
            orderLine.setSku(item);
        }

        if (rs.getObject("QTY") != null) {
            orderLine.setQty(rs.getDouble("QTY"));
        }

        return orderLine;
    }
}

