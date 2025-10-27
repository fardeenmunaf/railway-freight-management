package app.controller;

import app.model.Gauge;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class GaugeControllerTest {

    private GaugeController controller;
    private Gauge gauge1;
    private Gauge gauge2;

    @Before
    public void setUp() {
        // Clear repository to ensure test isolation
        Repositories.getInstance().getGaugeRepository().clear();

        controller = new GaugeController();
        gauge1 = new Gauge(1, "Standard", 1435.0);
        gauge2 = new Gauge(2, "Narrow", 1000.0);
    }

    @Test
    public void testCreateGauge() {
        Gauge result = controller.createGauge(gauge1);

        assertNotNull(result);
        assertEquals(gauge1, result);
        assertEquals(1, controller.getAllGauges().size());
    }

    @Test
    public void testCreateGaugeWithNull() {
        Gauge result = controller.createGauge(null);

        assertNull(result);
        assertEquals(0, controller.getAllGauges().size());
    }

    @Test
    public void testGetAllGauges() {
        controller.createGauge(gauge1);
        controller.createGauge(gauge2);

        List<Gauge> gauges = controller.getAllGauges();

        assertEquals(2, gauges.size());
        assertTrue(gauges.contains(gauge1));
        assertTrue(gauges.contains(gauge2));
    }

    @Test
    public void testGetAllGaugesEmpty() {
        List<Gauge> gauges = controller.getAllGauges();

        assertNotNull(gauges);
        assertEquals(0, gauges.size());
    }

    @Test
    public void testGetGaugeByIdFound() {
        controller.createGauge(gauge1);
        controller.createGauge(gauge2);

        Optional<Gauge> result = controller.getGaugeById(1);

        assertTrue(result.isPresent());
        assertEquals(gauge1, result.get());
    }

    @Test
    public void testGetGaugeByIdNotFound() {
        controller.createGauge(gauge1);

        Optional<Gauge> result = controller.getGaugeById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetGaugeByNameFound() {
        controller.createGauge(gauge1);
        controller.createGauge(gauge2);

        Optional<Gauge> result = controller.getGaugeByName("Standard");

        assertTrue(result.isPresent());
        assertEquals(gauge1, result.get());
    }

    @Test
    public void testGetGaugeByNameNotFound() {
        controller.createGauge(gauge1);

        Optional<Gauge> result = controller.getGaugeByName("Non-existent");

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateGaugeSuccess() {
        controller.createGauge(gauge1);
        Gauge updatedGauge = new Gauge(1, "Standard Updated", 1435.0);

        boolean result = controller.updateGauge(1, updatedGauge);

        assertTrue(result);
        Optional<Gauge> retrievedGauge = controller.getGaugeById(1);
        assertTrue(retrievedGauge.isPresent());
        assertEquals("Standard Updated", retrievedGauge.get().getGaugeName());
    }

    @Test
    public void testUpdateGaugeNotFound() {
        controller.createGauge(gauge1);
        Gauge updatedGauge = new Gauge(999, "Non-existent Gauge", 1000.0);

        boolean result = controller.updateGauge(999, updatedGauge);

        assertFalse(result);
    }

    @Test
    public void testDeleteGaugeSuccess() {
        controller.createGauge(gauge1);
        controller.createGauge(gauge2);

        boolean result = controller.deleteGauge(1);

        assertTrue(result);
        assertEquals(1, controller.getAllGauges().size());
        assertFalse(controller.getGaugeById(1).isPresent());
    }

    @Test
    public void testDeleteGaugeNotFound() {
        controller.createGauge(gauge1);

        boolean result = controller.deleteGauge(999);

        assertFalse(result);
        assertEquals(1, controller.getAllGauges().size());
    }

    @Test
    public void testGetNextGaugeId() {
        Integer nextId = controller.getNextGaugeId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testConstructor() {
        GaugeController newController = new GaugeController();

        assertNotNull(newController);
        assertNotNull(newController.getAllGauges());
    }

    @Test
    public void testContainsNameFound() {
        controller.createGauge(gauge1);

        boolean result = controller.containsName("Standard");

        assertTrue(result);
    }

    @Test
    public void testContainsNameNotFound() {
        controller.createGauge(gauge1);

        boolean result = controller.containsName("Non-existent Gauge");

        assertFalse(result);
    }

    @Test
    public void testClearAllGauges() {
        controller.createGauge(gauge1);
        controller.createGauge(gauge2);
        assertEquals(2, controller.getAllGauges().size());

        controller.clearAllGauges();

        assertEquals(0, controller.getAllGauges().size());
    }
}

