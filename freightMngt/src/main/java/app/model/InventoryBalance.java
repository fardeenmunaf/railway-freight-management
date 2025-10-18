package app.model;

/**
 * Represents an inventory balance record in the freight management system.
 * Each inventory balance tracks the quantity of a specific lot stored in a particular bay.
 * This class establishes the relationship between warehouse bays and inventory lots
 * with their corresponding quantities, enabling precise inventory tracking and management.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class InventoryBalance {

    private Integer balance_id;
    private WarehouseBay bay;
    private InventoryLot lot;
    private Integer qty;

    /**
     * Default constructor that creates an InventoryBalance with null/default values.
     * Initializes all fields to null.
     */

    public InventoryBalance() {
        this.balance_id = null;
        this.bay = null;
        this.lot = null;
        this.qty = null;
    }

    /**
     * Parameterized constructor that creates an InventoryBalance with specified values.
     *
     * @param balance_id the unique identifier for this inventory balance record
     * @param bay the warehouse bay where the inventory is stored
     * @param lot the inventory lot being tracked
     * @param qty the quantity of items in this balance record
     */

    public InventoryBalance(Integer balance_id, WarehouseBay bay, InventoryLot lot, Integer qty) {
        this.balance_id = balance_id;
        this.bay = bay;
        this.lot = lot;
        this.qty = qty;
    }

    /**
     * Gets the unique identifier of this inventory balance record.
     *
     * @return the balance identifier as a Integer
     */

    public Integer getBalanceId() {
        return balance_id;
    }

    /**
     * Sets the unique identifier of this inventory balance record.
     *
     * @param balance_id the balance identifier to set
     */

    public void setBalanceId(Integer balance_id) {
        this.balance_id = balance_id;
    }

    /**
     * Gets the warehouse bay where the inventory is stored.
     *
     * @return the warehouse bay as a WarehouseBay object
     */

    public WarehouseBay getBay() {
        return bay;
    }

    /**
     * Sets the warehouse bay where the inventory is stored.
     *
     * @param bay the warehouse bay to set
     */

    public void setBayId(WarehouseBay bay) {
        this.bay = bay;
    }

    /**
     * Gets the inventory lot being tracked.
     *
     * @return the inventory lot as an InventoryLot object
     */

    public InventoryLot getLotId() {
        return lot;
    }

    /**
     * Sets the inventory lot being tracked.
     *
     * @param lot the inventory lot to set
     */

    public void setLotId(InventoryLot lot) {
        this.lot = lot;
    }

    /**
     * Gets the quantity of items in this balance record.
     *
     * @return the quantity as an integer
     */

    public Integer getQty() {
        return qty;
    }

    /**
     * Sets the quantity of items in this balance record.
     *
     * @param qty the quantity to set
     */

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
