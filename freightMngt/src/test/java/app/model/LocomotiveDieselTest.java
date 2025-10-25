package app.model;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Test class for LocomotiveDiesel model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class LocomotiveDieselTest {

    private LocomotiveDiesel locomotiveDiesel;

    @Before
    public void setUp() {
        locomotiveDiesel = new LocomotiveDiesel();
    }

    @Test
    public void testDefaultConstructor() {
        LocomotiveDiesel defaultDiesel = new LocomotiveDiesel();

        assertNull("Locomotive should be null", defaultDiesel.getLocomotive());
        assertNull("Fuel Capacity should be null", defaultDiesel.getFuelCapacity());
    }

    @Test
    public void testParameterizedConstructorBasic() {
        Locomotive locomotive = new Locomotive();
        LocomotiveDiesel paramDiesel = new LocomotiveDiesel(locomotive, 5000.0);

        assertEquals("Locomotive should match", locomotive, paramDiesel.getLocomotive());
        assertEquals("Fuel Capacity should be 5000.0", Double.valueOf(5000.0), paramDiesel.getFuelCapacity());
    }

    @Test
    public void testParameterizedConstructorFull() {
        User operator = new User();
        LocomotiveType locomotiveType = new LocomotiveType();
        Dimension dimensions = new Dimension();
        Locomotive locomotive = new Locomotive();

        LocomotiveDiesel fullDiesel = new LocomotiveDiesel(
            1, operator, locomotiveType, "Siemens", "Vectron", 2020,
            6400, 0.5, dimensions, 160.0, 120.0, 100.0,
            90.0, 4, "Y25", "Electric", locomotive, 4500.0
        );

        assertEquals("Locomotive ID should be 1", Integer.valueOf(1), fullDiesel.getLocomotiveId());
        assertEquals("Brand should be 'Siemens'", "Siemens", fullDiesel.getBrand());
        assertEquals("Model should be 'Vectron'", "Vectron", fullDiesel.getModel());
        assertEquals("Locomotive should match", locomotive, fullDiesel.getLocomotive());
        assertEquals("Fuel Capacity should be 4500.0", Double.valueOf(4500.0), fullDiesel.getFuelCapacity());
    }

    @Test
    public void testSetAndGetLocomotive() {
        Locomotive locomotive = new Locomotive();
        locomotiveDiesel.setLocomotive(locomotive);

        assertEquals("Locomotive should match", locomotive, locomotiveDiesel.getLocomotive());
    }

    @Test
    public void testSetAndGetFuelCapacity() {
        locomotiveDiesel.setFuelCapacity(6000.0);

        assertEquals("Fuel Capacity should be 6000.0", Double.valueOf(6000.0), locomotiveDiesel.getFuelCapacity());
    }

    @Test
    public void testSetLocomotiveToNull() {
        Locomotive locomotive = new Locomotive();
        locomotiveDiesel.setLocomotive(locomotive);
        locomotiveDiesel.setLocomotive(null);

        assertNull("Locomotive should be null", locomotiveDiesel.getLocomotive());
    }

    @Test
    public void testSetFuelCapacityToNull() {
        locomotiveDiesel.setFuelCapacity(5500.0);
        locomotiveDiesel.setFuelCapacity(null);

        assertNull("Fuel Capacity should be null", locomotiveDiesel.getFuelCapacity());
    }

    @Test
    public void testConstructorWithNullValues() {
        LocomotiveDiesel nullDiesel = new LocomotiveDiesel(null, null);

        assertNull("Locomotive should be null", nullDiesel.getLocomotive());
        assertNull("Fuel Capacity should be null", nullDiesel.getFuelCapacity());
    }

    @Test
    public void testToString() {
        Locomotive locomotive = new Locomotive();
        locomotiveDiesel.setLocomotive(locomotive);
        locomotiveDiesel.setFuelCapacity(7000.0);

        String result = locomotiveDiesel.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveDiesel{'", result.contains("LocomotiveDiesel{"));
        assertTrue("toString should contain locomotive", result.contains("locomotive="));
        assertTrue("toString should contain fuelCapacity", result.contains("fuelCapacity=7000.0"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = locomotiveDiesel.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveDiesel{'", result.contains("LocomotiveDiesel{"));
    }
}

