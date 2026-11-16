package app.service.sprint1;

import app.model.*;
import app.repositories.*;

/**
 * Helper class that provides a travel time feature and a TravelTimeCalculator for estimating travel time
 * between railway stations using repository data.
 */
public class TravelTimeFeature {

    /**
     * Creates a new instance of the travel time calculator.
     *
     * @param facilityRepository             repository for facilities
     * @param lineSegmentRepository          repository for line segments
     * @param lineSegmentFacilityRepository  repository for segment-to-facility links
     * @param locomotiveRepository           repository for locomotives
     * @return a new configured TravelTimeCalculator instance
     */
    public static TravelTimeCalculator create(
            FacilityRepository facilityRepository,
            LineSegmentRepository lineSegmentRepository,
            LineSegmentFacilityRepository lineSegmentFacilityRepository,
            LocomotiveRepository locomotiveRepository) {
        return new TravelTimeCalculator(
                facilityRepository,
                lineSegmentRepository,
                lineSegmentFacilityRepository,
                locomotiveRepository
        );
    }

    /**
     * Calculator class that computes travel time between two stations given locomotive constraints.
     */
    public static class TravelTimeCalculator {
        private final FacilityRepository facilityRepository;
        private final LineSegmentRepository lineSegmentRepository;
        private final LineSegmentFacilityRepository lineSegmentFacilityRepository;
        private final LocomotiveRepository locomotiveRepository;

        private LineSegment currentSegment;
        private double effectiveSpeed;
        private double distance;

        /**
         * Returns the total distance computed for the last calculated trip.
         *
         * @return distance in kilometers
         */
        public double getDistanceKm() {
            return this.distance;
        }

        /**
         * Returns the effective speed computed for the last calculated trip.
         * Effective speed is calculated as total distance divided by total time.
         *
         * @return effective speed in km/h
         */
        public double getEffectiveSpeedKmh() {
            return this.effectiveSpeed;
        }

        /**
         * Constructs a TravelTimeCalculator.
         *
         * @param facilityRepository            repository for facilities
         * @param lineSegmentRepository         repository for line segments
         * @param lineSegmentFacilityRepository repository for segment-to-facility links
         * @param locomotiveRepository          repository for locomotives
         */
        public TravelTimeCalculator(
                FacilityRepository facilityRepository,
                LineSegmentRepository lineSegmentRepository,
                LineSegmentFacilityRepository lineSegmentFacilityRepository,
                LocomotiveRepository locomotiveRepository) {
            this.facilityRepository = facilityRepository;
            this.lineSegmentRepository = lineSegmentRepository;
            this.lineSegmentFacilityRepository = lineSegmentFacilityRepository;
            this.locomotiveRepository = locomotiveRepository;
        }


        /**
         * Calculates the travel time in minutes between two stations using a specific locomotive.
         * Finds the fastest route by analyzing the railway network.
         *
         * @param stationId1  ID of the origin station
         * @param stationId2  ID of the destination station
         * @param locomotiveId ID of the locomotive to use
         * @return travel time in minutes
         * @throws NoDirectConnectionException if there is no path between the stations
         * @throws InvalidSpeedException if the locomotive has an invalid maximum speed or is missing
         */
        public double calculateTravelTimeMinutes(Integer stationId1, Integer stationId2, Integer locomotiveId) {
            Facility station1 = validateStation(stationId1);
            Facility station2 = validateStation(stationId2);
            Locomotive locomotive = validateLocomotive(locomotiveId);

            Double locomotiveMaxSpeed = locomotive.getMaxSpeed();
            if (locomotiveMaxSpeed == null || locomotiveMaxSpeed <= 0) {
                throw new InvalidSpeedException("Locomotive has an invalid maximum speed: " + locomotiveId);
            }

            PathResult result = findShortestTimePath(station1.getFacilityId(), station2.getFacilityId(), locomotiveMaxSpeed);
            if (result == null) {
                throw new NoDirectConnectionException("No direct connection between " + station1.getFacilityId() + " and " + station2.getFacilityId());
            }

            this.distance = result.totalDistanceKm;
            double totalHours = result.totalHours;
            this.effectiveSpeed = totalHours > 0 ? (result.totalDistanceKm / totalHours) : 0.0;
            return totalHours * 60.0;
        }

        /**
         * Represents the result of a path search between stations.
         * Contains total time (in hours) and total distance (in km) for the found route.
         */
        private static class PathResult {
            final double totalHours;
            final double totalDistanceKm;
            PathResult(double totalHours, double totalDistanceKm) {
                this.totalHours = totalHours;
                this.totalDistanceKm = totalDistanceKm;
            }
        }

        /**
         * Node used in the priority queue for pathfinding.
         * Keeps information about a facility and the accumulated costs to reach it.
         */
        private static class QueueNode {
            final Integer facilityId;
            final double hoursSoFar;
            final double distanceSoFar;
            QueueNode(Integer facilityId, double hoursSoFar, double distanceSoFar) {
                this.facilityId = facilityId;
                this.hoursSoFar = hoursSoFar;
                this.distanceSoFar = distanceSoFar;
            }
        }

        /**
         * Represents an edge in the railway graph connecting a destination facility with a line segment.
         */
        private static class Edge {
            final Integer toFacilityId;
            final LineSegment segment;
            Edge(Integer toFacilityId, LineSegment segment) {
                this.toFacilityId = toFacilityId;
                this.segment = segment;
            }
        }

