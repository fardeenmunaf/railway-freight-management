package app.db.dao.inventorylot;

import app.db.dao.BaseDAOImpl;
import app.model.InventoryLot;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for InventoryLot management in Oracle database.
 * Implements CRUD operations for the InventoryLot entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryLotDAOImpl extends BaseDAOImpl implements InventoryLotDAO {

    public InventoryLotDAOImpl() {
        super();
    }

    @Override
    public void create(InventoryLot lot) throws SQLException {
        String sql = "INSERT INTO " + getTableName("INVENTORY_LOT") + " (LOT_ID, SKU, QTY, EXP_DATE, RECEIVED_DATE) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (lot.getLotId() == null) {
                lot.setLotId(getNextId());
            }

            pstmt.setInt(1, lot.getLotId());
            pstmt.setInt(2, lot.getSku());
            pstmt.setInt(3, lot.getQty());
            pstmt.setTimestamp(4, lot.getExpDate() != null ? Timestamp.valueOf(lot.getExpDate()) : null);
            pstmt.setTimestamp(5, lot.getReceivedDate() != null ? Timestamp.valueOf(lot.getReceivedDate()) : null);

            pstmt.executeUpdate();
        }
    }

    @Override
    public InventoryLot get(int id) throws SQLException {
        String sql = "SELECT LOT_ID, SKU, QTY, EXP_DATE, RECEIVED_DATE FROM " + getTableName("INVENTORY_LOT") + " WHERE LOT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInventoryLot(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<InventoryLot> getAll() throws SQLException {
        List<InventoryLot> lots = new ArrayList<>();
        String sql = "SELECT LOT_ID, SKU, QTY, EXP_DATE, RECEIVED_DATE FROM " + getTableName("INVENTORY_LOT") + " ORDER BY LOT_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lots.add(mapResultSetToInventoryLot(rs));
            }
        }

        return lots;
    }

    @Override
    public void update(InventoryLot lot) throws SQLException {
        String sql = "UPDATE " + getTableName("INVENTORY_LOT") + " SET SKU = ?, QTY = ?, EXP_DATE = ?, RECEIVED_DATE = ? WHERE LOT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lot.getSku());
            pstmt.setInt(2, lot.getQty());
            pstmt.setTimestamp(3, lot.getExpDate() != null ? Timestamp.valueOf(lot.getExpDate()) : null);
            pstmt.setTimestamp(4, lot.getReceivedDate() != null ? Timestamp.valueOf(lot.getReceivedDate()) : null);
            pstmt.setInt(5, lot.getLotId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("INVENTORY_LOT") + " WHERE LOT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<InventoryLot> getBySku(String sku) throws SQLException {
        List<InventoryLot> lots = new ArrayList<>();
        String sql = "SELECT LOT_ID, SKU, QTY, EXP_DATE, RECEIVED_DATE FROM " + getTableName("INVENTORY_LOT") + " WHERE SKU = ? ORDER BY RECEIVED_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(sku));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lots.add(mapResultSetToInventoryLot(rs));
                }
            }
        }

        return lots;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LOT_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("INVENTORY_LOT");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private InventoryLot mapResultSetToInventoryLot(ResultSet rs) throws SQLException {
        InventoryLot lot = new InventoryLot();
        lot.setLotId(rs.getInt("LOT_ID"));
        lot.setSku(rs.getInt("SKU"));
        lot.setQty(rs.getInt("QTY"));

        Timestamp expDate = rs.getTimestamp("EXP_DATE");
        if (expDate != null) {
            lot.setExpDate(expDate.toLocalDateTime());
        }

        Timestamp receivedDate = rs.getTimestamp("RECEIVED_DATE");
        if (receivedDate != null) {
            lot.setReceivedDate(receivedDate.toLocalDateTime());
        }

        return lot;
    }
}
