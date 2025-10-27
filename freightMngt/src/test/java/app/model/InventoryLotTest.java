package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for InventoryLot model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class InventoryLotTest {

    private InventoryLot inventoryLot;

    @Before
    public void setUp() {
        inventoryLot = new InventoryLot();
    }

    @Test
    public void testDefaultConstructor() {
        InventoryLot defaultLot = new InventoryLot();

        assertNull("Lot ID should be null", defaultLot.getLotId());
        assertNull("SKU should be null", defaultLot.getSku());
        assertNull("Qty should be null", defaultLot.getQty());
        assertNull("Expiry date should be null", defaultLot.getExpDate());
        assertNull("Received date should be null", defaultLot.getReceivedDate());
    }

    @Test
    public void testParameterizedConstructor() {
        LocalDateTime expDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        LocalDateTime receivedDate = LocalDateTime.of(2025, 1, 15, 10, 30);

        InventoryLot paramLot = new InventoryLot(1, 23, 100, expDate, receivedDate);

        assertEquals("Lot ID should be 1", Integer.valueOf(1), paramLot.getLotId());
        assertEquals("SKU should be 23", Integer.valueOf(23), paramLot.getSku());
        assertEquals("Qty should be 100", Integer.valueOf(100), paramLot.getQty());
        assertEquals("Expiry date should match", expDate, paramLot.getExpDate());
        assertEquals("Received date should match", receivedDate, paramLot.getReceivedDate());
    }

    @Test
    public void testSetAndGetLotId() {
        inventoryLot.setLotId(10);

        assertEquals("Lot ID should be 10", Integer.valueOf(10), inventoryLot.getLotId());
    }

    @Test
    public void testSetAndGetSku() {
        inventoryLot.setSku(42);

        assertEquals("SKU should be 42", Integer.valueOf(42), inventoryLot.getSku());
    }

    @Test
    public void testSetAndGetQty() {
        inventoryLot.setQty(250);

        assertEquals("Qty should be 250", Integer.valueOf(250), inventoryLot.getQty());
    }

    @Test
    public void testSetAndGetExpDate() {
        LocalDateTime expDate = LocalDateTime.of(2026, 6, 30, 0, 0);
        inventoryLot.setExpDate(expDate);

        assertEquals("Expiry date should match", expDate, inventoryLot.getExpDate());
    }

    @Test
    public void testSetAndGetReceivedDate() {
        LocalDateTime receivedDate = LocalDateTime.of(2025, 2, 20, 14, 45);
        inventoryLot.setReceivedDate(receivedDate);

        assertEquals("Received date should match", receivedDate, inventoryLot.getReceivedDate());
    }

    @Test
    public void testSetLotIdToNull() {
        inventoryLot.setLotId(5);
        inventoryLot.setLotId(null);

        assertNull("Lot ID should be null", inventoryLot.getLotId());
    }

    @Test
    public void testSetSkuToNull() {
        inventoryLot.setSku(20);
        inventoryLot.setSku(null);

        assertNull("SKU should be null", inventoryLot.getSku());
    }

    @Test
    public void testSetQtyToNull() {
        inventoryLot.setQty(100);
        inventoryLot.setQty(null);

        assertNull("Qty should be null", inventoryLot.getQty());
    }

    @Test
    public void testSetExpDateToNull() {
        LocalDateTime expDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        inventoryLot.setExpDate(expDate);
        inventoryLot.setExpDate(null);

        assertNull("Expiry date should be null", inventoryLot.getExpDate());
    }

    @Test
    public void testSetReceivedDateToNull() {
        LocalDateTime receivedDate = LocalDateTime.now();
        inventoryLot.setReceivedDate(receivedDate);
        inventoryLot.setReceivedDate(null);

        assertNull("Received date should be null", inventoryLot.getReceivedDate());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        LocalDateTime expDate = LocalDateTime.of(2025, 11, 30, 23, 59);
        LocalDateTime receivedDate = LocalDateTime.of(2025, 1, 10, 8, 0);

        inventoryLot.setLotId(3);
        inventoryLot.setSku(15);
        inventoryLot.setQty(75);
        inventoryLot.setExpDate(expDate);
        inventoryLot.setReceivedDate(receivedDate);

        assertEquals("Lot ID should be 3", Integer.valueOf(3), inventoryLot.getLotId());
        assertEquals("SKU should be 15", Integer.valueOf(15), inventoryLot.getSku());
        assertEquals("Qty should be 75", Integer.valueOf(75), inventoryLot.getQty());
        assertEquals("Expiry date should match", expDate, inventoryLot.getExpDate());
        assertEquals("Received date should match", receivedDate, inventoryLot.getReceivedDate());
    }

    @Test
    public void testConstructorWithNullValues() {
        InventoryLot nullLot = new InventoryLot(null, null, null, null, null);

        assertNull("Lot ID should be null", nullLot.getLotId());
        assertNull("SKU should be null", nullLot.getSku());
        assertNull("Qty should be null", nullLot.getQty());
        assertNull("Expiry date should be null", nullLot.getExpDate());
        assertNull("Received date should be null", nullLot.getReceivedDate());
    }

    @Test
    public void testConstructorWithNullExpDate() {
        LocalDateTime receivedDate = LocalDateTime.now();
        InventoryLot lotWithNullExpDate = new InventoryLot(1, 23, 100, null, receivedDate);

        assertEquals("Lot ID should be 1", Integer.valueOf(1), lotWithNullExpDate.getLotId());
        assertNull("Expiry date should be null", lotWithNullExpDate.getExpDate());
        assertEquals("Received date should match", receivedDate, lotWithNullExpDate.getReceivedDate());
    }

    @Test
    public void testToString() {
        LocalDateTime expDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        LocalDateTime receivedDate = LocalDateTime.of(2025, 1, 15, 10, 30);

        inventoryLot.setLotId(7);
        inventoryLot.setSku(23);
        inventoryLot.setQty(150);
        inventoryLot.setExpDate(expDate);
        inventoryLot.setReceivedDate(receivedDate);

        String result = inventoryLot.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain lotId", result.contains("lotId=7"));
        assertTrue("toString should contain sku", result.contains("sku=23"));
        assertTrue("toString should contain qty", result.contains("qty=150"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = inventoryLot.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'InventoryLot{'", result.contains("InventoryLot{"));
    }
}

