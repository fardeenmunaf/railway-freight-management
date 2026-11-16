package app.service.sprint1;

import app.controller.InventoryBalanceController;
import app.controller.InventoryLotController;
import app.controller.WarehouseBayController;
import app.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for InventoryDispatch service.
 * Tests the dispatch operation following FEFO/FIFO rules.
 */
public class InventoryDispatchTest {

    private InventoryDispatch inventoryDispatch;
    private InventoryBalanceController balanceController;
    private InventoryLotController lotController;
    private WarehouseBayController bayController;

    @Before
    public void setUp() {
        // Clear repositories
        balanceController = new InventoryBalanceController();
        lotController = new InventoryLotController();
        bayController = new WarehouseBayController();

        balanceController.clearAllInventoryBalances();
        bayController.clearAllWarehouseBays();
        app.repositories.Repositories.getInstance().getInventoryLotRepository().clear();

        inventoryDispatch = new InventoryDispatch();
    }

    /**
     * Helper method to create warehouse structure for tests.
     * Creates: Facility -> Warehouse -> Aisle -> Bays
     */
    private WarehouseBay[] createWarehouseStructure(Integer... bayIds) {
        // Create facility
        Facility facility = new Facility(1, "Test Facility",
            new FacilityType(1, "Warehouse"), 0.0, 0.0, false);

        // Create warehouse
        Warehouse warehouse = new Warehouse(1, facility, "Test Warehouse");

        // Create aisle
        WarehouseAisle aisle = new WarehouseAisle(1, warehouse, 1, "A-001");

        // Create bays
        WarehouseBay[] bays = new WarehouseBay[bayIds.length];
        for (int i = 0; i < bayIds.length; i++) {
            bays[i] = new WarehouseBay(bayIds[i], aisle, bayIds[i], 10); // capacity of 10 lots
            bayController.createWarehouseBay(bays[i]);
        }

        return bays;
    }

    /**
     * Test dispatching entire lot from a single balance.
     * Expected: Balance should be deleted after lot is fully consumed.
     */
    @Test
    public void testDispatchEntireLotFromSingleBalance() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay1 = bays[0];

