package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for WagonType model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WagonTypeTest {

    private WagonType wagonType;

    @Before
    public void setUp() {
        wagonType = new WagonType();
    }

    @Test
    public void testDefaultConstructor() {
        WagonType defaultType = new WagonType();

        assertNull("Wagon Type ID should be null", defaultType.getWagonTypeId());
        assertNull("Name should be null", defaultType.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        WagonType paramType = new WagonType(1, "Flatcar");

        assertEquals("Wagon Type ID should be 1", Integer.valueOf(1), paramType.getWagonTypeId());
        assertEquals("Name should be 'Flatcar'", "Flatcar", paramType.getName());
    }

    @Test
    public void testSetAndGetWagonTypeId() {
        wagonType.setWagonTypeId(10);

        assertEquals("Wagon Type ID should be 10", Integer.valueOf(10), wagonType.getWagonTypeId());
    }

    @Test
    public void testSetAndGetName() {
        wagonType.setName("Boxcar");

        assertEquals("Name should be 'Boxcar'", "Boxcar", wagonType.getName());
    }

    @Test
    public void testSetWagonTypeIdToNull() {
        wagonType.setWagonTypeId(5);
        wagonType.setWagonTypeId(null);

        assertNull("Wagon Type ID should be null", wagonType.getWagonTypeId());
    }

    @Test
    public void testSetNameToNull() {
        wagonType.setName("Test Type");
        wagonType.setName(null);

        assertNull("Name should be null", wagonType.getName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        wagonType.setWagonTypeId(3);
        wagonType.setName("Tank Car");

        assertEquals("Wagon Type ID should be 3", Integer.valueOf(3), wagonType.getWagonTypeId());
        assertEquals("Name should be 'Tank Car'", "Tank Car", wagonType.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        WagonType nullType = new WagonType(null, null);

        assertNull("Wagon Type ID should be null", nullType.getWagonTypeId());
        assertNull("Name should be null", nullType.getName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        WagonType emptyNameType = new WagonType(1, "");

        assertEquals("Wagon Type ID should be 1", Integer.valueOf(1), emptyNameType.getWagonTypeId());
        assertEquals("Name should be empty string", "", emptyNameType.getName());
    }

    @Test
    public void testToString() {
        wagonType.setWagonTypeId(7);
        wagonType.setName("Hopper");

        String result = wagonType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain wagonTypeId", result.contains("wagonTypeId=7"));
        assertTrue("toString should contain name", result.contains("name='Hopper'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = wagonType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'WagonType{'", result.contains("WagonType{"));
    }
}

