package app.controller;

import app.model.TrainService;
import app.model.Train;
import app.model.User;
import app.model.Company;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TrainServiceControllerTest {

    private TrainServiceController controller;
    private TrainService trainService1;
    private TrainService trainService2;
    private TrainService trainService3;
    private Train train1;
    private Train train2;
    private User driver1;
    private User driver2;
    private Company company1;

    @Before
    public void setUp() {
        Repositories.getInstance().getTrainServiceRepository().clear();
        controller = new TrainServiceController();

        company1 = new Company(1, "Rail Company", "RC", "123456789");

        train1 = new Train(1, "101", "Porto", "Lisboa", company1);
        train2 = new Train(2, "102", "Lisboa", "Faro", company1);

        driver1 = new User(1, "driver1", "driver1@example.com", "password123", company1, true);
        driver2 = new User(2, "driver2", "driver2@example.com", "password456", company1, true);

        trainService1 = new TrainService(1, train1,
            LocalDateTime.of(2025, 10, 23, 8, 0),
            LocalDateTime.of(2025, 10, 23, 12, 0),
            driver1, "Scheduled");

        trainService2 = new TrainService(2, train2,
            LocalDateTime.of(2025, 10, 24, 9, 0),
            LocalDateTime.of(2025, 10, 24, 14, 0),
            driver2, "In Progress");

        trainService3 = new TrainService(3, train1,
            LocalDateTime.of(2025, 10, 25, 10, 0),
            LocalDateTime.of(2025, 10, 25, 15, 0),
            driver1, "Completed");
    }

    @Test
    public void testCreateTrainService() {
        TrainService result = controller.createTrainService(trainService1);

        assertNotNull(result);
        assertEquals(trainService1, result);
        assertEquals(1, controller.getAllTrainServices().size());
    }

    @Test
    public void testCreateTrainServiceWithNull() {
        TrainService result = controller.createTrainService(null);

        assertNull(result);
        assertEquals(0, controller.getAllTrainServices().size());
    }

    @Test
    public void testGetAllTrainServices() {
        controller.createTrainService(trainService1);
        controller.createTrainService(trainService2);
        controller.createTrainService(trainService3);

        List<TrainService> trainServices = controller.getAllTrainServices();

        assertEquals(3, trainServices.size());
        assertTrue(trainServices.contains(trainService1));
        assertTrue(trainServices.contains(trainService2));
        assertTrue(trainServices.contains(trainService3));
    }

    @Test
    public void testGetAllTrainServicesEmpty() {
        List<TrainService> trainServices = controller.getAllTrainServices();

        assertNotNull(trainServices);
        assertEquals(0, trainServices.size());
    }

    @Test
    public void testGetTrainServiceByIdFound() {
        controller.createTrainService(trainService1);
        controller.createTrainService(trainService2);

        Optional<TrainService> result = controller.getTrainServiceById(1);

        assertTrue(result.isPresent());
        assertEquals(trainService1, result.get());
    }

    @Test
    public void testGetTrainServiceByIdNotFound() {
        controller.createTrainService(trainService1);

        Optional<TrainService> result = controller.getTrainServiceById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateTrainServiceSuccess() {
        controller.createTrainService(trainService1);

        TrainService updatedTrainService = new TrainService(1, train1,
            LocalDateTime.of(2025, 10, 23, 8, 0),
            LocalDateTime.of(2025, 10, 23, 12, 0),
            driver1, "Completed");

        boolean result = controller.updateTrainService(1, updatedTrainService);

        assertTrue(result);
        Optional<TrainService> retrievedService = controller.getTrainServiceById(1);
        assertTrue(retrievedService.isPresent());
        assertEquals("Completed", retrievedService.get().getStatus());
    }

    @Test
    public void testUpdateTrainServiceNotFound() {
        controller.createTrainService(trainService1);

        boolean result = controller.updateTrainService(999, trainService2);

        assertFalse(result);
    }

    @Test
    public void testDeleteTrainServiceSuccess() {
        controller.createTrainService(trainService1);
        controller.createTrainService(trainService2);
        controller.createTrainService(trainService3);

        boolean result = controller.deleteTrainService(2);

        assertTrue(result);
        assertEquals(2, controller.getAllTrainServices().size());
        assertFalse(controller.getTrainServiceById(2).isPresent());
    }

    @Test
    public void testDeleteTrainServiceNotFound() {
        controller.createTrainService(trainService1);

        boolean result = controller.deleteTrainService(999);

        assertFalse(result);
        assertEquals(1, controller.getAllTrainServices().size());
    }

    @Test
    public void testGetNextTrainServiceId() {
        Integer nextId = controller.getNextTrainServiceId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllTrainServices() {
        controller.createTrainService(trainService1);
        controller.createTrainService(trainService2);
        controller.createTrainService(trainService3);
        assertEquals(3, controller.getAllTrainServices().size());

        controller.clearAllTrainServices();

        assertEquals(0, controller.getAllTrainServices().size());
    }

    @Test
    public void testConstructor() {
        TrainServiceController newController = new TrainServiceController();

        assertNotNull(newController);
        assertNotNull(newController.getAllTrainServices());
    }
}

