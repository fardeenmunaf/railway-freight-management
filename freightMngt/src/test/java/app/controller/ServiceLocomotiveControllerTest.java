package app.controller;

import app.model.ServiceLocomotive;
import app.model.TrainService;
import app.model.Locomotive;
import app.model.User;
import app.model.Company;
import app.model.LocomotiveType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceLocomotiveControllerTest {

    private ServiceLocomotiveController controller;
    private ServiceLocomotive serviceLoco1;
    private ServiceLocomotive serviceLoco2;
    private ServiceLocomotive serviceLoco3;
    private TrainService trainService1;
    private Locomotive locomotive1;
    private Locomotive locomotive2;
    private Locomotive locomotive3;

    @Before
    public void setUp() {
        Repositories.getInstance().getServiceLocomotiveRepository().clear();
        controller = new ServiceLocomotiveController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        app.model.Train train1 = new app.model.Train(1, "101", "Porto", "Lisboa", company1);
        trainService1 = new TrainService(1, train1,
            java.time.LocalDateTime.of(2025, 10, 23, 8, 0),
            java.time.LocalDateTime.of(2025, 10, 23, 12, 0),
            operator1, "Scheduled");

        LocomotiveType type1 = new LocomotiveType(1, "Freight");
        Dimension dim1 = new Dimension(15, 3);
        locomotive1 = new Locomotive(1, operator1, type1, "Siemens", "Vectron", 2020, 5600, 1.2, dim1, 160.0, 120.0, 22000.0, 87000.0, 4, "Y25", "350");
        locomotive2 = new Locomotive(2, operator1, type1, "Alstom", "Prima", 2019, 5200, 1.1, dim1, 150.0, 110.0, 21000.0, 85000.0, 4, "Y25", "330");
        locomotive3 = new Locomotive(3, operator1, type1, "Bombardier", "Traxx", 2021, 5800, 1.3, dim1, 170.0, 130.0, 23000.0, 88000.0, 4, "Y25", "360");

        serviceLoco1 = new ServiceLocomotive(1, trainService1, locomotive1, 1);
        serviceLoco2 = new ServiceLocomotive(2, trainService1, locomotive2, 2);
        serviceLoco3 = new ServiceLocomotive(3, trainService1, locomotive3, 3);
    }

    @Test
    public void testCreateServiceLocomotive() {
        ServiceLocomotive result = controller.createServiceLocomotive(serviceLoco1);

        assertNotNull(result);
        assertEquals(serviceLoco1, result);
        assertEquals(1, controller.getAllServiceLocomotives().size());
    }

    @Test
    public void testCreateServiceLocomotiveWithNull() {
        ServiceLocomotive result = controller.createServiceLocomotive(null);

        assertNull(result);
        assertEquals(0, controller.getAllServiceLocomotives().size());
    }

    @Test
    public void testGetAllServiceLocomotives() {
        controller.createServiceLocomotive(serviceLoco1);
        controller.createServiceLocomotive(serviceLoco2);
        controller.createServiceLocomotive(serviceLoco3);

        List<ServiceLocomotive> serviceLocos = controller.getAllServiceLocomotives();

        assertEquals(3, serviceLocos.size());
        assertTrue(serviceLocos.contains(serviceLoco1));
        assertTrue(serviceLocos.contains(serviceLoco2));
        assertTrue(serviceLocos.contains(serviceLoco3));
    }

    @Test
    public void testGetAllServiceLocomotivesEmpty() {
        List<ServiceLocomotive> serviceLocos = controller.getAllServiceLocomotives();

        assertNotNull(serviceLocos);
        assertEquals(0, serviceLocos.size());
    }

    @Test
    public void testGetServiceLocomotivesByTrainService() {
        controller.createServiceLocomotive(serviceLoco1);
        controller.createServiceLocomotive(serviceLoco2);
        controller.createServiceLocomotive(serviceLoco3);

        List<ServiceLocomotive> result = controller.getServiceLocomotivesByTrainService(1);

        assertEquals(3, result.size());
        assertTrue(result.contains(serviceLoco1));
        assertTrue(result.contains(serviceLoco2));
        assertTrue(result.contains(serviceLoco3));
    }

    @Test
    public void testGetServiceLocomotivesByTrainServiceNotFound() {
        controller.createServiceLocomotive(serviceLoco1);

        List<ServiceLocomotive> result = controller.getServiceLocomotivesByTrainService(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteServiceLocomotiveSuccess() {
        controller.createServiceLocomotive(serviceLoco1);
        controller.createServiceLocomotive(serviceLoco2);
        controller.createServiceLocomotive(serviceLoco3);

        boolean result = controller.deleteServiceLocomotive(1, 1);

        assertTrue(result);
        assertEquals(2, controller.getAllServiceLocomotives().size());
    }

    @Test
    public void testDeleteServiceLocomotiveNotFound() {
        controller.createServiceLocomotive(serviceLoco1);

        boolean result = controller.deleteServiceLocomotive(999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllServiceLocomotives().size());
    }

    @Test
    public void testClearAllServiceLocomotives() {
        controller.createServiceLocomotive(serviceLoco1);
        controller.createServiceLocomotive(serviceLoco2);
        controller.createServiceLocomotive(serviceLoco3);
        assertEquals(3, controller.getAllServiceLocomotives().size());

        controller.clearAllServiceLocomotives();

        assertEquals(0, controller.getAllServiceLocomotives().size());
    }

    @Test
    public void testConstructor() {
        ServiceLocomotiveController newController = new ServiceLocomotiveController();

        assertNotNull(newController);
        assertNotNull(newController.getAllServiceLocomotives());
    }
}

