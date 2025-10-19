package app.model;

public class TrackType {

    private Integer trackTypeId;
    private String name;

    public TrackType() {
        this.trackTypeId = null;
        this.name = null;
    }

    public TrackType(Integer trackTypeId, String name) {
        this.trackTypeId = trackTypeId;
        this.name = name;
    }

    public Integer getTrackTypeId() {
        return trackTypeId;
    }

    public void setTrackTypeId(Integer trackTypeId) {
        this.trackTypeId = trackTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
