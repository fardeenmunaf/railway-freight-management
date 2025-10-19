package app.model;

public class ParkingTrack {

    private Integer parkingTrackId;
    private Line line;
    private Facility facility;
    private String name;
    private Integer capacityWagons;

    public ParkingTrack() {
        this.parkingTrackId = null;
        this.line = null;
        this.facility = null;
        this.name = null;
        this.capacityWagons = null;
    }

    public ParkingTrack(Integer parkingTrackId, Line line, Facility facility, String name, Integer capacityWagons) {
        this.parkingTrackId = parkingTrackId;
        this.line = line;
        this.facility = facility;
        this.name = name;
        this.capacityWagons = capacityWagons;
    }

    public Integer getParkingTrackId() {
        return parkingTrackId;
    }

    public void setParkingTrackId(Integer parkingTrackId) {
        this.parkingTrackId = parkingTrackId;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacityWagons() {
        return capacityWagons;
    }

    public void setCapacityWagons(Integer capacityWagons) {
        this.capacityWagons = capacityWagons;
    }
}
