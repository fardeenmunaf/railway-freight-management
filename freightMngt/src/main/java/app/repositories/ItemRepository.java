package app.repositories;

import app.model.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing items in the freight management system.
 * This class provides methods to add, retrieve, and manage item entities.
 * It maintains a collection of all items and allows searching by item ID or SKU.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ItemRepository {

    private final List<Item> items = new ArrayList<>();

    /**
     * Adds a new item to the repository if it doesn't already exist.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        if (item != null && item.getItemId() != null) {
            if (!containsItem(item.getItemId())) {
                items.add(item);
            }
        }
    }

    /**
     * Retrieves all items in the repository.
     *
     * @return a new list containing all items
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    /**
     * Retrieves a specific item by its ID.
     *
     * @param itemId the item identifier
     * @return the matching item, or null if not found
     */
    public Item getItemById(Integer itemId) {
        for (Item item : items) {
            if (itemId.equals(item.getItemId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific item by its SKU.
     *
     * @param sku the stock keeping unit code
     * @return the matching item, or null if not found
     */
    public Item getItemBySku(String sku) {
        for (Item item : items) {
            if (sku != null && sku.equals(item.getSku())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gets the next available item ID.
     * Finds the maximum item ID currently in use and returns the next sequential ID.
     *
     * @return the next available item ID (max current ID + 1)
     */
    public Integer getNextItemId() {
        int maxId = 0;
        for (Item item : items) {
            if (item.getItemId() != null && item.getItemId() > maxId) {
                maxId = item.getItemId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if an item exists in the repository by its ID.
     *
     * @param itemId the item identifier
     * @return true if the item exists, false otherwise
     */
    private boolean containsItem(Integer itemId) {
        return getItemById(itemId) != null;
    }

    /**
     * Checks if an item with the given SKU exists in the repository.
     *
     * @param sku the stock keeping unit code
     * @return true if an item with this SKU exists, false otherwise
     */
    public boolean containsSku(String sku) {
        return getItemBySku(sku) != null;
    }

    /**
     * Clears all items from the repository.
     */
    public void clear() {
        items.clear();
    }
}
