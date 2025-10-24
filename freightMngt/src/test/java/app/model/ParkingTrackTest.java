package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for ParkingTrack model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class ParkingTrackTest {

    private ParkingTrack parkingTrack;

    @Before
    public void setUp() {
        parkingTrack = new ParkingTrack();
    }

    @Test
    public void testDefaultConstructor() {
        ParkingTrack defaultTrack = new ParkingTrack();

        assertNull("Parking Track ID should be null", defaultTrack.getParkingTrackId());
        assertNull("Line should be null", defaultTrack.getLine());
        assertNull("Facility should be null", defaultTrack.getFacility());
        assertNull("Name should be null", defaultTrack.getName());
        assertNull("Capacity Wagons should be null", defaultTrack.getCapacityWagons());
    }

    @Test
    public void testParameterizedConstructor() {
        Line line = new Line();
        Facility facility = new Facility();

        ParkingTrack paramTrack = new ParkingTrack(1, line, facility, "Track A", 50);

        assertEquals("Parking Track ID should be 1", Integer.valueOf(1), paramTrack.getParkingTrackId());
        assertEquals("Line should match", line, paramTrack.getLine());
        assertEquals("Facility should match", facility, paramTrack.getFacility());
        assertEquals("Name should be 'Track A'", "Track A", paramTrack.getName());
        assertEquals("Capacity Wagons should be 50", Integer.valueOf(50), paramTrack.getCapacityWagons());
    }

    @Test
    public void testSetAndGetParkingTrackId() {
        parkingTrack.setParkingTrackId(10);

        assertEquals("Parking Track ID should be 10", Integer.valueOf(10), parkingTrack.getParkingTrackId());
    }

    @Test
    public void testSetAndGetLine() {
        Line line = new Line();
        parkingTrack.setLine(line);

        assertEquals("Line should match", line, parkingTrack.getLine());
    }

    @Test
    public void testSetAndGetFacility() {
        Facility facility = new Facility();
        parkingTrack.setFacility(facility);

        assertEquals("Facility should match", facility, parkingTrack.getFacility());
    }

    @Test
    public void testSetAndGetName() {
        parkingTrack.setName("Track B");

        assertEquals("Name should be 'Track B'", "Track B", parkingTrack.getName());
    }

    @Test
    public void testSetAndGetCapacityWagons() {
        parkingTrack.setCapacityWagons(75);

        assertEquals("Capacity Wagons should be 75", Integer.valueOf(75), parkingTrack.getCapacityWagons());
    }

    @Test
    public void testSetParkingTrackIdToNull() {
        parkingTrack.setParkingTrackId(5);
        parkingTrack.setParkingTrackId(null);

        assertNull("Parking Track ID should be null", parkingTrack.getParkingTrackId());
    }

    @Test
    public void testSetLineToNull() {
        Line line = new Line();
        parkingTrack.setLine(line);
        parkingTrack.setLine(null);

        assertNull("Line should be null", parkingTrack.getLine());
    }

    @Test
    public void testSetFacilityToNull() {
        Facility facility = new Facility();
        parkingTrack.setFacility(facility);
        parkingTrack.setFacility(null);

        assertNull("Facility should be null", parkingTrack.getFacility());
    }

    @Test
    public void testSetNameToNull() {
        parkingTrack.setName("Track C");
        parkingTrack.setName(null);

        assertNull("Name should be null", parkingTrack.getName());
    }

    @Test
    public void testSetCapacityWagonsToNull() {
        parkingTrack.setCapacityWagons(100);
        parkingTrack.setCapacityWagons(null);

        assertNull("Capacity Wagons should be null", parkingTrack.getCapacityWagons());
    }

    @Test
    public void testConstructorWithNullValues() {
        ParkingTrack nullTrack = new ParkingTrack(null, null, null, null, null);

        assertNull("Parking Track ID should be null", nullTrack.getParkingTrackId());
        assertNull("Line should be null", nullTrack.getLine());
        assertNull("Facility should be null", nullTrack.getFacility());
        assertNull("Name should be null", nullTrack.getName());
        assertNull("Capacity Wagons should be null", nullTrack.getCapacityWagons());
    }
}

