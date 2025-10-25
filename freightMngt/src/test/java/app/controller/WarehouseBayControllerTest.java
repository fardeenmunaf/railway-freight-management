package app.controller;

import app.model.WarehouseBay;
import app.model.WarehouseAisle;
import app.model.Warehouse;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WarehouseBayControllerTest {

    private WarehouseBayController controller;
    private WarehouseBay bay1;
    private WarehouseBay bay2;
    private WarehouseBay bay3;
    private WarehouseAisle aisle1;
    private WarehouseAisle aisle2;
    private Warehouse warehouse1;

    @Before
    public void setUp() {
        Repositories.getInstance().getWarehouseBayRepository().clear();

        controller = new WarehouseBayController();

        warehouse1 = new Warehouse(1, null, "Warehouse A");

        aisle1 = new WarehouseAisle(1, warehouse1, 1, "A-01");
        aisle2 = new WarehouseAisle(2, warehouse1, 2, "A-02");

        bay1 = new WarehouseBay(1, aisle1, 1, 100);
        bay2 = new WarehouseBay(2, aisle1, 2, 150);
        bay3 = new WarehouseBay(3, aisle2, 1, 200);
    }

    @Test
    public void testCreateWarehouseBay() {
        WarehouseBay result = controller.createWarehouseBay(bay1);

        assertNotNull(result);
        assertEquals(bay1, result);
        assertEquals(1, controller.getAllWarehouseBays().size());
    }

    @Test
    public void testCreateWarehouseBayWithNull() {
        WarehouseBay result = controller.createWarehouseBay(null);

        assertNull(result);
        assertEquals(0, controller.getAllWarehouseBays().size());
    }

    @Test
    public void testGetAllWarehouseBays() {
        controller.createWarehouseBay(bay1);
        controller.createWarehouseBay(bay2);
        controller.createWarehouseBay(bay3);

        List<WarehouseBay> bays = controller.getAllWarehouseBays();

        assertEquals(3, bays.size());
        assertTrue(bays.contains(bay1));
        assertTrue(bays.contains(bay2));
        assertTrue(bays.contains(bay3));
    }

    @Test
    public void testGetAllWarehouseBaysEmpty() {
        List<WarehouseBay> bays = controller.getAllWarehouseBays();

        assertNotNull(bays);
        assertEquals(0, bays.size());
    }

    @Test
    public void testGetWarehouseBayFound() {
        controller.createWarehouseBay(bay1);
        controller.createWarehouseBay(bay2);

        Optional<WarehouseBay> result = controller.getWarehouseBay(1, 1, 1);

        assertTrue(result.isPresent());
        assertEquals(bay1, result.get());
    }

    @Test
    public void testGetWarehouseBayNotFound() {
        controller.createWarehouseBay(bay1);

        Optional<WarehouseBay> result = controller.getWarehouseBay(999, 999, 999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateWarehouseBaySuccess() {
        controller.createWarehouseBay(bay1);

        WarehouseBay updatedBay = new WarehouseBay(1, aisle1, 1, 120);

        boolean result = controller.updateWarehouseBay(1, 1, 1, updatedBay);

        assertTrue(result);
        Optional<WarehouseBay> retrievedBay = controller.getWarehouseBay(1, 1, 1);
        assertTrue(retrievedBay.isPresent());
        assertEquals(120, retrievedBay.get().getCapacityBox().intValue());
    }

    @Test
    public void testUpdateWarehouseBayNotFound() {
        controller.createWarehouseBay(bay1);

        boolean result = controller.updateWarehouseBay(999, 999, 999, bay2);

        assertFalse(result);
    }

    @Test
    public void testUpdateWarehouseBayWithNull() {
        controller.createWarehouseBay(bay1);

        boolean result = controller.updateWarehouseBay(1, 1, 1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteWarehouseBaySuccess() {
        controller.createWarehouseBay(bay1);
        controller.createWarehouseBay(bay2);
        controller.createWarehouseBay(bay3);

        boolean result = controller.deleteWarehouseBay(1, 1, 2);

        assertTrue(result);
        assertEquals(2, controller.getAllWarehouseBays().size());
        assertFalse(controller.getWarehouseBay(1, 1, 2).isPresent());
    }

    @Test
    public void testDeleteWarehouseBayNotFound() {
        controller.createWarehouseBay(bay1);

        boolean result = controller.deleteWarehouseBay(999, 999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllWarehouseBays().size());
    }

    @Test
    public void testGetNextBayNo() {
        Integer nextBayNo = controller.getNextBayNo(1, 1);

        assertNotNull(nextBayNo);
        assertTrue(nextBayNo > 0);
    }

    @Test
    public void testClearAllWarehouseBays() {
        controller.createWarehouseBay(bay1);
        controller.createWarehouseBay(bay2);
        controller.createWarehouseBay(bay3);
        assertEquals(3, controller.getAllWarehouseBays().size());

        controller.clearAllWarehouseBays();

        assertEquals(0, controller.getAllWarehouseBays().size());
    }

    @Test
    public void testConstructor() {
        WarehouseBayController newController = new WarehouseBayController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWarehouseBays());
    }

    @Test
    public void testMultipleWarehouseBayOperations() {
        controller.createWarehouseBay(bay1);
        controller.createWarehouseBay(bay2);
        controller.createWarehouseBay(bay3);
        assertEquals(3, controller.getAllWarehouseBays().size());

        WarehouseBay bay4 = new WarehouseBay(4, aisle2, 2, 180);
        controller.createWarehouseBay(bay4);
        assertEquals(4, controller.getAllWarehouseBays().size());

        WarehouseBay updatedBay = new WarehouseBay(2, aisle1, 2, 160);
        assertTrue(controller.updateWarehouseBay(1, 1, 2, updatedBay));

        assertTrue(controller.deleteWarehouseBay(1, 1, 1));
        assertEquals(3, controller.getAllWarehouseBays().size());

        assertFalse(controller.getWarehouseBay(1, 1, 1).isPresent());
        assertTrue(controller.getWarehouseBay(1, 1, 2).isPresent());
        assertTrue(controller.getWarehouseBay(1, 2, 1).isPresent());
    }
}

