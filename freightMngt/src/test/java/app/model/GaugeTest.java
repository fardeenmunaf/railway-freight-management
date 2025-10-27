package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Gauge model.
 * Tests constructors, getters, setters, and business logic.
 */
public class GaugeTest {

    private Gauge gauge;

    @Before
    public void setUp() {
        gauge = new Gauge();
    }

    @Test
    public void testDefaultConstructor() {
        Gauge defaultGauge = new Gauge();

        assertNull("Gauge ID should be null", defaultGauge.getGaugeId());
        assertNull("Name should be null", defaultGauge.getGaugeName());
        assertNull("Size should be null", defaultGauge.getGaugeSize());
    }

    @Test
    public void testParameterizedConstructor() {
        Gauge paramGauge = new Gauge(1, "Standard", 1435.0);

        assertEquals("Gauge ID should be 1", Integer.valueOf(1), paramGauge.getGaugeId());
        assertEquals("Name should be 'Standard'", "Standard", paramGauge.getGaugeName());
        assertEquals("Size should be 1435.0", Double.valueOf(1435.0), paramGauge.getGaugeSize());
    }

    @Test
    public void testSetAndGetGaugeId() {
        gauge.setGaugeId(10);

        assertEquals("Gauge ID should be 10", Integer.valueOf(10), gauge.getGaugeId());
    }

    @Test
    public void testSetAndGetName() {
        gauge.setGaugeName("Narrow");

        assertEquals("Name should be 'Narrow'", "Narrow", gauge.getGaugeName());
    }

    @Test
    public void testSetAndGetSize() {
        gauge.setGaugeSize(1000.0);

        assertEquals("Size should be 1000.0", Double.valueOf(1000.0), gauge.getGaugeSize());
    }

    @Test
    public void testSetGaugeIdToNull() {
        gauge.setGaugeId(5);
        gauge.setGaugeId(null);

        assertNull("Gauge ID should be null", gauge.getGaugeId());
    }

    @Test
    public void testSetNameToNull() {
        gauge.setGaugeName("Test Gauge");
        gauge.setGaugeName(null);

        assertNull("Name should be null", gauge.getGaugeName());
    }

    @Test
    public void testSetSizeToNull() {
        gauge.setGaugeSize(1500.0);
        gauge.setGaugeSize(null);

        assertNull("Size should be null", gauge.getGaugeSize());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        gauge.setGaugeId(3);
        gauge.setGaugeName("Iberian");
        gauge.setGaugeSize(1668.0);

        assertEquals("Gauge ID should be 3", Integer.valueOf(3), gauge.getGaugeId());
        assertEquals("Name should be 'Iberian'", "Iberian", gauge.getGaugeName());
        assertEquals("Size should be 1668.0", Double.valueOf(1668.0), gauge.getGaugeSize());
    }

    @Test
    public void testConstructorWithNullValues() {
        Gauge nullGauge = new Gauge(null, null, null);

        assertNull("Gauge ID should be null", nullGauge.getGaugeId());
        assertNull("Name should be null", nullGauge.getGaugeName());
        assertNull("Size should be null", nullGauge.getGaugeSize());
    }

    @Test
    public void testConstructorWithEmptyName() {
        Gauge emptyNameGauge = new Gauge(1, "", 1435.0);

        assertEquals("Gauge ID should be 1", Integer.valueOf(1), emptyNameGauge.getGaugeId());
        assertEquals("Name should be empty string", "", emptyNameGauge.getGaugeName());
        assertEquals("Size should be 1435.0", Double.valueOf(1435.0), emptyNameGauge.getGaugeSize());
    }

    @Test
    public void testSetSizeToZero() {
        gauge.setGaugeSize(0.0);

        assertEquals("Size should be 0.0", Double.valueOf(0.0), gauge.getGaugeSize());
    }

    @Test
    public void testSetSizeToNegative() {
        gauge.setGaugeSize(-100.0);

        assertEquals("Size should be -100.0", Double.valueOf(-100.0), gauge.getGaugeSize());
    }

    @Test
    public void testToString() {
        gauge.setGaugeId(2);
        gauge.setGaugeName("Standard");
        gauge.setGaugeSize(1435.0);

        String result = gauge.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain gaugeId", result.contains("gaugeId=2"));
        assertTrue("toString should contain gaugeName", result.contains("gaugeName='Standard'"));
        assertTrue("toString should contain gaugeSize", result.contains("gaugeSize=1435.0"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = gauge.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Gauge{'", result.contains("Gauge{"));
    }
}

