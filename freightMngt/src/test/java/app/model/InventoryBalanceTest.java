package app.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for InventoryBalance model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class InventoryBalanceTest {

    private InventoryBalance inventoryBalance;

    @Before
    public void setUp() {
        inventoryBalance = new InventoryBalance();
    }

    @Test
    public void testDefaultConstructor() {
        InventoryBalance defaultBalance = new InventoryBalance();

        assertNull("Balance ID should be null", defaultBalance.getBalanceId());
        assertNull("Bay should be null", defaultBalance.getBay());
        assertNotNull("Lots list should not be null", defaultBalance.getLots());
        assertEquals("Lots list should be empty", 0, defaultBalance.getLots().size());
        assertEquals("Qty should be 0", Integer.valueOf(0), defaultBalance.getQty());
    }

    @Test
    public void testParameterizedConstructor() {
        WarehouseBay bay = new WarehouseBay();
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 23, 100, LocalDateTime.now(), LocalDateTime.now()));

        InventoryBalance paramBalance = new InventoryBalance(1, bay, lots, 100);

        assertEquals("Balance ID should be 1", Integer.valueOf(1), paramBalance.getBalanceId());
        assertEquals("Bay should match", bay, paramBalance.getBay());
        assertNotNull("Lots should not be null", paramBalance.getLots());
        assertEquals("Lots size should be 1", 1, paramBalance.getLots().size());
        assertEquals("Qty should be 100", Integer.valueOf(100), paramBalance.getQty());
    }

    @Test
    public void testParameterizedConstructorWithNullLots() {
        WarehouseBay bay = new WarehouseBay();

        InventoryBalance balanceWithNullLots = new InventoryBalance(1, bay, null, 50);

        assertNotNull("Lots should not be null even if passed as null", balanceWithNullLots.getLots());
        assertEquals("Lots list should be empty", 0, balanceWithNullLots.getLots().size());
    }

    @Test
    public void testSetAndGetBalanceId() {
        inventoryBalance.setBalanceId(10);

        assertEquals("Balance ID should be 10", Integer.valueOf(10), inventoryBalance.getBalanceId());
    }

    @Test
    public void testSetAndGetBay() {
        WarehouseBay bay = new WarehouseBay();
        inventoryBalance.setBayId(bay);

        assertEquals("Bay should match", bay, inventoryBalance.getBay());
    }

    @Test
    public void testSetAndGetLots() {
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 23, 50, LocalDateTime.now(), LocalDateTime.now()));

        inventoryBalance.setLots(lots);

        assertNotNull("Lots should not be null", inventoryBalance.getLots());
        assertEquals("Lots size should be 1", 1, inventoryBalance.getLots().size());
    }

    @Test
    public void testSetAndGetQty() {
        inventoryBalance.setQty(200);

        assertEquals("Qty should be 200", Integer.valueOf(200), inventoryBalance.getQty());
    }

    @Test
    public void testSetBalanceIdToNull() {
        inventoryBalance.setBalanceId(5);
        inventoryBalance.setBalanceId(null);

        assertNull("Balance ID should be null", inventoryBalance.getBalanceId());
    }

    @Test
    public void testSetBayToNull() {
        WarehouseBay bay = new WarehouseBay();
        inventoryBalance.setBayId(bay);
        inventoryBalance.setBayId(null);

        assertNull("Bay should be null", inventoryBalance.getBay());
    }

    @Test
    public void testSetLotsToNull() {
        List<InventoryLot> lots = new ArrayList<>();
        inventoryBalance.setLots(lots);
        inventoryBalance.setLots(null);

        assertNotNull("Lots should not be null even after setting to null", inventoryBalance.getLots());
        assertEquals("Lots should be empty", 0, inventoryBalance.getLots().size());
    }

    @Test
    public void testSetQtyToNull() {
        inventoryBalance.setQty(100);
        inventoryBalance.setQty(null);

        assertNull("Qty should be null", inventoryBalance.getQty());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        WarehouseBay bay = new WarehouseBay();
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(new InventoryLot(1, 23, 75, LocalDateTime.now(), LocalDateTime.now()));

        inventoryBalance.setBalanceId(3);
        inventoryBalance.setBayId(bay);
        inventoryBalance.setLots(lots);
        inventoryBalance.setQty(75);

        assertEquals("Balance ID should be 3", Integer.valueOf(3), inventoryBalance.getBalanceId());
        assertEquals("Bay should match", bay, inventoryBalance.getBay());
        assertEquals("Lots size should be 1", 1, inventoryBalance.getLots().size());
        assertEquals("Qty should be 75", Integer.valueOf(75), inventoryBalance.getQty());
    }

    @Test
    public void testConstructorWithNullValues() {
        InventoryBalance nullBalance = new InventoryBalance(null, null, null, null);

        assertNull("Balance ID should be null", nullBalance.getBalanceId());
        assertNull("Bay should be null", nullBalance.getBay());
        assertNotNull("Lots should not be null", nullBalance.getLots());
        assertEquals("Lots should be empty", 0, nullBalance.getLots().size());
        assertNull("Qty should be null", nullBalance.getQty());
    }

    @Test
    public void testLotsListIsIndependent() {
        List<InventoryLot> originalLots = new ArrayList<>();
        originalLots.add(new InventoryLot(1, 23, 100, LocalDateTime.now(), LocalDateTime.now()));

        InventoryBalance balance = new InventoryBalance(1, new WarehouseBay(), originalLots, 100);

        originalLots.add(new InventoryLot(2, 23, 50, LocalDateTime.now(), LocalDateTime.now()));

        assertEquals("Balance lots should still have 1 element", 1, balance.getLots().size());
    }

    @Test
    public void testToString() {
        inventoryBalance.setBalanceId(5);
        inventoryBalance.setQty(150);

        String result = inventoryBalance.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain balanceId", result.contains("balanceId=5"));
        assertTrue("toString should contain qty", result.contains("qty=150"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = inventoryBalance.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'InventoryBalance{'", result.contains("InventoryBalance{"));
    }

    @Test
    public void testAddLotWithValidLot() {
        InventoryLot lot = new InventoryLot(1, 23, 50, LocalDateTime.now(), LocalDateTime.now());
        inventoryBalance.setQty(100);

        inventoryBalance.addLot(lot);

        assertEquals("Lots size should be 1", 1, inventoryBalance.getLots().size());
        assertEquals("Qty should be updated to 150", Integer.valueOf(150), inventoryBalance.getQty());
    }

    @Test
    public void testAddLotWithNullLot() {
        inventoryBalance.setQty(100);
        int initialSize = inventoryBalance.getLots().size();

        inventoryBalance.addLot(null);

        assertEquals("Lots size should remain unchanged", initialSize, inventoryBalance.getLots().size());
        assertEquals("Qty should remain unchanged", Integer.valueOf(100), inventoryBalance.getQty());
    }

    @Test
    public void testAddMultipleLots() {
        InventoryLot lot1 = new InventoryLot(1, 23, 30, LocalDateTime.now(), LocalDateTime.now());
        InventoryLot lot2 = new InventoryLot(2, 23, 20, LocalDateTime.now(), LocalDateTime.now());
        inventoryBalance.setQty(0);

        inventoryBalance.addLot(lot1);
        inventoryBalance.addLot(lot2);

        assertEquals("Lots size should be 2", 2, inventoryBalance.getLots().size());
        assertEquals("Qty should be updated to 50", Integer.valueOf(50), inventoryBalance.getQty());
    }
}

