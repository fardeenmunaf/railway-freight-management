package app.db.dao.warehouse;

import app.db.DatabaseConnection;
import app.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDAOImpl implements WarehouseDAO {

    private final Connection connection;

    public WarehouseDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Warehouse warehouse) throws SQLException {
        String sql = "INSERT INTO WAREHOUSE (WAREHOUSE_ID, FACILITY_ID, NAME) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (warehouse.getWarehouseId() == null) {
                warehouse.setWarehouseId(getNextId());
            }

            pstmt.setInt(1, warehouse.getWarehouseId());
            pstmt.setInt(2, warehouse.getFacility() != null ? warehouse.getFacility().getFacilityId() : null);
            pstmt.setString(3, warehouse.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Warehouse get(int id) throws SQLException {
        String sql = "SELECT * FROM WAREHOUSE WHERE WAREHOUSE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWarehouse(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Warehouse> getAll() throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = "SELECT * FROM WAREHOUSE ORDER BY WAREHOUSE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                warehouses.add(mapResultSetToWarehouse(rs));
            }
        }
        return warehouses;
    }

    @Override
    public void update(Warehouse warehouse) throws SQLException {
        String sql = "UPDATE WAREHOUSE SET FACILITY_ID = ?, NAME = ? WHERE WAREHOUSE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, warehouse.getFacility() != null ? warehouse.getFacility().getFacilityId() : null);
            pstmt.setString(2, warehouse.getName());
            pstmt.setInt(3, warehouse.getWarehouseId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM WAREHOUSE WHERE WAREHOUSE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Warehouse getByFacility(int facilityId) throws SQLException {
        String sql = "SELECT * FROM WAREHOUSE WHERE FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, facilityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWarehouse(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Warehouse getByName(String name) throws SQLException {
        String sql = "SELECT * FROM WAREHOUSE WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWarehouse(rs);
                }
            }
        }
        return null;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAREHOUSE_ID), 0) + 1 AS NEXT_ID FROM WAREHOUSE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    private Warehouse mapResultSetToWarehouse(ResultSet rs) throws SQLException {
        return new Warehouse();
    }
}

