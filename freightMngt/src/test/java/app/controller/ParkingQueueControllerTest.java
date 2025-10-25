package app.controller;

import app.model.ParkingQueue;
import app.model.ParkingTrack;
import app.model.Wagon;
import app.model.WagonModel;
import app.model.WagonType;
import app.model.Line;
import app.model.Facility;
import app.model.User;
import app.model.Company;
import app.model.FacilityType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ParkingQueueControllerTest {

    private ParkingQueueController controller;
    private ParkingQueue queue1;
    private ParkingQueue queue2;
    private ParkingQueue queue3;
    private ParkingTrack track1;
    private Wagon wagon1;
    private Wagon wagon2;

    @Before
    public void setUp() {
        Repositories.getInstance().getParkingQueueRepository().clear();

        controller = new ParkingQueueController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User owner1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        Line line1 = new Line(1, owner1);
        FacilityType facilityType1 = new FacilityType(1, "Yard");
        Facility facility1 = new Facility(1, "Main Yard", facilityType1, 41.1579, -8.6291, true);

        track1 = new ParkingTrack(1, line1, facility1, "Track 1", 20);

        WagonType wagonType1 = new WagonType(1, "Freight");
        app.model.Dimensions dim = new app.model.Dimensions(10.0, 2.5, 3.0);
        WagonModel wagonModel1 = new WagonModel(1, "Model X", wagonType1, "Constructor A", "Y25", 2, 15000.0, 50000.0, 120.0, dim);
        wagon1 = new Wagon(1, wagonModel1, owner1, 2020);
        wagon2 = new Wagon(2, wagonModel1, owner1, 2021);

        queue1 = new ParkingQueue(1, track1, wagon1, 1, "Parked");
        queue2 = new ParkingQueue(2, track1, wagon2, 2, "Parked");
        queue3 = new ParkingQueue(3, track1, wagon1, 3, "Loading");
    }

    @Test
    public void testCreateParkingQueue() {
        ParkingQueue result = controller.createParkingQueue(queue1);

        assertNotNull(result);
        assertEquals(queue1, result);
        assertEquals(1, controller.getAllParkingQueues().size());
    }

    @Test
    public void testCreateParkingQueueWithNull() {
        ParkingQueue result = controller.createParkingQueue(null);

        assertNull(result);
        assertEquals(0, controller.getAllParkingQueues().size());
    }

    @Test
    public void testGetAllParkingQueues() {
        controller.createParkingQueue(queue1);
        controller.createParkingQueue(queue2);
        controller.createParkingQueue(queue3);

        List<ParkingQueue> queues = controller.getAllParkingQueues();

        assertEquals(3, queues.size());
        assertTrue(queues.contains(queue1));
        assertTrue(queues.contains(queue2));
        assertTrue(queues.contains(queue3));
    }

    @Test
    public void testGetAllParkingQueuesEmpty() {
        List<ParkingQueue> queues = controller.getAllParkingQueues();

        assertNotNull(queues);
        assertEquals(0, queues.size());
    }

    @Test
    public void testGetParkingQueueByIdFound() {
        controller.createParkingQueue(queue1);
        controller.createParkingQueue(queue2);

        Optional<ParkingQueue> result = controller.getParkingQueueById(1);

        assertTrue(result.isPresent());
        assertEquals(queue1, result.get());
    }

    @Test
    public void testGetParkingQueueByIdNotFound() {
        controller.createParkingQueue(queue1);

        Optional<ParkingQueue> result = controller.getParkingQueueById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateParkingQueueSuccess() {
        controller.createParkingQueue(queue1);

        ParkingQueue updatedQueue = new ParkingQueue(1, track1, wagon1, 1, "Unloading");

        boolean result = controller.updateParkingQueue(1, updatedQueue);

        assertTrue(result);
        Optional<ParkingQueue> retrievedQueue = controller.getParkingQueueById(1);
        assertTrue(retrievedQueue.isPresent());
        assertEquals("Unloading", retrievedQueue.get().getStatus());
    }

    @Test
    public void testUpdateParkingQueueNotFound() {
        controller.createParkingQueue(queue1);

        boolean result = controller.updateParkingQueue(999, queue2);

        assertFalse(result);
    }

    @Test
    public void testDeleteParkingQueueSuccess() {
        controller.createParkingQueue(queue1);
        controller.createParkingQueue(queue2);
        controller.createParkingQueue(queue3);

        boolean result = controller.deleteParkingQueue(2);

        assertTrue(result);
        assertEquals(2, controller.getAllParkingQueues().size());
        assertFalse(controller.getParkingQueueById(2).isPresent());
    }

    @Test
    public void testDeleteParkingQueueNotFound() {
        controller.createParkingQueue(queue1);

        boolean result = controller.deleteParkingQueue(999);

        assertFalse(result);
        assertEquals(1, controller.getAllParkingQueues().size());
    }

    @Test
    public void testGetNextParkingQueueId() {
        Integer nextId = controller.getNextParkingQueueId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllParkingQueues() {
        controller.createParkingQueue(queue1);
        controller.createParkingQueue(queue2);
        controller.createParkingQueue(queue3);
        assertEquals(3, controller.getAllParkingQueues().size());

        controller.clearAllParkingQueues();

        assertEquals(0, controller.getAllParkingQueues().size());
    }

    @Test
    public void testConstructor() {
        ParkingQueueController newController = new ParkingQueueController();

        assertNotNull(newController);
        assertNotNull(newController.getAllParkingQueues());
    }
}

