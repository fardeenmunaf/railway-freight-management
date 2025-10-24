package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for QuarantineHold model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class QuarantineHoldTest {

    private QuarantineHold quarantineHold;

    @Before
    public void setUp() {
        quarantineHold = new QuarantineHold();
    }

    @Test
    public void testDefaultConstructor() {
        QuarantineHold defaultHold = new QuarantineHold();

        assertNull("Qhold ID should be null", defaultHold.getQholdId());
        assertNull("Return Order should be null", defaultHold.getReturnOrder());
        assertNull("Status should be null", defaultHold.getStatus());
        assertNotNull("Created At should not be null", defaultHold.getCreatedAt());
        assertNull("Released At should be null", defaultHold.getReleasedAt());
    }

    @Test
    public void testParameterizedConstructor() {
        ReturnOrder returnOrder = new ReturnOrder();
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 24, 10, 0);
        LocalDateTime releasedAt = LocalDateTime.of(2025, 10, 25, 14, 30);

        QuarantineHold paramHold = new QuarantineHold(1, returnOrder, "Active", createdAt, releasedAt);

        assertEquals("Qhold ID should be 1", Integer.valueOf(1), paramHold.getQholdId());
        assertEquals("Return Order should match", returnOrder, paramHold.getReturnOrder());
        assertEquals("Status should be 'Active'", "Active", paramHold.getStatus());
        assertEquals("Created At should match", createdAt, paramHold.getCreatedAt());
        assertEquals("Released At should match", releasedAt, paramHold.getReleasedAt());
    }

    @Test
    public void testSetAndGetQholdId() {
        quarantineHold.setQholdId(10);

        assertEquals("Qhold ID should be 10", Integer.valueOf(10), quarantineHold.getQholdId());
    }

    @Test
    public void testSetAndGetReturnOrder() {
        ReturnOrder returnOrder = new ReturnOrder();
        quarantineHold.setReturnOrder(returnOrder);

        assertEquals("Return Order should match", returnOrder, quarantineHold.getReturnOrder());
    }

    @Test
    public void testSetAndGetStatus() {
        quarantineHold.setStatus("Released");

        assertEquals("Status should be 'Released'", "Released", quarantineHold.getStatus());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 11, 1, 9, 0);
        quarantineHold.setCreatedAt(createdAt);

        assertEquals("Created At should match", createdAt, quarantineHold.getCreatedAt());
    }

    @Test
    public void testSetAndGetReleasedAt() {
        LocalDateTime releasedAt = LocalDateTime.of(2025, 11, 2, 15, 0);
        quarantineHold.setReleasedAt(releasedAt);

        assertEquals("Released At should match", releasedAt, quarantineHold.getReleasedAt());
    }

    @Test
    public void testSetQholdIdToNull() {
        quarantineHold.setQholdId(5);
        quarantineHold.setQholdId(null);

        assertNull("Qhold ID should be null", quarantineHold.getQholdId());
    }

    @Test
    public void testSetReturnOrderToNull() {
        ReturnOrder returnOrder = new ReturnOrder();
        quarantineHold.setReturnOrder(returnOrder);
        quarantineHold.setReturnOrder(null);

        assertNull("Return Order should be null", quarantineHold.getReturnOrder());
    }

    @Test
    public void testSetStatusToNull() {
        quarantineHold.setStatus("Pending");
        quarantineHold.setStatus(null);

        assertNull("Status should be null", quarantineHold.getStatus());
    }

    @Test
    public void testSetCreatedAtToNull() {
        LocalDateTime createdAt = LocalDateTime.now();
        quarantineHold.setCreatedAt(createdAt);
        quarantineHold.setCreatedAt(null);

        assertNull("Created At should be null", quarantineHold.getCreatedAt());
    }

    @Test
    public void testSetReleasedAtToNull() {
        LocalDateTime releasedAt = LocalDateTime.now();
        quarantineHold.setReleasedAt(releasedAt);
        quarantineHold.setReleasedAt(null);

        assertNull("Released At should be null", quarantineHold.getReleasedAt());
    }

    @Test
    public void testConstructorWithNullValues() {
        QuarantineHold nullHold = new QuarantineHold(null, null, null, null, null);

        assertNull("Qhold ID should be null", nullHold.getQholdId());
        assertNull("Return Order should be null", nullHold.getReturnOrder());
        assertNull("Status should be null", nullHold.getStatus());
        assertNull("Created At should be null", nullHold.getCreatedAt());
        assertNull("Released At should be null", nullHold.getReleasedAt());
    }

    @Test
    public void testToString() {
        ReturnOrder returnOrder = new ReturnOrder();
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 24, 12, 0);
        LocalDateTime releasedAt = LocalDateTime.of(2025, 10, 25, 16, 0);

        quarantineHold.setQholdId(7);
        quarantineHold.setReturnOrder(returnOrder);
        quarantineHold.setStatus("Resolved");
        quarantineHold.setCreatedAt(createdAt);
        quarantineHold.setReleasedAt(releasedAt);

        String result = quarantineHold.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain qholdId", result.contains("qholdId=7"));
        assertTrue("toString should contain status", result.contains("status='Resolved'"));
    }

    @Test
    public void testToStringWithNullValues() {
        quarantineHold.setCreatedAt(null);

        String result = quarantineHold.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'QuarantineHold{'", result.contains("QuarantineHold{"));
    }
}

