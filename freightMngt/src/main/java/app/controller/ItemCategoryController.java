package app.controller;

import app.model.ItemCategory;
import app.repositories.ItemCategoryRepository;
import app.repositories.Repositories;
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
    private final ItemCategoryRepository repository;

    /**
     * Default constructor that initializes the controller with the item category repository.
     */
    public ItemCategoryController() {
        this.repository = Repositories.getInstance().getItemCategoryRepository();
    }

    /**
     * Creates and adds a new item category to the system.
     *
     * @param itemCategory the ItemCategory object to be created
     * @return the created ItemCategory object
     */
    public ItemCategory createItemCategory(ItemCategory itemCategory) {
        if (itemCategory != null) {
            repository.addItemCategory(itemCategory);
        }
        return itemCategory;
    }

    /**
     * Retrieves all item categories from the system.
     *
     * @return a list of all ItemCategory objects
     */
    public List<ItemCategory> getAllItemCategories() {
        return repository.getAllCategories();
    }

    /**
     * Retrieves an item category by its unique identifier.
     *
     * @param categoryId the unique identifier of the item category
     * @return an Optional containing the ItemCategory if found, or empty if not found
     */
    public Optional<ItemCategory> getItemCategoryById(Integer categoryId) {
        ItemCategory itemCategory = repository.getCategoryById(categoryId);
        return Optional.ofNullable(itemCategory);
    }

    /**
     * Updates an existing item category in the system.
     *
     * @param categoryId the unique identifier of the item category to update
     * @param updatedItemCategory the ItemCategory object with updated information
     * @return true if the item category was updated successfully, false otherwise
     */
    public boolean updateItemCategory(Integer categoryId, ItemCategory updatedItemCategory) {
        if (updatedItemCategory == null) {
            return false;
        }
        return repository.updateItemCategory(categoryId, updatedItemCategory);
    }

    /**
     * Deletes an item category from the system.
     *
     * @param categoryId the unique identifier of the item category to delete
     * @return true if the item category was deleted successfully, false otherwise
     */
    public boolean deleteItemCategory(Integer categoryId) {
        return repository.deleteItemCategory(categoryId);
    }

    /**
     * Gets the next available item category ID.
     *
     * @return the next item category ID to be used
     */
    public Integer getNextItemCategoryId() {
        return repository.getNextCategoryId();
    }

    /**
     * Clears all item categories from the system.
     */
    public void clearAllItemCategories() {
        repository.clear();
    }
}
