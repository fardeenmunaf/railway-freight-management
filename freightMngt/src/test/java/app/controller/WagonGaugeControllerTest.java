package app.controller;

import app.model.*;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class WagonGaugeControllerTest {

    private WagonGaugeController controller;
    private WagonGauge wagonGauge1;
    private WagonGauge wagonGauge2;
    private WagonGauge wagonGauge3;
    private Wagon wagon1;
    private Wagon wagon2;
    private Gauge gauge1;
    private Gauge gauge2;

    @Before
    public void setUp() {
        Repositories.getInstance().getWagonGaugeRepository().clear();
        controller = new WagonGaugeController();

        Company company1 = new Company(1, "Rail Company", "RC", "123456789");
        User operator1 = new User(1, "operator1", "operator1@rail.com", "pass123", company1, true);

        WagonType wagonType1 = new WagonType(1, "Freight");
        Dimensions dim1 = new Dimensions(15.0, 2.8, 3.5);
        WagonModel wagonModel1 = new WagonModel(1, "FreightWagon-2000", wagonType1, "Bombardier",
                "Y25", 2, 18000.0, 60000.0, 120.0, dim1);
        WagonModel wagonModel2 = new WagonModel(2, "FreightWagon-3000", wagonType1, "CAF",
                "Y25", 2, 20000.0, 70000.0, 120.0, dim1);

        wagon1 = new Wagon(1, wagonModel1, operator1, 2018);
        wagon2 = new Wagon(2, wagonModel2, operator1, 2019);

        gauge1 = new Gauge(1, "Standard Gauge", 1435.0);
        gauge2 = new Gauge(2, "Iberian Gauge", 1668.0);

        wagonGauge1 = new WagonGauge(wagon1, gauge1);
        wagonGauge2 = new WagonGauge(wagon1, gauge2);
        wagonGauge3 = new WagonGauge(wagon2, gauge1);
    }

    @Test
    public void testCreateWagonGauge() {
        WagonGauge result = controller.createWagonGauge(wagonGauge1);

        assertNotNull(result);
        assertEquals(wagonGauge1, result);
        assertEquals(1, controller.getAllWagonGauges().size());
    }

    @Test
    public void testCreateWagonGaugeWithNull() {
        WagonGauge result = controller.createWagonGauge(null);

        assertNull(result);
        assertEquals(0, controller.getAllWagonGauges().size());
    }

    @Test
    public void testGetAllWagonGauges() {
        controller.createWagonGauge(wagonGauge1);
        controller.createWagonGauge(wagonGauge2);
        controller.createWagonGauge(wagonGauge3);

        List<WagonGauge> wagonGauges = controller.getAllWagonGauges();

        assertEquals(3, wagonGauges.size());
        assertTrue(wagonGauges.contains(wagonGauge1));
        assertTrue(wagonGauges.contains(wagonGauge2));
        assertTrue(wagonGauges.contains(wagonGauge3));
    }

    @Test
    public void testGetAllWagonGaugesEmpty() {
        List<WagonGauge> wagonGauges = controller.getAllWagonGauges();

        assertNotNull(wagonGauges);
        assertEquals(0, wagonGauges.size());
    }

    @Test
    public void testGetWagonGaugesByWagonId() {
        controller.createWagonGauge(wagonGauge1);
        controller.createWagonGauge(wagonGauge2);
        controller.createWagonGauge(wagonGauge3);

        List<WagonGauge> wagonGauges = controller.getWagonGaugesByWagonId(1);

        assertEquals(2, wagonGauges.size());
        assertTrue(wagonGauges.contains(wagonGauge1));
        assertTrue(wagonGauges.contains(wagonGauge2));
        assertFalse(wagonGauges.contains(wagonGauge3));
    }

    @Test
    public void testGetWagonGaugesByWagonIdNotFound() {
        controller.createWagonGauge(wagonGauge1);

        List<WagonGauge> wagonGauges = controller.getWagonGaugesByWagonId(999);

        assertNotNull(wagonGauges);
        assertEquals(0, wagonGauges.size());
    }

    @Test
    public void testDeleteWagonGaugeSuccess() {
        controller.createWagonGauge(wagonGauge1);
        controller.createWagonGauge(wagonGauge2);
        controller.createWagonGauge(wagonGauge3);

        boolean result = controller.deleteWagonGauge(1, 1);

        assertTrue(result);
        assertEquals(2, controller.getAllWagonGauges().size());
    }

    @Test
    public void testDeleteWagonGaugeNotFound() {
        controller.createWagonGauge(wagonGauge1);

        boolean result = controller.deleteWagonGauge(999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllWagonGauges().size());
    }

    @Test
    public void testClearAllWagonGauges() {
        controller.createWagonGauge(wagonGauge1);
        controller.createWagonGauge(wagonGauge2);
        controller.createWagonGauge(wagonGauge3);
        assertEquals(3, controller.getAllWagonGauges().size());

        controller.clearAllWagonGauges();

        assertEquals(0, controller.getAllWagonGauges().size());
    }

    @Test
    public void testConstructor() {
        WagonGaugeController newController = new WagonGaugeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllWagonGauges());
    }

    @Test
    public void testMultipleGaugesForSameWagon() {
        controller.createWagonGauge(wagonGauge1);
        controller.createWagonGauge(wagonGauge2);

        List<WagonGauge> gauges = controller.getWagonGaugesByWagonId(1);

        assertEquals(2, gauges.size());
        assertEquals(wagon1, gauges.get(0).getWagon());
        assertEquals(wagon1, gauges.get(1).getWagon());
    }

    @Test
    public void testDeleteSpecificGaugeFromMultiple() {
        controller.createWagonGauge(wagonGauge1);
        controller.createWagonGauge(wagonGauge2);
        controller.createWagonGauge(wagonGauge3);

        boolean result = controller.deleteWagonGauge(1, 2);

        assertTrue(result);
        List<WagonGauge> remainingGauges = controller.getWagonGaugesByWagonId(1);
        assertEquals(1, remainingGauges.size());
        assertEquals(gauge1, remainingGauges.get(0).getGauge());
    }
}

