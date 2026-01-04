package app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a segment of a freight route in the freight management system.
 * Each segment connects two facilities via a line segment and can have cargo operations (load/unload).
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightRouteSegment {

    private Integer segmentId;
    private FreightRoute freightRoute;
    private Integer sequenceOrder;
    private Facility fromFacility;
    private Facility toFacility;
    private LineSegment lineSegment;
    private List<CargoOperation> cargoOperations;
    private String notes;

    /**
     * Default constructor that creates a FreightRouteSegment with null/default values.
     */
    public FreightRouteSegment() {
        this.segmentId = null;
        this.freightRoute = null;
        this.sequenceOrder = null;
        this.fromFacility = null;
        this.toFacility = null;
        this.lineSegment = null;
        this.cargoOperations = new ArrayList<>();
        this.notes = null;
    }

    /**
     * Parameterized constructor that creates a FreightRouteSegment with specified values.
     *
     * @param segmentId the unique identifier for this segment
     * @param freightRoute the freight route this segment belongs to
     * @param sequenceOrder the order of this segment in the route
     * @param fromFacility the starting facility of this segment
     * @param toFacility the ending facility of this segment
     * @param lineSegment the line segment connecting the facilities
     * @param notes additional notes about the segment
     */
    public FreightRouteSegment(Integer segmentId, FreightRoute freightRoute, Integer sequenceOrder,
                               Facility fromFacility, Facility toFacility, LineSegment lineSegment, String notes) {
        this.segmentId = segmentId;
        this.freightRoute = freightRoute;
        this.sequenceOrder = sequenceOrder;
        this.fromFacility = fromFacility;
        this.toFacility = toFacility;
        this.lineSegment = lineSegment;
        this.cargoOperations = new ArrayList<>();
        this.notes = notes;
    }

    // Getters and Setters

    public Integer getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Integer segmentId) {
        this.segmentId = segmentId;
    }

    public FreightRoute getFreightRoute() {
        return freightRoute;
    }

    public void setFreightRoute(FreightRoute freightRoute) {
        this.freightRoute = freightRoute;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public Facility getFromFacility() {
        return fromFacility;
    }

    public void setFromFacility(Facility fromFacility) {
        this.fromFacility = fromFacility;
    }

    public Facility getToFacility() {
        return toFacility;
    }

    public void setToFacility(Facility toFacility) {
        this.toFacility = toFacility;
    }

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    public void setLineSegment(LineSegment lineSegment) {
        this.lineSegment = lineSegment;
    }

    public List<CargoOperation> getCargoOperations() {
        return new ArrayList<>(cargoOperations);
    }

    public void setCargoOperations(List<CargoOperation> cargoOperations) {
        this.cargoOperations = cargoOperations != null ? new ArrayList<>(cargoOperations) : new ArrayList<>();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Adds a cargo operation to this segment.
     *
     * @param operation the cargo operation to add
     */
    public void addCargoOperation(CargoOperation operation) {
        if (operation != null) {
            this.cargoOperations.add(operation);
        }
    }

    /**
     * Removes a cargo operation from this segment.
     *
     * @param operation the cargo operation to remove
     * @return true if the operation was removed, false otherwise
     */
    public boolean removeCargoOperation(CargoOperation operation) {
        return this.cargoOperations.remove(operation);
    }

    /**
     * Checks if this segment has any cargo operations.
     *
     * @return true if there are cargo operations, false otherwise
     */
    public boolean hasCargoOperations() {
        return !cargoOperations.isEmpty();
    }

    /**
     * Checks if this segment has any load operations.
     *
     * @return true if there are load operations, false otherwise
     */
    public boolean hasLoadOperations() {
        for (CargoOperation op : cargoOperations) {
            if (op.getOperationType() == CargoOperation.OperationType.LOAD) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this segment has any unload operations.
     *
     * @return true if there are unload operations, false otherwise
     */
    public boolean hasUnloadOperations() {
        for (CargoOperation op : cargoOperations) {
            if (op.getOperationType() == CargoOperation.OperationType.UNLOAD) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a summary of cargo operations at this segment.
     *
     * @return a formatted string with the cargo operations summary
     */
    public String getCargoOperationsSummary() {
        if (cargoOperations.isEmpty()) {
            return "No cargo operations";
        }

        StringBuilder summary = new StringBuilder();
        for (CargoOperation op : cargoOperations) {
            summary.append(op.getOperationType()).append(": ")
                    .append(op.getItem() != null ? op.getItem().getName() : "Unknown")
                    .append(" (").append(op.getQuantity()).append(" units)\n");
        }
        return summary.toString();
    }

    @Override
    public String toString() {
        return "FreightRouteSegment{" +
                "segmentId=" + segmentId +
                ", sequenceOrder=" + sequenceOrder +
                ", fromFacility=" + (fromFacility != null ? fromFacility.getName() : "null") +
                ", toFacility=" + (toFacility != null ? toFacility.getName() : "null") +
                ", cargoOperations=" + cargoOperations.size() +
                '}';
    }
}

