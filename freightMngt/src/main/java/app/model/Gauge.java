package app.model;

public class Gauge {
    private Integer gaugeId;
    private String gaugeName;
    private Double gaugeSize; // in millimeters

    public Gauge() {
        gaugeId = null;
        gaugeName = null;
        gaugeSize = null;
    }

    public Gauge(Integer gaugeId, String gaugeName, Double gaugeSize) {
        this.gaugeId = gaugeId;
        this.gaugeName = gaugeName;
        this.gaugeSize = gaugeSize;
    }

    public Integer getGaugeId() {
        return gaugeId;
    }

    public void setGaugeId(Integer gaugeId) {
        this.gaugeId = gaugeId;
    }

    public String getGaugeName() {
        return gaugeName;
    }

    public void setGaugeName(String gaugeName) {
        this.gaugeName = gaugeName;
    }

    public Double getGaugeSize() {
        return gaugeSize;
    }

    public void setGaugeSize(Double gaugeSize) {
        this.gaugeSize = gaugeSize;
    }

    @Override
    public String toString() {
        return "Gauge{" +
                "gaugeId=" + gaugeId +
                ", gaugeName='" + gaugeName + '\'' +
                ", gaugeSize=" + gaugeSize +
                '}';
    }
}
