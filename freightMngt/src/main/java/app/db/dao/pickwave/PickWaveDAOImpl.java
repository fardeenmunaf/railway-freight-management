package app.db.dao.pickwave;
import app.db.DatabaseConnection;
import app.model.PickWave;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PickWaveDAOImpl implements PickWaveDAO {
    private final Connection connection;
    public PickWaveDAOImpl() { this.connection = DatabaseConnection.getInstance().getConnection(); }

    @Override
    public void create(PickWave pickWave) throws SQLException {
        String sql = "INSERT INTO PICK_WAVE (WAVE_ID, CART_ID, STRATEGY, STATUS, CREATED_AT) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pickWave.getWaveId() == null) pickWave.setWaveId(getNextId());
            pstmt.setInt(1, pickWave.getWaveId());
            pstmt.setInt(2, pickWave.getCart() != null ? pickWave.getCart().getCartId() : null);
            pstmt.setString(3, pickWave.getStrategy());
            pstmt.setString(4, pickWave.getStatus());
            pstmt.executeUpdate();
        }
    }

    @Override public PickWave get(int id) throws SQLException { return null; }
    @Override public List<PickWave> getAll() throws SQLException { return new ArrayList<>(); }
    @Override public void update(PickWave pickWave) throws SQLException { }
    @Override public void delete(int id) throws SQLException { }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAVE_ID), 0) + 1 AS NEXT_ID FROM PICK_WAVE";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NEXT_ID");
        }
        return 1;
    }
}

