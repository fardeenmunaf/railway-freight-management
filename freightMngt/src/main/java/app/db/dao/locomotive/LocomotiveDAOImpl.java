package app.db.dao.locomotive;

import app.db.DatabaseConnection;
import app.model.Locomotive;
import app.model.LocomotiveType;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocomotiveDAOImpl implements LocomotiveDAO {

    private final Connection connection;

    public LocomotiveDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Locomotive locomotive) throws SQLException {
        String sql = "INSERT INTO LOCOMOTIVE (LOCOMOTIVE_ID, OPERATOR_ID, LOCOMOTIVE_TYPE, NAME, BRAND, MODEL, " +
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
        String sql = "SELECT * FROM LOCOMOTIVE WHERE LOCOMOTIVE_ID = ?";

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
        String sql = "SELECT * FROM LOCOMOTIVE ORDER BY LOCOMOTIVE_ID";

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
        String sql = "UPDATE LOCOMOTIVE SET OPERATOR_ID = ?, LOCOMOTIVE_TYPE = ?, NAME = ?, BRAND = ?, MODEL = ?, " +
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
        String sql = "DELETE FROM LOCOMOTIVE WHERE LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Locomotive> getByOperator(int operatorId) throws SQLException {
        List<Locomotive> locomotives = new ArrayList<>();
        String sql = "SELECT * FROM LOCOMOTIVE WHERE OPERATOR_ID = ? ORDER BY LOCOMOTIVE_ID";

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
        String sql = "SELECT * FROM LOCOMOTIVE WHERE LOCOMOTIVE_TYPE = ? ORDER BY LOCOMOTIVE_ID";

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

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LOCOMOTIVE_ID), 0) + 1 AS NEXT_ID FROM LOCOMOTIVE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    private Locomotive mapResultSetToLocomotive(ResultSet rs) throws SQLException {
        // Simplificado - retorna apenas com campos básicos
        return new Locomotive();
    }
}

