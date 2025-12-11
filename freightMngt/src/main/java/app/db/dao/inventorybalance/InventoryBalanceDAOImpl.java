package app.db.dao.inventorybalance;

import app.db.DatabaseConnection;
import app.model.InventoryBalance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryBalanceDAOImpl implements InventoryBalanceDAO {
    private final Connection connection;

    public InventoryBalanceDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(InventoryBalance balance) throws SQLException {
        String sql = "INSERT INTO INVENTORY_BALANCE (BALANCE_ID, BAY_ID, LOT_ID, QTY) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (balance.getBalanceId() == null) {
                balance.setBalanceId(getNextId());
            }
            pstmt.setInt(1, balance.getBalanceId());
            pstmt.setInt(2, balance.getBay() != null ? balance.getBay().getBayId() : null);
            pstmt.setInt(3, balance.getLot() != null ? balance.getLot().getLotId() : null);
            pstmt.setInt(4, balance.getQty());
            pstmt.executeUpdate();
        }
    }

    @Override
    public InventoryBalance get(int id) throws SQLException {
        return null;
    }

    @Override
    public List<InventoryBalance> getAll() throws SQLException {
        return new ArrayList<>();
    }

    @Override
    public void update(InventoryBalance balance) throws SQLException {
    }

    @Override
    public void delete(int id) throws SQLException {
    }

    @Override
    public List<InventoryBalance> getByBay(int bayId) throws SQLException {
        return new ArrayList<>();
    }

    @Override
    public List<InventoryBalance> getByLot(int lotId) throws SQLException {
        return new ArrayList<>();
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(BALANCE_ID), 0) + 1 AS NEXT_ID FROM INVENTORY_BALANCE";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }
}

