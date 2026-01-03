package app.db.dao.trainservice;

import app.db.DatabaseConnection;
import app.model.TrainService;
import app.model.Train;
import app.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for TrainService management in the Oracle database.
 * Implements CRUD operations for the TrainService entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainServiceDAOImpl implements TrainServiceDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public TrainServiceDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(TrainService service) throws SQLException {
        String sql = "INSERT INTO TRAIN_SERVICE (SERVICE_ID, TRAIN_ID, DEPARTURE_DATE, ARRIVAL_DATE, " +
                     "TRAIN_DRIVER_ID, STATUS) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (service.getServiceId() == null) {
                service.setServiceId(getNextId());
            }

            pstmt.setInt(1, service.getServiceId());

            if (service.getTrain() != null && service.getTrain().getTrainId() != null) {
                pstmt.setInt(2, service.getTrain().getTrainId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (service.getDepartureDate() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(service.getDepartureDate()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            if (service.getArrivalDate() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(service.getArrivalDate()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            if (service.getTrainDriver() != null && service.getTrainDriver().getUserId() != null) {
                pstmt.setInt(5, service.getTrainDriver().getUserId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setString(6, service.getStatus());

            pstmt.executeUpdate();
        }
    }

    @Override
    public TrainService get(int id) throws SQLException {
        String sql = "SELECT * FROM TRAIN_SERVICE WHERE SERVICE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrainService(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<TrainService> getAll() throws SQLException {
        List<TrainService> services = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_SERVICE ORDER BY DEPARTURE_DATE DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapResultSetToTrainService(rs));
            }
        }

        return services;
    }

    @Override
    public void update(TrainService service) throws SQLException {
        String sql = "UPDATE TRAIN_SERVICE SET TRAIN_ID = ?, DEPARTURE_DATE = ?, ARRIVAL_DATE = ?, " +
                     "TRAIN_DRIVER_ID = ?, STATUS = ? WHERE SERVICE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (service.getTrain() != null && service.getTrain().getTrainId() != null) {
                pstmt.setInt(1, service.getTrain().getTrainId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (service.getDepartureDate() != null) {
                pstmt.setTimestamp(2, Timestamp.valueOf(service.getDepartureDate()));
            } else {
                pstmt.setNull(2, Types.TIMESTAMP);
            }

            if (service.getArrivalDate() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(service.getArrivalDate()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            if (service.getTrainDriver() != null && service.getTrainDriver().getUserId() != null) {
                pstmt.setInt(4, service.getTrainDriver().getUserId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, service.getStatus());
            pstmt.setInt(6, service.getServiceId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM TRAIN_SERVICE WHERE SERVICE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<TrainService> getByTrain(int trainId) throws SQLException {
        List<TrainService> services = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_SERVICE WHERE TRAIN_ID = ? ORDER BY DEPARTURE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, trainId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToTrainService(rs));
                }
            }
        }

        return services;
    }

    @Override
    public List<TrainService> getByStatus(String status) throws SQLException {
        List<TrainService> services = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_SERVICE WHERE STATUS = ? ORDER BY DEPARTURE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToTrainService(rs));
                }
            }
        }

        return services;
    }

    @Override
    public List<TrainService> getByDriver(int driverId) throws SQLException {
        List<TrainService> services = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_SERVICE WHERE TRAIN_DRIVER_ID = ? ORDER BY DEPARTURE_DATE DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToTrainService(rs));
                }
            }
        }

        return services;
    }

    @Override
    public List<TrainService> getInTransit() throws SQLException {
        List<TrainService> services = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_SERVICE WHERE STATUS = 'IN_TRANSIT' ORDER BY DEPARTURE_DATE DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                services.add(mapResultSetToTrainService(rs));
            }
        }

        return services;
    }

    @Override
    public List<TrainService> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<TrainService> services = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_SERVICE WHERE DEPARTURE_DATE BETWEEN ? AND ? ORDER BY DEPARTURE_DATE";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToTrainService(rs));
                }
            }
        }

        return services;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM TRAIN_SERVICE WHERE SERVICE_ID = ?";

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
     * Gets the next available ID for a new TrainService.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(SERVICE_ID), 0) + 1 AS NEXT_ID FROM TRAIN_SERVICE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a TrainService object.
     *
     * @param rs the ResultSet containing the data
     * @return the TrainService object
     * @throws SQLException if an error occurs during the operation
     */
    private TrainService mapResultSetToTrainService(ResultSet rs) throws SQLException {
        TrainService service = new TrainService();

        service.setServiceId(rs.getInt("SERVICE_ID"));

        // Set Train if available
        if (rs.getObject("TRAIN_ID") != null) {
            Train train = new Train();
            train.setTrainId(rs.getInt("TRAIN_ID"));
            service.setTrain(train);
        }

        if (rs.getObject("DEPARTURE_DATE") != null) {
            Timestamp timestamp = rs.getTimestamp("DEPARTURE_DATE");
            service.setDepartureDate(timestamp.toLocalDateTime());
        }

        if (rs.getObject("ARRIVAL_DATE") != null) {
            Timestamp timestamp = rs.getTimestamp("ARRIVAL_DATE");
            service.setArrivalDate(timestamp.toLocalDateTime());
        }

        // Set Train Driver if available
        if (rs.getObject("TRAIN_DRIVER_ID") != null) {
            User driver = new User();
            driver.setUserId(rs.getInt("TRAIN_DRIVER_ID"));
            service.setTrainDriver(driver);
        }

        service.setStatus(rs.getString("STATUS"));

        return service;
    }
}

