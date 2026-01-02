package app.model;

/**
 * Represents a cargo operation (loading or unloading) at a facility during freight transportation.
 * Each operation specifies what item is being loaded/unloaded, the quantity, and at which facility.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CargoOperation {

    private Integer operationId;
    private OperationType operationType;
    private Item item;
    private Integer quantity;
    private Facility facility;
    private String notes;

    /**
     * Enum for cargo operation type.
     */
    public enum OperationType {
        LOAD,
        UNLOAD
    }

    /**
     * Default constructor that creates a CargoOperation with null/default values.
     */
    public CargoOperation() {
        this.operationId = null;
        this.operationType = null;
        this.item = null;
        this.quantity = null;
        this.facility = null;
        this.notes = null;
    }

    /**
     * Parameterized constructor that creates a CargoOperation with specified values.
     *
     * @param operationId the unique identifier for this operation
     * @param operationType the type of operation (LOAD or UNLOAD)
     * @param item the item being loaded/unloaded
     * @param quantity the quantity of items
     * @param facility the facility where the operation occurs
     * @param notes additional notes about the operation
     */
    public CargoOperation(Integer operationId, OperationType operationType, Item item,
                          Integer quantity, Facility facility, String notes) {
        this.operationId = operationId;
        this.operationType = operationType;
        this.item = item;
        this.quantity = quantity;
        this.facility = facility;
        this.notes = notes;
    }

    // Getters and Setters

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CargoOperation{" +
                "operationId=" + operationId +
                ", operationType=" + operationType +
                ", item=" + (item != null ? item.getName() : "null") +
                ", quantity=" + quantity +
                ", facility=" + (facility != null ? facility.getName() : "null") +
                '}';
    }
}

