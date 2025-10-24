package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for TrainService model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class TrainServiceTest {

    private TrainService trainService;

    @Before
    public void setUp() {
        trainService = new TrainService();
    }

    @Test
    public void testDefaultConstructor() {
        TrainService defaultService = new TrainService();

        assertNull("Service ID should be null", defaultService.getServiceId());
        assertNull("Train should be null", defaultService.getTrain());
        assertNull("Departure Date should be null", defaultService.getDepartureDate());
        assertNull("Arrival Date should be null", defaultService.getArrivalDate());
        assertNull("Train Driver should be null", defaultService.getTrainDriver());
        assertNull("Status should be null", defaultService.getStatus());
    }

    @Test
    public void testParameterizedConstructor() {
        Train train = new Train();
        User trainDriver = new User();
        LocalDateTime departureDate = LocalDateTime.of(2025, 10, 24, 10, 0);
        LocalDateTime arrivalDate = LocalDateTime.of(2025, 10, 24, 18, 0);

        TrainService paramService = new TrainService(1, train, departureDate, arrivalDate, trainDriver, "Active");

        assertEquals("Service ID should be 1", Integer.valueOf(1), paramService.getServiceId());
        assertEquals("Train should match", train, paramService.getTrain());
        assertEquals("Departure Date should match", departureDate, paramService.getDepartureDate());
        assertEquals("Arrival Date should match", arrivalDate, paramService.getArrivalDate());
        assertEquals("Train Driver should match", trainDriver, paramService.getTrainDriver());
        assertEquals("Status should be 'Active'", "Active", paramService.getStatus());
    }

    @Test
    public void testSetAndGetServiceId() {
        trainService.setServiceId(10);

        assertEquals("Service ID should be 10", Integer.valueOf(10), trainService.getServiceId());
    }

    @Test
    public void testSetAndGetTrain() {
        Train train = new Train();
        trainService.setTrain(train);

        assertEquals("Train should match", train, trainService.getTrain());
    }

    @Test
    public void testSetAndGetDepartureDate() {
        LocalDateTime departureDate = LocalDateTime.of(2025, 11, 1, 9, 30);
        trainService.setDepartureDate(departureDate);

        assertEquals("Departure Date should match", departureDate, trainService.getDepartureDate());
    }

    @Test
    public void testSetAndGetArrivalDate() {
        LocalDateTime arrivalDate = LocalDateTime.of(2025, 11, 1, 17, 0);
        trainService.setArrivalDate(arrivalDate);

        assertEquals("Arrival Date should match", arrivalDate, trainService.getArrivalDate());
    }

    @Test
    public void testSetAndGetTrainDriver() {
        User trainDriver = new User();
        trainService.setTrainDriver(trainDriver);

        assertEquals("Train Driver should match", trainDriver, trainService.getTrainDriver());
    }

    @Test
    public void testSetAndGetStatus() {
        trainService.setStatus("Completed");

        assertEquals("Status should be 'Completed'", "Completed", trainService.getStatus());
    }

    @Test
    public void testSetServiceIdToNull() {
        trainService.setServiceId(5);
        trainService.setServiceId(null);

        assertNull("Service ID should be null", trainService.getServiceId());
    }

    @Test
    public void testSetTrainToNull() {
        Train train = new Train();
        trainService.setTrain(train);
        trainService.setTrain(null);

        assertNull("Train should be null", trainService.getTrain());
    }

    @Test
    public void testSetDepartureDateToNull() {
        LocalDateTime departureDate = LocalDateTime.now();
        trainService.setDepartureDate(departureDate);
        trainService.setDepartureDate(null);

        assertNull("Departure Date should be null", trainService.getDepartureDate());
    }

    @Test
    public void testSetArrivalDateToNull() {
        LocalDateTime arrivalDate = LocalDateTime.now();
        trainService.setArrivalDate(arrivalDate);
        trainService.setArrivalDate(null);

        assertNull("Arrival Date should be null", trainService.getArrivalDate());
    }

    @Test
    public void testSetTrainDriverToNull() {
        User trainDriver = new User();
        trainService.setTrainDriver(trainDriver);
        trainService.setTrainDriver(null);

        assertNull("Train Driver should be null", trainService.getTrainDriver());
    }

    @Test
    public void testSetStatusToNull() {
        trainService.setStatus("In Transit");
        trainService.setStatus(null);

        assertNull("Status should be null", trainService.getStatus());
    }

    @Test
    public void testConstructorWithNullValues() {
        TrainService nullService = new TrainService(null, null, null, null, null, null);

        assertNull("Service ID should be null", nullService.getServiceId());
        assertNull("Train should be null", nullService.getTrain());
        assertNull("Departure Date should be null", nullService.getDepartureDate());
        assertNull("Arrival Date should be null", nullService.getArrivalDate());
        assertNull("Train Driver should be null", nullService.getTrainDriver());
        assertNull("Status should be null", nullService.getStatus());
    }
}

