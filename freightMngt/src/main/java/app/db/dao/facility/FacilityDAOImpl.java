package app.db.dao.facility;

import app.db.DatabaseConnection;
import app.model.Facility;
import app.model.FacilityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de Facility na base de dados Oracle.
 * Implementa operações CRUD para a entidade Facility.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityDAOImpl implements FacilityDAO {

    private final Connection connection;

    public FacilityDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Facility facility) throws SQLException {
        String sql = "INSERT INTO FACILITY (FACILITY_ID, NAME, FACILITY_TYPE_ID, LAT, LON, IS_INTERMODAL) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (facility.getFacilityId() == null) {
                facility.setFacilityId(getNextId());
            }

            pstmt.setInt(1, facility.getFacilityId());
            pstmt.setString(2, facility.getName());

            if (facility.getFacilityType() != null) {
                pstmt.setInt(3, facility.getFacilityType().getFacilityTypeId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setDouble(4, facility.getLat());
            pstmt.setDouble(5, facility.getLon());
            pstmt.setInt(6, facility.getIsIntermodal() ? 1 : 0);

            pstmt.executeUpdate();
        }
    }

    @Override
    public Facility get(int id) throws SQLException {
        String sql = "SELECT f.FACILITY_ID, f.NAME, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.FACILITY_TYPE_ID, ft.NAME as TYPE_NAME " +
                     "FROM FACILITY f " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "WHERE f.FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFacility(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Facility> getAll() throws SQLException {
        List<Facility> facilities = new ArrayList<>();
        String sql = "SELECT f.FACILITY_ID, f.NAME, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.FACILITY_TYPE_ID, ft.NAME as TYPE_NAME " +
                     "FROM FACILITY f " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "ORDER BY f.FACILITY_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                facilities.add(mapResultSetToFacility(rs));
            }
        }

        return facilities;
    }

    @Override
    public void update(Facility facility) throws SQLException {
        String sql = "UPDATE FACILITY SET NAME = ?, FACILITY_TYPE_ID = ?, LAT = ?, LON = ?, IS_INTERMODAL = ? WHERE FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, facility.getName());

            if (facility.getFacilityType() != null) {
                pstmt.setInt(2, facility.getFacilityType().getFacilityTypeId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setDouble(3, facility.getLat());
            pstmt.setDouble(4, facility.getLon());
            pstmt.setInt(5, facility.getIsIntermodal() ? 1 : 0);
            pstmt.setInt(6, facility.getFacilityId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM FACILITY WHERE FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Facility> getByIntermodal(boolean isIntermodal) throws SQLException {
        List<Facility> facilities = new ArrayList<>();
        String sql = "SELECT f.FACILITY_ID, f.NAME, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.FACILITY_TYPE_ID, ft.NAME as TYPE_NAME " +
                     "FROM FACILITY f " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "WHERE f.IS_INTERMODAL = ? " +
                     "ORDER BY f.FACILITY_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, isIntermodal ? 1 : 0);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facilities.add(mapResultSetToFacility(rs));
                }
            }
        }

        return facilities;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(FACILITY_ID), 0) + 1 AS NEXT_ID FROM FACILITY";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private Facility mapResultSetToFacility(ResultSet rs) throws SQLException {
        FacilityType facilityType = null;
        if (rs.getObject("FACILITY_TYPE_ID") != null) {
            facilityType = new FacilityType(
                    rs.getInt("FACILITY_TYPE_ID"),
                    rs.getString("TYPE_NAME")
            );
        }

        return new Facility(
                rs.getInt("FACILITY_ID"),
                rs.getString("NAME"),
                facilityType,
                rs.getDouble("LAT"),
                rs.getDouble("LON"),
                rs.getInt("IS_INTERMODAL") == 1
        );
    }
}

