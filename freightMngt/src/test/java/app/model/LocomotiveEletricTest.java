package app.model;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Test class for LocomotiveEletric model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class LocomotiveEletricTest {

    private LocomotiveEletric locomotiveEletric;

    @Before
    public void setUp() {
        locomotiveEletric = new LocomotiveEletric();
    }

    @Test
    public void testDefaultConstructor() {
        LocomotiveEletric defaultEletric = new LocomotiveEletric();

        assertNull("Locomotive should be null", defaultEletric.getLocomotive());
        assertNull("Fuel Capacity should be null", defaultEletric.getFuelCapacity());
    }

    @Test
    public void testParameterizedConstructorBasic() {
        Locomotive locomotive = new Locomotive();
        LocomotiveEletric paramEletric = new LocomotiveEletric(locomotive, 3000.0);

        assertEquals("Locomotive should match", locomotive, paramEletric.getLocomotive());
        assertEquals("Fuel Capacity should be 3000.0", Double.valueOf(3000.0), paramEletric.getFuelCapacity());
    }

    @Test
    public void testParameterizedConstructorFull() {
        User operator = new User();
        LocomotiveType locomotiveType = new LocomotiveType();
        Dimension dimensions = new Dimension();
        Locomotive locomotive = new Locomotive();

        LocomotiveEletric fullEletric = new LocomotiveEletric(
            1, operator, locomotiveType, "Alstom", "Prima", 2019,
            5600, 0.6, dimensions, 140.0, 100.0, 80.0,
            85.0, 4, "Y23", "AC", locomotive, 3500.0
        );

        assertEquals("Locomotive ID should be 1", Integer.valueOf(1), fullEletric.getLocomotiveId());
        assertEquals("Brand should be 'Alstom'", "Alstom", fullEletric.getBrand());
        assertEquals("Model should be 'Prima'", "Prima", fullEletric.getModel());
        assertEquals("Locomotive should match", locomotive, fullEletric.getLocomotive());
        assertEquals("Fuel Capacity should be 3500.0", Double.valueOf(3500.0), fullEletric.getFuelCapacity());
    }

    @Test
    public void testSetAndGetLocomotive() {
        Locomotive locomotive = new Locomotive();
        locomotiveEletric.setLocomotive(locomotive);

        assertEquals("Locomotive should match", locomotive, locomotiveEletric.getLocomotive());
    }

    @Test
    public void testSetAndGetFuelCapacity() {
        locomotiveEletric.setFuelCapacity(4000.0);

        assertEquals("Fuel Capacity should be 4000.0", Double.valueOf(4000.0), locomotiveEletric.getFuelCapacity());
    }

    @Test
    public void testSetLocomotiveToNull() {
        Locomotive locomotive = new Locomotive();
        locomotiveEletric.setLocomotive(locomotive);
        locomotiveEletric.setLocomotive(null);

        assertNull("Locomotive should be null", locomotiveEletric.getLocomotive());
    }

    @Test
    public void testSetFuelCapacityToNull() {
        locomotiveEletric.setFuelCapacity(3200.0);
        locomotiveEletric.setFuelCapacity(null);

        assertNull("Fuel Capacity should be null", locomotiveEletric.getFuelCapacity());
    }

    @Test
    public void testConstructorWithNullValues() {
        LocomotiveEletric nullEletric = new LocomotiveEletric(null, null);

        assertNull("Locomotive should be null", nullEletric.getLocomotive());
        assertNull("Fuel Capacity should be null", nullEletric.getFuelCapacity());
    }

    @Test
    public void testToString() {
        Locomotive locomotive = new Locomotive();
        locomotiveEletric.setLocomotive(locomotive);
        locomotiveEletric.setFuelCapacity(2800.0);

        String result = locomotiveEletric.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveDiesel{'", result.contains("LocomotiveDiesel{"));
        assertTrue("toString should contain locomotive", result.contains("locomotive="));
        assertTrue("toString should contain fuelCapacity", result.contains("fuelCapacity=2800.0"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = locomotiveEletric.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LocomotiveDiesel{'", result.contains("LocomotiveDiesel{"));
    }
}

