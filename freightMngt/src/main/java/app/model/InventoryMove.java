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

    private Integer move_id;
    private InventoryLot lot;
    private WarehouseBay from_bay;
    private WarehouseBay to_bay;
    private Integer qty;
    private String reason;
    private LocalDateTime created_at;

    /**
     * Default constructor that creates an InventoryMove with null/default values.
     * Initializes object references to null, and created_at to current timestamp.
     */

    public InventoryMove() {
        this.move_id = null;
        this.lot= null;
        this.from_bay = null;
        this.to_bay = null;
        this.qty = null;
        this.reason = null;
        this.created_at = LocalDateTime.now();
    }

    /**
     * Parameterized constructor that creates an InventoryMove with specified values.
     *
     * @param move_id the unique identifier for this inventory move
     * @param lot_id the inventory lot being moved
     * @param from_bay the source bay from which the inventory is moved
     * @param to_bay the destination bay to which the inventory is moved
     * @param qty the quantity of items being moved
     * @param reason the reason for this inventory movement
     * @param created_at the timestamp when this move was created
     */

    public InventoryMove(Integer move_id, InventoryLot lot_id, WarehouseBay from_bay, WarehouseBay to_bay, Integer qty, String reason, LocalDateTime created_at) {
        this.move_id = move_id;
        this.lot = lot_id;
        this.from_bay = from_bay;
        this.to_bay = to_bay;
        this.qty = qty;
        this.reason = reason;
        this.created_at = created_at;
    }

    /**
     * Gets the unique identifier of this inventory move.
     *
     * @return the move identifier as a Integer
     */

    public Integer getMoveId() {
        return move_id;
    }

    /**
     * Sets the unique identifier of this inventory move.
     *
     * @param move_id the move identifier to set
     */

    public void setMoveId(Integer move_id) {
        this.move_id = move_id;
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
        return from_bay;
    }

    /**
     * Sets the source bay from which the inventory is moved.
     *
     * @param from_bay the source bay to set
     */

    public void setFromBayId(WarehouseBay from_bay) {
        this.from_bay = from_bay;
    }

    /**
     * Gets the destination bay to which the inventory is moved.
     *
     * @return the destination bay as a WarehouseBay object
     */

    public WarehouseBay getToBay() {
        return to_bay;
    }

    /**
     * Sets the destination bay to which the inventory is moved.
     *
     * @param to_bay the destination bay to set
     */

    public void setToBay(WarehouseBay to_bay) {
        this.to_bay = to_bay;
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
        return created_at;
    }

    /**
     * Sets the timestamp when this move was created.
     *
     * @param created_at the creation timestamp to set
     */

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
