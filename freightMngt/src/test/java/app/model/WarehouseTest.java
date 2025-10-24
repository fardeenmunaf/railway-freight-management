package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for WarehouseTest model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WarehouseTest {

    private Warehouse warehouse;

    @Before
    public void setUp() {
        warehouse = new Warehouse();
    }

    @Test
    public void testDefaultConstructor() {
        Warehouse defaultWarehouse = new Warehouse();

        assertNull("Warehouse ID should be null", defaultWarehouse.getWarehouseId());
        assertNull("Facility should be null", defaultWarehouse.getFacility());
        assertNull("Name should be null", defaultWarehouse.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        Facility facility = new Facility();
        Warehouse paramWarehouse = new Warehouse(1, facility, "Main Warehouse");

        assertEquals("Warehouse ID should be 1", Integer.valueOf(1), paramWarehouse.getWarehouseId());
        assertEquals("Facility should match", facility, paramWarehouse.getFacility());
        assertEquals("Name should be 'Main Warehouse'", "Main Warehouse", paramWarehouse.getName());
    }

    @Test
    public void testSetAndGetWarehouseId() {
        warehouse.setWarehouseId(10);

        assertEquals("Warehouse ID should be 10", Integer.valueOf(10), warehouse.getWarehouseId());
    }

    @Test
    public void testSetAndGetFacility() {
        Facility facility = new Facility();
        warehouse.setFacility(facility);

        assertEquals("Facility should match", facility, warehouse.getFacility());
    }

    @Test
    public void testSetAndGetName() {
        warehouse.setName("Storage Center");

        assertEquals("Name should be 'Storage Center'", "Storage Center", warehouse.getName());
    }

    @Test
    public void testSetWarehouseIdToNull() {
        warehouse.setWarehouseId(5);
        warehouse.setWarehouseId(null);

        assertNull("Warehouse ID should be null", warehouse.getWarehouseId());
    }

    @Test
    public void testSetFacilityToNull() {
        Facility facility = new Facility();
        warehouse.setFacility(facility);
        warehouse.setFacility(null);

        assertNull("Facility should be null", warehouse.getFacility());
    }

    @Test
    public void testSetNameToNull() {
        warehouse.setName("Test Warehouse");
        warehouse.setName(null);

        assertNull("Name should be null", warehouse.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        Warehouse nullWarehouse = new Warehouse(null, null, null);

        assertNull("Warehouse ID should be null", nullWarehouse.getWarehouseId());
        assertNull("Facility should be null", nullWarehouse.getFacility());
        assertNull("Name should be null", nullWarehouse.getName());
    }

    @Test
    public void testToString() {
        Facility facility = new Facility();
        warehouse.setWarehouseId(7);
        warehouse.setFacility(facility);
        warehouse.setName("Distribution Hub");

        String result = warehouse.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain warehouseId", result.contains("warehouseId=7"));
        assertTrue("toString should contain name", result.contains("name='Distribution Hub'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = warehouse.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Warehouse{'", result.contains("Warehouse{"));
    }
}

