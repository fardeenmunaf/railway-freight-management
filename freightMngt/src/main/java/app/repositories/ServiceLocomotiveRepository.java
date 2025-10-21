package app.repositories;

import app.model.ServiceLocomotive;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing service locomotives in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ServiceLocomotiveRepository {

    private final List<ServiceLocomotive> serviceLocomotives = new ArrayList<>();

    public void addServiceLocomotive(ServiceLocomotive serviceLocomotive) {
        if (serviceLocomotive != null) {
            serviceLocomotives.add(serviceLocomotive);
        }
    }

    public List<ServiceLocomotive> getAllServiceLocomotives() {
        return new ArrayList<>(serviceLocomotives);
    }

    public List<ServiceLocomotive> getServiceLocomotivesByServiceId(Integer serviceId) {
        List<ServiceLocomotive> result = new ArrayList<>();
        for (ServiceLocomotive sl : serviceLocomotives) {
            if (sl.getService() != null && serviceId != null && serviceId.equals(sl.getService().getServiceId())) {
                result.add(sl);
            }
        }
        return result;
    }

    public boolean deleteServiceLocomotive(Integer serviceId, Integer locomotiveId) {
        return serviceLocomotives.removeIf(sl ->
            sl.getService() != null && sl.getLocomotive() != null &&
            serviceId.equals(sl.getService().getServiceId()) &&
            locomotiveId.equals(sl.getLocomotive().getLocomotiveId()));
    }

    public void clear() {
        serviceLocomotives.clear();
    }
}
