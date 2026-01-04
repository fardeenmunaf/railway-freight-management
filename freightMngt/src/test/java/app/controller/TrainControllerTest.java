package app.controller;

import app.model.Train;
import app.model.Company;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TrainControllerTest {

    private TrainController controller;
    private Train train1;
    private Train train2;
    private Train train3;
    private Company company1;

    @Before
    public void setUp() {
        Repositories.getInstance().getTrainRepository().clear();
        controller = new TrainController();

        company1 = new Company(1, "Rail Company", "RC", "123456789");

        train1 = new Train(1, "101", "Porto", "Lisboa", company1);
        train2 = new Train(2, "102", "Lisboa", "Faro", company1);
        train3 = new Train(3, "103", "Braga", "Coimbra", company1);
    }

    @Test
    public void testCreateTrain() {
        Train result = controller.createTrain(train1);

        assertNotNull(result);
        assertEquals(train1, result);
        assertEquals(1, controller.getAllTrains().size());
    }

    @Test
    public void testCreateTrainWithNull() {
        Train result = controller.createTrain(null);

        assertNull(result);
        assertEquals(0, controller.getAllTrains().size());
    }

    @Test
    public void testGetAllTrains() {
        controller.createTrain(train1);
        controller.createTrain(train2);
        controller.createTrain(train3);

        List<Train> trains = controller.getAllTrains();

        assertEquals(3, trains.size());
        assertTrue(trains.contains(train1));
        assertTrue(trains.contains(train2));
        assertTrue(trains.contains(train3));
    }

    @Test
    public void testGetAllTrainsEmpty() {
        List<Train> trains = controller.getAllTrains();

        assertNotNull(trains);
        assertEquals(0, trains.size());
    }

    @Test
    public void testGetTrainByIdFound() {
        controller.createTrain(train1);
        controller.createTrain(train2);

        Optional<Train> result = controller.getTrainById(1);

        assertTrue(result.isPresent());
        assertEquals(train1, result.get());
    }

    @Test
    public void testGetTrainByIdNotFound() {
        controller.createTrain(train1);

        Optional<Train> result = controller.getTrainById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateTrainSuccess() {
        controller.createTrain(train1);

        Train updatedTrain = new Train(1, "101", "Porto", "Aveiro", company1);

        boolean result = controller.updateTrain(1, updatedTrain);

        assertTrue(result);
        Optional<Train> retrievedTrain = controller.getTrainById(1);
        assertTrue(retrievedTrain.isPresent());
        assertEquals("Aveiro", retrievedTrain.get().getDestination());
    }

    @Test
    public void testUpdateTrainNotFound() {
        controller.createTrain(train1);

        boolean result = controller.updateTrain(999, train2);

        assertFalse(result);
    }

    @Test
    public void testDeleteTrainSuccess() {
        controller.createTrain(train1);
        controller.createTrain(train2);
        controller.createTrain(train3);

        boolean result = controller.deleteTrain(2);

        assertTrue(result);
        assertEquals(2, controller.getAllTrains().size());
        assertFalse(controller.getTrainById(2).isPresent());
    }

    @Test
    public void testDeleteTrainNotFound() {
        controller.createTrain(train1);

        boolean result = controller.deleteTrain(999);

        assertFalse(result);
        assertEquals(1, controller.getAllTrains().size());
    }

    @Test
    public void testGetNextTrainId() {
        Integer nextId = controller.getNextTrainId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllTrains() {
        controller.createTrain(train1);
        controller.createTrain(train2);
        controller.createTrain(train3);
        assertEquals(3, controller.getAllTrains().size());

        controller.clearAllTrains();

        assertEquals(0, controller.getAllTrains().size());
    }

    @Test
    public void testConstructor() {
        TrainController newController = new TrainController();

        assertNotNull(newController);
        assertNotNull(newController.getAllTrains());
    }
}

