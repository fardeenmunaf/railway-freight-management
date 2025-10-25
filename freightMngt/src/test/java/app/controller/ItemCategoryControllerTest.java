package app.controller;

import app.model.ItemCategory;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ItemCategoryControllerTest {

    private ItemCategoryController controller;
    private ItemCategory category1;
    private ItemCategory category2;
    private ItemCategory category3;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getItemCategoryRepository().clear();

        controller = new ItemCategoryController();

        category1 = new ItemCategory(1, "Electronics");
        category2 = new ItemCategory(2, "Clothing");
        category3 = new ItemCategory(3, "Food");
    }

    @Test
    public void testCreateItemCategory() {
        ItemCategory result = controller.createItemCategory(category1);

        assertNotNull(result);
        assertEquals(category1, result);
        assertEquals(1, controller.getAllItemCategories().size());
    }

    @Test
    public void testCreateItemCategoryWithNull() {
        ItemCategory result = controller.createItemCategory(null);

        assertNull(result);
        assertEquals(0, controller.getAllItemCategories().size());
    }

    @Test
    public void testGetAllItemCategories() {
        controller.createItemCategory(category1);
        controller.createItemCategory(category2);
        controller.createItemCategory(category3);

        List<ItemCategory> categories = controller.getAllItemCategories();

        assertEquals(3, categories.size());
        assertTrue(categories.contains(category1));
        assertTrue(categories.contains(category2));
        assertTrue(categories.contains(category3));
    }

    @Test
    public void testGetAllItemCategoriesEmpty() {
        List<ItemCategory> categories = controller.getAllItemCategories();

        assertNotNull(categories);
        assertEquals(0, categories.size());
    }

    @Test
    public void testGetItemCategoryByIdFound() {
        controller.createItemCategory(category1);
        controller.createItemCategory(category2);

        Optional<ItemCategory> result = controller.getItemCategoryById(1);

        assertTrue(result.isPresent());
        assertEquals(category1, result.get());
    }

    @Test
    public void testGetItemCategoryByIdNotFound() {
        controller.createItemCategory(category1);

        Optional<ItemCategory> result = controller.getItemCategoryById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateItemCategorySuccess() {
        controller.createItemCategory(category1);

        ItemCategory updatedCategory = new ItemCategory(1, "Electronics & Gadgets");

        boolean result = controller.updateItemCategory(1, updatedCategory);

        assertTrue(result);
        Optional<ItemCategory> retrievedCategory = controller.getItemCategoryById(1);
        assertTrue(retrievedCategory.isPresent());
        assertEquals("Electronics & Gadgets", retrievedCategory.get().getName());
    }

    @Test
    public void testUpdateItemCategoryNotFound() {
        controller.createItemCategory(category1);

        boolean result = controller.updateItemCategory(999, category2);

        assertFalse(result);
    }

    @Test
    public void testUpdateItemCategoryWithNull() {
        controller.createItemCategory(category1);

        boolean result = controller.updateItemCategory(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteItemCategorySuccess() {
        controller.createItemCategory(category1);
        controller.createItemCategory(category2);
        controller.createItemCategory(category3);

        boolean result = controller.deleteItemCategory(2);

        assertTrue(result);
        assertEquals(2, controller.getAllItemCategories().size());
        assertFalse(controller.getItemCategoryById(2).isPresent());
    }

    @Test
    public void testDeleteItemCategoryNotFound() {
        controller.createItemCategory(category1);

        boolean result = controller.deleteItemCategory(999);

        assertFalse(result);
        assertEquals(1, controller.getAllItemCategories().size());
    }

    @Test
    public void testGetNextItemCategoryId() {
        Integer nextId = controller.getNextItemCategoryId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testConstructor() {
        ItemCategoryController newController = new ItemCategoryController();

        assertNotNull(newController);
        assertNotNull(newController.getAllItemCategories());
    }

    @Test
    public void testClearAllItemCategories() {
        controller.createItemCategory(category1);
        controller.createItemCategory(category2);
        controller.createItemCategory(category3);
        assertEquals(3, controller.getAllItemCategories().size());

        controller.clearAllItemCategories();

        assertEquals(0, controller.getAllItemCategories().size());
    }

    @Test
    public void testMultipleItemCategoryOperations() {
        // Create multiple categories
        controller.createItemCategory(category1);
        controller.createItemCategory(category2);
        controller.createItemCategory(category3);
        assertEquals(3, controller.getAllItemCategories().size());

        // Get next ID
        Integer nextId = controller.getNextItemCategoryId();
        assertNotNull(nextId);

        // Create another category with next ID
        ItemCategory category4 = new ItemCategory(nextId, "Furniture");
        controller.createItemCategory(category4);
        assertEquals(4, controller.getAllItemCategories().size());

        // Update one
        ItemCategory updatedCategory = new ItemCategory(2, "Apparel & Clothing");
        assertTrue(controller.updateItemCategory(2, updatedCategory));

        // Delete one
        assertTrue(controller.deleteItemCategory(1));
        assertEquals(3, controller.getAllItemCategories().size());

        // Verify final state
        assertFalse(controller.getItemCategoryById(1).isPresent());
        assertTrue(controller.getItemCategoryById(2).isPresent());
        assertTrue(controller.getItemCategoryById(3).isPresent());
        assertTrue(controller.getItemCategoryById(nextId).isPresent());
    }

    @Test
    public void testSequentialOperations() {
        // Test creating one by one and verifying
        controller.createItemCategory(category1);
        assertEquals(1, controller.getAllItemCategories().size());

        controller.createItemCategory(category2);
        assertEquals(2, controller.getAllItemCategories().size());

        // Verify both are retrievable
        assertTrue(controller.getItemCategoryById(1).isPresent());
        assertTrue(controller.getItemCategoryById(2).isPresent());

        // Update first one
        ItemCategory updated = new ItemCategory(1, "Updated Electronics");
        controller.updateItemCategory(1, updated);

        Optional<ItemCategory> retrieved = controller.getItemCategoryById(1);
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Electronics", retrieved.get().getName());
    }
}

