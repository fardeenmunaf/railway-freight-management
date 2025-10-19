package app.model;

/**
 * Represents an item in the freight management system.
 * <p>
 * Contains detailed information about inventory items including identification,
 * category classification, physical properties (volume, weight), and unit of measure.
 * Used for tracking and managing goods in the warehouse system.
 * </p>
 */
public class Item {
    private Integer itemId;
    private String sku;
    private String name;
    private ItemCategory itemCategory;
    private String uom;
    private Double volume;
    private Double unitWeight;

    /**
     * Default constructor for Item.
     * Initializes all fields to null.
     */
    public Item() {
        this.itemId = null;
        this.sku = null;
        this.name = null;
        this.itemCategory = null;
        this.uom = null;
        this.volume = null;
        this.unitWeight = null;
    }

    /**
     * Parameterized constructor for Item.
     *
     * @param itemId the unique identifier for the item
     * @param sku the stock keeping unit identifier
     * @param name the name of the item
     * @param itemCategory the category classification of the item
     * @param uom the unit of measure for the item
     * @param volume the volume of the item
     * @param unitWeight the weight per unit of the item
     */
    public Item(Integer itemId, String sku, String name, ItemCategory itemCategory, String uom, Double volume, Double unitWeight) {
        this.itemId = itemId;
        this.sku = sku;
        this.name = name;
        this.itemCategory = itemCategory;
        this.uom = uom;
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    /**
     * Gets the item identifier.
     *
     * @return the item ID
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * Sets the item identifier.
     *
     * @param itemId the item ID to set
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * Gets the stock keeping unit identifier.
     *
     * @return the SKU
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit identifier.
     *
     * @param sku the SKU to set
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the item name.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the item name.
     *
     * @param name the item name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the item category.
     *
     * @return the item category
     */
    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    /**
     * Sets the item category.
     *
     * @param itemCategory the item category to set
     */
    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    /**
     * Gets the unit of measure.
     *
     * @return the unit of measure
     */
    public String getUom() {
        return uom;
    }

    /**
     * Sets the unit of measure.
     *
     * @param uom the unit of measure to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

    /**
     * Gets the volume of the item.
     *
     * @return the volume
     */
    public Double getVolume() {
        return volume;
    }

    /**
     * Sets the volume of the item.
     *
     * @param volume the volume to set
     */
    public void setVolume(Double volume) {
        this.volume = volume;
    }

    /**
     * Gets the unit weight of the item.
     *
     * @return the unit weight
     */
    public Double getUnitWeight() {
        return unitWeight;
    }

    /**
     * Sets the unit weight of the item.
     *
     * @param unitWeight the unit weight to set
     */
    public void setUnitWeight(Double unitWeight) {
        this.unitWeight = unitWeight;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", itemCategory=" + itemCategory +
                ", uom='" + uom + '\'' +
                ", volume=" + volume +
                ", unitWeight=" + unitWeight +
                '}';
    }
}
