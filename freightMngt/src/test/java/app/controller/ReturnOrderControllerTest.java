package app.controller;

import app.model.ReturnOrder;
import app.model.Order;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReturnOrderControllerTest {

    private ReturnOrderController controller;
    private ReturnOrder returnOrder1;
    private ReturnOrder returnOrder2;
    private ReturnOrder returnOrder3;
    private Order order1;

    @Before
    public void setUp() {
        Repositories.getInstance().getReturnOrderRepository().clear();
        controller = new ReturnOrderController();

        order1 = new Order(1, "Completed", 1, LocalDateTime.of(2025, 1, 15, 10, 0));

        returnOrder1 = new ReturnOrder(1, order1, "Damaged", "Pending", LocalDateTime.of(2025, 2, 1, 14, 30));
        returnOrder2 = new ReturnOrder(2, order1, "Wrong Item", "Processing", LocalDateTime.of(2025, 2, 5, 9, 15));
        returnOrder3 = new ReturnOrder(3, order1, "Not Needed", "Approved", LocalDateTime.of(2025, 2, 10, 16, 45));
    }

    @Test
    public void testCreateReturnOrder() {
        ReturnOrder result = controller.createReturnOrder(returnOrder1);

        assertNotNull(result);
        assertEquals(returnOrder1, result);
        assertEquals(1, controller.getAllReturnOrders().size());
    }

    @Test
    public void testCreateReturnOrderWithNull() {
        ReturnOrder result = controller.createReturnOrder(null);

        assertNull(result);
        assertEquals(0, controller.getAllReturnOrders().size());
    }

    @Test
    public void testGetAllReturnOrders() {
        controller.createReturnOrder(returnOrder1);
        controller.createReturnOrder(returnOrder2);
        controller.createReturnOrder(returnOrder3);

        List<ReturnOrder> returnOrders = controller.getAllReturnOrders();

        assertEquals(3, returnOrders.size());
        assertTrue(returnOrders.contains(returnOrder1));
        assertTrue(returnOrders.contains(returnOrder2));
        assertTrue(returnOrders.contains(returnOrder3));
    }

    @Test
    public void testGetAllReturnOrdersEmpty() {
        List<ReturnOrder> returnOrders = controller.getAllReturnOrders();

        assertNotNull(returnOrders);
        assertEquals(0, returnOrders.size());
    }

    @Test
    public void testGetReturnOrderByIdFound() {
        controller.createReturnOrder(returnOrder1);
        controller.createReturnOrder(returnOrder2);

        Optional<ReturnOrder> result = controller.getReturnOrderById(1);

        assertTrue(result.isPresent());
        assertEquals(returnOrder1, result.get());
    }

    @Test
    public void testGetReturnOrderByIdNotFound() {
        controller.createReturnOrder(returnOrder1);

        Optional<ReturnOrder> result = controller.getReturnOrderById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateReturnOrderSuccess() {
        controller.createReturnOrder(returnOrder1);

        ReturnOrder updatedReturnOrder = new ReturnOrder(1, order1, "Damaged", "Approved", LocalDateTime.of(2025, 2, 1, 14, 30));

        boolean result = controller.updateReturnOrder(1, updatedReturnOrder);

        assertTrue(result);
        Optional<ReturnOrder> retrievedReturnOrder = controller.getReturnOrderById(1);
        assertTrue(retrievedReturnOrder.isPresent());
        assertEquals("Approved", retrievedReturnOrder.get().getStatus());
    }

    @Test
    public void testUpdateReturnOrderNotFound() {
        controller.createReturnOrder(returnOrder1);

        boolean result = controller.updateReturnOrder(999, returnOrder2);

        assertFalse(result);
    }

    @Test
    public void testDeleteReturnOrderSuccess() {
        controller.createReturnOrder(returnOrder1);
        controller.createReturnOrder(returnOrder2);
        controller.createReturnOrder(returnOrder3);

        boolean result = controller.deleteReturnOrder(2);

        assertTrue(result);
        assertEquals(2, controller.getAllReturnOrders().size());
        assertFalse(controller.getReturnOrderById(2).isPresent());
    }

    @Test
    public void testDeleteReturnOrderNotFound() {
        controller.createReturnOrder(returnOrder1);

        boolean result = controller.deleteReturnOrder(999);

        assertFalse(result);
        assertEquals(1, controller.getAllReturnOrders().size());
    }

    @Test
    public void testGetNextReturnOrderId() {
        Integer nextId = controller.getNextReturnOrderId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllReturnOrders() {
        controller.createReturnOrder(returnOrder1);
        controller.createReturnOrder(returnOrder2);
        controller.createReturnOrder(returnOrder3);
        assertEquals(3, controller.getAllReturnOrders().size());

        controller.clearAllReturnOrders();

        assertEquals(0, controller.getAllReturnOrders().size());
    }

    @Test
    public void testConstructor() {
        ReturnOrderController newController = new ReturnOrderController();

        assertNotNull(newController);
        assertNotNull(newController.getAllReturnOrders());
    }
}
