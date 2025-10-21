package app.repositories;

import app.model.LocomotiveGauge;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing locomotive gauges in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveGaugeRepository {

    private final List<LocomotiveGauge> locomotiveGauges = new ArrayList<>();

    public void addLocomotiveGauge(LocomotiveGauge locomotiveGauge) {
        if (locomotiveGauge != null) {
            locomotiveGauges.add(locomotiveGauge);
        }
    }

    public List<LocomotiveGauge> getAllLocomotiveGauges() {
        return new ArrayList<>(locomotiveGauges);
    }

    public List<LocomotiveGauge> getLocomotiveGaugesByLocomotiveId(Integer locomotiveId) {
        List<LocomotiveGauge> result = new ArrayList<>();
        for (LocomotiveGauge lg : locomotiveGauges) {
            if (lg.getLocomotive() != null && locomotiveId != null &&
                locomotiveId.equals(lg.getLocomotive().getLocomotiveId())) {
                result.add(lg);
            }
        }
        return result;
    }

    public boolean deleteLocomotiveGauge(Integer locomotiveId, Integer gaugeId) {
        return locomotiveGauges.removeIf(lg ->
            lg.getLocomotive() != null && lg.getGauge() != null &&
            locomotiveId.equals(lg.getLocomotive().getLocomotiveId()) &&
            gaugeId.equals(lg.getGauge().getGaugeId()));
    }

    public void clear() {
        locomotiveGauges.clear();
    }
}