        /**
         * Finds the fastest path between two railway stations.
         * Builds a directed graph of connections and computes travel times based on effective speeds
         * (the minimum of segment speed limit and the locomotive's maximum speed).
         *
         * @param startFacilityId  ID of the start facility
         * @param targetFacilityId ID of the target facility
         * @param locomotiveMaxSpeed locomotive maximum speed in km/h
         * @return PathResult containing total hours and distance for the fastest route, or null if no route exists
         */
        private PathResult findShortestTimePath(Integer startFacilityId, Integer targetFacilityId, double locomotiveMaxSpeed) {
            if (startFacilityId == null || targetFacilityId == null) return null;
            java.util.Map<Integer, java.util.List<Edge>> graph = new java.util.HashMap<>();
            java.util.List<LineSegment> segments = lineSegmentRepository.getAllLineSegments();
            if (segments == null) segments = java.util.Collections.emptyList();

            for (LineSegment seg : segments) {
                if (seg == null) continue;
                Integer segId = seg.getSegmentId();
                if (segId == null) continue;
                java.util.List<LineSegmentFacility> facs = lineSegmentFacilityRepository.getLineSegmentFacilitiesBySegmentId(segId);
                if (facs == null || facs.isEmpty()) continue;
                java.util.List<Integer> facilityIds = new java.util.ArrayList<>();
                for (LineSegmentFacility lsf : facs) {
                    if (lsf == null || lsf.getFacility() == null || lsf.getFacility().getFacilityId() == null) continue;
                    facilityIds.add(lsf.getFacility().getFacilityId());
                }
                for (int i = 0; i < facilityIds.size(); i++) {
                    for (int j = 0; j < facilityIds.size(); j++) {
                        if (i == j) continue;
                        Integer a = facilityIds.get(i);
                        Integer b = facilityIds.get(j);
                        graph.computeIfAbsent(a, k -> new java.util.ArrayList<>()).add(new Edge(b, seg));
                    }
                }
            }

            java.util.PriorityQueue<QueueNode> pq = new java.util.PriorityQueue<>(java.util.Comparator.comparingDouble(n -> n.hoursSoFar));
            java.util.Map<Integer, Double> bestHours = new java.util.HashMap<>();
            pq.add(new QueueNode(startFacilityId, 0.0, 0.0));
            bestHours.put(startFacilityId, 0.0);

            while (!pq.isEmpty()) {
                QueueNode node = pq.poll();
                Integer cur = node.facilityId;
                double curHours = node.hoursSoFar;
                double curDistance = node.distanceSoFar;

                Double best = bestHours.get(cur);
                if (best != null && curHours > best + 1e-9) continue;

                if (cur.equals(targetFacilityId)) {
                    return new PathResult(curHours, curDistance);
                }

                java.util.List<Edge> neighbors = graph.getOrDefault(cur, java.util.Collections.emptyList());
                for (Edge e : neighbors) {
                    if (e == null || e.segment == null) continue;
                    double segDistanceKm = e.segment.getLengthKm() != null ? e.segment.getLengthKm() : 0.0;
                    if (segDistanceKm <= 0.0) continue;

                    Double segSpeedLimit = e.segment.getSpeedLimitKmh();
                    double speedKmh = (segSpeedLimit != null && segSpeedLimit > 0) ? Math.min(segSpeedLimit, locomotiveMaxSpeed) : locomotiveMaxSpeed;
                    if (speedKmh <= 0.0) continue;

                    double edgeHours = segDistanceKm / speedKmh;
                    double newHours = curHours + edgeHours;
                    double newDist = curDistance + segDistanceKm;

                    Double known = bestHours.get(e.toFacilityId);
                    if (known == null || newHours + 1e-9 < known) {
                        bestHours.put(e.toFacilityId, newHours);
                        pq.add(new QueueNode(e.toFacilityId, newHours, newDist));
                    }
                }
            }

            return null;
        }

        /**
         * Validates that a facility exists and is a railway station.
         *
         * @param facilityId ID of the facility to validate
         * @return validated Facility
         * @throws NoDirectConnectionException if the facility does not exist or is not a station
         */
        private Facility validateStation(Integer facilityId) {
            Facility facility = facilityRepository.getFacilityById(facilityId);
            if (facility == null) {
                throw new NoDirectConnectionException("Station not found: " + facilityId);
            }
            if (facility.getFacilityType() == null || facility.getFacilityType().getFacilityTypeId() != 2) {
                throw new NoDirectConnectionException("Facility is not a station: " + facilityId);
            }
            return facility;
        }

        /**
         * Validates that a locomotive exists in the repository.
         *
         * @param locomotiveId ID of the locomotive to validate
         * @return validated Locomotive
         * @throws InvalidSpeedException if the locomotive does not exist
         */
        private Locomotive validateLocomotive(Integer locomotiveId) {
            Locomotive locomotive = locomotiveRepository.getLocomotiveById(locomotiveId);
            if (locomotive == null) {
                throw new InvalidSpeedException("Locomotive not found: " + locomotiveId);
            }
            return locomotive;
        }

    }

    /**
     * Exception thrown when no railway connection exists between two stations.
     */
    public static class NoDirectConnectionException extends RuntimeException {
        public NoDirectConnectionException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a locomotive has an invalid speed or is missing.
     */
    public static class InvalidSpeedException extends RuntimeException {
        public InvalidSpeedException(String message) {
            super(message);
        }
    }
}
