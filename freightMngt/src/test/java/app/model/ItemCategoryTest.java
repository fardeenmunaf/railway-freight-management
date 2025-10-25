package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for ItemCategory model.
 * Tests constructors, getters, setters, and business logic.
 */
public class ItemCategoryTest {

    private ItemCategory category;

    @Before
    public void setUp() {
        category = new ItemCategory();
    }

    @Test
    public void testDefaultConstructor() {
        ItemCategory defaultCategory = new ItemCategory();

        assertNull("Category ID should be null", defaultCategory.getCategoryId());
        assertNull("Name should be null", defaultCategory.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        ItemCategory paramCategory = new ItemCategory(1, "Electronics");

        assertEquals("Category ID should be 1", Integer.valueOf(1), paramCategory.getCategoryId());
        assertEquals("Name should be 'Electronics'", "Electronics", paramCategory.getName());
    }

    @Test
    public void testSetAndGetCategoryId() {
        category.setCategoryId(10);

        assertEquals("Category ID should be 10", Integer.valueOf(10), category.getCategoryId());
    }

    @Test
    public void testSetAndGetName() {
        category.setName("Furniture");

        assertEquals("Name should be 'Furniture'", "Furniture", category.getName());
    }

    @Test
    public void testSetCategoryIdToNull() {
        category.setCategoryId(5);
        category.setCategoryId(null);

        assertNull("Category ID should be null", category.getCategoryId());
    }

    @Test
    public void testSetNameToNull() {
        category.setName("Test Category");
        category.setName(null);

        assertNull("Name should be null", category.getName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        category.setCategoryId(3);
        category.setName("Food");

        assertEquals("Category ID should be 3", Integer.valueOf(3), category.getCategoryId());
        assertEquals("Name should be 'Food'", "Food", category.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        ItemCategory nullCategory = new ItemCategory(null, null);

        assertNull("Category ID should be null", nullCategory.getCategoryId());
        assertNull("Name should be null", nullCategory.getName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        ItemCategory emptyNameCategory = new ItemCategory(1, "");

        assertEquals("Category ID should be 1", Integer.valueOf(1), emptyNameCategory.getCategoryId());
        assertEquals("Name should be empty string", "", emptyNameCategory.getName());
    }

    @Test
    public void testToString() {
        category.setCategoryId(7);
        category.setName("Electronics");

        String result = category.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain categoryId", result.contains("categoryId=7"));
        assertTrue("toString should contain name", result.contains("name='Electronics'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = category.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'ItemCategory{'", result.contains("ItemCategory{"));
    }
}

