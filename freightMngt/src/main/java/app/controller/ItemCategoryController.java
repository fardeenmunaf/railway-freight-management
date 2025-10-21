package app.controller;

import app.model.ItemCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ItemCategory entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for ItemCategory objects.
 * This controller manages item category data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ItemCategoryController {
    private List<ItemCategory> itemCategories;

    /**
     * Default constructor that initializes the item category list.
     */
    public ItemCategoryController() {
        this.itemCategories = new ArrayList<>();
    }

    /**
     * Creates and adds a new item category to the system.
     *
     * @param itemCategory the ItemCategory object to be created
     * @return the created ItemCategory object
     */
    public ItemCategory createItemCategory(ItemCategory itemCategory) {
        if (itemCategory != null) {
            itemCategories.add(itemCategory);
        }
        return itemCategory;
    }

    /**
     * Retrieves all item categories from the system.
     *
     * @return a list of all ItemCategory objects
     */
    public List<ItemCategory> getAllItemCategories() {
        return new ArrayList<>(itemCategories);
    }

    /**
     * Retrieves an item category by its unique identifier.
     *
     * @param itemCategoryId the unique identifier of the item category
     * @return an Optional containing the ItemCategory if found, or empty if not found
     */
    public Optional<ItemCategory> getItemCategoryById(Integer itemCategoryId) {
        return itemCategories.stream()
                .filter(itemCategory -> itemCategory.getCategoryId().equals(itemCategoryId))
                .findFirst();
    }

    /**
     * Updates an existing item category in the system.
     *
     * @param itemCategoryId the unique identifier of the item category to update
     * @param updatedItemCategory the ItemCategory object with updated information
     * @return true if the item category was updated successfully, false otherwise
     */
    public boolean updateItemCategory(Integer itemCategoryId, ItemCategory updatedItemCategory) {
        Optional<ItemCategory> existingItemCategory = getItemCategoryById(itemCategoryId);
        if (existingItemCategory.isPresent()) {
            int index = itemCategories.indexOf(existingItemCategory.get());
            itemCategories.set(index, updatedItemCategory);
            return true;
        }
        return false;
    }

    /**
     * Deletes an item category from the system.
     *
     * @param itemCategoryId the unique identifier of the item category to delete
     * @return true if the item category was deleted successfully, false otherwise
     */
    public boolean deleteItemCategory(Integer itemCategoryId) {
        return itemCategories.removeIf(itemCategory -> itemCategory.getCategoryId().equals(itemCategoryId));
    }
}
