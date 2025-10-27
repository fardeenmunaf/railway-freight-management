package app.model;

/**
 * Represents the relationship between a wagon and its gauge.
 * <p>
 * Associates a wagon with the track gauge it uses.
 * </p>
 */
public class WagonGauge {
    public Wagon wagon;
    public Gauge gauge;

    public WagonGauge() {
        this.wagon = null;
        this.gauge = null;
    }

    public WagonGauge(Wagon wagon, Gauge gauge) {
        this.wagon = wagon;
        this.gauge = gauge;
    }

    public Wagon getWagon() {
        return wagon;
    }

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }

    public Gauge getGauge() {
        return gauge;
    }

    public void setGauge(Gauge gauge) {
        this.gauge = gauge;
    }

    @Override
    public String toString() {
        return "WagonGauge{" +
                "wagon=" + wagon +
                ", gauge=" + gauge +
                '}';
    }
}

