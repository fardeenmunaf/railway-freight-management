package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Wagon model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WagonTest {

    private Wagon wagon;

    @Before
    public void setUp() {
        wagon = new Wagon();
    }

    @Test
    public void testDefaultConstructor() {
        Wagon defaultWagon = new Wagon();

        assertNull("Wagon ID should be null", defaultWagon.getWagonId());
        assertNull("Wagon Model should be null", defaultWagon.getWagonModel());
        assertNull("Operator should be null", defaultWagon.getOperator());
        assertNull("Service Year should be null", defaultWagon.getServiceYear());
    }

    @Test
    public void testParameterizedConstructor() {
        WagonModel wagonModel = new WagonModel();
        User operator = new User();

        Wagon paramWagon = new Wagon(1, wagonModel, operator, 2020);

        assertEquals("Wagon ID should be 1", Integer.valueOf(1), paramWagon.getWagonId());
        assertEquals("Wagon Model should match", wagonModel, paramWagon.getWagonModel());
        assertEquals("Operator should match", operator, paramWagon.getOperator());
        assertEquals("Service Year should be 2020", Integer.valueOf(2020), paramWagon.getServiceYear());
    }

    @Test
    public void testSetAndGetWagonId() {
        wagon.setWagonId(10);

        assertEquals("Wagon ID should be 10", Integer.valueOf(10), wagon.getWagonId());
    }

    @Test
    public void testSetAndGetWagonModel() {
        WagonModel wagonModel = new WagonModel();
        wagon.setWagonModel(wagonModel);

        assertEquals("Wagon Model should match", wagonModel, wagon.getWagonModel());
    }

    @Test
    public void testSetAndGetOperator() {
        User operator = new User();
        wagon.setOperator(operator);

        assertEquals("Operator should match", operator, wagon.getOperator());
    }

    @Test
    public void testSetAndGetServiceYear() {
        wagon.setServiceYear(2025);

        assertEquals("Service Year should be 2025", Integer.valueOf(2025), wagon.getServiceYear());
    }

    @Test
    public void testSetWagonIdToNull() {
        wagon.setWagonId(5);
        wagon.setWagonId(null);

        assertNull("Wagon ID should be null", wagon.getWagonId());
    }

    @Test
    public void testSetWagonModelToNull() {
        WagonModel wagonModel = new WagonModel();
        wagon.setWagonModel(wagonModel);
        wagon.setWagonModel(null);

        assertNull("Wagon Model should be null", wagon.getWagonModel());
    }

    @Test
    public void testSetOperatorToNull() {
        User operator = new User();
        wagon.setOperator(operator);
        wagon.setOperator(null);

        assertNull("Operator should be null", wagon.getOperator());
    }

    @Test
    public void testSetServiceYearToNull() {
        wagon.setServiceYear(2020);
        wagon.setServiceYear(null);

        assertNull("Service Year should be null", wagon.getServiceYear());
    }

    @Test
    public void testConstructorWithNullValues() {
        Wagon nullWagon = new Wagon(null, null, null, null);

        assertNull("Wagon ID should be null", nullWagon.getWagonId());
        assertNull("Wagon Model should be null", nullWagon.getWagonModel());
        assertNull("Operator should be null", nullWagon.getOperator());
        assertNull("Service Year should be null", nullWagon.getServiceYear());
    }

    @Test
    public void testToString() {
        WagonModel wagonModel = new WagonModel();
        User operator = new User();
        wagon.setWagonId(7);
        wagon.setWagonModel(wagonModel);
        wagon.setOperator(operator);
        wagon.setServiceYear(2022);

        String result = wagon.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain wagonId", result.contains("wagonId=7"));
        assertTrue("toString should contain serviceYear", result.contains("serviceYear=2022"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = wagon.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Wagon{'", result.contains("Wagon{"));
    }
}

