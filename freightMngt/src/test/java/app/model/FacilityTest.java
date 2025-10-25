package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Facility model.
 * Tests constructors, getters, setters, and business logic.
 */
public class FacilityTest {

    private Facility facility;

    @Before
    public void setUp() {
        facility = new Facility();
    }

    @Test
    public void testDefaultConstructor() {
        Facility defaultFacility = new Facility();

        assertNull("Facility ID should be null", defaultFacility.getFacilityId());
        assertNull("Name should be null", defaultFacility.getName());
        assertNull("Facility type should be null", defaultFacility.getFacilityType());
        assertNull("Latitude should be null", defaultFacility.getLat());
        assertNull("Longitude should be null", defaultFacility.getLon());
        assertNull("IsIntermodal should be null", defaultFacility.getIsIntermodal());
    }

    @Test
    public void testParameterizedConstructor() {
        FacilityType facilityType = new FacilityType(1, "Warehouse");
        Facility paramFacility = new Facility(1, "Main Warehouse", facilityType, 41.1579, -8.6291, true);

        assertEquals("Facility ID should be 1", Integer.valueOf(1), paramFacility.getFacilityId());
        assertEquals("Name should be 'Main Warehouse'", "Main Warehouse", paramFacility.getName());
        assertEquals("Facility type should match", facilityType, paramFacility.getFacilityType());
        assertEquals("Latitude should be 41.1579", Double.valueOf(41.1579), paramFacility.getLat());
        assertEquals("Longitude should be -8.6291", Double.valueOf(-8.6291), paramFacility.getLon());
        assertEquals("IsIntermodal should be true", Boolean.TRUE, paramFacility.getIsIntermodal());
    }

    @Test
    public void testSetAndGetFacilityId() {
        facility.setFacilityId(10);

        assertEquals("Facility ID should be 10", Integer.valueOf(10), facility.getFacilityId());
    }

    @Test
    public void testSetAndGetName() {
        facility.setName("Distribution Center");

        assertEquals("Name should be 'Distribution Center'", "Distribution Center", facility.getName());
    }

    @Test
    public void testSetAndGetFacilityType() {
        FacilityType facilityType = new FacilityType(2, "Terminal");
        facility.setFacilityType(facilityType);

        assertEquals("Facility type should match", facilityType, facility.getFacilityType());
    }

    @Test
    public void testSetAndGetLat() {
        facility.setLat(38.7223);

        assertEquals("Latitude should be 38.7223", Double.valueOf(38.7223), facility.getLat());
    }

    @Test
    public void testSetAndGetLon() {
        facility.setLon(-9.1393);

        assertEquals("Longitude should be -9.1393", Double.valueOf(-9.1393), facility.getLon());
    }

    @Test
    public void testSetAndGetIsIntermodal() {
        facility.setIsIntermodal(false);

        assertEquals("IsIntermodal should be false", Boolean.FALSE, facility.getIsIntermodal());
    }

    @Test
    public void testSetFacilityIdToNull() {
        facility.setFacilityId(5);
        facility.setFacilityId(null);

        assertNull("Facility ID should be null", facility.getFacilityId());
    }

    @Test
    public void testSetNameToNull() {
        facility.setName("Test Facility");
        facility.setName(null);

        assertNull("Name should be null", facility.getName());
    }

    @Test
    public void testSetFacilityTypeToNull() {
        FacilityType facilityType = new FacilityType(1, "Warehouse");
        facility.setFacilityType(facilityType);
        facility.setFacilityType(null);

        assertNull("Facility type should be null", facility.getFacilityType());
    }

    @Test
    public void testSetLatToNull() {
        facility.setLat(40.0);
        facility.setLat(null);

        assertNull("Latitude should be null", facility.getLat());
    }

    @Test
    public void testSetLonToNull() {
        facility.setLon(-8.0);
        facility.setLon(null);

        assertNull("Longitude should be null", facility.getLon());
    }

    @Test
    public void testSetIsIntermodalToNull() {
        facility.setIsIntermodal(true);
        facility.setIsIntermodal(null);

        assertNull("IsIntermodal should be null", facility.getIsIntermodal());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        FacilityType facilityType = new FacilityType(3, "Station");
        facility.setFacilityId(7);
        facility.setName("Central Station");
        facility.setFacilityType(facilityType);
        facility.setLat(41.5518);
        facility.setLon(-8.4229);
        facility.setIsIntermodal(true);

        assertEquals("Facility ID should be 7", Integer.valueOf(7), facility.getFacilityId());
        assertEquals("Name should be 'Central Station'", "Central Station", facility.getName());
        assertEquals("Facility type should match", facilityType, facility.getFacilityType());
        assertEquals("Latitude should be 41.5518", Double.valueOf(41.5518), facility.getLat());
        assertEquals("Longitude should be -8.4229", Double.valueOf(-8.4229), facility.getLon());
        assertEquals("IsIntermodal should be true", Boolean.TRUE, facility.getIsIntermodal());
    }

    @Test
    public void testConstructorWithNullValues() {
        Facility nullFacility = new Facility(null, null, null, null, null, null);

        assertNull("Facility ID should be null", nullFacility.getFacilityId());
        assertNull("Name should be null", nullFacility.getName());
        assertNull("Facility type should be null", nullFacility.getFacilityType());
        assertNull("Latitude should be null", nullFacility.getLat());
        assertNull("Longitude should be null", nullFacility.getLon());
        assertNull("IsIntermodal should be null", nullFacility.getIsIntermodal());
    }

    @Test
    public void testConstructorWithEmptyName() {
        FacilityType facilityType = new FacilityType(1, "Warehouse");
        Facility emptyNameFacility = new Facility(1, "", facilityType, 40.0, -8.0, false);

        assertEquals("Facility ID should be 1", Integer.valueOf(1), emptyNameFacility.getFacilityId());
        assertEquals("Name should be empty string", "", emptyNameFacility.getName());
        assertEquals("Facility type should match", facilityType, emptyNameFacility.getFacilityType());
    }

    @Test
    public void testToString() {
        FacilityType facilityType = new FacilityType(1, "Warehouse");
        facility.setFacilityId(5);
        facility.setName("Test Facility");
        facility.setFacilityType(facilityType);
        facility.setLat(40.0);
        facility.setLon(-8.0);
        facility.setIsIntermodal(true);

        String result = facility.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain facilityId", result.contains("facilityId=5"));
        assertTrue("toString should contain name", result.contains("name='Test Facility'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = facility.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Facility{'", result.contains("Facility{"));
    }
}

