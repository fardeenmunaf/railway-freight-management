package app.model;

public class ParkingQueue {

    private Integer parkingQueueId;
    private ParkingTrack parkingTrack;
    private Wagon wagon;
    private Integer positionNo;
    private String status;

    public ParkingQueue() {
        this.parkingQueueId = null;
        this.parkingTrack = null;
        this.wagon = null;
        this.positionNo = null;
        this.status = null;
    }

    public ParkingQueue(Integer parkingQueueId, ParkingTrack parkingTrack, Wagon wagon, Integer positionNo, String status) {
        this.parkingQueueId = parkingQueueId;
        this.parkingTrack = parkingTrack;
        this.wagon = wagon;
        this.positionNo = positionNo;
        this.status = status;
    }

    public Integer getParkingQueueId() {
        return parkingQueueId;
    }

    public void setParkingQueueId(Integer parkingQueueId) {
        this.parkingQueueId = parkingQueueId;
    }

    public ParkingTrack getParkingTrack() {
        return parkingTrack;
    }

    public void setParkingTrack(ParkingTrack parkingTrack) {
        this.parkingTrack = parkingTrack;
    }

    public Wagon getWagon() {
        return wagon;
    }

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }

    public Integer getPositionNo() {
        return positionNo;
    }

    public void setPositionNo(Integer positionNo) {
        this.positionNo = positionNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
