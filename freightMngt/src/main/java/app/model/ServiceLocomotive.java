package app.model;

/**
 * Represents the assignment of a locomotive to a train service in the freight management system.
 * This class tracks which specific locomotive is assigned to a train service and its position
 * within the locomotive consist (e.g., lead locomotive, helper locomotive).
 * Used for managing locomotive assignments and operational configurations.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class ServiceLocomotive {

    private Integer serviceLocomotiveId;
    private TrainService service;
    private Locomotive locomotive;
    private Integer positionNo;

    /**
     * Default constructor that creates a ServiceLocomotive with null values.
     * Initializes all fields to null.
     */

    public ServiceLocomotive() {
        this.serviceLocomotiveId = null;
        this.service = null;
        this.locomotive = null;
        this.positionNo = null;
    }

    /**
     * Parameterized constructor that creates a ServiceLocomotive with specified values.
     *
     * @param serviceLocomotiveId the unique identifier for this service locomotive assignment
     * @param service the train service to which the locomotive is assigned
     * @param locomotive the locomotive assigned to the service
     * @param positionNo the position number of the locomotive in the consist
     */

    public ServiceLocomotive(Integer serviceLocomotiveId, TrainService service, Locomotive locomotive, Integer positionNo) {
        this.serviceLocomotiveId = serviceLocomotiveId;
        this.service = service;
        this.locomotive = locomotive;
        this.positionNo = positionNo;
    }

    /**
     * Gets the unique identifier of this service locomotive assignment.
     *
     * @return the service locomotive identifier as an Integer
     */

    public Integer getServiceLocomotiveId() {
        return serviceLocomotiveId;
    }

    /**
     * Sets the unique identifier of this service locomotive assignment.
     *
     * @param serviceLocomotiveId the service locomotive identifier to set
     */

    public void setServiceLocomotiveId(Integer serviceLocomotiveId) {
        this.serviceLocomotiveId = serviceLocomotiveId;
    }

    /**
     * Gets the train service to which the locomotive is assigned.
     *
     * @return the service as a TrainService object
     */

    public TrainService getService() {
        return service;
    }

    /**
     * Sets the train service to which the locomotive is assigned.
     *
     * @param service the service to set
     */

    public void setService(TrainService service) {
        this.service = service;
    }

    /**
     * Gets the locomotive assigned to the service.
     *
     * @return the locomotive as a Locomotive object
     */

    public Locomotive getLocomotive() {
        return locomotive;
    }

    /**
     * Sets the locomotive assigned to the service.
     *
     * @param locomotive the locomotive to set
     */

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    /**
     * Gets the position number of the locomotive in the consist.
     *
     * @return the position number as an Integer
     */

    public Integer getPositionNo() {
        return positionNo;
    }

    /**
     * Sets the position number of the locomotive in the consist.
     *
     * @param positionNo the position number to set
     */

    public void setPositionNo(Integer positionNo) {
        this.positionNo = positionNo;
    }
}
