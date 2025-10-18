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

    private Integer aisle_id;
    private Warehouse warehouse;
    private Integer aisle_no;
    private String unique_code;

    /**
     * Default constructor that creates a WarehouseAisle with null/default values.
     * Initializes all fields to null.
     */

    public WarehouseAisle() {
        this.aisle_id = null;
        this.warehouse = null;
        this.aisle_no = null;
        this.unique_code = null;
    }

    /**
     * Parameterized constructor that creates a WarehouseAisle with specified values.
     *
     * @param aisle_id the unique identifier for this aisle
     * @param warehouse the warehouse this aisle belongs to
     * @param aisle_no the sequential number of this aisle within the warehouse
     * @param unique_code the unique code assigned to this aisle
     */

    public WarehouseAisle(Integer aisle_id, Warehouse warehouse, Integer aisle_no, String unique_code) {
        this.aisle_id = aisle_id;
        this.warehouse = warehouse;
        this.aisle_no = aisle_no;
        this.unique_code = unique_code;
    }

    /**
     * Gets the unique identifier of this aisle.
     *
     * @return the aisle identifier as a Integer
     */

    public Integer getAisleId() {
        return aisle_id;
    }

    /**
     * Sets the unique identifier of this aisle.
     *
     * @param aisle_id the aisle identifier to set
     */

    public void setAisleId(Integer aisle_id) {
        this.aisle_id = aisle_id;
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

    public void setWarehouseId(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Gets the sequential number of this aisle within the warehouse.
     *
     * @return the aisle number as an Integer
     */

    public Integer getAisleNo() {
        return aisle_no;
    }

    /**
     * Sets the sequential number of this aisle within the warehouse.
     *
     * @param aisle_no the aisle number to set
     */

    public void setAisleNo(Integer aisle_no) {
        this.aisle_no = aisle_no;
    }

    /**
     * Gets the unique code assigned to this aisle.
     *
     * @return the unique code as a String
     */

    public String getUniqueCode() {
        return unique_code;
    }

    /**
     * Sets the unique code assigned to this aisle.
     *
     * @param unique_code the unique code to set
     */

    public void setUniqueCode(String unique_code) {
        this.unique_code = unique_code;
    }
}
