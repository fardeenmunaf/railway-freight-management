package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for QaInspection model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class QaInspectionTest {

    private QaInspection qaInspection;

    @Before
    public void setUp() {
        qaInspection = new QaInspection();
    }

    @Test
    public void testDefaultConstructor() {
        QaInspection defaultInspection = new QaInspection();

        assertNull("QA ID should be null", defaultInspection.getQaId());
        assertNull("Return Line ID should be null", defaultInspection.getReturnLineId());
        assertNull("Status should be null", defaultInspection.getStatus());
        assertNull("Inspected At should be null", defaultInspection.getInspectedAt());
    }

    @Test
    public void testParameterizedConstructor() {
        LocalDateTime inspectedAt = LocalDateTime.of(2025, 10, 24, 14, 30);
        QaInspection paramInspection = new QaInspection(1, 100, "Approved", inspectedAt);

        assertEquals("QA ID should be 1", Integer.valueOf(1), paramInspection.getQaId());
        assertEquals("Return Line ID should be 100", Integer.valueOf(100), paramInspection.getReturnLineId());
        assertEquals("Status should be 'Approved'", "Approved", paramInspection.getStatus());
        assertEquals("Inspected At should match", inspectedAt, paramInspection.getInspectedAt());
    }

    @Test
    public void testSetAndGetQaId() {
        qaInspection.setQaId(10);

        assertEquals("QA ID should be 10", Integer.valueOf(10), qaInspection.getQaId());
    }

    @Test
    public void testSetAndGetReturnLineId() {
        qaInspection.setReturnLineId(200);

        assertEquals("Return Line ID should be 200", Integer.valueOf(200), qaInspection.getReturnLineId());
    }

    @Test
    public void testSetAndGetStatus() {
        qaInspection.setStatus("Rejected");

        assertEquals("Status should be 'Rejected'", "Rejected", qaInspection.getStatus());
    }

    @Test
    public void testSetAndGetInspectedAt() {
        LocalDateTime inspectedAt = LocalDateTime.of(2025, 11, 1, 10, 0);
        qaInspection.setInspectedAt(inspectedAt);

        assertEquals("Inspected At should match", inspectedAt, qaInspection.getInspectedAt());
    }

    @Test
    public void testSetQaIdToNull() {
        qaInspection.setQaId(5);
        qaInspection.setQaId(null);

        assertNull("QA ID should be null", qaInspection.getQaId());
    }

    @Test
    public void testSetReturnLineIdToNull() {
        qaInspection.setReturnLineId(150);
        qaInspection.setReturnLineId(null);

        assertNull("Return Line ID should be null", qaInspection.getReturnLineId());
    }

    @Test
    public void testSetStatusToNull() {
        qaInspection.setStatus("Pending");
        qaInspection.setStatus(null);

        assertNull("Status should be null", qaInspection.getStatus());
    }

    @Test
    public void testSetInspectedAtToNull() {
        LocalDateTime inspectedAt = LocalDateTime.now();
        qaInspection.setInspectedAt(inspectedAt);
        qaInspection.setInspectedAt(null);

        assertNull("Inspected At should be null", qaInspection.getInspectedAt());
    }

    @Test
    public void testConstructorWithNullValues() {
        QaInspection nullInspection = new QaInspection(null, null, null, null);

        assertNull("QA ID should be null", nullInspection.getQaId());
        assertNull("Return Line ID should be null", nullInspection.getReturnLineId());
        assertNull("Status should be null", nullInspection.getStatus());
        assertNull("Inspected At should be null", nullInspection.getInspectedAt());
    }

    @Test
    public void testToString() {
        LocalDateTime inspectedAt = LocalDateTime.of(2025, 10, 24, 15, 0);
        qaInspection.setQaId(7);
        qaInspection.setReturnLineId(250);
        qaInspection.setStatus("Passed");
        qaInspection.setInspectedAt(inspectedAt);

        String result = qaInspection.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain qaId", result.contains("qaId=7"));
        assertTrue("toString should contain returnLineId", result.contains("returnLineId=250"));
        assertTrue("toString should contain status", result.contains("status='Passed'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = qaInspection.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'QaInspection{'", result.contains("QaInspection{"));
    }
}

