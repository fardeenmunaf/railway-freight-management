package app.db.dao.inventorymove;

import app.db.dao.BaseDAOImpl;
import app.model.InventoryMove;
import app.model.InventoryLot;
import app.model.WarehouseBay;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for InventoryMove management in Oracle database.
 * Implements CRUD operations for the InventoryMove entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryMoveDAOImpl extends BaseDAOImpl implements InventoryMoveDAO {

    public InventoryMoveDAOImpl() {
        super();
    }

    @Override
    public void create(InventoryMove inventoryMove) throws SQLException {
        String sql = "INSERT INTO " + getTableName("INVENTORY_MOVE") +
                     " (MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QUANTITY, MOVE_DATE, USER_ID, REASON) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (inventoryMove.getMoveId() == null) {
                inventoryMove.setMoveId(getNextId());
            }

            pstmt.setInt(1, inventoryMove.getMoveId());
            pstmt.setInt(2, inventoryMove.getLot() != null ? inventoryMove.getLot().getLotId() : null);
            pstmt.setInt(3, inventoryMove.getFromBay() != null ? inventoryMove.getFromBay().getBayId() : null);
            pstmt.setInt(4, inventoryMove.getToBay() != null ? inventoryMove.getToBay().getBayId() : null);
            pstmt.setInt(5, inventoryMove.getQty());
            pstmt.setString(6, inventoryMove.getReason());
            pstmt.setTimestamp(7, inventoryMove.getCreatedAt() != null ? Timestamp.valueOf(inventoryMove.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));

            pstmt.executeUpdate();
        }
    }

    @Override
    public InventoryMove get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") + " WHERE MOVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInventoryMove(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<InventoryMove> getAll() throws SQLException {
        List<InventoryMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") + " ORDER BY MOVE_DATE DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                moves.add(mapResultSetToInventoryMove(rs));
            }
        }

        return moves;
    }

    @Override
    public void update(InventoryMove inventoryMove) throws SQLException {
        String sql = "UPDATE " + getTableName("INVENTORY_MOVE") +
                     " SET LOT_ID = ?, FROM_BAY_ID = ?, TO_BAY_ID = ?, QUANTITY = ?, " +
                     "MOVE_DATE = ?, USER_ID = ?, REASON = ? WHERE MOVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (inventoryMove.getLot() != null && inventoryMove.getLot().getLotId() != null) {
                pstmt.setInt(1, inventoryMove.getLot().getLotId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (inventoryMove.getFromBay() != null && inventoryMove.getFromBay().getBayId() != null) {
                pstmt.setInt(2, inventoryMove.getFromBay().getBayId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (inventoryMove.getToBay() != null && inventoryMove.getToBay().getBayId() != null) {
                pstmt.setInt(3, inventoryMove.getToBay().getBayId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, inventoryMove.getQty());
            pstmt.setTimestamp(5, inventoryMove.getCreatedAt() != null ? Timestamp.valueOf(inventoryMove.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(6, 1); // USER_ID placeholder
            pstmt.setString(7, inventoryMove.getReason());
            pstmt.setInt(8, inventoryMove.getMoveId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("INVENTORY_MOVE") + " WHERE MOVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<InventoryMove> getByLot(int lotId) throws SQLException {
        List<InventoryMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") + " WHERE LOT_ID = ? ORDER BY MOVE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return moves;
    }

    @Override
    public List<InventoryMove> getByFromBay(int bayId) throws SQLException {
        List<InventoryMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") +
                     " WHERE FROM_BAY_ID = ? ORDER BY MOVE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return moves;
    }

    @Override
    public List<InventoryMove> getByToBay(int bayId) throws SQLException {
        List<InventoryMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") +
                     " WHERE TO_BAY_ID = ? ORDER BY MOVE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return moves;
    }

    public List<InventoryMove> getByBay(int bayId) throws SQLException {
        List<InventoryMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") +
                     " WHERE FROM_BAY_ID = ? OR TO_BAY_ID = ? ORDER BY MOVE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bayId);
            pstmt.setInt(2, bayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return moves;
    }

    public List<InventoryMove> getByUser(int userId) throws SQLException {
        List<InventoryMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("INVENTORY_MOVE") + " WHERE USER_ID = ? ORDER BY MOVE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return moves;
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("INVENTORY_MOVE") + " WHERE MOVE_ID = ?";

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
        String sql = "SELECT NVL(MAX(MOVE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("INVENTORY_MOVE");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private InventoryMove mapResultSetToInventoryMove(ResultSet rs) throws SQLException {
        InventoryMove inventoryMove = new InventoryMove();
        inventoryMove.setMoveId(rs.getInt("MOVE_ID"));

        // Create InventoryLot with just the ID
        int lotId = rs.getInt("LOT_ID");
        if (!rs.wasNull()) {
            InventoryLot lot = new InventoryLot();
            lot.setLotId(lotId);
            inventoryMove.setLot(lot);
        }

        // Create WarehouseBay objects with just the IDs
        int fromBayId = rs.getInt("FROM_BAY_ID");
        if (!rs.wasNull()) {
            WarehouseBay fromBay = new WarehouseBay();
            fromBay.setBayId(fromBayId);
            inventoryMove.setFromBayId(fromBay);
        }

        int toBayId = rs.getInt("TO_BAY_ID");
        if (!rs.wasNull()) {
            WarehouseBay toBay = new WarehouseBay();
            toBay.setBayId(toBayId);
            inventoryMove.setToBay(toBay);
        }

        inventoryMove.setQty(rs.getInt("QTY"));
        inventoryMove.setReason(rs.getString("REASON"));

        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            inventoryMove.setCreatedAt(createdAt.toLocalDateTime());
        }

        return inventoryMove;
    }
}

