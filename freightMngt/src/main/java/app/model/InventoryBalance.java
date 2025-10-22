package app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inventory balance record in the freight management system.
 * Each inventory balance tracks multiple lots of a specific SKU stored in a particular bay.
 * This class establishes the relationship between warehouse bays and inventory lots
 * with their corresponding quantities, enabling precise inventory tracking and management.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class InventoryBalance {

    private Integer balanceId;
    private WarehouseBay bay;
    private List<InventoryLot> lots;
    private Integer qty;

    /**
     * Default constructor that creates an InventoryBalance with null/default values.
     * Initializes all fields to null and lots to an empty list.
     */

    public InventoryBalance() {
        this.balanceId = null;
        this.bay = null;
        this.lots = new ArrayList<>();
        this.qty = 0;
    }

    /**
     * Parameterized constructor that creates an InventoryBalance with specified values.
     *
     * @param balanceId the unique identifier for this inventory balance record
     * @param bay the warehouse bay where the inventory is stored
     * @param lots the list of inventory lots being tracked
     * @param qty the total quantity of items in this balance record
     */

    public InventoryBalance(Integer balanceId, WarehouseBay bay, List<InventoryLot> lots, Integer qty) {
        this.balanceId = balanceId;
        this.bay = bay;
        this.lots = lots != null ? new ArrayList<>(lots) : new ArrayList<>();
        this.qty = qty;
    }

    /**
     * Gets the unique identifier of this inventory balance record.
     *
     * @return the balance identifier as a Integer
     */

    public Integer getBalanceId() {
        return balanceId;
    }

    /**
     * Sets the unique identifier of this inventory balance record.
     *
     * @param balanceId the balance identifier to set
     */

    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
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
     * Gets the inventory lots being tracked.
     *
     * @return a list of InventoryLot objects
     */

    public List<InventoryLot> getLots() {
        return new ArrayList<>(lots);
    }

    /**
     * Sets the inventory lots being tracked.
     *
     * @param lots the list of inventory lots to set
     */

    public void setLots(List<InventoryLot> lots) {
        this.lots = lots != null ? new ArrayList<>(lots) : new ArrayList<>();
    }

    /**
     * Adds a single inventory lot to this balance.
     *
     * @param lot the inventory lot to add
     */

    public void addLot(InventoryLot lot) {
        if (lot != null) {
            this.lots.add(lot);
            this.qty += lot.getBoxCode();
        }
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

    @Override
    public String toString() {
        return "InventoryBalance{" +
                "balanceId=" + balanceId +
                ", bay=" + bay +
                ", lots=" + lots +
                ", qty=" + qty +
                '}';
    }
}
