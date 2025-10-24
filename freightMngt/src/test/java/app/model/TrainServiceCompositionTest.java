package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for TrainServiceComposition model.
 * Tests constructors, getters, and setters with 100% coverage.
 */
public class TrainServiceCompositionTest {

    private TrainServiceComposition composition;

    @Before
    public void setUp() {
        composition = new TrainServiceComposition();
    }

    @Test
    public void testDefaultConstructor() {
        TrainServiceComposition defaultComposition = new TrainServiceComposition();

        assertNull("TrainService should be null", defaultComposition.getTrainService());
        assertNull("ServiceLocomotive should be null", defaultComposition.getServiceLocomotive());
        assertNull("Wagon should be null", defaultComposition.getWagon());
    }

    @Test
    public void testParameterizedConstructor() {
        TrainService trainService = new TrainService();
        ServiceLocomotive serviceLocomotive = new ServiceLocomotive();
        Wagon wagon = new Wagon();

        TrainServiceComposition paramComposition = new TrainServiceComposition(trainService, serviceLocomotive, wagon);

        assertEquals("TrainService should match", trainService, paramComposition.getTrainService());
        assertEquals("ServiceLocomotive should match", serviceLocomotive, paramComposition.getServiceLocomotive());
        assertEquals("Wagon should match", wagon, paramComposition.getWagon());
    }

    @Test
    public void testSetAndGetTrainService() {
        TrainService trainService = new TrainService();
        composition.setTrainService(trainService);

        assertEquals("TrainService should match", trainService, composition.getTrainService());
    }

    @Test
    public void testSetAndGetServiceLocomotive() {
        ServiceLocomotive serviceLocomotive = new ServiceLocomotive();
        composition.setServiceLocomotive(serviceLocomotive);

        assertEquals("ServiceLocomotive should match", serviceLocomotive, composition.getServiceLocomotive());
    }

    @Test
    public void testSetAndGetWagon() {
        Wagon wagon = new Wagon();
        composition.setWagon(wagon);

        assertEquals("Wagon should match", wagon, composition.getWagon());
    }

    @Test
    public void testSetTrainServiceToNull() {
        TrainService trainService = new TrainService();
        composition.setTrainService(trainService);
        composition.setTrainService(null);

        assertNull("TrainService should be null", composition.getTrainService());
    }

    @Test
    public void testSetServiceLocomotiveToNull() {
        ServiceLocomotive serviceLocomotive = new ServiceLocomotive();
        composition.setServiceLocomotive(serviceLocomotive);
        composition.setServiceLocomotive(null);

        assertNull("ServiceLocomotive should be null", composition.getServiceLocomotive());
    }

    @Test
    public void testSetWagonToNull() {
        Wagon wagon = new Wagon();
        composition.setWagon(wagon);
        composition.setWagon(null);

        assertNull("Wagon should be null", composition.getWagon());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        TrainService trainService = new TrainService();
        ServiceLocomotive serviceLocomotive = new ServiceLocomotive();
        Wagon wagon = new Wagon();

        composition.setTrainService(trainService);
        composition.setServiceLocomotive(serviceLocomotive);
        composition.setWagon(wagon);

        assertEquals("TrainService should match", trainService, composition.getTrainService());
        assertEquals("ServiceLocomotive should match", serviceLocomotive, composition.getServiceLocomotive());
        assertEquals("Wagon should match", wagon, composition.getWagon());
    }

    @Test
    public void testConstructorWithNullValues() {
        TrainServiceComposition nullComposition = new TrainServiceComposition(null, null, null);

        assertNull("TrainService should be null", nullComposition.getTrainService());
        assertNull("ServiceLocomotive should be null", nullComposition.getServiceLocomotive());
        assertNull("Wagon should be null", nullComposition.getWagon());
    }
}

