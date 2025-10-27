package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for LineSegment model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class LineSegmentTest {

    private LineSegment lineSegment;

    @Before
    public void setUp() {
        lineSegment = new LineSegment();
    }

    @Test
    public void testDefaultConstructor() {
        LineSegment defaultSegment = new LineSegment();

        assertNull("Segment ID should be null", defaultSegment.getSegmentId());
        assertNull("Line should be null", defaultSegment.getLine());
        assertNull("Length Km should be null", defaultSegment.getLengthKm());
        assertNull("Track Type should be null", defaultSegment.getTrackType());
        assertNull("Electrif Type should be null", defaultSegment.getElectrifType());
        assertNull("Gauge should be null", defaultSegment.getGauge());
        assertNull("Max Weight KgM should be null", defaultSegment.getMaxWeightKgM());
        assertNull("Speed Limit Kmh should be null", defaultSegment.getSpeedLimitKmh());
        assertNull("Track Number should be null", defaultSegment.getTrackNumber());
        assertNull("Position should be null", defaultSegment.getPosition());
    }

    @Test
    public void testParameterizedConstructor() {
        Line line = new Line();
        TrackType trackType = new TrackType();
        Gauge gauge = new Gauge();

        LineSegment paramSegment = new LineSegment(1, line, 15.5, trackType, true, gauge, 22.5, 120.0, 2, 1);

        assertEquals("Segment ID should be 1", Integer.valueOf(1), paramSegment.getSegmentId());
        assertEquals("Line should match", line, paramSegment.getLine());
        assertEquals("Length Km should be 15.5", Double.valueOf(15.5), paramSegment.getLengthKm());
        assertEquals("Track Type should match", trackType, paramSegment.getTrackType());
        assertEquals("Electrif Type should be true", Boolean.TRUE, paramSegment.getElectrifType());
        assertEquals("Gauge should match", gauge, paramSegment.getGauge());
        assertEquals("Max Weight KgM should be 22.5", Double.valueOf(22.5), paramSegment.getMaxWeightKgM());
        assertEquals("Speed Limit Kmh should be 120.0", Double.valueOf(120.0), paramSegment.getSpeedLimitKmh());
        assertEquals("Track Number should be 2", Integer.valueOf(2), paramSegment.getTrackNumber());
        assertEquals("Position should be 1", Integer.valueOf(1), paramSegment.getPosition());
    }

    @Test
    public void testSetAndGetSegmentId() {
        lineSegment.setSegmentId(10);

        assertEquals("Segment ID should be 10", Integer.valueOf(10), lineSegment.getSegmentId());
    }

    @Test
    public void testSetAndGetLine() {
        Line line = new Line();
        lineSegment.setLine(line);

        assertEquals("Line should match", line, lineSegment.getLine());
    }

    @Test
    public void testSetAndGetLengthKm() {
        lineSegment.setLengthKm(25.3);

        assertEquals("Length Km should be 25.3", Double.valueOf(25.3), lineSegment.getLengthKm());
    }

    @Test
    public void testSetAndGetTrackType() {
        TrackType trackType = new TrackType();
        lineSegment.setTrackType(trackType);

        assertEquals("Track Type should match", trackType, lineSegment.getTrackType());
    }

    @Test
    public void testSetAndGetElectrifType() {
        lineSegment.setElectrifType(false);

        assertEquals("Electrif Type should be false", Boolean.FALSE, lineSegment.getElectrifType());
    }

    @Test
    public void testSetAndGetGauge() {
        Gauge gauge = new Gauge();
        lineSegment.setGauge(gauge);

        assertEquals("Gauge should match", gauge, lineSegment.getGauge());
    }

    @Test
    public void testSetAndGetMaxWeightKgM() {
        lineSegment.setMaxWeightKgM(18.7);

        assertEquals("Max Weight KgM should be 18.7", Double.valueOf(18.7), lineSegment.getMaxWeightKgM());
    }

    @Test
    public void testSetAndGetSpeedLimitKmh() {
        lineSegment.setSpeedLimitKmh(100.0);

        assertEquals("Speed Limit Kmh should be 100.0", Double.valueOf(100.0), lineSegment.getSpeedLimitKmh());
    }

    @Test
    public void testSetAndGetTrackNumber() {
        lineSegment.setTrackNumber(4);

        assertEquals("Track Number should be 4", Integer.valueOf(4), lineSegment.getTrackNumber());
    }

    @Test
    public void testSetAndGetPosition() {
        lineSegment.setPosition(3);

        assertEquals("Position should be 3", Integer.valueOf(3), lineSegment.getPosition());
    }

    @Test
    public void testSetSegmentIdToNull() {
        lineSegment.setSegmentId(5);
        lineSegment.setSegmentId(null);

        assertNull("Segment ID should be null", lineSegment.getSegmentId());
    }

    @Test
    public void testSetLineToNull() {
        Line line = new Line();
        lineSegment.setLine(line);
        lineSegment.setLine(null);

        assertNull("Line should be null", lineSegment.getLine());
    }

    @Test
    public void testSetLengthKmToNull() {
        lineSegment.setLengthKm(10.0);
        lineSegment.setLengthKm(null);

        assertNull("Length Km should be null", lineSegment.getLengthKm());
    }

    @Test
    public void testSetTrackTypeToNull() {
        TrackType trackType = new TrackType();
        lineSegment.setTrackType(trackType);
        lineSegment.setTrackType(null);

        assertNull("Track Type should be null", lineSegment.getTrackType());
    }

    @Test
    public void testSetElectrifTypeToNull() {
        lineSegment.setElectrifType(true);
        lineSegment.setElectrifType(null);

        assertNull("Electrif Type should be null", lineSegment.getElectrifType());
    }

    @Test
    public void testSetGaugeToNull() {
        Gauge gauge = new Gauge();
        lineSegment.setGauge(gauge);
        lineSegment.setGauge(null);

        assertNull("Gauge should be null", lineSegment.getGauge());
    }

    @Test
    public void testSetMaxWeightKgMToNull() {
        lineSegment.setMaxWeightKgM(20.0);
        lineSegment.setMaxWeightKgM(null);

        assertNull("Max Weight KgM should be null", lineSegment.getMaxWeightKgM());
    }

    @Test
    public void testSetSpeedLimitKmhToNull() {
        lineSegment.setSpeedLimitKmh(110.0);
        lineSegment.setSpeedLimitKmh(null);

        assertNull("Speed Limit Kmh should be null", lineSegment.getSpeedLimitKmh());
    }

    @Test
    public void testSetTrackNumberToNull() {
        lineSegment.setTrackNumber(2);
        lineSegment.setTrackNumber(null);

        assertNull("Track Number should be null", lineSegment.getTrackNumber());
    }

    @Test
    public void testSetPositionToNull() {
        lineSegment.setPosition(5);
        lineSegment.setPosition(null);

        assertNull("Position should be null", lineSegment.getPosition());
    }

    @Test
    public void testConstructorWithNullValues() {
        LineSegment nullSegment = new LineSegment(null, null, null, null, null, null, null, null, null, null);

        assertNull("Segment ID should be null", nullSegment.getSegmentId());
        assertNull("Line should be null", nullSegment.getLine());
        assertNull("Length Km should be null", nullSegment.getLengthKm());
        assertNull("Track Type should be null", nullSegment.getTrackType());
        assertNull("Electrif Type should be null", nullSegment.getElectrifType());
        assertNull("Gauge should be null", nullSegment.getGauge());
        assertNull("Max Weight KgM should be null", nullSegment.getMaxWeightKgM());
        assertNull("Speed Limit Kmh should be null", nullSegment.getSpeedLimitKmh());
        assertNull("Track Number should be null", nullSegment.getTrackNumber());
        assertNull("Position should be null", nullSegment.getPosition());
    }

    @Test
    public void testToString() {
        Line line = new Line();
        TrackType trackType = new TrackType();
        Gauge gauge = new Gauge();

        lineSegment.setSegmentId(7);
        lineSegment.setLine(line);
        lineSegment.setLengthKm(30.5);
        lineSegment.setTrackType(trackType);
        lineSegment.setElectrifType(true);
        lineSegment.setGauge(gauge);
        lineSegment.setMaxWeightKgM(25.0);
        lineSegment.setSpeedLimitKmh(140.0);
        lineSegment.setTrackNumber(2);
        lineSegment.setPosition(2);

        String result = lineSegment.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain segmentId", result.contains("segmentId=7"));
        assertTrue("toString should contain lengthKm", result.contains("lengthKm=30.5"));
        assertTrue("toString should contain electrifType", result.contains("electrifType=true"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = lineSegment.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'LineSegment{'", result.contains("LineSegment{"));
    }
}

