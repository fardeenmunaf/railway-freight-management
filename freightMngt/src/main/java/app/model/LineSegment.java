package app.model;

public class LineSegment {

    private Integer segmentId;
    private Line line;
    private Double lengthKm;
    private TrackType trackType;
    private Boolean electrifType;
    private Gauge gauge;
    private Double maxWeightKgM;
    private Double speedLimitKmh;
    private Boolean singleTrack;
    private Integer position;

    public LineSegment() {
        this.segmentId = null;
        this.line = null;
        this.lengthKm = null;
        this.trackType = null;
        this.electrifType = null;
        this.gauge = null;
        this.maxWeightKgM = null;
        this.speedLimitKmh = null;
        this.singleTrack = null;
        this.position = null;
    }

    public LineSegment(Integer segmentId, Line line, Double lengthKm, TrackType trackType, Boolean electrifType, Gauge gauge, Double maxWeightKgM, Double speedLimitKmh, Boolean singleTrack, Integer position) {
        this.segmentId = segmentId;
        this.line = line;
        this.lengthKm = lengthKm;
        this.trackType = trackType;
        this.electrifType = electrifType;
        this.gauge = gauge;
        this.maxWeightKgM = maxWeightKgM;
        this.speedLimitKmh = speedLimitKmh;
        this.singleTrack = singleTrack;
        this.position = position;
    }

    public Integer getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Integer segmentId) {
        this.segmentId = segmentId;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Double getLengthKm() {
        return lengthKm;
    }

    public void setLengthKm(Double lengthKm) {
        this.lengthKm = lengthKm;
    }

    public TrackType getTrackType() {
        return trackType;
    }

    public void setTrackType(TrackType trackType) {
        this.trackType = trackType;
    }

    public Boolean getElectrifType() {
        return electrifType;
    }

    public void setElectrifType(Boolean electrifType) {
        this.electrifType = electrifType;
    }

    public Gauge getGauge() {
        return gauge;
    }

    public void setGauge(Gauge gauge) {
        this.gauge = gauge;
    }

    public Double getMaxWeightKgM() {
        return maxWeightKgM;
    }

    public void setMaxWeightKgM(Double maxWeightKgM) {
        this.maxWeightKgM = maxWeightKgM;
    }

    public Double getSpeedLimitKmh() {
        return speedLimitKmh;
    }

    public void setSpeedLimitKmh(Double speedLimitKmh) {
        this.speedLimitKmh = speedLimitKmh;
    }

    public Boolean getSingleTrack() {
        return singleTrack;
    }

    public void setSingleTrack(Boolean singleTrack) {
        this.singleTrack = singleTrack;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
