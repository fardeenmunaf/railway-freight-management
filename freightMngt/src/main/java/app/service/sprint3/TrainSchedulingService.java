package app.service.sprint3;

import app.model.*;
import app.db.DatabaseConnection;
import app.db.dao.trainservice.TrainServiceDAO;
import app.db.dao.trainservice.TrainServiceDAOImpl;
import app.db.dao.trainstop.TrainStopDAO;
import app.db.dao.trainstop.TrainStopDAOImpl;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para persistir/carregar TrainDispatch usando o esquema APPUSER (TRAIN_SERVICE / TRAIN_STOP).
 * Uso: instanciar com URL JDBC, user e password, ou criar DB-backed instance via new TrainSchedulingService(true).
 * Métodos principais: saveDispatch, loadDispatch. Também contém scheduleDispatch in-memory.
 */
public class TrainSchedulingService {

    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPassword;

    // DB-backed DAOs and connection (optional)
    private TrainServiceDAO trainServiceDAO;
    private TrainStopDAO trainStopDAO;
    private Connection connection;

    public TrainSchedulingService(String jdbcUrl, String dbUser, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    // No-arg constructor used by tests and view for in-memory scheduling logic
    public TrainSchedulingService() {
        this.jdbcUrl = null;
        this.dbUser = null;
        this.dbPassword = null;
        this.trainServiceDAO = null;
        this.trainStopDAO = null;
        this.connection = null;
    }

    /**
     * Create a DB-backed TrainSchedulingService that uses the same DAO pattern as TrainAssemblyService.
     * This constructor initializes DAO implementations and uses DatabaseConnection singleton.
     */
    public TrainSchedulingService(boolean useDb) {
        this.jdbcUrl = null;
        this.dbUser = null;
        this.dbPassword = null;
        if (useDb) {
            this.trainServiceDAO = new TrainServiceDAOImpl();
            this.trainStopDAO = new TrainStopDAOImpl();
            this.connection = DatabaseConnection.getInstance().getConnection();
        } else {
            this.trainServiceDAO = null;
            this.trainStopDAO = null;
            this.connection = null;
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection != null) return connection;
        if (jdbcUrl == null) throw new IllegalStateException("No JDBC configuration provided");
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }

    /**
     * Persist dispatch using DAO-based approach (if initialized) or fallback to JDBC logic.
     */
    public void saveDispatch(TrainDispatch dispatch) throws SQLException {
        if (dispatch == null || dispatch.getTrainService() == null) {
            throw new IllegalArgumentException("dispatch ou trainService não pode ser null");
        }

        TrainService svc = dispatch.getTrainService();

        // Use DAO-backed flow when DAOs are present
        if (trainServiceDAO != null && trainStopDAO != null) {
            // Create or update train service
            if (svc.getServiceId() == null) {
                trainServiceDAO.create(svc);
            } else {
                trainServiceDAO.update(svc);
            }

            // Remove existing stops
            trainStopDAO.deleteByService(svc.getServiceId());

            // Insert new stops mapping RoutePoint -> TrainStop
            if (dispatch.getRoute() != null) {
                for (RoutePoint rp : dispatch.getRoute()) {
                    TrainStop ts = new TrainStop();
                    ts.setService(svc);
                    if (rp.getFacility() != null) {
                        Facility f = new Facility();
                        f.setFacilityId(rp.getFacility().getFacilityId());
                        ts.setFacility(f);
                    }
                    // parking track: left null (DAO implementation can handle default)
                    ts.setParkingTrack(null);
                    ts.setPosition(rp.getSequenceOrder() != null ? rp.getSequenceOrder() : 0);
                    ts.setSchedArr(rp.getEstimatedArrivalTime());
                    ts.setSchedDep(rp.getEstimatedDepartureTime());
                    ts.setPlannedStopTime(rp.getExpectedWaitTimeMinutes());
                    trainStopDAO.create(ts);
                }
            }
            return;
        }

        // Fallback: original JDBC implementation that requires explicit JDBC url/user/password
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer svcId = svc.getServiceId();
                if (svcId == null) {
                    // insert train_service
                    String insSvc = "INSERT INTO TRAIN_SERVICE (train_id, departure_date, arrival_date, train_driver_id, status) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insSvc, new String[]{"SERVICE_ID"})) {
                        ps.setObject(1, svc.getTrain() != null ? svc.getTrain().getTrainId() : null);
                        ps.setTimestamp(2, toTimestamp(svc.getDepartureDate()));
                        ps.setTimestamp(3, toTimestamp(svc.getArrivalDate()));
                        ps.setObject(4, svc.getTrainDriver() != null ? svc.getTrainDriver().getUserId() : null);
                        ps.setString(5, svc.getStatus());
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) {
                                svcId = keys.getInt(1);
                                svc.setServiceId(svcId);
                            }
                        }
                    }
                } else {
                    // update existing
                    String updSvc = "UPDATE TRAIN_SERVICE SET train_id = ?, departure_date = ?, arrival_date = ?, train_driver_id = ?, status = ? WHERE service_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updSvc)) {
                        ps.setObject(1, svc.getTrain() != null ? svc.getTrain().getTrainId() : null);
                        ps.setTimestamp(2, toTimestamp(svc.getDepartureDate()));
                        ps.setTimestamp(3, toTimestamp(svc.getArrivalDate()));
                        ps.setObject(4, svc.getTrainDriver() != null ? svc.getTrainDriver().getUserId() : null);
                        ps.setString(5, svc.getStatus());
                        ps.setInt(6, svcId);
                        ps.executeUpdate();
                    }
                }

                // remove existing stops for this service to simplify sync
                String delStops = "DELETE FROM TRAIN_STOP WHERE service_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(delStops)) {
                    ps.setInt(1, svc.getServiceId());
                    ps.executeUpdate();
                }

                // insert stops (route points)
                if (dispatch.getRoute() != null && !dispatch.getRoute().isEmpty()) {
                    String insStop = "INSERT INTO TRAIN_STOP (service_id, facility_id, parking_track, position, sched_arr, sched_dep, planned_stop_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insStop)) {
                        for (RoutePoint rp : dispatch.getRoute()) {
                            ps.setInt(1, svc.getServiceId());
                            ps.setObject(2, rp.getFacility() != null ? rp.getFacility().getFacilityId() : null);
                            ps.setInt(3, 0); // parking_track: se tiver lógica, ajustar
                            ps.setInt(4, rp.getSequenceOrder() != null ? rp.getSequenceOrder() : 0);
                            ps.setTimestamp(5, toTimestamp(rp.getEstimatedArrivalTime()));
                            ps.setTimestamp(6, toTimestamp(rp.getEstimatedDepartureTime()));
                            ps.setObject(7, rp.getExpectedWaitTimeMinutes() != null ? rp.getExpectedWaitTimeMinutes() : null);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Lightweight in-memory scheduler used by tests and the interactive view.
     * Builds a TrainDispatch from either a manual route (list of RoutePoint) or
     * a FreightRoute (automatic) and performs simple conflict detection for
     * single-track segments.
     */
    public TrainDispatch scheduleDispatch(int dispatchId,
                                          TrainService trainService,
                                          FreightRoute freightRoute,
                                          LocalDateTime plannedDeparture,
                                          String routeType,
                                          List<RoutePoint> manualRoute,
                                          List<TrainDispatch> existingDispatches) {
        if (trainService == null) throw new IllegalArgumentException("trainService is required");

        TrainDispatch dispatch = new TrainDispatch();
        dispatch.setDispatchId(dispatchId);
        dispatch.setTrainService(trainService);
        dispatch.setPlannedDepartureTime(plannedDeparture);
        dispatch.setRouteType(routeType);

        List<RoutePoint> route = new ArrayList<>();

        if ("AUTOMATIC".equalsIgnoreCase(routeType) && freightRoute != null) {
            // build route points from freightRoute segments (start + intermediates + end)
            List<FreightRouteSegment> segs = new ArrayList<>(freightRoute.getRouteSegments());
            segs.sort((a, b) -> Integer.compare(a.getSequenceOrder(), b.getSequenceOrder()));
            if (!segs.isEmpty()) {
                // start
                FreightRouteSegment first = segs.get(0);
                RoutePoint start = new RoutePoint();
                start.setFacility(first.getFromFacility());
                start.setSequenceOrder(1);
                start.setSegmentToNext(first.getLineSegment());
                route.add(start);
                int seq = 2;
                for (FreightRouteSegment s : segs) {
                    RoutePoint rp = new RoutePoint();
                    rp.setFacility(s.getToFacility());
                    rp.setSequenceOrder(seq++);
                    // next segment for this point is the next segment in list or null
                    rp.setSegmentToNext(null);
                    route.add(rp);
                }
            }
        } else if (manualRoute != null && !manualRoute.isEmpty()) {
            // use provided manual route, but clone to avoid mutating caller
            for (int i = 0; i < manualRoute.size(); i++) {
                RoutePoint src = manualRoute.get(i);
                RoutePoint rp = new RoutePoint();
                rp.setFacility(src.getFacility());
                rp.setSegmentToNext(src.getSegmentToNext());
                rp.setSequenceOrder(src.getSequenceOrder() != null ? src.getSequenceOrder() : i + 1);
                route.add(rp);
            }
        } else {
            throw new IllegalArgumentException("No route information provided");
        }

        // compute simple timings and distances
        double totalDistance = 0.0;
        LocalDateTime currentTime = plannedDeparture != null ? plannedDeparture : LocalDateTime.now();

        for (int i = 0; i < route.size(); i++) {
            RoutePoint rp = route.get(i);
            rp.setEstimatedDepartureTime(currentTime);
            if (i < route.size() - 1) {
                LineSegment seg = rp.getSegmentToNext();
                double length = seg != null && seg.getLengthKm() != null ? seg.getLengthKm() : 1.0; // default 1km
                totalDistance += length;
                long travelMinutes = computeTravelMinutes(length, seg);
                LocalDateTime arrival = currentTime.plusMinutes(travelMinutes);
                // next point arrival
                RoutePoint next = route.get(i + 1);
                next.setEstimatedArrivalTime(arrival);
                next.setEstimatedDepartureTime(arrival); // no dwell by default
                // set flags for single line
                boolean single = seg != null && (seg.getTrackNumber() == null ? false : seg.getTrackNumber() == 1);
                next.setIsSingleLineSegment(single);

                // conflict detection for single-track segments
                if (single && existingDispatches != null) {
                    for (TrainDispatch ex : existingDispatches) {
                        if (ex.getRoute() == null) continue;
                        for (RoutePoint exRp : ex.getRoute()) {
                            LineSegment exSeg = exRp.getSegmentToNext();
                            if (exSeg == null || seg == null) continue;
                            // compare by object equality or by segment id when available
                            boolean sameSegment = (exSeg == seg) || (exSeg.getSegmentId() != null && seg.getSegmentId() != null && exSeg.getSegmentId().equals(seg.getSegmentId()));
                            if (!sameSegment) continue;

                            LocalDateTime exStart = exRp.getEstimatedDepartureTime();
                            LocalDateTime exEnd = exRp.getEstimatedArrivalTime();
                            if (exStart == null || exEnd == null) continue;

                            // this train will be on segment between currentTime and arrival
                            LocalDateTime ourStart = currentTime;
                            LocalDateTime ourEnd = arrival;

                            boolean overlap = !(ourEnd.isBefore(exStart) || ourStart.isAfter(exEnd));
                            if (overlap) {
                                // mark waiting at next point
                                next.setCrossingOperation("WAIT_FOR_CROSSING");
                                // compute wait until exEnd
                                long wait = Duration.between(ourEnd.isBefore(exEnd) ? exEnd : ourEnd, exEnd).abs().toMinutes();
                                if (wait <= 0) wait = 1;
                                next.setExpectedWaitTimeMinutes((int) wait);
                            }
                        }
                    }
                }

                currentTime = arrival; // continue from arrival
            } else {
                // last point
                rp.setEstimatedArrivalTime(currentTime);
            }
        }

        // set route and metrics
        dispatch.setRoute(route);
        dispatch.setTotalDistanceKm(totalDistance);
        // estimated travel time in minutes
        double totalMinutes = 0.0;
        for (RoutePoint r : route) {
            if (r.getEstimatedArrivalTime() != null && r.getEstimatedDepartureTime() != null) {
                totalMinutes = Duration.between(route.get(0).getEstimatedDepartureTime(), route.get(route.size() - 1).getEstimatedArrivalTime()).toMinutes();
            }
        }
        dispatch.setEstimatedTravelTimeMinutes(totalMinutes);

        return dispatch;
    }

    private long computeTravelMinutes(double lengthKm, LineSegment seg) {
        // use speed limit if present, otherwise default 60 km/h
        double speedKmh = 60.0;
        if (seg != null && seg.getSpeedLimitKmh() != null && seg.getSpeedLimitKmh() > 0) {
            speedKmh = seg.getSpeedLimitKmh();
        }
        double hours = lengthKm / speedKmh;
        long minutes = Math.max(1, Math.round(hours * 60.0));
        return minutes;
    }

    public TrainDispatch loadDispatch(int serviceId) throws SQLException {
        // If DAOs present, use them
        if (trainServiceDAO != null && trainStopDAO != null) {
            TrainDispatch dispatch = new TrainDispatch();
            TrainService svc = trainServiceDAO.get(serviceId);
            if (svc == null) return null;
            dispatch.setTrainService(svc);
            List<TrainStop> stops = trainStopDAO.getByService(serviceId);
            List<RoutePoint> route = new ArrayList<>();
            for (TrainStop ts : stops) {
                RoutePoint rp = new RoutePoint();
                if (ts.getFacility() != null) {
                    Facility f = new Facility();
                    f.setFacilityId(ts.getFacility().getFacilityId());
                    rp.setFacility(f);
                }
                rp.setSequenceOrder(ts.getPosition());
                rp.setEstimatedArrivalTime(ts.getSchedArr());
                rp.setEstimatedDepartureTime(ts.getSchedDep());
                rp.setExpectedWaitTimeMinutes(ts.getPlannedStopTime());
                route.add(rp);
            }
            dispatch.setRoute(route);
            return dispatch;
        }

        TrainDispatch dispatch = new TrainDispatch();
        TrainService svc = new TrainService();
        svc.setServiceId(serviceId);
        dispatch.setTrainService(svc);

        try (Connection conn = getConnection()) {
            // load train_service
            String qSvc = "SELECT service_id, train_id, departure_date, arrival_date, train_driver_id, status FROM TRAIN_SERVICE WHERE service_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(qSvc)) {
                ps.setInt(1, serviceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        svc.setServiceId(rs.getInt("service_id"));
                        // apenas setar ids; objetos Train/User podem ser carregados por outros repositórios
                        Train t = new Train();
                        t.setTrainId(getIntOrNull(rs, "train_id"));
                        svc.setTrain(t);
                        svc.setDepartureDate(toLocalDateTime(rs.getTimestamp("departure_date")));
                        svc.setArrivalDate(toLocalDateTime(rs.getTimestamp("arrival_date")));
                        User driver = new User();
                        driver.setUserId(getIntOrNull(rs, "train_driver_id"));
                        svc.setTrainDriver(driver);
                        svc.setStatus(rs.getString("status"));
                    } else {
                        return null;
                    }
                }
            }

            // load stops ordered by position
            String qStops = "SELECT facility_id, position, sched_arr, sched_dep, planned_stop_time FROM TRAIN_STOP WHERE service_id = ? ORDER BY position";
            try (PreparedStatement ps = conn.prepareStatement(qStops)) {
                ps.setInt(1, serviceId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<RoutePoint> route = new ArrayList<>();
                    while (rs.next()) {
                        RoutePoint rp = new RoutePoint();
                        Facility f = new Facility();
                        f.setFacilityId(getIntOrNull(rs, "facility_id"));
                        rp.setFacility(f);
                        rp.setSequenceOrder(rs.getInt("position"));
                        rp.setEstimatedArrivalTime(toLocalDateTime(rs.getTimestamp("sched_arr")));
                        rp.setEstimatedDepartureTime(toLocalDateTime(rs.getTimestamp("sched_dep")));
                        Integer wait = rs.getObject("planned_stop_time") != null ? rs.getInt("planned_stop_time") : 0;
                        rp.setExpectedWaitTimeMinutes(wait);
                        route.add(rp);
                    }
                    dispatch.setRoute(route);
                }
            }
        }

        return dispatch;
    }

    /* ---------- util helpers ---------- */

    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private Integer getIntOrNull(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
}
