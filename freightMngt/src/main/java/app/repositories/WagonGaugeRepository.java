package app.repositories;

import app.model.WagonGauge;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing wagon gauges in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonGaugeRepository {

    private final List<WagonGauge> wagonGauges = new ArrayList<>();

    public void addWagonGauge(WagonGauge wagonGauge) {
        if (wagonGauge != null) {
            wagonGauges.add(wagonGauge);
        }
    }

    public List<WagonGauge> getAllWagonGauges() {
        return new ArrayList<>(wagonGauges);
    }

    public List<WagonGauge> getWagonGaugesByWagonId(Integer wagonId) {
        List<WagonGauge> result = new ArrayList<>();
        for (WagonGauge wagonGauge : wagonGauges) {
            if (wagonGauge.getWagon() != null && wagonId != null &&
                wagonId.equals(wagonGauge.getWagon().getWagonId())) {
                result.add(wagonGauge);
            }
        }
        return result;
    }

    public boolean deleteWagonGauge(Integer wagonId, Integer gaugeId) {
        return wagonGauges.removeIf(wg ->
            wg.getWagon() != null && wg.getGauge() != null &&
            wagonId.equals(wg.getWagon().getWagonId()) &&
            gaugeId.equals(wg.getGauge().getGaugeId()));
    }

    public void clear() {
        wagonGauges.clear();
    }
}
