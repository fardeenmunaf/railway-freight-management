package app.controller;

import app.model.Warehouse;
import app.model.Facility;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WarehouseControllerTest {

    private WarehouseController controller;
    private Warehouse warehouse1;
    private Warehouse warehouse2;
    private Warehouse warehouse3;
    private Facility facility1;
    private Facility facility2;

    @Before
    public void setUp() {
        Repositories.getInstance().getWarehouseRepository().clear();

        controller = new WarehouseController();

        facility1 = new Facility(1, "Main Facility", null, null, null, null);
        facility2 = new Facility(2, "Secondary Facility", null, null, null, null);

        warehouse1 = new Warehouse(1, facility1, "Warehouse A");
        warehouse2 = new Warehouse(2, facility1, "Warehouse B");
        warehouse3 = new Warehouse(3, facility2, "Warehouse C");
    }

    @Test
    public void testCreateWarehouse() {
        Warehouse result = controller.createWarehouse(warehouse1);

        assertNotNull(result);
        assertEquals(warehouse1, result);
        assertEquals(1, controller.getAllWarehouses().size());
    }

    @Test
    public void testCreateWarehouseWithNull() {
        Warehouse result = controller.createWarehouse(null);

        assertNull(result);
        assertEquals(0, controller.getAllWarehouses().size());
    }

    @Test
    public void testGetAllWarehouses() {
        controller.createWarehouse(warehouse1);
        controller.createWarehouse(warehouse2);
        controller.createWarehouse(warehouse3);

        List<Warehouse> warehouses = controller.getAllWarehouses();

        assertEquals(3, warehouses.size());
        assertTrue(warehouses.contains(warehouse1));
        assertTrue(warehouses.contains(warehouse2));
        assertTrue(warehouses.contains(warehouse3));
    }

    @Test
    public void testGetAllWarehousesEmpty() {
        List<Warehouse> warehouses = controller.getAllWarehouses();

        assertNotNull(warehouses);
        assertEquals(0, warehouses.size());
    }

    @Test
    public void testGetWarehouseByIdFound() {
        controller.createWarehouse(warehouse1);
        controller.createWarehouse(warehouse2);

        Optional<Warehouse> result = controller.getWarehouseById(1);

        assertTrue(result.isPresent());
        assertEquals(warehouse1, result.get());
    }

    @Test
    public void testGetWarehouseByIdNotFound() {
        controller.createWarehouse(warehouse1);

        Optional<Warehouse> result = controller.getWarehouseById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetWarehouseByNameFound() {
        controller.createWarehouse(warehouse1);
        controller.createWarehouse(warehouse2);

        Optional<Warehouse> result = controller.getWarehouseByName("Warehouse A");

        assertTrue(result.isPresent());
        assertEquals(warehouse1, result.get());
    }

    @Test
    public void testGetWarehouseByNameNotFound() {
        controller.createWarehouse(warehouse1);

        Optional<Warehouse> result = controller.getWarehouseByName("Warehouse Z");

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetWarehouseByNameWithNull() {
        controller.createWarehouse(warehouse1);

        Optional<Warehouse> result = controller.getWarehouseByName(null);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateWarehouseSuccess() {
        controller.createWarehouse(warehouse1);

        Warehouse updatedWarehouse = new Warehouse(1, facility1, "Warehouse A - Updated");

        boolean result = controller.updateWarehouse(1, updatedWarehouse);

        assertTrue(result);
        Optional<Warehouse> retrievedWarehouse = controller.getWarehouseById(1);
        assertTrue(retrievedWarehouse.isPresent());
        assertEquals("Warehouse A - Updated", retrievedWarehouse.get().getName());
    }

    @Test
    public void testUpdateWarehouseNotFound() {
        controller.createWarehouse(warehouse1);

        boolean result = controller.updateWarehouse(999, warehouse2);

        assertFalse(result);
    }

    @Test
    public void testUpdateWarehouseWithNull() {
        controller.createWarehouse(warehouse1);

        boolean result = controller.updateWarehouse(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteWarehouseSuccess() {
        controller.createWarehouse(warehouse1);
        controller.createWarehouse(warehouse2);
        controller.createWarehouse(warehouse3);

        boolean result = controller.deleteWarehouse(2);

        assertTrue(result);
        assertEquals(2, controller.getAllWarehouses().size());
        assertFalse(controller.getWarehouseById(2).isPresent());
    }

    @Test
    public void testDeleteWarehouseNotFound() {
        controller.createWarehouse(warehouse1);

        boolean result = controller.deleteWarehouse(999);

        assertFalse(result);
        assertEquals(1, controller.getAllWarehouses().size());
    }

    @Test
    public void testGetNextWarehouseId() {
        Integer nextId = controller.getNextWarehouseId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllWarehouses() {
        controller.createWarehouse(warehouse1);
        controller.createWarehouse(warehouse2);
        controller.createWarehouse(warehouse3);
        assertEquals(3, controller.getAllWarehouses().size());

        controller.clearAllWarehouses();

        assertEquals(0, controller.getAllWarehouses().size());
    }

    @Test
    public void testConstructor() {
        WarehouseController newController = new WarehouseController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWarehouses());
    }

    @Test
    public void testMultipleWarehouseOperations() {
        controller.createWarehouse(warehouse1);
        controller.createWarehouse(warehouse2);
        controller.createWarehouse(warehouse3);
        assertEquals(3, controller.getAllWarehouses().size());

        Integer nextId = controller.getNextWarehouseId();
        assertNotNull(nextId);

        Warehouse warehouse4 = new Warehouse(nextId, facility2, "Warehouse D");
        controller.createWarehouse(warehouse4);
        assertEquals(4, controller.getAllWarehouses().size());

        Warehouse updatedWarehouse = new Warehouse(2, facility1, "Warehouse B - Renovated");
        assertTrue(controller.updateWarehouse(2, updatedWarehouse));

        assertTrue(controller.deleteWarehouse(1));
        assertEquals(3, controller.getAllWarehouses().size());

        assertFalse(controller.getWarehouseById(1).isPresent());
        assertTrue(controller.getWarehouseById(2).isPresent());
        assertTrue(controller.getWarehouseById(3).isPresent());
        assertTrue(controller.getWarehouseById(nextId).isPresent());
    }
}

