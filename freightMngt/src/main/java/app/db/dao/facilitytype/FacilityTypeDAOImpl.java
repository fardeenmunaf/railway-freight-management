package app.db.dao.facilitytype;

import app.db.DatabaseConnection;
import app.model.FacilityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de FacilityType na base de dados Oracle.
 * Implementa operações CRUD para a entidade FacilityType.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityTypeDAOImpl implements FacilityTypeDAO {

    private final Connection connection;

    public FacilityTypeDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(FacilityType facilityType) throws SQLException {
        String sql = "INSERT INTO FACILITY_TYPE (FACILITY_TYPE_ID, NAME) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (facilityType.getFacilityTypeId() == null) {
                facilityType.setFacilityTypeId(getNextId());
            }

            pstmt.setInt(1, facilityType.getFacilityTypeId());
            pstmt.setString(2, facilityType.getName());

            pstmt.executeUpdate();
        }
    }

    @Override
    public FacilityType get(int id) throws SQLException {
        String sql = "SELECT FACILITY_TYPE_ID, NAME FROM FACILITY_TYPE WHERE FACILITY_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFacilityType(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<FacilityType> getAll() throws SQLException {
        List<FacilityType> facilityTypes = new ArrayList<>();
        String sql = "SELECT FACILITY_TYPE_ID, NAME FROM FACILITY_TYPE ORDER BY FACILITY_TYPE_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                facilityTypes.add(mapResultSetToFacilityType(rs));
            }
        }

        return facilityTypes;
    }

    @Override
    public void update(FacilityType facilityType) throws SQLException {
        String sql = "UPDATE FACILITY_TYPE SET NAME = ? WHERE FACILITY_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, facilityType.getName());
            pstmt.setInt(2, facilityType.getFacilityTypeId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM FACILITY_TYPE WHERE FACILITY_TYPE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public FacilityType getByName(String name) throws SQLException {
        String sql = "SELECT FACILITY_TYPE_ID, NAME FROM FACILITY_TYPE WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFacilityType(rs);
                }
            }
        }

        return null;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(FACILITY_TYPE_ID), 0) + 1 AS NEXT_ID FROM FACILITY_TYPE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private FacilityType mapResultSetToFacilityType(ResultSet rs) throws SQLException {
        return new FacilityType(
                rs.getInt("FACILITY_TYPE_ID"),
                rs.getString("NAME")
        );
    }
}

