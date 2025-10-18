package app.model;

/**
 * Represents an item in the freight management system.
 * This class defines individual products or goods that can be transported,
 * stored, and tracked through the logistics network. Each item has physical
 * properties such as dimensions, weight, and volume, along with identification
 * and categorization information.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Item {

    private Integer item_id;
    private ItemCategory item_category;
    private String sku;
    private String name;
    private String uom;
    private Double volume;
    private Double unit_weight;

    /**
     * Default constructor that creates an Item with null/default values.
     * Initializes all string fields and object references to null,
     * and numeric fields to 0.0.
     */

    public Item() {
        this.item_id = null;
        this.item_category = null;
        this.sku = null;
        this.name = null;
        this.uom = null;
        this.volume = 0.0;
        this.unit_weight = 0.0;
    }

    /**
     * Parameterized constructor that creates an Item with specified values.
     *
     * @param item_id       the unique identifier for this item
     * @param item_category the category this item belongs to
     * @param sku           the stock keeping unit code
     * @param name          the descriptive name of the item
     * @param uom           the unit of measure for this item
     * @param volume        the volume of the item
     * @param unit_weight   the weight per unit of the item
     */

    public Item(Integer item_id, ItemCategory item_category, String sku, String name, String uom, Double volume, Double unit_weight) {
        this.item_id = item_id;
        this.item_category = item_category;
        this.sku = sku;
        this.name = name;
        this.uom = uom;
        this.volume = volume;
        this.unit_weight = unit_weight;
    }

    /**
     * Gets the unique identifier of this item.
     *
     * @return the item identifier as an Integer
     */

    public Integer getItemId() {
        return item_id;
    }

    /**
     * Sets the unique identifier of this item.
     *
     * @param item_id the item identifier to set
     */

    public void setItemId(Integer item_id) {
        this.item_id = item_id;
    }

    /**
     * Gets the category of this item.
     *
     * @return the item category as an ItemCategory object
     */

    public ItemCategory getItemCategory() {
        return item_category;
    }

    /**
     * Sets the category of this item.
     *
     * @param item_category the item category to set
     */

    public void setItemCategory(ItemCategory item_category) {
        this.item_category = item_category;
    }

    /**
     * Gets the stock keeping unit (SKU) of this item.
     *
     * @return the SKU as a String
     */

    public String getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit (SKU) of this item.
     *
     * @param sku the SKU to set
     */

    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the name of this item.
     *
     * @return the item name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this item.
     *
     * @param name the item name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the unit of measure for this item.
     *
     * @return the unit of measure as a String
     */

    public String getUom() {
        return uom;
    }

    /**
     * Sets the unit of measure for this item.
     *
     * @param uom the unit of measure to set
     */

    public void setUom(String uom) {
        this.uom = uom;
    }

    /**
     * Gets the volume of this item.
     *
     * @return the volume as a double
     */

    public double getVolume() {
        return volume;
    }

    /**
     * Sets the volume of this item.
     *
     * @param volume the volume to set
     */

    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
     * Gets the unit weight of this item.
     *
     * @return the unit weight as a double
     */

    public double getUnitWeight() {
        return unit_weight;
    }

    /**
     * Sets the unit weight of this item.
     *
     * @param unit_weight the unit weight to set
     */

    public void setUnitWeight(double unit_weight) {
        this.unit_weight = unit_weight;
    }
}
