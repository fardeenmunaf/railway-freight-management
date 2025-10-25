package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for FacilityType model.
 * Tests constructors, getters, setters, and business logic.
 */
public class FacilityTypeTest {

    private FacilityType facilityType;

    @Before
    public void setUp() {
        facilityType = new FacilityType();
    }

    @Test
    public void testDefaultConstructor() {
        FacilityType defaultFacilityType = new FacilityType();

        assertNull("Facility Type ID should be null", defaultFacilityType.getFacilityTypeId());
        assertNull("Name should be null", defaultFacilityType.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        FacilityType paramFacilityType = new FacilityType(1, "Warehouse");

        assertEquals("Facility Type ID should be 1", Integer.valueOf(1), paramFacilityType.getFacilityTypeId());
        assertEquals("Name should be 'Warehouse'", "Warehouse", paramFacilityType.getName());
    }

    @Test
    public void testSetAndGetFacilityTypeId() {
        facilityType.setFacilityTypeId(10);

        assertEquals("Facility Type ID should be 10", Integer.valueOf(10), facilityType.getFacilityTypeId());
    }

    @Test
    public void testSetAndGetName() {
        facilityType.setName("Terminal");

        assertEquals("Name should be 'Terminal'", "Terminal", facilityType.getName());
    }

    @Test
    public void testSetFacilityTypeIdToNull() {
        facilityType.setFacilityTypeId(5);
        facilityType.setFacilityTypeId(null);

        assertNull("Facility Type ID should be null", facilityType.getFacilityTypeId());
    }

    @Test
    public void testSetNameToNull() {
        facilityType.setName("Test Type");
        facilityType.setName(null);

        assertNull("Name should be null", facilityType.getName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        facilityType.setFacilityTypeId(3);
        facilityType.setName("Station");

        assertEquals("Facility Type ID should be 3", Integer.valueOf(3), facilityType.getFacilityTypeId());
        assertEquals("Name should be 'Station'", "Station", facilityType.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        FacilityType nullFacilityType = new FacilityType(null, null);

        assertNull("Facility Type ID should be null", nullFacilityType.getFacilityTypeId());
        assertNull("Name should be null", nullFacilityType.getName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        FacilityType emptyNameFacilityType = new FacilityType(1, "");

        assertEquals("Facility Type ID should be 1", Integer.valueOf(1), emptyNameFacilityType.getFacilityTypeId());
        assertEquals("Name should be empty string", "", emptyNameFacilityType.getName());
    }

    @Test
    public void testToString() {
        facilityType.setFacilityTypeId(5);
        facilityType.setName("Test Type");

        String result = facilityType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain facilityTypeId", result.contains("facilityTypeId=5"));
        assertTrue("toString should contain name", result.contains("name='Test Type'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = facilityType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'FacilityType{'", result.contains("FacilityType{"));
    }
}

