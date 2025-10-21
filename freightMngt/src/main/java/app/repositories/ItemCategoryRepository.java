package app.repositories;

import app.model.ItemCategory;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing item categories in the freight management system.
 * This class provides methods to add, retrieve, and manage item category entities.
 * It maintains a collection of all item categories and allows searching by category ID or name.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ItemCategoryRepository {

    private final List<ItemCategory> categories = new ArrayList<>();

    /**
     * Adds a new item category to the repository if it doesn't already exist.
     *
     * @param category the item category to add
     */
    public void addItemCategory(ItemCategory category) {
        if (category != null && category.getCategoryId() != null) {
            if (!containsCategory(category.getCategoryId())) {
                categories.add(category);
            }
        }
    }

    /**
     * Retrieves all item categories in the repository.
     *
     * @return a new list containing all item categories
     */
    public List<ItemCategory> getAllCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * Retrieves a specific item category by its ID.
     *
     * @param categoryId the category identifier
     * @return the matching item category, or null if not found
     */
    public ItemCategory getCategoryById(Integer categoryId) {
        for (ItemCategory category : categories) {
            if (categoryId.equals(category.getCategoryId())) {
                return category;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific item category by its name.
     *
     * @param name the category name
     * @return the matching item category, or null if not found
     */
    public ItemCategory getCategoryByName(String name) {
        for (ItemCategory category : categories) {
            if (name != null && name.equals(category.getName())) {
                return category;
            }
        }
        return null;
    }

    /**
     * Gets the next available category ID.
     * Finds the maximum category ID currently in use and returns the next sequential ID.
     *
     * @return the next available category ID (max current ID + 1)
     */
    public Integer getNextCategoryId() {
        int maxId = 0;
        for (ItemCategory category : categories) {
            if (category.getCategoryId() != null && category.getCategoryId() > maxId) {
                maxId = category.getCategoryId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a category exists in the repository by its ID.
     *
     * @param categoryId the category identifier
     * @return true if the category exists, false otherwise
     */
    private boolean containsCategory(Integer categoryId) {
        return getCategoryById(categoryId) != null;
    }

    /**
     * Checks if a category with the given name exists in the repository.
     *
     * @param name the category name
     * @return true if a category with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return getCategoryByName(name) != null;
    }

    /**
     * Clears all item categories from the repository.
     */
    public void clear() {
        categories.clear();
    }
}
