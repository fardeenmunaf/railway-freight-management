package app.controller;

import app.model.ItemCategory;
import app.repositories.ItemCategoryRepository;
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
     * Default constructor that initializes the controller with a new repository.
     */
    public ItemCategoryController() {
        this.repository = new ItemCategoryRepository();
    }

    /**
     * Constructor that accepts a repository instance.
     *
     * @param repository the ItemCategoryRepository to use
     */
    public ItemCategoryController(ItemCategoryRepository repository) {
        this.repository = repository;
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
     * @param itemCategoryId the unique identifier of the item category
     * @return an Optional containing the ItemCategory if found, or empty if not found
     */
    public Optional<ItemCategory> getItemCategoryById(Integer itemCategoryId) {
        ItemCategory category = repository.getCategoryById(itemCategoryId);
        return Optional.ofNullable(category);
    }

    /**
     * Retrieves an item category by its name.
     *
     * @param name the name of the item category
     * @return an Optional containing the ItemCategory if found, or empty if not found
     */
    public Optional<ItemCategory> getItemCategoryByName(String name) {
        ItemCategory category = repository.getCategoryByName(name);
        return Optional.ofNullable(category);
    }

    /**
     * Updates an existing item category in the system.
     *
     * @param itemCategoryId the unique identifier of the item category to update
     * @param updatedItemCategory the ItemCategory object with updated information
     * @return true if the item category was updated successfully, false otherwise
     */
    public boolean updateItemCategory(Integer itemCategoryId, ItemCategory updatedItemCategory) {
        return repository.updateItemCategory(itemCategoryId, updatedItemCategory);
    }

    /**
     * Deletes an item category from the system.
     *
     * @param itemCategoryId the unique identifier of the item category to delete
     * @return true if the item category was deleted successfully, false otherwise
     */
    public boolean deleteItemCategory(Integer itemCategoryId) {
        return repository.deleteItemCategory(itemCategoryId);
    }

    /**
     * Gets the next available category ID.
     *
     * @return the next available category ID
     */
    public Integer getNextCategoryId() {
        return repository.getNextCategoryId();
    }

    /**
     * Checks if a category with the given name exists.
     *
     * @param name the category name
     * @return true if a category with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return repository.containsName(name);
    }
}
