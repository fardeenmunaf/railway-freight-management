package app.controller;

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

public class LocomotiveControllerTest {

    private LocomotiveController controller;
    private Locomotive locomotive1;
    private Locomotive locomotive2;
    private Locomotive locomotive3;
    private User operator1;
    private LocomotiveType type1;

    @Before
    public void setUp() {
        Repositories.getInstance().getLocomotiveRepository().clear();

        controller = new LocomotiveController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        type1 = new LocomotiveType(1, "Freight");

        Dimension dim1 = new Dimension(15, 3);

        locomotive1 = new Locomotive(1, operator1, type1, "Siemens", "Vectron", 2020, 5600, 1.2, dim1, 160.0, 120.0, 22000.0, 87000.0, 4, "Y25", "350");
        locomotive2 = new Locomotive(2, operator1, type1, "Alstom", "Prima", 2019, 5000, 1.0, dim1, 140.0, 100.0, 20000.0, 85000.0, 4, "Y25", "320");
        locomotive3 = new Locomotive(3, operator1, type1, "Bombardier", "Traxx", 2021, 6400, 1.3, dim1, 180.0, 140.0, 24000.0, 90000.0, 4, "Y25", "380");
    }

    @Test
    public void testCreateLocomotive() {
        Locomotive result = controller.createLocomotive(locomotive1);

        assertNotNull(result);
        assertEquals(locomotive1, result);
        assertEquals(1, controller.getAllLocomotives().size());
    }

    @Test
    public void testCreateLocomotiveWithNull() {
        Locomotive result = controller.createLocomotive(null);

        assertNull(result);
        assertEquals(0, controller.getAllLocomotives().size());
    }

    @Test
    public void testGetAllLocomotives() {
        controller.createLocomotive(locomotive1);
        controller.createLocomotive(locomotive2);
        controller.createLocomotive(locomotive3);

        List<Locomotive> locomotives = controller.getAllLocomotives();

        assertEquals(3, locomotives.size());
        assertTrue(locomotives.contains(locomotive1));
        assertTrue(locomotives.contains(locomotive2));
        assertTrue(locomotives.contains(locomotive3));
    }

    @Test
    public void testGetAllLocomotivesEmpty() {
        List<Locomotive> locomotives = controller.getAllLocomotives();

        assertNotNull(locomotives);
        assertEquals(0, locomotives.size());
    }

    @Test
    public void testGetLocomotiveByIdFound() {
        controller.createLocomotive(locomotive1);
        controller.createLocomotive(locomotive2);

        Optional<Locomotive> result = controller.getLocomotiveById(1);

        assertTrue(result.isPresent());
        assertEquals(locomotive1, result.get());
    }

    @Test
    public void testGetLocomotiveByIdNotFound() {
        controller.createLocomotive(locomotive1);

        Optional<Locomotive> result = controller.getLocomotiveById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateLocomotiveSuccess() {
        controller.createLocomotive(locomotive1);

        Dimension dim1 = new Dimension(15, 3);
        Locomotive updatedLocomotive = new Locomotive(1, operator1, type1, "Siemens", "Vectron MS", 2020, 5600, 1.2, dim1, 160.0, 120.0, 22000.0, 87000.0, 4, "Y25", "350");

        boolean result = controller.updateLocomotive(1, updatedLocomotive);

        assertTrue(result);
        Optional<Locomotive> retrievedLocomotive = controller.getLocomotiveById(1);
        assertTrue(retrievedLocomotive.isPresent());
        assertEquals("Vectron MS", retrievedLocomotive.get().getModel());
    }

    @Test
    public void testUpdateLocomotiveNotFound() {
        controller.createLocomotive(locomotive1);

        boolean result = controller.updateLocomotive(999, locomotive2);

        assertFalse(result);
    }

    @Test
    public void testDeleteLocomotiveSuccess() {
        controller.createLocomotive(locomotive1);
        controller.createLocomotive(locomotive2);
        controller.createLocomotive(locomotive3);

        boolean result = controller.deleteLocomotive(2);

        assertTrue(result);
        assertEquals(2, controller.getAllLocomotives().size());
        assertFalse(controller.getLocomotiveById(2).isPresent());
    }

    @Test
    public void testDeleteLocomotiveNotFound() {
        controller.createLocomotive(locomotive1);

        boolean result = controller.deleteLocomotive(999);

        assertFalse(result);
        assertEquals(1, controller.getAllLocomotives().size());
    }

    @Test
    public void testGetNextLocomotiveId() {
        Integer nextId = controller.getNextLocomotiveId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllLocomotives() {
        controller.createLocomotive(locomotive1);
        controller.createLocomotive(locomotive2);
        controller.createLocomotive(locomotive3);
        assertEquals(3, controller.getAllLocomotives().size());

        controller.clearAllLocomotives();

        assertEquals(0, controller.getAllLocomotives().size());
    }

    @Test
    public void testConstructor() {
        LocomotiveController newController = new LocomotiveController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLocomotives());
    }
}
