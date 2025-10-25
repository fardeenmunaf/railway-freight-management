package app.controller;

import app.model.Order;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class OrderControllerTest {

    private OrderController controller;
    private Order order1;
    private Order order2;
    private Order order3;

    @Before
    public void setUp() {
        Repositories.getInstance().getOrderRepository().clear();
        controller = new OrderController();

        order1 = new Order(1, "Pending", 1, LocalDateTime.of(2025, 11, 1, 10, 0));
        order2 = new Order(2, "Processing", 2, LocalDateTime.of(2025, 11, 5, 14, 30));
        order3 = new Order(3, "Completed", 3, LocalDateTime.of(2025, 10, 20, 9, 15));
    }

    @Test
    public void testCreateOrder() {
        Order result = controller.createOrder(order1);

        assertNotNull(result);
        assertEquals(order1, result);
        assertEquals(1, controller.getAllOrders().size());
    }

    @Test
    public void testCreateOrderWithNull() {
        Order result = controller.createOrder(null);

        assertNull(result);
        assertEquals(0, controller.getAllOrders().size());
    }

    @Test
    public void testGetAllOrders() {
        controller.createOrder(order1);
        controller.createOrder(order2);
        controller.createOrder(order3);

        List<Order> orders = controller.getAllOrders();

        assertEquals(3, orders.size());
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
        assertTrue(orders.contains(order3));
    }

    @Test
    public void testGetAllOrdersEmpty() {
        List<Order> orders = controller.getAllOrders();

        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    public void testGetOrderByIdFound() {
        controller.createOrder(order1);
        controller.createOrder(order2);

        Optional<Order> result = controller.getOrderById(1);

        assertTrue(result.isPresent());
        assertEquals(order1, result.get());
    }

    @Test
    public void testGetOrderByIdNotFound() {
        controller.createOrder(order1);

        Optional<Order> result = controller.getOrderById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateOrderSuccess() {
        controller.createOrder(order1);

        Order updatedOrder = new Order(1, "Shipped", 1, LocalDateTime.of(2025, 11, 1, 10, 0));

        boolean result = controller.updateOrder(1, updatedOrder);

        assertTrue(result);
        Optional<Order> retrievedOrder = controller.getOrderById(1);
        assertTrue(retrievedOrder.isPresent());
        assertEquals("Shipped", retrievedOrder.get().getStatus());
    }

    @Test
    public void testUpdateOrderNotFound() {
        controller.createOrder(order1);

        boolean result = controller.updateOrder(999, order2);

        assertFalse(result);
    }

    @Test
    public void testDeleteOrderSuccess() {
        controller.createOrder(order1);
        controller.createOrder(order2);
        controller.createOrder(order3);

        boolean result = controller.deleteOrder(2);

        assertTrue(result);
        assertEquals(2, controller.getAllOrders().size());
        assertFalse(controller.getOrderById(2).isPresent());
    }

    @Test
    public void testDeleteOrderNotFound() {
        controller.createOrder(order1);

        boolean result = controller.deleteOrder(999);

        assertFalse(result);
        assertEquals(1, controller.getAllOrders().size());
    }

    @Test
    public void testGetNextOrderId() {
        Integer nextId = controller.getNextOrderId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllOrders() {
        controller.createOrder(order1);
        controller.createOrder(order2);
        controller.createOrder(order3);
        assertEquals(3, controller.getAllOrders().size());

        controller.clearAllOrders();

        assertEquals(0, controller.getAllOrders().size());
    }

    @Test
    public void testConstructor() {
        OrderController newController = new OrderController();

        assertNotNull(newController);
        assertNotNull(newController.getAllOrders());
    }
}

