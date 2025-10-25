package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Item model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        item = new Item();
    }

    @Test
    public void testDefaultConstructor() {
        Item defaultItem = new Item();

        assertNull("Item ID should be null", defaultItem.getItemId());
        assertNull("Item Category should be null", defaultItem.getItemCategory());
        assertNull("SKU should be null", defaultItem.getSku());
        assertNull("Name should be null", defaultItem.getName());
        assertNull("UOM should be null", defaultItem.getUom());
        assertEquals("Volume should be 0.0", 0.0, defaultItem.getVolume(), 0.001);
        assertEquals("Unit Weight should be 0.0", 0.0, defaultItem.getUnitWeight(), 0.001);
    }

    @Test
    public void testParameterizedConstructor() {
        ItemCategory category = new ItemCategory(1, "Electronics");
        Item paramItem = new Item(1, category, "SKU001", "Laptop", "unit", 2.5, 1.5);

        assertEquals("Item ID should be 1", Integer.valueOf(1), paramItem.getItemId());
        assertEquals("Item Category should match", category, paramItem.getItemCategory());
        assertEquals("SKU should be 'SKU001'", "SKU001", paramItem.getSku());
        assertEquals("Name should be 'Laptop'", "Laptop", paramItem.getName());
        assertEquals("UOM should be 'unit'", "unit", paramItem.getUom());
        assertEquals("Volume should be 2.5", 2.5, paramItem.getVolume(), 0.001);
        assertEquals("Unit Weight should be 1.5", 1.5, paramItem.getUnitWeight(), 0.001);
    }

    @Test
    public void testSetAndGetItemId() {
        item.setItemId(10);

        assertEquals("Item ID should be 10", Integer.valueOf(10), item.getItemId());
    }

    @Test
    public void testSetAndGetItemCategory() {
        ItemCategory category = new ItemCategory(2, "Furniture");
        item.setItemCategory(category);

        assertEquals("Item Category should match", category, item.getItemCategory());
    }

    @Test
    public void testSetAndGetSku() {
        item.setSku("SKU123");

        assertEquals("SKU should be 'SKU123'", "SKU123", item.getSku());
    }

    @Test
    public void testSetAndGetName() {
        item.setName("Monitor");

        assertEquals("Name should be 'Monitor'", "Monitor", item.getName());
    }

    @Test
    public void testSetAndGetUom() {
        item.setUom("piece");

        assertEquals("UOM should be 'piece'", "piece", item.getUom());
    }

    @Test
    public void testSetAndGetVolume() {
        item.setVolume(5.5);

        assertEquals("Volume should be 5.5", 5.5, item.getVolume(), 0.001);
    }

    @Test
    public void testSetAndGetUnitWeight() {
        item.setUnitWeight(3.2);

        assertEquals("Unit Weight should be 3.2", 3.2, item.getUnitWeight(), 0.001);
    }

    @Test
    public void testSetItemIdToNull() {
        item.setItemId(5);
        item.setItemId(null);

        assertNull("Item ID should be null", item.getItemId());
    }

    @Test
    public void testSetItemCategoryToNull() {
        ItemCategory category = new ItemCategory(1, "Test");
        item.setItemCategory(category);
        item.setItemCategory(null);

        assertNull("Item Category should be null", item.getItemCategory());
    }

    @Test
    public void testSetSkuToNull() {
        item.setSku("SKU999");
        item.setSku(null);

        assertNull("SKU should be null", item.getSku());
    }

    @Test
    public void testSetNameToNull() {
        item.setName("Test Item");
        item.setName(null);

        assertNull("Name should be null", item.getName());
    }

    @Test
    public void testSetUomToNull() {
        item.setUom("kg");
        item.setUom(null);

        assertNull("UOM should be null", item.getUom());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        ItemCategory category = new ItemCategory(3, "Food");

        item.setItemId(7);
        item.setItemCategory(category);
        item.setSku("SKU777");
        item.setName("Apple");
        item.setUom("kg");
        item.setVolume(0.5);
        item.setUnitWeight(0.2);

        assertEquals("Item ID should be 7", Integer.valueOf(7), item.getItemId());
        assertEquals("Item Category should match", category, item.getItemCategory());
        assertEquals("SKU should be 'SKU777'", "SKU777", item.getSku());
        assertEquals("Name should be 'Apple'", "Apple", item.getName());
        assertEquals("UOM should be 'kg'", "kg", item.getUom());
        assertEquals("Volume should be 0.5", 0.5, item.getVolume(), 0.001);
        assertEquals("Unit Weight should be 0.2", 0.2, item.getUnitWeight(), 0.001);
    }

    @Test
    public void testConstructorWithNullValues() {
        Item nullItem = new Item(null, null, null, null, null, 0.0, 0.0);

        assertNull("Item ID should be null", nullItem.getItemId());
        assertNull("Item Category should be null", nullItem.getItemCategory());
        assertNull("SKU should be null", nullItem.getSku());
        assertNull("Name should be null", nullItem.getName());
        assertNull("UOM should be null", nullItem.getUom());
        assertEquals("Volume should be 0.0", 0.0, nullItem.getVolume(), 0.001);
        assertEquals("Unit Weight should be 0.0", 0.0, nullItem.getUnitWeight(), 0.001);
    }

    @Test
    public void testConstructorWithEmptyStrings() {
        ItemCategory category = new ItemCategory(1, "Test");
        Item itemWithEmptyStrings = new Item(1, category, "", "", "", 1.0, 1.0);

        assertEquals("SKU should be empty string", "", itemWithEmptyStrings.getSku());
        assertEquals("Name should be empty string", "", itemWithEmptyStrings.getName());
        assertEquals("UOM should be empty string", "", itemWithEmptyStrings.getUom());
    }

    @Test
    public void testToString() {
        ItemCategory category = new ItemCategory(1, "Electronics");
        item.setItemId(5);
        item.setItemCategory(category);
        item.setSku("SKU555");
        item.setName("Keyboard");
        item.setUom("unit");
        item.setVolume(0.8);
        item.setUnitWeight(0.4);

        String result = item.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain itemId", result.contains("itemId=5"));
        assertTrue("toString should contain sku", result.contains("sku='SKU555'"));
        assertTrue("toString should contain name", result.contains("name='Keyboard'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = item.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Item{'", result.contains("Item{"));
    }
}

