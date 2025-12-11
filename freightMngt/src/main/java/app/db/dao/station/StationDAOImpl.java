package app.db.dao.station;

import app.db.DatabaseConnection;
import app.model.Station;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationDAOImpl implements StationDAO {
    private final Connection connection;

    public StationDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Station station) throws SQLException {
        String sql = "INSERT INTO FACILITY (FACILITY_ID, NAME, FACILITY_TYPE_ID, LAT, LON, IS_INTERMODAL) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (station.getFacilityId() == null) {
                station.setFacilityId(getNextId());
            }
            pstmt.setInt(1, station.getFacilityId());
            pstmt.setString(2, station.getName());
            pstmt.setInt(3, station.getFacilityType() != null ? station.getFacilityType().getFacilityTypeId() : 2);
            pstmt.setDouble(4, station.getLat());
            pstmt.setDouble(5, station.getLon());
            pstmt.setInt(6, station.getIsIntermodal() ? 1 : 0);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Station get(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Station> getAll() throws SQLException {
        return new ArrayList<>();
    }

    @Override
    public void update(Station station) throws SQLException {
    }

    @Override
    public void delete(int id) throws SQLException {
    }

    @Override
    public List<Station> getByCountry(String country) throws SQLException {
        return new ArrayList<>();
    }

    @Override
    public List<Station> getMainStations() throws SQLException {
        return new ArrayList<>();
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
}

