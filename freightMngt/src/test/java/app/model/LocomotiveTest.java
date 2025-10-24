package app.model;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Test class for Locomotive model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class LocomotiveTest {

    private Locomotive locomotive;

    @Before
    public void setUp() {
        locomotive = new Locomotive();
    }

    @Test
    public void testDefaultConstructor() {
        Locomotive defaultLocomotive = new Locomotive();

        assertNull("Locomotive ID should be null", defaultLocomotive.getLocomotiveId());
        assertNull("Operator should be null", defaultLocomotive.getOperator());
        assertNull("Locomotive Type should be null", defaultLocomotive.getLocomotiveType());
        assertNull("Brand should be null", defaultLocomotive.getBrand());
        assertNull("Model should be null", defaultLocomotive.getModel());
        assertNull("Year should be null", defaultLocomotive.getYear());
        assertNull("Power should be null", defaultLocomotive.getPower());
        assertNull("Acceleration should be null", defaultLocomotive.getAcceleration());
        assertNull("Dimensions should be null", defaultLocomotive.getDimensions());
        assertNull("Max Speed should be null", defaultLocomotive.getMaxSpeed());
        assertNull("Operational Speed should be null", defaultLocomotive.getOperationalSpeed());
        assertNull("Max Weight should be null", defaultLocomotive.getMaxWeight());
        assertNull("Total Weight should be null", defaultLocomotive.getTotalWeight());
        assertNull("Max Bogies should be null", defaultLocomotive.getMaxBogies());
        assertNull("Bogie Type should be null", defaultLocomotive.getBogieType());
        assertNull("Traction should be null", defaultLocomotive.getTraction());
    }

    @Test
    public void testParameterizedConstructor() {
        User operator = new User();
        LocomotiveType locomotiveType = new LocomotiveType();
        Dimension dimensions = new Dimension();

        Locomotive paramLocomotive = new Locomotive(
            1, operator, locomotiveType, "Siemens", "Vectron", 2020,
            6400, 0.5, dimensions, 160.0, 120.0, 100.0,
            90.0, 4, "Y25", "300"
        );

        assertEquals("Locomotive ID should be 1", Integer.valueOf(1), paramLocomotive.getLocomotiveId());
        assertEquals("Operator should match", operator, paramLocomotive.getOperator());
        assertEquals("Locomotive Type should match", locomotiveType, paramLocomotive.getLocomotiveType());
        assertEquals("Brand should be 'Siemens'", "Siemens", paramLocomotive.getBrand());
        assertEquals("Model should be 'Vectron'", "Vectron", paramLocomotive.getModel());
        assertEquals("Year should be 2020", Integer.valueOf(2020), paramLocomotive.getYear());
        assertEquals("Power should be 6400", Integer.valueOf(6400), paramLocomotive.getPower());
        assertEquals("Acceleration should be 0.5", Double.valueOf(0.5), paramLocomotive.getAcceleration());
        assertEquals("Dimensions should match", dimensions, paramLocomotive.getDimensions());
        assertEquals("Max Speed should be 160.0", Double.valueOf(160.0), paramLocomotive.getMaxSpeed());
        assertEquals("Operational Speed should be 120.0", Double.valueOf(120.0), paramLocomotive.getOperationalSpeed());
        assertEquals("Max Weight should be 100.0", Double.valueOf(100.0), paramLocomotive.getMaxWeight());
        assertEquals("Total Weight should be 90.0", Double.valueOf(90.0), paramLocomotive.getTotalWeight());
        assertEquals("Max Bogies should be 4", Integer.valueOf(4), paramLocomotive.getMaxBogies());
        assertEquals("Bogie Type should be 'Y25'", "Y25", paramLocomotive.getBogieType());
        assertEquals("Traction should be '300'", "300", paramLocomotive.getTraction());
    }

    @Test
    public void testSetAndGetLocomotiveId() {
        locomotive.setLocomotiveId(10);
        assertEquals("Locomotive ID should be 10", Integer.valueOf(10), locomotive.getLocomotiveId());
    }

    @Test
    public void testSetAndGetOperator() {
        User operator = new User();
        locomotive.setOperator(operator);
        assertEquals("Operator should match", operator, locomotive.getOperator());
    }

    @Test
    public void testSetAndGetLocomotiveType() {
        LocomotiveType locomotiveType = new LocomotiveType();
        locomotive.setLocomotiveType(locomotiveType);
        assertEquals("Locomotive Type should match", locomotiveType, locomotive.getLocomotiveType());
    }

    @Test
    public void testSetAndGetBrand() {
        locomotive.setBrand("Alstom");
        assertEquals("Brand should be 'Alstom'", "Alstom", locomotive.getBrand());
    }

    @Test
    public void testSetAndGetModel() {
        locomotive.setModel("Prima");
        assertEquals("Model should be 'Prima'", "Prima", locomotive.getModel());
    }

    @Test
    public void testSetAndGetYear() {
        locomotive.setYear(2019);
        assertEquals("Year should be 2019", Integer.valueOf(2019), locomotive.getYear());
    }

    @Test
    public void testSetAndGetPower() {
        locomotive.setPower(5600);
        assertEquals("Power should be 5600", Integer.valueOf(5600), locomotive.getPower());
    }

    @Test
    public void testSetAndGetAcceleration() {
        locomotive.setAcceleration(0.6);
        assertEquals("Acceleration should be 0.6", Double.valueOf(0.6), locomotive.getAcceleration());
    }

    @Test
    public void testSetAndGetDimensions() {
        Dimension dimensions = new Dimension();
        locomotive.setDimensions(dimensions);
        assertEquals("Dimensions should match", dimensions, locomotive.getDimensions());
    }

    @Test
    public void testSetAndGetMaxSpeed() {
        locomotive.setMaxSpeed(100.0);
        assertEquals("Max Speed should be 100.0", Double.valueOf(100.0), locomotive.getMaxSpeed());
    }

    @Test
    public void testSetAndGetOperationalSpeed() {
        locomotive.setOperationalSpeed(90.0);
        assertEquals("Operational Speed should be 90.0", Double.valueOf(90.0), locomotive.getOperationalSpeed());
    }

    @Test
    public void testSetAndGetMaxWeight() {
        locomotive.setMaxWeight(80.0);
        assertEquals("Max Weight should be 80.0", Double.valueOf(80.0), locomotive.getMaxWeight());
    }

    @Test
    public void testSetAndGetTotalWeight() {
        locomotive.setTotalWeight(85.0);
        assertEquals("Total Weight should be 85.0", Double.valueOf(85.0), locomotive.getTotalWeight());
    }

    @Test
    public void testSetAndGetMaxBogies() {
        locomotive.setMaxBogies(6);
        assertEquals("Max Bogies should be 6", Integer.valueOf(6), locomotive.getMaxBogies());
    }

    @Test
    public void testSetAndGetBogieType() {
        locomotive.setBogieType("Y23");
        assertEquals("Bogie Type should be 'Y23'", "Y23", locomotive.getBogieType());
    }

    @Test
    public void testSetAndGetTraction() {
        locomotive.setTraction("250");
        assertEquals("Traction should be '250'", "250", locomotive.getTraction());
    }

    @Test
    public void testGetName() {
        locomotive.setBrand("Siemens");
        locomotive.setModel("Vectron");
        locomotive.setLocomotiveId(123);

        String name = locomotive.getName();

        assertEquals("Name should be 'Siemens Vectron 123'", "Siemens Vectron 123", name);
    }

    @Test
    public void testGetNameWithNullValues() {
        String name = locomotive.getName();

        assertEquals("Name should be 'null null null'", "null null null", name);
    }

    @Test
    public void testConstructorWithNullValues() {
        Locomotive nullLocomotive = new Locomotive(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertNull("All fields should be null", nullLocomotive.getLocomotiveId());
    }

    @Test
    public void testToString() {
        User operator = new User();
        LocomotiveType locomotiveType = new LocomotiveType();
        Dimension dimensions = new Dimension();

        locomotive.setLocomotiveId(7);
        locomotive.setOperator(operator);
        locomotive.setLocomotiveType(locomotiveType);
        locomotive.setBrand("Bombardier");
        locomotive.setModel("Traxx");
        locomotive.setYear(2021);
        locomotive.setPower(7200);
        locomotive.setAcceleration(0.45);
        locomotive.setDimensions(dimensions);
        locomotive.setMaxSpeed(180.0);
        locomotive.setOperationalSpeed(140.0);
        locomotive.setMaxWeight(110.0);
        locomotive.setTotalWeight(95.0);
        locomotive.setMaxBogies(4);
        locomotive.setBogieType("Y25");
        locomotive.setTraction("320");

        String result = locomotive.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Locomotive{'", result.contains("Locomotive{"));
        assertTrue("toString should contain locomotiveId", result.contains("locomotiveId=7"));
        assertTrue("toString should contain brand", result.contains("brand='Bombardier'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = locomotive.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Locomotive{'", result.contains("Locomotive{"));
    }
}
