package app.db.dao.wagon;

import app.db.dao.BaseDAOImpl;
import app.model.Wagon;
import app.model.WagonModel;
import app.model.Dimensions;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Wagon management in the Oracle database.
 * Implements CRUD operations for the Wagon entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonDAOImpl extends BaseDAOImpl implements WagonDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public WagonDAOImpl() {
        super();
    }

    @Override
    public void create(Wagon wagon) throws SQLException {
        String sql = "INSERT INTO " + getTableName("WAGON") + " (WAGON_ID, WAGON_MODEL, OPERATOR_ID, SERVICE_YEAR) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wagon.getWagonId() == null) {
                wagon.setWagonId(getNextId());
            }

            pstmt.setInt(1, wagon.getWagonId());

            if (wagon.getWagonModel() != null && wagon.getWagonModel().getWagonModelId() != null) {
                pstmt.setInt(2, wagon.getWagonModel().getWagonModelId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (wagon.getOperator() != null && wagon.getOperator().getUserId() != null) {
                pstmt.setInt(3, wagon.getOperator().getUserId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (wagon.getServiceYear() != null) {
                pstmt.setInt(4, wagon.getServiceYear());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public Wagon get(int id) throws SQLException {
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM " + getTableName("WAGON") + " w " +
                     "LEFT JOIN " + getTableName("WAGON_MODEL") + " wm ON w.WAGON_MODEL = wm.WAGON_MODEL_ID " +
                     "WHERE w.WAGON_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWagon(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Wagon> getAll() throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM " + getTableName("WAGON") + " w " +
                     "LEFT JOIN " + getTableName("WAGON_MODEL") + " wm ON w.WAGON_MODEL = wm.WAGON_MODEL_ID " +
                     "ORDER BY w.WAGON_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                wagons.add(mapResultSetToWagon(rs));
            }
        }

        return wagons;
    }

    @Override
    public void update(Wagon wagon) throws SQLException {
        String sql = "UPDATE " + getTableName("WAGON") + " SET WAGON_MODEL = ?, OPERATOR_ID = ?, SERVICE_YEAR = ? " +
                     "WHERE WAGON_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wagon.getWagonModel() != null && wagon.getWagonModel().getWagonModelId() != null) {
                pstmt.setInt(1, wagon.getWagonModel().getWagonModelId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (wagon.getOperator() != null && wagon.getOperator().getUserId() != null) {
                pstmt.setInt(2, wagon.getOperator().getUserId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (wagon.getServiceYear() != null) {
                pstmt.setInt(3, wagon.getServiceYear());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, wagon.getWagonId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("WAGON") + " WHERE WAGON_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Wagon> getByModel(int wagonModelId) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM " + getTableName("WAGON") + " w " +
                     "LEFT JOIN " + getTableName("WAGON_MODEL") + " wm ON w.WAGON_MODEL = wm.WAGON_MODEL_ID " +
                     "WHERE w.WAGON_MODEL = ? " +
                     "ORDER BY w.WAGON_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, wagonModelId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wagons.add(mapResultSetToWagon(rs));
                }
            }
        }

        return wagons;
    }

    @Override
    public List<Wagon> getByOperator(int operatorId) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM " + getTableName("WAGON") + " w " +
                     "LEFT JOIN " + getTableName("WAGON_MODEL") + " wm ON w.WAGON_MODEL = wm.WAGON_MODEL_ID " +
                     "WHERE w.OPERATOR_ID = ? " +
                     "ORDER BY w.WAGON_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, operatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wagons.add(mapResultSetToWagon(rs));
                }
            }
        }

        return wagons;
    }

    @Override
    public List<Wagon> getByServiceYear(int serviceYear) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM " + getTableName("WAGON") + " w " +
                     "LEFT JOIN " + getTableName("WAGON_MODEL") + " wm ON w.WAGON_MODEL = wm.WAGON_MODEL_ID " +
                     "WHERE w.SERVICE_YEAR = ? " +
                     "ORDER BY w.WAGON_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceYear);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wagons.add(mapResultSetToWagon(rs));
                }
            }
        }

        return wagons;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("WAGON") + " WHERE WAGON_ID = ?";

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
    public List<Wagon> getAvailableForRoute(Integer departureFacilityId) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "{? = call APPUSER.fn_get_available_wagons(?)}";

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
                    wagons.add(mapResultSetToAvailableWagon(rs));
                }
            }
        }

        return wagons;
    }

    /**
     * Gets the next available ID for a new Wagon.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAGON_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("WAGON");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Wagon object.
     *
     * @param rs the ResultSet containing the data
     * @return the Wagon object
     * @throws SQLException if an error occurs during the operation
     */
    private Wagon mapResultSetToWagon(ResultSet rs) throws SQLException {
        Wagon wagon = new Wagon();

        wagon.setWagonId(rs.getInt("WAGON_ID"));

        // Set WagonModel if available
        if (rs.getObject("WAGON_MODEL") != null) {
            WagonModel model = new WagonModel();
            model.setWagonModelId(rs.getInt("WAGON_MODEL"));
            model.setName(rs.getString("MODEL_NAME"));
            wagon.setWagonModel(model);
        }

        // Set Operator if available
        if (rs.getObject("OPERATOR_ID") != null) {
            User operator = new User();
            operator.setUserId(rs.getInt("OPERATOR_ID"));
            wagon.setOperator(operator);
        }

        if (rs.getObject("SERVICE_YEAR") != null) {
            wagon.setServiceYear(rs.getInt("SERVICE_YEAR"));
        }

        return wagon;
    }

    /**
     * Maps a ResultSet from fn_get_available_wagons to a Wagon object
     * with USLP09 transient fields populated.
     *
     * @param rs the ResultSet from the PL/SQL function
     * @return the Wagon object with all fields populated
     * @throws SQLException if an error occurs during the operation
     */
    private Wagon mapResultSetToAvailableWagon(ResultSet rs) throws SQLException {
        Wagon wagon = new Wagon();

        // Basic fields from database
        wagon.setWagonId(rs.getInt("wagon_id"));
        wagon.setServiceYear(rs.getInt("service_year"));

        // Set WagonModel with data from function
        WagonModel model = new WagonModel();
        model.setName(rs.getString("model_name"));
        model.setConstructor(rs.getString("constructor"));
        model.setPayloadWeight(rs.getDouble("payload_t"));
        model.setTareWeight(rs.getDouble("tare_t"));
        model.setMaxSpeed(rs.getDouble("max_speed_kmh"));

        // Set Dimensions
        Dimensions dimensions = new Dimensions();
        dimensions.setLength(rs.getDouble("length_m"));
        dimensions.setWidth(rs.getDouble("width_m"));
        dimensions.setHeight(rs.getDouble("height_m"));
        model.setDimensions(dimensions);

        wagon.setWagonModel(model);

        // USLP09 transient fields
        wagon.setCurrentLocationName(rs.getString("current_location"));
        wagon.setLocationId(rs.getInt("location_id"));
        wagon.setLocationLat(rs.getDouble("location_lat"));
        wagon.setLocationLon(rs.getDouble("location_lon"));
        wagon.setDistanceFromDeparture(rs.getDouble("distance_from_departure_km"));
        wagon.setStatus(rs.getString("status"));
        wagon.setParkingTrackName(rs.getString("parking_track_name"));

        // Handle Oracle NUMBER to Integer conversion (returns BigDecimal)
        Object parkingTrackObj = rs.getObject("parking_track_id");
        if (parkingTrackObj != null) {
            if (parkingTrackObj instanceof java.math.BigDecimal) {
                wagon.setParkingTrackId(((java.math.BigDecimal) parkingTrackObj).intValue());
            } else if (parkingTrackObj instanceof Number) {
                wagon.setParkingTrackId(((Number) parkingTrackObj).intValue());
            }
        }

        Object parkingPosObj = rs.getObject("parking_position");
        if (parkingPosObj != null) {
            if (parkingPosObj instanceof java.math.BigDecimal) {
                wagon.setParkingPosition(((java.math.BigDecimal) parkingPosObj).intValue());
            } else if (parkingPosObj instanceof Number) {
                wagon.setParkingPosition(((Number) parkingPosObj).intValue());
            }
        }

        wagon.setOperatorCompanyName(rs.getString("operator_company"));

        // Handle in_active_freight conversion
        Object inActiveFreightObj = rs.getObject("in_active_freight");
        if (inActiveFreightObj != null) {
            if (inActiveFreightObj instanceof java.math.BigDecimal) {
                wagon.setInActiveFreight(((java.math.BigDecimal) inActiveFreightObj).intValue());
            } else if (inActiveFreightObj instanceof Number) {
                wagon.setInActiveFreight(((Number) inActiveFreightObj).intValue());
            } else {
                wagon.setInActiveFreight(0);
            }
        } else {
            wagon.setInActiveFreight(0);
        }

        return wagon;
    }
}

