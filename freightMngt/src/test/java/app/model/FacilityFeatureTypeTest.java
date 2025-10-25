package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for FacilityFeatureType model.
 * Tests constructors, getters, setters, and business logic.
 */
public class FacilityFeatureTypeTest {

    private FacilityFeatureType featureType;

    @Before
    public void setUp() {
        featureType = new FacilityFeatureType();
    }

    @Test
    public void testDefaultConstructor() {
        FacilityFeatureType defaultFeatureType = new FacilityFeatureType();

        assertNull("Feature ID should be null", defaultFeatureType.getFeatureId());
        assertNull("Name should be null", defaultFeatureType.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        FacilityFeatureType paramFeatureType = new FacilityFeatureType(1, "Loading Dock");

        assertEquals("Feature ID should be 1", Integer.valueOf(1), paramFeatureType.getFeatureId());
        assertEquals("Name should be 'Loading Dock'", "Loading Dock", paramFeatureType.getName());
    }

    @Test
    public void testSetAndGetFeatureId() {
        featureType.setFeatureId(10);

        assertEquals("Feature ID should be 10", Integer.valueOf(10), featureType.getFeatureId());
    }

    @Test
    public void testSetAndGetName() {
        featureType.setName("Crane");

        assertEquals("Name should be 'Crane'", "Crane", featureType.getName());
    }

    @Test
    public void testSetFeatureIdToNull() {
        featureType.setFeatureId(5);
        featureType.setFeatureId(null);

        assertNull("Feature ID should be null", featureType.getFeatureId());
    }

    @Test
    public void testSetNameToNull() {
        featureType.setName("Test Feature");
        featureType.setName(null);

        assertNull("Name should be null", featureType.getName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        featureType.setFeatureId(3);
        featureType.setName("Refrigeration");

        assertEquals("Feature ID should be 3", Integer.valueOf(3), featureType.getFeatureId());
        assertEquals("Name should be 'Refrigeration'", "Refrigeration", featureType.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        FacilityFeatureType nullFeatureType = new FacilityFeatureType(null, null);

        assertNull("Feature ID should be null", nullFeatureType.getFeatureId());
        assertNull("Name should be null", nullFeatureType.getName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        FacilityFeatureType emptyNameFeatureType = new FacilityFeatureType(1, "");

        assertEquals("Feature ID should be 1", Integer.valueOf(1), emptyNameFeatureType.getFeatureId());
        assertEquals("Name should be empty string", "", emptyNameFeatureType.getName());
    }

    @Test
    public void testToString() {
        featureType.setFeatureId(3);
        featureType.setName("Test Feature");

        String result = featureType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain featureId", result.contains("featureId=3"));
        assertTrue("toString should contain name", result.contains("name='Test Feature'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = featureType.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'FacilityFeatureType{'", result.contains("FacilityFeatureType{"));
    }
}

