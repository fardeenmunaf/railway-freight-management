package app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a freight route in the freight management system.
 * A route can be simple (direct path) or complex (multiple ordered segments).
 * Each route has a start and end facility, and may have intermediate segments.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightRoute {

    private Integer routeId;
    private String routeName;
    private RouteType routeType;
    private Facility startFacility;
    private Facility endFacility;
    private List<FreightRouteSegment> routeSegments;
    private Double totalDistanceKm;
    private Double estimatedDurationHours;
    private String notes;

    /**
     * Enum for route type.
     */
    public enum RouteType {
        SIMPLE,   // Direct path from start to end
        COMPLEX   // Multiple ordered segments with intermediate stops
    }

    /**
     * Default constructor that creates a FreightRoute with null/default values.
     */
    public FreightRoute() {
        this.routeId = null;
        this.routeName = null;
        this.routeType = RouteType.SIMPLE;
        this.startFacility = null;
        this.endFacility = null;
        this.routeSegments = new ArrayList<>();
        this.totalDistanceKm = 0.0;
        this.estimatedDurationHours = 0.0;
        this.notes = null;
    }

    /**
     * Parameterized constructor that creates a FreightRoute with specified values.
     *
     * @param routeId the unique identifier for this route
     * @param routeName the name of the route
     * @param routeType the type of route (SIMPLE or COMPLEX)
     * @param startFacility the starting facility
     * @param endFacility the ending facility
     * @param notes additional notes about the route
     */
    public FreightRoute(Integer routeId, String routeName, RouteType routeType,
                        Facility startFacility, Facility endFacility, String notes) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routeType = routeType != null ? routeType : RouteType.SIMPLE;
        this.startFacility = startFacility;
        this.endFacility = endFacility;
        this.routeSegments = new ArrayList<>();
        this.totalDistanceKm = 0.0;
        this.estimatedDurationHours = 0.0;
        this.notes = notes;
        // Calculate totals immediately for simple routes
        calculateTotals();
    }

    // Getters and Setters

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public Facility getStartFacility() {
        return startFacility;
    }

    public void setStartFacility(Facility startFacility) {
        this.startFacility = startFacility;
    }

    public Facility getEndFacility() {
        return endFacility;
    }

    public void setEndFacility(Facility endFacility) {
        this.endFacility = endFacility;
    }

    public List<FreightRouteSegment> getRouteSegments() {
        return new ArrayList<>(routeSegments);
    }

    public void setRouteSegments(List<FreightRouteSegment> routeSegments) {
        this.routeSegments = routeSegments != null ? new ArrayList<>(routeSegments) : new ArrayList<>();
        calculateTotals();
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public Double getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Double estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Adds a route segment to this route.
     *
     * @param segment the segment to add
     */
    public void addRouteSegment(FreightRouteSegment segment) {
        if (segment != null) {
            this.routeSegments.add(segment);
            calculateTotals();
        }
    }

    /**
     * Removes a route segment from this route.
     *
     * @param segment the segment to remove
     * @return true if the segment was removed, false otherwise
     */
    public boolean removeRouteSegment(FreightRouteSegment segment) {
        boolean removed = this.routeSegments.remove(segment);
        if (removed) {
            calculateTotals();
        }
        return removed;
    }

    /**
     * Calculates the total distance and estimated duration based on route segments.
     * For simple routes, calculates direct distance between start and end facilities.
     * For complex routes, sums up the distance of all segments.
     */
    private void calculateTotals() {
        this.totalDistanceKm = 0.0;
        this.estimatedDurationHours = 0.0;

        if (routeType == RouteType.SIMPLE) {
            // For simple routes, calculate direct distance between start and end facilities
            if (startFacility != null && endFacility != null &&
                startFacility.getLat() != null && startFacility.getLon() != null &&
                endFacility.getLat() != null && endFacility.getLon() != null) {

                this.totalDistanceKm = app.utils.HaversineDistance.calculate(
                    startFacility.getLat(), startFacility.getLon(),
                    endFacility.getLat(), endFacility.getLon()
                );

                // Assume average speed of 80 km/h for simple routes
                double averageSpeedKmh = 80.0;
                this.estimatedDurationHours = this.totalDistanceKm / averageSpeedKmh;
            }
        } else {
            // For complex routes, sum up all segment distances
            for (FreightRouteSegment segment : routeSegments) {
                if (segment.getLineSegment() != null && segment.getLineSegment().getLengthKm() != null) {
                    this.totalDistanceKm += segment.getLineSegment().getLengthKm();

                    // Calculate duration based on speed limit
                    Double speedLimit = segment.getLineSegment().getSpeedLimitKmh();
                    if (speedLimit != null && speedLimit > 0) {
                        this.estimatedDurationHours += segment.getLineSegment().getLengthKm() / speedLimit;
                    }
                }
            }
        }
    }

    /**
     * Validates if this route is properly defined.
     *
     * @return true if the route is valid, false otherwise
     */
    public boolean isValid() {
        if (routeId == null || startFacility == null || endFacility == null) {
            return false;
        }

        // For complex routes, ensure we have segments
        if (routeType == RouteType.COMPLEX && routeSegments.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Gets a summary of the route with all stations in sequence.
     *
     * @return a formatted string with the route summary
     */
    public String getRouteSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Route: ").append(routeName != null ? routeName : "Unnamed")
                .append(" (").append(routeType).append(")\n");
        summary.append("Start: ").append(startFacility != null ? startFacility.getName() : "Unknown").append("\n");

        if (!routeSegments.isEmpty()) {
            summary.append("Intermediate Stations:\n");
            for (int i = 0; i < routeSegments.size(); i++) {
                FreightRouteSegment segment = routeSegments.get(i);
                summary.append("  ").append(i + 1).append(". ")
                        .append(segment.getFromFacility() != null ? segment.getFromFacility().getName() : "Unknown")
                        .append(" → ")
                        .append(segment.getToFacility() != null ? segment.getToFacility().getName() : "Unknown");

                // Add cargo operations if any
                if (segment.hasCargoOperations()) {
                    summary.append(" [");
                    if (segment.hasLoadOperations()) {
                        summary.append("LOAD");
                    }
                    if (segment.hasUnloadOperations()) {
                        if (segment.hasLoadOperations()) {
                            summary.append(", ");
                        }
                        summary.append("UNLOAD");
                    }
                    summary.append("]");
                }
                summary.append("\n");
            }
        }

        summary.append("End: ").append(endFacility != null ? endFacility.getName() : "Unknown").append("\n");
        summary.append("Total Distance: ").append(String.format("%.2f", totalDistanceKm)).append(" km\n");
        summary.append("Estimated Duration: ").append(String.format("%.2f", estimatedDurationHours)).append(" hours");

        return summary.toString();
    }

    @Override
    public String toString() {
        return "FreightRoute{" +
                "routeId=" + routeId +
                ", routeName='" + routeName + '\'' +
                ", routeType=" + routeType +
                ", startFacility=" + (startFacility != null ? startFacility.getName() : "null") +
                ", endFacility=" + (endFacility != null ? endFacility.getName() : "null") +
                ", segments=" + routeSegments.size() +
                ", totalDistanceKm=" + totalDistanceKm +
                '}';
    }
}

