package app.model;

/**
 * Represents a bay within a warehouse aisle in the freight management system.
 * Each bay has a unique identifier, belongs to a specific aisle,
 * has a sequential number within the aisle, and has a defined storage capacity in boxes.
 * Bays are the smallest storage units within the warehouse hierarchy.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseBay {

    private Integer bay_id;
    private WarehouseAisle aisle;
    private Integer bay_no;
    private Integer capacity_box;

    /**
     * Default constructor that creates a WarehouseBay with null/default values.
     * Initializes all fields to null.
     */

    public WarehouseBay() {
        this.bay_id = null;
        this.aisle = null;
        this.bay_no = null;
        this.capacity_box = null;
    }

    /**
     * Parameterized constructor that creates a WarehouseBay with specified values.
     *
     * @param bay_id the unique identifier for this bay
     * @param aisle the warehouse aisle this bay belongs to
     * @param bay_no the sequential number of this bay within the aisle
     * @param capacity_box the storage capacity of this bay measured in number of boxes
     */

    public WarehouseBay(Integer bay_id, WarehouseAisle aisle, Integer bay_no, Integer capacity_box) {
        this.bay_id = bay_id;
        this.aisle = aisle;
        this.bay_no = bay_no;
        this.capacity_box = capacity_box;
    }

    /**
     * Gets the unique identifier of this bay.
     *
     * @return the bay identifier as a Integer
     */

    public Integer getBayId() {
        return bay_id;
    }

    /**
     * Sets the unique identifier of this bay.
     *
     * @param bay_id the bay identifier to set
     */

    public void setBayId(Integer bay_id) {
        this.bay_id = bay_id;
    }

    /**
     * Gets the warehouse aisle this bay belongs to.
     *
     * @return the warehouse aisle as a WarehouseAisle object
     */

    public WarehouseAisle getAisleId() {
        return aisle;
    }

    /**
     * Sets the warehouse aisle this bay belongs to.
     *
     * @param aisle the warehouse aisle to set
     */

    public void setAisleId(WarehouseAisle aisle) {
        this.aisle = aisle;
    }

    /**
     * Gets the sequential number of this bay within the aisle.
     *
     * @return the bay number as an Integer
     */

    public Integer getBayNo() {
        return bay_no;
    }

    /**
     * Sets the sequential number of this bay within the aisle.
     *
     * @param bay_no the bay number to set
     */

    public void setBayNo(Integer bay_no) {
        this.bay_no = bay_no;
    }

    /**
     * Gets the storage capacity of this bay measured in number of boxes.
     *
     * @return the capacity in boxes as an integer
     */

    public Integer getCapacityBox() {
        return capacity_box;
    }

    /**
     * Sets the storage capacity of this bay measured in number of boxes.
     *
     * @param capacity_box the capacity in boxes to set
     */

    public void setCapacityBox(Integer capacity_box) {
        this.capacity_box = capacity_box;
    }
}
