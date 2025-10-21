package app.controller;

import app.model.Item;
import java.util.ArrayList;
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
    private List<Item> items;

    /**
     * Default constructor that initializes the item list.
     */
    public ItemController() {
        this.items = new ArrayList<>();
    }

    /**
     * Creates and adds a new item to the system.
     *
     * @param item the Item object to be created
     * @return the created Item object
     */
    public Item createItem(Item item) {
        if (item != null) {
            items.add(item);
        }
        return item;
    }

    /**
     * Retrieves all items from the system.
     *
     * @return a list of all Item objects
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    /**
     * Retrieves an item by its unique identifier.
     *
     * @param itemId the unique identifier of the item
     * @return an Optional containing the Item if found, or empty if not found
     */
    public Optional<Item> getItemById(Integer itemId) {
        return items.stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst();
    }

    /**
     * Updates an existing item in the system.
     *
     * @param itemId the unique identifier of the item to update
     * @param updatedItem the Item object with updated information
     * @return true if the item was updated successfully, false otherwise
     */
    public boolean updateItem(Integer itemId, Item updatedItem) {
        Optional<Item> existingItem = getItemById(itemId);
        if (existingItem.isPresent()) {
            int index = items.indexOf(existingItem.get());
            items.set(index, updatedItem);
            return true;
        }
        return false;
    }

    /**
     * Deletes an item from the system.
     *
     * @param itemId the unique identifier of the item to delete
     * @return true if the item was deleted successfully, false otherwise
     */
    public boolean deleteItem(Integer itemId) {
        return items.removeIf(item -> item.getItemId().equals(itemId));
    }
}

