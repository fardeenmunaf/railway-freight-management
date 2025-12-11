package app.db.dao.train;

import app.db.DatabaseConnection;
import app.model.Company;
import app.model.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao DAO para gestao de Train na base de dados Oracle.
 * Implementa operacoes CRUD para a entidade Train.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDAOImpl implements TrainDAO {

    private final Connection connection;

    public TrainDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Train train) throws SQLException {
        String sql = "INSERT INTO TRAIN (TRAIN_ID, CODE, DEPARTURE, DESTINATION, COMPANY_ID) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (train.getTrainId() == null) {
                train.setTrainId(getNextId());
            }

            pstmt.setInt(1, train.getTrainId());
            pstmt.setInt(2, train.getCode());
            pstmt.setString(3, train.getDeparture());
            pstmt.setString(4, train.getDestination());

            if (train.getOperator() != null) {
                pstmt.setInt(5, train.getOperator().getCompanyId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public Train get(int id) throws SQLException {
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, " +
                     "c.COMPANY_ID, c.NAME, c.SHORT_NAME, c.VAT " +
                     "FROM TRAIN t " +
                     "LEFT JOIN COMPANY c ON t.COMPANY_ID = c.COMPANY_ID " +
                     "WHERE t.TRAIN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrain(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Train> getAll() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, " +
                     "c.COMPANY_ID, c.NAME, c.SHORT_NAME, c.VAT " +
                     "FROM TRAIN t " +
                     "LEFT JOIN COMPANY c ON t.COMPANY_ID = c.COMPANY_ID " +
                     "ORDER BY t.TRAIN_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trains.add(mapResultSetToTrain(rs));
            }
        }

        return trains;
    }

    @Override
    public void update(Train train) throws SQLException {
        String sql = "UPDATE TRAIN SET CODE = ?, DEPARTURE = ?, DESTINATION = ?, COMPANY_ID = ? WHERE TRAIN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, train.getCode());
            pstmt.setString(2, train.getDeparture());
            pstmt.setString(3, train.getDestination());

            if (train.getOperator() != null) {
                pstmt.setInt(4, train.getOperator().getCompanyId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, train.getTrainId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM TRAIN WHERE TRAIN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Train> getByOperator(int companyId) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, " +
                     "c.COMPANY_ID, c.NAME, c.SHORT_NAME, c.VAT " +
                     "FROM TRAIN t " +
                     "LEFT JOIN COMPANY c ON t.COMPANY_ID = c.COMPANY_ID " +
                     "WHERE t.COMPANY_ID = ? " +
                     "ORDER BY t.TRAIN_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapResultSetToTrain(rs));
                }
            }
        }

        return trains;
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(TRAIN_ID), 0) + 1 AS NEXT_ID FROM TRAIN";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    private Train mapResultSetToTrain(ResultSet rs) throws SQLException {
        Company operator = null;
        if (rs.getObject("COMPANY_ID") != null) {
            operator = new Company(
                    rs.getInt("COMPANY_ID"),
                    rs.getString("NAME"),
                    rs.getString("SHORT_NAME"),
                    rs.getString("VAT")
            );
        }

        return new Train(
                rs.getInt("TRAIN_ID"),
                Integer.parseInt(rs.getString("CODE")),
                String.valueOf(rs.getInt("DEPARTURE")),
                String.valueOf(rs.getInt("DESTINATION")),
                operator
        );
    }
}

