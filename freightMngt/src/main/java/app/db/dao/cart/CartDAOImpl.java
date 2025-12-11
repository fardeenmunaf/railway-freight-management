package app.db.dao.cart;
import app.db.DatabaseConnection;
import app.model.Cart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAOImpl implements CartDAO {
    private final Connection connection;
    public CartDAOImpl() { this.connection = DatabaseConnection.getInstance().getConnection(); }

    @Override
    public void create(Cart cart) throws SQLException {
        String sql = "INSERT INTO CART (CART_ID, NOME, MAX_WEIGHT) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (cart.getCartId() == null) cart.setCartId(getNextId());
            pstmt.setInt(1, cart.getCartId());
            pstmt.setString(2, cart.getNome());
            pstmt.setDouble(3, cart.getMaxWeight());
            pstmt.executeUpdate();
        }
    }

    @Override public Cart get(int id) throws SQLException { return null; }
    @Override public List<Cart> getAll() throws SQLException { return new ArrayList<>(); }
    @Override public void update(Cart cart) throws SQLException { }
    @Override public void delete(int id) throws SQLException { }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(CART_ID), 0) + 1 AS NEXT_ID FROM CART";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NEXT_ID");
        }
        return 1;
    }
}

