package app.model;

/**
 * Represents a category of items in the freight management system.
 * This class defines groupings or classifications for items, allowing
 * for better organization and management of inventory. Each category
 * has a unique identifier and descriptive name.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class ItemCategory {
    private Integer categoryId;
    private String name;

    /**
     * Default constructor that creates an ItemCategory with null values.
     * Initializes all fields to null.
     */

    public ItemCategory() {
        this.categoryId = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates an ItemCategory with specified values.
     *
     * @param categoryId the unique identifier for this category
     * @param name the name of the category
     */

    public ItemCategory(Integer categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this category.
     *
     * @return the category identifier as an Integer
     */

    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the unique identifier of this category.
     *
     * @param categoryId the category identifier to set
     */

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the name of this category.
     *
     * @return the category name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this category.
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
