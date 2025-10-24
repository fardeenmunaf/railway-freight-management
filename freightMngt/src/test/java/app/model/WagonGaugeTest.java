package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for WagonGauge model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class WagonGaugeTest {

    private WagonGauge wagonGauge;

    @Before
    public void setUp() {
        wagonGauge = new WagonGauge();
    }

    @Test
    public void testDefaultConstructor() {
        WagonGauge defaultWagonGauge = new WagonGauge();

        assertNull("Wagon should be null", defaultWagonGauge.getWagon());
        assertNull("Gauge should be null", defaultWagonGauge.getGauge());
    }

    @Test
    public void testParameterizedConstructor() {
        Wagon wagon = new Wagon();
        Gauge gauge = new Gauge();

        WagonGauge paramWagonGauge = new WagonGauge(wagon, gauge);

        assertEquals("Wagon should match", wagon, paramWagonGauge.getWagon());
        assertEquals("Gauge should match", gauge, paramWagonGauge.getGauge());
    }

    @Test
    public void testSetAndGetWagon() {
        Wagon wagon = new Wagon();
        wagonGauge.setWagon(wagon);

        assertEquals("Wagon should match", wagon, wagonGauge.getWagon());
    }

    @Test
    public void testSetAndGetGauge() {
        Gauge gauge = new Gauge();
        wagonGauge.setGauge(gauge);

        assertEquals("Gauge should match", gauge, wagonGauge.getGauge());
    }

    @Test
    public void testSetWagonToNull() {
        Wagon wagon = new Wagon();
        wagonGauge.setWagon(wagon);
        wagonGauge.setWagon(null);

        assertNull("Wagon should be null", wagonGauge.getWagon());
    }

    @Test
    public void testSetGaugeToNull() {
        Gauge gauge = new Gauge();
        wagonGauge.setGauge(gauge);
        wagonGauge.setGauge(null);

        assertNull("Gauge should be null", wagonGauge.getGauge());
    }

    @Test
    public void testConstructorWithNullValues() {
        WagonGauge nullWagonGauge = new WagonGauge(null, null);

        assertNull("Wagon should be null", nullWagonGauge.getWagon());
        assertNull("Gauge should be null", nullWagonGauge.getGauge());
    }

    @Test
    public void testToString() {
        Wagon wagon = new Wagon();
        Gauge gauge = new Gauge();
        wagonGauge.setWagon(wagon);
        wagonGauge.setGauge(gauge);

        String result = wagonGauge.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'WagonGauge{'", result.contains("WagonGauge{"));
        assertTrue("toString should contain wagon", result.contains("wagon="));
        assertTrue("toString should contain gauge", result.contains("gauge="));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = wagonGauge.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'WagonGauge{'", result.contains("WagonGauge{"));
    }
}

