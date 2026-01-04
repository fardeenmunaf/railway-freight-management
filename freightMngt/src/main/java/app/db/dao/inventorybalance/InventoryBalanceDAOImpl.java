package app.db.dao.inventorybalance;

import app.db.dao.BaseDAOImpl;
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
public class InventoryBalanceDAOImpl extends BaseDAOImpl implements InventoryBalanceDAO {

    public InventoryBalanceDAOImpl() {
        super();
    }

    @Override
    public void create(InventoryBalance balance) throws SQLException {
        String sql = "INSERT INTO " + getTableName("INVENTORY_BALANCE") +
                     " (BALANCE_ID, LOT_ID, BAY_ID, QUANTITY, LAST_UPDATED) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (balance.getBalanceId() == null) {
                balance.setBalanceId(getNextId());
            }

            pstmt.setInt(1, balance.getBalanceId());

            // LOT_ID - use first lot from the list if available
            if (balance.getLots() != null && !balance.getLots().isEmpty() && balance.getLots().get(0).getLotId() != null) {
                pstmt.setInt(2, balance.getLots().get(0).getLotId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            // BAY_ID
            if (balance.getBay() != null && balance.getBay().getBayId() != null) {
                pstmt.setInt(3, balance.getBay().getBayId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, balance.getQty());
            pstmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));

            pstmt.executeUpdate();

            // Insert lots if any exist
            if (balance.getLots() != null && !balance.getLots().isEmpty()) {
                insertBalanceLots(balance.getBalanceId(), balance.getLots());
            }
        }
    }

    @Override
    public InventoryBalance get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("INVENTORY_BALANCE") + " WHERE BALANCE_ID = ?";

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
        String sql = "SELECT * FROM " + getTableName("INVENTORY_BALANCE") + " ORDER BY BALANCE_ID";

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
        String sql = "UPDATE " + getTableName("INVENTORY_BALANCE") +
                     " SET LOT_ID = ?, BAY_ID = ?, QUANTITY = ?, LAST_UPDATED = ? WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // LOT_ID - use first lot from the list if available
            if (balance.getLots() != null && !balance.getLots().isEmpty() && balance.getLots().get(0).getLotId() != null) {
                pstmt.setInt(1, balance.getLots().get(0).getLotId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            // BAY_ID
            if (balance.getBay() != null && balance.getBay().getBayId() != null) {
                pstmt.setInt(2, balance.getBay().getBayId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setInt(3, balance.getQty());
            pstmt.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setInt(5, balance.getBalanceId());

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

        String sql = "DELETE FROM " + getTableName("INVENTORY_BALANCE") + " WHERE BALANCE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<InventoryBalance> getByBay(int bayId) throws SQLException {
        List<InventoryBalance> balances = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_BALANCE") + " WHERE BAY_ID = ? ORDER BY BALANCE_ID";

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
        String sql = "SELECT * FROM " + getTableName("INVENTORY_BALANCE") + " WHERE LOT_ID = ? ORDER BY BAY_ID";

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

    public InventoryBalance getByLotAndBay(int lotId, int bayId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("INVENTORY_BALANCE") + " WHERE LOT_ID = ? AND BAY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);
            pstmt.setInt(2, bayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    InventoryBalance balance = mapResultSetToInventoryBalance(rs);
                    balance.setLots(getLotsForBalance(balance.getBalanceId()));
                    return balance;
                }
            }
        }

        return null;
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("INVENTORY_BALANCE") + " WHERE BALANCE_ID = ?";

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
        String sql = "SELECT NVL(MAX(BALANCE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("INVENTORY_BALANCE");

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

