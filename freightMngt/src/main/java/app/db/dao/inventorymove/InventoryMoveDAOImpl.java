package app.db.dao.inventorymove;

import app.db.DatabaseConnection;
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
public class InventoryMoveDAOImpl implements InventoryMoveDAO {

    private final Connection connection;

    public InventoryMoveDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(InventoryMove inventoryMove) throws SQLException {
        String sql = "INSERT INTO INVENTORY_MOVE (MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QTY, REASON, CREATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
        String sql = "SELECT MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QTY, REASON, CREATED_AT FROM INVENTORY_MOVE WHERE MOVE_ID = ?";

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
        List<InventoryMove> inventoryMoves = new ArrayList<>();
        String sql = "SELECT MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QTY, REASON, CREATED_AT FROM INVENTORY_MOVE ORDER BY MOVE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inventoryMoves.add(mapResultSetToInventoryMove(rs));
            }
        }

        return inventoryMoves;
    }

    @Override
    public void update(InventoryMove inventoryMove) throws SQLException {
        String sql = "UPDATE INVENTORY_MOVE SET LOT_ID = ?, FROM_BAY_ID = ?, TO_BAY_ID = ?, QTY = ?, REASON = ? WHERE MOVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inventoryMove.getLot() != null ? inventoryMove.getLot().getLotId() : null);
            pstmt.setInt(2, inventoryMove.getFromBay() != null ? inventoryMove.getFromBay().getBayId() : null);
            pstmt.setInt(3, inventoryMove.getToBay() != null ? inventoryMove.getToBay().getBayId() : null);
            pstmt.setInt(4, inventoryMove.getQty());
            pstmt.setString(5, inventoryMove.getReason());
            pstmt.setInt(6, inventoryMove.getMoveId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM INVENTORY_MOVE WHERE MOVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<InventoryMove> getByLot(int lotId) throws SQLException {
        List<InventoryMove> inventoryMoves = new ArrayList<>();
        String sql = "SELECT MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QTY, REASON, CREATED_AT FROM INVENTORY_MOVE WHERE LOT_ID = ? ORDER BY CREATED_AT DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, lotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inventoryMoves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return inventoryMoves;
    }

    @Override
    public List<InventoryMove> getByFromBay(int fromBayId) throws SQLException {
        List<InventoryMove> inventoryMoves = new ArrayList<>();
        String sql = "SELECT MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QTY, REASON, CREATED_AT FROM INVENTORY_MOVE WHERE FROM_BAY_ID = ? ORDER BY CREATED_AT DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, fromBayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inventoryMoves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return inventoryMoves;
    }

    @Override
    public List<InventoryMove> getByToBay(int toBayId) throws SQLException {
        List<InventoryMove> inventoryMoves = new ArrayList<>();
        String sql = "SELECT MOVE_ID, LOT_ID, FROM_BAY_ID, TO_BAY_ID, QTY, REASON, CREATED_AT FROM INVENTORY_MOVE WHERE TO_BAY_ID = ? ORDER BY CREATED_AT DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, toBayId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inventoryMoves.add(mapResultSetToInventoryMove(rs));
                }
            }
        }

        return inventoryMoves;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(MOVE_ID), 0) + 1 AS NEXT_ID FROM INVENTORY_MOVE";

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

