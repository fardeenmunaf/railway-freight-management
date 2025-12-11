package app.db.dao.wagonmodel;

import app.db.DatabaseConnection;
import app.model.WagonModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WagonModelDAOImpl implements WagonModelDAO {

    private final Connection connection;

    public WagonModelDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(WagonModel wagonModel) throws SQLException {
        String sql = "INSERT INTO WAGON_MODEL (WAGON_MODEL_ID, NAME, WAGON_TYPE, CONSTRUCTOR, NUMBER_BOGIES, " +
                     "BOGIES, TARE_T, PAYLOAD_T, VOLUME_M3, LENGTH_M, WIDTH_M, HEIGHT_M, MAX_SPEED_KMH) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wagonModel.getWagonModelId() == null) {
                wagonModel.setWagonModelId(getNextId());
            }

            pstmt.setInt(1, wagonModel.getWagonModelId());
            pstmt.setString(2, wagonModel.getName());
            pstmt.setInt(3, wagonModel.getWagonType() != null ? wagonModel.getWagonType().getWagonTypeId() : null);
            pstmt.setString(4, wagonModel.getConstructor());
            pstmt.setInt(5, wagonModel.getNumberBogies());
            pstmt.setString(6, wagonModel.getBogies());
            pstmt.setDouble(7, wagonModel.getTareT());
            pstmt.setDouble(8, wagonModel.getPayloadT());
            pstmt.setDouble(9, wagonModel.getVolumeM3());
            pstmt.setDouble(10, wagonModel.getLengthM());
            pstmt.setDouble(11, wagonModel.getWidthM());
            pstmt.setDouble(12, wagonModel.getHeightM());
            pstmt.setDouble(13, wagonModel.getMaxSpeedKmh());

            pstmt.executeUpdate();
        }
    }

    @Override
    public WagonModel get(int id) throws SQLException {
        String sql = "SELECT * FROM WAGON_MODEL WHERE WAGON_MODEL_ID = ?";

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
        List<WagonModel> models = new ArrayList<>();
        String sql = "SELECT * FROM WAGON_MODEL ORDER BY WAGON_MODEL_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                models.add(mapResultSetToWagonModel(rs));
            }
        }
        return models;
    }

    @Override
    public void update(WagonModel wagonModel) throws SQLException {
        String sql = "UPDATE WAGON_MODEL SET NAME = ?, WAGON_TYPE = ?, CONSTRUCTOR = ? WHERE WAGON_MODEL_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, wagonModel.getName());
            pstmt.setInt(2, wagonModel.getWagonType() != null ? wagonModel.getWagonType().getWagonTypeId() : null);
            pstmt.setString(3, wagonModel.getConstructor());
            pstmt.setInt(4, wagonModel.getWagonModelId());

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
        List<WagonModel> models = new ArrayList<>();
        String sql = "SELECT * FROM WAGON_MODEL WHERE WAGON_TYPE = ? ORDER BY WAGON_MODEL_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, wagonTypeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    models.add(mapResultSetToWagonModel(rs));
                }
            }
        }
        return models;
    }

    @Override
    public WagonModel getByName(String name) throws SQLException {
        String sql = "SELECT * FROM WAGON_MODEL WHERE NAME = ?";

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

    private WagonModel mapResultSetToWagonModel(ResultSet rs) throws SQLException {
        return new WagonModel();
    }
}

