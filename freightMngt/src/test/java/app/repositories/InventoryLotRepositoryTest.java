package app.repositories;

import app.model.InventoryLot;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for InventoryLotRepository.
 * Tests all public methods including CRUD operations and edge cases.
 */
public class InventoryLotRepositoryTest {

    private InventoryLotRepository repository;
    private InventoryLot lot1;
    private InventoryLot lot2;
    private InventoryLot lot3;

    @Before
    public void setUp() {
        repository = new InventoryLotRepository();

        lot1 = new InventoryLot(1, 23, 100, LocalDateTime.of(2025, 12, 31, 23, 59), LocalDateTime.now());
        lot2 = new InventoryLot(2, 23, 200, LocalDateTime.of(2025, 11, 30, 23, 59), LocalDateTime.now());
        lot3 = new InventoryLot(3, 23, 150, null, LocalDateTime.now());
    }

    @Test
    public void testAddInventoryLot() {
        repository.addInventoryLot(lot1);

        List<InventoryLot> lots = repository.getAllInventoryLots();
        assertEquals(1, lots.size());
        assertTrue(lots.contains(lot1));
    }

    @Test
    public void testAddInventoryLotWithNull() {
        repository.addInventoryLot(null);

        List<InventoryLot> lots = repository.getAllInventoryLots();
        assertEquals(0, lots.size());
    }

    @Test
    public void testAddInventoryLotWithNullId() {
        InventoryLot lotWithNullId = new InventoryLot(null, 23, 50, null, LocalDateTime.now());
        repository.addInventoryLot(lotWithNullId);

        List<InventoryLot> lots = repository.getAllInventoryLots();
        assertEquals(0, lots.size());
    }

    @Test
    public void testAddDuplicateInventoryLot() {
        repository.addInventoryLot(lot1);
        repository.addInventoryLot(lot1);

        List<InventoryLot> lots = repository.getAllInventoryLots();
        assertEquals(1, lots.size());
    }

    @Test
    public void testGetAllInventoryLots() {
        repository.addInventoryLot(lot1);
        repository.addInventoryLot(lot2);
        repository.addInventoryLot(lot3);

        List<InventoryLot> lots = repository.getAllInventoryLots();

        assertEquals(3, lots.size());
        assertTrue(lots.contains(lot1));
        assertTrue(lots.contains(lot2));
        assertTrue(lots.contains(lot3));
    }

    @Test
    public void testGetAllInventoryLotsEmpty() {
        List<InventoryLot> lots = repository.getAllInventoryLots();

        assertNotNull(lots);
        assertEquals(0, lots.size());
    }

    @Test
    public void testGetInventoryLotByIdFound() {
        repository.addInventoryLot(lot1);
        repository.addInventoryLot(lot2);

        InventoryLot result = repository.getInventoryLotById(1);

        assertNotNull(result);
        assertEquals(lot1, result);
    }

    @Test
    public void testGetInventoryLotByIdNotFound() {
        repository.addInventoryLot(lot1);

        InventoryLot result = repository.getInventoryLotById(999);

        assertNull(result);
    }

    @Test
    public void testGetInventoryLotByIdWithNull() {
        repository.addInventoryLot(lot1);

        InventoryLot result = repository.getInventoryLotById(null);

        assertNull(result);
    }

    @Test
    public void testUpdateInventoryLotSuccess() {
        repository.addInventoryLot(lot1);

        InventoryLot updatedLot = new InventoryLot(1, 23, 300, LocalDateTime.of(2026, 1, 31, 23, 59), LocalDateTime.now());

        boolean result = repository.updateInventoryLot(1, updatedLot);

        assertTrue(result);
        InventoryLot retrievedLot = repository.getInventoryLotById(1);
        assertNotNull(retrievedLot);
        assertEquals(Integer.valueOf(300), retrievedLot.getQty());
    }

    @Test
    public void testUpdateInventoryLotNotFound() {
        repository.addInventoryLot(lot1);

        boolean result = repository.updateInventoryLot(999, lot2);

        assertFalse(result);
    }

    @Test
    public void testDeleteInventoryLotSuccess() {
        repository.addInventoryLot(lot1);
        repository.addInventoryLot(lot2);
        repository.addInventoryLot(lot3);

        boolean result = repository.deleteInventoryLot(2);

        assertTrue(result);
        assertEquals(2, repository.getAllInventoryLots().size());
        assertNull(repository.getInventoryLotById(2));
    }

    @Test
    public void testDeleteInventoryLotNotFound() {
        repository.addInventoryLot(lot1);

        boolean result = repository.deleteInventoryLot(999);

        assertFalse(result);
        assertEquals(1, repository.getAllInventoryLots().size());
    }

    @Test
    public void testDeleteInventoryLotWithNull() {
        repository.addInventoryLot(lot1);

        boolean result = repository.deleteInventoryLot(null);

        assertFalse(result);
        assertEquals(1, repository.getAllInventoryLots().size());
    }

    @Test
    public void testGetNextLotId() {
        repository.addInventoryLot(lot1);
        repository.addInventoryLot(lot2);

        Integer nextId = repository.getNextLotId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextLotIdEmpty() {
        Integer nextId = repository.getNextLotId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testClear() {
        repository.addInventoryLot(lot1);
        repository.addInventoryLot(lot2);
        repository.addInventoryLot(lot3);
        assertEquals(3, repository.getAllInventoryLots().size());

        repository.clear();

        assertEquals(0, repository.getAllInventoryLots().size());
    }

    @Test
    public void testConstructor() {
        InventoryLotRepository newRepository = new InventoryLotRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllInventoryLots());
    }
}

