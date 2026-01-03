package app.controller;

import app.model.Locomotive;
import app.model.Wagon;
import app.service.sprint3.TrainAssemblyService;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for managing Train Assembly operations (USLP09).
 * Provides methods to list available rolling stock and assemble train compositions.
 * Delegates business logic to TrainAssemblyService.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainAssemblyController {

    private final TrainAssemblyService service;

    /**
     * Default constructor that initializes the controller with the service.
     */
    public TrainAssemblyController() {
        this.service = new TrainAssemblyService();
    }

    /**
     * Constructor for dependency injection (useful for testing).
     *
     * @param service the train assembly service
     */
    public TrainAssemblyController(TrainAssemblyService service) {
        this.service = service;
    }

    /**
     * Gets all available locomotives for a specific departure facility.
     * Returns locomotives sorted by status (PARKED first) and distance.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of available locomotives
     * @throws SQLException if an error occurs during the operation
     */
    public List<Locomotive> getAvailableLocomotives(Integer departureFacilityId) throws SQLException {
        return service.getAvailableLocomotives(departureFacilityId);
    }

    /**
     * Gets all available wagons for a specific departure facility.
     * Returns wagons sorted by status (PARKED first) and distance.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of available wagons
     * @throws SQLException if an error occurs during the operation
     */
    public List<Wagon> getAvailableWagons(Integer departureFacilityId) throws SQLException {
        return service.getAvailableWagons(departureFacilityId);
    }

    /**
     * Gets only parked locomotives for a specific departure facility.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of parked locomotives
     * @throws SQLException if an error occurs during the operation
     */
    public List<Locomotive> getParkedLocomotives(Integer departureFacilityId) throws SQLException {
        return service.getParkedLocomotives(departureFacilityId);
    }

    /**
     * Gets only in-transit locomotives for a specific departure facility.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of in-transit locomotives
     * @throws SQLException if an error occurs during the operation
     */
    public List<Locomotive> getInTransitLocomotives(Integer departureFacilityId) throws SQLException {
        return service.getInTransitLocomotives(departureFacilityId);
    }

    /**
     * Gets only parked wagons for a specific departure facility.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of parked wagons
     * @throws SQLException if an error occurs during the operation
     */
    public List<Wagon> getParkedWagons(Integer departureFacilityId) throws SQLException {
        return service.getParkedWagons(departureFacilityId);
    }

    /**
     * Gets only in-transit wagons for a specific departure facility.
     *
     * @param departureFacilityId the facility ID of the departure point
     * @return list of in-transit wagons
     * @throws SQLException if an error occurs during the operation
     */
    public List<Wagon> getInTransitWagons(Integer departureFacilityId) throws SQLException {
        return service.getInTransitWagons(departureFacilityId);
    }

    /**
     * Assembles a train composition by assigning locomotives to a train service.
     *
     * @param serviceId the train service ID
     * @param locomotiveIds list of locomotive IDs to assign
     * @return number of locomotives successfully assigned
     * @throws SQLException if an error occurs during the operation
     */
    public int assembleTrainComposition(Integer serviceId, List<Integer> locomotiveIds) throws SQLException {
        return service.assembleTrainComposition(serviceId, locomotiveIds);
    }

    /**
     * Validates a train composition.
     *
     * @param serviceId the train service ID
     * @return validation result message
     * @throws SQLException if an error occurs during the operation
     */
    public String validateTrainComposition(Integer serviceId) throws SQLException {
        return service.validateTrainComposition(serviceId);
    }

    /**
     * Assembles and validates a train composition in a single operation.
     *
     * @param serviceId the train service ID
     * @param locomotiveIds list of locomotive IDs to assign
     * @return assembly result with count and validation message
     * @throws SQLException if an error occurs during the operation
     */
    public TrainAssemblyService.AssemblyResult assembleAndValidate(Integer serviceId, List<Integer> locomotiveIds) throws SQLException {
        return service.assembleAndValidate(serviceId, locomotiveIds);
    }
}

