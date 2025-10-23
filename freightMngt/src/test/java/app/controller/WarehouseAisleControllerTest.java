package app.controller;

import app.model.WarehouseAisle;
import app.model.Warehouse;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WarehouseAisleControllerTest {

    private WarehouseAisleController controller;
    private WarehouseAisle aisle1;
    private WarehouseAisle aisle2;
    private WarehouseAisle aisle3;
    private Warehouse warehouse1;
    private Warehouse warehouse2;

    @Before
    public void setUp() {
        Repositories.getInstance().getWarehouseAisleRepository().clear();

        controller = new WarehouseAisleController();

        warehouse1 = new Warehouse(1, null, "Warehouse A");
        warehouse2 = new Warehouse(2, null, "Warehouse B");

        aisle1 = new WarehouseAisle(1, warehouse1, 1, "A-01");
        aisle2 = new WarehouseAisle(2, warehouse1, 2, "A-02");
        aisle3 = new WarehouseAisle(3, warehouse2, 1, "B-01");
    }

    @Test
    public void testCreateWarehouseAisle() {
        WarehouseAisle result = controller.createWarehouseAisle(aisle1);

        assertNotNull(result);
        assertEquals(aisle1, result);
        assertEquals(1, controller.getAllWarehouseAisles().size());
    }

    @Test
    public void testCreateWarehouseAisleWithNull() {
        WarehouseAisle result = controller.createWarehouseAisle(null);

        assertNull(result);
        assertEquals(0, controller.getAllWarehouseAisles().size());
    }

    @Test
    public void testGetAllWarehouseAisles() {
        controller.createWarehouseAisle(aisle1);
        controller.createWarehouseAisle(aisle2);
        controller.createWarehouseAisle(aisle3);

        List<WarehouseAisle> aisles = controller.getAllWarehouseAisles();

        assertEquals(3, aisles.size());
        assertTrue(aisles.contains(aisle1));
        assertTrue(aisles.contains(aisle2));
        assertTrue(aisles.contains(aisle3));
    }

    @Test
    public void testGetAllWarehouseAislesEmpty() {
        List<WarehouseAisle> aisles = controller.getAllWarehouseAisles();

        assertNotNull(aisles);
        assertEquals(0, aisles.size());
    }

    @Test
    public void testGetWarehouseAisleFound() {
        controller.createWarehouseAisle(aisle1);
        controller.createWarehouseAisle(aisle2);

        Optional<WarehouseAisle> result = controller.getWarehouseAisle(1, 1);

        assertTrue(result.isPresent());
        assertEquals(aisle1, result.get());
    }

    @Test
    public void testGetWarehouseAisleNotFound() {
        controller.createWarehouseAisle(aisle1);

        Optional<WarehouseAisle> result = controller.getWarehouseAisle(999, 999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateWarehouseAisleSuccess() {
        controller.createWarehouseAisle(aisle1);

        WarehouseAisle updatedAisle = new WarehouseAisle(1, warehouse1, 1, "A-01-UPDATED");

        boolean result = controller.updateWarehouseAisle(1, 1, updatedAisle);

        assertTrue(result);
        Optional<WarehouseAisle> retrievedAisle = controller.getWarehouseAisle(1, 1);
        assertTrue(retrievedAisle.isPresent());
        assertEquals("A-01-UPDATED", retrievedAisle.get().getUniqueCode());
    }

    @Test
    public void testUpdateWarehouseAisleNotFound() {
        controller.createWarehouseAisle(aisle1);

        boolean result = controller.updateWarehouseAisle(999, 999, aisle2);

        assertFalse(result);
    }

    @Test
    public void testUpdateWarehouseAisleWithNull() {
        controller.createWarehouseAisle(aisle1);

        boolean result = controller.updateWarehouseAisle(1, 1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteWarehouseAisleSuccess() {
        controller.createWarehouseAisle(aisle1);
        controller.createWarehouseAisle(aisle2);
        controller.createWarehouseAisle(aisle3);

        boolean result = controller.deleteWarehouseAisle(1, 2);

        assertTrue(result);
        assertEquals(2, controller.getAllWarehouseAisles().size());
        assertFalse(controller.getWarehouseAisle(1, 2).isPresent());
    }

    @Test
    public void testDeleteWarehouseAisleNotFound() {
        controller.createWarehouseAisle(aisle1);

        boolean result = controller.deleteWarehouseAisle(999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllWarehouseAisles().size());
    }

    @Test
    public void testGetNextAisleNo() {
        Integer nextAisleNo = controller.getNextAisleNo(1);

        assertNotNull(nextAisleNo);
        assertTrue(nextAisleNo > 0);
    }

    @Test
    public void testClearAllWarehouseAisles() {
        controller.createWarehouseAisle(aisle1);
        controller.createWarehouseAisle(aisle2);
        controller.createWarehouseAisle(aisle3);
        assertEquals(3, controller.getAllWarehouseAisles().size());

        controller.clearAllWarehouseAisles();

        assertEquals(0, controller.getAllWarehouseAisles().size());
    }

    @Test
    public void testConstructor() {
        WarehouseAisleController newController = new WarehouseAisleController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWarehouseAisles());
    }

    @Test
    public void testMultipleWarehouseAisleOperations() {
        controller.createWarehouseAisle(aisle1);
        controller.createWarehouseAisle(aisle2);
        controller.createWarehouseAisle(aisle3);
        assertEquals(3, controller.getAllWarehouseAisles().size());

        WarehouseAisle aisle4 = new WarehouseAisle(4, warehouse2, 2, "B-02");
        controller.createWarehouseAisle(aisle4);
        assertEquals(4, controller.getAllWarehouseAisles().size());

        WarehouseAisle updatedAisle = new WarehouseAisle(2, warehouse1, 2, "A-02-RENOVATED");
        assertTrue(controller.updateWarehouseAisle(1, 2, updatedAisle));

        assertTrue(controller.deleteWarehouseAisle(1, 1));
        assertEquals(3, controller.getAllWarehouseAisles().size());

        assertFalse(controller.getWarehouseAisle(1, 1).isPresent());
        assertTrue(controller.getWarehouseAisle(1, 2).isPresent());
        assertTrue(controller.getWarehouseAisle(2, 1).isPresent());
    }
}

