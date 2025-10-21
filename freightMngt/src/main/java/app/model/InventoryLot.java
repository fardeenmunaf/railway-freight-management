package app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an inventory lot in the freight management system.
 * Each inventory lot contains information about a specific batch of products,
 * including identification details, expiration date, and received date.
 * This class is essential for tracking product batches and their lifecycle within the warehouse.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class InventoryLot {

    private Integer lotId;
    private Integer sku;
    private Integer qty;
    private LocalDateTime expDate;
    private LocalDateTime receivedDate;

    /**
     * Default constructor that creates an InventoryLot with null/default values.
     * Initializes all String fields to null and date fields to null.
     */

    public InventoryLot() {
        this.lotId = null;
        this.sku = null;
        this.qty = null;
        this.expDate = null;
        this.receivedDate = null;
    }

    /**
     * Parameterized constructor that creates an InventoryLot with specified values.
     *
     * @param lotId the unique identifier for this inventory lot
     * @param sku the stock keeping unit identifier for the product
     * @param qty the quantity of items in this lot
     * @param expDate the expiration date of the products in this lot
     * @param receivedDate the date when this lot was received in the warehouse
     */

    public InventoryLot(Integer lotId, Integer sku, Integer qty, LocalDateTime expDate, LocalDateTime receivedDate) {
        this.lotId = lotId;
        this.sku = sku;
        this.qty = qty;
        this.expDate = expDate;
        this.receivedDate = receivedDate;
    }

    /**
     * Gets the unique identifier of this inventory lot.
     *
     * @return the lot identifier as a Integer
     */

    public Integer getLotId() {
        return lotId;
    }

    /**
     * Sets the unique identifier of this inventory lot.
     *
     * @param lotId the lot identifier to set
     */

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    /**
     * Gets the stock keeping unit identifier for the product.
     *
     * @return the SKU as a Integer
     */

    public Integer getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit identifier for the product.
     *
     * @param sku the SKU to set
     */

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    /**
     * Gets the quantity of items in this lot.
     *
     * @return the quantity as an Integer
     */

    public Integer getBoxCode() {
        return qty;
    }

    /**
     * Sets the quantity of items in this lot.
     *
     * @param qty the quantity to set
     *
     */

    public void setBoxCode(Integer qty) {
        this.qty = qty;
    }

    /**
     * Gets the expiration date of the products in this lot.
     *
     * @return the expiration date as a LocalDateTime
     */

    public LocalDateTime getExpDate() {
        return expDate;
    }

    /**
     * Sets the expiration date of the products in this lot.
     *
     * @param expDate the expiration date to set
     */

    public void setExpDate(LocalDateTime expDate) {
        this.expDate = expDate;
    }

    /**
     * Gets the date when this lot was received in the warehouse.
     *
     * @return the received date as a LocalDateTime
     */

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    /**
     * Sets the date when this lot was received in the warehouse.
     *
     * @param receivedDate the received date to set
     */

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }
}
