package app.controller;

import app.model.OrderLine;
import app.model.Order;
import app.model.Item;
import app.model.ItemCategory;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class OrderLineControllerTest {

    private OrderLineController controller;
    private OrderLine orderLine1;
    private OrderLine orderLine2;
    private OrderLine orderLine3;
    private Order order1;
    private Order order2;
    private Item item1;
    private Item item2;

    @Before
    public void setUp() {
        Repositories.getInstance().getOrderLineRepository().clear();

        controller = new OrderLineController();

        order1 = new Order(1, "Pending", 1, LocalDateTime.of(2025, 11, 1, 10, 0));
        order2 = new Order(2, "Processing", 2, LocalDateTime.of(2025, 11, 5, 14, 30));

        ItemCategory category1 = new ItemCategory(1, "Electronics");
        item1 = new Item(1, category1, "SKU001", "Laptop", "unit", 0.05, 2.5);
        item2 = new Item(2, category1, "SKU002", "Monitor", "unit", 0.08, 5.0);

        orderLine1 = new OrderLine(1, order1, item1, 10.0);
        orderLine2 = new OrderLine(2, order1, item2, 5.0);
        orderLine3 = new OrderLine(3, order2, item1, 20.0);
    }

    @Test
    public void testCreateOrderLine() {
        OrderLine result = controller.createOrderLine(orderLine1);

        assertNotNull(result);
        assertEquals(orderLine1, result);
        assertEquals(1, controller.getAllOrderLines().size());
    }

    @Test
    public void testCreateOrderLineWithNull() {
        OrderLine result = controller.createOrderLine(null);

        assertNull(result);
        assertEquals(0, controller.getAllOrderLines().size());
    }

    @Test
    public void testGetAllOrderLines() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);
        controller.createOrderLine(orderLine3);

        List<OrderLine> orderLines = controller.getAllOrderLines();

        assertEquals(3, orderLines.size());
        assertTrue(orderLines.contains(orderLine1));
        assertTrue(orderLines.contains(orderLine2));
        assertTrue(orderLines.contains(orderLine3));
    }

    @Test
    public void testGetAllOrderLinesEmpty() {
        List<OrderLine> orderLines = controller.getAllOrderLines();

        assertNotNull(orderLines);
        assertEquals(0, orderLines.size());
    }

    @Test
    public void testGetOrderLineByIdFound() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);

        Optional<OrderLine> result = controller.getOrderLineById(1);

        assertTrue(result.isPresent());
        assertEquals(orderLine1, result.get());
    }

    @Test
    public void testGetOrderLineByIdNotFound() {
        controller.createOrderLine(orderLine1);

        Optional<OrderLine> result = controller.getOrderLineById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateOrderLineSuccess() {
        controller.createOrderLine(orderLine1);

        OrderLine updatedOrderLine = new OrderLine(1, order1, item1, 15.0);

        boolean result = controller.updateOrderLine(1, updatedOrderLine);

        assertTrue(result);
        Optional<OrderLine> retrievedOrderLine = controller.getOrderLineById(1);
        assertTrue(retrievedOrderLine.isPresent());
        assertEquals(15.0, retrievedOrderLine.get().getQty(), 0.01);
    }

    @Test
    public void testUpdateOrderLineNotFound() {
        controller.createOrderLine(orderLine1);

        boolean result = controller.updateOrderLine(999, orderLine2);

        assertFalse(result);
    }

    @Test
    public void testGetOrderLinesByOrderIdFound() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);
        controller.createOrderLine(orderLine3);

        List<OrderLine> result = controller.getOrderLinesByOrderId(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(orderLine1));
        assertTrue(result.contains(orderLine2));
        assertFalse(result.contains(orderLine3));
    }

    @Test
    public void testGetOrderLinesByOrderIdNotFound() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);

        List<OrderLine> result = controller.getOrderLinesByOrderId(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetOrderLinesByOrderIdWithNull() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);

        List<OrderLine> result = controller.getOrderLinesByOrderId(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteOrderLineSuccess() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);
        controller.createOrderLine(orderLine3);

        boolean result = controller.deleteOrderLine(2);

        assertTrue(result);
        assertEquals(2, controller.getAllOrderLines().size());
        assertFalse(controller.getOrderLineById(2).isPresent());
    }

    @Test
    public void testDeleteOrderLineNotFound() {
        controller.createOrderLine(orderLine1);

        boolean result = controller.deleteOrderLine(999);

        assertFalse(result);
        assertEquals(1, controller.getAllOrderLines().size());
    }

    @Test
    public void testGetNextOrderLineId() {
        Integer nextId = controller.getNextOrderLineId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllOrderLines() {
        controller.createOrderLine(orderLine1);
        controller.createOrderLine(orderLine2);
        controller.createOrderLine(orderLine3);
        assertEquals(3, controller.getAllOrderLines().size());

        controller.clearAllOrderLines();

        assertEquals(0, controller.getAllOrderLines().size());
    }

    @Test
    public void testConstructor() {
        OrderLineController newController = new OrderLineController();

        assertNotNull(newController);
        assertNotNull(newController.getAllOrderLines());
    }
}
