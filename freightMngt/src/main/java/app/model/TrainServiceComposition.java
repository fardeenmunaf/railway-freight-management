package app.model;

public class TrainServiceComposition {

    private TrainService trainService;
    private ServiceLocomotive serviceLocomotive;
    private Wagon wagon;

    public TrainServiceComposition() {
        this.trainService = null;
        this.serviceLocomotive = null;
        this.wagon = null;
    }

    public TrainServiceComposition(TrainService trainService, ServiceLocomotive serviceLocomotive, Wagon wagon) {
        this.trainService = trainService;
        this.serviceLocomotive = serviceLocomotive;
        this.wagon = wagon;
    }

    public TrainService getTrainService() {
        return trainService;
    }

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    public ServiceLocomotive getServiceLocomotive() {
        return serviceLocomotive;
    }

    public void setServiceLocomotive(ServiceLocomotive serviceLocomotive) {
        this.serviceLocomotive = serviceLocomotive;
    }

    public Wagon getWagon() {
        return wagon;
    }

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }
}
