package app.db.dao.trainstop;

import app.db.DatabaseConnection;
import app.model.TrainStop;
import app.model.TrainService;
import app.model.Facility;
import app.model.ParkingTrack;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for TrainStop management in the Oracle database.
 * Implements CRUD operations for the TrainStop entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainStopDAOImpl implements TrainStopDAO {

    private final Connection connection;

    /**
     * Constructor that initializes the database connection.
     */
    public TrainStopDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(TrainStop stop) throws SQLException {
        String sql = "INSERT INTO TRAIN_STOP (TRAIN_STOP_ID, SERVICE_ID, FACILITY_ID, PARKING_TRACK_ID, " +
                     "POSITION, SCHED_ARR, SCHED_DEP, PLANNED_STOP_TIME, ACT_ARR, ACT_DEP, ACT_STOP_TIME) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (stop.getTrainStopId() == null) {
                stop.setTrainStopId(getNextId());
            }

            pstmt.setInt(1, stop.getTrainStopId());

            if (stop.getService() != null && stop.getService().getServiceId() != null) {
                pstmt.setInt(2, stop.getService().getServiceId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (stop.getFacility() != null && stop.getFacility().getFacilityId() != null) {
                pstmt.setInt(3, stop.getFacility().getFacilityId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (stop.getParkingTrack() != null && stop.getParkingTrack().getParkingTrackId() != null) {
                pstmt.setInt(4, stop.getParkingTrack().getParkingTrackId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            if (stop.getPosition() != null) {
                pstmt.setInt(5, stop.getPosition());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            if (stop.getSchedArr() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(stop.getSchedArr()));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }

            if (stop.getSchedDep() != null) {
                pstmt.setTimestamp(7, Timestamp.valueOf(stop.getSchedDep()));
            } else {
                pstmt.setNull(7, Types.TIMESTAMP);
            }

            if (stop.getPlannedStopTime() != null) {
                pstmt.setInt(8, stop.getPlannedStopTime());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            if (stop.getActArr() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(stop.getActArr()));
            } else {
                pstmt.setNull(9, Types.TIMESTAMP);
            }

            if (stop.getActDep() != null) {
                pstmt.setTimestamp(10, Timestamp.valueOf(stop.getActDep()));
            } else {
                pstmt.setNull(10, Types.TIMESTAMP);
            }

            if (stop.getActStopTime() != null) {
                pstmt.setInt(11, stop.getActStopTime());
            } else {
                pstmt.setNull(11, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public TrainStop get(int id) throws SQLException {
        String sql = "SELECT * FROM TRAIN_STOP WHERE TRAIN_STOP_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrainStop(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<TrainStop> getAll() throws SQLException {
        List<TrainStop> stops = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_STOP ORDER BY SERVICE_ID, POSITION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                stops.add(mapResultSetToTrainStop(rs));
            }
        }

        return stops;
    }

    @Override
    public void update(TrainStop stop) throws SQLException {
        String sql = "UPDATE TRAIN_STOP SET SERVICE_ID = ?, FACILITY_ID = ?, PARKING_TRACK_ID = ?, " +
                     "POSITION = ?, SCHED_ARR = ?, SCHED_DEP = ?, PLANNED_STOP_TIME = ?, " +
                     "ACT_ARR = ?, ACT_DEP = ?, ACT_STOP_TIME = ? WHERE TRAIN_STOP_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (stop.getService() != null && stop.getService().getServiceId() != null) {
                pstmt.setInt(1, stop.getService().getServiceId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (stop.getFacility() != null && stop.getFacility().getFacilityId() != null) {
                pstmt.setInt(2, stop.getFacility().getFacilityId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (stop.getParkingTrack() != null && stop.getParkingTrack().getParkingTrackId() != null) {
                pstmt.setInt(3, stop.getParkingTrack().getParkingTrackId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (stop.getPosition() != null) {
                pstmt.setInt(4, stop.getPosition());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            if (stop.getSchedArr() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(stop.getSchedArr()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            if (stop.getSchedDep() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(stop.getSchedDep()));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }

            if (stop.getPlannedStopTime() != null) {
                pstmt.setInt(7, stop.getPlannedStopTime());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            if (stop.getActArr() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(stop.getActArr()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }

            if (stop.getActDep() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(stop.getActDep()));
            } else {
                pstmt.setNull(9, Types.TIMESTAMP);
            }

            if (stop.getActStopTime() != null) {
                pstmt.setInt(10, stop.getActStopTime());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setInt(11, stop.getTrainStopId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM TRAIN_STOP WHERE TRAIN_STOP_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<TrainStop> getByService(int serviceId) throws SQLException {
        List<TrainStop> stops = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_STOP WHERE SERVICE_ID = ? ORDER BY POSITION";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(mapResultSetToTrainStop(rs));
                }
            }
        }

        return stops;
    }

    @Override
    public List<TrainStop> getByFacility(int facilityId) throws SQLException {
        List<TrainStop> stops = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_STOP WHERE FACILITY_ID = ? ORDER BY SCHED_ARR";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, facilityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(mapResultSetToTrainStop(rs));
                }
            }
        }

        return stops;
    }

    @Override
    public List<TrainStop> getByParkingTrack(int parkingTrackId) throws SQLException {
        List<TrainStop> stops = new ArrayList<>();
        String sql = "SELECT * FROM TRAIN_STOP WHERE PARKING_TRACK_ID = ? ORDER BY SCHED_ARR";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, parkingTrackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(mapResultSetToTrainStop(rs));
                }
            }
        }

        return stops;
    }

    @Override
    public void deleteByService(int serviceId) throws SQLException {
        String sql = "DELETE FROM TRAIN_STOP WHERE SERVICE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM TRAIN_STOP WHERE TRAIN_STOP_ID = ?";

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
     * Gets the next available ID for a new TrainStop.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(TRAIN_STOP_ID), 0) + 1 AS NEXT_ID FROM TRAIN_STOP";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a TrainStop object.
     *
     * @param rs the ResultSet containing the data
     * @return the TrainStop object
     * @throws SQLException if an error occurs during the operation
     */
    private TrainStop mapResultSetToTrainStop(ResultSet rs) throws SQLException {
        TrainStop stop = new TrainStop();

        stop.setTrainStopId(rs.getInt("TRAIN_STOP_ID"));

        // Set TrainService if available
        if (rs.getObject("SERVICE_ID") != null) {
            TrainService service = new TrainService();
            service.setServiceId(rs.getInt("SERVICE_ID"));
            stop.setService(service);
        }

        // Set Facility if available
        if (rs.getObject("FACILITY_ID") != null) {
            Facility facility = new Facility();
            facility.setFacilityId(rs.getInt("FACILITY_ID"));
            stop.setFacility(facility);
        }

        // Set ParkingTrack if available
        if (rs.getObject("PARKING_TRACK_ID") != null) {
            ParkingTrack parkingTrack = new ParkingTrack();
            parkingTrack.setParkingTrackId(rs.getInt("PARKING_TRACK_ID"));
            stop.setParkingTrack(parkingTrack);
        }

        if (rs.getObject("POSITION") != null) {
            stop.setPosition(rs.getInt("POSITION"));
        }

        if (rs.getObject("SCHED_ARR") != null) {
            Timestamp timestamp = rs.getTimestamp("SCHED_ARR");
            stop.setSchedArr(timestamp.toLocalDateTime());
        }

        if (rs.getObject("SCHED_DEP") != null) {
            Timestamp timestamp = rs.getTimestamp("SCHED_DEP");
            stop.setSchedDep(timestamp.toLocalDateTime());
        }

        if (rs.getObject("PLANNED_STOP_TIME") != null) {
            stop.setPlannedStopTime(rs.getInt("PLANNED_STOP_TIME"));
        }

        if (rs.getObject("ACT_ARR") != null) {
            Timestamp timestamp = rs.getTimestamp("ACT_ARR");
            stop.setActArr(timestamp.toLocalDateTime());
        }

        if (rs.getObject("ACT_DEP") != null) {
            Timestamp timestamp = rs.getTimestamp("ACT_DEP");
            stop.setActDep(timestamp.toLocalDateTime());
        }

        if (rs.getObject("ACT_STOP_TIME") != null) {
            stop.setActStopTime(rs.getInt("ACT_STOP_TIME"));
        }

        return stop;
    }
}