        // Create a single lot with SKU 10, quantity 50
        InventoryLot lot1 = new InventoryLot(101, 10, 50,
            LocalDateTime.of(2099, 6, 1, 0, 0),      // expDate
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot1);
        InventoryLot lot1FromRepo = lotController.getInventoryLotById(101);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot1FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots, 50);
        balanceController.createInventoryBalance(balance1);

        // ===== EXECUTE =====
        boolean success = inventoryDispatch.dispatchStock(10, 50);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // Balance should be deleted (bay empty now)
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("No balances should remain", 0, balancesAfter.size());
    }

    /**
     * Test dispatching partial lot from a single balance.
     * Expected: Lot quantity should be reduced, balance should be updated.
     */
    @Test
    public void testDispatchPartialLotFromSingleBalance() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay1 = bays[0];

        // Create a lot with SKU 10, quantity 50
        InventoryLot lot1 = new InventoryLot(102, 10, 50,
            LocalDateTime.of(2099, 6, 1, 0, 0),      // expDate
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot1);
        InventoryLot lot1FromRepo = lotController.getInventoryLotById(102);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot1FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots, 50);
        balanceController.createInventoryBalance(balance1);

        // ===== EXECUTE =====
        boolean success = inventoryDispatch.dispatchStock(10, 30);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // Balance should still exist with reduced quantity
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());

        InventoryBalance balanceAfter = balancesAfter.get(0);
        assertEquals("Balance should have 1 lot", 1, balanceAfter.getLots().size());
        assertEquals("Balance total qty should be 20", Integer.valueOf(20), balanceAfter.getQty());
        assertEquals("Lot qty should be 20", Integer.valueOf(20), balanceAfter.getLots().get(0).getQty());
    }

    /**
     * Test dispatching with insufficient stock.
     * Expected: Dispatch should fail, nothing should be consumed.
     */
    @Test
    public void testDispatchInsufficientStock_ShouldFail() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay1 = bays[0];

        // Create a lot with SKU 10, quantity 30
        InventoryLot lot1 = new InventoryLot(103, 10, 30,
            LocalDateTime.of(2099, 6, 1, 0, 0),      // expDate
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot1);
        InventoryLot lot1FromRepo = lotController.getInventoryLotById(103);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot1FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots, 30);
        balanceController.createInventoryBalance(balance1);

        // ===== EXECUTE =====
        boolean success = inventoryDispatch.dispatchStock(10, 50); // Request more than available

        // ===== VERIFY =====
        assertFalse("Dispatch should fail", success);

        // Balance should remain unchanged
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());
        assertEquals("Balance qty should be unchanged (30)", Integer.valueOf(30), balancesAfter.get(0).getQty());
    }

    /**
     * Test dispatching with non-existent SKU.
     * Expected: Dispatch should fail.
     */
    @Test
    public void testDispatchNonExistentSKU_ShouldFail() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay1 = bays[0];

        // Create a lot with SKU 10
        InventoryLot lot1 = new InventoryLot(104, 10, 30,
            LocalDateTime.of(2099, 6, 1, 0, 0),      // expDate
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot1);
        InventoryLot lot1FromRepo = lotController.getInventoryLotById(104);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot1FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots, 30);
        balanceController.createInventoryBalance(balance1);

        // ===== EXECUTE =====
        boolean success = inventoryDispatch.dispatchStock(999, 10); // Non-existent SKU

        // ===== VERIFY =====
        assertFalse("Dispatch should fail", success);

        // Balance should remain unchanged
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());
        assertEquals("Balance qty should be unchanged", Integer.valueOf(30), balancesAfter.get(0).getQty());
    }

    /**
     * Test FEFO dispatch: lots with earlier expiry dates should be consumed first.
     * Creates two balances with same SKU but different expiry dates.
     * Expected: Earlier expiry lot should be consumed first.
     */
    @Test
    public void testDispatchFEFO_ConsumesEarliestExpiryFirst() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay bay1 = bays[0];
        WarehouseBay bay2 = bays[1];

        // Create lot with EARLY expiry (Feb 2026)
        InventoryLot lotEarly = new InventoryLot(201, 20, 30,
            LocalDateTime.of(2099, 2, 1, 0, 0),      // expDate (Feb 2026)
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        // Create lot with LATE expiry (Jun 2026)
        InventoryLot lotLate = new InventoryLot(202, 20, 40,
            LocalDateTime.of(2099, 6, 1, 0, 0),      // expDate (Jun 2026)
            LocalDateTime.of(2025, 10, 2, 10, 0));   // receivedDate

        lotController.createInventoryLot(lotEarly);
        lotController.createInventoryLot(lotLate);

        InventoryLot lotEarlyFromRepo = lotController.getInventoryLotById(201);
        InventoryLot lotLateFromRepo = lotController.getInventoryLotById(202);

        // Create balances
        List<InventoryLot> lotsEarly = new ArrayList<>();
        lotsEarly.add(lotEarlyFromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lotsEarly, 30);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lotsLate = new ArrayList<>();
        lotsLate.add(lotLateFromRepo);
        InventoryBalance balance2 = new InventoryBalance(2, bay2, lotsLate, 40);
        balanceController.createInventoryBalance(balance2);

        // ===== EXECUTE =====
        // Dispatch 30 units (should consume entire early lot)
        boolean success = inventoryDispatch.dispatchStock(20, 30);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // Only balance2 (late expiry) should remain
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());

        InventoryBalance remainingBalance = balancesAfter.get(0);
        assertEquals("Remaining balance should be balance2", Integer.valueOf(2), remainingBalance.getBalanceId());
        assertEquals("Remaining balance qty should be 40", Integer.valueOf(40), remainingBalance.getQty());
        assertEquals("Remaining lot should be lotLate (202)", Integer.valueOf(202), remainingBalance.getLots().get(0).getLotId());
    }

    /**
     * Test FEFO dispatch across multiple balances.
     * Expected: Consume from earliest expiry first, then next earliest, etc.
     */
    @Test
    public void testDispatchFEFO_AcrossMultipleBalances() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2, 3);
        WarehouseBay bay1 = bays[0];
        WarehouseBay bay2 = bays[1];
        WarehouseBay bay3 = bays[2];

        // Create lots with different expiry dates (SKU 30)
        // Lot 301: Expires Jan 2026, qty 20
        InventoryLot lot301 = new InventoryLot(301, 30, 20,
            LocalDateTime.of(2099, 1, 1, 0, 0),      // expDate (Jan 2026) - EARLIEST
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        // Lot 302: Expires Mar 2026, qty 25
        InventoryLot lot302 = new InventoryLot(302, 30, 25,
            LocalDateTime.of(2099, 3, 1, 0, 0),      // expDate (Mar 2026) - MIDDLE
            LocalDateTime.of(2025, 10, 2, 10, 0));   // receivedDate

        // Lot 303: Expires May 2026, qty 30
        InventoryLot lot303 = new InventoryLot(303, 30, 30,
            LocalDateTime.of(2099, 5, 1, 0, 0),      // expDate (May 2026) - LATEST
            LocalDateTime.of(2025, 10, 3, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot301);
        lotController.createInventoryLot(lot302);
        lotController.createInventoryLot(lot303);

        InventoryLot lot301FromRepo = lotController.getInventoryLotById(301);
        InventoryLot lot302FromRepo = lotController.getInventoryLotById(302);
        InventoryLot lot303FromRepo = lotController.getInventoryLotById(303);

        // Create balances (intentionally out of order to test sorting)
        List<InventoryLot> lots3 = new ArrayList<>();
        lots3.add(lot303FromRepo);
        InventoryBalance balance3 = new InventoryBalance(3, bay3, lots3, 30);
        balanceController.createInventoryBalance(balance3);

        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(lot301FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots1, 20);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(lot302FromRepo);
        InventoryBalance balance2 = new InventoryBalance(2, bay2, lots2, 25);
        balanceController.createInventoryBalance(balance2);

        // ===== EXECUTE =====
        // Dispatch 35 units (should consume lot301 entirely + 15 from lot302)
        boolean success = inventoryDispatch.dispatchStock(30, 35);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // Should have 2 balances remaining
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("Two balances should remain", 2, balancesAfter.size());

        // Find balances by bay
        InventoryBalance balance2After = null;
        InventoryBalance balance3After = null;
        for (InventoryBalance balance : balancesAfter) {
            if (balance.getBay().getBayId().equals(2)) {
                balance2After = balance;
            } else if (balance.getBay().getBayId().equals(3)) {
                balance3After = balance;
            }
        }

        assertNotNull("Balance2 should exist", balance2After);
        assertNotNull("Balance3 should exist", balance3After);

        // Balance2 should have 10 remaining (25 - 15)
        assertEquals("Balance2 qty should be 10", Integer.valueOf(10), balance2After.getQty());
        assertEquals("Lot302 qty should be 10", Integer.valueOf(10), balance2After.getLots().get(0).getQty());

        // Balance3 should be unchanged (30)
        assertEquals("Balance3 qty should be 30", Integer.valueOf(30), balance3After.getQty());
    }

    /**
     * Test FIFO dispatch: when lots have no expiry date, consume oldest received first.
     */
    @Test
    public void testDispatchFIFO_NoExpiryDate_ConsumesOldestFirst() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay bay1 = bays[0];
        WarehouseBay bay2 = bays[1];

        // Create lots with NO expiry dates but different received dates
        // Lot 401: Received earlier (Oct 1)
        InventoryLot lotOld = new InventoryLot(401, 40, 30,
            null,                                        // expDate = null
            LocalDateTime.of(2025, 10, 1, 10, 0));      // receivedDate (Oct 1)

        // Lot 402: Received later (Oct 15)
        InventoryLot lotNew = new InventoryLot(402, 40, 40,
            null,                                        // expDate = null
            LocalDateTime.of(2025, 10, 15, 10, 0));     // receivedDate (Oct 15)

        lotController.createInventoryLot(lotOld);
        lotController.createInventoryLot(lotNew);

        InventoryLot lotOldFromRepo = lotController.getInventoryLotById(401);
        InventoryLot lotNewFromRepo = lotController.getInventoryLotById(402);

        // Create balances
        List<InventoryLot> lotsOld = new ArrayList<>();
        lotsOld.add(lotOldFromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lotsOld, 30);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lotsNew = new ArrayList<>();
        lotsNew.add(lotNewFromRepo);
        InventoryBalance balance2 = new InventoryBalance(2, bay2, lotsNew, 40);
        balanceController.createInventoryBalance(balance2);

        // ===== EXECUTE =====
        // Dispatch 30 units (should consume entire old lot)
        boolean success = inventoryDispatch.dispatchStock(40, 30);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // Only balance2 (newer received) should remain
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());

        InventoryBalance remainingBalance = balancesAfter.get(0);
        assertEquals("Remaining balance should be balance2", Integer.valueOf(2), remainingBalance.getBalanceId());
        assertEquals("Remaining balance qty should be 40", Integer.valueOf(40), remainingBalance.getQty());
        assertEquals("Remaining lot should be lotNew (402)", Integer.valueOf(402), remainingBalance.getLots().get(0).getLotId());
    }

    /**
     * Test FEFO priority over FIFO: lots with expiry dates should be consumed before those without.
     */
    @Test
    public void testDispatchFEFO_HasPriorityOverFIFO() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay bay1 = bays[0];
        WarehouseBay bay2 = bays[1];

        // Create lot WITH expiry date (expires later but should be consumed first)
        InventoryLot lotWithExpiry = new InventoryLot(501, 50, 25,
            LocalDateTime.of(2099, 12, 31, 0, 0),      // expDate (Dec 2026)
            LocalDateTime.of(2025, 10, 15, 10, 0));    // receivedDate (Oct 15)

        // Create lot WITHOUT expiry date (received earlier)
        InventoryLot lotNoExpiry = new InventoryLot(502, 50, 30,
            null,                                       // expDate = null
            LocalDateTime.of(2025, 10, 1, 10, 0));     // receivedDate (Oct 1 - OLDER)

        lotController.createInventoryLot(lotWithExpiry);
        lotController.createInventoryLot(lotNoExpiry);

        InventoryLot lotWithExpiryFromRepo = lotController.getInventoryLotById(501);
        InventoryLot lotNoExpiryFromRepo = lotController.getInventoryLotById(502);

        // Create balances
        List<InventoryLot> lotsWithExpiry = new ArrayList<>();
        lotsWithExpiry.add(lotWithExpiryFromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lotsWithExpiry, 25);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lotsNoExpiry = new ArrayList<>();
        lotsNoExpiry.add(lotNoExpiryFromRepo);
        InventoryBalance balance2 = new InventoryBalance(2, bay2, lotsNoExpiry, 30);
        balanceController.createInventoryBalance(balance2);

        // ===== EXECUTE =====
        // Dispatch 25 units (should consume lot WITH expiry first, despite being received later)
        boolean success = inventoryDispatch.dispatchStock(50, 25);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // Only balance2 (no expiry) should remain
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());

        InventoryBalance remainingBalance = balancesAfter.get(0);
        assertEquals("Remaining balance should be balance2", Integer.valueOf(2), remainingBalance.getBalanceId());
        assertEquals("Remaining lot should have no expiry (502)", Integer.valueOf(502), remainingBalance.getLots().get(0).getLotId());
        assertNull("Remaining lot should have null expiry", remainingBalance.getLots().get(0).getExpDate());
    }

    /**
     * Test dispatch consumes multiple lots from same balance in FEFO order.
     */
    @Test
    public void testDispatchMultipleLotsFromSameBalance_FEFO() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay1 = bays[0];

        // Create multiple lots with same SKU in one balance
        // Constructor: InventoryLot(lotId, sku, qty, expDate, receivedDate)
        InventoryLot lot601 = new InventoryLot(601, 60, 10,
            LocalDateTime.of(2099, 1, 1, 0, 0),      // expDate (Jan) - EARLIEST
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        InventoryLot lot602 = new InventoryLot(602, 60, 15,
            LocalDateTime.of(2099, 3, 1, 0, 0),      // expDate (Mar) - MIDDLE
            LocalDateTime.of(2025, 10, 2, 10, 0));   // receivedDate

        InventoryLot lot603 = new InventoryLot(603, 60, 20,
            LocalDateTime.of(2099, 5, 1, 0, 0),      // expDate (May) - LATEST
            LocalDateTime.of(2025, 10, 3, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot601);
        lotController.createInventoryLot(lot602);
        lotController.createInventoryLot(lot603);

        InventoryLot lot601FromRepo = lotController.getInventoryLotById(601);
        InventoryLot lot602FromRepo = lotController.getInventoryLotById(602);
        InventoryLot lot603FromRepo = lotController.getInventoryLotById(603);

        // Create balance with all lots (in FEFO order)
        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot601FromRepo);
        lots.add(lot602FromRepo);
        lots.add(lot603FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots, 45);
        balanceController.createInventoryBalance(balance1);

        // ===== EXECUTE =====
        // Dispatch 20 units (should consume lot601 entirely + 10 from lot602)
        boolean success = inventoryDispatch.dispatchStock(60, 20);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("One balance should remain", 1, balancesAfter.size());

        InventoryBalance balanceAfter = balancesAfter.get(0);
        assertEquals("Balance should have 2 lots remaining", 2, balanceAfter.getLots().size());
        assertEquals("Balance total qty should be 25", Integer.valueOf(25), balanceAfter.getQty());

        // Verify lots in order
        assertEquals("First lot should be lot602", Integer.valueOf(602), balanceAfter.getLots().get(0).getLotId());
        assertEquals("Lot602 qty should be 5 (15-10)", Integer.valueOf(5), balanceAfter.getLots().get(0).getQty());

        assertEquals("Second lot should be lot603", Integer.valueOf(603), balanceAfter.getLots().get(1).getLotId());
        assertEquals("Lot603 qty should be unchanged (20)", Integer.valueOf(20), balanceAfter.getLots().get(1).getQty());
    }

    /**
     * Test dispatch with exact match: all stock is consumed.
     */
    @Test
    public void testDispatchExactMatch_AllStockConsumed() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay bay1 = bays[0];
        WarehouseBay bay2 = bays[1];

        // Create two lots with total qty = 50
        InventoryLot lot701 = new InventoryLot(701, 70, 30,
            LocalDateTime.of(2099, 1, 1, 0, 0),      // expDate
            LocalDateTime.of(2025, 10, 1, 10, 0));   // receivedDate

        InventoryLot lot702 = new InventoryLot(702, 70, 20,
            LocalDateTime.of(2099, 2, 1, 0, 0),      // expDate
            LocalDateTime.of(2025, 10, 2, 10, 0));   // receivedDate

        lotController.createInventoryLot(lot701);
        lotController.createInventoryLot(lot702);

        InventoryLot lot701FromRepo = lotController.getInventoryLotById(701);
        InventoryLot lot702FromRepo = lotController.getInventoryLotById(702);

        List<InventoryLot> lots1 = new ArrayList<>();
        lots1.add(lot701FromRepo);
        InventoryBalance balance1 = new InventoryBalance(1, bay1, lots1, 30);
        balanceController.createInventoryBalance(balance1);

        List<InventoryLot> lots2 = new ArrayList<>();
        lots2.add(lot702FromRepo);
        InventoryBalance balance2 = new InventoryBalance(2, bay2, lots2, 20);
        balanceController.createInventoryBalance(balance2);

        // ===== EXECUTE =====
        // Dispatch exactly 50 units
        boolean success = inventoryDispatch.dispatchStock(70, 50);

        // ===== VERIFY =====
        assertTrue("Dispatch should succeed", success);

        // All balances should be deleted
        List<InventoryBalance> balancesAfter = balanceController.getAllInventoryBalances();
        assertEquals("No balances should remain", 0, balancesAfter.size());
    }
}

