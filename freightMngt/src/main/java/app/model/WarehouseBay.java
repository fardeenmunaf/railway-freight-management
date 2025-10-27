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

    private Integer bayId;
    private WarehouseAisle aisle;
    private Integer bayNo;
    private Integer capacityBox;

    /**
     * Default constructor that creates a WarehouseBay with null/default values.
     * Initializes all fields to null.
     */

    public WarehouseBay() {
        this.bayId = null;
        this.aisle = null;
        this.bayNo = null;
        this.capacityBox = null;
    }

    /**
     * Parameterized constructor that creates a WarehouseBay with specified values.
     *
     * @param bayId the unique identifier for this bay
     * @param aisle the warehouse aisle this bay belongs to
     * @param bayNo the sequential number of this bay within the aisle
     * @param capacityBox the storage capacity of this bay measured in number of boxes
     */

    public WarehouseBay(Integer bayId, WarehouseAisle aisle, Integer bayNo, Integer capacityBox) {
        this.bayId = bayId;
        this.aisle = aisle;
        this.bayNo = bayNo;
        this.capacityBox = capacityBox;
    }

    /**
     * Gets the unique identifier of this bay.
     *
     * @return the bay identifier as a Integer
     */

    public Integer getBayId() {
        return bayId;
    }

    /**
     * Sets the unique identifier of this bay.
     *
     * @param bayId the bay identifier to set
     */

    public void setBayId(Integer bayId) {
        this.bayId = bayId;
    }

    /**
     * Gets the warehouse aisle this bay belongs to.
     *
     * @return the warehouse aisle as a WarehouseAisle object
     */

    public WarehouseAisle getAisle() {
        return aisle;
    }

    /**
     * Sets the warehouse aisle this bay belongs to.
     *
     * @param aisle the warehouse aisle to set
     */

    public void setAisle(WarehouseAisle aisle) {
        this.aisle = aisle;
    }

    /**
     * Gets the sequential number of this bay within the aisle.
     *
     * @return the bay number as an Integer
     */

    public Integer getBayNo() {
        return bayNo;
    }

    /**
     * Sets the sequential number of this bay within the aisle.
     *
     * @param bayNo the bay number to set
     */

    public void setBayNo(Integer bayNo) {
        this.bayNo = bayNo;
    }

    /**
     * Gets the storage capacity of this bay measured in number of boxes.
     *
     * @return the capacity in boxes as an integer
     */

    public Integer getCapacityBox() {
        return capacityBox;
    }

    /**
     * Sets the storage capacity of this bay measured in number of boxes.
     *
     * @param capacityBox the capacity in boxes to set
     */

    public void setCapacityBox(Integer capacityBox) {
        this.capacityBox = capacityBox;
    }

    @Override
    public String toString() {
        return "WarehouseBay{" +
                "bayId=" + bayId +
                ", aisle=" + aisle +
                ", bayNo=" + bayNo +
                ", capacityBox=" + capacityBox +
                '}';
    }
}
