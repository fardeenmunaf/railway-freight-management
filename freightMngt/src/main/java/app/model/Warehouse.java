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

    private Integer warehouse_id;
    private Facility facility;
    private String name;

    /**
     * Default constructor that creates a Warehouse with null values.
     * Initializes warehouse_id, facility_id, and name to null.
     */

    public Warehouse() {
        this.warehouse_id = null;
        this.facility = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a Warehouse with specified values.
     *
     * @param warehouse_id the unique identifier for this warehouse
     * @param facility the facility this warehouse belongs to
     * @param name the descriptive name of this warehouse
     */

    public Warehouse(Integer warehouse_id, Facility facility, String name) {
        this.warehouse_id = warehouse_id;
        this.facility = facility;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this warehouse.
     *
     * @return the warehouse identifier as an Integer
     */

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    /**
     * Sets the unique identifier of this warehouse.
     *
     * @param warehouse_id the warehouse identifier to set
     */

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    /**
     * Gets the identifier of the facility this warehouse belongs to.
     *
     * @return the facility identifier as a String
     */

    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the identifier of the facility this warehouse belongs to.
     *
     * @param facility the facility identifier to set
     */

    public void setFacility_id(Facility facility) {
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