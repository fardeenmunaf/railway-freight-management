package app.model;

/**
 * Represents an allocation of products in a specific bay within a warehouse.
 * <p>
 * Each Allocation instance indicates the WarehouseBay where a certain
 * quantity of boxes or products is located for picking operations.
 *
 * <p>This class is part of the data model used in USEI04 - Pick Path Sequencing.</p>
 *
 * @author G113
 * @version 1.0
 */
public class Allocation {

    /** The warehouse bay where the allocated products are stored. */
    private WarehouseBay warehouseBay;

    /** The quantity of boxes or units to pick from this bay. */
    private int quantity;

    /**
     * Default constructor that creates an empty Allocation instance
     * with null/default values.
     */
    public Allocation() {
        this.warehouseBay = null;
        this.quantity = 0;
    }

    /**
     * Parameterized constructor that creates an Allocation with specified values.
     *
     * @param warehouseBay the warehouse bay associated with this allocation
     * @param quantity the quantity of boxes to be picked from the bay
     */
    public Allocation(WarehouseBay warehouseBay, int quantity) {
        this.warehouseBay = warehouseBay;
        this.quantity = quantity;
    }

    /**
     * Gets the warehouse bay associated with this allocation.
     *
     * @return the WarehouseBay object for this allocation
     */
    public WarehouseBay getWarehouseBay() {
        return warehouseBay;
    }

    /**
     * Sets the warehouse bay associated with this allocation.
     *
     * @param warehouseBay the WarehouseBay object to associate
     */
    public void setWarehouseBay(WarehouseBay warehouseBay) {
        this.warehouseBay = warehouseBay;
    }

    /**
     * Gets the quantity of boxes or units to pick from this bay.
     *
     * @return the quantity to pick
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of boxes or units to pick from this bay.
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a string representation of the Allocation,
     * including its aisle, bay number, and quantity.
     *
     * @return formatted string with Allocation information
     */
    @Override
    public String toString() {
        return "Allocation{" +
                "aisle=" + (warehouseBay != null && warehouseBay.getAisle() != null
                ? warehouseBay.getAisle().getAisleId() : "null") +
                ", bay=" + (warehouseBay != null ? warehouseBay.getBayNo() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}
