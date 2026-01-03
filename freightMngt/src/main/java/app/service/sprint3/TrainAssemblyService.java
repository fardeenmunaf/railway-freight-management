package app.service.sprint3;

import app.db.dao.locomotive.LocomotiveDAO;
import app.db.dao.locomotive.LocomotiveDAOImpl;
import app.db.dao.wagon.WagonDAO;
import app.db.dao.wagon.WagonDAOImpl;
import app.db.dao.trainassembly.TrainAssemblyDAO;
import app.db.dao.trainassembly.TrainAssemblyDAOImpl;
import app.model.Locomotive;
import app.model.Wagon;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for train assembly operations (USLP09).
 * Provides business logic for listing available rolling stock and assembling train compositions.
 *
 * Implements the acceptance criteria:
 * - Lists locomotives and wagons distinguishing between PARKED and IN_TRANSIT
 * - Orders parked items by distance from departure (descending)
 * - Uses PL/SQL functions exclusively for database access
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainAssemblyService {

    private final LocomotiveDAO locomotiveDAO;
    private final WagonDAO wagonDAO;
    private final TrainAssemblyDAO trainAssemblyDAO;

    /**
     * Constructor that initializes the service with DAO implementations.
     */
    public TrainAssemblyService() {
        this.locomotiveDAO = new LocomotiveDAOImpl();
        this.wagonDAO = new WagonDAOImpl();
        this.trainAssemblyDAO = new TrainAssemblyDAOImpl();
    }

    /**
     * Constructor for dependency injection (useful for testing).
     *
     * @param locomotiveDAO the locomotive DAO
     * @param wagonDAO the wagon DAO
     * @param trainAssemblyDAO the train assembly DAO
     */
    public TrainAssemblyService(LocomotiveDAO locomotiveDAO, WagonDAO wagonDAO, TrainAssemblyDAO trainAssemblyDAO) {
        this.locomotiveDAO = locomotiveDAO;
        this.wagonDAO = wagonDAO;
        this.trainAssemblyDAO = trainAssemblyDAO;
    }

    /**
     * Gets all available locomotives for a specific departure facility.
     * Returns locomotives with status and distance information.
     * Already sorted by status (PARKED first) and distance (closest first) by PL/SQL function.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of available locomotives with USLP09 fields populated
     * @throws SQLException if an error occurs during the operation
     */
    public List<Locomotive> getAvailableLocomotives(Integer departureFacilityId) throws SQLException {
        return locomotiveDAO.getAvailableForRoute(departureFacilityId);
    }

    /**
     * Gets all available wagons for a specific departure facility.
     * Returns wagons with status and distance information.
     * Already sorted by status (PARKED first) and distance (closest first) by PL/SQL function.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of available wagons with USLP09 fields populated
     * @throws SQLException if an error occurs during the operation
     */
    public List<Wagon> getAvailableWagons(Integer departureFacilityId) throws SQLException {
        return wagonDAO.getAvailableForRoute(departureFacilityId);
    }

    /**
     * Gets only parked locomotives for a specific departure facility.
     * Filters locomotives with status "PARKED".
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of parked locomotives
     * @throws SQLException if an error occurs during the operation
     */
    public List<Locomotive> getParkedLocomotives(Integer departureFacilityId) throws SQLException {
        return getAvailableLocomotives(departureFacilityId).stream()
                .filter(loc -> "PARKED".equals(loc.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Gets only in-transit locomotives for a specific departure facility.
     * Filters locomotives with status "IN_TRANSIT".
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of in-transit locomotives
     * @throws SQLException if an error occurs during the operation
     */
    public List<Locomotive> getInTransitLocomotives(Integer departureFacilityId) throws SQLException {
        return getAvailableLocomotives(departureFacilityId).stream()
                .filter(loc -> "IN_TRANSIT".equals(loc.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Gets only parked wagons for a specific departure facility.
     * Filters wagons with status "PARKED" or "WAITING".
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of parked wagons
     * @throws SQLException if an error occurs during the operation
     */
    public List<Wagon> getParkedWagons(Integer departureFacilityId) throws SQLException {
        return getAvailableWagons(departureFacilityId).stream()
                .filter(wagon -> "PARKED".equals(wagon.getStatus()) || "WAITING".equals(wagon.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Gets only in-transit wagons for a specific departure facility.
     * Filters wagons with status "IN_TRANSIT".
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of in-transit wagons
     * @throws SQLException if an error occurs during the operation
     */
    public List<Wagon> getInTransitWagons(Integer departureFacilityId) throws SQLException {
        return getAvailableWagons(departureFacilityId).stream()
                .filter(wagon -> "IN_TRANSIT".equals(wagon.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Assembles a train composition by assigning selected locomotives to a train service.
     * Uses PL/SQL function to perform the assignment.
     *
     * @param serviceId the train service ID
     * @param locomotiveIds list of locomotive IDs to assign
     * @return number of locomotives successfully assigned
     * @throws SQLException if an error occurs during the operation
     */
    public int assembleTrainComposition(Integer serviceId, List<Integer> locomotiveIds) throws SQLException {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("Locomotive IDs list cannot be null or empty");
        }

        // Convert list to comma-separated string
        String locomotiveIdsStr = locomotiveIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return trainAssemblyDAO.assembleTrainComposition(serviceId, locomotiveIdsStr);
    }

    /**
     * Validates a train composition to ensure it meets all technical requirements.
     * Uses PL/SQL function to perform validation.
     *
     * @param serviceId the train service ID
     * @return validation result message ("VALID" or error description)
     * @throws SQLException if an error occurs during the operation
     */
    public String validateTrainComposition(Integer serviceId) throws SQLException {
        return trainAssemblyDAO.validateTrainComposition(serviceId);
    }

    /**
     * Assembles and validates a train composition in a single operation.
     * First assembles the composition, then validates it.
     *
     * @param serviceId the train service ID
     * @param locomotiveIds list of locomotive IDs to assign
     * @return validation result object containing count and validation message
     * @throws SQLException if an error occurs during the operation
     */
    public AssemblyResult assembleAndValidate(Integer serviceId, List<Integer> locomotiveIds) throws SQLException {
        int count = assembleTrainComposition(serviceId, locomotiveIds);
        String validationMessage = validateTrainComposition(serviceId);
        return new AssemblyResult(count, validationMessage);
    }

    /**
     * Result object for assembly and validation operations.
     */
    public static class AssemblyResult {
        private final int locomotivesAssigned;
        private final String validationMessage;

        public AssemblyResult(int locomotivesAssigned, String validationMessage) {
            this.locomotivesAssigned = locomotivesAssigned;
            this.validationMessage = validationMessage;
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

        @Override
        public String toString() {
            return "AssemblyResult{" +
                    "locomotivesAssigned=" + locomotivesAssigned +
                    ", validationMessage='" + validationMessage + '\'' +
                    ", valid=" + isValid() +
                    '}';
        }
    }
}

