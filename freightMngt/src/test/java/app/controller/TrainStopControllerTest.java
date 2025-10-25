package app.controller;

import app.model.TrainStop;
import app.model.TrainService;
import app.model.Facility;
import app.model.ParkingTrack;
import app.model.Line;
import app.model.User;
import app.model.Company;
import app.model.FacilityType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TrainStopControllerTest {

    private TrainStopController controller;
    private TrainStop trainStop1;
    private TrainStop trainStop2;
    private TrainStop trainStop3;
    private TrainService trainService1;
    private Facility facility1;
    private ParkingTrack parkingTrack1;

    @Before
    public void setUp() {
        Repositories.getInstance().getTrainStopRepository().clear();
        controller = new TrainStopController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User owner1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        app.model.Train train1 = new app.model.Train(1, 101, "Porto", "Lisboa", company1);
        trainService1 = new TrainService(1, train1,
            LocalDateTime.of(2025, 2, 1, 8, 0),
            LocalDateTime.of(2025, 2, 1, 16, 0),
            owner1, "Scheduled");

        FacilityType facilityType1 = new FacilityType(1, "Station");
        facility1 = new Facility(1, "Porto Station", facilityType1, 41.1579, -8.6291, true);

        Line line1 = new Line(1, owner1);
        parkingTrack1 = new ParkingTrack(1, line1, facility1, "Track 1", 20);

        trainStop1 = new TrainStop(1, trainService1, facility1, parkingTrack1, 1, LocalDateTime.of(2025, 2, 1, 10, 0), LocalDateTime.of(2025, 2, 1, 10, 15), 15, null, null, null);
        trainStop2 = new TrainStop(2, trainService1, facility1, parkingTrack1, 2, LocalDateTime.of(2025, 2, 1, 12, 0), LocalDateTime.of(2025, 2, 1, 12, 10), 10, null, null, null);
        trainStop3 = new TrainStop(3, trainService1, facility1, parkingTrack1, 3, LocalDateTime.of(2025, 2, 1, 14, 0), LocalDateTime.of(2025, 2, 1, 14, 20), 20, null, null, null);
    }

    @Test
    public void testCreateTrainStop() {
        TrainStop result = controller.createTrainStop(trainStop1);

        assertNotNull(result);
        assertEquals(trainStop1, result);
        assertEquals(1, controller.getAllTrainStops().size());
    }

    @Test
    public void testCreateTrainStopWithNull() {
        TrainStop result = controller.createTrainStop(null);

        assertNull(result);
        assertEquals(0, controller.getAllTrainStops().size());
    }

    @Test
    public void testGetAllTrainStops() {
        controller.createTrainStop(trainStop1);
        controller.createTrainStop(trainStop2);
        controller.createTrainStop(trainStop3);

        List<TrainStop> trainStops = controller.getAllTrainStops();

        assertEquals(3, trainStops.size());
        assertTrue(trainStops.contains(trainStop1));
        assertTrue(trainStops.contains(trainStop2));
        assertTrue(trainStops.contains(trainStop3));
    }

    @Test
    public void testGetAllTrainStopsEmpty() {
        List<TrainStop> trainStops = controller.getAllTrainStops();

        assertNotNull(trainStops);
        assertEquals(0, trainStops.size());
    }

    @Test
    public void testGetTrainStopByIdFound() {
        controller.createTrainStop(trainStop1);
        controller.createTrainStop(trainStop2);

        Optional<TrainStop> result = controller.getTrainStopById(1);

        assertTrue(result.isPresent());
        assertEquals(trainStop1, result.get());
    }

    @Test
    public void testGetTrainStopByIdNotFound() {
        controller.createTrainStop(trainStop1);

        Optional<TrainStop> result = controller.getTrainStopById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateTrainStopSuccess() {
        controller.createTrainStop(trainStop1);

        TrainStop updatedTrainStop = new TrainStop(1, trainService1, facility1, parkingTrack1, 1, LocalDateTime.of(2025, 2, 1, 10, 0), LocalDateTime.of(2025, 2, 1, 10, 20), 20, null, null, null);

        boolean result = controller.updateTrainStop(1, updatedTrainStop);

        assertTrue(result);
        Optional<TrainStop> retrievedTrainStop = controller.getTrainStopById(1);
        assertTrue(retrievedTrainStop.isPresent());
        assertEquals(20, retrievedTrainStop.get().getPlannedStopTime().intValue());
    }

    @Test
    public void testUpdateTrainStopNotFound() {
        controller.createTrainStop(trainStop1);

        boolean result = controller.updateTrainStop(999, trainStop2);

        assertFalse(result);
    }

    @Test
    public void testDeleteTrainStopSuccess() {
        controller.createTrainStop(trainStop1);
        controller.createTrainStop(trainStop2);
        controller.createTrainStop(trainStop3);

        boolean result = controller.deleteTrainStop(2);

        assertTrue(result);
        assertEquals(2, controller.getAllTrainStops().size());
        assertFalse(controller.getTrainStopById(2).isPresent());
    }

    @Test
    public void testDeleteTrainStopNotFound() {
        controller.createTrainStop(trainStop1);

        boolean result = controller.deleteTrainStop(999);

        assertFalse(result);
        assertEquals(1, controller.getAllTrainStops().size());
    }

    @Test
    public void testGetNextTrainStopId() {
        Integer nextId = controller.getNextTrainStopId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllTrainStops() {
        controller.createTrainStop(trainStop1);
        controller.createTrainStop(trainStop2);
        controller.createTrainStop(trainStop3);
        assertEquals(3, controller.getAllTrainStops().size());

        controller.clearAllTrainStops();

        assertEquals(0, controller.getAllTrainStops().size());
    }

    @Test
    public void testConstructor() {
        TrainStopController newController = new TrainStopController();

        assertNotNull(newController);
        assertNotNull(newController.getAllTrainStops());
    }
}

