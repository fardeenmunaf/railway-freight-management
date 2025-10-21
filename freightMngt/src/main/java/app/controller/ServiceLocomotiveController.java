package app.controller;

import app.model.ServiceLocomotive;
import app.repositories.ServiceLocomotiveRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ServiceLocomotive entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for ServiceLocomotive objects.
 * This controller manages service locomotive data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ServiceLocomotiveController {
    private final ServiceLocomotiveRepository repository;

    /**
     * Default constructor that initializes the controller with the service locomotive repository.
     */
    public ServiceLocomotiveController() {
        this.repository = Repositories.getInstance().getServiceLocomotiveRepository();
    }

    /**
     * Creates and adds a new service locomotive to the system.
     *
     * @param serviceLocomotive the ServiceLocomotive object to be created
     * @return the created ServiceLocomotive object
     */
    public ServiceLocomotive createServiceLocomotive(ServiceLocomotive serviceLocomotive) {
        if (serviceLocomotive != null) {
            repository.addServiceLocomotive(serviceLocomotive);
        }
        return serviceLocomotive;
    }

    /**
     * Retrieves all service locomotives from the system.
     *
     * @return a list of all ServiceLocomotive objects
     */
    public List<ServiceLocomotive> getAllServiceLocomotives() {
        return repository.getAllServiceLocomotives();
    }

    /**
     * Retrieves all service locomotives for a specific train service.
     *
     * @param trainServiceId the train service identifier
     * @return a list of all ServiceLocomotive objects for the specified train service
     */
    public List<ServiceLocomotive> getServiceLocomotivesByTrainService(Integer trainServiceId) {
        return repository.getServiceLocomotivesByServiceId(trainServiceId);
    }

    /**
     * Deletes a service locomotive from the system.
     *
     * @param trainServiceId the train service identifier
     * @param locomotiveId the locomotive identifier
     * @return true if the service locomotive was deleted successfully, false otherwise
     */
    public boolean deleteServiceLocomotive(Integer trainServiceId, Integer locomotiveId) {
        return repository.deleteServiceLocomotive(trainServiceId, locomotiveId);
    }

    /**
     * Clears all service locomotives from the system.
     */
    public void clearAllServiceLocomotives() {
        repository.clear();
    }
}

