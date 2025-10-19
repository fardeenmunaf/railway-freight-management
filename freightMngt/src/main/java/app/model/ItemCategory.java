package app.model;

/**
 * Represents an item category in the freight management system.
 * <p>
 * Contains information about category identification and naming,
 * used to classify items in the inventory system.
 * </p>
 */
public class ItemCategory {
    private Integer categoryId;
    private String name;

    /**
     * Default constructor for ItemCategory.
     * Initializes all fields to null.
     */
    public ItemCategory() {
        this.categoryId = null;
        this.name = null;
    }

    /**
     * Parameterized constructor for ItemCategory.
     *
     * @param categoryId the unique identifier for the category
     * @param name the name of the category
     */
    public ItemCategory(Integer categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    /**
     * Gets the category identifier.
     *
     * @return the category ID
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category identifier.
     *
     * @param categoryId the category ID to set
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the category name.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the category name.
     *
     * @param name the category name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ItemCategory{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }
}
