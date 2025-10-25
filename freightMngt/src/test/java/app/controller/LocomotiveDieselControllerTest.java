package app.controller;

import app.model.LocomotiveDiesel;
import app.model.Locomotive;
import app.model.User;
import app.model.Company;
import app.model.LocomotiveType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LocomotiveDieselControllerTest {

    private LocomotiveDieselController controller;
    private LocomotiveDiesel locomotiveDiesel1;
    private LocomotiveDiesel locomotiveDiesel2;
    private LocomotiveDiesel locomotiveDiesel3;
    private Locomotive locomotive1;

    @Before
    public void setUp() {
        Repositories.getInstance().getLocomotiveDieselRepository().clear();
        controller = new LocomotiveDieselController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        LocomotiveType type1 = new LocomotiveType(1, "Freight");
        Dimension dim1 = new Dimension(15, 3);

        locomotive1 = new Locomotive(1, operator1, type1, "Siemens", "Vectron", 2020, 5600, 1.2, dim1, 160.0, 120.0, 22000.0, 87000.0, 4, "Y25", "350");

        locomotiveDiesel1 = new LocomotiveDiesel(locomotive1, 3000.0);
        locomotiveDiesel1.setLocomotiveId(1);

        locomotiveDiesel2 = new LocomotiveDiesel(locomotive1, 3500.0);
        locomotiveDiesel2.setLocomotiveId(2);

        locomotiveDiesel3 = new LocomotiveDiesel(locomotive1, 4000.0);
        locomotiveDiesel3.setLocomotiveId(3);
    }

    @Test
    public void testCreateLocomotiveDiesel() {
        LocomotiveDiesel result = controller.createLocomotiveDiesel(locomotiveDiesel1);

        assertNotNull(result);
        assertEquals(locomotiveDiesel1, result);
        assertEquals(1, controller.getAllLocomotiveDiesels().size());
    }

    @Test
    public void testCreateLocomotiveDieselWithNull() {
        LocomotiveDiesel result = controller.createLocomotiveDiesel(null);

        assertNull(result);
        assertEquals(0, controller.getAllLocomotiveDiesels().size());
    }

    @Test
    public void testGetAllLocomotiveDiesels() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);
        controller.createLocomotiveDiesel(locomotiveDiesel2);
        controller.createLocomotiveDiesel(locomotiveDiesel3);

        List<LocomotiveDiesel> locomotives = controller.getAllLocomotiveDiesels();

        assertEquals(3, locomotives.size());
        assertTrue(locomotives.contains(locomotiveDiesel1));
        assertTrue(locomotives.contains(locomotiveDiesel2));
        assertTrue(locomotives.contains(locomotiveDiesel3));
    }

    @Test
    public void testGetAllLocomotiveDieselsEmpty() {
        List<LocomotiveDiesel> locomotives = controller.getAllLocomotiveDiesels();

        assertNotNull(locomotives);
        assertEquals(0, locomotives.size());
    }

    @Test
    public void testGetLocomotiveDieselByIdFound() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);
        controller.createLocomotiveDiesel(locomotiveDiesel2);

        Optional<LocomotiveDiesel> result = controller.getLocomotiveDieselById(1);

        assertTrue(result.isPresent());
        assertEquals(locomotiveDiesel1, result.get());
    }

    @Test
    public void testGetLocomotiveDieselByIdNotFound() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);

        Optional<LocomotiveDiesel> result = controller.getLocomotiveDieselById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateLocomotiveDieselSuccess() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);

        LocomotiveDiesel updatedLocomotive = new LocomotiveDiesel(locomotive1, 3200.0);
        updatedLocomotive.setLocomotiveId(1);

        boolean result = controller.updateLocomotiveDiesel(1, updatedLocomotive);

        assertTrue(result);
        Optional<LocomotiveDiesel> retrievedLocomotive = controller.getLocomotiveDieselById(1);
        assertTrue(retrievedLocomotive.isPresent());
        assertEquals(3200.0, retrievedLocomotive.get().getFuelCapacity(), 0.01);
    }

    @Test
    public void testUpdateLocomotiveDieselNotFound() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);

        boolean result = controller.updateLocomotiveDiesel(999, locomotiveDiesel2);

        assertFalse(result);
    }

    @Test
    public void testDeleteLocomotiveDieselSuccess() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);
        controller.createLocomotiveDiesel(locomotiveDiesel2);
        controller.createLocomotiveDiesel(locomotiveDiesel3);

        boolean result = controller.deleteLocomotiveDiesel(2);

        assertTrue(result);
        assertEquals(2, controller.getAllLocomotiveDiesels().size());
        assertFalse(controller.getLocomotiveDieselById(2).isPresent());
    }

    @Test
    public void testDeleteLocomotiveDieselNotFound() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);

        boolean result = controller.deleteLocomotiveDiesel(999);

        assertFalse(result);
        assertEquals(1, controller.getAllLocomotiveDiesels().size());
    }

    @Test
    public void testGetNextLocomotiveDieselId() {
        Integer nextId = controller.getNextLocomotiveDieselId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllLocomotiveDiesels() {
        controller.createLocomotiveDiesel(locomotiveDiesel1);
        controller.createLocomotiveDiesel(locomotiveDiesel2);
        controller.createLocomotiveDiesel(locomotiveDiesel3);
        assertEquals(3, controller.getAllLocomotiveDiesels().size());

        controller.clearAllLocomotiveDiesels();

        assertEquals(0, controller.getAllLocomotiveDiesels().size());
    }

    @Test
    public void testConstructor() {
        LocomotiveDieselController newController = new LocomotiveDieselController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLocomotiveDiesels());
    }
}

