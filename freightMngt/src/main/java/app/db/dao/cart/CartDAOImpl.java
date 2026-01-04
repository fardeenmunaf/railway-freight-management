package app.db.dao.cart;

import app.db.dao.BaseDAOImpl;
import app.model.Cart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Cart management in Oracle database.
 * Implements CRUD operations for the Cart entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CartDAOImpl extends BaseDAOImpl implements CartDAO {

    public CartDAOImpl() {
        super();
    }

    @Override
    public void create(Cart cart) throws SQLException {
        String sql = "INSERT INTO " + getTableName("CART") + " (CART_ID, NAME, BAY_ID) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (cart.getCartId() == null) {
                cart.setCartId(getNextId());
            }
            pstmt.setInt(1, cart.getCartId());
            pstmt.setString(2, cart.getName());
            pstmt.setDouble(3, cart.getMaxWeight());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Cart get(int id) throws SQLException {
        String sql = "SELECT CART_ID, NAME, BAY_ID FROM " + getTableName("CART") + " WHERE CART_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCart(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Cart> getAll() throws SQLException {
        List<Cart> carts = new ArrayList<>();
        String sql = "SELECT CART_ID, NAME, BAY_ID FROM " + getTableName("CART") + " ORDER BY CART_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                carts.add(mapResultSetToCart(rs));
            }
        }

        return carts;
    }

    @Override
    public void update(Cart cart) throws SQLException {
        String sql = "UPDATE " + getTableName("CART") + " SET NAME = ?, BAY_ID = ? WHERE CART_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cart.getName());
            pstmt.setDouble(2, cart.getMaxWeight());
            pstmt.setInt(3, cart.getCartId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("CART") + " WHERE CART_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Cart getByName(String name) throws SQLException {
        String sql = "SELECT CART_ID, NAME, BAY_ID FROM " + getTableName("CART") + " WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCart(rs);
                }
            }
        }

        return null;
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("CART") + " WHERE CART_ID = ?";

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

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(CART_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("CART");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private Cart mapResultSetToCart(ResultSet rs) throws SQLException {
        return new Cart(
                rs.getInt("CART_ID"),
                rs.getString("NOME"),
                rs.getDouble("MAX_WEIGHT")
        );
    }
}


