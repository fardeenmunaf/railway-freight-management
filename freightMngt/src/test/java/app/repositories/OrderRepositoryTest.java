package app.repositories;

import app.model.Order;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for OrderRepository.
 * Tests all public methods including CRUD operations, search by status and priority, and edge cases.
 */
public class OrderRepositoryTest {

    private OrderRepository repository;
    private Order order1;
    private Order order2;
    private Order order3;

    @Before
    public void setUp() {
        repository = new OrderRepository();

        order1 = new Order(1, "Pending", 1, LocalDateTime.of(2025, 12, 1, 10, 0));

        order2 = new Order(2, "Processing", 2, LocalDateTime.of(2025, 12, 5, 14, 30));

        order3 = new Order(3, "Completed", 1, LocalDateTime.of(2025, 12, 10, 16, 45));
    }

    @Test
    public void testAddOrder() {
        repository.addOrder(order1);

        List<Order> orders = repository.getAllOrders();
        assertEquals(1, orders.size());
        assertTrue(orders.contains(order1));
    }

    @Test
    public void testAddOrderWithNull() {
        repository.addOrder(null);

        List<Order> orders = repository.getAllOrders();
        assertEquals(0, orders.size());
    }

    @Test
    public void testAddOrderWithNullId() {
        Order orderWithNullId = new Order(null, "Pending", 1, LocalDateTime.now());
        repository.addOrder(orderWithNullId);

        List<Order> orders = repository.getAllOrders();
        assertEquals(0, orders.size());
    }

    @Test
    public void testAddDuplicateOrder() {
        repository.addOrder(order1);
        repository.addOrder(order1);

        List<Order> orders = repository.getAllOrders();
        assertEquals(1, orders.size());
    }

    @Test
    public void testGetAllOrders() {
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);

        List<Order> orders = repository.getAllOrders();

        assertEquals(3, orders.size());
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
        assertTrue(orders.contains(order3));
    }

    @Test
    public void testGetAllOrdersEmpty() {
        List<Order> orders = repository.getAllOrders();

        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    public void testGetOrderByIdFound() {
        repository.addOrder(order1);
        repository.addOrder(order2);

        Order result = repository.getOrderById(1);

        assertNotNull(result);
        assertEquals(order1, result);
    }

    @Test
    public void testGetOrderByIdNotFound() {
        repository.addOrder(order1);

        Order result = repository.getOrderById(999);

        assertNull(result);
    }

    @Test
    public void testGetOrdersByStatus() {
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);

        List<Order> pendingOrders = repository.getOrdersByStatus("Pending");

        assertEquals(1, pendingOrders.size());
        assertTrue(pendingOrders.contains(order1));
    }

    @Test
    public void testGetOrdersByStatusNotFound() {
        repository.addOrder(order1);
        repository.addOrder(order2);

        List<Order> result = repository.getOrdersByStatus("NonExistent");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetOrdersByStatusWithNull() {
        repository.addOrder(order1);

        List<Order> result = repository.getOrdersByStatus(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetOrdersByPriority() {
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);

        List<Order> priority1Orders = repository.getOrdersByPriority(1);

        assertEquals(2, priority1Orders.size());
        assertTrue(priority1Orders.contains(order1));
        assertTrue(priority1Orders.contains(order3));
    }

    @Test
    public void testGetOrdersByPriorityNotFound() {
        repository.addOrder(order1);
        repository.addOrder(order2);

        List<Order> result = repository.getOrdersByPriority(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetOrdersByPriorityWithNull() {
        repository.addOrder(order1);

        List<Order> result = repository.getOrdersByPriority(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testUpdateOrderSuccess() {
        repository.addOrder(order1);

        Order updatedOrder = new Order(1, "Updated", 3, LocalDateTime.of(2025, 12, 15, 9, 0));

        boolean result = repository.updateOrder(1, updatedOrder);

        assertTrue(result);
        Order retrievedOrder = repository.getOrderById(1);
        assertNotNull(retrievedOrder);
        assertEquals("Updated", retrievedOrder.getStatus());
        assertEquals(Integer.valueOf(3), retrievedOrder.getPriority());
    }

    @Test
    public void testUpdateOrderNotFound() {
        repository.addOrder(order1);

        boolean result = repository.updateOrder(999, order2);

        assertFalse(result);
    }

    @Test
    public void testDeleteOrderSuccess() {
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);

        boolean result = repository.deleteOrder(2);

        assertTrue(result);
        assertEquals(2, repository.getAllOrders().size());
        assertNull(repository.getOrderById(2));
    }

    @Test
    public void testDeleteOrderNotFound() {
        repository.addOrder(order1);

        boolean result = repository.deleteOrder(999);

        assertFalse(result);
        assertEquals(1, repository.getAllOrders().size());
    }

    @Test
    public void testGetNextOrderId() {
        repository.addOrder(order1);
        repository.addOrder(order2);

        Integer nextId = repository.getNextOrderId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextOrderIdEmpty() {
        Integer nextId = repository.getNextOrderId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testGetNextOrderIdWithNullIds() {
        repository.addOrder(order1);

        Integer nextId = repository.getNextOrderId();

        assertEquals(Integer.valueOf(2), nextId);
    }

    @Test
    public void testClear() {
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);
        assertEquals(3, repository.getAllOrders().size());

        repository.clear();

        assertEquals(0, repository.getAllOrders().size());
    }

    @Test
    public void testConstructor() {
        OrderRepository newRepository = new OrderRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllOrders());
    }

    @Test
    public void testGetOrdersByStatusMultipleMatches() {
        Order order4 = new Order(4, "Pending", 1, LocalDateTime.now());

        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order4);

        List<Order> pendingOrders = repository.getOrdersByStatus("Pending");

        assertEquals(2, pendingOrders.size());
        assertTrue(pendingOrders.contains(order1));
        assertTrue(pendingOrders.contains(order4));
    }

    @Test
    public void testGetOrdersByPriorityMultipleMatches() {
        repository.addOrder(order1);
        repository.addOrder(order2);
        repository.addOrder(order3);

        List<Order> priority1Orders = repository.getOrdersByPriority(1);

        assertEquals(2, priority1Orders.size());
        assertTrue(priority1Orders.contains(order1));
        assertTrue(priority1Orders.contains(order3));
    }
}

