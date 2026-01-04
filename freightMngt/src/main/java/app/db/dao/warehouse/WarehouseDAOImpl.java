package app.db.dao.warehouse;

import app.db.DatabaseConnection;
import app.db.dao.BaseDAOImpl;
import app.model.Warehouse;
import app.model.Facility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Warehouse management in the Oracle database.
 * Implements CRUD operations for the Warehouse entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseDAOImpl extends BaseDAOImpl implements WarehouseDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public WarehouseDAOImpl() {
        super();
    }

    @Override
    public void create(Warehouse warehouse) throws SQLException {
        String sql = "INSERT INTO WAREHOUSE (WAREHOUSE_ID, FACILITY_ID, NAME) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (warehouse.getWarehouseId() == null) {
                warehouse.setWarehouseId(getNextId());
            }

            pstmt.setInt(1, warehouse.getWarehouseId());

            if (warehouse.getFacility() != null && warehouse.getFacility().getFacilityId() != null) {
                pstmt.setInt(2, warehouse.getFacility().getFacilityId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, warehouse.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Warehouse get(int id) throws SQLException {
        String sql = "SELECT w.WAREHOUSE_ID, w.FACILITY_ID, w.NAME, f.NAME AS FACILITY_NAME " +
                     "FROM WAREHOUSE w " +
                     "LEFT JOIN FACILITY f ON w.FACILITY_ID = f.FACILITY_ID " +
                     "WHERE w.WAREHOUSE_ID = ?";

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
        String sql = "SELECT w.WAREHOUSE_ID, w.FACILITY_ID, w.NAME, f.NAME AS FACILITY_NAME " +
                     "FROM WAREHOUSE w " +
                     "LEFT JOIN FACILITY f ON w.FACILITY_ID = f.FACILITY_ID " +
                     "ORDER BY w.WAREHOUSE_ID";

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
            if (warehouse.getFacility() != null && warehouse.getFacility().getFacilityId() != null) {
                pstmt.setInt(1, warehouse.getFacility().getFacilityId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

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
        String sql = "SELECT w.WAREHOUSE_ID, w.FACILITY_ID, w.NAME, f.NAME AS FACILITY_NAME " +
                     "FROM WAREHOUSE w " +
                     "LEFT JOIN FACILITY f ON w.FACILITY_ID = f.FACILITY_ID " +
                     "WHERE w.FACILITY_ID = ?";

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
        String sql = "SELECT w.WAREHOUSE_ID, w.FACILITY_ID, w.NAME, f.NAME AS FACILITY_NAME " +
                     "FROM WAREHOUSE w " +
                     "LEFT JOIN FACILITY f ON w.FACILITY_ID = f.FACILITY_ID " +
                     "WHERE w.NAME = ?";

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

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM WAREHOUSE WHERE WAREHOUSE_ID = ?";

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
     * Gets the next available ID for a new Warehouse.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
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

    /**
     * Maps a ResultSet row to a Warehouse object.
     *
     * @param rs the ResultSet containing the data
     * @return the Warehouse object
     * @throws SQLException if an error occurs during the operation
     */
    private Warehouse mapResultSetToWarehouse(ResultSet rs) throws SQLException {
        Warehouse warehouse = new Warehouse();

        warehouse.setWarehouseId(rs.getInt("WAREHOUSE_ID"));

        // Set Facility if available
        if (rs.getObject("FACILITY_ID") != null) {
            Facility facility = new Facility();
            facility.setFacilityId(rs.getInt("FACILITY_ID"));
            facility.setName(rs.getString("FACILITY_NAME"));
            warehouse.setFacility(facility);
        }

        warehouse.setName(rs.getString("NAME"));

        return warehouse;
    }
}
