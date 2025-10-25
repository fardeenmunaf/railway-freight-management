package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for OrderLine model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class OrderLineTest {

    private OrderLine orderLine;

    @Before
    public void setUp() {
        orderLine = new OrderLine();
    }

    @Test
    public void testDefaultConstructor() {
        OrderLine defaultOrderLine = new OrderLine();

        assertNull("Order Line ID should be null", defaultOrderLine.getOrderLineId());
        assertNull("Order should be null", defaultOrderLine.getOrder());
        assertNull("SKU should be null", defaultOrderLine.getSku());
        assertNull("Qty should be null", defaultOrderLine.getQty());
    }

    @Test
    public void testParameterizedConstructor() {
        Order order = new Order();
        Item item = new Item();

        OrderLine paramOrderLine = new OrderLine(1, order, item, 50.0);

        assertEquals("Order Line ID should be 1", Integer.valueOf(1), paramOrderLine.getOrderLineId());
        assertEquals("Order should match", order, paramOrderLine.getOrder());
        assertEquals("SKU should match", item, paramOrderLine.getSku());
        assertEquals("Qty should be 50.0", Double.valueOf(50.0), paramOrderLine.getQty());
    }

    @Test
    public void testSetAndGetOrderLineId() {
        orderLine.setOrderLineId(10);

        assertEquals("Order Line ID should be 10", Integer.valueOf(10), orderLine.getOrderLineId());
    }

    @Test
    public void testSetAndGetOrder() {
        Order order = new Order();
        orderLine.setOrder(order);

        assertEquals("Order should match", order, orderLine.getOrder());
    }

    @Test
    public void testSetAndGetSku() {
        Item item = new Item();
        orderLine.setSku(item);

        assertEquals("SKU should match", item, orderLine.getSku());
    }

    @Test
    public void testSetAndGetQty() {
        orderLine.setQty(100.5);

        assertEquals("Qty should be 100.5", Double.valueOf(100.5), orderLine.getQty());
    }

    @Test
    public void testSetOrderLineIdToNull() {
        orderLine.setOrderLineId(5);
        orderLine.setOrderLineId(null);

        assertNull("Order Line ID should be null", orderLine.getOrderLineId());
    }

    @Test
    public void testSetOrderToNull() {
        Order order = new Order();
        orderLine.setOrder(order);
        orderLine.setOrder(null);

        assertNull("Order should be null", orderLine.getOrder());
    }

    @Test
    public void testSetSkuToNull() {
        Item item = new Item();
        orderLine.setSku(item);
        orderLine.setSku(null);

        assertNull("SKU should be null", orderLine.getSku());
    }

    @Test
    public void testSetQtyToNull() {
        orderLine.setQty(50.0);
        orderLine.setQty(null);

        assertNull("Qty should be null", orderLine.getQty());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        Order order = new Order();
        Item item = new Item();

        orderLine.setOrderLineId(3);
        orderLine.setOrder(order);
        orderLine.setSku(item);
        orderLine.setQty(75.25);

        assertEquals("Order Line ID should be 3", Integer.valueOf(3), orderLine.getOrderLineId());
        assertEquals("Order should match", order, orderLine.getOrder());
        assertEquals("SKU should match", item, orderLine.getSku());
        assertEquals("Qty should be 75.25", Double.valueOf(75.25), orderLine.getQty());
    }

    @Test
    public void testConstructorWithNullValues() {
        OrderLine nullOrderLine = new OrderLine(null, null, null, null);

        assertNull("Order Line ID should be null", nullOrderLine.getOrderLineId());
        assertNull("Order should be null", nullOrderLine.getOrder());
        assertNull("SKU should be null", nullOrderLine.getSku());
        assertNull("Qty should be null", nullOrderLine.getQty());
    }

    @Test
    public void testConstructorWithZeroQty() {
        Order order = new Order();
        Item item = new Item();

        OrderLine orderLineWithZeroQty = new OrderLine(1, order, item, 0.0);

        assertEquals("Qty should be 0.0", Double.valueOf(0.0), orderLineWithZeroQty.getQty());
    }

    @Test
    public void testToString() {
        Order order = new Order();
        Item item = new Item();

        orderLine.setOrderLineId(7);
        orderLine.setOrder(order);
        orderLine.setSku(item);
        orderLine.setQty(125.5);

        String result = orderLine.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain orderLineId", result.contains("orderLineId=7"));
        assertTrue("toString should contain qty", result.contains("qty=125.5"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = orderLine.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'OrderLine{'", result.contains("OrderLine{"));
    }
}

