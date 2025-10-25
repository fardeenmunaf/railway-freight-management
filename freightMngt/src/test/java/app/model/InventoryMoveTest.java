package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test class for InventoryMove model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class InventoryMoveTest {

    private InventoryMove inventoryMove;

    @Before
    public void setUp() {
        inventoryMove = new InventoryMove();
    }

    @Test
    public void testDefaultConstructor() {
        InventoryMove defaultMove = new InventoryMove();

        assertNull("Move ID should be null", defaultMove.getMoveId());
        assertNull("Lot should be null", defaultMove.getLot());
        assertNull("From bay should be null", defaultMove.getFromBay());
        assertNull("To bay should be null", defaultMove.getToBay());
        assertNull("Qty should be null", defaultMove.getQty());
        assertNull("Reason should be null", defaultMove.getReason());
        assertNotNull("Created at should not be null", defaultMove.getCreatedAt());
    }

    @Test
    public void testParameterizedConstructor() {
        InventoryLot lot = new InventoryLot();
        WarehouseBay fromBay = new WarehouseBay();
        WarehouseBay toBay = new WarehouseBay();
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 24, 15, 30);

        InventoryMove paramMove = new InventoryMove(1, lot, fromBay, toBay, 50, "Relocation", createdAt);

        assertEquals("Move ID should be 1", Integer.valueOf(1), paramMove.getMoveId());
        assertEquals("Lot should match", lot, paramMove.getLot());
        assertEquals("From bay should match", fromBay, paramMove.getFromBay());
        assertEquals("To bay should match", toBay, paramMove.getToBay());
        assertEquals("Qty should be 50", Integer.valueOf(50), paramMove.getQty());
        assertEquals("Reason should be 'Relocation'", "Relocation", paramMove.getReason());
        assertEquals("Created at should match", createdAt, paramMove.getCreatedAt());
    }

    @Test
    public void testSetAndGetMoveId() {
        inventoryMove.setMoveId(10);

        assertEquals("Move ID should be 10", Integer.valueOf(10), inventoryMove.getMoveId());
    }

    @Test
    public void testSetAndGetLot() {
        InventoryLot lot = new InventoryLot();
        inventoryMove.setLot(lot);

        assertEquals("Lot should match", lot, inventoryMove.getLot());
    }

    @Test
    public void testSetAndGetFromBay() {
        WarehouseBay fromBay = new WarehouseBay();
        inventoryMove.setFromBayId(fromBay);

        assertEquals("From bay should match", fromBay, inventoryMove.getFromBay());
    }

    @Test
    public void testSetAndGetToBay() {
        WarehouseBay toBay = new WarehouseBay();
        inventoryMove.setToBay(toBay);

        assertEquals("To bay should match", toBay, inventoryMove.getToBay());
    }

    @Test
    public void testSetAndGetQty() {
        inventoryMove.setQty(100);

        assertEquals("Qty should be 100", Integer.valueOf(100), inventoryMove.getQty());
    }

    @Test
    public void testSetAndGetReason() {
        inventoryMove.setReason("Quality Check");

        assertEquals("Reason should be 'Quality Check'", "Quality Check", inventoryMove.getReason());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 11, 15, 9, 45);
        inventoryMove.setCreatedAt(createdAt);

        assertEquals("Created at should match", createdAt, inventoryMove.getCreatedAt());
    }

    @Test
    public void testSetMoveIdToNull() {
        inventoryMove.setMoveId(5);
        inventoryMove.setMoveId(null);

        assertNull("Move ID should be null", inventoryMove.getMoveId());
    }

    @Test
    public void testSetLotToNull() {
        InventoryLot lot = new InventoryLot();
        inventoryMove.setLot(lot);
        inventoryMove.setLot(null);

        assertNull("Lot should be null", inventoryMove.getLot());
    }

    @Test
    public void testSetFromBayToNull() {
        WarehouseBay fromBay = new WarehouseBay();
        inventoryMove.setFromBayId(fromBay);
        inventoryMove.setFromBayId(null);

        assertNull("From bay should be null", inventoryMove.getFromBay());
    }

    @Test
    public void testSetToBayToNull() {
        WarehouseBay toBay = new WarehouseBay();
        inventoryMove.setToBay(toBay);
        inventoryMove.setToBay(null);

        assertNull("To bay should be null", inventoryMove.getToBay());
    }

    @Test
    public void testSetReasonToNull() {
        inventoryMove.setReason("Test");
        inventoryMove.setReason(null);

        assertNull("Reason should be null", inventoryMove.getReason());
    }

    @Test
    public void testSetCreatedAtToNull() {
        LocalDateTime createdAt = LocalDateTime.now();
        inventoryMove.setCreatedAt(createdAt);
        inventoryMove.setCreatedAt(null);

        assertNull("Created at should be null", inventoryMove.getCreatedAt());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        InventoryLot lot = new InventoryLot();
        WarehouseBay fromBay = new WarehouseBay();
        WarehouseBay toBay = new WarehouseBay();
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 20, 14, 0);

        inventoryMove.setMoveId(3);
        inventoryMove.setLot(lot);
        inventoryMove.setFromBayId(fromBay);
        inventoryMove.setToBay(toBay);
        inventoryMove.setQty(75);
        inventoryMove.setReason("Transfer");
        inventoryMove.setCreatedAt(createdAt);

        assertEquals("Move ID should be 3", Integer.valueOf(3), inventoryMove.getMoveId());
        assertEquals("Lot should match", lot, inventoryMove.getLot());
        assertEquals("From bay should match", fromBay, inventoryMove.getFromBay());
        assertEquals("To bay should match", toBay, inventoryMove.getToBay());
        assertEquals("Qty should be 75", Integer.valueOf(75), inventoryMove.getQty());
        assertEquals("Reason should be 'Transfer'", "Transfer", inventoryMove.getReason());
        assertEquals("Created at should match", createdAt, inventoryMove.getCreatedAt());
    }

    @Test
    public void testConstructorWithNullValues() {
        InventoryMove nullMove = new InventoryMove(null, null, null, null, null, null, null);

        assertNull("Move ID should be null", nullMove.getMoveId());
        assertNull("Lot should be null", nullMove.getLot());
        assertNull("From bay should be null", nullMove.getFromBay());
        assertNull("To bay should be null", nullMove.getToBay());
        assertNull("Qty should be null", nullMove.getQty());
        assertNull("Reason should be null", nullMove.getReason());
        assertNull("Created at should be null", nullMove.getCreatedAt());
    }

    @Test
    public void testToString() {
        inventoryMove.setMoveId(7);
        inventoryMove.setQty(150);

        String result = inventoryMove.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain moveId", result.contains("moveId=7"));
        assertTrue("toString should contain qty", result.contains("qty=150"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = inventoryMove.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'InventoryMove{'", result.contains("InventoryMove{"));
    }
}

