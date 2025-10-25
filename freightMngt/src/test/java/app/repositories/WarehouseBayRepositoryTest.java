package app.repositories;

import app.model.Warehouse;
import app.model.WarehouseAisle;
import app.model.WarehouseBay;
import app.model.Facility;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for WarehouseBayRepository.
 * Tests all public methods including CRUD operations, search by aisle, and edge cases.
 */
public class WarehouseBayRepositoryTest {

    private WarehouseBayRepository repository;
    private WarehouseBay bay1;
    private WarehouseBay bay2;
    private WarehouseBay bay3;
    private WarehouseAisle aisle1;
    private WarehouseAisle aisle2;

    @Before
    public void setUp() {
        repository = new WarehouseBayRepository();

        Facility facility = new Facility();
        Warehouse warehouse = new Warehouse(1, facility, "Main Warehouse");
        aisle1 = new WarehouseAisle(1, warehouse, 1, "W1-A1");
        aisle2 = new WarehouseAisle(2, warehouse, 2, "W1-A2");

        bay1 = new WarehouseBay(1, aisle1, 1, 100);
        bay2 = new WarehouseBay(2, aisle1, 2, 150);
        bay3 = new WarehouseBay(3, aisle2, 1, 200);
    }

    @Test
    public void testAddWarehouseBay() {
        repository.addWarehouseBay(bay1);

        List<WarehouseBay> bays = repository.getAllWarehouseBays();
        assertEquals(1, bays.size());
        assertTrue(bays.contains(bay1));
    }

    @Test
    public void testAddWarehouseBayWithNull() {
        repository.addWarehouseBay(null);

        List<WarehouseBay> bays = repository.getAllWarehouseBays();
        assertEquals(0, bays.size());
    }

    @Test
    public void testAddWarehouseBayWithNullAisle() {
        WarehouseBay bayWithNullAisle = new WarehouseBay(4, null, 1, 100);
        repository.addWarehouseBay(bayWithNullAisle);

        List<WarehouseBay> bays = repository.getAllWarehouseBays();
        assertEquals(0, bays.size());
    }

    @Test
    public void testAddDuplicateWarehouseBay() {
        repository.addWarehouseBay(bay1);
        repository.addWarehouseBay(bay1);

        List<WarehouseBay> bays = repository.getAllWarehouseBays();
        assertEquals(1, bays.size());
    }

    @Test
    public void testGetAllWarehouseBays() {
        repository.addWarehouseBay(bay1);
        repository.addWarehouseBay(bay2);
        repository.addWarehouseBay(bay3);

        List<WarehouseBay> bays = repository.getAllWarehouseBays();

        assertEquals(3, bays.size());
        assertTrue(bays.contains(bay1));
        assertTrue(bays.contains(bay2));
        assertTrue(bays.contains(bay3));
    }

    @Test
    public void testGetAllWarehouseBaysEmpty() {
        List<WarehouseBay> bays = repository.getAllWarehouseBays();

        assertNotNull(bays);
        assertEquals(0, bays.size());
    }

    @Test
    public void testGetWarehouseBayFound() {
        repository.addWarehouseBay(bay1);
        repository.addWarehouseBay(bay2);

        WarehouseBay result = repository.getWarehouseBay(1, 1, 1);

        assertNotNull(result);
        assertEquals(bay1, result);
    }

    @Test
    public void testGetWarehouseBayNotFound() {
        repository.addWarehouseBay(bay1);

        WarehouseBay result = repository.getWarehouseBay(1, 1, 999);

        assertNull(result);
    }

    @Test
    public void testGetBaysByAisle() {
        repository.addWarehouseBay(bay1);
        repository.addWarehouseBay(bay2);
        repository.addWarehouseBay(bay3);

        List<WarehouseBay> result = repository.getBaysByAisle(1, 1);

        assertEquals(2, result.size());
        assertTrue(result.contains(bay1));
        assertTrue(result.contains(bay2));
        assertFalse(result.contains(bay3));
    }

    @Test
    public void testGetBaysByAisleEmpty() {
        repository.addWarehouseBay(bay1);

        List<WarehouseBay> result = repository.getBaysByAisle(1, 999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetNextBayNo() {
        repository.addWarehouseBay(bay1);
        repository.addWarehouseBay(bay2);

        Integer nextBayNo = repository.getNextBayNo(1, 1);

        assertNotNull(nextBayNo);
        assertEquals(Integer.valueOf(3), nextBayNo);
    }

    @Test
    public void testGetNextBayNoEmpty() {
        Integer nextBayNo = repository.getNextBayNo(1, 1);

        assertNotNull(nextBayNo);
        assertEquals(Integer.valueOf(1), nextBayNo);
    }

    @Test
    public void testUpdateWarehouseBaySuccess() {
        repository.addWarehouseBay(bay1);

        WarehouseBay updatedBay = new WarehouseBay(1, aisle1, 1, 250);

        boolean result = repository.updateWarehouseBay(1, 1, 1, updatedBay);

        assertTrue(result);
        WarehouseBay retrievedBay = repository.getWarehouseBay(1, 1, 1);
        assertNotNull(retrievedBay);
        assertEquals(Integer.valueOf(250), retrievedBay.getCapacityBox());
    }

    @Test
    public void testUpdateWarehouseBayNotFound() {
        repository.addWarehouseBay(bay1);

        boolean result = repository.updateWarehouseBay(1, 1, 999, bay2);

        assertFalse(result);
    }

    @Test
    public void testDeleteWarehouseBaySuccess() {
        repository.addWarehouseBay(bay1);
        repository.addWarehouseBay(bay2);
        repository.addWarehouseBay(bay3);

        boolean result = repository.deleteWarehouseBay(1, 1, 2);

        assertTrue(result);
        assertEquals(2, repository.getAllWarehouseBays().size());
        assertNull(repository.getWarehouseBay(1, 1, 2));
    }

    @Test
    public void testDeleteWarehouseBayNotFound() {
        repository.addWarehouseBay(bay1);

        boolean result = repository.deleteWarehouseBay(1, 1, 999);

        assertFalse(result);
        assertEquals(1, repository.getAllWarehouseBays().size());
    }

    @Test
    public void testConstructor() {
        WarehouseBayRepository newRepository = new WarehouseBayRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllWarehouseBays());
    }
}

