package app.db.dao.pickwave;

import app.db.DatabaseConnection;
import app.model.PickWave;
import app.model.Cart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for PickWave management in the Oracle database.
 * Implements CRUD operations for the PickWave entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class PickWaveDAOImpl implements PickWaveDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public PickWaveDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(PickWave pickWave) throws SQLException {
        String sql = "INSERT INTO PICK_WAVE (WAVE_ID, CART_ID, STRATEGY, STATUS, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pickWave.getPickWaveId() == null) {
                pickWave.setPickWaveId(getNextId());
            }

            pstmt.setInt(1, pickWave.getPickWaveId());

            if (pickWave.getCart() != null && pickWave.getCart().getCartId() != null) {
                pstmt.setInt(2, pickWave.getCart().getCartId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, pickWave.getStrategy());
            pstmt.setString(4, pickWave.getStatus());

            pstmt.executeUpdate();
        }
    }

    @Override
    public PickWave get(int id) throws SQLException {
        String sql = "SELECT * FROM PICK_WAVE WHERE WAVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPickWave(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<PickWave> getAll() throws SQLException {
        List<PickWave> pickWaves = new ArrayList<>();
        String sql = "SELECT * FROM PICK_WAVE ORDER BY WAVE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pickWaves.add(mapResultSetToPickWave(rs));
            }
        }
        return pickWaves;
    }

    @Override
    public void update(PickWave pickWave) throws SQLException {
        String sql = "UPDATE PICK_WAVE SET CART_ID = ?, STRATEGY = ?, STATUS = ? WHERE WAVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pickWave.getCart() != null && pickWave.getCart().getCartId() != null) {
                pstmt.setInt(1, pickWave.getCart().getCartId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, pickWave.getStrategy());
            pstmt.setString(3, pickWave.getStatus());
            pstmt.setInt(4, pickWave.getPickWaveId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM PICK_WAVE WHERE WAVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<PickWave> getByStatus(String status) throws SQLException {
        List<PickWave> pickWaves = new ArrayList<>();
        String sql = "SELECT * FROM PICK_WAVE WHERE STATUS = ? ORDER BY WAVE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pickWaves.add(mapResultSetToPickWave(rs));
                }
            }
        }
        return pickWaves;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM PICK_WAVE WHERE WAVE_ID = ?";

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
     * Gets the next available ID for a new PickWave.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAVE_ID), 0) + 1 AS NEXT_ID FROM PICK_WAVE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to a PickWave object.
     *
     * @param rs the ResultSet containing the data
     * @return the PickWave object
     * @throws SQLException if an error occurs during the operation
     */
    private PickWave mapResultSetToPickWave(ResultSet rs) throws SQLException {
        PickWave pickWave = new PickWave();

        pickWave.setPickWaveId(rs.getInt("WAVE_ID"));

        // Set Cart if available
        if (rs.getObject("CART_ID") != null) {
            Cart cart = new Cart();
            cart.setCartId(rs.getInt("CART_ID"));
            pickWave.setCart(cart);
        }

        pickWave.setStrategy(rs.getString("STRATEGY"));
        pickWave.setStatus(rs.getString("STATUS"));

        return pickWave;
    }
}

