package app.controller;

import app.model.PickTask;
import app.model.PickWave;
import app.model.OrderLine;
import app.model.Order;
import app.model.Item;
import app.model.ItemCategory;
import app.model.Cart;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PickTaskControllerTest {

    private PickTaskController controller;
    private PickTask pickTask1;
    private PickTask pickTask2;
    private PickTask pickTask3;
    private OrderLine orderLine1;

    @Before
    public void setUp() {
        Repositories.getInstance().getPickTaskRepository().clear();
        controller = new PickTaskController();

        Order order1 = new Order(1, "Pending", 1, LocalDateTime.of(2025, 2, 1, 10, 0));
        ItemCategory category1 = new ItemCategory(1, "Electronics");
        Item item1 = new Item(1, category1, "SKU001", "Laptop", "unit", 0.05, 2.5);
        orderLine1 = new OrderLine(1, order1, item1, 10.0);

        Cart cart1 = new Cart(1, "CART-001", 500.0);
        PickWave pickWave1 = new PickWave(1, cart1, "FIFO", "Pending");
        List<PickWave> pickWaves = new ArrayList<>();
        pickWaves.add(pickWave1);

        pickTask1 = new PickTask(1, pickWaves, orderLine1, 10, 5.0, 1);
        pickTask2 = new PickTask(2, pickWaves, orderLine1, 5, 3.0, 2);
        pickTask3 = new PickTask(3, pickWaves, orderLine1, 2, 2.0, 3);
    }

    @Test
    public void testCreatePickTask() {
        PickTask result = controller.createPickTask(pickTask1);

        assertNotNull(result);
        assertEquals(pickTask1, result);
        assertEquals(1, controller.getAllPickTasks().size());
    }

    @Test
    public void testCreatePickTaskWithNull() {
        PickTask result = controller.createPickTask(null);

        assertNull(result);
        assertEquals(0, controller.getAllPickTasks().size());
    }

    @Test
    public void testGetAllPickTasks() {
        controller.createPickTask(pickTask1);
        controller.createPickTask(pickTask2);
        controller.createPickTask(pickTask3);

        List<PickTask> pickTasks = controller.getAllPickTasks();

        assertEquals(3, pickTasks.size());
        assertTrue(pickTasks.contains(pickTask1));
        assertTrue(pickTasks.contains(pickTask2));
        assertTrue(pickTasks.contains(pickTask3));
    }

    @Test
    public void testGetAllPickTasksEmpty() {
        List<PickTask> pickTasks = controller.getAllPickTasks();

        assertNotNull(pickTasks);
        assertEquals(0, pickTasks.size());
    }

    @Test
    public void testGetPickTaskByIdFound() {
        controller.createPickTask(pickTask1);
        controller.createPickTask(pickTask2);

        Optional<PickTask> result = controller.getPickTaskById(1);

        assertTrue(result.isPresent());
        assertEquals(pickTask1, result.get());
    }

    @Test
    public void testGetPickTaskByIdNotFound() {
        controller.createPickTask(pickTask1);

        Optional<PickTask> result = controller.getPickTaskById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdatePickTaskSuccess() {
        controller.createPickTask(pickTask1);

        Cart cart1 = new Cart(1, "CART-001", 500.0);
        PickWave pickWave1 = new PickWave(1, cart1, "FIFO", "Pending");
        List<PickWave> pickWaves = new ArrayList<>();
        pickWaves.add(pickWave1);
        PickTask updatedPickTask = new PickTask(1, pickWaves, orderLine1, 8, 5.0, 1);

        boolean result = controller.updatePickTask(1, updatedPickTask);

        assertTrue(result);
        Optional<PickTask> retrievedPickTask = controller.getPickTaskById(1);
        assertTrue(retrievedPickTask.isPresent());
        assertEquals(8, retrievedPickTask.get().getBalance().intValue());
    }

    @Test
    public void testUpdatePickTaskNotFound() {
        controller.createPickTask(pickTask1);

        boolean result = controller.updatePickTask(999, pickTask2);

        assertFalse(result);
    }

    @Test
    public void testDeletePickTaskSuccess() {
        controller.createPickTask(pickTask1);
        controller.createPickTask(pickTask2);
        controller.createPickTask(pickTask3);

        boolean result = controller.deletePickTask(2);

        assertTrue(result);
        assertEquals(2, controller.getAllPickTasks().size());
        assertFalse(controller.getPickTaskById(2).isPresent());
    }

    @Test
    public void testDeletePickTaskNotFound() {
        controller.createPickTask(pickTask1);

        boolean result = controller.deletePickTask(999);

        assertFalse(result);
        assertEquals(1, controller.getAllPickTasks().size());
    }

    @Test
    public void testGetNextPickTaskId() {
        Integer nextId = controller.getNextPickTaskId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllPickTasks() {
        controller.createPickTask(pickTask1);
        controller.createPickTask(pickTask2);
        controller.createPickTask(pickTask3);
        assertEquals(3, controller.getAllPickTasks().size());

        controller.clearAllPickTasks();

        assertEquals(0, controller.getAllPickTasks().size());
    }

    @Test
    public void testConstructor() {
        PickTaskController newController = new PickTaskController();

        assertNotNull(newController);
        assertNotNull(newController.getAllPickTasks());
    }
}

