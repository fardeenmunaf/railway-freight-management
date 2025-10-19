package app.model;

import java.time.LocalDate;

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

    private String lotId;
    private String sku;
    private String boxCode;
    private LocalDate expDate;
    private LocalDate receivedDate;

    /**
     * Default constructor that creates an InventoryLot with null/default values.
     * Initializes all String fields to null and date fields to null.
     */

    public InventoryLot() {
        this.lotId = null;
        this.sku = null;
        this.boxCode = null;
        this.expDate = null;
        this.receivedDate = null;
    }

    /**
     * Parameterized constructor that creates an InventoryLot with specified values.
     *
     * @param lotId the unique identifier for this inventory lot
     * @param sku the stock keeping unit identifier for the product
     * @param boxCode the code identifying the box type or packaging
     * @param expDate the expiration date of the products in this lot
     * @param receivedDate the date when this lot was received in the warehouse
     */

    public InventoryLot(String lotId, String sku, String boxCode, LocalDate expDate, LocalDate receivedDate) {
        this.lotId = lotId;
        this.sku = sku;
        this.boxCode = boxCode;
        this.expDate = expDate;
        this.receivedDate = receivedDate;
    }

    /**
     * Gets the unique identifier of this inventory lot.
     *
     * @return the lot identifier as a String
     */

    public String getLotId() {
        return lotId;
    }

    /**
     * Sets the unique identifier of this inventory lot.
     *
     * @param lotId the lot identifier to set
     */

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    /**
     * Gets the stock keeping unit identifier for the product.
     *
     * @return the SKU as a String
     */

    public String getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit identifier for the product.
     *
     * @param sku the SKU to set
     */

    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the code identifying the box type or packaging.
     *
     * @return the box code as a String
     */

    public String getBoxCode() {
        return boxCode;
    }

    /**
     * Sets the box code identifying the box type or packaging.
     *
     * @param boxCode the box code to set
     */

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    /**
     * Gets the expiration date of the products in this lot.
     *
     * @return the expiration date as a LocalDate
     */

    public LocalDate getExpDate() {
        return expDate;
    }

    /**
     * Sets the expiration date of the products in this lot.
     *
     * @param expDate the expiration date to set
     */

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
    }

    /**
     * Gets the date when this lot was received in the warehouse.
     *
     * @return the received date as a LocalDate
     */

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    /**
     * Sets the date when this lot was received in the warehouse.
     *
     * @param receivedDate the received date to set
     */

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }
}
