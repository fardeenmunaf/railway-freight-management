package app.model;

/**
 * Represents a warehouse facility in the freight management system.
 * Each warehouse has a unique identifier, belongs to a specific facility,
 * and has a descriptive name for identification purposes.
 * Warehouses serve as the top-level storage units that contain aisles and bays.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Warehouse{

    private Integer warehouseId;
    private Facility facility;
    private String name;

    /**
     * Default constructor that creates a Warehouse with null values.
     * Initializes warehouseId, facility, and name to null.
     */

    public Warehouse() {
        this.warehouseId = null;
        this.facility = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a Warehouse with specified values.
     *
     * @param warehouseId the unique identifier for this warehouse
     * @param facility the facility this warehouse belongs to
     * @param name the descriptive name of this warehouse
     */

    public Warehouse(Integer warehouseId, Facility facility, String name) {
        this.warehouseId = warehouseId;
        this.facility = facility;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this warehouse.
     *
     * @return the warehouse identifier as an Integer
     */

    public Integer getWarehouseId() {
        return warehouseId;
    }

    /**
     * Sets the unique identifier of this warehouse.
     *
     * @param warehouseId the warehouse identifier to set
     */

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    /**
     * Gets the facility this warehouse belongs to.
     *
     * @return the facility as a Facility object
     */

    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the facility this warehouse belongs to.
     *
     * @param facility the facility to set
     */

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Gets the descriptive name of this warehouse.
     *
     * @return the warehouse name as a String
     */

    public String getName () {
        return name;
    }

    /**
     * Sets the descriptive name of this warehouse.
     *
     * @param name the warehouse name to set
     */

    public void setName (String name) {
        this.name = name;
    }
}