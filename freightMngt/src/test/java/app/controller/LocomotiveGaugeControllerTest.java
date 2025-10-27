package app.controller;

import app.model.LocomotiveGauge;
import app.model.Locomotive;
import app.model.Gauge;
import app.model.User;
import app.model.Company;
import app.model.LocomotiveType;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.util.List;

import static org.junit.Assert.*;

public class LocomotiveGaugeControllerTest {

    private LocomotiveGaugeController controller;
    private LocomotiveGauge lg1;
    private LocomotiveGauge lg2;
    private LocomotiveGauge lg3;
    private Locomotive locomotive1;
    private Locomotive locomotive2;
    private Gauge gauge1;
    private Gauge gauge2;

    @Before
    public void setUp() {
        Repositories.getInstance().getLocomotiveGaugeRepository().clear();
        controller = new LocomotiveGaugeController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "user1", "user1@rail.com", "pass123", company1, true);

        LocomotiveType type1 = new LocomotiveType(1, "Freight");
        Dimension dim1 = new Dimension(15, 3);

        locomotive1 = new Locomotive(1, operator1, type1, "Siemens", "Vectron", 2020, 5600, 1.2, dim1, 160.0, 120.0, 22000.0, 87000.0, 4, "Y25", "350");
        locomotive2 = new Locomotive(2, operator1, type1, "Alstom", "Prima", 2019, 5000, 1.0, dim1, 140.0, 100.0, 20000.0, 85000.0, 4, "Y25", "320");

        gauge1 = new Gauge(1, "Standard Gauge", 1435.0);
        gauge2 = new Gauge(2, "Iberian Gauge", 1668.0);

        lg1 = new LocomotiveGauge(locomotive1, gauge1);
        lg2 = new LocomotiveGauge(locomotive1, gauge2);
        lg3 = new LocomotiveGauge(locomotive2, gauge1);
    }

    @Test
    public void testCreateLocomotiveGauge() {
        LocomotiveGauge result = controller.createLocomotiveGauge(lg1);

        assertNotNull(result);
        assertEquals(lg1, result);
        assertEquals(1, controller.getAllLocomotiveGauges().size());
    }

    @Test
    public void testCreateLocomotiveGaugeWithNull() {
        LocomotiveGauge result = controller.createLocomotiveGauge(null);

        assertNull(result);
        assertEquals(0, controller.getAllLocomotiveGauges().size());
    }

    @Test
    public void testGetAllLocomotiveGauges() {
        controller.createLocomotiveGauge(lg1);
        controller.createLocomotiveGauge(lg2);
        controller.createLocomotiveGauge(lg3);

        List<LocomotiveGauge> gauges = controller.getAllLocomotiveGauges();

        assertEquals(3, gauges.size());
        assertTrue(gauges.contains(lg1));
        assertTrue(gauges.contains(lg2));
        assertTrue(gauges.contains(lg3));
    }

    @Test
    public void testGetAllLocomotiveGaugesEmpty() {
        List<LocomotiveGauge> gauges = controller.getAllLocomotiveGauges();

        assertNotNull(gauges);
        assertEquals(0, gauges.size());
    }

    @Test
    public void testGetLocomotiveGaugesByLocomotiveId() {
        controller.createLocomotiveGauge(lg1);
        controller.createLocomotiveGauge(lg2);
        controller.createLocomotiveGauge(lg3);

        List<LocomotiveGauge> result = controller.getLocomotiveGaugesByLocomotiveId(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(lg1));
        assertTrue(result.contains(lg2));
        assertFalse(result.contains(lg3));
    }

    @Test
    public void testGetLocomotiveGaugesByLocomotiveIdNotFound() {
        controller.createLocomotiveGauge(lg1);

        List<LocomotiveGauge> result = controller.getLocomotiveGaugesByLocomotiveId(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteLocomotiveGaugeSuccess() {
        controller.createLocomotiveGauge(lg1);
        controller.createLocomotiveGauge(lg2);
        controller.createLocomotiveGauge(lg3);

        boolean result = controller.deleteLocomotiveGauge(1, 2);

        assertTrue(result);
        assertEquals(2, controller.getAllLocomotiveGauges().size());
    }

    @Test
    public void testDeleteLocomotiveGaugeNotFound() {
        controller.createLocomotiveGauge(lg1);

        boolean result = controller.deleteLocomotiveGauge(999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllLocomotiveGauges().size());
    }

    @Test
    public void testClearAllLocomotiveGauges() {
        controller.createLocomotiveGauge(lg1);
        controller.createLocomotiveGauge(lg2);
        controller.createLocomotiveGauge(lg3);
        assertEquals(3, controller.getAllLocomotiveGauges().size());

        controller.clearAllLocomotiveGauges();

        assertEquals(0, controller.getAllLocomotiveGauges().size());
    }

    @Test
    public void testConstructor() {
        LocomotiveGaugeController newController = new LocomotiveGaugeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllLocomotiveGauges());
    }
}

