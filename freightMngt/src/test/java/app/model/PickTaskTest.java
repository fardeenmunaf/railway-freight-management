package app.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for PickTask model.
 * Tests constructors, getters, setters with 100% coverage.
 */
public class PickTaskTest {

    private PickTask pickTask;

    @Before
    public void setUp() {
        pickTask = new PickTask();
    }

    @Test
    public void testDefaultConstructor() {
        PickTask defaultTask = new PickTask();

        assertNull("Pick Task ID should be null", defaultTask.getPickTaskId());
        assertNotNull("Pick Waves should not be null", defaultTask.getPickWaves());
        assertEquals("Pick Waves should be empty", 0, defaultTask.getPickWaves().size());
        assertNull("Order Line should be null", defaultTask.getOrderLine());
        assertNull("Balance should be null", defaultTask.getBalance());
        assertNull("Qty should be null", defaultTask.getQty());
        assertNull("Position Sequence should be null", defaultTask.getPositionSequence());
    }

    @Test
    public void testParameterizedConstructor() {
        List<PickWave> pickWaves = new ArrayList<>();
        pickWaves.add(new PickWave());
        OrderLine orderLine = new OrderLine();

        PickTask paramTask = new PickTask(1, pickWaves, orderLine, 100, 50.0, 1);

        assertEquals("Pick Task ID should be 1", Integer.valueOf(1), paramTask.getPickTaskId());
        assertEquals("Pick Waves size should be 1", 1, paramTask.getPickWaves().size());
        assertEquals("Order Line should match", orderLine, paramTask.getOrderLine());
        assertEquals("Balance should be 100", Integer.valueOf(100), paramTask.getBalance());
        assertEquals("Qty should be 50.0", Double.valueOf(50.0), paramTask.getQty());
        assertEquals("Position Sequence should be 1", Integer.valueOf(1), paramTask.getPositionSequence());
    }

    @Test
    public void testSetAndGetPickTaskId() {
        pickTask.setPickTaskId(10);

        assertEquals("Pick Task ID should be 10", Integer.valueOf(10), pickTask.getPickTaskId());
    }

    @Test
    public void testSetAndGetPickWaves() {
        List<PickWave> pickWaves = new ArrayList<>();
        pickWaves.add(new PickWave());
        pickTask.setPickWaves(pickWaves);

        assertEquals("Pick Waves size should be 1", 1, pickTask.getPickWaves().size());
    }

    @Test
    public void testSetAndGetOrderLine() {
        OrderLine orderLine = new OrderLine();
        pickTask.setOrderLine(orderLine);

        assertEquals("Order Line should match", orderLine, pickTask.getOrderLine());
    }

    @Test
    public void testSetAndGetBalance() {
        pickTask.setBalance(200);

        assertEquals("Balance should be 200", Integer.valueOf(200), pickTask.getBalance());
    }

    @Test
    public void testSetAndGetQty() {
        pickTask.setQty(75.5);

        assertEquals("Qty should be 75.5", Double.valueOf(75.5), pickTask.getQty());
    }

    @Test
    public void testSetAndGetPositionSequence() {
        pickTask.setPositionSequence(3);

        assertEquals("Position Sequence should be 3", Integer.valueOf(3), pickTask.getPositionSequence());
    }

    @Test
    public void testAddPickWave() {
        PickWave pickWave = new PickWave();
        pickTask.addPickWave(pickWave);

        assertEquals("Pick Waves size should be 1", 1, pickTask.getPickWaves().size());
    }

    @Test
    public void testAddPickWaveWithNull() {
        int initialSize = pickTask.getPickWaves().size();
        pickTask.addPickWave(null);

        assertEquals("Pick Waves size should remain unchanged", initialSize, pickTask.getPickWaves().size());
    }

    @Test
    public void testRemovePickWave() {
        PickWave pickWave = new PickWave();
        pickTask.addPickWave(pickWave);

        boolean removed = pickTask.removePickWave(pickWave);

        assertTrue("Pick wave should be removed", removed);
        assertEquals("Pick Waves should be empty", 0, pickTask.getPickWaves().size());
    }

    @Test
    public void testRemoveNonExistentPickWave() {
        PickWave pickWave = new PickWave();

        boolean removed = pickTask.removePickWave(pickWave);

        assertFalse("Pick wave should not be removed", removed);
    }

    @Test
    public void testConstructorWithNullPickWaves() {
        OrderLine orderLine = new OrderLine();
        PickTask task = new PickTask(1, null, orderLine, 50, 25.0, 2);

        assertNotNull("Pick Waves should not be null", task.getPickWaves());
        assertEquals("Pick Waves should be empty", 0, task.getPickWaves().size());
    }

    @Test
    public void testSetPickWavesToNull() {
        pickTask.setPickWaves(null);

        assertNotNull("Pick Waves should not be null", pickTask.getPickWaves());
        assertEquals("Pick Waves should be empty", 0, pickTask.getPickWaves().size());
    }

    @Test
    public void testToString() {
        List<PickWave> pickWaves = new ArrayList<>();
        pickWaves.add(new PickWave());
        OrderLine orderLine = new OrderLine();

        pickTask.setPickTaskId(5);
        pickTask.setPickWaves(pickWaves);
        pickTask.setOrderLine(orderLine);
        pickTask.setBalance(150);
        pickTask.setQty(75.5);
        pickTask.setPositionSequence(2);

        String result = pickTask.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'PickTask{'", result.contains("PickTask{"));
        assertTrue("toString should contain pickTaskId", result.contains("pickTaskId=5"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = pickTask.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'PickTask{'", result.contains("PickTask{"));
    }
}
