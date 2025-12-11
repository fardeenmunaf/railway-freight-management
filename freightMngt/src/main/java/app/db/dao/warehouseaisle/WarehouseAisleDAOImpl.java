package app.db.dao.warehouseaisle;
import app.db.DatabaseConnection;
import app.model.WarehouseAisle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseAisleDAOImpl implements WarehouseAisleDAO {
    private final Connection connection;
    public WarehouseAisleDAOImpl() { this.connection = DatabaseConnection.getInstance().getConnection(); }

    @Override
    public void create(WarehouseAisle aisle) throws SQLException {
        String sql = "INSERT INTO WAREHOUSE_AISLE (AISLE_ID, WAREHOUSE_ID, AISLE_NO, UNIQUE_CODE) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (aisle.getAisleId() == null) aisle.setAisleId(getNextId());
            pstmt.setInt(1, aisle.getAisleId());
            pstmt.setInt(2, aisle.getWarehouse() != null ? aisle.getWarehouse().getWarehouseId() : null);
            pstmt.setInt(3, aisle.getAisleNo());
            pstmt.setString(4, aisle.getUniqueCode());
            pstmt.executeUpdate();
        }
    }

    @Override public WarehouseAisle get(int id) throws SQLException { return null; }
    @Override public List<WarehouseAisle> getAll() throws SQLException { return new ArrayList<>(); }
    @Override public void update(WarehouseAisle aisle) throws SQLException { }
    @Override public void delete(int id) throws SQLException { }
    @Override public List<WarehouseAisle> getByWarehouse(int warehouseId) throws SQLException { return new ArrayList<>(); }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(AISLE_ID), 0) + 1 AS NEXT_ID FROM WAREHOUSE_AISLE";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NEXT_ID");
        }
        return 1;
    }
}

