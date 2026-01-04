package app.db.dao.station;

import app.db.dao.BaseDAOImpl;
import app.model.Station;
import app.model.FacilityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Station management in the Oracle database.
 * Implements CRUD operations for the Station entity.
 * Station extends Facility, so includes facility fields.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class StationDAOImpl extends BaseDAOImpl implements StationDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public StationDAOImpl() {
        super();
    }

    @Override
    public void create(Station station) throws SQLException {
        String sql = "INSERT INTO STATION (FACILITY_ID, COUNTRY, TIME_ZONE, TIME_ZONE_GROUP, " +
                     "IS_CITY, IS_MAIN_STATION, IS_AIRPORT) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (station.getFacilityId() == null) {
                station.setFacilityId(getNextId());
            }

            pstmt.setInt(1, station.getFacilityId());
            pstmt.setString(2, station.getCountry());
            pstmt.setString(3, station.getTimeZone());
            pstmt.setString(4, station.getTimeZoneGroup());

            pstmt.setInt(5, station.getIsCity() != null && station.getIsCity() ? 1 : 0);
            pstmt.setInt(6, station.getIsMainStation() != null && station.getIsMainStation() ? 1 : 0);
            pstmt.setInt(7, station.getIsAirport() != null && station.getIsAirport() ? 1 : 0);

            pstmt.executeUpdate();
        }
    }

    @Override
    public Station get(int id) throws SQLException {
        String sql = "SELECT s.FACILITY_ID, s.COUNTRY, s.TIME_ZONE, s.TIME_ZONE_GROUP, " +
                     "s.IS_CITY, s.IS_MAIN_STATION, s.IS_AIRPORT, " +
                     "f.NAME, f.FACILITY_TYPE_ID, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.NAME AS FACILITY_TYPE_NAME " +
                     "FROM STATION s " +
                     "JOIN FACILITY f ON s.FACILITY_ID = f.FACILITY_ID " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "WHERE s.FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStation(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Station> getAll() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT s.FACILITY_ID, s.COUNTRY, s.TIME_ZONE, s.TIME_ZONE_GROUP, " +
                     "s.IS_CITY, s.IS_MAIN_STATION, s.IS_AIRPORT, " +
                     "f.NAME, f.FACILITY_TYPE_ID, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.NAME AS FACILITY_TYPE_NAME " +
                     "FROM STATION s " +
                     "JOIN FACILITY f ON s.FACILITY_ID = f.FACILITY_ID " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "ORDER BY s.FACILITY_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stations.add(mapResultSetToStation(rs));
            }
        }

        return stations;
    }

    @Override
    public void update(Station station) throws SQLException {
        String sql = "UPDATE STATION SET COUNTRY = ?, TIME_ZONE = ?, TIME_ZONE_GROUP = ?, " +
                     "IS_CITY = ?, IS_MAIN_STATION = ?, IS_AIRPORT = ? WHERE FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, station.getCountry());
            pstmt.setString(2, station.getTimeZone());
            pstmt.setString(3, station.getTimeZoneGroup());

            pstmt.setInt(4, station.getIsCity() != null && station.getIsCity() ? 1 : 0);
            pstmt.setInt(5, station.getIsMainStation() != null && station.getIsMainStation() ? 1 : 0);
            pstmt.setInt(6, station.getIsAirport() != null && station.getIsAirport() ? 1 : 0);

            pstmt.setInt(7, station.getFacilityId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM STATION WHERE FACILITY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Station> getByCountry(String country) throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT s.FACILITY_ID, s.COUNTRY, s.TIME_ZONE, s.TIME_ZONE_GROUP, " +
                     "s.IS_CITY, s.IS_MAIN_STATION, s.IS_AIRPORT, " +
                     "f.NAME, f.FACILITY_TYPE_ID, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.NAME AS FACILITY_TYPE_NAME " +
                     "FROM STATION s " +
                     "JOIN FACILITY f ON s.FACILITY_ID = f.FACILITY_ID " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "WHERE s.COUNTRY = ? " +
                     "ORDER BY s.FACILITY_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, country);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stations.add(mapResultSetToStation(rs));
                }
            }
        }

        return stations;
    }

    @Override
    public List<Station> getMainStations() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT s.FACILITY_ID, s.COUNTRY, s.TIME_ZONE, s.TIME_ZONE_GROUP, " +
                     "s.IS_CITY, s.IS_MAIN_STATION, s.IS_AIRPORT, " +
                     "f.NAME, f.FACILITY_TYPE_ID, f.LAT, f.LON, f.IS_INTERMODAL, " +
                     "ft.NAME AS FACILITY_TYPE_NAME " +
                     "FROM STATION s " +
                     "JOIN FACILITY f ON s.FACILITY_ID = f.FACILITY_ID " +
                     "LEFT JOIN FACILITY_TYPE ft ON f.FACILITY_TYPE_ID = ft.FACILITY_TYPE_ID " +
                     "WHERE s.IS_MAIN_STATION = 1 " +
                     "ORDER BY s.FACILITY_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stations.add(mapResultSetToStation(rs));
            }
        }

        return stations;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("STATION") + " WHERE STATION_ID = ?";

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
     * Gets the next available ID for a new Station.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(FACILITY_ID), 0) + 1 AS NEXT_ID FROM STATION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Station object.
     *
     * @param rs the ResultSet containing the data
     * @return the Station object
     * @throws SQLException if an error occurs during the operation
     */
    private Station mapResultSetToStation(ResultSet rs) throws SQLException {
        Station station = new Station();

        // Set Facility inherited fields
        station.setFacilityId(rs.getInt("FACILITY_ID"));
        station.setName(rs.getString("NAME"));

        // Set FacilityType if available
        if (rs.getObject("FACILITY_TYPE_ID") != null) {
            FacilityType facilityType = new FacilityType();
            facilityType.setFacilityTypeId(rs.getInt("FACILITY_TYPE_ID"));
            facilityType.setName(rs.getString("FACILITY_TYPE_NAME"));
            station.setFacilityType(facilityType);
        }

        if (rs.getObject("LAT") != null) {
            station.setLat(rs.getDouble("LAT"));
        }

        if (rs.getObject("LON") != null) {
            station.setLon(rs.getDouble("LON"));
        }

        station.setIsIntermodal(rs.getInt("IS_INTERMODAL") == 1);

        // Set Station specific fields
        station.setCountry(rs.getString("COUNTRY"));
        station.setTimeZone(rs.getString("TIME_ZONE"));
        station.setTimeZoneGroup(rs.getString("TIME_ZONE_GROUP"));
        station.setIsCity(rs.getInt("IS_CITY") == 1);
        station.setIsMainStation(rs.getInt("IS_MAIN_STATION") == 1);
        station.setIsAirport(rs.getInt("IS_AIRPORT") == 1);

        return station;
    }
}

