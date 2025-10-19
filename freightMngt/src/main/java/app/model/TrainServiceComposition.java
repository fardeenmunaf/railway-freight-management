package app.model;

/**
 * Represents the composition of a train service in the freight management system.
 * This class defines the relationship between a train service and its rolling stock,
 * including locomotives and wagons that make up the train consist.
 * Used to track which locomotives and wagons are assigned to specific train services.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class TrainServiceComposition {

    private TrainService trainService;
    private ServiceLocomotive serviceLocomotive;
    private Wagon wagon;

    /**
     * Default constructor that creates a TrainServiceComposition with null values.
     * Initializes all fields to null.
     */

    public TrainServiceComposition() {
        this.trainService = null;
        this.serviceLocomotive = null;
        this.wagon = null;
    }

    /**
     * Parameterized constructor that creates a TrainServiceComposition with specified values.
     *
     * @param trainService the train service in the composition
     * @param serviceLocomotive the locomotive assigned to the service
     * @param wagon the wagon in the composition
     */

    public TrainServiceComposition(TrainService trainService, ServiceLocomotive serviceLocomotive, Wagon wagon) {
        this.trainService = trainService;
        this.serviceLocomotive = serviceLocomotive;
        this.wagon = wagon;
    }

    /**
     * Gets the train service in this composition.
     *
     * @return the train service as a TrainService object
     */

    public TrainService getTrainService() {
        return trainService;
    }

    /**
     * Sets the train service in this composition.
     *
     * @param trainService the train service to set
     */

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * Gets the locomotive assigned to the service.
     *
     * @return the service locomotive as a ServiceLocomotive object
     */

    public ServiceLocomotive getServiceLocomotive() {
        return serviceLocomotive;
    }

    /**
     * Sets the locomotive assigned to the service.
     *
     * @param serviceLocomotive the service locomotive to set
     */

    public void setServiceLocomotive(ServiceLocomotive serviceLocomotive) {
        this.serviceLocomotive = serviceLocomotive;
    }

    /**
     * Gets the wagon in this composition.
     *
     * @return the wagon as a Wagon object
     */

    public Wagon getWagon() {
        return wagon;
    }

    /**
     * Sets the wagon in this composition.
     *
     * @param wagon the wagon to set
     */

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }
}
