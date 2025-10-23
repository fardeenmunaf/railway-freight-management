package app.controller;

import app.model.LocomotiveEletric;
import app.model.Locomotive;
import app.model.User;
import app.model.Company;
import app.model.LocomotiveType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LocomotiveEletricControllerTest {

    private LocomotiveEletricController controller;
    private LocomotiveEletric locomotiveEletric1;
    private LocomotiveEletric locomotiveEletric2;
    private LocomotiveEletric locomotiveEletric3;
    private Locomotive locomotive1;

    @Before
    public void setUp() {
        Repositories.getInstance().getLocomotiveEletricRepository().clear();

        controller = new LocomotiveEletricController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        LocomotiveType type1 = new LocomotiveType(1, "Freight");

        Dimension dim1 = new Dimension(15, 3);

        locomotive1 = new Locomotive(1, operator1, type1, "Siemens", "Vectron", 2020, 5600, 1.2, dim1, 160.0, 120.0, 22000.0, 87000.0, 4, "Y25", "350");

        locomotiveEletric1 = new LocomotiveEletric(locomotive1, 2000.0);
        locomotiveEletric1.setLocomotiveId(1);

        locomotiveEletric2 = new LocomotiveEletric(locomotive1, 2500.0);
        locomotiveEletric2.setLocomotiveId(2);

        locomotiveEletric3 = new LocomotiveEletric(locomotive1, 3000.0);
        locomotiveEletric3.setLocomotiveId(3);
    }

    @Test
    public void testCreateLocomotiveEletric() {
        LocomotiveEletric result = controller.createLocomotiveEletric(locomotiveEletric1);

        assertNotNull(result);
        assertEquals(locomotiveEletric1, result);
        assertEquals(1, controller.getAllLocomotiveEletrics().size());
    }

    @Test
    public void testCreateLocomotiveEletricWithNull() {
        LocomotiveEletric result = controller.createLocomotiveEletric(null);

        assertNull(result);
        assertEquals(0, controller.getAllLocomotiveEletrics().size());
    }

    @Test
    public void testGetAllLocomotiveEletrics() {
        controller.createLocomotiveEletric(locomotiveEletric1);
        controller.createLocomotiveEletric(locomotiveEletric2);
        controller.createLocomotiveEletric(locomotiveEletric3);

        List<LocomotiveEletric> locomotives = controller.getAllLocomotiveEletrics();

        assertEquals(3, locomotives.size());
        assertTrue(locomotives.contains(locomotiveEletric1));
        assertTrue(locomotives.contains(locomotiveEletric2));
        assertTrue(locomotives.contains(locomotiveEletric3));
    }

    @Test
    public void testGetAllLocomotiveEletricsEmpty() {
        List<LocomotiveEletric> locomotives = controller.getAllLocomotiveEletrics();

        assertNotNull(locomotives);
        assertEquals(0, locomotives.size());
    }

    @Test
    public void testGetLocomotiveEletricByIdFound() {
        controller.createLocomotiveEletric(locomotiveEletric1);
        controller.createLocomotiveEletric(locomotiveEletric2);

        Optional<LocomotiveEletric> result = controller.getLocomotiveEletricById(1);

        assertTrue(result.isPresent());
        assertEquals(locomotiveEletric1, result.get());
    }

    @Test
    public void testGetLocomotiveEletricByIdNotFound() {
        controller.createLocomotiveEletric(locomotiveEletric1);

        Optional<LocomotiveEletric> result = controller.getLocomotiveEletricById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateLocomotiveEletricSuccess() {
        controller.createLocomotiveEletric(locomotiveEletric1);

        LocomotiveEletric updatedLocomotive = new LocomotiveEletric(locomotive1, 2200.0);
        updatedLocomotive.setLocomotiveId(1);

        boolean result = controller.updateLocomotiveEletric(1, updatedLocomotive);

        assertTrue(result);
        Optional<LocomotiveEletric> retrievedLocomotive = controller.getLocomotiveEletricById(1);
        assertTrue(retrievedLocomotive.isPresent());
        assertEquals(2200.0, retrievedLocomotive.get().getFuelCapacity(), 0.01);
    }

    @Test
    public void testUpdateLocomotiveEletricNotFound() {
        controller.createLocomotiveEletric(locomotiveEletric1);

        boolean result = controller.updateLocomotiveEletric(999, locomotiveEletric2);

        assertFalse(result);
    }

    @Test
    public void testDeleteLocomotiveEletricSuccess() {
        controller.createLocomotiveEletric(locomotiveEletric1);
        controller.createLocomotiveEletric(locomotiveEletric2);
        controller.createLocomotiveEletric(locomotiveEletric3);

        boolean result = controller.deleteLocomotiveEletric(2);

        assertTrue(result);
        assertEquals(2, controller.getAllLocomotiveEletrics().size());
        assertFalse(controller.getLocomotiveEletricById(2).isPresent());
    }

    @Test
    public void testDeleteLocomotiveEletricNotFound() {
        controller.createLocomotiveEletric(locomotiveEletric1);

        boolean result = controller.deleteLocomotiveEletric(999);

        assertFalse(result);
        assertEquals(1, controller.getAllLocomotiveEletrics().size());
    }

    @Test
    public void testGetNextLocomotiveEletricId() {
        Integer nextId = controller.getNextLocomotiveEletricId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllLocomotiveEletrics() {
        controller.createLocomotiveEletric(locomotiveEletric1);
        controller.createLocomotiveEletric(locomotiveEletric2);
        controller.createLocomotiveEletric(locomotiveEletric3);
        assertEquals(3, controller.getAllLocomotiveEletrics().size());

        controller.clearAllLocomotiveEletrics();

        assertEquals(0, controller.getAllLocomotiveEletrics().size());
    }

    @Test
    public void testConstructor() {
        LocomotiveEletricController newController = new LocomotiveEletricController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLocomotiveEletrics());
    }
}
