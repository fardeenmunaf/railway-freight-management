package app.model;

/**
 * Represents the relationship between a locomotive and its gauge.
 * <p>
 * Associates a locomotive with the track gauge it uses.
 * </p>
 */
public class LocomotiveGauge {
    private Locomotive locomotive;
    private Gauge gauge;

    public LocomotiveGauge() {
        this.locomotive = null;
        this.gauge = null;
    }

    public LocomotiveGauge(Locomotive locomotive, Gauge gauge) {
        this.locomotive = locomotive;
        this.gauge = gauge;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    public Gauge getGauge() {
        return gauge;
    }

    public void setGauge(Gauge gauge) {
        this.gauge = gauge;
    }

    @Override
    public String toString() {
        return "LocomotiveGauge{" +
                "locomotive=" + locomotive +
                ", gauge=" + gauge +
                '}';
    }
}
