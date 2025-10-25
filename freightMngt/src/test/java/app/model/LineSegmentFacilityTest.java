package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for LineSegmentFacility model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class LineSegmentFacilityTest {

    private LineSegmentFacility lineSegmentFacility;

    @Before
    public void setUp() {
        lineSegmentFacility = new LineSegmentFacility();
    }

    @Test
    public void testDefaultConstructor() {
        LineSegmentFacility defaultLineSegmentFacility = new LineSegmentFacility();

        assertNull("Segment Facility ID should be null", defaultLineSegmentFacility.getSegmentFacilityId());
        assertNull("Line Segment should be null", defaultLineSegmentFacility.getLineSegment());
        assertNull("Facility should be null", defaultLineSegmentFacility.getFacility());
    }

    @Test
    public void testParameterizedConstructor() {
        LineSegment lineSegment = new LineSegment();
        Facility facility = new Facility();

        LineSegmentFacility paramLineSegmentFacility = new LineSegmentFacility(1, lineSegment, facility);

        assertEquals("Segment Facility ID should be 1", Integer.valueOf(1), paramLineSegmentFacility.getSegmentFacilityId());
        assertEquals("Line Segment should match", lineSegment, paramLineSegmentFacility.getLineSegment());
        assertEquals("Facility should match", facility, paramLineSegmentFacility.getFacility());
    }

    @Test
    public void testSetAndGetSegmentFacilityId() {
        lineSegmentFacility.setSegmentFacilityId(10);

        assertEquals("Segment Facility ID should be 10", Integer.valueOf(10), lineSegmentFacility.getSegmentFacilityId());
    }

    @Test
    public void testSetAndGetLineSegment() {
        LineSegment lineSegment = new LineSegment();
        lineSegmentFacility.setLineSegment(lineSegment);

        assertEquals("Line Segment should match", lineSegment, lineSegmentFacility.getLineSegment());
    }

    @Test
    public void testSetAndGetFacility() {
        Facility facility = new Facility();
        lineSegmentFacility.setFacility(facility);

        assertEquals("Facility should match", facility, lineSegmentFacility.getFacility());
    }

    @Test
    public void testSetSegmentFacilityIdToNull() {
        lineSegmentFacility.setSegmentFacilityId(5);
        lineSegmentFacility.setSegmentFacilityId(null);

        assertNull("Segment Facility ID should be null", lineSegmentFacility.getSegmentFacilityId());
    }

    @Test
    public void testSetLineSegmentToNull() {
        LineSegment lineSegment = new LineSegment();
        lineSegmentFacility.setLineSegment(lineSegment);
        lineSegmentFacility.setLineSegment(null);

        assertNull("Line Segment should be null", lineSegmentFacility.getLineSegment());
    }

    @Test
    public void testSetFacilityToNull() {
        Facility facility = new Facility();
        lineSegmentFacility.setFacility(facility);
        lineSegmentFacility.setFacility(null);

        assertNull("Facility should be null", lineSegmentFacility.getFacility());
    }

    @Test
    public void testConstructorWithNullValues() {
        LineSegmentFacility nullLineSegmentFacility = new LineSegmentFacility(null, null, null);

        assertNull("Segment Facility ID should be null", nullLineSegmentFacility.getSegmentFacilityId());
        assertNull("Line Segment should be null", nullLineSegmentFacility.getLineSegment());
        assertNull("Facility should be null", nullLineSegmentFacility.getFacility());
    }
}

