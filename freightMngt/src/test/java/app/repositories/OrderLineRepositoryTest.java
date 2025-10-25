package app.repositories;

import app.model.Order;
import app.model.OrderLine;
import app.model.Item;
import app.model.ItemCategory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for OrderLineRepository.
 * Tests all public methods including CRUD operations, search by order, and edge cases.
 */
public class OrderLineRepositoryTest {

    private OrderLineRepository repository;
    private OrderLine orderLine1;
    private OrderLine orderLine2;
    private OrderLine orderLine3;
    private Order order1;
    private Order order2;
    private Item item1;
    private Item item2;
    private Item item3;

    @Before
    public void setUp() {
        repository = new OrderLineRepository();

        order1 = new Order(1, "Pending", 1, LocalDateTime.now());
        order2 = new Order(2, "Processing", 2, LocalDateTime.now());

        ItemCategory category = new ItemCategory(1, "Electronics");
        item1 = new Item(1, category, "SKU001", "Laptop", "unit", 2.5, 1.5);
        item2 = new Item(2, category, "SKU002", "Mouse", "unit", 0.1, 0.05);
        item3 = new Item(3, category, "SKU003", "Keyboard", "unit", 0.8, 0.4);

        orderLine1 = new OrderLine(1, order1, item1, 10.0);
        orderLine2 = new OrderLine(2, order1, item2, 20.0);
        orderLine3 = new OrderLine(3, order2, item3, 15.0);
    }

    @Test
    public void testAddOrderLine() {
        repository.addOrderLine(orderLine1);

        List<OrderLine> orderLines = repository.getAllOrderLines();
        assertEquals(1, orderLines.size());
        assertTrue(orderLines.contains(orderLine1));
    }

    @Test
    public void testAddOrderLineWithNull() {
        repository.addOrderLine(null);

        List<OrderLine> orderLines = repository.getAllOrderLines();
        assertEquals(0, orderLines.size());
    }

    @Test
    public void testAddOrderLineWithNullId() {
        OrderLine orderLineWithNullId = new OrderLine(null, order1, item1, 5.0);
        repository.addOrderLine(orderLineWithNullId);

        List<OrderLine> orderLines = repository.getAllOrderLines();
        assertEquals(0, orderLines.size());
    }

    @Test
    public void testAddDuplicateOrderLine() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine1);

        List<OrderLine> orderLines = repository.getAllOrderLines();
        assertEquals(1, orderLines.size());
    }

    @Test
    public void testGetAllOrderLines() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine2);
        repository.addOrderLine(orderLine3);

        List<OrderLine> orderLines = repository.getAllOrderLines();

        assertEquals(3, orderLines.size());
        assertTrue(orderLines.contains(orderLine1));
        assertTrue(orderLines.contains(orderLine2));
        assertTrue(orderLines.contains(orderLine3));
    }

    @Test
    public void testGetAllOrderLinesEmpty() {
        List<OrderLine> orderLines = repository.getAllOrderLines();

        assertNotNull(orderLines);
        assertEquals(0, orderLines.size());
    }

    @Test
    public void testGetOrderLineByIdFound() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine2);

        OrderLine result = repository.getOrderLineById(1);

        assertNotNull(result);
        assertEquals(orderLine1, result);
    }

    @Test
    public void testGetOrderLineByIdNotFound() {
        repository.addOrderLine(orderLine1);

        OrderLine result = repository.getOrderLineById(999);

        assertNull(result);
    }

    @Test
    public void testGetOrderLinesByOrderId() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine2);
        repository.addOrderLine(orderLine3);

        List<OrderLine> result = repository.getOrderLinesByOrderId(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(orderLine1));
        assertTrue(result.contains(orderLine2));
        assertFalse(result.contains(orderLine3));
    }

    @Test
    public void testGetOrderLinesByOrderIdEmpty() {
        repository.addOrderLine(orderLine1);

        List<OrderLine> result = repository.getOrderLinesByOrderId(999);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetOrderLinesByOrderIdWithNull() {
        repository.addOrderLine(orderLine1);

        List<OrderLine> result = repository.getOrderLinesByOrderId(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testUpdateOrderLineSuccess() {
        repository.addOrderLine(orderLine1);

        OrderLine updatedOrderLine = new OrderLine(1, order1, item1, 25.0);

        boolean result = repository.updateOrderLine(1, updatedOrderLine);

        assertTrue(result);
        OrderLine retrievedOrderLine = repository.getOrderLineById(1);
        assertNotNull(retrievedOrderLine);
        assertEquals(Double.valueOf(25.0), retrievedOrderLine.getQty());
    }

    @Test
    public void testUpdateOrderLineNotFound() {
        repository.addOrderLine(orderLine1);

        boolean result = repository.updateOrderLine(999, orderLine2);

        assertFalse(result);
    }

    @Test
    public void testDeleteOrderLineSuccess() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine2);
        repository.addOrderLine(orderLine3);

        boolean result = repository.deleteOrderLine(2);

        assertTrue(result);
        assertEquals(2, repository.getAllOrderLines().size());
        assertNull(repository.getOrderLineById(2));
    }

    @Test
    public void testDeleteOrderLineNotFound() {
        repository.addOrderLine(orderLine1);

        boolean result = repository.deleteOrderLine(999);

        assertFalse(result);
        assertEquals(1, repository.getAllOrderLines().size());
    }

    @Test
    public void testGetNextOrderLineId() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine2);

        Integer nextId = repository.getNextOrderLineId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextOrderLineIdEmpty() {
        Integer nextId = repository.getNextOrderLineId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testClear() {
        repository.addOrderLine(orderLine1);
        repository.addOrderLine(orderLine2);
        repository.addOrderLine(orderLine3);
        assertEquals(3, repository.getAllOrderLines().size());

        repository.clear();

        assertEquals(0, repository.getAllOrderLines().size());
    }

    @Test
    public void testConstructor() {
        OrderLineRepository newRepository = new OrderLineRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllOrderLines());
    }
}

