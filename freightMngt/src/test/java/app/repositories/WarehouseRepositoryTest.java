package app.repositories;

import app.model.Warehouse;
import app.model.Facility;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for WarehouseRepository.
 * Tests all public methods including CRUD operations, search by name, and edge cases.
 */
public class WarehouseRepositoryTest {

    private WarehouseRepository repository;
    private Warehouse warehouse1;
    private Warehouse warehouse2;
    private Warehouse warehouse3;
    private Facility facility;

    @Before
    public void setUp() {
        repository = new WarehouseRepository();

        facility = new Facility();

        warehouse1 = new Warehouse(1, facility, "Main Warehouse");
        warehouse2 = new Warehouse(2, facility, "Secondary Warehouse");
        warehouse3 = new Warehouse(3, facility, "Storage Facility");
    }

    @Test
    public void testAddWarehouse() {
        repository.addWarehouse(warehouse1);

        List<Warehouse> warehouses = repository.getAllWarehouses();
        assertEquals(1, warehouses.size());
        assertTrue(warehouses.contains(warehouse1));
    }

    @Test
    public void testAddWarehouseWithNull() {
        repository.addWarehouse(null);

        List<Warehouse> warehouses = repository.getAllWarehouses();
        assertEquals(0, warehouses.size());
    }

    @Test
    public void testAddWarehouseWithNullId() {
        Warehouse warehouseWithNullId = new Warehouse(null, facility, "Test Warehouse");
        repository.addWarehouse(warehouseWithNullId);

        List<Warehouse> warehouses = repository.getAllWarehouses();
        assertEquals(0, warehouses.size());
    }

    @Test
    public void testAddDuplicateWarehouse() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse1);

        List<Warehouse> warehouses = repository.getAllWarehouses();
        assertEquals(1, warehouses.size());
    }

    @Test
    public void testGetAllWarehouses() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);
        repository.addWarehouse(warehouse3);

        List<Warehouse> warehouses = repository.getAllWarehouses();

        assertEquals(3, warehouses.size());
        assertTrue(warehouses.contains(warehouse1));
        assertTrue(warehouses.contains(warehouse2));
        assertTrue(warehouses.contains(warehouse3));
    }

    @Test
    public void testGetAllWarehousesEmpty() {
        List<Warehouse> warehouses = repository.getAllWarehouses();

        assertNotNull(warehouses);
        assertEquals(0, warehouses.size());
    }

    @Test
    public void testGetWarehouseByIdFound() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);

        Warehouse result = repository.getWarehouseById(1);

        assertNotNull(result);
        assertEquals(warehouse1, result);
    }

    @Test
    public void testGetWarehouseByIdNotFound() {
        repository.addWarehouse(warehouse1);

        Warehouse result = repository.getWarehouseById(999);

        assertNull(result);
    }

    @Test
    public void testGetWarehouseByNameFound() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);

        Warehouse result = repository.getWarehouseByName("Main Warehouse");

        assertNotNull(result);
        assertEquals(warehouse1, result);
    }

    @Test
    public void testGetWarehouseByNameNotFound() {
        repository.addWarehouse(warehouse1);

        Warehouse result = repository.getWarehouseByName("Nonexistent Warehouse");

        assertNull(result);
    }

    @Test
    public void testGetWarehouseByNameWithNull() {
        repository.addWarehouse(warehouse1);

        Warehouse result = repository.getWarehouseByName(null);

        assertNull(result);
    }

    @Test
    public void testUpdateWarehouseSuccess() {
        repository.addWarehouse(warehouse1);

        Warehouse updatedWarehouse = new Warehouse(1, facility, "Updated Main Warehouse");

        boolean result = repository.updateWarehouse(1, updatedWarehouse);

        assertTrue(result);
        Warehouse retrievedWarehouse = repository.getWarehouseById(1);
        assertNotNull(retrievedWarehouse);
        assertEquals("Updated Main Warehouse", retrievedWarehouse.getName());
    }

    @Test
    public void testUpdateWarehouseNotFound() {
        repository.addWarehouse(warehouse1);

        boolean result = repository.updateWarehouse(999, warehouse2);

        assertFalse(result);
    }

    @Test
    public void testDeleteWarehouseSuccess() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);
        repository.addWarehouse(warehouse3);

        boolean result = repository.deleteWarehouse(2);

        assertTrue(result);
        assertEquals(2, repository.getAllWarehouses().size());
        assertNull(repository.getWarehouseById(2));
    }

    @Test
    public void testDeleteWarehouseNotFound() {
        repository.addWarehouse(warehouse1);

        boolean result = repository.deleteWarehouse(999);

        assertFalse(result);
        assertEquals(1, repository.getAllWarehouses().size());
    }

    @Test
    public void testGetNextWarehouseId() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);

        Integer nextId = repository.getNextWarehouseId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextWarehouseIdEmpty() {
        Integer nextId = repository.getNextWarehouseId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testContainsNameTrue() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);

        boolean result = repository.containsName("Main Warehouse");

        assertTrue(result);
    }

    @Test
    public void testContainsNameFalse() {
        repository.addWarehouse(warehouse1);

        boolean result = repository.containsName("Nonexistent Warehouse");

        assertFalse(result);
    }

    @Test
    public void testContainsNameWithNull() {
        repository.addWarehouse(warehouse1);

        boolean result = repository.containsName(null);

        assertFalse(result);
    }

    @Test
    public void testClear() {
        repository.addWarehouse(warehouse1);
        repository.addWarehouse(warehouse2);
        repository.addWarehouse(warehouse3);
        assertEquals(3, repository.getAllWarehouses().size());

        repository.clear();

        assertEquals(0, repository.getAllWarehouses().size());
    }

    @Test
    public void testConstructor() {
        WarehouseRepository newRepository = new WarehouseRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllWarehouses());
    }
}
