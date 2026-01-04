package app.db.dao.wagonmodel;

import app.db.DatabaseConnection;
import app.db.dao.BaseDAOImpl;
import app.model.WagonModel;
import app.model.WagonType;
import app.model.Dimensions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for WagonModel management in the Oracle database.
 * Implements CRUD operations for the WagonModel entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonModelDAOImpl extends BaseDAOImpl implements WagonModelDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public WagonModelDAOImpl() {
        super();
    }

    @Override
    public void create(WagonModel wagonModel) throws SQLException {
        String sql = "INSERT INTO WAGON_MODEL (WAGON_MODEL_ID, NAME, WAGON_TYPE_ID, CONSTRUCTOR, " +
                     "BOGIES, NUMBER_BOGIES, TARE_WEIGHT, PAYLOAD_WEIGHT, MAX_SPEED) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wagonModel.getWagonModelId() == null) {
                wagonModel.setWagonModelId(getNextId());
            }

            pstmt.setInt(1, wagonModel.getWagonModelId());
            pstmt.setString(2, wagonModel.getName());

            if (wagonModel.getWagonType() != null && wagonModel.getWagonType().getWagonTypeId() != null) {
                pstmt.setInt(3, wagonModel.getWagonType().getWagonTypeId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, wagonModel.getConstructor());
            pstmt.setString(5, wagonModel.getBogies());

            if (wagonModel.getNumberBogies() != null) {
                pstmt.setInt(6, wagonModel.getNumberBogies());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            if (wagonModel.getTareWeight() != null) {
                pstmt.setDouble(7, wagonModel.getTareWeight());
            } else {
                pstmt.setNull(7, Types.DOUBLE);
            }

            if (wagonModel.getPayloadWeight() != null) {
                pstmt.setDouble(8, wagonModel.getPayloadWeight());
            } else {
                pstmt.setNull(8, Types.DOUBLE);
            }

            if (wagonModel.getMaxSpeed() != null) {
                pstmt.setDouble(9, wagonModel.getMaxSpeed());
            } else {
                pstmt.setNull(9, Types.DOUBLE);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public WagonModel get(int id) throws SQLException {
        String sql = "SELECT wm.WAGON_MODEL_ID, wm.NAME, wm.WAGON_TYPE_ID, wm.CONSTRUCTOR, " +
                     "wm.BOGIES, wm.NUMBER_BOGIES, wm.TARE_WEIGHT, wm.PAYLOAD_WEIGHT, wm.MAX_SPEED, " +
                     "wt.NAME AS WAGON_TYPE_NAME " +
                     "FROM WAGON_MODEL wm " +
                     "LEFT JOIN WAGON_TYPE wt ON wm.WAGON_TYPE_ID = wt.WAGON_TYPE_ID " +
                     "WHERE wm.WAGON_MODEL_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWagonModel(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<WagonModel> getAll() throws SQLException {
        List<WagonModel> wagonModels = new ArrayList<>();
        String sql = "SELECT wm.WAGON_MODEL_ID, wm.NAME, wm.WAGON_TYPE_ID, wm.CONSTRUCTOR, " +
                     "wm.BOGIES, wm.NUMBER_BOGIES, wm.TARE_WEIGHT, wm.PAYLOAD_WEIGHT, wm.MAX_SPEED, " +
                     "wt.NAME AS WAGON_TYPE_NAME " +
                     "FROM WAGON_MODEL wm " +
                     "LEFT JOIN WAGON_TYPE wt ON wm.WAGON_TYPE_ID = wt.WAGON_TYPE_ID " +
                     "ORDER BY wm.WAGON_MODEL_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                wagonModels.add(mapResultSetToWagonModel(rs));
            }
        }

        return wagonModels;
    }

    @Override
    public void update(WagonModel wagonModel) throws SQLException {
        String sql = "UPDATE WAGON_MODEL SET NAME = ?, WAGON_TYPE_ID = ?, CONSTRUCTOR = ?, " +
                     "BOGIES = ?, NUMBER_BOGIES = ?, TARE_WEIGHT = ?, PAYLOAD_WEIGHT = ?, " +
                     "MAX_SPEED = ? WHERE WAGON_MODEL_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, wagonModel.getName());

            if (wagonModel.getWagonType() != null && wagonModel.getWagonType().getWagonTypeId() != null) {
                pstmt.setInt(2, wagonModel.getWagonType().getWagonTypeId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, wagonModel.getConstructor());
            pstmt.setString(4, wagonModel.getBogies());

            if (wagonModel.getNumberBogies() != null) {
                pstmt.setInt(5, wagonModel.getNumberBogies());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            if (wagonModel.getTareWeight() != null) {
                pstmt.setDouble(6, wagonModel.getTareWeight());
            } else {
                pstmt.setNull(6, Types.DOUBLE);
            }

            if (wagonModel.getPayloadWeight() != null) {
                pstmt.setDouble(7, wagonModel.getPayloadWeight());
            } else {
                pstmt.setNull(7, Types.DOUBLE);
            }

            if (wagonModel.getMaxSpeed() != null) {
                pstmt.setDouble(8, wagonModel.getMaxSpeed());
            } else {
                pstmt.setNull(8, Types.DOUBLE);
            }

            pstmt.setInt(9, wagonModel.getWagonModelId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM WAGON_MODEL WHERE WAGON_MODEL_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<WagonModel> getByType(int wagonTypeId) throws SQLException {
        List<WagonModel> wagonModels = new ArrayList<>();
        String sql = "SELECT wm.WAGON_MODEL_ID, wm.NAME, wm.WAGON_TYPE_ID, wm.CONSTRUCTOR, " +
                     "wm.BOGIES, wm.NUMBER_BOGIES, wm.TARE_WEIGHT, wm.PAYLOAD_WEIGHT, wm.MAX_SPEED, " +
                     "wt.NAME AS WAGON_TYPE_NAME " +
                     "FROM WAGON_MODEL wm " +
                     "LEFT JOIN WAGON_TYPE wt ON wm.WAGON_TYPE_ID = wt.WAGON_TYPE_ID " +
                     "WHERE wm.WAGON_TYPE_ID = ? " +
                     "ORDER BY wm.WAGON_MODEL_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, wagonTypeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wagonModels.add(mapResultSetToWagonModel(rs));
                }
            }
        }

        return wagonModels;
    }

    @Override
    public WagonModel getByName(String name) throws SQLException {
        String sql = "SELECT wm.WAGON_MODEL_ID, wm.NAME, wm.WAGON_TYPE_ID, wm.CONSTRUCTOR, " +
                     "wm.BOGIES, wm.NUMBER_BOGIES, wm.TARE_WEIGHT, wm.PAYLOAD_WEIGHT, wm.MAX_SPEED, " +
                     "wt.NAME AS WAGON_TYPE_NAME " +
                     "FROM WAGON_MODEL wm " +
                     "LEFT JOIN WAGON_TYPE wt ON wm.WAGON_TYPE_ID = wt.WAGON_TYPE_ID " +
                     "WHERE wm.NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWagonModel(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM WAGON_MODEL WHERE WAGON_MODEL_ID = ?";

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
     * Gets the next available ID for a new WagonModel.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAGON_MODEL_ID), 0) + 1 AS NEXT_ID FROM WAGON_MODEL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a WagonModel object.
     *
     * @param rs the ResultSet containing the data
     * @return the WagonModel object
     * @throws SQLException if an error occurs during the operation
     */
    private WagonModel mapResultSetToWagonModel(ResultSet rs) throws SQLException {
        WagonModel wagonModel = new WagonModel();

        wagonModel.setWagonModelId(rs.getInt("WAGON_MODEL_ID"));
        wagonModel.setName(rs.getString("NAME"));

        // Set WagonType if available
        if (rs.getObject("WAGON_TYPE_ID") != null) {
            WagonType wagonType = new WagonType();
            wagonType.setWagonTypeId(rs.getInt("WAGON_TYPE_ID"));
            wagonType.setName(rs.getString("WAGON_TYPE_NAME"));
            wagonModel.setWagonType(wagonType);
        }

        wagonModel.setConstructor(rs.getString("CONSTRUCTOR"));
        wagonModel.setBogies(rs.getString("BOGIES"));

        if (rs.getObject("NUMBER_BOGIES") != null) {
            wagonModel.setNumberBogies(rs.getInt("NUMBER_BOGIES"));
        }

        if (rs.getObject("TARE_WEIGHT") != null) {
            wagonModel.setTareWeight(rs.getDouble("TARE_WEIGHT"));
        }

        if (rs.getObject("PAYLOAD_WEIGHT") != null) {
            wagonModel.setPayloadWeight(rs.getDouble("PAYLOAD_WEIGHT"));
        }

        if (rs.getObject("MAX_SPEED") != null) {
            wagonModel.setMaxSpeed(rs.getDouble("MAX_SPEED"));
        }

        // Note: Dimensions are not stored in this table, set to null
        wagonModel.setDimensions(null);

        return wagonModel;
    }
}
