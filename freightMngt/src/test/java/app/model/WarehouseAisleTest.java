package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for WarehouseAisle model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WarehouseAisleTest {

    private WarehouseAisle warehouseAisle;

    @Before
    public void setUp() {
        warehouseAisle = new WarehouseAisle();
    }

    @Test
    public void testDefaultConstructor() {
        WarehouseAisle defaultAisle = new WarehouseAisle();

        assertNull("Aisle ID should be null", defaultAisle.getAisleId());
        assertNull("Warehouse should be null", defaultAisle.getWarehouse());
        assertNull("Aisle No should be null", defaultAisle.getAisleNo());
        assertNull("Unique Code should be null", defaultAisle.getUniqueCode());
    }

    @Test
    public void testParameterizedConstructor() {
        Warehouse warehouse = new Warehouse();
        WarehouseAisle paramAisle = new WarehouseAisle(1, warehouse, 2, "A2");

        assertEquals("Aisle ID should be 1", Integer.valueOf(1), paramAisle.getAisleId());
        assertEquals("Warehouse should match", warehouse, paramAisle.getWarehouse());
        assertEquals("Aisle No should be 2", Integer.valueOf(2), paramAisle.getAisleNo());
        assertEquals("Unique Code should be 'A2'", "A2", paramAisle.getUniqueCode());
    }

    @Test
    public void testSetAndGetAisleId() {
        warehouseAisle.setAisleId(10);

        assertEquals("Aisle ID should be 10", Integer.valueOf(10), warehouseAisle.getAisleId());
    }

    @Test
    public void testSetAndGetWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouseAisle.setWarehouse(warehouse);

        assertEquals("Warehouse should match", warehouse, warehouseAisle.getWarehouse());
    }

    @Test
    public void testSetAndGetAisleNo() {
        warehouseAisle.setAisleNo(5);

        assertEquals("Aisle No should be 5", Integer.valueOf(5), warehouseAisle.getAisleNo());
    }

    @Test
    public void testSetAndGetUniqueCode() {
        warehouseAisle.setUniqueCode("B3");

        assertEquals("Unique Code should be 'B3'", "B3", warehouseAisle.getUniqueCode());
    }

    @Test
    public void testSetAisleIdToNull() {
        warehouseAisle.setAisleId(5);
        warehouseAisle.setAisleId(null);

        assertNull("Aisle ID should be null", warehouseAisle.getAisleId());
    }

    @Test
    public void testSetWarehouseToNull() {
        Warehouse warehouse = new Warehouse();
        warehouseAisle.setWarehouse(warehouse);
        warehouseAisle.setWarehouse(null);

        assertNull("Warehouse should be null", warehouseAisle.getWarehouse());
    }

    @Test
    public void testSetAisleNoToNull() {
        warehouseAisle.setAisleNo(3);
        warehouseAisle.setAisleNo(null);

        assertNull("Aisle No should be null", warehouseAisle.getAisleNo());
    }

    @Test
    public void testSetUniqueCodeToNull() {
        warehouseAisle.setUniqueCode("C1");
        warehouseAisle.setUniqueCode(null);

        assertNull("Unique Code should be null", warehouseAisle.getUniqueCode());
    }

    @Test
    public void testConstructorWithNullValues() {
        WarehouseAisle nullAisle = new WarehouseAisle(null, null, null, null);

        assertNull("Aisle ID should be null", nullAisle.getAisleId());
        assertNull("Warehouse should be null", nullAisle.getWarehouse());
        assertNull("Aisle No should be null", nullAisle.getAisleNo());
        assertNull("Unique Code should be null", nullAisle.getUniqueCode());
    }

    @Test
    public void testToString() {
        Warehouse warehouse = new Warehouse();
        warehouseAisle.setAisleId(7);
        warehouseAisle.setWarehouse(warehouse);
        warehouseAisle.setAisleNo(3);
        warehouseAisle.setUniqueCode("D4");

        String result = warehouseAisle.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain aisleId", result.contains("aisleId=7"));
        assertTrue("toString should contain aisleNo", result.contains("aisleNo=3"));
        assertTrue("toString should contain uniqueCode", result.contains("uniqueCode='D4'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = warehouseAisle.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'WarehouseAisle{'", result.contains("WarehouseAisle{"));
    }
}

