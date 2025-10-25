package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for WarehouseBay model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WarehouseBayTest {

    private WarehouseBay warehouseBay;

    @Before
    public void setUp() {
        warehouseBay = new WarehouseBay();
    }

    @Test
    public void testDefaultConstructor() {
        WarehouseBay defaultBay = new WarehouseBay();

        assertNull("Bay ID should be null", defaultBay.getBayId());
        assertNull("Aisle should be null", defaultBay.getAisle());
        assertNull("Bay No should be null", defaultBay.getBayNo());
        assertNull("Capacity Box should be null", defaultBay.getCapacityBox());
    }

    @Test
    public void testParameterizedConstructor() {
        WarehouseAisle aisle = new WarehouseAisle();
        WarehouseBay paramBay = new WarehouseBay(1, aisle, 5, 100);

        assertEquals("Bay ID should be 1", Integer.valueOf(1), paramBay.getBayId());
        assertEquals("Aisle should match", aisle, paramBay.getAisle());
        assertEquals("Bay No should be 5", Integer.valueOf(5), paramBay.getBayNo());
        assertEquals("Capacity Box should be 100", Integer.valueOf(100), paramBay.getCapacityBox());
    }

    @Test
    public void testSetAndGetBayId() {
        warehouseBay.setBayId(10);

        assertEquals("Bay ID should be 10", Integer.valueOf(10), warehouseBay.getBayId());
    }

    @Test
    public void testSetAndGetAisle() {
        WarehouseAisle aisle = new WarehouseAisle();
        warehouseBay.setAisle(aisle);

        assertEquals("Aisle should match", aisle, warehouseBay.getAisle());
    }

    @Test
    public void testSetAndGetBayNo() {
        warehouseBay.setBayNo(3);

        assertEquals("Bay No should be 3", Integer.valueOf(3), warehouseBay.getBayNo());
    }

    @Test
    public void testSetAndGetCapacityBox() {
        warehouseBay.setCapacityBox(150);

        assertEquals("Capacity Box should be 150", Integer.valueOf(150), warehouseBay.getCapacityBox());
    }

    @Test
    public void testSetBayIdToNull() {
        warehouseBay.setBayId(5);
        warehouseBay.setBayId(null);

        assertNull("Bay ID should be null", warehouseBay.getBayId());
    }

    @Test
    public void testSetAisleToNull() {
        WarehouseAisle aisle = new WarehouseAisle();
        warehouseBay.setAisle(aisle);
        warehouseBay.setAisle(null);

        assertNull("Aisle should be null", warehouseBay.getAisle());
    }

    @Test
    public void testSetBayNoToNull() {
        warehouseBay.setBayNo(2);
        warehouseBay.setBayNo(null);

        assertNull("Bay No should be null", warehouseBay.getBayNo());
    }

    @Test
    public void testSetCapacityBoxToNull() {
        warehouseBay.setCapacityBox(100);
        warehouseBay.setCapacityBox(null);

        assertNull("Capacity Box should be null", warehouseBay.getCapacityBox());
    }

    @Test
    public void testConstructorWithNullValues() {
        WarehouseBay nullBay = new WarehouseBay(null, null, null, null);

        assertNull("Bay ID should be null", nullBay.getBayId());
        assertNull("Aisle should be null", nullBay.getAisle());
        assertNull("Bay No should be null", nullBay.getBayNo());
        assertNull("Capacity Box should be null", nullBay.getCapacityBox());
    }

    @Test
    public void testToString() {
        WarehouseAisle aisle = new WarehouseAisle();
        warehouseBay.setBayId(7);
        warehouseBay.setAisle(aisle);
        warehouseBay.setBayNo(4);
        warehouseBay.setCapacityBox(200);

        String result = warehouseBay.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain bayId", result.contains("bayId=7"));
        assertTrue("toString should contain bayNo", result.contains("bayNo=4"));
        assertTrue("toString should contain capacityBox", result.contains("capacityBox=200"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = warehouseBay.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'WarehouseBay{'", result.contains("WarehouseBay{"));
    }
}

