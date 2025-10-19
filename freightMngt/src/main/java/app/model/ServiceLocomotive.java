package app.model;

public class ServiceLocomotive {

    private Integer serviceLocomotiveId;
    private TrainService service;
    private Locomotive locomotive;
    private Integer positionNo;

    public ServiceLocomotive() {
        this.serviceLocomotiveId = null;
        this.service = null;
        this.locomotive = null;
        this.positionNo = null;
    }

    public ServiceLocomotive(Integer serviceLocomotiveId, TrainService service, Locomotive locomotive, Integer positionNo) {
        this.serviceLocomotiveId = serviceLocomotiveId;
        this.service = service;
        this.locomotive = locomotive;
        this.positionNo = positionNo;
    }

    public Integer getServiceLocomotiveId() {
        return serviceLocomotiveId;
    }

    public void setServiceLocomotiveId(Integer serviceLocomotiveId) {
        this.serviceLocomotiveId = serviceLocomotiveId;
    }

    public TrainService getService() {
        return service;
    }

    public void setService(TrainService service) {
        this.service = service;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    public Integer getPositionNo() {
        return positionNo;
    }

    public void setPositionNo(Integer positionNo) {
        this.positionNo = positionNo;
    }
}
