package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for ReturnOrder model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class ReturnOrderTest {

    private ReturnOrder returnOrder;

    @Before
    public void setUp() {
        returnOrder = new ReturnOrder();
    }

    @Test
    public void testDefaultConstructor() {
        ReturnOrder defaultOrder = new ReturnOrder();

        assertNull("Return ID should be null", defaultOrder.getReturnId());
        assertNull("Orig Order should be null", defaultOrder.getOrigOrder());
        assertNull("Reason should be null", defaultOrder.getReason());
        assertNull("Status should be null", defaultOrder.getStatus());
        assertNull("Received At should be null", defaultOrder.getReceivedAt());
    }

    @Test
    public void testParameterizedConstructor() {
        Order origOrder = new Order();
        LocalDateTime receivedAt = LocalDateTime.of(2025, 10, 24, 10, 30);

        ReturnOrder paramOrder = new ReturnOrder(1, origOrder, "Defective", "Pending", receivedAt);

        assertEquals("Return ID should be 1", Integer.valueOf(1), paramOrder.getReturnId());
        assertEquals("Orig Order should match", origOrder, paramOrder.getOrigOrder());
        assertEquals("Reason should be 'Defective'", "Defective", paramOrder.getReason());
        assertEquals("Status should be 'Pending'", "Pending", paramOrder.getStatus());
        assertEquals("Received At should match", receivedAt, paramOrder.getReceivedAt());
    }

    @Test
    public void testSetAndGetReturnId() {
        returnOrder.setReturnId(10);

        assertEquals("Return ID should be 10", Integer.valueOf(10), returnOrder.getReturnId());
    }

    @Test
    public void testSetAndGetOrigOrder() {
        Order origOrder = new Order();
        returnOrder.setOrigOrder(origOrder);

        assertEquals("Orig Order should match", origOrder, returnOrder.getOrigOrder());
    }

    @Test
    public void testSetAndGetReason() {
        returnOrder.setReason("Wrong Item");

        assertEquals("Reason should be 'Wrong Item'", "Wrong Item", returnOrder.getReason());
    }

    @Test
    public void testSetAndGetStatus() {
        returnOrder.setStatus("Approved");

        assertEquals("Status should be 'Approved'", "Approved", returnOrder.getStatus());
    }

    @Test
    public void testSetAndGetReceivedAt() {
        LocalDateTime receivedAt = LocalDateTime.of(2025, 11, 1, 14, 0);
        returnOrder.setReceivedAt(receivedAt);

        assertEquals("Received At should match", receivedAt, returnOrder.getReceivedAt());
    }

    @Test
    public void testSetReturnIdToNull() {
        returnOrder.setReturnId(5);
        returnOrder.setReturnId(null);

        assertNull("Return ID should be null", returnOrder.getReturnId());
    }

    @Test
    public void testSetOrigOrderToNull() {
        Order origOrder = new Order();
        returnOrder.setOrigOrder(origOrder);
        returnOrder.setOrigOrder(null);

        assertNull("Orig Order should be null", returnOrder.getOrigOrder());
    }

    @Test
    public void testSetReasonToNull() {
        returnOrder.setReason("Damaged");
        returnOrder.setReason(null);

        assertNull("Reason should be null", returnOrder.getReason());
    }

    @Test
    public void testSetStatusToNull() {
        returnOrder.setStatus("Rejected");
        returnOrder.setStatus(null);

        assertNull("Status should be null", returnOrder.getStatus());
    }

    @Test
    public void testSetReceivedAtToNull() {
        LocalDateTime receivedAt = LocalDateTime.now();
        returnOrder.setReceivedAt(receivedAt);
        returnOrder.setReceivedAt(null);

        assertNull("Received At should be null", returnOrder.getReceivedAt());
    }

    @Test
    public void testConstructorWithNullValues() {
        ReturnOrder nullOrder = new ReturnOrder(null, null, null, null, null);

        assertNull("Return ID should be null", nullOrder.getReturnId());
        assertNull("Orig Order should be null", nullOrder.getOrigOrder());
        assertNull("Reason should be null", nullOrder.getReason());
        assertNull("Status should be null", nullOrder.getStatus());
        assertNull("Received At should be null", nullOrder.getReceivedAt());
    }

    @Test
    public void testToString() {
        Order origOrder = new Order();
        LocalDateTime receivedAt = LocalDateTime.of(2025, 10, 24, 16, 0);

        returnOrder.setReturnId(7);
        returnOrder.setOrigOrder(origOrder);
        returnOrder.setReason("Not as described");
        returnOrder.setStatus("Completed");
        returnOrder.setReceivedAt(receivedAt);

        String result = returnOrder.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain returnId", result.contains("returnId=7"));
        assertTrue("toString should contain reason", result.contains("reason='Not as described'"));
        assertTrue("toString should contain status", result.contains("status='Completed'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = returnOrder.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'ReturnOrder{'", result.contains("ReturnOrder{"));
    }
}

