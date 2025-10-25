package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for LocomotiveGauge model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class LocomotiveGaugeTest {

    private LocomotiveGauge locomotiveGauge;

    @Before
    public void setUp() {
        locomotiveGauge = new LocomotiveGauge();
    }

    @Test
    public void testDefaultConstructor() {
        LocomotiveGauge defaultLocomotiveGauge = new LocomotiveGauge();

        assertNull("Locomotive should be null", defaultLocomotiveGauge.getLocomotive());
        assertNull("Gauge should be null", defaultLocomotiveGauge.getGauge());
    }

    @Test
    public void testParameterizedConstructor() {
        Locomotive locomotive = new Locomotive();
        Gauge gauge = new Gauge();

        LocomotiveGauge paramLocomotiveGauge = new LocomotiveGauge(locomotive, gauge);

        assertEquals("Locomotive should match", locomotive, paramLocomotiveGauge.getLocomotive());
        assertEquals("Gauge should match", gauge, paramLocomotiveGauge.getGauge());
    }

    @Test
    public void testSetAndGetLocomotive() {
        Locomotive locomotive = new Locomotive();
        locomotiveGauge.setLocomotive(locomotive);

        assertEquals("Locomotive should match", locomotive, locomotiveGauge.getLocomotive());
    }

    @Test
    public void testSetAndGetGauge() {
        Gauge gauge = new Gauge();
        locomotiveGauge.setGauge(gauge);

        assertEquals("Gauge should match", gauge, locomotiveGauge.getGauge());
    }

    @Test
    public void testSetLocomotiveToNull() {
        Locomotive locomotive = new Locomotive();
        locomotiveGauge.setLocomotive(locomotive);
        locomotiveGauge.setLocomotive(null);

        assertNull("Locomotive should be null", locomotiveGauge.getLocomotive());
    }

    @Test
    public void testSetGaugeToNull() {
        Gauge gauge = new Gauge();
        locomotiveGauge.setGauge(gauge);
        locomotiveGauge.setGauge(null);

        assertNull("Gauge should be null", locomotiveGauge.getGauge());
    }

    @Test
    public void testConstructorWithNullValues() {
        LocomotiveGauge nullLocomotiveGauge = new LocomotiveGauge(null, null);

        assertNull("Locomotive should be null", nullLocomotiveGauge.getLocomotive());
        assertNull("Gauge should be null", nullLocomotiveGauge.getGauge());
    }

    @Test
    public void testToString() {
        Locomotive locomotive = new Locomotive();
        Gauge gauge = new Gauge();
        locomotiveGauge.setLocomotive(locomotive);
        locomotiveGauge.setGauge(gauge);

        String result = locomotiveGauge.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveGauge{'", result.contains("LocomotiveGauge{"));
        assertTrue("toString should contain locomotive", result.contains("locomotive="));
        assertTrue("toString should contain gauge", result.contains("gauge="));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = locomotiveGauge.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveGauge{'", result.contains("LocomotiveGauge{"));
    }
}

