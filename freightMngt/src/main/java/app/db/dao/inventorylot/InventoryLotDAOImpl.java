package app.db.dao.inventorylot;

import app.db.DatabaseConnection;
import app.model.InventoryLot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryLotDAOImpl implements InventoryLotDAO {
    private final Connection connection;

    public InventoryLotDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(InventoryLot lot) throws SQLException {
        String sql = "INSERT INTO INVENTORY_LOT (LOT_ID, SKU, BOX_CODE, EXP_DATE, RECEIVED_DATE) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (lot.getLotId() == null) {
                lot.setLotId(getNextId());
            }
            pstmt.setInt(1, lot.getLotId());
            pstmt.setString(2, lot.getSku());
            pstmt.setString(3, lot.getBoxCode());
            pstmt.setDate(4, lot.getExpDate() != null ? new java.sql.Date(lot.getExpDate().getTime()) : null);
            pstmt.setDate(5, lot.getReceivedDate() != null ? new java.sql.Date(lot.getReceivedDate().getTime()) : null);
            pstmt.executeUpdate();
        }
    }

    @Override
    public InventoryLot get(int id) throws SQLException {
        return null;
    }

    @Override
    public List<InventoryLot> getAll() throws SQLException {
        return new ArrayList<>();
    }

    @Override
    public void update(InventoryLot lot) throws SQLException {
    }

    @Override
    public void delete(int id) throws SQLException {
    }

    @Override
    public List<InventoryLot> getBySku(String sku) throws SQLException {
        return new ArrayList<>();
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LOT_ID), 0) + 1 AS NEXT_ID FROM INVENTORY_LOT";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }
}

