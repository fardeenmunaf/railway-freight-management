package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for Order model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class OrderTest {

    private Order order;

    @Before
    public void setUp() {
        order = new Order();
    }

    @Test
    public void testDefaultConstructor() {
        Order defaultOrder = new Order();

        assertNull("Order ID should be null", defaultOrder.getOrderId());
        assertNull("Status should be null", defaultOrder.getStatus());
        assertNull("Priority should be null", defaultOrder.getPriority());
        assertNull("Due date should be null", defaultOrder.getDueDate());
    }

    @Test
    public void testParameterizedConstructor() {
        LocalDateTime dueDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        Order paramOrder = new Order(1, "Pending", 1, dueDate);

        assertEquals("Order ID should be 1", Integer.valueOf(1), paramOrder.getOrderId());
        assertEquals("Status should be 'Pending'", "Pending", paramOrder.getStatus());
        assertEquals("Priority should be 1", Integer.valueOf(1), paramOrder.getPriority());
        assertEquals("Due date should match", dueDate, paramOrder.getDueDate());
    }

    @Test
    public void testSetAndGetOrderId() {
        order.setOrderId(10);

        assertEquals("Order ID should be 10", Integer.valueOf(10), order.getOrderId());
    }

    @Test
    public void testSetAndGetStatus() {
        order.setStatus("Processing");

        assertEquals("Status should be 'Processing'", "Processing", order.getStatus());
    }

    @Test
    public void testSetAndGetPriority() {
        order.setPriority(3);

        assertEquals("Priority should be 3", Integer.valueOf(3), order.getPriority());
    }

    @Test
    public void testSetAndGetDueDate() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 1, 15, 10, 0);
        order.setDueDate(dueDate);

        assertEquals("Due date should match", dueDate, order.getDueDate());
    }

    @Test
    public void testSetOrderIdToNull() {
        order.setOrderId(5);
        order.setOrderId(null);

        assertNull("Order ID should be null", order.getOrderId());
    }

    @Test
    public void testSetStatusToNull() {
        order.setStatus("Completed");
        order.setStatus(null);

        assertNull("Status should be null", order.getStatus());
    }

    @Test
    public void testSetPriorityToNull() {
        order.setPriority(2);
        order.setPriority(null);

        assertNull("Priority should be null", order.getPriority());
    }

    @Test
    public void testSetDueDateToNull() {
        LocalDateTime dueDate = LocalDateTime.now();
        order.setDueDate(dueDate);
        order.setDueDate(null);

        assertNull("Due date should be null", order.getDueDate());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        LocalDateTime dueDate = LocalDateTime.of(2025, 11, 30, 18, 30);

        order.setOrderId(7);
        order.setStatus("Shipped");
        order.setPriority(2);
        order.setDueDate(dueDate);

        assertEquals("Order ID should be 7", Integer.valueOf(7), order.getOrderId());
        assertEquals("Status should be 'Shipped'", "Shipped", order.getStatus());
        assertEquals("Priority should be 2", Integer.valueOf(2), order.getPriority());
        assertEquals("Due date should match", dueDate, order.getDueDate());
    }

    @Test
    public void testConstructorWithNullValues() {
        Order nullOrder = new Order(null, null, null, null);

        assertNull("Order ID should be null", nullOrder.getOrderId());
        assertNull("Status should be null", nullOrder.getStatus());
        assertNull("Priority should be null", nullOrder.getPriority());
        assertNull("Due date should be null", nullOrder.getDueDate());
    }

    @Test
    public void testConstructorWithEmptyStatus() {
        LocalDateTime dueDate = LocalDateTime.now();
        Order orderWithEmptyStatus = new Order(1, "", 1, dueDate);

        assertEquals("Order ID should be 1", Integer.valueOf(1), orderWithEmptyStatus.getOrderId());
        assertEquals("Status should be empty string", "", orderWithEmptyStatus.getStatus());
    }

    @Test
    public void testToString() {
        LocalDateTime dueDate = LocalDateTime.of(2025, 12, 25, 12, 0);
        order.setOrderId(5);
        order.setStatus("Ready");
        order.setPriority(1);
        order.setDueDate(dueDate);

        String result = order.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain orderId", result.contains("orderId=5"));
        assertTrue("toString should contain status", result.contains("status='Ready'"));
        assertTrue("toString should contain priority", result.contains("priority=1"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = order.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Order{'", result.contains("Order{"));
    }
}

