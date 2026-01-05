package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Train model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class TrainTest {

    private Train train;

    @Before
    public void setUp() {
        train = new Train();
    }

    @Test
    public void testDefaultConstructor() {
        Train defaultTrain = new Train();

        assertNull("Train ID should be null", defaultTrain.getTrainId());
        assertNull("Code should be null", defaultTrain.getCode());
        assertNull("Departure should be null", defaultTrain.getDeparture());
        assertNull("Destination should be null", defaultTrain.getDestination());
        assertNull("Operator should be null", defaultTrain.getOperator());
    }

    @Test
    public void testParameterizedConstructor() {
        Company operator = new Company();
        Train paramTrain = new Train(1, "TRN-101", "Porto", "Lisboa", operator);

        assertEquals("Train ID should be 1", Integer.valueOf(1), paramTrain.getTrainId());
        assertEquals("Code should be 'TRN-101'", "TRN-101", paramTrain.getCode());
        assertEquals("Departure should be 'Porto'", "Porto", paramTrain.getDeparture());
        assertEquals("Destination should be 'Lisboa'", "Lisboa", paramTrain.getDestination());
        assertEquals("Operator should match", operator, paramTrain.getOperator());
    }

    @Test
    public void testSetAndGetTrainId() {
        train.setTrainId(10);

        assertEquals("Train ID should be 10", Integer.valueOf(10), train.getTrainId());
    }

    @Test
    public void testSetAndGetCode() {
        train.setCode("TRN-202");

        assertEquals("Code should be 'TRN-202'", "TRN-202", train.getCode());
    }

    @Test
    public void testSetAndGetDeparture() {
        train.setDeparture("Braga");

        assertEquals("Departure should be 'Braga'", "Braga", train.getDeparture());
    }

    @Test
    public void testSetAndGetDestination() {
        train.setDestination("Faro");

        assertEquals("Destination should be 'Faro'", "Faro", train.getDestination());
    }

    @Test
    public void testSetAndGetOperator() {
        Company operator = new Company();
        train.setOperatorId(operator);

        assertEquals("Operator should match", operator, train.getOperator());
    }

    @Test
    public void testSetTrainIdToNull() {
        train.setTrainId(5);
        train.setTrainId(null);

        assertNull("Train ID should be null", train.getTrainId());
    }

    @Test
    public void testSetCodeToNull() {
        train.setCode("TRN-303");
        train.setCode(null);

        assertNull("Code should be null", train.getCode());
    }

    @Test
    public void testSetDepartureToNull() {
        train.setDeparture("Coimbra");
        train.setDeparture(null);

        assertNull("Departure should be null", train.getDeparture());
    }

    @Test
    public void testSetDestinationToNull() {
        train.setDestination("Aveiro");
        train.setDestination(null);

        assertNull("Destination should be null", train.getDestination());
    }

    @Test
    public void testSetOperatorToNull() {
        Company operator = new Company();
        train.setOperatorId(operator);
        train.setOperatorId(null);

        assertNull("Operator should be null", train.getOperator());
    }

    @Test
    public void testConstructorWithNullValues() {
        Train nullTrain = new Train(null, null, null, null, null);

        assertNull("Train ID should be null", nullTrain.getTrainId());
        assertNull("Code should be null", nullTrain.getCode());
        assertNull("Departure should be null", nullTrain.getDeparture());
        assertNull("Destination should be null", nullTrain.getDestination());
        assertNull("Operator should be null", nullTrain.getOperator());
    }
}

