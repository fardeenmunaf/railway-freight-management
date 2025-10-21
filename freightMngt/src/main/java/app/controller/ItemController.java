package app.controller;

import app.model.Item;
import app.repositories.ItemRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Item entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Item objects.
 * This controller manages item data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ItemController {
    private final ItemRepository repository;

    /**
     * Default constructor that initializes the controller with the item repository.
     */
    public ItemController() {
        this.repository = Repositories.getInstance().getItemRepository();
    }

    /**
     * Creates and adds a new item to the system.
     *
     * @param item the Item object to be created
     * @return the created Item object
     */
    public Item createItem(Item item) {
        if (item != null) {
            repository.addItem(item);
        }
        return item;
    }

    /**
     * Retrieves all items from the system.
     *
     * @return a list of all Item objects
     */
    public List<Item> getAllItems() {
        return repository.getAllItems();
    }

    /**
     * Retrieves an item by its unique identifier.
     *
     * @param itemId the unique identifier of the item
     * @return an Optional containing the Item if found, or empty if not found
     */
    public Optional<Item> getItemById(Integer itemId) {
        Item item = repository.getItemById(itemId);
        return Optional.ofNullable(item);
    }

    /**
     * Retrieves an item by its SKU.
     *
     * @param sku the stock keeping unit code
     * @return an Optional containing the Item if found, or empty if not found
     */
    public Optional<Item> getItemBySku(String sku) {
        Item item = repository.getItemBySku(sku);
        return Optional.ofNullable(item);
    }

    /**
     * Updates an existing item in the system.
     *
     * @param itemId the unique identifier of the item to update
     * @param updatedItem the Item object with updated information
     * @return true if the item was updated successfully, false otherwise
     */
    public boolean updateItem(Integer itemId, Item updatedItem) {
        return repository.updateItem(itemId, updatedItem);
    }

    /**
     * Deletes an item from the system.
     *
     * @param itemId the unique identifier of the item to delete
     * @return true if the item was deleted successfully, false otherwise
     */
    public boolean deleteItem(Integer itemId) {
        return repository.deleteItem(itemId);
    }

    /**
     * Gets the next available item ID.
     *
     * @return the next item ID to be used
     */
    public Integer getNextItemId() {
        return repository.getNextItemId();
    }

    /**
     * Clears all items from the system.
     */
    public void clearAllItems() {
        repository.clear();
    }
}
