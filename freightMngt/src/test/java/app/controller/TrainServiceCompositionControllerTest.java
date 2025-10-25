package app.controller;

import app.model.*;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class TrainServiceCompositionControllerTest {

    private TrainServiceCompositionController controller;
    private TrainServiceComposition composition1;
    private TrainServiceComposition composition2;
    private TrainServiceComposition composition3;
    private TrainService trainService1;
    private TrainService trainService2;
    private ServiceLocomotive serviceLocomotive1;
    private ServiceLocomotive serviceLocomotive2;
    private Wagon wagon1;
    private Wagon wagon2;
    private Locomotive locomotive1;
    private Locomotive locomotive2;
    private WagonModel wagonModel1;
    private WagonModel wagonModel2;
    private Train train1;
    private User operator1;
    private Company company1;
    private LocomotiveType locomotiveType1;
    private WagonType wagonType1;

    @Before
    public void setUp() {
        Repositories.getInstance().getTrainServiceCompositionRepository().clear();
        controller = new TrainServiceCompositionController();

        // Create company
        company1 = new Company(1, "Rail Company", "RC", "123456789");

        // Create operator/user
        operator1 = new User(1, "operator1", "operator1@example.com", "password123", company1, true);

        // Create train
        train1 = new Train(1, 101, "Porto", "Lisboa", company1);

        // Create train services
        trainService1 = new TrainService(1, train1,
                LocalDateTime.of(2025, 10, 23, 8, 0),
                LocalDateTime.of(2025, 10, 23, 12, 0),
                operator1, "Scheduled");

        trainService2 = new TrainService(2, train1,
                LocalDateTime.of(2025, 10, 24, 9, 0),
                LocalDateTime.of(2025, 10, 24, 14, 0),
                operator1, "In Progress");

        // Create locomotive type
        locomotiveType1 = new LocomotiveType(1, "Electric");

        // Create locomotives
        Dimension dim1 = new Dimension(20000, 3000);
        locomotive1 = new Locomotive(1, operator1, locomotiveType1, "Siemens", "Vectron",
                2020, 6400, 0.8, dim1, 200.0, 160.0,
                90000.0, 87000.0, 4, "Bo'Bo'", "300");

        locomotive2 = new Locomotive(2, operator1, locomotiveType1, "Alstom", "Prima",
                2019, 5600, 0.7, dim1, 180.0, 150.0,
                85000.0, 82000.0, 4, "Bo'Bo'", "280");

        // Create service locomotives
        serviceLocomotive1 = new ServiceLocomotive(1, trainService1, locomotive1, 1);
        serviceLocomotive2 = new ServiceLocomotive(2, trainService2, locomotive2, 1);

        // Create wagon type
        wagonType1 = new WagonType(1, "Freight");

        // Create wagon models
        Dimensions wagonDim1 = new Dimensions(15.0, 2.8, 3.5);
        wagonModel1 = new WagonModel(1, "FreightWagon-2000", wagonType1, "Bombardier",
                "Y25", 2, 18000.0, 60000.0, 120.0, wagonDim1);

        wagonModel2 = new WagonModel(2, "FreightWagon-3000", wagonType1, "CAF",
                "Y25", 2, 20000.0, 70000.0, 120.0, wagonDim1);

        // Create wagons
        wagon1 = new Wagon(1, wagonModel1, operator1, 2018);
        wagon2 = new Wagon(2, wagonModel2, operator1, 2019);

        // Create compositions
        composition1 = new TrainServiceComposition(trainService1, serviceLocomotive1, wagon1);
        composition2 = new TrainServiceComposition(trainService1, serviceLocomotive1, wagon2);
        composition3 = new TrainServiceComposition(trainService2, serviceLocomotive2, wagon1);
    }

    @Test
    public void testCreateTrainServiceComposition() {
        TrainServiceComposition result = controller.createTrainServiceComposition(composition1);

        assertNotNull(result);
        assertEquals(composition1, result);
        assertEquals(1, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testCreateTrainServiceCompositionWithNull() {
        TrainServiceComposition result = controller.createTrainServiceComposition(null);

        assertNull(result);
        assertEquals(0, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testGetAllTrainServiceCompositions() {
        controller.createTrainServiceComposition(composition1);
        controller.createTrainServiceComposition(composition2);
        controller.createTrainServiceComposition(composition3);

        List<TrainServiceComposition> compositions = controller.getAllTrainServiceCompositions();

        assertEquals(3, compositions.size());
        assertTrue(compositions.contains(composition1));
        assertTrue(compositions.contains(composition2));
        assertTrue(compositions.contains(composition3));
    }

    @Test
    public void testGetAllTrainServiceCompositionsEmpty() {
        List<TrainServiceComposition> compositions = controller.getAllTrainServiceCompositions();

        assertNotNull(compositions);
        assertEquals(0, compositions.size());
    }

    @Test
    public void testGetTrainServiceCompositionsByService() {
        controller.createTrainServiceComposition(composition1);
        controller.createTrainServiceComposition(composition2);
        controller.createTrainServiceComposition(composition3);

        List<TrainServiceComposition> compositions = controller.getTrainServiceCompositionsByService(1);

        assertEquals(2, compositions.size());
        assertTrue(compositions.contains(composition1));
        assertTrue(compositions.contains(composition2));
        assertFalse(compositions.contains(composition3));
    }

    @Test
    public void testGetTrainServiceCompositionsByServiceNotFound() {
        controller.createTrainServiceComposition(composition1);

        List<TrainServiceComposition> compositions = controller.getTrainServiceCompositionsByService(999);

        assertNotNull(compositions);
        assertEquals(0, compositions.size());
    }

    @Test
    public void testDeleteTrainServiceCompositionSuccess() {
        controller.createTrainServiceComposition(composition1);
        controller.createTrainServiceComposition(composition2);
        controller.createTrainServiceComposition(composition3);

        boolean result = controller.deleteTrainServiceComposition(1, 1, 1);

        assertTrue(result);
        assertEquals(2, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testDeleteTrainServiceCompositionNotFound() {
        controller.createTrainServiceComposition(composition1);

        boolean result = controller.deleteTrainServiceComposition(999, 999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testDeleteTrainServiceCompositionWithNullLocomotive() {
        controller.createTrainServiceComposition(composition1);

        boolean result = controller.deleteTrainServiceComposition(1, null, 1);

        assertTrue(result);
        assertEquals(0, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testDeleteTrainServiceCompositionWithNullWagon() {
        controller.createTrainServiceComposition(composition1);

        boolean result = controller.deleteTrainServiceComposition(1, 1, null);

        assertTrue(result);
        assertEquals(0, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testClearAllTrainServiceCompositions() {
        controller.createTrainServiceComposition(composition1);
        controller.createTrainServiceComposition(composition2);
        controller.createTrainServiceComposition(composition3);
        assertEquals(3, controller.getAllTrainServiceCompositions().size());

        controller.clearAllTrainServiceCompositions();

        assertEquals(0, controller.getAllTrainServiceCompositions().size());
    }

    @Test
    public void testConstructor() {
        TrainServiceCompositionController newController = new TrainServiceCompositionController();

        assertNotNull(newController);
        assertNotNull(newController.getAllTrainServiceCompositions());
    }

    @Test
    public void testMultipleCompositionsForSameService() {
        controller.createTrainServiceComposition(composition1);
        controller.createTrainServiceComposition(composition2);

        List<TrainServiceComposition> compositions = controller.getTrainServiceCompositionsByService(1);

        assertEquals(2, compositions.size());
        assertEquals(trainService1, compositions.get(0).getTrainService());
        assertEquals(trainService1, compositions.get(1).getTrainService());
    }

    @Test
    public void testDeleteSpecificCompositionFromMultiple() {
        controller.createTrainServiceComposition(composition1);
        controller.createTrainServiceComposition(composition2);
        controller.createTrainServiceComposition(composition3);

        boolean result = controller.deleteTrainServiceComposition(1, 1, 2);

        assertTrue(result);
        List<TrainServiceComposition> remainingCompositions = controller.getTrainServiceCompositionsByService(1);
        assertEquals(1, remainingCompositions.size());
        assertEquals(wagon1, remainingCompositions.get(0).getWagon());
    }
}

