package app.repositories;

import app.model.Warehouse;
import app.model.WarehouseAisle;
import app.model.Facility;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for WarehouseAisleRepository.
 * Tests all public methods including CRUD operations, search by warehouse, and edge cases.
 */
public class WarehouseAisleRepositoryTest {

    private WarehouseAisleRepository repository;
    private WarehouseAisle aisle1;
    private WarehouseAisle aisle2;
    private WarehouseAisle aisle3;
    private Warehouse warehouse1;
    private Warehouse warehouse2;

    @Before
    public void setUp() {
        repository = new WarehouseAisleRepository();

        Facility facility = new Facility();
        warehouse1 = new Warehouse(1, facility, "Main Warehouse");
        warehouse2 = new Warehouse(2, facility, "Secondary Warehouse");

        aisle1 = new WarehouseAisle(1, warehouse1, 1, "W1-A1");
        aisle2 = new WarehouseAisle(2, warehouse1, 2, "W1-A2");
        aisle3 = new WarehouseAisle(3, warehouse2, 1, "W2-A1");
    }

    @Test
    public void testAddWarehouseAisle() {
        repository.addWarehouseAisle(aisle1);

        List<WarehouseAisle> aisles = repository.getAllAisles();
        assertEquals(1, aisles.size());
        assertTrue(aisles.contains(aisle1));
    }

    @Test
    public void testAddWarehouseAisleWithNull() {
        repository.addWarehouseAisle(null);

        List<WarehouseAisle> aisles = repository.getAllAisles();
        assertEquals(0, aisles.size());
    }

    @Test
    public void testAddWarehouseAisleWithNullWarehouse() {
        WarehouseAisle aisleWithNullWarehouse = new WarehouseAisle(4, null, 1, "TEST");
        repository.addWarehouseAisle(aisleWithNullWarehouse);

        List<WarehouseAisle> aisles = repository.getAllAisles();
        assertEquals(0, aisles.size());
    }

    @Test
    public void testAddDuplicateWarehouseAisle() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle1);

        List<WarehouseAisle> aisles = repository.getAllAisles();
        assertEquals(1, aisles.size());
    }

    @Test
    public void testGetAllAisles() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle2);
        repository.addWarehouseAisle(aisle3);

        List<WarehouseAisle> aisles = repository.getAllAisles();

        assertEquals(3, aisles.size());
        assertTrue(aisles.contains(aisle1));
        assertTrue(aisles.contains(aisle2));
        assertTrue(aisles.contains(aisle3));
    }

    @Test
    public void testGetAllAislesEmpty() {
        List<WarehouseAisle> aisles = repository.getAllAisles();

        assertNotNull(aisles);
        assertEquals(0, aisles.size());
    }

    @Test
    public void testGetAisleFound() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle2);

        WarehouseAisle result = repository.getAisle(1, 1);

        assertNotNull(result);
        assertEquals(aisle1, result);
    }

    @Test
    public void testGetAisleNotFound() {
        repository.addWarehouseAisle(aisle1);

        WarehouseAisle result = repository.getAisle(1, 999);

        assertNull(result);
    }

    @Test
    public void testGetAislesByWarehouse() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle2);
        repository.addWarehouseAisle(aisle3);

        List<WarehouseAisle> result = repository.getAislesByWarehouse(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(aisle1));
        assertTrue(result.contains(aisle2));
        assertFalse(result.contains(aisle3));
    }

    @Test
    public void testGetAislesByWarehouseEmpty() {
        repository.addWarehouseAisle(aisle1);

        List<WarehouseAisle> result = repository.getAislesByWarehouse(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetNextAisleNo() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle2);

        Integer nextAisleNo = repository.getNextAisleNo(1);

        assertNotNull(nextAisleNo);
        assertEquals(Integer.valueOf(3), nextAisleNo);
    }

    @Test
    public void testGetNextAisleNoEmpty() {
        Integer nextAisleNo = repository.getNextAisleNo(1);

        assertNotNull(nextAisleNo);
        assertEquals(Integer.valueOf(1), nextAisleNo);
    }

    @Test
    public void testUpdateWarehouseAisleSuccess() {
        repository.addWarehouseAisle(aisle1);

        WarehouseAisle updatedAisle = new WarehouseAisle(1, warehouse1, 1, "W1-A1-UPDATED");

        boolean result = repository.updateWarehouseAisle(1, 1, updatedAisle);

        assertTrue(result);
        WarehouseAisle retrievedAisle = repository.getAisle(1, 1);
        assertNotNull(retrievedAisle);
    }

    @Test
    public void testUpdateWarehouseAisleNotFound() {
        repository.addWarehouseAisle(aisle1);

        boolean result = repository.updateWarehouseAisle(1, 999, aisle2);

        assertFalse(result);
    }

    @Test
    public void testDeleteWarehouseAisleSuccess() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle2);
        repository.addWarehouseAisle(aisle3);

        boolean result = repository.deleteWarehouseAisle(1, 2);

        assertTrue(result);
        assertEquals(2, repository.getAllAisles().size());
        assertNull(repository.getAisle(1, 2));
    }

    @Test
    public void testDeleteWarehouseAisleNotFound() {
        repository.addWarehouseAisle(aisle1);

        boolean result = repository.deleteWarehouseAisle(1, 999);

        assertFalse(result);
        assertEquals(1, repository.getAllAisles().size());
    }

    @Test
    public void testClear() {
        repository.addWarehouseAisle(aisle1);
        repository.addWarehouseAisle(aisle2);
        repository.addWarehouseAisle(aisle3);
        assertEquals(3, repository.getAllAisles().size());

        repository.clear();

        assertEquals(0, repository.getAllAisles().size());
    }

    @Test
    public void testConstructor() {
        WarehouseAisleRepository newRepository = new WarehouseAisleRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllAisles());
    }
}

