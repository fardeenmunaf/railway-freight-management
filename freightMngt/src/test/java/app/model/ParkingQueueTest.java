package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for ParkingQueue model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class ParkingQueueTest {

    private ParkingQueue parkingQueue;

    @Before
    public void setUp() {
        parkingQueue = new ParkingQueue();
    }

    @Test
    public void testDefaultConstructor() {
        ParkingQueue defaultQueue = new ParkingQueue();

        assertNull("Parking Queue ID should be null", defaultQueue.getParkingQueueId());
        assertNull("Parking Track should be null", defaultQueue.getParkingTrack());
        assertNull("Wagon should be null", defaultQueue.getWagon());
        assertNull("Position No should be null", defaultQueue.getPositionNo());
        assertNull("Status should be null", defaultQueue.getStatus());
    }

    @Test
    public void testParameterizedConstructor() {
        ParkingTrack parkingTrack = new ParkingTrack();
        Wagon wagon = new Wagon();

        ParkingQueue paramQueue = new ParkingQueue(1, parkingTrack, wagon, 1, "Waiting");

        assertEquals("Parking Queue ID should be 1", Integer.valueOf(1), paramQueue.getParkingQueueId());
        assertEquals("Parking Track should match", parkingTrack, paramQueue.getParkingTrack());
        assertEquals("Wagon should match", wagon, paramQueue.getWagon());
        assertEquals("Position No should be 1", Integer.valueOf(1), paramQueue.getPositionNo());
        assertEquals("Status should be 'Waiting'", "Waiting", paramQueue.getStatus());
    }

    @Test
    public void testSetAndGetParkingQueueId() {
        parkingQueue.setParkingQueueId(10);

        assertEquals("Parking Queue ID should be 10", Integer.valueOf(10), parkingQueue.getParkingQueueId());
    }

    @Test
    public void testSetAndGetParkingTrack() {
        ParkingTrack parkingTrack = new ParkingTrack();
        parkingQueue.setParkingTrack(parkingTrack);

        assertEquals("Parking Track should match", parkingTrack, parkingQueue.getParkingTrack());
    }

    @Test
    public void testSetAndGetWagon() {
        Wagon wagon = new Wagon();
        parkingQueue.setWagon(wagon);

        assertEquals("Wagon should match", wagon, parkingQueue.getWagon());
    }

    @Test
    public void testSetAndGetPositionNo() {
        parkingQueue.setPositionNo(5);

        assertEquals("Position No should be 5", Integer.valueOf(5), parkingQueue.getPositionNo());
    }

    @Test
    public void testSetAndGetStatus() {
        parkingQueue.setStatus("Ready");

        assertEquals("Status should be 'Ready'", "Ready", parkingQueue.getStatus());
    }

    @Test
    public void testSetParkingQueueIdToNull() {
        parkingQueue.setParkingQueueId(5);
        parkingQueue.setParkingQueueId(null);

        assertNull("Parking Queue ID should be null", parkingQueue.getParkingQueueId());
    }

    @Test
    public void testSetParkingTrackToNull() {
        ParkingTrack parkingTrack = new ParkingTrack();
        parkingQueue.setParkingTrack(parkingTrack);
        parkingQueue.setParkingTrack(null);

        assertNull("Parking Track should be null", parkingQueue.getParkingTrack());
    }

    @Test
    public void testSetWagonToNull() {
        Wagon wagon = new Wagon();
        parkingQueue.setWagon(wagon);
        parkingQueue.setWagon(null);

        assertNull("Wagon should be null", parkingQueue.getWagon());
    }

    @Test
    public void testSetPositionNoToNull() {
        parkingQueue.setPositionNo(3);
        parkingQueue.setPositionNo(null);

        assertNull("Position No should be null", parkingQueue.getPositionNo());
    }

    @Test
    public void testSetStatusToNull() {
        parkingQueue.setStatus("Processing");
        parkingQueue.setStatus(null);

        assertNull("Status should be null", parkingQueue.getStatus());
    }

    @Test
    public void testConstructorWithNullValues() {
        ParkingQueue nullQueue = new ParkingQueue(null, null, null, null, null);

        assertNull("Parking Queue ID should be null", nullQueue.getParkingQueueId());
        assertNull("Parking Track should be null", nullQueue.getParkingTrack());
        assertNull("Wagon should be null", nullQueue.getWagon());
        assertNull("Position No should be null", nullQueue.getPositionNo());
        assertNull("Status should be null", nullQueue.getStatus());
    }
}

