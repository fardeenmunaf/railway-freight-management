package app.db.dao.wagon;

import app.db.DatabaseConnection;
import app.model.Wagon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WagonDAOImpl implements WagonDAO {

    private final Connection connection;

    public WagonDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Wagon wagon) throws SQLException {
        String sql = "INSERT INTO WAGON (WAGON_ID, WAGON_MODEL, OPERATOR_ID, SERVICE_YEAR, CREATED_AT) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wagon.getWagonId() == null) {
                wagon.setWagonId(getNextId());
            }

            pstmt.setInt(1, wagon.getWagonId());
            pstmt.setInt(2, wagon.getWagonModel() != null ? wagon.getWagonModel().getWagonModelId() : null);
            pstmt.setInt(3, wagon.getOperator() != null ? wagon.getOperator().getUserId() : null);
            pstmt.setInt(4, wagon.getServiceYear());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Wagon get(int id) throws SQLException {
        String sql = "SELECT * FROM WAGON WHERE WAGON_ID = ?";

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
        String sql = "SELECT * FROM WAGON ORDER BY WAGON_ID";

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
        String sql = "UPDATE WAGON SET WAGON_MODEL = ?, OPERATOR_ID = ?, SERVICE_YEAR = ?, UPDATED_AT = CURRENT_TIMESTAMP " +
                     "WHERE WAGON_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, wagon.getWagonModel() != null ? wagon.getWagonModel().getWagonModelId() : null);
            pstmt.setInt(2, wagon.getOperator() != null ? wagon.getOperator().getUserId() : null);
            pstmt.setInt(3, wagon.getServiceYear());
            pstmt.setInt(4, wagon.getWagonId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM WAGON WHERE WAGON_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Wagon> getByOperator(int operatorId) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT * FROM WAGON WHERE OPERATOR_ID = ? ORDER BY WAGON_ID";

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
    public List<Wagon> getByModel(int modelId) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT * FROM WAGON WHERE WAGON_MODEL = ? ORDER BY WAGON_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, modelId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wagons.add(mapResultSetToWagon(rs));
                }
            }
        }
        return wagons;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(WAGON_ID), 0) + 1 AS NEXT_ID FROM WAGON";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        return 1;
    }

    private Wagon mapResultSetToWagon(ResultSet rs) throws SQLException {
        return new Wagon();
    }
}

