package app.db.dao.warehouseaisle;

import app.db.DatabaseConnection;
import app.model.WarehouseAisle;
import app.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for WarehouseAisle management in the Oracle database.
 * Implements CRUD operations for the WarehouseAisle entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseAisleDAOImpl implements WarehouseAisleDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public WarehouseAisleDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(WarehouseAisle aisle) throws SQLException {
        String sql = "INSERT INTO WAREHOUSE_AISLE (AISLE_ID, WAREHOUSE_ID, AISLE_NO, UNIQUE_CODE) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (aisle.getAisleId() == null) {
                aisle.setAisleId(getNextId());
            }

            pstmt.setInt(1, aisle.getAisleId());

            if (aisle.getWarehouse() != null && aisle.getWarehouse().getWarehouseId() != null) {
                pstmt.setInt(2, aisle.getWarehouse().getWarehouseId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (aisle.getAisleNo() != null) {
                pstmt.setInt(3, aisle.getAisleNo());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, aisle.getUniqueCode());

            pstmt.executeUpdate();
        }
    }

    @Override
    public WarehouseAisle get(int id) throws SQLException {
        String sql = "SELECT wa.AISLE_ID, wa.WAREHOUSE_ID, wa.AISLE_NO, wa.UNIQUE_CODE, " +
                     "w.NAME AS WAREHOUSE_NAME " +
                     "FROM WAREHOUSE_AISLE wa " +
                     "LEFT JOIN WAREHOUSE w ON wa.WAREHOUSE_ID = w.WAREHOUSE_ID " +
                     "WHERE wa.AISLE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWarehouseAisle(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<WarehouseAisle> getAll() throws SQLException {
        List<WarehouseAisle> aisles = new ArrayList<>();
        String sql = "SELECT wa.AISLE_ID, wa.WAREHOUSE_ID, wa.AISLE_NO, wa.UNIQUE_CODE, " +
                     "w.NAME AS WAREHOUSE_NAME " +
                     "FROM WAREHOUSE_AISLE wa " +
                     "LEFT JOIN WAREHOUSE w ON wa.WAREHOUSE_ID = w.WAREHOUSE_ID " +
                     "ORDER BY wa.AISLE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                aisles.add(mapResultSetToWarehouseAisle(rs));
            }
        }

        return aisles;
    }

    @Override
    public void update(WarehouseAisle aisle) throws SQLException {
        String sql = "UPDATE WAREHOUSE_AISLE SET WAREHOUSE_ID = ?, AISLE_NO = ?, UNIQUE_CODE = ? " +
                     "WHERE AISLE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (aisle.getWarehouse() != null && aisle.getWarehouse().getWarehouseId() != null) {
                pstmt.setInt(1, aisle.getWarehouse().getWarehouseId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (aisle.getAisleNo() != null) {
                pstmt.setInt(2, aisle.getAisleNo());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, aisle.getUniqueCode());
            pstmt.setInt(4, aisle.getAisleId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM WAREHOUSE_AISLE WHERE AISLE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<WarehouseAisle> getByWarehouse(int warehouseId) throws SQLException {
        List<WarehouseAisle> aisles = new ArrayList<>();
        String sql = "SELECT wa.AISLE_ID, wa.WAREHOUSE_ID, wa.AISLE_NO, wa.UNIQUE_CODE, " +
                     "w.NAME AS WAREHOUSE_NAME " +
                     "FROM WAREHOUSE_AISLE wa " +
                     "LEFT JOIN WAREHOUSE w ON wa.WAREHOUSE_ID = w.WAREHOUSE_ID " +
                     "WHERE wa.WAREHOUSE_ID = ? " +
                     "ORDER BY wa.AISLE_NO";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, warehouseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    aisles.add(mapResultSetToWarehouseAisle(rs));
                }
            }
        }

        return aisles;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM WAREHOUSE_AISLE WHERE AISLE_ID = ?";

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
     * Gets the next available ID for a new WarehouseAisle.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(AISLE_ID), 0) + 1 AS NEXT_ID FROM WAREHOUSE_AISLE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a WarehouseAisle object.
     *
     * @param rs the ResultSet containing the data
     * @return the WarehouseAisle object
     * @throws SQLException if an error occurs during the operation
     */
    private WarehouseAisle mapResultSetToWarehouseAisle(ResultSet rs) throws SQLException {
        WarehouseAisle aisle = new WarehouseAisle();

        aisle.setAisleId(rs.getInt("AISLE_ID"));

        // Set Warehouse if available
        if (rs.getObject("WAREHOUSE_ID") != null) {
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseId(rs.getInt("WAREHOUSE_ID"));
            warehouse.setName(rs.getString("WAREHOUSE_NAME"));
            aisle.setWarehouse(warehouse);
        }

        if (rs.getObject("AISLE_NO") != null) {
            aisle.setAisleNo(rs.getInt("AISLE_NO"));
        }

        aisle.setUniqueCode(rs.getString("UNIQUE_CODE"));

        return aisle;
    }
}

