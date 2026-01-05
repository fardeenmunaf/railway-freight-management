package app.db.dao.freight;

import app.db.dao.BaseDAOImpl;
import app.model.Freight;
import app.model.Freight.FreightStatus;
import app.model.Facility;
import app.model.Item;
import app.model.FreightRoute;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Freight management in the Oracle database.
 * Implements CRUD operations for the Freight entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightDAOImpl extends BaseDAOImpl implements FreightDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public FreightDAOImpl() {
        super();
    }

    @Override
    public void create(Freight freight) throws SQLException {
        String sql = "INSERT INTO " + getTableName("FREIGHT") + " " +
                     "(FREIGHT_ID, DESCRIPTION, ITEM_ID, WEIGHT_KG, VOLUME_M3, " +
                     "ORIGIN_FACILITY_ID, DESTINATION_FACILITY_ID, STATUS, " +
                     "ROUTE_ID, DELIVERY_DEADLINE, SPECIAL_HANDLING, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (freight.getFreightId() == null) {
                freight.setFreightId(getNextId());
            }

            pstmt.setInt(1, freight.getFreightId());
            pstmt.setString(2, freight.getDescription());

            if (freight.getItem() != null && freight.getItem().getItemId() != null) {
                pstmt.setInt(3, freight.getItem().getItemId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (freight.getWeightKg() != null) {
                pstmt.setDouble(4, freight.getWeightKg());
            } else {
                pstmt.setNull(4, Types.DOUBLE);
            }

            if (freight.getVolumeM3() != null) {
                pstmt.setDouble(5, freight.getVolumeM3());
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }

            if (freight.getOriginFacility() != null && freight.getOriginFacility().getFacilityId() != null) {
                pstmt.setInt(6, freight.getOriginFacility().getFacilityId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            if (freight.getDestinationFacility() != null && freight.getDestinationFacility().getFacilityId() != null) {
                pstmt.setInt(7, freight.getDestinationFacility().getFacilityId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            pstmt.setString(8, freight.getStatus() != null ? freight.getStatus().name() : FreightStatus.PENDING.name());

            if (freight.getAssignedRoute() != null && freight.getAssignedRoute().getRouteId() != null) {
                pstmt.setInt(9, freight.getAssignedRoute().getRouteId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }

            if (freight.getDeliveryDeadline() != null) {
                pstmt.setTimestamp(10, Timestamp.valueOf(freight.getDeliveryDeadline()));
            } else {
                pstmt.setNull(10, Types.TIMESTAMP);
            }

            pstmt.setString(11, freight.getSpecialHandlingInstructions());

            LocalDateTime createdAt = freight.getCreatedAt() != null ? freight.getCreatedAt() : LocalDateTime.now();
            pstmt.setTimestamp(12, Timestamp.valueOf(createdAt));

            pstmt.executeUpdate();
        }
    }

    @Override
    public Freight get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("FREIGHT") + " WHERE FREIGHT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFreight(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Freight> getAll() throws SQLException {
        List<Freight> freights = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("FREIGHT") + " ORDER BY FREIGHT_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                freights.add(mapResultSetToFreight(rs));
            }
        }

        return freights;
    }

    @Override
    public void update(Freight freight) throws SQLException {
        String sql = "UPDATE " + getTableName("FREIGHT") + " SET " +
                     "DESCRIPTION = ?, ITEM_ID = ?, WEIGHT_KG = ?, VOLUME_M3 = ?, " +
                     "ORIGIN_FACILITY_ID = ?, DESTINATION_FACILITY_ID = ?, STATUS = ?, " +
                     "ROUTE_ID = ?, DELIVERY_DEADLINE = ?, SPECIAL_HANDLING = ? " +
                     "WHERE FREIGHT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, freight.getDescription());

            if (freight.getItem() != null && freight.getItem().getItemId() != null) {
                pstmt.setInt(2, freight.getItem().getItemId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (freight.getWeightKg() != null) {
                pstmt.setDouble(3, freight.getWeightKg());
            } else {
                pstmt.setNull(3, Types.DOUBLE);
            }

            if (freight.getVolumeM3() != null) {
                pstmt.setDouble(4, freight.getVolumeM3());
            } else {
                pstmt.setNull(4, Types.DOUBLE);
            }

            if (freight.getOriginFacility() != null && freight.getOriginFacility().getFacilityId() != null) {
                pstmt.setInt(5, freight.getOriginFacility().getFacilityId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            if (freight.getDestinationFacility() != null && freight.getDestinationFacility().getFacilityId() != null) {
                pstmt.setInt(6, freight.getDestinationFacility().getFacilityId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            pstmt.setString(7, freight.getStatus() != null ? freight.getStatus().name() : FreightStatus.PENDING.name());

            if (freight.getAssignedRoute() != null && freight.getAssignedRoute().getRouteId() != null) {
                pstmt.setInt(8, freight.getAssignedRoute().getRouteId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            if (freight.getDeliveryDeadline() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(freight.getDeliveryDeadline()));
            } else {
                pstmt.setNull(9, Types.TIMESTAMP);
            }

            pstmt.setString(10, freight.getSpecialHandlingInstructions());
            pstmt.setInt(11, freight.getFreightId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("FREIGHT") + " WHERE FREIGHT_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Freight> getByStatus(String status) throws SQLException {
        List<Freight> freights = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("FREIGHT") + " WHERE STATUS = ? ORDER BY FREIGHT_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    freights.add(mapResultSetToFreight(rs));
                }
            }
        }

        return freights;
    }

    @Override
    public List<Freight> getPendingFreights() throws SQLException {
        return getByStatus(FreightStatus.PENDING.name());
    }

    @Override
    public List<Freight> getByOrigin(int originFacilityId) throws SQLException {
        List<Freight> freights = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("FREIGHT") + " WHERE ORIGIN_FACILITY_ID = ? ORDER BY FREIGHT_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, originFacilityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    freights.add(mapResultSetToFreight(rs));
                }
            }
        }

        return freights;
    }

    @Override
    public List<Freight> getByDestination(int destinationFacilityId) throws SQLException {
        List<Freight> freights = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("FREIGHT") + " WHERE DESTINATION_FACILITY_ID = ? ORDER BY FREIGHT_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, destinationFacilityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    freights.add(mapResultSetToFreight(rs));
                }
            }
        }

        return freights;
    }

    @Override
    public List<Freight> getByRoute(int routeId) throws SQLException {
        List<Freight> freights = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("FREIGHT") + " WHERE ROUTE_ID = ? ORDER BY FREIGHT_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, routeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    freights.add(mapResultSetToFreight(rs));
                }
            }
        }

        return freights;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("FREIGHT") + " WHERE FREIGHT_ID = ?";

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
     * Gets the next available ID for a new Freight.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(FREIGHT_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("FREIGHT");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Freight object.
     *
     * @param rs the ResultSet containing the data
     * @return the Freight object
     * @throws SQLException if an error occurs during the operation
     */
    private Freight mapResultSetToFreight(ResultSet rs) throws SQLException {
        Freight freight = new Freight();

        freight.setFreightId(rs.getInt("FREIGHT_ID"));
        freight.setDescription(rs.getString("DESCRIPTION"));

        // Set Item if available
        if (rs.getObject("ITEM_ID") != null) {
            Item item = new Item();
            item.setItemId(rs.getInt("ITEM_ID"));
            freight.setItem(item);
        }

        if (rs.getObject("WEIGHT_KG") != null) {
            freight.setWeightKg(rs.getDouble("WEIGHT_KG"));
        }

        if (rs.getObject("VOLUME_M3") != null) {
            freight.setVolumeM3(rs.getDouble("VOLUME_M3"));
        }

        // Set Origin Facility if available
        if (rs.getObject("ORIGIN_FACILITY_ID") != null) {
            Facility originFacility = new Facility();
            originFacility.setFacilityId(rs.getInt("ORIGIN_FACILITY_ID"));
            freight.setOriginFacility(originFacility);
        }

        // Set Destination Facility if available
        if (rs.getObject("DESTINATION_FACILITY_ID") != null) {
            Facility destinationFacility = new Facility();
            destinationFacility.setFacilityId(rs.getInt("DESTINATION_FACILITY_ID"));
            freight.setDestinationFacility(destinationFacility);
        }

        // Set Status
        String statusStr = rs.getString("STATUS");
        if (statusStr != null) {
            try {
                freight.setStatus(FreightStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                freight.setStatus(FreightStatus.PENDING);
            }
        }

        // Set Assigned Route if available
        if (rs.getObject("ROUTE_ID") != null) {
            FreightRoute route = new FreightRoute();
            route.setRouteId(rs.getInt("ROUTE_ID"));
            freight.setAssignedRoute(route);
        }

        // Set timestamps
        Timestamp createdAtTimestamp = rs.getTimestamp("CREATED_AT");
        if (createdAtTimestamp != null) {
            freight.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        Timestamp deliveryDeadlineTimestamp = rs.getTimestamp("DELIVERY_DEADLINE");
        if (deliveryDeadlineTimestamp != null) {
            freight.setDeliveryDeadline(deliveryDeadlineTimestamp.toLocalDateTime());
        }

        freight.setSpecialHandlingInstructions(rs.getString("SPECIAL_HANDLING"));

        return freight;
    }
}

