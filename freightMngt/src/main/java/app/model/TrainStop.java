package app.model;

import java.time.LocalDateTime;

public class TrainStop {

    private TrainService service;
    private Facility facility;
    private ParkingTrack parkingTrack;
    private Integer trainStopId;
    private Integer position;
    private LocalDateTime schedArr;
    private LocalDateTime schedDep;
    private Integer plannedStopTime;
    private LocalDateTime actArr;
    private LocalDateTime actDep;
    private Integer actStopTime;

    public TrainStop() {
        this.trainStopId = null;
        this.service = null;
        this.facility = null;
        this.parkingTrack = null;
        this.position = null;
        this.schedArr = null;
        this.schedDep = null;
        this.plannedStopTime = null;
        this.actArr = null;
        this.actDep = null;
        this.actStopTime = null;
    }

    public TrainStop(Integer trainStopId, TrainService service, Facility facility, ParkingTrack parkingTrack, Integer position,
                     LocalDateTime schedArr, LocalDateTime schedDep, Integer plannedStopTime,
                     LocalDateTime actArr, LocalDateTime actDep, Integer actStopTime) {
        this.trainStopId = trainStopId;
        this.service = service;
        this.facility = facility;
        this.parkingTrack = parkingTrack;
        this.position = position;
        this.schedArr = schedArr;
        this.schedDep = schedDep;
        this.plannedStopTime = plannedStopTime;
        this.actArr = actArr;
        this.actDep = actDep;
        this.actStopTime = actStopTime;
    }

    public Integer getTrainStopId() {
        return trainStopId;
    }

    public void setTrainStopId(Integer trainStopId) {
        this.trainStopId = trainStopId;
    }

    public TrainService getService() {
        return service;
    }

    public void setService(TrainService service) {
        this.service = service;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public ParkingTrack getParkingTrack() {
        return parkingTrack;
    }

    public void setParkingTrack(ParkingTrack parkingTrack) {
        this.parkingTrack = parkingTrack;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LocalDateTime getSchedArr() {
        return schedArr;
    }

    public void setSchedArr(LocalDateTime schedArr) {
        this.schedArr = schedArr;
    }

    public LocalDateTime getSchedDep() {
        return schedDep;
    }

    public void setSchedDep(LocalDateTime schedDep) {
        this.schedDep = schedDep;
    }

    public Integer getPlannedStopTime() {
        return plannedStopTime;
    }

    public void setPlannedStopTime(Integer plannedStopTime) {
        this.plannedStopTime = plannedStopTime;
    }

    public LocalDateTime getActArr() {
        return actArr;
    }

    public void setActArr(LocalDateTime actArr) {
        this.actArr = actArr;
    }

    public LocalDateTime getActDep() {
        return actDep;
    }

    public void setActDep(LocalDateTime actDep) {
        this.actDep = actDep;
    }

    public Integer getActStopTime() {
        return actStopTime;
    }

    public void setActStopTime(Integer actStopTime) {
        this.actStopTime = actStopTime;
    }
}
