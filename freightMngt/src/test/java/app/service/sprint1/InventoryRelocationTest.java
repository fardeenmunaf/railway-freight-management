package app.service.sprint1;

import app.controller.InventoryBalanceController;
import app.controller.InventoryLotController;
import app.controller.InventoryMoveController;
import app.controller.WarehouseBayController;
import app.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class InventoryRelocationTest {

    private InventoryRelocation inventoryRelocation;
    private InventoryBalanceController balanceController;
    private InventoryLotController lotController;
    private InventoryMoveController moveController;
    private WarehouseBayController bayController;

    @Before
    public void setUp() {
        // Clear repositories
        balanceController = new InventoryBalanceController();
        lotController = new InventoryLotController();
        moveController = new InventoryMoveController();
        bayController = new WarehouseBayController();

        balanceController.clearAllInventoryBalances();
        moveController.clearAllInventoryMoves();
        bayController.clearAllWarehouseBays();

        // CRITICAL: Clear lots repository to avoid conflicts between tests
        app.repositories.Repositories.getInstance().getInventoryLotRepository().clear();

        inventoryRelocation = new InventoryRelocation();
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
     * Test relocating a single lot from a balance containing only that lot to an empty bay.
     * This test simulates the scenario in Main.java where lot4 is moved from bay1 to bay27.
     *
     * Expected behavior:
     * - Source balance should be deleted (as it becomes empty)
     * - New balance should be created in target bay
     * - InventoryMove audit record should be created
     */
    @Test
    public void testRelocateSingleLotToEmptyBay() {
        // ===== SETUP =====

        // Create warehouse structure with bay 1 and bay 27
        WarehouseBay[] bays = createWarehouseStructure(1, 27);
        WarehouseBay sourceBay = bays[0];
        // targetBay = bays[1] (bay 27)

        // Create a single lot (lot4 with SKU 22) - IMPORTANT: Use ArrayList for mutability
        InventoryLot lot4 = new InventoryLot(
            4,                                          // lotId
            22,                                         // sku
            19,                                         // qty
            LocalDateTime.of(2026, 1, 10, 0, 0),       // expDate
            LocalDateTime.of(2025, 9, 6, 12, 45)       // receivedDate
        );

        // CRITICAL: Create lot in repository (required for InventoryRelocation validation)
        lotController.createInventoryLot(lot4);

        // CRITICAL: Retrieve the lot from repository to ensure we use the same object reference
        InventoryLot lot4FromRepo = lotController.getInventoryLotById(4);

        // Create balance in source bay with ONLY this lot (using ArrayList for mutability)
        List<InventoryLot> lotsInSourceBay = new ArrayList<>();
        lotsInSourceBay.add(lot4FromRepo);  // Use lot from repository!

        InventoryBalance sourceBalance = new InventoryBalance(
            1,                      // balanceId
            sourceBay,              // bay
            lotsInSourceBay,        // lots (MUTABLE ArrayList)
            19                      // qty
        );

        balanceController.createInventoryBalance(sourceBalance);

        // ===== VERIFICATION BEFORE RELOCATION =====

        // Verify lot4 exists and is in source bay
        Optional<InventoryBalance> balanceBeforeOpt = balanceController.getInventoryBalanceById(1);
        assertTrue("Source balance should exist before relocation", balanceBeforeOpt.isPresent());

        InventoryBalance balanceBeforeRelocation = balanceBeforeOpt.get();
        assertEquals("Source balance should have 1 lot", 1, balanceBeforeRelocation.getLots().size());
        assertEquals("Source balance should have quantity 19", Integer.valueOf(19), balanceBeforeRelocation.getQty());
        assertEquals("Source balance should be in bay 1", Integer.valueOf(1), balanceBeforeRelocation.getBay().getBayId());

        // Verify lot4 is the one in the balance
        InventoryLot lotInBalance = balanceBeforeRelocation.getLots().get(0);
        assertEquals("Lot ID should be 4", Integer.valueOf(4), lotInBalance.getLotId());
        assertEquals("Lot SKU should be 22", Integer.valueOf(22), lotInBalance.getSku());
        assertEquals("Lot quantity should be 19", Integer.valueOf(19), lotInBalance.getQty());

        // Verify target bay is empty
        InventoryBalance targetBalanceBeforeRelocation = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(27)) {
                targetBalanceBeforeRelocation = balance;
                break;
            }
        }
        assertNull("Target bay should be empty before relocation", targetBalanceBeforeRelocation);

        // Count inventory moves before relocation
        int moveCountBefore = moveController.getAllInventoryMoves().size();

        // ===== EXECUTE RELOCATION =====

        boolean success = inventoryRelocation.relocateLot(4, 27, "Test relocation - Moving lot4 to bay27");

        // ===== VERIFICATION AFTER RELOCATION =====

        assertTrue("Relocation should succeed", success);

        // VERIFY 1: Source balance should be deleted (bay1 is now empty)
        // Search for bay1 like Main.java does
        InventoryBalance bay1BalanceAfter = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(1)) {
                bay1BalanceAfter = balance;
                break;
            }
        }
        assertNull("Bay 1 should have no balance after relocation (balance deleted)", bay1BalanceAfter);

        // VERIFY 2: New balance should exist in target bay27
        InventoryBalance targetBalanceAfterRelocation = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(27)) {
                targetBalanceAfterRelocation = balance;
                break;
            }
        }
        assertNotNull("Target balance should exist after relocation", targetBalanceAfterRelocation);
        assertEquals("Target balance should have 1 lot", 1, targetBalanceAfterRelocation.getLots().size());
        assertEquals("Target balance should have quantity 19", Integer.valueOf(19), targetBalanceAfterRelocation.getQty());

        // VERIFY 3: Lot4 should be in the target balance
        InventoryLot relocatedLot = targetBalanceAfterRelocation.getLots().get(0);
        assertEquals("Relocated lot should be lot4", Integer.valueOf(4), relocatedLot.getLotId());
        assertEquals("Relocated lot should have SKU 22", Integer.valueOf(22), relocatedLot.getSku());
        assertEquals("Relocated lot should have quantity 19", Integer.valueOf(19), relocatedLot.getQty());

        // VERIFY 4: InventoryMove audit record should be created
        List<InventoryMove> allMoves = moveController.getAllInventoryMoves();
        assertEquals("One new inventory move should be created", moveCountBefore + 1, allMoves.size());

        InventoryMove lastMove = allMoves.get(allMoves.size() - 1);
        assertEquals("Move should be for lot4", Integer.valueOf(4), lastMove.getLot().getLotId());
        assertEquals("Move should be from bay 1", Integer.valueOf(1), lastMove.getFromBay().getBayId());
        assertEquals("Move should be to bay 27", Integer.valueOf(27), lastMove.getToBay().getBayId());
        assertEquals("Move quantity should be 19", Integer.valueOf(19), lastMove.getQty());
        assertEquals("Move reason should match", "Test relocation - Moving lot4 to bay27", lastMove.getReason());
        assertNotNull("Move timestamp should be set", lastMove.getCreatedAt());
    }

    /**
     * Test relocating one lot from a balance containing multiple lots to an empty bay.
     *
     * Expected behavior:
     * - Source balance should be updated (lot removed, quantity decreased)
     * - New balance should be created in target bay
     * - FEFO order should be maintained in source balance
     */
    @Test
    public void testRelocateOneLotFromMultipleLotBalance() {
        // ===== SETUP =====

        // Create warehouse structure with bay 1 and bay 2
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay sourceBay = bays[0];
        // targetBay = bays[1] (bay 2)

        // Create multiple lots with SKU 22 in FEFO order
        InventoryLot lot1 = new InventoryLot(1, 22, 20,
            LocalDateTime.of(2026, 1, 1, 0, 0),      // expDate - Expires first
            LocalDateTime.of(2025, 9, 1, 10, 0));    // receivedDate

        InventoryLot lot2 = new InventoryLot(2, 22, 30,
            LocalDateTime.of(2026, 2, 1, 0, 0),      // expDate - Expires second - THIS ONE WILL BE MOVED
            LocalDateTime.of(2025, 9, 2, 10, 0));    // receivedDate

        InventoryLot lot3 = new InventoryLot(3, 22, 25,
            LocalDateTime.of(2026, 3, 1, 0, 0),      // expDate - Expires last
            LocalDateTime.of(2025, 9, 3, 10, 0));    // receivedDate

        // CRITICAL: Create lots in repository (required for InventoryRelocation validation)
        lotController.createInventoryLot(lot1);
        lotController.createInventoryLot(lot2);
        lotController.createInventoryLot(lot3);

        // CRITICAL: Retrieve lots from repository to ensure same object references
        InventoryLot lot1FromRepo = lotController.getInventoryLotById(1);
        InventoryLot lot2FromRepo = lotController.getInventoryLotById(2);
        InventoryLot lot3FromRepo = lotController.getInventoryLotById(3);

        // Create balance with all 3 lots (using ArrayList for mutability)
        List<InventoryLot> lotsInSourceBay = new ArrayList<>();
        lotsInSourceBay.add(lot1FromRepo);  // Use lots from repository!
        lotsInSourceBay.add(lot2FromRepo);
        lotsInSourceBay.add(lot3FromRepo);

        InventoryBalance sourceBalance = new InventoryBalance(1, sourceBay, lotsInSourceBay, 75);
        balanceController.createInventoryBalance(sourceBalance);

        // ===== EXECUTE RELOCATION =====

        boolean success = inventoryRelocation.relocateLot(2, 2, "Moving lot2 to separate bay");

        // ===== VERIFICATION =====

        assertTrue("Relocation should succeed", success);

        // Source balance should still exist with 2 lots - search like Main.java does
        InventoryBalance sourceAfter = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(1)) {
                sourceAfter = balance;
                break;
            }
        }

        assertNotNull("Source balance should still exist", sourceAfter);
        assertEquals("Source balance should have 2 lots remaining", 2, sourceAfter.getLots().size());
        assertEquals("Source balance quantity should be 45 (75-30)", Integer.valueOf(45), sourceAfter.getQty());

        // Verify lot2 was removed and FEFO order is maintained
        assertEquals("First lot should be lot1", Integer.valueOf(1), sourceAfter.getLots().get(0).getLotId());
        assertEquals("Second lot should be lot3", Integer.valueOf(3), sourceAfter.getLots().get(1).getLotId());

        // Target balance should have lot2 - search like Main.java does
        InventoryBalance targetAfter = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(2)) {
                targetAfter = balance;
                break;
            }
        }
        assertNotNull("Target balance should exist", targetAfter);
        assertEquals("Target balance should have 1 lot", 1, targetAfter.getLots().size());
        assertEquals("Target balance should have lot2", Integer.valueOf(2), targetAfter.getLots().get(0).getLotId());
        assertEquals("Target balance quantity should be 30", Integer.valueOf(30), targetAfter.getQty());
    }

    /**
     * Test relocating a lot to a bay that already has lots (FEFO insertion).
     *
     * Expected behavior:
     * - Lot should be inserted in correct FEFO position
     * - Existing lots should remain in FEFO order
     */
    @Test
    public void testRelocateLotToNonEmptyBay_FEFOInsertion() {
        // ===== SETUP =====

        // Create warehouse structure with bay 1 and bay 2
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay sourceBay = bays[0];
        WarehouseBay targetBay = bays[1];

        // CRITICAL: Create ALL lots in repository FIRST
        // Create existing lots for target bay - these will be in FEFO order
        // Using unique IDs: 301, 302, 300 to avoid conflicts with other tests
        // Constructor: InventoryLot(lotId, sku, qty, expDate, receivedDate)
        InventoryLot existingLot1 = new InventoryLot(301, 22, 20,
            LocalDateTime.of(2027, 1, 1, 0, 0),     // expDate - Expires FIRST (Jan 2027)
            LocalDateTime.of(2025, 9, 1, 10, 0));   // receivedDate - Received Sept 2025

        InventoryLot existingLot2 = new InventoryLot(302, 22, 30,
            LocalDateTime.of(2027, 3, 1, 0, 0),     // expDate - Expires LAST (Mar 2027)
            LocalDateTime.of(2025, 9, 2, 10, 0));   // receivedDate - Received Sept 2025

        // Create lot to be moved - expires in the MIDDLE (between lot1 and lot2)
        InventoryLot lotToMove = new InventoryLot(300, 22, 15,
            LocalDateTime.of(2027, 2, 15, 0, 0),    // expDate - Expires MIDDLE (Feb 2027)
            LocalDateTime.of(2025, 9, 15, 10, 0));  // receivedDate - Received Sept 2025

        // Add all lots to repository
        lotController.createInventoryLot(existingLot1);
        lotController.createInventoryLot(existingLot2);
        lotController.createInventoryLot(lotToMove);

        // CRITICAL: Retrieve ALL lots from repository to ensure same object references
        InventoryLot existingLot1FromRepo = lotController.getInventoryLotById(301);
        InventoryLot existingLot2FromRepo = lotController.getInventoryLotById(302);
        InventoryLot lotToMoveFromRepo = lotController.getInventoryLotById(300);

        // Create source balance with lot to be moved
        List<InventoryLot> sourceBalanceLots = new ArrayList<>();
        sourceBalanceLots.add(lotToMoveFromRepo);  // Use lot from repository!
        InventoryBalance sourceBalance = new InventoryBalance(1, sourceBay, sourceBalanceLots, 15);
        balanceController.createInventoryBalance(sourceBalance);

        // Create target balance with existing lots IN FEFO ORDER (earliest expiration first)
        List<InventoryLot> targetBalanceLots = new ArrayList<>();
        targetBalanceLots.add(existingLot1FromRepo);  // Jan 2026 - expires first
        targetBalanceLots.add(existingLot2FromRepo);  // Mar 2026 - expires last
        // lot100 (Feb 2026) will be inserted between them by InventoryRelocation

        InventoryBalance targetBalance = new InventoryBalance(2, targetBay, targetBalanceLots, 50);
        balanceController.createInventoryBalance(targetBalance);

        // ===== EXECUTE RELOCATION =====

        boolean success = inventoryRelocation.relocateLot(300, 2, "Testing FEFO insertion");

        // ===== VERIFICATION =====

        assertTrue("Relocation should succeed", success);

        // Target balance should have 3 lots in FEFO order - search like Main.java does
        InventoryBalance targetAfter = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(2)) {
                targetAfter = balance;
                break;
            }
        }

        assertNotNull("Target balance should exist", targetAfter);
        assertEquals("Target balance should have 3 lots", 3, targetAfter.getLots().size());
        assertEquals("Target balance quantity should be 65", Integer.valueOf(65), targetAfter.getQty());


        // Verify FEFO order: lot301 (Jan), lot300 (Feb), lot302 (Mar)
        assertEquals("First lot should be lot301 (expires Jan)", Integer.valueOf(301), targetAfter.getLots().get(0).getLotId());
        assertEquals("Second lot should be lot300 (expires Feb)", Integer.valueOf(300), targetAfter.getLots().get(1).getLotId());
        assertEquals("Third lot should be lot302 (expires Mar)", Integer.valueOf(302), targetAfter.getLots().get(2).getLotId());
    }

    /**
     * Test validation: Cannot relocate lot to same bay.
     */
    @Test
    public void testRelocateLotToSameBay_ShouldFail() {
        // Setup
        WarehouseBay[] bays = createWarehouseStructure(1);
        WarehouseBay bay = bays[0];

        InventoryLot lot = new InventoryLot(401, 22, 10, LocalDateTime.now().plusDays(30), LocalDateTime.now());
        lotController.createInventoryLot(lot);
        InventoryLot lotFromRepo = lotController.getInventoryLotById(401);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lotFromRepo);

        InventoryBalance balance = new InventoryBalance(1, bay, lots, 10);
        balanceController.createInventoryBalance(balance);

        // Execute
        boolean success = inventoryRelocation.relocateLot(401, 1, "Same bay test");

        // Verify
        assertFalse("Relocation to same bay should fail", success);
    }

    /**
     * Test validation: Cannot relocate to bay with different SKU.
     */
    @Test
    public void testRelocateToBayWithDifferentSKU_ShouldFail() {
        // Setup
        WarehouseBay[] bays = createWarehouseStructure(1, 2);
        WarehouseBay sourceBay = bays[0];
        WarehouseBay targetBay = bays[1];

        // Lot with SKU 22
        InventoryLot lot1 = new InventoryLot(501, 22, 10, LocalDateTime.now().plusDays(30), LocalDateTime.now());
        lotController.createInventoryLot(lot1);
        InventoryLot lot1FromRepo = lotController.getInventoryLotById(501);

        List<InventoryLot> sourceLots = new ArrayList<>();
        sourceLots.add(lot1FromRepo);
        InventoryBalance sourceBalance = new InventoryBalance(1, sourceBay, sourceLots, 10);
        balanceController.createInventoryBalance(sourceBalance);

        // Target bay has SKU 99 (different)
        InventoryLot lot2 = new InventoryLot(502, 99, 15, LocalDateTime.now().plusDays(30), LocalDateTime.now());
        lotController.createInventoryLot(lot2);
        InventoryLot lot2FromRepo = lotController.getInventoryLotById(502);

        List<InventoryLot> targetLots = new ArrayList<>();
        targetLots.add(lot2FromRepo);
        InventoryBalance targetBalance = new InventoryBalance(2, targetBay, targetLots, 15);
        balanceController.createInventoryBalance(targetBalance);

        // Execute
        boolean success = inventoryRelocation.relocateLot(501, 2, "Different SKU test");

        // Verify
        assertFalse("Relocation to bay with different SKU should fail", success);
    }

    /**
     * Test validation: Cannot relocate to bay with no capacity.
     */
    @Test
    public void testRelocateToBayWithNoCapacity_ShouldFail() {
        // Setup - Create bays with custom capacity
        Facility facility = new Facility(1, "Test Facility",
            new FacilityType(1, "Warehouse"), 0.0, 0.0, false);
        Warehouse warehouse = new Warehouse(1, facility, "Test Warehouse");
        WarehouseAisle aisle = new WarehouseAisle(1, warehouse, 1, "A-001");

        WarehouseBay sourceBay = new WarehouseBay(1, aisle, 1, 10);
        WarehouseBay targetBay = new WarehouseBay(2, aisle, 2, 1); // Only capacity for 1 lot

        bayController.createWarehouseBay(sourceBay);
        bayController.createWarehouseBay(targetBay);

        // Lot to move
        InventoryLot lotToMove = new InventoryLot(601, 22, 10, LocalDateTime.now().plusDays(30), LocalDateTime.now());
        lotController.createInventoryLot(lotToMove);
        InventoryLot lotToMoveFromRepo = lotController.getInventoryLotById(601);

        List<InventoryLot> sourceLots = new ArrayList<>();
        sourceLots.add(lotToMoveFromRepo);
        InventoryBalance sourceBalance = new InventoryBalance(1, sourceBay, sourceLots, 10);
        balanceController.createInventoryBalance(sourceBalance);

        // Target bay already full (1 lot in bay with capacity 1)
        InventoryLot existingLot = new InventoryLot(602, 22, 15, LocalDateTime.now().plusDays(30), LocalDateTime.now());
        lotController.createInventoryLot(existingLot);
        InventoryLot existingLotFromRepo = lotController.getInventoryLotById(602);

        List<InventoryLot> targetLots = new ArrayList<>();
        targetLots.add(existingLotFromRepo);
        InventoryBalance targetBalance = new InventoryBalance(2, targetBay, targetLots, 15);
        balanceController.createInventoryBalance(targetBalance);

        // Execute
        boolean success = inventoryRelocation.relocateLot(601, 2, "No capacity test");

        // Verify
        assertFalse("Relocation to full bay should fail", success);
    }

    /**
     * Test validation: Cannot relocate non-existent lot.
     */
    @Test
    public void testRelocateNonExistentLot_ShouldFail() {
        // Setup bay
        createWarehouseStructure(1);

        // Execute - try to relocate lot that doesn't exist
        boolean success = inventoryRelocation.relocateLot(999, 1, "Non-existent lot test");

        // Verify
        assertFalse("Relocation of non-existent lot should fail", success);
    }

    /**
     * Simple test to verify lot controller is working and lots are shared across instances.
     */
    @Test
    public void testLotControllerSharing() {
        // Create a lot using our test controller
        InventoryLot testLot = new InventoryLot(999, 1, 10, LocalDateTime.now().plusDays(30), LocalDateTime.now());
        lotController.createInventoryLot(testLot);

        // Create a NEW controller instance (like InventoryRelocation does)
        InventoryLotController newController = new InventoryLotController();

        // Try to retrieve the lot with the new controller
        InventoryLot retrievedLot = newController.getInventoryLotById(999);

        // Verify the lot is accessible from the new controller instance
        assertNotNull("Lot should be accessible from different controller instance", retrievedLot);
        assertEquals("Lot ID should match", Integer.valueOf(999), retrievedLot.getLotId());
    }
}
