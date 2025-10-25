package app.repositories;

import app.model.Item;
import app.model.ItemCategory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for ItemRepository.
 * Tests all public methods including CRUD operations, SKU search, and edge cases.
 */
public class ItemRepositoryTest {

    private ItemRepository repository;
    private Item item1;
    private Item item2;
    private Item item3;

    @Before
    public void setUp() {
        repository = new ItemRepository();

        ItemCategory category = new ItemCategory(1, "Electronics");

        item1 = new Item(1, category, "SKU001", "Laptop", "unit", 2.5, 1.5);
        item2 = new Item(2, category, "SKU002", "Mouse", "unit", 0.1, 0.05);
        item3 = new Item(3, category, "SKU003", "Keyboard", "unit", 0.8, 0.4);
    }

    @Test
    public void testAddItem() {
        repository.addItem(item1);

        List<Item> items = repository.getAllItems();
        assertEquals(1, items.size());
        assertTrue(items.contains(item1));
    }

    @Test
    public void testAddItemWithNull() {
        repository.addItem(null);

        List<Item> items = repository.getAllItems();
        assertEquals(0, items.size());
    }

    @Test
    public void testAddItemWithNullId() {
        ItemCategory category = new ItemCategory(1, "Test");
        Item itemWithNullId = new Item(null, category, "SKU999", "Test Item", "unit", 1.0, 1.0);
        repository.addItem(itemWithNullId);

        List<Item> items = repository.getAllItems();
        assertEquals(0, items.size());
    }

    @Test
    public void testAddDuplicateItem() {
        repository.addItem(item1);
        repository.addItem(item1);

        List<Item> items = repository.getAllItems();
        assertEquals(1, items.size());
    }

    @Test
    public void testGetAllItems() {
        repository.addItem(item1);
        repository.addItem(item2);
        repository.addItem(item3);

        List<Item> items = repository.getAllItems();

        assertEquals(3, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
        assertTrue(items.contains(item3));
    }

    @Test
    public void testGetAllItemsEmpty() {
        List<Item> items = repository.getAllItems();

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    public void testGetItemByIdFound() {
        repository.addItem(item1);
        repository.addItem(item2);

        Item result = repository.getItemById(1);

        assertNotNull(result);
        assertEquals(item1, result);
    }

    @Test
    public void testGetItemByIdNotFound() {
        repository.addItem(item1);

        Item result = repository.getItemById(999);

        assertNull(result);
    }

    @Test
    public void testGetItemBySkuFound() {
        repository.addItem(item1);
        repository.addItem(item2);

        Item result = repository.getItemBySku("SKU001");

        assertNotNull(result);
        assertEquals(item1, result);
    }

    @Test
    public void testGetItemBySkuNotFound() {
        repository.addItem(item1);

        Item result = repository.getItemBySku("NONEXISTENT");

        assertNull(result);
    }

    @Test
    public void testGetItemBySkuWithNull() {
        repository.addItem(item1);

        Item result = repository.getItemBySku(null);

        assertNull(result);
    }

    @Test
    public void testUpdateItemSuccess() {
        repository.addItem(item1);

        ItemCategory category = new ItemCategory(1, "Electronics");
        Item updatedItem = new Item(1, category, "SKU001", "Updated Laptop", "unit", 3.0, 2.0);

        boolean result = repository.updateItem(1, updatedItem);

        assertTrue(result);
        Item retrievedItem = repository.getItemById(1);
        assertNotNull(retrievedItem);
        assertEquals("Updated Laptop", retrievedItem.getName());
    }

    @Test
    public void testUpdateItemNotFound() {
        repository.addItem(item1);

        boolean result = repository.updateItem(999, item2);

        assertFalse(result);
    }

    @Test
    public void testDeleteItemSuccess() {
        repository.addItem(item1);
        repository.addItem(item2);
        repository.addItem(item3);

        boolean result = repository.deleteItem(2);

        assertTrue(result);
        assertEquals(2, repository.getAllItems().size());
        assertNull(repository.getItemById(2));
    }

    @Test
    public void testDeleteItemNotFound() {
        repository.addItem(item1);

        boolean result = repository.deleteItem(999);

        assertFalse(result);
        assertEquals(1, repository.getAllItems().size());
    }

    @Test
    public void testGetNextItemId() {
        repository.addItem(item1);
        repository.addItem(item2);

        Integer nextId = repository.getNextItemId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextItemIdEmpty() {
        Integer nextId = repository.getNextItemId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testContainsSkuTrue() {
        repository.addItem(item1);
        repository.addItem(item2);

        boolean result = repository.containsSku("SKU001");

        assertTrue(result);
    }

    @Test
    public void testContainsSkuFalse() {
        repository.addItem(item1);

        boolean result = repository.containsSku("NONEXISTENT");

        assertFalse(result);
    }

    @Test
    public void testContainsSkuWithNull() {
        repository.addItem(item1);

        boolean result = repository.containsSku(null);

        assertFalse(result);
    }

    @Test
    public void testClear() {
        repository.addItem(item1);
        repository.addItem(item2);
        repository.addItem(item3);
        assertEquals(3, repository.getAllItems().size());

        repository.clear();

        assertEquals(0, repository.getAllItems().size());
    }

    @Test
    public void testConstructor() {
        ItemRepository newRepository = new ItemRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllItems());
    }
}