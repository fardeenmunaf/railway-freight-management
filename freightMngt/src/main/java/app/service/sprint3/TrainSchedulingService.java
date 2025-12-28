package app.service.sprint3;

import app.model.*;
import app.repositories.Repositories;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Train Scheduling Service
 * - Manual and automatic route definition
 * - Speed calculation based on track, locomotive power and wagon weight
 * - Single-line crossing resolution at stations or sidings
 * - Estimated times of passage at route points
 */
public class TrainSchedulingService {

    public TrainDispatch scheduleDispatch(
            Integer dispatchId,
            TrainService trainService,
            FreightRoute freightRoute,
            LocalDateTime plannedDeparture,
            String routeType, // MANUAL | AUTOMATIC
            List<RoutePoint> manualRoute,
            List<TrainDispatch> existingDispatches
    ) {

        TrainDispatch dispatch = new TrainDispatch();
        dispatch.setDispatchId(dispatchId);
        dispatch.setTrainService(trainService);
        dispatch.setPlannedDepartureTime(plannedDeparture);
        dispatch.setRouteType(routeType);
        dispatch.setStatus("PLANNED");

        List<RoutePoint> route;
        if ("MANUAL".equalsIgnoreCase(routeType)) {
            route = manualRoute != null ? new ArrayList<>(manualRoute) : new ArrayList<>();
        } else {
            route = buildAutomaticRoute(freightRoute);
        }

        // Ensure sequence order and link segmentToNext for manual routes if possible
        normalizeRoutePoints(route, freightRoute);

        dispatch.setRoute(route);

        calculateSchedule(dispatch, freightRoute, existingDispatches);

        return dispatch;
    }

    /* ============================================================
       ROUTE CREATION
       ============================================================ */

    private List<RoutePoint> buildAutomaticRoute(FreightRoute route) {
        List<RoutePoint> points = new ArrayList<>();
        if (route == null) return points;

        List<FreightRouteSegment> segments = new ArrayList<>(route.getRouteSegments());
        segments.sort(Comparator.comparing(FreightRouteSegment::getSequenceOrder));

        RoutePoint last = null;
        int seq = 1;
        for (FreightRouteSegment seg : segments) {
            Facility from = seg.getFromFacility();
            Facility to = seg.getToFacility();
            LineSegment line = seg.getLineSegment();

            if (last == null) {
                RoutePoint rpFrom = createRoutePoint(from);
                rpFrom.setSequenceOrder(seq++);
                rpFrom.setSegmentToNext(line);
                points.add(rpFrom);

                RoutePoint rpTo = createRoutePoint(to);
                rpTo.setSequenceOrder(seq++);
                // segmentToNext will be set by next segment if any
                points.add(rpTo);
                last = rpTo;
            } else {
                // if last facility equals this from, just set its segmentToNext
                if (last.getFacility() != null && last.getFacility().equals(from)) {
                    last.setSegmentToNext(line);
                    // add 'to' as next point
                    RoutePoint rpTo = createRoutePoint(to);
                    rpTo.setSequenceOrder(seq++);
                    points.add(rpTo);
                    last = rpTo;
                } else {
                    // non contiguous segments (defensive) - add both
                    RoutePoint rpFrom = createRoutePoint(from);
                    rpFrom.setSequenceOrder(seq++);
                    rpFrom.setSegmentToNext(line);
                    points.add(rpFrom);

                    RoutePoint rpTo = createRoutePoint(to);
                    rpTo.setSequenceOrder(seq++);
                    points.add(rpTo);
                    last = rpTo;
                }
            }
        }

        // ensure last point has null segmentToNext
        if (!points.isEmpty()) {
            points.get(points.size() - 1).setSegmentToNext(null);
        }

        return points;
    }

    private RoutePoint createRoutePoint(Facility facility) {
        RoutePoint rp = new RoutePoint();
        rp.setFacility(facility);
        return rp;
    }

    /**
     * Fills missing sequenceOrder and tries to populate segmentToNext for manual routes
     * using the FreightRoute segments if available.
     */
    private void normalizeRoutePoints(List<RoutePoint> routePoints, FreightRoute freightRoute) {
        if (routePoints == null || routePoints.isEmpty()) return;

        // set sequence orders
        for (int i = 0; i < routePoints.size(); i++) {
            RoutePoint rp = routePoints.get(i);
            rp.setSequenceOrder(i + 1);
        }

        // if any segmentToNext is missing and freightRoute provided, try to infer
        if (freightRoute != null) {
            Map<String, FreightRouteSegment> segMap = new HashMap<>();
            for (FreightRouteSegment frs : freightRoute.getRouteSegments()) {
                String key = frs.getFromFacility().getFacilityId() + "_" + frs.getToFacility().getFacilityId();
                segMap.put(key, frs);
            }

            for (int i = 0; i < routePoints.size() - 1; i++) {
                RoutePoint rp = routePoints.get(i);
                if (rp.getSegmentToNext() == null) {
                    Facility from = rp.getFacility();
                    Facility to = routePoints.get(i + 1).getFacility();
                    if (from != null && to != null) {
                        String key = from.getFacilityId() + "_" + to.getFacilityId();
                        FreightRouteSegment frs = segMap.get(key);
                        if (frs != null) {
                            rp.setSegmentToNext(frs.getLineSegment());
                        }
                    }
                }
            }
        }
    }

