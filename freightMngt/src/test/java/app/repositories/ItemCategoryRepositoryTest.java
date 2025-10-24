package app.repositories;

import app.model.ItemCategory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for ItemCategoryRepository.
 * Tests all public methods including CRUD operations, search by name, and edge cases.
 */
public class ItemCategoryRepositoryTest {

    private ItemCategoryRepository repository;
    private ItemCategory category1;
    private ItemCategory category2;
    private ItemCategory category3;

    @Before
    public void setUp() {
        repository = new ItemCategoryRepository();

        category1 = new ItemCategory(1, "Electronics");
        category2 = new ItemCategory(2, "Furniture");
        category3 = new ItemCategory(3, "Food");
    }

    @Test
    public void testAddItemCategory() {
        repository.addItemCategory(category1);

        List<ItemCategory> categories = repository.getAllCategories();
        assertEquals(1, categories.size());
        assertTrue(categories.contains(category1));
    }

    @Test
    public void testAddItemCategoryWithNull() {
        repository.addItemCategory(null);

        List<ItemCategory> categories = repository.getAllCategories();
        assertEquals(0, categories.size());
    }

    @Test
    public void testAddItemCategoryWithNullId() {
        ItemCategory categoryWithNullId = new ItemCategory(null, "Test Category");
        repository.addItemCategory(categoryWithNullId);

        List<ItemCategory> categories = repository.getAllCategories();
        assertEquals(0, categories.size());
    }

    @Test
    public void testAddDuplicateItemCategory() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category1);

        List<ItemCategory> categories = repository.getAllCategories();
        assertEquals(1, categories.size());
    }

    @Test
    public void testGetAllCategories() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);
        repository.addItemCategory(category3);

        List<ItemCategory> categories = repository.getAllCategories();

        assertEquals(3, categories.size());
        assertTrue(categories.contains(category1));
        assertTrue(categories.contains(category2));
        assertTrue(categories.contains(category3));
    }

    @Test
    public void testGetAllCategoriesEmpty() {
        List<ItemCategory> categories = repository.getAllCategories();

        assertNotNull(categories);
        assertEquals(0, categories.size());
    }

    @Test
    public void testGetCategoryByIdFound() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);

        ItemCategory result = repository.getCategoryById(1);

        assertNotNull(result);
        assertEquals(category1, result);
    }

    @Test
    public void testGetCategoryByIdNotFound() {
        repository.addItemCategory(category1);

        ItemCategory result = repository.getCategoryById(999);

        assertNull(result);
    }

    @Test
    public void testGetCategoryByNameFound() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);

        ItemCategory result = repository.getCategoryByName("Electronics");

        assertNotNull(result);
        assertEquals(category1, result);
    }

    @Test
    public void testGetCategoryByNameNotFound() {
        repository.addItemCategory(category1);

        ItemCategory result = repository.getCategoryByName("NonExistent");

        assertNull(result);
    }

    @Test
    public void testGetCategoryByNameWithNull() {
        repository.addItemCategory(category1);

        ItemCategory result = repository.getCategoryByName(null);

        assertNull(result);
    }

    @Test
    public void testUpdateItemCategorySuccess() {
        repository.addItemCategory(category1);

        ItemCategory updatedCategory = new ItemCategory(1, "Updated Electronics");

        boolean result = repository.updateItemCategory(1, updatedCategory);

        assertTrue(result);
        ItemCategory retrievedCategory = repository.getCategoryById(1);
        assertNotNull(retrievedCategory);
        assertEquals("Updated Electronics", retrievedCategory.getName());
    }

    @Test
    public void testUpdateItemCategoryNotFound() {
        repository.addItemCategory(category1);

        boolean result = repository.updateItemCategory(999, category2);

        assertFalse(result);
    }

    @Test
    public void testDeleteItemCategorySuccess() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);
        repository.addItemCategory(category3);

        boolean result = repository.deleteItemCategory(2);

        assertTrue(result);
        assertEquals(2, repository.getAllCategories().size());
        assertNull(repository.getCategoryById(2));
    }

    @Test
    public void testDeleteItemCategoryNotFound() {
        repository.addItemCategory(category1);

        boolean result = repository.deleteItemCategory(999);

        assertFalse(result);
        assertEquals(1, repository.getAllCategories().size());
    }

    @Test
    public void testGetNextCategoryId() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);

        Integer nextId = repository.getNextCategoryId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextCategoryIdEmpty() {
        Integer nextId = repository.getNextCategoryId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testGetNextCategoryIdWithNullIds() {
        repository.addItemCategory(category1);

        Integer nextId = repository.getNextCategoryId();

        assertEquals(Integer.valueOf(2), nextId);
    }

    @Test
    public void testContainsNameTrue() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);

        boolean result = repository.containsName("Electronics");

        assertTrue(result);
    }

    @Test
    public void testContainsNameFalse() {
        repository.addItemCategory(category1);

        boolean result = repository.containsName("NonExistent");

        assertFalse(result);
    }

    @Test
    public void testContainsNameWithNull() {
        repository.addItemCategory(category1);

        boolean result = repository.containsName(null);

        assertFalse(result);
    }

    @Test
    public void testClear() {
        repository.addItemCategory(category1);
        repository.addItemCategory(category2);
        repository.addItemCategory(category3);
        assertEquals(3, repository.getAllCategories().size());

        repository.clear();

        assertEquals(0, repository.getAllCategories().size());
    }

    @Test
    public void testConstructor() {
        ItemCategoryRepository newRepository = new ItemCategoryRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllCategories());
    }
}

