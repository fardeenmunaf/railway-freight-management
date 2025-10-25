package app.controller;

import app.model.PickWave;
import app.model.Cart;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PickWaveControllerTest {

    private PickWaveController controller;
    private PickWave pickWave1;
    private PickWave pickWave2;
    private PickWave pickWave3;
    private Cart cart1;

    @Before
    public void setUp() {
        Repositories.getInstance().getPickWaveRepository().clear();
        controller = new PickWaveController();

        cart1 = new Cart(1, "CART-001", 500.0);

        pickWave1 = new PickWave(1, cart1, "FIFO", "Pending");
        pickWave2 = new PickWave(2, cart1, "Zone Picking", "In Progress");
        pickWave3 = new PickWave(3, cart1, "Batch Picking", "Completed");
    }

    @Test
    public void testCreatePickWave() {
        PickWave result = controller.createPickWave(pickWave1);

        assertNotNull(result);
        assertEquals(pickWave1, result);
        assertEquals(1, controller.getAllPickWaves().size());
    }

    @Test
    public void testCreatePickWaveWithNull() {
        PickWave result = controller.createPickWave(null);

        assertNull(result);
        assertEquals(0, controller.getAllPickWaves().size());
    }

    @Test
    public void testGetAllPickWaves() {
        controller.createPickWave(pickWave1);
        controller.createPickWave(pickWave2);
        controller.createPickWave(pickWave3);

        List<PickWave> pickWaves = controller.getAllPickWaves();

        assertEquals(3, pickWaves.size());
        assertTrue(pickWaves.contains(pickWave1));
        assertTrue(pickWaves.contains(pickWave2));
        assertTrue(pickWaves.contains(pickWave3));
    }

    @Test
    public void testGetAllPickWavesEmpty() {
        List<PickWave> pickWaves = controller.getAllPickWaves();

        assertNotNull(pickWaves);
        assertEquals(0, pickWaves.size());
    }

    @Test
    public void testGetPickWaveByIdFound() {
        controller.createPickWave(pickWave1);
        controller.createPickWave(pickWave2);

        Optional<PickWave> result = controller.getPickWaveById(1);

        assertTrue(result.isPresent());
        assertEquals(pickWave1, result.get());
    }

    @Test
    public void testGetPickWaveByIdNotFound() {
        controller.createPickWave(pickWave1);

        Optional<PickWave> result = controller.getPickWaveById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdatePickWaveSuccess() {
        controller.createPickWave(pickWave1);

        PickWave updatedPickWave = new PickWave(1, cart1, "FIFO", "Completed");

        boolean result = controller.updatePickWave(1, updatedPickWave);

        assertTrue(result);
        Optional<PickWave> retrievedPickWave = controller.getPickWaveById(1);
        assertTrue(retrievedPickWave.isPresent());
        assertEquals("Completed", retrievedPickWave.get().getStatus());
    }

    @Test
    public void testUpdatePickWaveNotFound() {
        controller.createPickWave(pickWave1);

        boolean result = controller.updatePickWave(999, pickWave2);

        assertFalse(result);
    }

    @Test
    public void testDeletePickWaveSuccess() {
        controller.createPickWave(pickWave1);
        controller.createPickWave(pickWave2);
        controller.createPickWave(pickWave3);

        boolean result = controller.deletePickWave(2);

        assertTrue(result);
        assertEquals(2, controller.getAllPickWaves().size());
        assertFalse(controller.getPickWaveById(2).isPresent());
    }

    @Test
    public void testDeletePickWaveNotFound() {
        controller.createPickWave(pickWave1);

        boolean result = controller.deletePickWave(999);

        assertFalse(result);
        assertEquals(1, controller.getAllPickWaves().size());
    }

    @Test
    public void testGetNextPickWaveId() {
        Integer nextId = controller.getNextPickWaveId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllPickWaves() {
        controller.createPickWave(pickWave1);
        controller.createPickWave(pickWave2);
        controller.createPickWave(pickWave3);
        assertEquals(3, controller.getAllPickWaves().size());

        controller.clearAllPickWaves();

        assertEquals(0, controller.getAllPickWaves().size());
    }

    @Test
    public void testConstructor() {
        PickWaveController newController = new PickWaveController();

        assertNotNull(newController);
        assertNotNull(newController.getAllPickWaves());
    }
}

