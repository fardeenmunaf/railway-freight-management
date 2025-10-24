package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for WagonModel model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WagonModelTest {

    private WagonModel wagonModel;

    @Before
    public void setUp() {
        wagonModel = new WagonModel();
    }

    @Test
    public void testDefaultConstructor() {
        WagonModel defaultModel = new WagonModel();

        assertNull("Wagon Model ID should be null", defaultModel.getWagonModelId());
        assertNull("Name should be null", defaultModel.getName());
        assertNull("Wagon Type should be null", defaultModel.getWagonType());
        assertNull("Constructor should be null", defaultModel.getConstructor());
        assertNull("Bogies should be null", defaultModel.getBogies());
        assertNull("Number Bogies should be null", defaultModel.getNumberBogies());
        assertNull("Tare Weight should be null", defaultModel.getTareWeight());
        assertNull("Payload Weight should be null", defaultModel.getPayloadWeight());
        assertNull("Max Speed should be null", defaultModel.getMaxSpeed());
        assertNull("Dimensions should be null", defaultModel.getDimensions());
    }

    @Test
    public void testParameterizedConstructor() {
        WagonType wagonType = new WagonType();
        Dimensions dimensions = new Dimensions(2.0, 3.0, 4.0);

        WagonModel paramModel = new WagonModel(1, "Model X", wagonType, "ACME", "Y25", 4, 20.0, 80.0, 120.0, dimensions);

        assertEquals("Wagon Model ID should be 1", Integer.valueOf(1), paramModel.getWagonModelId());
        assertEquals("Name should be 'Model X'", "Model X", paramModel.getName());
        assertEquals("Wagon Type should match", wagonType, paramModel.getWagonType());
        assertEquals("Constructor should be 'ACME'", "ACME", paramModel.getConstructor());
        assertEquals("Bogies should be 'Y25'", "Y25", paramModel.getBogies());
        assertEquals("Number Bogies should be 4", Integer.valueOf(4), paramModel.getNumberBogies());
        assertEquals("Tare Weight should be 20.0", Double.valueOf(20.0), paramModel.getTareWeight());
        assertEquals("Payload Weight should be 80.0", Double.valueOf(80.0), paramModel.getPayloadWeight());
        assertEquals("Max Speed should be 120.0", Double.valueOf(120.0), paramModel.getMaxSpeed());
        assertEquals("Dimensions should match", dimensions, paramModel.getDimensions());
    }

    @Test
    public void testSetAndGetWagonModelId() {
        wagonModel.setWagonModelId(10);

        assertEquals("Wagon Model ID should be 10", Integer.valueOf(10), wagonModel.getWagonModelId());
    }

    @Test
    public void testSetAndGetName() {
        wagonModel.setName("Flatcar 2000");

        assertEquals("Name should be 'Flatcar 2000'", "Flatcar 2000", wagonModel.getName());
    }

    @Test
    public void testSetAndGetWagonType() {
        WagonType wagonType = new WagonType();
        wagonModel.setWagonType(wagonType);

        assertEquals("Wagon Type should match", wagonType, wagonModel.getWagonType());
    }

    @Test
    public void testSetAndGetConstructor() {
        wagonModel.setConstructor("Siemens");

        assertEquals("Constructor should be 'Siemens'", "Siemens", wagonModel.getConstructor());
    }

    @Test
    public void testSetAndGetBogies() {
        wagonModel.setBogies("Diamond Frame");

        assertEquals("Bogies should be 'Diamond Frame'", "Diamond Frame", wagonModel.getBogies());
    }

    @Test
    public void testSetAndGetNumberBogies() {
        wagonModel.setNumberBogies(6);

        assertEquals("Number Bogies should be 6", Integer.valueOf(6), wagonModel.getNumberBogies());
    }

    @Test
    public void testSetAndGetTareWeight() {
        wagonModel.setTareWeight(25.5);

        assertEquals("Tare Weight should be 25.5", Double.valueOf(25.5), wagonModel.getTareWeight());
    }

    @Test
    public void testSetAndGetPayloadWeight() {
        wagonModel.setPayloadWeight(100.0);

        assertEquals("Payload Weight should be 100.0", Double.valueOf(100.0), wagonModel.getPayloadWeight());
    }

    @Test
    public void testSetAndGetMaxSpeed() {
        wagonModel.setMaxSpeed(140.0);

        assertEquals("Max Speed should be 140.0", Double.valueOf(140.0), wagonModel.getMaxSpeed());
    }

    @Test
    public void testSetAndGetDimensions() {
        Dimensions dimensions = new Dimensions(3.0, 15.0, 4.5);
        wagonModel.setDimensions(dimensions);

        assertEquals("Dimensions should match", dimensions, wagonModel.getDimensions());
    }

    @Test
    public void testSetWagonModelIdToNull() {
        wagonModel.setWagonModelId(5);
        wagonModel.setWagonModelId(null);

        assertNull("Wagon Model ID should be null", wagonModel.getWagonModelId());
    }

    @Test
    public void testSetNameToNull() {
        wagonModel.setName("Test Model");
        wagonModel.setName(null);

        assertNull("Name should be null", wagonModel.getName());
    }

    @Test
    public void testSetWagonTypeToNull() {
        WagonType wagonType = new WagonType();
        wagonModel.setWagonType(wagonType);
        wagonModel.setWagonType(null);

        assertNull("Wagon Type should be null", wagonModel.getWagonType());
    }

    @Test
    public void testSetConstructorToNull() {
        wagonModel.setConstructor("Test Constructor");
        wagonModel.setConstructor(null);

        assertNull("Constructor should be null", wagonModel.getConstructor());
    }

    @Test
    public void testSetBogiesToNull() {
        wagonModel.setBogies("Test Bogies");
        wagonModel.setBogies(null);

        assertNull("Bogies should be null", wagonModel.getBogies());
    }

    @Test
    public void testSetNumberBogiesToNull() {
        wagonModel.setNumberBogies(4);
        wagonModel.setNumberBogies(null);

        assertNull("Number Bogies should be null", wagonModel.getNumberBogies());
    }

    @Test
    public void testSetTareWeightToNull() {
        wagonModel.setTareWeight(30.0);
        wagonModel.setTareWeight(null);

        assertNull("Tare Weight should be null", wagonModel.getTareWeight());
    }

    @Test
    public void testSetPayloadWeightToNull() {
        wagonModel.setPayloadWeight(90.0);
        wagonModel.setPayloadWeight(null);

        assertNull("Payload Weight should be null", wagonModel.getPayloadWeight());
    }

    @Test
    public void testSetMaxSpeedToNull() {
        wagonModel.setMaxSpeed(150.0);
        wagonModel.setMaxSpeed(null);

        assertNull("Max Speed should be null", wagonModel.getMaxSpeed());
    }

    @Test
    public void testSetDimensionsToNull() {
        Dimensions dimensions = new Dimensions(2.0, 10.0, 3.0);
        wagonModel.setDimensions(dimensions);
        wagonModel.setDimensions(null);

        assertNull("Dimensions should be null", wagonModel.getDimensions());
    }

    @Test
    public void testConstructorWithNullValues() {
        WagonModel nullModel = new WagonModel(null, null, null, null, null, null, null, null, null, null);

        assertNull("Wagon Model ID should be null", nullModel.getWagonModelId());
        assertNull("Name should be null", nullModel.getName());
        assertNull("Wagon Type should be null", nullModel.getWagonType());
        assertNull("Constructor should be null", nullModel.getConstructor());
        assertNull("Bogies should be null", nullModel.getBogies());
        assertNull("Number Bogies should be null", nullModel.getNumberBogies());
        assertNull("Tare Weight should be null", nullModel.getTareWeight());
        assertNull("Payload Weight should be null", nullModel.getPayloadWeight());
        assertNull("Max Speed should be null", nullModel.getMaxSpeed());
        assertNull("Dimensions should be null", nullModel.getDimensions());
    }

    @Test
    public void testToString() {
        WagonType wagonType = new WagonType();
        Dimensions dimensions = new Dimensions(2.5, 12.0, 3.5);
        wagonModel.setWagonModelId(7);
        wagonModel.setName("Tank Wagon");
        wagonModel.setWagonType(wagonType);
        wagonModel.setConstructor("Bombardier");
        wagonModel.setBogies("Y21");
        wagonModel.setNumberBogies(2);
        wagonModel.setTareWeight(18.0);
        wagonModel.setPayloadWeight(70.0);
        wagonModel.setMaxSpeed(100.0);
        wagonModel.setDimensions(dimensions);

        String result = wagonModel.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain WagonModelId", result.contains("WagonModelId=7"));
        assertTrue("toString should contain name", result.contains("name='Tank Wagon'"));
        assertTrue("toString should contain constructor", result.contains("constructor='Bombardier'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = wagonModel.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'WagonModel{'", result.contains("WagonModel{"));
    }
}