    /* ============================================================
       SCHEDULING + TIMING
       ============================================================ */

    private void calculateSchedule(
            TrainDispatch dispatch,
            FreightRoute route,
            List<TrainDispatch> existingDispatches
    ) {

        if (dispatch == null || dispatch.getPlannedDepartureTime() == null) return;

        LocalDateTime currentTime = dispatch.getPlannedDepartureTime();
        double totalDistance = 0;
        double totalSeconds = 0;

        double totalTrainWeight = calculateTrainWeight(dispatch.getTrainService());
        double totalPower = calculateLocomotivePower(dispatch.getTrainService());

        List<RoutePoint> points = dispatch.getRoute();
        if (points == null || points.isEmpty()) {
            dispatch.setTotalDistanceKm(0.0);
            dispatch.setEstimatedTravelTimeMinutes(0.0);
            return;
        }

        // initialize first point departure
        RoutePoint first = points.get(0);
        first.setEstimatedDepartureTime(currentTime);
        first.setEstimatedArrivalTime(currentTime);
        first.setDistanceFromStartKm(0.0);
        first.setCumulativeTravelTimeMinutes(0.0);
        first.setIsSingleLineSegment(first.getSegmentToNext() != null && isSingleTrack(first.getSegmentToNext()));
        first.setCrossingOperation("NONE");
        first.setExpectedWaitTimeMinutes(0);

        double cumulativeDistance = 0.0;
        double cumulativeSeconds = 0.0;

        for (int i = 0; i < points.size() - 1; i++) {
            RoutePoint currentPoint = points.get(i);
            RoutePoint nextPoint = points.get(i + 1);

            LineSegment line = currentPoint.getSegmentToNext();
            if (line == null) {
                // can't compute travel without a line - assume 0 distance
                nextPoint.setEstimatedArrivalTime(currentTime);
                nextPoint.setEstimatedDepartureTime(currentTime);
                nextPoint.setDistanceFromStartKm(cumulativeDistance);
                nextPoint.setCumulativeTravelTimeMinutes(cumulativeSeconds / 60.0);
                nextPoint.setIsSingleLineSegment(false);
                nextPoint.setCrossingOperation("NONE");
                nextPoint.setExpectedWaitTimeMinutes(0);
                continue;
            }

            double distanceKm = safe(line.getLengthKm());
            double speedKmh = calculateEffectiveSpeed(line, totalPower, totalTrainWeight);
            // seconds precision
            double travelSeconds = (distanceKm / Math.max(speedKmh, 0.0001)) * 3600.0;

            LocalDateTime entryTime = currentTime;
            LocalDateTime exitTime = currentTime.plusSeconds((long) Math.ceil(travelSeconds));

            // SINGLE LINE CROSSING -> check conflicts and compute wait (in minutes)
            long waitMinutes = 0;
            if (isSingleTrack(line)) {
                long delay = calculateCrossingDelay(entryTime, exitTime, line, existingDispatches);
                if (delay > 0) {
                    waitMinutes = delay;
                    entryTime = entryTime.plusMinutes(delay);
                    exitTime = exitTime.plusMinutes(delay);
                    travelSeconds += delay * 60.0;
                }
            }

            // Update cumulative values
            cumulativeDistance += distanceKm;
            cumulativeSeconds += travelSeconds;

            // Fill next point timings and metadata
            nextPoint.setEstimatedArrivalTime(exitTime);
            nextPoint.setEstimatedDepartureTime(exitTime);
            nextPoint.setDistanceFromStartKm(cumulativeDistance);
            nextPoint.setCumulativeTravelTimeMinutes(cumulativeSeconds / 60.0);
            nextPoint.setIsSingleLineSegment(isSingleTrack(line));
            if (waitMinutes > 0) {
                nextPoint.setCrossingOperation("WAIT_FOR_CROSSING");
                nextPoint.setExpectedWaitTimeMinutes((int) waitMinutes);
            } else {
                nextPoint.setCrossingOperation("NONE");
                nextPoint.setExpectedWaitTimeMinutes(0);
            }

            // advance time
            currentTime = exitTime;
            totalDistance += distanceKm;
            totalSeconds += travelSeconds;
        }

        dispatch.setTotalDistanceKm(totalDistance);
        dispatch.setEstimatedTravelTimeMinutes(totalSeconds / 60.0);
        // TrainDispatch computes estimated arrival from plannedDeparture + estimatedTravelTime, no setter available
        // dispatch.setEstimatedArrivalTime(dispatch.getPlannedDepartureTime().plusSeconds((long) Math.ceil(totalSeconds)));
    }

