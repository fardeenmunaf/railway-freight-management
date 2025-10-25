package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for LocomotiveType model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class LocomotiveTypeTest {

    private LocomotiveType locomotiveType;

    @Before
    public void setUp() {
        locomotiveType = new LocomotiveType();
    }

    @Test
    public void testDefaultConstructor() {
        LocomotiveType defaultType = new LocomotiveType();

        assertNull("Locomotive Type ID should be null", defaultType.getLocomotiveTypeId());
        assertNull("Name should be null", defaultType.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        LocomotiveType paramType = new LocomotiveType(1, "Diesel");

        assertEquals("Locomotive Type ID should be 1", Integer.valueOf(1), paramType.getLocomotiveTypeId());
        assertEquals("Name should be 'Diesel'", "Diesel", paramType.getName());
    }

    @Test
    public void testSetAndGetLocomotiveTypeId() {
        locomotiveType.setLocomotiveTypeId(10);

        assertEquals("Locomotive Type ID should be 10", Integer.valueOf(10), locomotiveType.getLocomotiveTypeId());
    }

    @Test
    public void testSetAndGetName() {
        locomotiveType.setName("Electric");

        assertEquals("Name should be 'Electric'", "Electric", locomotiveType.getName());
    }

    @Test
    public void testSetLocomotiveTypeIdToNull() {
        locomotiveType.setLocomotiveTypeId(5);
        locomotiveType.setLocomotiveTypeId(null);

        assertNull("Locomotive Type ID should be null", locomotiveType.getLocomotiveTypeId());
    }

    @Test
    public void testSetNameToNull() {
        locomotiveType.setName("Test Type");
        locomotiveType.setName(null);

        assertNull("Name should be null", locomotiveType.getName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        locomotiveType.setLocomotiveTypeId(3);
        locomotiveType.setName("Steam");

        assertEquals("Locomotive Type ID should be 3", Integer.valueOf(3), locomotiveType.getLocomotiveTypeId());
        assertEquals("Name should be 'Steam'", "Steam", locomotiveType.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        LocomotiveType nullType = new LocomotiveType(null, null);

        assertNull("Locomotive Type ID should be null", nullType.getLocomotiveTypeId());
        assertNull("Name should be null", nullType.getName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        LocomotiveType emptyNameType = new LocomotiveType(1, "");

        assertEquals("Locomotive Type ID should be 1", Integer.valueOf(1), emptyNameType.getLocomotiveTypeId());
        assertEquals("Name should be empty string", "", emptyNameType.getName());
    }

    @Test
    public void testToString() {
        locomotiveType.setLocomotiveTypeId(7);
        locomotiveType.setName("Hybrid");

        String result = locomotiveType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain locomotiveTypeId", result.contains("locomotiveTypeId=7"));
        assertTrue("toString should contain name", result.contains("name='Hybrid'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = locomotiveType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveType{'", result.contains("LocomotiveType{"));
    }
}

