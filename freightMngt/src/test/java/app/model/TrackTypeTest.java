package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for TrackType model.
 * Tests constructors, getters, and setters with 100% coverage.
 */
public class TrackTypeTest {

    private TrackType trackType;

    @Before
    public void setUp() {
        trackType = new TrackType();
    }

    @Test
    public void testDefaultConstructor() {
        TrackType defaultType = new TrackType();

        assertNull("Track Type ID should be null", defaultType.getTrackTypeId());
        assertNull("Name should be null", defaultType.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        TrackType paramType = new TrackType(1, "Mainline");

        assertEquals("Track Type ID should be 1", Integer.valueOf(1), paramType.getTrackTypeId());
        assertEquals("Name should be 'Mainline'", "Mainline", paramType.getName());
    }

    @Test
    public void testSetAndGetTrackTypeId() {
        trackType.setTrackTypeId(10);

        assertEquals("Track Type ID should be 10", Integer.valueOf(10), trackType.getTrackTypeId());
    }

    @Test
    public void testSetAndGetName() {
        trackType.setName("Siding");

        assertEquals("Name should be 'Siding'", "Siding", trackType.getName());
    }

    @Test
    public void testSetTrackTypeIdToNull() {
        trackType.setTrackTypeId(5);
        trackType.setTrackTypeId(null);

        assertNull("Track Type ID should be null", trackType.getTrackTypeId());
    }

    @Test
    public void testSetNameToNull() {
        trackType.setName("Test Type");
        trackType.setName(null);

        assertNull("Name should be null", trackType.getName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        trackType.setTrackTypeId(3);
        trackType.setName("Yard Track");

        assertEquals("Track Type ID should be 3", Integer.valueOf(3), trackType.getTrackTypeId());
        assertEquals("Name should be 'Yard Track'", "Yard Track", trackType.getName());
    }

    @Test
    public void testConstructorWithNullValues() {
        TrackType nullType = new TrackType(null, null);

        assertNull("Track Type ID should be null", nullType.getTrackTypeId());
        assertNull("Name should be null", nullType.getName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        TrackType emptyNameType = new TrackType(1, "");

        assertEquals("Track Type ID should be 1", Integer.valueOf(1), emptyNameType.getTrackTypeId());
        assertEquals("Name should be empty string", "", emptyNameType.getName());
    }
}