    /* ============================================================
       SPEED CALCULATION
       ============================================================ */

    private double calculateEffectiveSpeed(
            LineSegment line,
            double locomotivePower,
            double trainWeight
    ) {

        double trackLimit = safe(line.getSpeedLimitKmh());
        if (trackLimit <= 0) trackLimit = 60;

        // Simplified realistic constraints
        double powerLimitSpeed = locomotivePower / 10;   // heuristic
        double weightLimitSpeed = 200000 / Math.max(trainWeight, 1);

        double effectiveSpeed = Math.min(trackLimit,
                Math.min(powerLimitSpeed, weightLimitSpeed));

        return Math.max(effectiveSpeed, 30); // minimum operational speed
    }

    private double calculateLocomotivePower(TrainService service) {
        if (service == null || service.getServiceId() == null) return 0.0;
        double power = 0.0;
        List<TrainServiceComposition> compositions = Repositories.getInstance()
                .getTrainServiceCompositionRepository()
                .getCompositionsByTrainService(service.getServiceId());
        if (compositions == null) return 0.0;
        for (TrainServiceComposition comp : compositions) {
            ServiceLocomotive sl = comp.getServiceLocomotive();
            if (sl != null && sl.getLocomotive() != null && sl.getLocomotive().getPower() != null) {
                power += sl.getLocomotive().getPower();
            }
        }
        return power;
    }

    private double calculateTrainWeight(TrainService service) {
        if (service == null || service.getServiceId() == null) return 1.0; // avoid div by zero
        double weight = 0.0;
        List<TrainServiceComposition> compositions = Repositories.getInstance()
                .getTrainServiceCompositionRepository()
                .getCompositionsByTrainService(service.getServiceId());
        if (compositions == null) return 1.0;
        for (TrainServiceComposition comp : compositions) {
            // include wagon tare + payload
            Wagon w = comp.getWagon();
            if (w != null && w.getWagonModel() != null) {
                Double tare = w.getWagonModel().getTareWeight();
                Double payload = w.getWagonModel().getPayloadWeight();
                if (tare != null) weight += tare;
                if (payload != null) weight += payload;
            }
            // include locomotive weight if present
            ServiceLocomotive sl = comp.getServiceLocomotive();
            if (sl != null && sl.getLocomotive() != null && sl.getLocomotive().getTotalWeight() != null) {
                weight += sl.getLocomotive().getTotalWeight();
            }
        }
        return Math.max(weight, 1.0);
    }

    /* ============================================================
       CROSSING LOGIC
       ============================================================ */

    private boolean isSingleTrack(LineSegment line) {
        return line.getTrackNumber() != null && line.getTrackNumber() == 1;
    }

    /**
     * Calculate minimal wait (in minutes) required to avoid overlap with existing dispatches
     * on the same LineSegment. It checks existingDispatches' route points for occupations of the line
     * and ensures this dispatch's entry is after any conflicting occupation.
     */
    private long calculateCrossingDelay(
            LocalDateTime entry,
            LocalDateTime exit,
            LineSegment line,
            List<TrainDispatch> existingDispatches
    ) {

        if (existingDispatches == null || existingDispatches.isEmpty()) return 0;

        long maxDelayMinutes = 0;

        for (TrainDispatch other : existingDispatches) {
            if (other == null) continue;

            // try to find a route point in 'other' that uses the same line
            List<RoutePoint> otherPoints = other.getRoute();
            if (otherPoints == null) continue;

            for (int i = 0; i < otherPoints.size() - 1; i++) {
                RoutePoint op = otherPoints.get(i);
                LineSegment otherLine = op.getSegmentToNext();
                if (otherLine == null) continue;
                if (!otherLine.equals(line)) continue;

                // determine occupation window for the other dispatch on this line
                LocalDateTime otherEntry = op.getEstimatedDepartureTime();
                LocalDateTime otherExit = otherPoints.get(i + 1).getEstimatedArrivalTime();

                if (otherEntry == null || otherExit == null) {
                    // fallback to planned times if available
                    if (other.getPlannedDepartureTime() != null && other.getEstimatedTravelTimeMinutes() != null) {
                        otherEntry = other.getPlannedDepartureTime();
                        otherExit = otherEntry.plusMinutes(other.getEstimatedTravelTimeMinutes().longValue());
                    } else {
                        continue;
                    }
                }

                // check overlap: (entry < otherExit) && (otherEntry < exit)
                boolean overlap = entry.isBefore(otherExit) && otherEntry.isBefore(exit);
                if (overlap) {
                    long delay = Duration.between(entry, otherExit).toMinutes();
                    if (delay > maxDelayMinutes) maxDelayMinutes = delay;
                }
            }
        }

        return maxDelayMinutes;
    }

    /* ============================================================
       UTILS
       ============================================================ */

    private double safe(Double value) {
        return value != null ? value : 0;
    }
}
