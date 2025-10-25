package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Line model.
 * Tests constructors, getters, and setters with 100% coverage.
 */
public class LineTest {

    private Line line;

    @Before
    public void setUp() {
        line = new Line();
    }

    @Test
    public void testDefaultConstructor() {
        Line defaultLine = new Line();

        assertNull("Line ID should be null", defaultLine.getLineId());
        assertNull("Owner should be null", defaultLine.getOwner());
    }

    @Test
    public void testParameterizedConstructor() {
        User owner = new User();
        Line paramLine = new Line(1, owner);

        assertEquals("Line ID should be 1", Integer.valueOf(1), paramLine.getLineId());
        assertEquals("Owner should match", owner, paramLine.getOwner());
    }

    @Test
    public void testSetAndGetLineId() {
        line.setLineId(10);

        assertEquals("Line ID should be 10", Integer.valueOf(10), line.getLineId());
    }

    @Test
    public void testSetAndGetOwner() {
        User owner = new User();
        line.setOwner(owner);

        assertEquals("Owner should match", owner, line.getOwner());
    }

    @Test
    public void testSetLineIdToNull() {
        line.setLineId(5);
        line.setLineId(null);

        assertNull("Line ID should be null", line.getLineId());
    }

    @Test
    public void testSetOwnerToNull() {
        User owner = new User();
        line.setOwner(owner);
        line.setOwner(null);

        assertNull("Owner should be null", line.getOwner());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        User owner = new User();
        line.setLineId(3);
        line.setOwner(owner);

        assertEquals("Line ID should be 3", Integer.valueOf(3), line.getLineId());
        assertEquals("Owner should match", owner, line.getOwner());
    }

    @Test
    public void testConstructorWithNullValues() {
        Line nullLine = new Line(null, null);

        assertNull("Line ID should be null", nullLine.getLineId());
        assertNull("Owner should be null", nullLine.getOwner());
    }

    @Test
    public void testConstructorWithNullOwner() {
        Line lineWithNullOwner = new Line(1, null);

        assertEquals("Line ID should be 1", Integer.valueOf(1), lineWithNullOwner.getLineId());
        assertNull("Owner should be null", lineWithNullOwner.getOwner());
    }
}

