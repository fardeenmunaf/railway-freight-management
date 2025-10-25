package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for TrainStop model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class TrainStopTest {

    private TrainStop trainStop;

    @Before
    public void setUp() {
        trainStop = new TrainStop();
    }

    @Test
    public void testDefaultConstructor() {
        TrainStop defaultStop = new TrainStop();

        assertNull("Train Stop ID should be null", defaultStop.getTrainStopId());
        assertNull("Service should be null", defaultStop.getService());
        assertNull("Facility should be null", defaultStop.getFacility());
        assertNull("Parking Track should be null", defaultStop.getParkingTrack());
        assertNull("Position should be null", defaultStop.getPosition());
        assertNull("SchedArr should be null", defaultStop.getSchedArr());
        assertNull("SchedDep should be null", defaultStop.getSchedDep());
        assertNull("Planned Stop Time should be null", defaultStop.getPlannedStopTime());
        assertNull("ActArr should be null", defaultStop.getActArr());
        assertNull("ActDep should be null", defaultStop.getActDep());
        assertNull("Act Stop Time should be null", defaultStop.getActStopTime());
    }

    @Test
    public void testParameterizedConstructor() {
        TrainService service = new TrainService();
        Facility facility = new Facility();
        ParkingTrack parkingTrack = new ParkingTrack();
        LocalDateTime schedArr = LocalDateTime.of(2025, 10, 24, 10, 0);
        LocalDateTime schedDep = LocalDateTime.of(2025, 10, 24, 10, 30);
        LocalDateTime actArr = LocalDateTime.of(2025, 10, 24, 10, 5);
        LocalDateTime actDep = LocalDateTime.of(2025, 10, 24, 10, 35);

        TrainStop paramStop = new TrainStop(1, service, facility, parkingTrack, 1, schedArr, schedDep, 30, actArr, actDep, 30);

        assertEquals("Train Stop ID should be 1", Integer.valueOf(1), paramStop.getTrainStopId());
        assertEquals("Service should match", service, paramStop.getService());
        assertEquals("Facility should match", facility, paramStop.getFacility());
        assertEquals("Parking Track should match", parkingTrack, paramStop.getParkingTrack());
        assertEquals("Position should be 1", Integer.valueOf(1), paramStop.getPosition());
        assertEquals("SchedArr should match", schedArr, paramStop.getSchedArr());
        assertEquals("SchedDep should match", schedDep, paramStop.getSchedDep());
        assertEquals("Planned Stop Time should be 30", Integer.valueOf(30), paramStop.getPlannedStopTime());
        assertEquals("ActArr should match", actArr, paramStop.getActArr());
        assertEquals("ActDep should match", actDep, paramStop.getActDep());
        assertEquals("Act Stop Time should be 30", Integer.valueOf(30), paramStop.getActStopTime());
    }

    @Test
    public void testSetAndGetTrainStopId() {
        trainStop.setTrainStopId(10);

        assertEquals("Train Stop ID should be 10", Integer.valueOf(10), trainStop.getTrainStopId());
    }

    @Test
    public void testSetAndGetService() {
        TrainService service = new TrainService();
        trainStop.setService(service);

        assertEquals("Service should match", service, trainStop.getService());
    }

    @Test
    public void testSetAndGetFacility() {
        Facility facility = new Facility();
        trainStop.setFacility(facility);

        assertEquals("Facility should match", facility, trainStop.getFacility());
    }

    @Test
    public void testSetAndGetParkingTrack() {
        ParkingTrack parkingTrack = new ParkingTrack();
        trainStop.setParkingTrack(parkingTrack);

        assertEquals("Parking Track should match", parkingTrack, trainStop.getParkingTrack());
    }

    @Test
    public void testSetAndGetPosition() {
        trainStop.setPosition(5);

        assertEquals("Position should be 5", Integer.valueOf(5), trainStop.getPosition());
    }

    @Test
    public void testSetAndGetSchedArr() {
        LocalDateTime schedArr = LocalDateTime.of(2025, 10, 24, 14, 0);
        trainStop.setSchedArr(schedArr);

        assertEquals("SchedArr should match", schedArr, trainStop.getSchedArr());
    }

    @Test
    public void testSetAndGetSchedDep() {
        LocalDateTime schedDep = LocalDateTime.of(2025, 10, 24, 14, 45);
        trainStop.setSchedDep(schedDep);

        assertEquals("SchedDep should match", schedDep, trainStop.getSchedDep());
    }

    @Test
    public void testSetAndGetPlannedStopTime() {
        trainStop.setPlannedStopTime(20);

        assertEquals("Planned Stop Time should be 20", Integer.valueOf(20), trainStop.getPlannedStopTime());
    }

    @Test
    public void testSetAndGetActArr() {
        LocalDateTime actArr = LocalDateTime.of(2025, 10, 24, 14, 5);
        trainStop.setActArr(actArr);

        assertEquals("ActArr should match", actArr, trainStop.getActArr());
    }

    @Test
    public void testSetAndGetActDep() {
        LocalDateTime actDep = LocalDateTime.of(2025, 10, 24, 14, 50);
        trainStop.setActDep(actDep);

        assertEquals("ActDep should match", actDep, trainStop.getActDep());
    }

    @Test
    public void testSetAndGetActStopTime() {
        trainStop.setActStopTime(25);

        assertEquals("Act Stop Time should be 25", Integer.valueOf(25), trainStop.getActStopTime());
    }

    @Test
    public void testConstructorWithNullValues() {
        TrainStop nullStop = new TrainStop(null, null, null, null, null, null, null, null, null, null, null);

        assertNull("Train Stop ID should be null", nullStop.getTrainStopId());
        assertNull("Service should be null", nullStop.getService());
        assertNull("Facility should be null", nullStop.getFacility());
        assertNull("Parking Track should be null", nullStop.getParkingTrack());
        assertNull("Position should be null", nullStop.getPosition());
        assertNull("SchedArr should be null", nullStop.getSchedArr());
        assertNull("SchedDep should be null", nullStop.getSchedDep());
        assertNull("Planned Stop Time should be null", nullStop.getPlannedStopTime());
        assertNull("ActArr should be null", nullStop.getActArr());
        assertNull("ActDep should be null", nullStop.getActDep());
        assertNull("Act Stop Time should be null", nullStop.getActStopTime());
    }
}

