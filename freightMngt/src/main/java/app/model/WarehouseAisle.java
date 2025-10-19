package app.model;

/**
 * Represents an aisle within a warehouse in the freight management system.
 * Each aisle has a unique identifier, belongs to a specific warehouse,
 * has a sequential number, and contains a unique code for identification purposes.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseAisle {

    private Integer aisleId;
    private Warehouse warehouse;
    private Integer aisleNo;
    private String uniqueCode;

    /**
     * Default constructor that creates a WarehouseAisle with null/default values.
     * Initializes all fields to null.
     */

    public WarehouseAisle() {
        this.aisleId = null;
        this.warehouse = null;
        this.aisleNo = null;
        this.uniqueCode = null;
    }

    /**
     * Parameterized constructor that creates a WarehouseAisle with specified values.
     *
     * @param aisleId the unique identifier for this aisle
     * @param warehouse the warehouse this aisle belongs to
     * @param aisleNo the sequential number of this aisle within the warehouse
     * @param uniqueCode the unique code assigned to this aisle
     */

    public WarehouseAisle(Integer aisleId, Warehouse warehouse, Integer aisleNo, String uniqueCode) {
        this.aisleId = aisleId;
        this.warehouse = warehouse;
        this.aisleNo = aisleNo;
        this.uniqueCode = uniqueCode;
    }

    /**
     * Gets the unique identifier of this aisle.
     *
     * @return the aisle identifier as a Integer
     */

    public Integer getAisleId() {
        return aisleId;
    }

    /**
     * Sets the unique identifier of this aisle.
     *
     * @param aisleId the aisle identifier to set
     */

    public void setAisleId(Integer aisleId) {
        this.aisleId = aisleId;
    }

    /**
     * Gets the warehouse this aisle belongs to.
     *
     * @return the warehouse as a Warehouse object
     */

    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Sets the warehouse this aisle belongs to.
     *
     * @param warehouse the warehouse to set
     */

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Gets the sequential number of this aisle within the warehouse.
     *
     * @return the aisle number as an Integer
     */

    public Integer getAisleNo() {
        return aisleNo;
    }

    /**
     * Sets the sequential number of this aisle within the warehouse.
     *
     * @param aisleNo the aisle number to set
     */

    public void setAisleNo(Integer aisleNo) {
        this.aisleNo = aisleNo;
    }

    /**
     * Gets the unique code assigned to this aisle.
     *
     * @return the unique code as a String
     */

    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
     * Sets the unique code assigned to this aisle.
     *
     * @param uniqueCode the unique code to set
     */

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
