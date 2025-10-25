package app.controller;

import app.model.Item;
import app.model.ItemCategory;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ItemControllerTest {

    private ItemController controller;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemCategory category1;
    private ItemCategory category2;

    @Before
    public void setUp() {
        Repositories.getInstance().getItemRepository().clear();

        controller = new ItemController();

        category1 = new ItemCategory(1, "Electronics");
        category2 = new ItemCategory(2, "Clothing");

        item1 = new Item(1, category1, "SKU001", "Laptop", "unit", 0.5, 2.5);
        item2 = new Item(2, category1, "SKU002", "Mouse", "unit", 0.1, 0.2);
        item3 = new Item(3, category2, "SKU003", "T-Shirt", "unit", 0.05, 0.3);
    }

    @Test
    public void testCreateItem() {
        Item result = controller.createItem(item1);

        assertNotNull(result);
        assertEquals(item1, result);
        assertEquals(1, controller.getAllItems().size());
    }

    @Test
    public void testCreateItemWithNull() {
        Item result = controller.createItem(null);

        assertNull(result);
        assertEquals(0, controller.getAllItems().size());
    }

    @Test
    public void testGetAllItems() {
        controller.createItem(item1);
        controller.createItem(item2);
        controller.createItem(item3);

        List<Item> items = controller.getAllItems();

        assertEquals(3, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
        assertTrue(items.contains(item3));
    }

    @Test
    public void testGetAllItemsEmpty() {
        List<Item> items = controller.getAllItems();

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    public void testGetItemByIdFound() {
        controller.createItem(item1);
        controller.createItem(item2);

        Optional<Item> result = controller.getItemById(1);

        assertTrue(result.isPresent());
        assertEquals(item1, result.get());
    }

    @Test
    public void testGetItemByIdNotFound() {
        controller.createItem(item1);

        Optional<Item> result = controller.getItemById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetItemBySkuFound() {
        controller.createItem(item1);
        controller.createItem(item2);

        Optional<Item> result = controller.getItemBySku("SKU001");

        assertTrue(result.isPresent());
        assertEquals(item1, result.get());
    }

    @Test
    public void testGetItemBySkuNotFound() {
        controller.createItem(item1);

        Optional<Item> result = controller.getItemBySku("SKU999");

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetItemBySkuWithNull() {
        controller.createItem(item1);

        Optional<Item> result = controller.getItemBySku(null);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateItemSuccess() {
        controller.createItem(item1);

        Item updatedItem = new Item(1, category1, "SKU001", "Gaming Laptop", "unit", 0.6, 3.0);

        boolean result = controller.updateItem(1, updatedItem);

        assertTrue(result);
        Optional<Item> retrievedItem = controller.getItemById(1);
        assertTrue(retrievedItem.isPresent());
        assertEquals("Gaming Laptop", retrievedItem.get().getName());
        assertEquals(0.6, retrievedItem.get().getVolume(), 0.001);
    }

    @Test
    public void testUpdateItemNotFound() {
        controller.createItem(item1);

        boolean result = controller.updateItem(999, item2);

        assertFalse(result);
    }

    @Test
    public void testUpdateItemWithNull() {
        controller.createItem(item1);

        boolean result = controller.updateItem(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteItemSuccess() {
        controller.createItem(item1);
        controller.createItem(item2);
        controller.createItem(item3);

        boolean result = controller.deleteItem(2);

        assertTrue(result);
        assertEquals(2, controller.getAllItems().size());
        assertFalse(controller.getItemById(2).isPresent());
    }

    @Test
    public void testDeleteItemNotFound() {
        controller.createItem(item1);

        boolean result = controller.deleteItem(999);

        assertFalse(result);
        assertEquals(1, controller.getAllItems().size());
    }

    @Test
    public void testGetNextItemId() {
        Integer nextId = controller.getNextItemId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllItems() {
        controller.createItem(item1);
        controller.createItem(item2);
        controller.createItem(item3);
        assertEquals(3, controller.getAllItems().size());

        controller.clearAllItems();

        assertEquals(0, controller.getAllItems().size());
    }

    @Test
    public void testConstructor() {
        ItemController newController = new ItemController();

        assertNotNull(newController);
        assertNotNull(newController.getAllItems());
    }

    @Test
    public void testMultipleItemOperations() {
        controller.createItem(item1);
        controller.createItem(item2);
        controller.createItem(item3);
        assertEquals(3, controller.getAllItems().size());

        Integer nextId = controller.getNextItemId();
        assertNotNull(nextId);

        Item item4 = new Item(nextId, category2, "SKU004", "Jeans", "unit", 0.08, 0.5);
        controller.createItem(item4);
        assertEquals(4, controller.getAllItems().size());

        Item updatedItem = new Item(2, category1, "SKU002", "Wireless Mouse", "unit", 0.12, 0.25);
        assertTrue(controller.updateItem(2, updatedItem));

        assertTrue(controller.deleteItem(1));
        assertEquals(3, controller.getAllItems().size());

        assertFalse(controller.getItemById(1).isPresent());
        assertTrue(controller.getItemById(2).isPresent());
        assertTrue(controller.getItemById(3).isPresent());
        assertTrue(controller.getItemById(nextId).isPresent());
    }
}

