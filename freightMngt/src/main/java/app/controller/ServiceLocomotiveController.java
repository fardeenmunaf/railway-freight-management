package app.controller;

import app.model.ServiceLocomotive;
import java.util.ArrayList;
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
    private List<ServiceLocomotive> serviceLocomotives;

    /**
     * Default constructor that initializes the service locomotive list.
     */
    public ServiceLocomotiveController() {
        this.serviceLocomotives = new ArrayList<>();
    }

    /**
     * Creates and adds a new service locomotive to the system.
     *
     * @param serviceLocomotive the ServiceLocomotive object to be created
     * @return the created ServiceLocomotive object
     */
    public ServiceLocomotive createServiceLocomotive(ServiceLocomotive serviceLocomotive) {
        if (serviceLocomotive != null) {
            serviceLocomotives.add(serviceLocomotive);
        }
        return serviceLocomotive;
    }

    /**
     * Retrieves all service locomotives from the system.
     *
     * @return a list of all ServiceLocomotive objects
     */
    public List<ServiceLocomotive> getAllServiceLocomotives() {
        return new ArrayList<>(serviceLocomotives);
    }

    /**
     * Retrieves a service locomotive by its unique identifier.
     *
     * @param serviceLocomotiveId the unique identifier of the service locomotive
     * @return an Optional containing the ServiceLocomotive if found, or empty if not found
     */
    public Optional<ServiceLocomotive> getServiceLocomotiveById(Integer serviceLocomotiveId) {
        return serviceLocomotives.stream()
                .filter(serviceLocomotive -> serviceLocomotive.getServiceLocomotiveId().equals(serviceLocomotiveId))
                .findFirst();
    }

    /**
     * Updates an existing service locomotive in the system.
     *
     * @param serviceLocomotiveId the unique identifier of the service locomotive to update
     * @param updatedServiceLocomotive the ServiceLocomotive object with updated information
     * @return true if the service locomotive was updated successfully, false otherwise
     */
    public boolean updateServiceLocomotive(Integer serviceLocomotiveId, ServiceLocomotive updatedServiceLocomotive) {
        Optional<ServiceLocomotive> existingServiceLocomotive = getServiceLocomotiveById(serviceLocomotiveId);
        if (existingServiceLocomotive.isPresent()) {
            int index = serviceLocomotives.indexOf(existingServiceLocomotive.get());
            serviceLocomotives.set(index, updatedServiceLocomotive);
            return true;
        }
        return false;
    }

    /**
     * Deletes a service locomotive from the system.
     *
     * @param serviceLocomotiveId the unique identifier of the service locomotive to delete
     * @return true if the service locomotive was deleted successfully, false otherwise
     */
    public boolean deleteServiceLocomotive(Integer serviceLocomotiveId) {
        return serviceLocomotives.removeIf(serviceLocomotive -> serviceLocomotive.getServiceLocomotiveId().equals(serviceLocomotiveId));
    }
}

