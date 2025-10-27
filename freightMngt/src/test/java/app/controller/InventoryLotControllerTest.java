package app.controller;

import app.model.InventoryLot;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class InventoryLotControllerTest {

    private InventoryLotController controller;
    private InventoryLot lot1;
    private InventoryLot lot2;
    private InventoryLot lot3;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getInventoryLotRepository().clear();

        controller = new InventoryLotController();

        LocalDateTime now = LocalDateTime.now();
        lot1 = new InventoryLot(1, 100, 50, now.plusDays(30), now.minusDays(5));
        lot2 = new InventoryLot(2, 200, 75, now.plusDays(60), now.minusDays(3));
        lot3 = new InventoryLot(3, 300, 100, null, now.minusDays(1));
    }

    @Test
    public void testCreateInventoryLot() {
        InventoryLot result = controller.createInventoryLot(lot1);

        assertNotNull(result);
        assertEquals(lot1, result);
        assertEquals(1, controller.getAllInventoryLots().size());
    }

    @Test
    public void testCreateInventoryLotWithNull() {
        InventoryLot result = controller.createInventoryLot(null);

        assertNull(result);
        assertEquals(0, controller.getAllInventoryLots().size());
    }

    @Test
    public void testGetAllInventoryLots() {
        controller.createInventoryLot(lot1);
        controller.createInventoryLot(lot2);
        controller.createInventoryLot(lot3);

        List<InventoryLot> lots = controller.getAllInventoryLots();

        assertEquals(3, lots.size());
        assertTrue(lots.contains(lot1));
        assertTrue(lots.contains(lot2));
        assertTrue(lots.contains(lot3));
    }

    @Test
    public void testGetAllInventoryLotsEmpty() {
        List<InventoryLot> lots = controller.getAllInventoryLots();

        assertNotNull(lots);
        assertEquals(0, lots.size());
    }

    @Test
    public void testGetInventoryLotByIdFound() {
        controller.createInventoryLot(lot1);
        controller.createInventoryLot(lot2);

        InventoryLot result = controller.getInventoryLotById(1);

        assertNotNull(result);
        assertEquals(lot1, result);
    }

    @Test
    public void testGetInventoryLotByIdNotFound() {
        controller.createInventoryLot(lot1);

        InventoryLot result = controller.getInventoryLotById(999);

        assertNull(result);
    }

    @Test
    public void testUpdateInventoryLotSuccess() {
        controller.createInventoryLot(lot1);

        LocalDateTime now = LocalDateTime.now();
        InventoryLot updatedLot = new InventoryLot(1, 100, 60, now.plusDays(30), now.minusDays(5));

        boolean result = controller.updateInventoryLot(1, updatedLot);

        assertTrue(result);
        InventoryLot retrievedLot = controller.getInventoryLotById(1);
        assertNotNull(retrievedLot);
        assertEquals(60, retrievedLot.getQty().intValue());
    }

    @Test
    public void testUpdateInventoryLotNotFound() {
        controller.createInventoryLot(lot1);

        boolean result = controller.updateInventoryLot(999, lot2);

        assertFalse(result);
    }

    @Test
    public void testUpdateInventoryLotWithNull() {
        controller.createInventoryLot(lot1);

        boolean result = controller.updateInventoryLot(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteInventoryLotSuccess() {
        controller.createInventoryLot(lot1);
        controller.createInventoryLot(lot2);
        controller.createInventoryLot(lot3);

        boolean result = controller.deleteInventoryLot(2);

        assertTrue(result);
        assertEquals(2, controller.getAllInventoryLots().size());
        assertNull(controller.getInventoryLotById(2));
    }

    @Test
    public void testDeleteInventoryLotNotFound() {
        controller.createInventoryLot(lot1);

        boolean result = controller.deleteInventoryLot(999);

        assertFalse(result);
        assertEquals(1, controller.getAllInventoryLots().size());
    }

    @Test
    public void testConstructor() {
        InventoryLotController newController = new InventoryLotController();

        assertNotNull(newController);
        assertNotNull(newController.getAllInventoryLots());
    }

    @Test
    public void testMultipleLotOperations() {
        // Test creating multiple lots
        controller.createInventoryLot(lot1);
        controller.createInventoryLot(lot2);
        controller.createInventoryLot(lot3);
        assertEquals(3, controller.getAllInventoryLots().size());

        // Test updating one
        LocalDateTime now = LocalDateTime.now();
        InventoryLot updatedLot = new InventoryLot(2, 200, 80, now.plusDays(60), now.minusDays(3));
        assertTrue(controller.updateInventoryLot(2, updatedLot));

        // Test deleting one
        assertTrue(controller.deleteInventoryLot(1));
        assertEquals(2, controller.getAllInventoryLots().size());

        // Verify remaining lots
        assertNull(controller.getInventoryLotById(1));
        assertNotNull(controller.getInventoryLotById(2));
        assertNotNull(controller.getInventoryLotById(3));
    }
}

