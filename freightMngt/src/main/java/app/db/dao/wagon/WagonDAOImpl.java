package app.db.dao.wagon;

import app.db.DatabaseConnection;
import app.model.Wagon;
import app.model.WagonModel;
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
public class WagonDAOImpl implements WagonDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public WagonDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Wagon wagon) throws SQLException {
        String sql = "INSERT INTO WAGON (WAGON_ID, WAGON_MODEL_ID, OPERATOR_ID, SERVICE_YEAR) " +
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
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL_ID, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM WAGON w " +
                     "LEFT JOIN WAGON_MODEL wm ON w.WAGON_MODEL_ID = wm.WAGON_MODEL_ID " +
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
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL_ID, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM WAGON w " +
                     "LEFT JOIN WAGON_MODEL wm ON w.WAGON_MODEL_ID = wm.WAGON_MODEL_ID " +
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
        String sql = "UPDATE WAGON SET WAGON_MODEL_ID = ?, OPERATOR_ID = ?, SERVICE_YEAR = ? " +
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
        String sql = "DELETE FROM WAGON WHERE WAGON_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Wagon> getByModel(int wagonModelId) throws SQLException {
        List<Wagon> wagons = new ArrayList<>();
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL_ID, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM WAGON w " +
                     "LEFT JOIN WAGON_MODEL wm ON w.WAGON_MODEL_ID = wm.WAGON_MODEL_ID " +
                     "WHERE w.WAGON_MODEL_ID = ? " +
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
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL_ID, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM WAGON w " +
                     "LEFT JOIN WAGON_MODEL wm ON w.WAGON_MODEL_ID = wm.WAGON_MODEL_ID " +
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
        String sql = "SELECT w.WAGON_ID, w.WAGON_MODEL_ID, w.OPERATOR_ID, w.SERVICE_YEAR, " +
                     "wm.NAME AS MODEL_NAME " +
                     "FROM WAGON w " +
                     "LEFT JOIN WAGON_MODEL wm ON w.WAGON_MODEL_ID = wm.WAGON_MODEL_ID " +
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
        String sql = "SELECT COUNT(*) AS COUNT FROM WAGON WHERE WAGON_ID = ?";

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
     * Gets the next available ID for a new Wagon.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
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
        if (rs.getObject("WAGON_MODEL_ID") != null) {
            WagonModel model = new WagonModel();
            model.setWagonModelId(rs.getInt("WAGON_MODEL_ID"));
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
}

