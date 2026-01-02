package app.db.dao.inventorybalance;

import app.db.DatabaseConnection;
import app.model.InventoryBalance;
import app.model.InventoryLot;
import app.model.WarehouseBay;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for InventoryBalance management in Oracle database.
 * Implements CRUD operations for the InventoryBalance entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryBalanceDAOImpl implements InventoryBalanceDAO {

    private final Connection connection;

    public InventoryBalanceDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(InventoryBalance balance) throws SQLException {
        String sql = "INSERT INTO INVENTORY_BALANCE (BALANCE_ID, BAY_ID, QTY) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (balance.getBalanceId() == null) {
                balance.setBalanceId(getNextId());
            }

            pstmt.setInt(1, balance.getBalanceId());
            pstmt.setInt(2, balance.getBay() != null ? balance.getBay().getBayId() : null);
            pstmt.setInt(3, balance.getQty());

            pstmt.executeUpdate();

            // Insert lots if any exist
            if (balance.getLots() != null && !balance.getLots().isEmpty()) {
                insertBalanceLots(balance.getBalanceId(), balance.getLots());
            }
        }
    }

    @Override
    public InventoryBalance get(int id) throws SQLException {
        String sql = "SELECT BALANCE_ID, BAY_ID, QTY FROM INVENTORY_BALANCE WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    InventoryBalance balance = mapResultSetToInventoryBalance(rs);
                    balance.setLots(getLotsForBalance(id));
                    return balance;
                }
            }
        }

        return null;
    }

    @Override
    public List<InventoryBalance> getAll() throws SQLException {
        List<InventoryBalance> balances = new ArrayList<>();
        String sql = "SELECT BALANCE_ID, BAY_ID, QTY FROM INVENTORY_BALANCE ORDER BY BALANCE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventoryBalance balance = mapResultSetToInventoryBalance(rs);
                balance.setLots(getLotsForBalance(balance.getBalanceId()));
                balances.add(balance);
            }
        }

        return balances;
    }

    @Override
    public void update(InventoryBalance balance) throws SQLException {
        String sql = "UPDATE INVENTORY_BALANCE SET BAY_ID = ?, QTY = ? WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, balance.getBay() != null ? balance.getBay().getBayId() : null);
            pstmt.setInt(2, balance.getQty());
            pstmt.setInt(3, balance.getBalanceId());

            pstmt.executeUpdate();

            // Update lots relationship
            deleteBalanceLots(balance.getBalanceId());
            if (balance.getLots() != null && !balance.getLots().isEmpty()) {
                insertBalanceLots(balance.getBalanceId(), balance.getLots());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        deleteBalanceLots(id);

        String sql = "DELETE FROM INVENTORY_BALANCE WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<InventoryBalance> getByBay(int bayId) throws SQLException {
        List<InventoryBalance> balances = new ArrayList<>();
        String sql = "SELECT BALANCE_ID, BAY_ID, QTY FROM INVENTORY_BALANCE WHERE BAY_ID = ? ORDER BY BALANCE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InventoryBalance balance = mapResultSetToInventoryBalance(rs);
                    balance.setLots(getLotsForBalance(balance.getBalanceId()));
                    balances.add(balance);
                }
            }
        }

        return balances;
    }

    @Override
    public List<InventoryBalance> getByLot(int lotId) throws SQLException {
        List<InventoryBalance> balances = new ArrayList<>();
        String sql = "SELECT b.BALANCE_ID, b.BAY_ID, b.QTY " +
                     "FROM INVENTORY_BALANCE b " +
                     "JOIN BALANCE_LOT bl ON b.BALANCE_ID = bl.BALANCE_ID " +
                     "WHERE bl.LOT_ID = ? " +
                     "ORDER BY b.BALANCE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InventoryBalance balance = mapResultSetToInventoryBalance(rs);
                    balance.setLots(getLotsForBalance(balance.getBalanceId()));
                    balances.add(balance);
                }
            }
        }

        return balances;
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

    private InventoryBalance mapResultSetToInventoryBalance(ResultSet rs) throws SQLException {
        InventoryBalance balance = new InventoryBalance();
        balance.setBalanceId(rs.getInt("BALANCE_ID"));

        int bayId = rs.getInt("BAY_ID");
        if (!rs.wasNull()) {
            WarehouseBay bay = new WarehouseBay();
            bay.setBayId(bayId);
            balance.setBayId(bay);
        }

        balance.setQty(rs.getInt("QTY"));

        return balance;
    }

    private List<InventoryLot> getLotsForBalance(int balanceId) throws SQLException {
        List<InventoryLot> lots = new ArrayList<>();
        String sql = "SELECT LOT_ID FROM BALANCE_LOT WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, balanceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InventoryLot lot = new InventoryLot();
                    lot.setLotId(rs.getInt("LOT_ID"));
                    lots.add(lot);
                }
            }
        }

        return lots;
    }

    private void insertBalanceLots(int balanceId, List<InventoryLot> lots) throws SQLException {
        String sql = "INSERT INTO BALANCE_LOT (BALANCE_ID, LOT_ID) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (InventoryLot lot : lots) {
                if (lot.getLotId() != null) {
                    pstmt.setInt(1, balanceId);
                    pstmt.setInt(2, lot.getLotId());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }

    private void deleteBalanceLots(int balanceId) throws SQLException {
        String sql = "DELETE FROM BALANCE_LOT WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, balanceId);
            pstmt.executeUpdate();
        }
    }
}

