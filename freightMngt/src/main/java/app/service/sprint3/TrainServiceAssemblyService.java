package app.service.sprint3;

import app.db.dao.facility.FacilityDAO;
import app.db.dao.facility.FacilityDAOImpl;
import app.db.dao.locomotive.LocomotiveDAO;
import app.db.dao.locomotive.LocomotiveDAOImpl;
import app.db.dao.train.TrainDAO;
import app.db.dao.train.TrainDAOImpl;
import app.db.dao.trainassembly.TrainAssemblyDAO;
import app.db.dao.trainassembly.TrainAssemblyDAOImpl;
import app.db.dao.trainservice.TrainServiceDAO;
import app.db.dao.trainservice.TrainServiceDAOImpl;
import app.db.dao.trainstop.TrainStopDAO;
import app.db.dao.trainstop.TrainStopDAOImpl;
import app.db.dao.wagon.WagonDAO;
import app.db.dao.wagon.WagonDAOImpl;
import app.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for complete train service assembly operations (USLP09).
 * Provides comprehensive workflow to create Train, TrainService, TrainStops,
 * assign locomotives and wagons, and update their status to IN_TRANSIT.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainServiceAssemblyService {

    private final TrainDAO trainDAO;
    private final TrainServiceDAO trainServiceDAO;
    private final TrainStopDAO trainStopDAO;
    private final LocomotiveDAO locomotiveDAO;
    private final WagonDAO wagonDAO;
    private final FacilityDAO facilityDAO;
    private final TrainAssemblyDAO trainAssemblyDAO;

    /**
     * Constructor that initializes the service with DAO implementations.
     */
    public TrainServiceAssemblyService() {
        this.trainDAO = new TrainDAOImpl();
        this.trainServiceDAO = new TrainServiceDAOImpl();
        this.trainStopDAO = new TrainStopDAOImpl();
        this.locomotiveDAO = new LocomotiveDAOImpl();
        this.wagonDAO = new WagonDAOImpl();
        this.facilityDAO = new FacilityDAOImpl();
        this.trainAssemblyDAO = new TrainAssemblyDAOImpl();
    }

    /**
     * Result object containing all created entities after assembly.
     */
    public static class AssemblyResult {
        private final Train train;
        private final TrainService trainService;
        private final List<TrainStop> trainStops;
        private final int locomotivesAssigned;
        private final String validationMessage;

        public AssemblyResult(Train train, TrainService trainService, List<TrainStop> trainStops,
                              int locomotivesAssigned, String validationMessage) {
            this.train = train;
            this.trainService = trainService;
            this.trainStops = trainStops;
            this.locomotivesAssigned = locomotivesAssigned;
            this.validationMessage = validationMessage;
        }

        public Train getTrain() {
            return train;
        }

        public TrainService getTrainService() {
            return trainService;
        }

        public List<TrainStop> getTrainStops() {
            return trainStops;
        }

        public int getLocomotivesAssigned() {
            return locomotivesAssigned;
        }

        public String getValidationMessage() {
            return validationMessage;
        }

        public boolean isValid() {
            return "VALID".equals(validationMessage);
        }
    }

    /**
     * Route stop definition with facility and timing information.
     */
    public static class RouteStop {
        private final Integer facilityId;
        private final LocalDateTime schedArrival;
        private final LocalDateTime scheduledDeparture;
        private final Integer plannedStopTimeMinutes;

        public RouteStop(Integer facilityId, LocalDateTime schedArrival,
                        LocalDateTime scheduledDeparture, Integer plannedStopTimeMinutes) {
            this.facilityId = facilityId;
            this.schedArrival = schedArrival;
            this.scheduledDeparture = scheduledDeparture;
            this.plannedStopTimeMinutes = plannedStopTimeMinutes;
        }

        public Integer getFacilityId() {
            return facilityId;
        }

        public LocalDateTime getSchedArrival() {
            return schedArrival;
        }

        public LocalDateTime getScheduledDeparture() {
            return scheduledDeparture;
        }

        public Integer getPlannedStopTimeMinutes() {
            return plannedStopTimeMinutes;
        }
    }

    /**
     * Assembles a complete train service with all required database records.
     * This is the main workflow that orchestrates the entire assembly process.
     *
     * @param locomotiveIds list of locomotive IDs to assign to the service
     * @param wagonIds list of wagon IDs to assign to the service (optional, can be empty)
     * @param routeStops list of route stops in sequence (first is departure, last is destination)
     * @param departureDate overall departure date/time
     * @param arrivalDate overall arrival date/time
     * @param driverId train driver user ID (can be null)
     * @param operatorCompanyId company operating this train
     * @return AssemblyResult with all created entities and validation status
     * @throws SQLException if an error occurs during the operation
     * @throws IllegalArgumentException if validation fails
     */
    public AssemblyResult assembleCompleteTrainService(
            List<Integer> locomotiveIds,
            List<Integer> wagonIds,
            List<RouteStop> routeStops,
            LocalDateTime departureDate,
            LocalDateTime arrivalDate,
            Integer driverId,
            Integer operatorCompanyId) throws SQLException {

        // Validate inputs
        validateInputs(locomotiveIds, routeStops, departureDate, arrivalDate);

        try {
            // Step 1: Create TRAIN record
            Train train = createTrain(routeStops, operatorCompanyId);

            // Step 2: Create TRAIN_SERVICE record
            TrainService trainService = createTrainService(train, departureDate, arrivalDate, driverId);

            // Step 3: Create TRAIN_STOP records
            List<TrainStop> trainStops = createTrainStops(trainService, routeStops);

            // Step 4: Assign locomotives using PL/SQL function
            int locomotivesAssigned = assignLocomotives(trainService.getServiceId(), locomotiveIds);

            // Step 5: Update locomotive start_facility to mark as IN_TRANSIT
            updateLocomotivesStatus(locomotiveIds, routeStops.get(0).getFacilityId());

            // Step 6: Update wagon start_facility if wagons are provided
            if (wagonIds != null && !wagonIds.isEmpty()) {
                updateWagonsStatus(wagonIds, routeStops.get(0).getFacilityId());
            }

            // Step 7: Validate the composition
            String validationMessage = trainAssemblyDAO.validateTrainComposition(trainService.getServiceId());

            return new AssemblyResult(train, trainService, trainStops, locomotivesAssigned, validationMessage);

        } catch (SQLException e) {
            // Roll back will be handled by database transaction
            throw new SQLException("Failed to assemble train service: " + e.getMessage(), e);
        }
    }

    /**
     * Validates the input parameters before assembly.
     */
    private void validateInputs(List<Integer> locomotiveIds, List<RouteStop> routeStops,
                                LocalDateTime departureDate, LocalDateTime arrivalDate) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("At least one locomotive must be provided");
        }

        if (routeStops == null || routeStops.size() < 2) {
            throw new IllegalArgumentException("At least 2 route stops (departure and destination) must be provided");
        }

        if (departureDate == null || arrivalDate == null) {
            throw new IllegalArgumentException("Departure and arrival dates must be provided");
        }

        if (arrivalDate.isBefore(departureDate)) {
            throw new IllegalArgumentException("Arrival date must be after departure date");
        }
    }

    /**
     * Creates a TRAIN record with departure and destination.
     */
    private Train createTrain(List<RouteStop> routeStops, Integer operatorCompanyId) throws SQLException {
        Train train = new Train();

        // Generate train code (format: TRN-YYYYMMDD-HHmm)
        train.setCode(generateTrainCode());

        // Set departure (first stop) and destination (last stop) as facility IDs
        train.setDeparture(String.valueOf(routeStops.get(0).getFacilityId()));
        train.setDestination(String.valueOf(routeStops.get(routeStops.size() - 1).getFacilityId()));

        // Set operator
        if (operatorCompanyId != null) {
            Company operator = new Company();
            operator.setCompanyId(operatorCompanyId);
            train.setOperatorId(operator);
        }

        trainDAO.create(train);
        return train;
    }

    /**
     * Creates a TRAIN_SERVICE record.
     */
    private TrainService createTrainService(Train train, LocalDateTime departureDate,
                                           LocalDateTime arrivalDate, Integer driverId) throws SQLException {
        TrainService trainService = new TrainService();
        trainService.setTrain(train);
        trainService.setDepartureDate(departureDate);
        trainService.setArrivalDate(arrivalDate);
        trainService.setStatus("SCHEDULED");

        if (driverId != null) {
            User driver = new User();
            driver.setUserId(driverId);
            trainService.setTrainDriver(driver);
        }

        trainServiceDAO.create(trainService);
        return trainService;
    }

    /**
     * Creates TRAIN_STOP records for all intermediate stops.
     */
    private List<TrainStop> createTrainStops(TrainService trainService, List<RouteStop> routeStops) throws SQLException {
        List<TrainStop> trainStops = new java.util.ArrayList<>();

        for (int i = 0; i < routeStops.size(); i++) {
            RouteStop routeStop = routeStops.get(i);

            TrainStop trainStop = new TrainStop();
            trainStop.setService(trainService);

            Facility facility = new Facility();
            facility.setFacilityId(routeStop.getFacilityId());
            trainStop.setFacility(facility);

            // Get first available parking track for this facility (simplified - could be enhanced)
            ParkingTrack parkingTrack = getFirstAvailableParkingTrack(routeStop.getFacilityId());
            trainStop.setParkingTrack(parkingTrack);

            trainStop.setPosition(i + 1);
            trainStop.setSchedArr(routeStop.getSchedArrival());
            trainStop.setSchedDep(routeStop.getScheduledDeparture());
            trainStop.setPlannedStopTime(routeStop.getPlannedStopTimeMinutes());

            trainStopDAO.create(trainStop);
            trainStops.add(trainStop);
        }

        return trainStops;
    }

    /**
     * Gets the first available parking track for a facility.
     * Simplified implementation - returns a default parking track.
     */
    private ParkingTrack getFirstAvailableParkingTrack(Integer facilityId) {
        // For now, create a minimal parking track object
        // In a full implementation, this would query available tracks
        ParkingTrack parkingTrack = new ParkingTrack();
        parkingTrack.setParkingTrackId(1); // Default to track 1
        return parkingTrack;
    }

    /**
     * Assigns locomotives to the train service using PL/SQL function.
     */
    private int assignLocomotives(Integer serviceId, List<Integer> locomotiveIds) throws SQLException {
        String locomotiveIdsStr = locomotiveIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return trainAssemblyDAO.assembleTrainComposition(serviceId, locomotiveIdsStr);
    }

    /**
     * Updates locomotive start_facility to mark them as IN_TRANSIT.
     */
    private void updateLocomotivesStatus(List<Integer> locomotiveIds, Integer departureFacilityId) throws SQLException {
        for (Integer locomotiveId : locomotiveIds) {
            Locomotive locomotive = locomotiveDAO.get(locomotiveId);
            if (locomotive != null) {
                locomotive.setStartFacility(departureFacilityId);
                locomotiveDAO.update(locomotive);
            }
        }
    }

    /**
     * Updates wagon start_facility to mark them as IN_TRANSIT.
     */
    private void updateWagonsStatus(List<Integer> wagonIds, Integer departureFacilityId) throws SQLException {
        for (Integer wagonId : wagonIds) {
            Wagon wagon = wagonDAO.get(wagonId);
            if (wagon != null) {
                wagon.setStartFacility(departureFacilityId);
                wagonDAO.update(wagon);
            }
        }
    }

    /**
     * Generates a unique train code.
     * Format: TRN-YYYYMMDD-HHmm (e.g., TRN-20260104-1530)
     */
    private String generateTrainCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");
        return "TRN-" + LocalDateTime.now().format(formatter);
    }

    /**
     * Gets available locomotives for a departure facility.
     */
    public List<Locomotive> getAvailableLocomotives(Integer departureFacilityId) throws SQLException {
        return locomotiveDAO.getAvailableForRoute(departureFacilityId);
    }

    /**
     * Gets available wagons for a departure facility.
     */
    public List<Wagon> getAvailableWagons(Integer departureFacilityId) throws SQLException {
        return wagonDAO.getAvailableForRoute(departureFacilityId);
    }

    /**
     * Gets all facilities (for route selection).
     */
    public List<Facility> getAllFacilities() throws SQLException {
        return facilityDAO.getAll();
    }

    /**
     * Gets a facility by ID.
     */
    public Facility getFacilityById(Integer facilityId) throws SQLException {
        return facilityDAO.get(facilityId);
    }

    /**
     * Gets all train services.
     */
    public List<TrainService> getAllTrainServices() throws SQLException {
        return trainServiceDAO.getAll();
    }

    /**
     * Gets train stops for a specific service.
     */
    public List<TrainStop> getTrainStopsByService(Integer serviceId) throws SQLException {
        return trainStopDAO.getByService(serviceId);
    }
}

