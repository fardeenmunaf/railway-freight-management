package app.db.dao.facilityfeaturetype;

import app.db.DatabaseConnection;
import app.model.FacilityFeatureType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for FacilityFeatureType management in Oracle database.
 * Implements CRUD operations for the FacilityFeatureType entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityFeatureTypeDAOImpl implements FacilityFeatureTypeDAO {

    private final Connection connection;

    public FacilityFeatureTypeDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(FacilityFeatureType facilityFeatureType) throws SQLException {
        String sql = "INSERT INTO FACILITY_FEATURE_TYPE (FEATURE_ID, NAME) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (facilityFeatureType.getFeatureId() == null) {
                facilityFeatureType.setFeatureId(getNextId());
            }

            pstmt.setInt(1, facilityFeatureType.getFeatureId());
            pstmt.setString(2, facilityFeatureType.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public FacilityFeatureType get(int id) throws SQLException {
        String sql = "SELECT FEATURE_ID, NAME FROM FACILITY_FEATURE_TYPE WHERE FEATURE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFacilityFeatureType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<FacilityFeatureType> getAll() throws SQLException {
        List<FacilityFeatureType> facilityFeatureTypes = new ArrayList<>();
        String sql = "SELECT FEATURE_ID, NAME FROM FACILITY_FEATURE_TYPE ORDER BY FEATURE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                facilityFeatureTypes.add(mapResultSetToFacilityFeatureType(rs));
            }
        }

        return facilityFeatureTypes;
    }

    @Override
    public void update(FacilityFeatureType facilityFeatureType) throws SQLException {
        String sql = "UPDATE FACILITY_FEATURE_TYPE SET NAME = ? WHERE FEATURE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, facilityFeatureType.getName());
            pstmt.setInt(2, facilityFeatureType.getFeatureId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM FACILITY_FEATURE_TYPE WHERE FEATURE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public FacilityFeatureType getByName(String name) throws SQLException {
        String sql = "SELECT FEATURE_ID, NAME FROM FACILITY_FEATURE_TYPE WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFacilityFeatureType(rs);
                }
            }
        }

        return null;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(FEATURE_ID), 0) + 1 AS NEXT_ID FROM FACILITY_FEATURE_TYPE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private FacilityFeatureType mapResultSetToFacilityFeatureType(ResultSet rs) throws SQLException {
        return new FacilityFeatureType(
                rs.getInt("FEATURE_ID"),
                rs.getString("NAME")
        );
    }
}

