package app.model;

import java.time.LocalDateTime;

/**
 * Represents a freight shipment in the freight management system.
 * A freight is a cargo shipment that needs to be transported from one facility to another.
 * Each freight has a status (PENDING, IN_TRANSIT, DELIVERED) and can be assigned a route.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class Freight {

    private Integer freightId;
    private String description;
    private Item item;
    private Double weightKg;
    private Double volumeM3;
    private Facility originFacility;
    private Facility destinationFacility;
    private FreightStatus status;
    private FreightRoute assignedRoute;
    private LocalDateTime createdAt;
    private LocalDateTime deliveryDeadline;
    private String specialHandlingInstructions;

    /**
     * Enum for freight status.
     */
    public enum FreightStatus {
        PENDING,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED
    }

    /**
     * Default constructor that creates a Freight with null/default values.
     */
    public Freight() {
        this.freightId = null;
        this.description = null;
        this.item = null;
        this.weightKg = null;
        this.volumeM3 = null;
        this.originFacility = null;
        this.destinationFacility = null;
        this.status = FreightStatus.PENDING;
        this.assignedRoute = null;
        this.createdAt = LocalDateTime.now();
        this.deliveryDeadline = null;
        this.specialHandlingInstructions = null;
    }

    /**
     * Parameterized constructor that creates a Freight with specified values.
     *
     * @param freightId the unique identifier for this freight
     * @param description the description of the freight
     * @param item the item being shipped
     * @param weightKg the weight in kilograms
     * @param volumeM3 the volume in cubic meters
     * @param originFacility the origin facility
     * @param destinationFacility the destination facility
     * @param status the status of the freight
     * @param deliveryDeadline the delivery deadline
     * @param specialHandlingInstructions special handling instructions
     */
    public Freight(Integer freightId, String description, Item item, Double weightKg, Double volumeM3,
                   Facility originFacility, Facility destinationFacility, FreightStatus status,
                   LocalDateTime deliveryDeadline, String specialHandlingInstructions) {
        this.freightId = freightId;
        this.description = description;
        this.item = item;
        this.weightKg = weightKg;
        this.volumeM3 = volumeM3;
        this.originFacility = originFacility;
        this.destinationFacility = destinationFacility;
        this.status = status != null ? status : FreightStatus.PENDING;
        this.assignedRoute = null;
        this.createdAt = LocalDateTime.now();
        this.deliveryDeadline = deliveryDeadline;
        this.specialHandlingInstructions = specialHandlingInstructions;
    }

    // Getters and Setters

    public Integer getFreightId() {
        return freightId;
    }

    public void setFreightId(Integer freightId) {
        this.freightId = freightId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Double getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(Double volumeM3) {
        this.volumeM3 = volumeM3;
    }

    public Facility getOriginFacility() {
        return originFacility;
    }

    public void setOriginFacility(Facility originFacility) {
        this.originFacility = originFacility;
    }

    public Facility getDestinationFacility() {
        return destinationFacility;
    }

    public void setDestinationFacility(Facility destinationFacility) {
        this.destinationFacility = destinationFacility;
    }

    public FreightStatus getStatus() {
        return status;
    }

    public void setStatus(FreightStatus status) {
        this.status = status;
    }

    public FreightRoute getAssignedRoute() {
        return assignedRoute;
    }

    public void setAssignedRoute(FreightRoute assignedRoute) {
        this.assignedRoute = assignedRoute;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeliveryDeadline() {
        return deliveryDeadline;
    }

    public void setDeliveryDeadline(LocalDateTime deliveryDeadline) {
        this.deliveryDeadline = deliveryDeadline;
    }

    public String getSpecialHandlingInstructions() {
        return specialHandlingInstructions;
    }

    public void setSpecialHandlingInstructions(String specialHandlingInstructions) {
        this.specialHandlingInstructions = specialHandlingInstructions;
    }

    /**
     * Checks if this freight is pending (i.e., can be assigned a route).
     *
     * @return true if the freight is pending, false otherwise
     */
    public boolean isPending() {
        return status == FreightStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Freight{" +
                "freightId=" + freightId +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", originFacility=" + (originFacility != null ? originFacility.getName() : "null") +
                ", destinationFacility=" + (destinationFacility != null ? destinationFacility.getName() : "null") +
                ", assignedRoute=" + (assignedRoute != null ? assignedRoute.getRouteId() : "null") +
                '}';
    }
}

