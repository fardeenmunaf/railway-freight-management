package app.db.dao.locomotive;

import app.db.dao.BaseDAOImpl;
import app.model.Locomotive;

import java.awt.Dimension;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Locomotive management in the Oracle database.
 * Implements CRUD operations for the Locomotive entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveDAOImpl extends BaseDAOImpl implements LocomotiveDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public LocomotiveDAOImpl() {
        super();
    }

    @Override
    public void create(Locomotive locomotive) throws SQLException {
        String sql = "INSERT INTO " + getTableName("LOCOMOTIVE") + " (LOCOMOTIVE_ID, OPERATOR_ID, LOCOMOTIVE_TYPE, NAME, BRAND, MODEL, " +
                     "YEAR, POWER_KW, ACCELERATION_MS2, LENGTH_M, WIDTH_M, HEIGHT_M, MAX_WEIGHT_T, TOTAL_WEIGHT_T, " +
                     "MAX_SPEED_KMH, OPERATIONAL_SPEED_KMH, NUM_BOGIES, BOGIE_TYPE, TRACTION_KN, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (locomotive.getLocomotiveId() == null) {
                locomotive.setLocomotiveId(getNextId());
            }

            pstmt.setInt(1, locomotive.getLocomotiveId());
            pstmt.setInt(2, locomotive.getOperator() != null ? locomotive.getOperator().getUserId() : null);
            pstmt.setInt(3, locomotive.getLocomotiveType() != null ? locomotive.getLocomotiveType().getLocomotiveTypeId() : null);
            pstmt.setString(4, locomotive.getName());
            pstmt.setString(5, locomotive.getBrand());
            pstmt.setString(6, locomotive.getModel());
            pstmt.setInt(7, locomotive.getYear());
            pstmt.setInt(8, locomotive.getPower());
            pstmt.setDouble(9, locomotive.getAcceleration());
            pstmt.setDouble(10, locomotive.getDimensions() != null ? locomotive.getDimensions().getWidth() : 0);
            pstmt.setDouble(11, locomotive.getDimensions() != null ? locomotive.getDimensions().getHeight() : 0);
            pstmt.setDouble(12, locomotive.getDimensions() != null ? locomotive.getDimensions().getHeight() : 0);
            pstmt.setDouble(13, locomotive.getMaxWeight());
            pstmt.setDouble(14, locomotive.getTotalWeight());
            pstmt.setDouble(15, locomotive.getMaxSpeed());
            pstmt.setDouble(16, locomotive.getOperationalSpeed());
            pstmt.setInt(17, locomotive.getMaxBogies());
            pstmt.setString(18, locomotive.getBogieType());
            pstmt.setString(19, locomotive.getTraction());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Locomotive get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("LOCOMOTIVE") + " WHERE LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocomotive(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Locomotive> getAll() throws SQLException {
        List<Locomotive> locomotives = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("LOCOMOTIVE") + " ORDER BY LOCOMOTIVE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                locomotives.add(mapResultSetToLocomotive(rs));
            }
        }
        return locomotives;
    }

    @Override
    public void update(Locomotive locomotive) throws SQLException {
        String sql = "UPDATE " + getTableName("LOCOMOTIVE") + " SET OPERATOR_ID = ?, LOCOMOTIVE_TYPE = ?, NAME = ?, BRAND = ?, MODEL = ?, " +
                     "YEAR = ?, POWER_KW = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locomotive.getOperator() != null ? locomotive.getOperator().getUserId() : null);
            pstmt.setInt(2, locomotive.getLocomotiveType() != null ? locomotive.getLocomotiveType().getLocomotiveTypeId() : null);
            pstmt.setString(3, locomotive.getName());
            pstmt.setString(4, locomotive.getBrand());
            pstmt.setString(5, locomotive.getModel());
            pstmt.setInt(6, locomotive.getYear());
            pstmt.setInt(7, locomotive.getPower());
            pstmt.setInt(8, locomotive.getLocomotiveId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("LOCOMOTIVE") + " WHERE LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Locomotive> getByOperator(int operatorId) throws SQLException {
        List<Locomotive> locomotives = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("LOCOMOTIVE") + " WHERE OPERATOR_ID = ? ORDER BY LOCOMOTIVE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, operatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    locomotives.add(mapResultSetToLocomotive(rs));
                }
            }
        }
        return locomotives;
    }

    @Override
    public List<Locomotive> getByType(int locomotiveTypeId) throws SQLException {
        List<Locomotive> locomotives = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("LOCOMOTIVE") + " WHERE LOCOMOTIVE_TYPE = ? ORDER BY LOCOMOTIVE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locomotiveTypeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    locomotives.add(mapResultSetToLocomotive(rs));
                }
            }
        }
        return locomotives;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("LOCOMOTIVE") + " WHERE LOCOMOTIVE_ID = ?";

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

    @Override
    public List<Locomotive> getAvailableForRoute(Integer departureFacilityId) throws SQLException {
        List<Locomotive> locomotives = new ArrayList<>();
        String sql = "{? = call APPUSER.fn_get_available_locomotives(?)}";

        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            // Register OUT parameter for the cursor
            cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            // Set IN parameter
            cstmt.setInt(2, departureFacilityId);

            // Execute the function
            cstmt.execute();

            // Get the cursor result
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs.next()) {
                    locomotives.add(mapResultSetToAvailableLocomotive(rs));
                }
            }
        }

        return locomotives;
    }

    /**
     * Gets the next available ID for a new Locomotive.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LOCOMOTIVE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("LOCOMOTIVE");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    /**
     * Maps a ResultSet row to a Locomotive object.
     *
     * @param rs the ResultSet containing the data
     * @return the Locomotive object
     * @throws SQLException if an error occurs during the operation
     */
    private Locomotive mapResultSetToLocomotive(ResultSet rs) throws SQLException {
        Locomotive locomotive = new Locomotive();

        // Basic fields from LOCOMOTIVE table
        locomotive.setLocomotiveId(rs.getInt("LOCOMOTIVE_ID"));
        locomotive.setBrand(rs.getString("BRAND"));
        locomotive.setModel(rs.getString("MODEL"));
        locomotive.setYear(rs.getInt("YEAR"));
        locomotive.setPower(rs.getInt("POWER_KW"));
        locomotive.setAcceleration(rs.getDouble("ACCELERATION_MS2"));
        locomotive.setMaxSpeed(rs.getDouble("MAX_SPEED_KMH"));
        locomotive.setOperationalSpeed(rs.getDouble("OPERATIONAL_SPEED_KMH"));
        locomotive.setMaxWeight(rs.getDouble("MAX_WEIGHT_T"));
        locomotive.setTotalWeight(rs.getDouble("TOTAL_WEIGHT_T"));
        locomotive.setMaxBogies(rs.getInt("NUM_BOGIES"));
        locomotive.setBogieType(rs.getString("BOGIE_TYPE"));
        locomotive.setTraction(rs.getString("TRACTION_KN"));
        locomotive.setStartFacility(rs.getInt("START_FACILITY"));

        // Dimensions - create Dimension object from length, width, height
        double length = rs.getDouble("LENGTH_M");
        double width = rs.getDouble("WIDTH_M");
        double height = rs.getDouble("HEIGHT_M");
        locomotive.setDimensions(new Dimension((int)(width * 100), (int)(height * 100))); // Convert to cm

        // Note: operator_id and locomotive_type are FKs - would need separate queries to populate
        // those objects if needed. For now, just displaying basic locomotive data.

        return locomotive;
    }

    /**
     * Maps a ResultSet from fn_get_available_locomotives to a Locomotive object
     * with USLP09 transient fields populated.
     *
     * @param rs the ResultSet from the PL/SQL function
     * @return the Locomotive object with all fields populated
     * @throws SQLException if an error occurs during the operation
     */
    private Locomotive mapResultSetToAvailableLocomotive(ResultSet rs) throws SQLException {
        Locomotive locomotive = new Locomotive();

        // Basic fields from database
        locomotive.setLocomotiveId(rs.getInt("locomotive_id"));
        locomotive.setBrand(rs.getString("brand"));
        locomotive.setModel(rs.getString("model"));
        locomotive.setYear(rs.getInt("year"));
        locomotive.setPower(rs.getInt("power_kw"));
        locomotive.setMaxSpeed(rs.getDouble("max_speed_kmh"));
        locomotive.setOperationalSpeed(rs.getDouble("operational_speed_kmh"));
        locomotive.setTotalWeight(rs.getDouble("total_weight_t"));

        // Dimensions - using length_m for Dimension object (simplified)
        Double length = rs.getDouble("length_m");
        locomotive.setDimensions(new java.awt.Dimension(length.intValue(), length.intValue()));

        // USLP09 transient fields
        locomotive.setCurrentLocationName(rs.getString("current_location"));
        locomotive.setLocationId(rs.getInt("location_id"));
        locomotive.setLocationLat(rs.getDouble("location_lat"));
        locomotive.setLocationLon(rs.getDouble("location_lon"));
        locomotive.setDistanceFromDeparture(rs.getDouble("distance_from_departure_km"));
        locomotive.setStatus(rs.getString("status"));
        locomotive.setTransitDestination(rs.getString("transit_destination"));
        locomotive.setTransitArrivalDate(rs.getTimestamp("transit_arrival_date"));
        locomotive.setOperatorCompanyName(rs.getString("operator_company"));

        return locomotive;
    }
}
