package app.model;

import java.time.LocalDateTime;

/**
 * Represents an inventory movement record in the freight management system.
 * Each inventory move tracks the transfer of a specific lot from one bay to another,
 * including the quantity moved, reason for the move, and timestamp.
 * This class is essential for maintaining audit trails of inventory movements within the warehouse.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class InventoryMove {

    private Integer moveId;
    private InventoryLot lot;
    private WarehouseBay fromBay;
    private WarehouseBay toBay;
    private Integer qty;
    private String reason;
    private LocalDateTime createdAt;

    /**
     * Default constructor that creates an InventoryMove with null/default values.
     * Initializes object references to null, and createdAt to current timestamp.
     */

    public InventoryMove() {
        this.moveId = null;
        this.lot= null;
        this.fromBay = null;
        this.toBay = null;
        this.qty = null;
        this.reason = null;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor that creates an InventoryMove with specified values.
     *
     * @param moveId the unique identifier for this inventory move
     * @param lotId the inventory lot being moved
     * @param fromBay the source bay from which the inventory is moved
     * @param toBay the destination bay to which the inventory is moved
     * @param qty the quantity of items being moved
     * @param reason the reason for this inventory movement
     * @param createdAt the timestamp when this move was created
     */

    public InventoryMove(Integer moveId, InventoryLot lotId, WarehouseBay fromBay, WarehouseBay toBay, Integer qty, String reason, LocalDateTime createdAt) {
        this.moveId = moveId;
        this.lot = lotId;
        this.fromBay = fromBay;
        this.toBay = toBay;
        this.qty = qty;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    /**
     * Gets the unique identifier of this inventory move.
     *
     * @return the move identifier as a Integer
     */

    public Integer getMoveId() {
        return moveId;
    }

    /**
     * Sets the unique identifier of this inventory move.
     *
     * @param moveId the move identifier to set
     */

    public void setMoveId(Integer moveId) {
        this.moveId = moveId;
    }

    /**
     * Gets the inventory lot being moved.
     *
     * @return the inventory lot as an InventoryLot object
     */

    public InventoryLot getLot() {
        return lot;
    }

    /**
     * Sets the inventory lot being moved.
     *
     * @param lot the inventory lot to set
     */

    public void setLot(InventoryLot lot) {
        this.lot = lot;
    }

    /**
     * Gets the source bay from which the inventory is moved.
     *
     * @return the source bay as a WarehouseBay object
     */

    public WarehouseBay getFromBay() {
        return fromBay;
    }

    /**
     * Sets the source bay from which the inventory is moved.
     *
     * @param fromBay the source bay to set
     */

    public void setFromBayId(WarehouseBay fromBay) {
        this.fromBay = fromBay;
    }

    /**
     * Gets the destination bay to which the inventory is moved.
     *
     * @return the destination bay as a WarehouseBay object
     */

    public WarehouseBay getToBay() {
        return toBay;
    }

    /**
     * Sets the destination bay to which the inventory is moved.
     *
     * @param toBay the destination bay to set
     */

    public void setToBay(WarehouseBay toBay) {
        this.toBay = toBay;
    }

    /**
     * Gets the quantity of items being moved.
     *
     * @return the quantity as an integer
     */

    public Integer getQty() {
        return qty;
    }

    /**
     * Sets the quantity of items being moved.
     *
     * @param qty the quantity to set
     */

    public void setQty(int qty) {
        this.qty = qty;
    }

    /**
     * Gets the reason for this inventory movement.
     *
     * @return the reason as a String
     */

    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for this inventory movement.
     *
     * @param reason the reason to set
     */

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the timestamp when this move was created.
     *
     * @return the creation timestamp as a LocalDateTime
     */

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this move was created.
     *
     * @param createdAt the creation timestamp to set
     */

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
