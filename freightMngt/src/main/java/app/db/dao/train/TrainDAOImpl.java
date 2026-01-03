package app.db.dao.train;

import app.db.dao.BaseDAOImpl;
import app.model.Train;
import app.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for Train management in the Oracle database.
 * Implements CRUD operations for the Train entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDAOImpl extends BaseDAOImpl implements TrainDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public TrainDAOImpl() {
        super();
    }

    @Override
    public void create(Train train) throws SQLException {
        String sql = "INSERT INTO " + getTableName("TRAIN") + " (TRAIN_ID, CODE, DEPARTURE, DESTINATION, OPERATOR_ID) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (train.getTrainId() == null) {
                train.setTrainId(getNextId());
            }

            pstmt.setInt(1, train.getTrainId());

            if (train.getCode() != null) {
                pstmt.setInt(2, train.getCode());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, train.getDeparture());
            pstmt.setString(4, train.getDestination());

            if (train.getOperator() != null && train.getOperator().getCompanyId() != null) {
                pstmt.setInt(5, train.getOperator().getCompanyId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public Train get(int id) throws SQLException {
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, t.OPERATOR_ID, " +
                     "c.NAME AS OPERATOR_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM " + getTableName("TRAIN") + " t " +
                     "LEFT JOIN COMPANY c ON t.OPERATOR_ID = c.COMPANY_ID " +
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
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, t.OPERATOR_ID, " +
                     "c.NAME AS OPERATOR_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM " + getTableName("TRAIN") + " t " +
                     "LEFT JOIN COMPANY c ON t.OPERATOR_ID = c.COMPANY_ID " +
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
        String sql = "UPDATE " + getTableName("TRAIN") + " SET CODE = ?, DEPARTURE = ?, DESTINATION = ?, OPERATOR_ID = ? " +
                     "WHERE TRAIN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (train.getCode() != null) {
                pstmt.setInt(1, train.getCode());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, train.getDeparture());
            pstmt.setString(3, train.getDestination());

            if (train.getOperator() != null && train.getOperator().getCompanyId() != null) {
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
        String sql = "DELETE FROM " + getTableName("TRAIN") + " WHERE TRAIN_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Train getByCode(int code) throws SQLException {
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, t.OPERATOR_ID, " +
                     "c.NAME AS OPERATOR_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM " + getTableName("TRAIN") + " t " +
                     "LEFT JOIN COMPANY c ON t.OPERATOR_ID = c.COMPANY_ID " +
                     "WHERE t.CODE = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, code);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrain(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Train> getByOperator(int operatorId) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, t.OPERATOR_ID, " +
                     "c.NAME AS OPERATOR_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM " + getTableName("TRAIN") + " t " +
                     "LEFT JOIN COMPANY c ON t.OPERATOR_ID = c.COMPANY_ID " +
                     "WHERE t.OPERATOR_ID = ? " +
                     "ORDER BY t.TRAIN_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, operatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapResultSetToTrain(rs));
                }
            }
        }

        return trains;
    }

    @Override
    public List<Train> getByDeparture(String departure) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, t.OPERATOR_ID, " +
                     "c.NAME AS OPERATOR_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM " + getTableName("TRAIN") + " t " +
                     "LEFT JOIN COMPANY c ON t.OPERATOR_ID = c.COMPANY_ID " +
                     "WHERE t.DEPARTURE = ? " +
                     "ORDER BY t.TRAIN_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, departure);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapResultSetToTrain(rs));
                }
            }
        }

        return trains;
    }

    @Override
    public List<Train> getByDestination(String destination) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT t.TRAIN_ID, t.CODE, t.DEPARTURE, t.DESTINATION, t.OPERATOR_ID, " +
                     "c.NAME AS OPERATOR_NAME, c.SHORT_NAME, c.VAT " +
                     "FROM " + getTableName("TRAIN") + " t " +
                     "LEFT JOIN COMPANY c ON t.OPERATOR_ID = c.COMPANY_ID " +
                     "WHERE t.DESTINATION = ? " +
                     "ORDER BY t.TRAIN_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, destination);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapResultSetToTrain(rs));
                }
            }
        }

        return trains;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("TRAIN") + " WHERE TRAIN_ID = ?";

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

    public int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(TRAIN_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("TRAIN");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Train object.
     *
     * @param rs the ResultSet containing the data
     * @return the Train object
     * @throws SQLException if an error occurs during the operation
     */
    private Train mapResultSetToTrain(ResultSet rs) throws SQLException {
        Train train = new Train();

        train.setTrainId(rs.getInt("TRAIN_ID"));

        if (rs.getObject("CODE") != null) {
            train.setCode(rs.getInt("CODE"));
        }

        train.setDeparture(rs.getString("DEPARTURE"));
        train.setDestination(rs.getString("DESTINATION"));

        // Set Operator (Company) if available
        if (rs.getObject("OPERATOR_ID") != null) {
            Company operator = new Company();
            operator.setCompanyId(rs.getInt("OPERATOR_ID"));
            operator.setName(rs.getString("OPERATOR_NAME"));
            operator.setShortName(rs.getString("SHORT_NAME"));
            operator.setVat(rs.getString("VAT"));
            train.setOperatorId(operator);
        }

        return train;
    }
}
