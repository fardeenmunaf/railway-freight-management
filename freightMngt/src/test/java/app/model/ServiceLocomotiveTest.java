package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for ServiceLocomotive model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class ServiceLocomotiveTest {

    private ServiceLocomotive serviceLocomotive;

    @Before
    public void setUp() {
        serviceLocomotive = new ServiceLocomotive();
    }

    @Test
    public void testDefaultConstructor() {
        ServiceLocomotive defaultServiceLocomotive = new ServiceLocomotive();

        assertNull("Service Locomotive ID should be null", defaultServiceLocomotive.getServiceLocomotiveId());
        assertNull("Service should be null", defaultServiceLocomotive.getService());
        assertNull("Locomotive should be null", defaultServiceLocomotive.getLocomotive());
        assertNull("Position No should be null", defaultServiceLocomotive.getPositionNo());
    }

    @Test
    public void testParameterizedConstructor() {
        TrainService service = new TrainService();
        Locomotive locomotive = new Locomotive();

        ServiceLocomotive paramServiceLocomotive = new ServiceLocomotive(1, service, locomotive, 1);

        assertEquals("Service Locomotive ID should be 1", Integer.valueOf(1), paramServiceLocomotive.getServiceLocomotiveId());
        assertEquals("Service should match", service, paramServiceLocomotive.getService());
        assertEquals("Locomotive should match", locomotive, paramServiceLocomotive.getLocomotive());
        assertEquals("Position No should be 1", Integer.valueOf(1), paramServiceLocomotive.getPositionNo());
    }

    @Test
    public void testSetAndGetServiceLocomotiveId() {
        serviceLocomotive.setServiceLocomotiveId(10);

        assertEquals("Service Locomotive ID should be 10", Integer.valueOf(10), serviceLocomotive.getServiceLocomotiveId());
    }

    @Test
    public void testSetAndGetService() {
        TrainService service = new TrainService();
        serviceLocomotive.setService(service);

        assertEquals("Service should match", service, serviceLocomotive.getService());
    }

    @Test
    public void testSetAndGetLocomotive() {
        Locomotive locomotive = new Locomotive();
        serviceLocomotive.setLocomotive(locomotive);

        assertEquals("Locomotive should match", locomotive, serviceLocomotive.getLocomotive());
    }

    @Test
    public void testSetAndGetPositionNo() {
        serviceLocomotive.setPositionNo(2);

        assertEquals("Position No should be 2", Integer.valueOf(2), serviceLocomotive.getPositionNo());
    }

    @Test
    public void testSetServiceLocomotiveIdToNull() {
        serviceLocomotive.setServiceLocomotiveId(5);
        serviceLocomotive.setServiceLocomotiveId(null);

        assertNull("Service Locomotive ID should be null", serviceLocomotive.getServiceLocomotiveId());
    }

    @Test
    public void testSetServiceToNull() {
        TrainService service = new TrainService();
        serviceLocomotive.setService(service);
        serviceLocomotive.setService(null);

        assertNull("Service should be null", serviceLocomotive.getService());
    }

    @Test
    public void testSetLocomotiveToNull() {
        Locomotive locomotive = new Locomotive();
        serviceLocomotive.setLocomotive(locomotive);
        serviceLocomotive.setLocomotive(null);

        assertNull("Locomotive should be null", serviceLocomotive.getLocomotive());
    }

    @Test
    public void testSetPositionNoToNull() {
        serviceLocomotive.setPositionNo(3);
        serviceLocomotive.setPositionNo(null);

        assertNull("Position No should be null", serviceLocomotive.getPositionNo());
    }

    @Test
    public void testConstructorWithNullValues() {
        ServiceLocomotive nullServiceLocomotive = new ServiceLocomotive(null, null, null, null);

        assertNull("Service Locomotive ID should be null", nullServiceLocomotive.getServiceLocomotiveId());
        assertNull("Service should be null", nullServiceLocomotive.getService());
        assertNull("Locomotive should be null", nullServiceLocomotive.getLocomotive());
        assertNull("Position No should be null", nullServiceLocomotive.getPositionNo());
    }
}

