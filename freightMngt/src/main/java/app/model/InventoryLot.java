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

    private String lot_id;
    private String sku;
    private String box_code;
    private LocalDate exp_date;
    private LocalDate received_date;

    /**
     * Default constructor that creates an InventoryLot with null/default values.
     * Initializes all String fields to null and date fields to null.
     */

    public InventoryLot() {
        this.lot_id = null;
        this.sku = null;
        this.box_code = null;
        this.exp_date = null;
        this.received_date = null;
    }

    /**
     * Parameterized constructor that creates an InventoryLot with specified values.
     *
     * @param lot_id the unique identifier for this inventory lot
     * @param sku the stock keeping unit identifier for the product
     * @param box_code the code identifying the box type or packaging
     * @param exp_date the expiration date of the products in this lot
     * @param received_date the date when this lot was received in the warehouse
     */

    public InventoryLot(String lot_id, String sku, String box_code, LocalDate exp_date, LocalDate received_date) {
        this.lot_id = lot_id;
        this.sku = sku;
        this.box_code = box_code;
        this.exp_date = exp_date;
        this.received_date = received_date;
    }

    /**
     * Gets the unique identifier of this inventory lot.
     *
     * @return the lot identifier as a String
     */

    public String getLotId() {
        return lot_id;
    }

    /**
     * Sets the unique identifier of this inventory lot.
     *
     * @param lot_id the lot identifier to set
     */

    public void setLotId(String lot_id) {
        this.lot_id = lot_id;
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
        return box_code;
    }

    /**
     * Sets the code identifying the box type or packaging.
     *
     * @param box_code the box code to set
     */

    public void setBoxCode(String box_code) {
        this.box_code = box_code;
    }

    /**
     * Gets the expiration date of the products in this lot.
     *
     * @return the expiration date as a LocalDate
     */

    public LocalDate getExpDate() {
        return exp_date;
    }

    /**
     * Sets the expiration date of the products in this lot.
     *
     * @param exp_date the expiration date to set
     */

    public void setExpDate(LocalDate exp_date) {
        this.exp_date = exp_date;
    }

    /**
     * Gets the date when this lot was received in the warehouse.
     *
     * @return the received date as a LocalDate
     */

    public LocalDate getReceivedDate() {
        return received_date;
    }

    /**
     * Sets the date when this lot was received in the warehouse.
     *
     * @param received_date the received date to set
     */

    public void setReceivedDate(LocalDate received_date) {
        this.received_date = received_date;
    }
}
