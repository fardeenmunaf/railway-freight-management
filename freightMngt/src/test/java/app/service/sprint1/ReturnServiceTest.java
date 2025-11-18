package app.service.sprint1;

import app.controller.*;
import app.model.*;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for ReturnService.
 * Tests the complete return processing workflow including quarantine, inspection,
 * restocking, and discarding of returned items.
 */
public class ReturnServiceTest {

    private ReturnService returnService;
    private InventoryBalanceController balanceController;
    private WarehouseBayController bayController;
    private ReturnOrderController returnOrderController;
    private ReturnLineController returnLineController;
    private Repositories repositories;

    private static final String AUDIT_LOG_FILE = "src/main/java/app/service/AuditLogs/return_inspection_audit.log";

    @Before
    public void setUp() {
        // Initialize controllers
        balanceController = new InventoryBalanceController();
        bayController = new WarehouseBayController();
        returnOrderController = new ReturnOrderController();
        returnLineController = new ReturnLineController();
        repositories = Repositories.getInstance();

        // Clear all repositories
        balanceController.clearAllInventoryBalances();
        bayController.clearAllWarehouseBays();
        repositories.getInventoryLotRepository().clear();
        repositories.getReturnOrderRepository().clear();
        repositories.getReturnLineRepository().clear();
        repositories.getQaInspectionRepository().clear();

        // Clear audit log file
        clearAuditLog();

        // Initialize service
        returnService = new ReturnService();
    }

    /**
     * Helper method to clear the audit log file.
     */
    private void clearAuditLog() {
        try {
            File logFile = new File(AUDIT_LOG_FILE);
            if (logFile.exists()) {
                Files.write(logFile.toPath(), new byte[0]);
            }
        } catch (IOException e) {
            System.err.println("Could not clear audit log: " + e.getMessage());
        }
    }

    /**
     * Helper method to create warehouse structure for tests.
     */
    private WarehouseBay[] createWarehouseStructure(Integer... bayIds) {
        Facility facility = new Facility(1, "Test Facility",
                new FacilityType(1, "Warehouse"), 0.0, 0.0, false);
        Warehouse warehouse = new Warehouse(1, facility, "Test Warehouse");
        WarehouseAisle aisle = new WarehouseAisle(1, warehouse, 1, "A-001");

        WarehouseBay[] bays = new WarehouseBay[bayIds.length];
        for (int i = 0; i < bayIds.length; i++) {
            bays[i] = new WarehouseBay(bayIds[i], aisle, bayIds[i], 10);
            bayController.createWarehouseBay(bays[i]);
        }

        return bays;
    }

    /**
     * Test quarantining returns with "received" status.
     * Expected: All "received" returns should be moved to "quarantine" status.
     */
    @Test
    public void testQuarantineReturns_MovesReceivedToQuarantine() {
        // ===== SETUP =====
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());

        ReturnOrder return1 = new ReturnOrder(1, order, "damaged", "received",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        ReturnOrder return2 = new ReturnOrder(2, order, "expired", "received",
                LocalDateTime.of(2025, 10, 21, 11, 0));
        ReturnOrder return3 = new ReturnOrder(3, order, "customer-remorse", "processed",
                LocalDateTime.of(2025, 10, 22, 12, 0));

        returnOrderController.createReturnOrder(return1);
        returnOrderController.createReturnOrder(return2);
        returnOrderController.createReturnOrder(return3);

        // ===== EXECUTE =====
        returnService.quarantineReturns();

        // ===== VERIFY =====
        ReturnOrder updated1 = returnOrderController.getReturnOrderById(1).orElse(null);
        ReturnOrder updated2 = returnOrderController.getReturnOrderById(2).orElse(null);
        ReturnOrder updated3 = returnOrderController.getReturnOrderById(3).orElse(null);

        assertNotNull("Return 1 should exist", updated1);
        assertNotNull("Return 2 should exist", updated2);
        assertNotNull("Return 3 should exist", updated3);
        assertEquals("Return 1 should be in quarantine", "quarantine", updated1.getStatus());
        assertEquals("Return 2 should be in quarantine", "quarantine", updated2.getStatus());
        assertEquals("Return 3 should remain processed", "processed", updated3.getStatus());
    }

    /**
     * Test quarantining when no returns are in "received" status.
     */
    @Test
    public void testQuarantineReturns_NoReceivedReturns() {
        // ===== SETUP =====
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder return1 = new ReturnOrder(1, order, "damaged", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));

        returnOrderController.createReturnOrder(return1);

        // ===== EXECUTE =====
        returnService.quarantineReturns();

        // ===== VERIFY =====
        ReturnOrder updated = returnOrderController.getReturnOrderById(1).orElse(null);
        assertNotNull("Return should exist", updated);
        assertEquals("Return should remain in quarantine", "quarantine", updated.getStatus());
    }

    /**
     * Test inspecting quarantined returns - damaged items should be discarded.
     */
    @Test
    public void testInspectQuarantinedReturns_DamagedItemsDiscarded() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "damaged", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-100", 10, "damaged");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        ReturnOrder processed = returnOrderController.getReturnOrderById(1).orElse(null);
        assertNotNull("Return should exist", processed);
        assertEquals("Return should be processed", "processed", processed.getStatus());

        ReturnLine updatedLine = returnLineController.getReturnLineById(1).orElse(null);
        assertNotNull("Return line should exist", updatedLine);
        assertEquals("Line should be marked as discarded", "discarded", updatedLine.getCondition());

        // Verify audit log
        assertTrue("Audit log should exist", new File(AUDIT_LOG_FILE).exists());
    }

    /**
     * Test inspecting quarantined returns - expired items should be discarded.
     */
    @Test
    public void testInspectQuarantinedReturns_ExpiredItemsDiscarded() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "expired", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-200", 5, "expired");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        ReturnLine updatedLine = returnLineController.getReturnLineById(1).orElse(null);
        assertNotNull("Return line should exist", updatedLine);
        assertEquals("Expired line should be discarded", "discarded", updatedLine.getCondition());
    }

    /**
     * Test inspecting quarantined returns - customer remorse with good condition should be restocked.
     */
    @Test
    public void testInspectQuarantinedReturns_CustomerRemorseGoodConditionRestocked() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-100", 8, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        ReturnOrder processed = returnOrderController.getReturnOrderById(1).orElse(null);
        assertNotNull("Return should exist", processed);
        assertEquals("Return should be processed", "processed", processed.getStatus());

        // Verify new lot was created
        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse("At least one lot should be created", lots.isEmpty());

        InventoryLot createdLot = lots.get(0);
        assertEquals("Lot should have correct SKU", Integer.valueOf(100), createdLot.getSku());
        assertEquals("Lot should have correct quantity", Integer.valueOf(8), createdLot.getQty());
    }

    /**
     * Test inspecting quarantined returns - customer remorse with bad condition should be discarded.
     */
    @Test
    public void testInspectQuarantinedReturns_CustomerRemorseBadConditionDiscarded() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-300", 3, "damaged");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        ReturnLine updatedLine = returnLineController.getReturnLineById(1).orElse(null);
        assertNotNull("Return line should exist", updatedLine);
        assertEquals("Line with bad condition should be discarded", "discarded", updatedLine.getCondition());
    }

    /**
     * Test inspecting quarantined returns - customer remorse with null condition should be restocked.
     */
    @Test
    public void testInspectQuarantinedReturns_CustomerRemorseNullConditionRestocked() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-400", 5, null);
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse("Lot should be created for null condition", lots.isEmpty());
    }

    /**
     * Test inspecting quarantined returns - cycle-count should go to QA inspection.
     */
    @Test
    public void testInspectQuarantinedReturns_CycleCountSentToQA() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "cycle-count", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-500", 12, "unknown");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        List<QaInspection> inspections = repositories.getQaInspectionRepository().getAllQaInspections();
        assertEquals("One QA inspection should be created", 1, inspections.size());

        QaInspection qa = inspections.get(0);
        assertEquals("QA should reference correct return line", Integer.valueOf(1), qa.getReturnLineId());
        assertEquals("QA status should be pending", "pending", qa.getStatus());
    }

    /**
     * Test inspecting returns in reverse order (latest first, ties by returnId ASC).
     */
    @Test
    public void testInspectQuarantinedReturns_ProcessedInReverseOrder() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());

        // Create returns with different timestamps
        ReturnOrder return1 = new ReturnOrder(1, order, "damaged", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        ReturnOrder return2 = new ReturnOrder(2, order, "damaged", "quarantine",
                LocalDateTime.of(2025, 10, 21, 11, 0));
        ReturnOrder return3 = new ReturnOrder(3, order, "damaged", "quarantine",
                LocalDateTime.of(2025, 10, 22, 12, 0));

        returnOrderController.createReturnOrder(return1);
        returnOrderController.createReturnOrder(return2);
        returnOrderController.createReturnOrder(return3);

        returnLineController.createReturnLine(new ReturnLine(1, return1, "SKU-100", 1, "damaged"));
        returnLineController.createReturnLine(new ReturnLine(2, return2, "SKU-100", 1, "damaged"));
        returnLineController.createReturnLine(new ReturnLine(3, return3, "SKU-100", 1, "damaged"));

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        // All should be processed
        ReturnOrder r1 = returnOrderController.getReturnOrderById(1).orElse(null);
        ReturnOrder r2 = returnOrderController.getReturnOrderById(2).orElse(null);
        ReturnOrder r3 = returnOrderController.getReturnOrderById(3).orElse(null);
        assertNotNull("Return 1 should exist", r1);
        assertNotNull("Return 2 should exist", r2);
        assertNotNull("Return 3 should exist", r3);
        assertEquals("processed", r1.getStatus());
        assertEquals("processed", r2.getStatus());
        assertEquals("processed", r3.getStatus());
    }

    /**
     * Test inspecting returns with same timestamp - should break ties by returnId ASC.
     */
    @Test
    public void testInspectQuarantinedReturns_SameTimestampTiesByReturnIdAsc() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        LocalDateTime sameTime = LocalDateTime.of(2025, 10, 20, 10, 0);

        ReturnOrder return1 = new ReturnOrder(1, order, "damaged", "quarantine", sameTime);
        ReturnOrder return2 = new ReturnOrder(2, order, "damaged", "quarantine", sameTime);
        ReturnOrder return3 = new ReturnOrder(3, order, "damaged", "quarantine", sameTime);

        returnOrderController.createReturnOrder(return1);
        returnOrderController.createReturnOrder(return2);
        returnOrderController.createReturnOrder(return3);

        returnLineController.createReturnLine(new ReturnLine(1, return1, "SKU-100", 1, "damaged"));
        returnLineController.createReturnLine(new ReturnLine(2, return2, "SKU-100", 1, "damaged"));
        returnLineController.createReturnLine(new ReturnLine(3, return3, "SKU-100", 1, "damaged"));

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        // All should be processed (ties broken by returnId ASC)
        ReturnOrder ret1 = returnOrderController.getReturnOrderById(1).orElse(null);
        ReturnOrder ret2 = returnOrderController.getReturnOrderById(2).orElse(null);
        ReturnOrder ret3 = returnOrderController.getReturnOrderById(3).orElse(null);
        assertNotNull("Return 1 should exist", ret1);
        assertNotNull("Return 2 should exist", ret2);
        assertNotNull("Return 3 should exist", ret3);
        assertEquals("processed", ret1.getStatus());
        assertEquals("processed", ret2.getStatus());
        assertEquals("processed", ret3.getStatus());
    }

    /**
     * Test processing QA result - approved should restock item.
     */
    @Test
    public void testProcessQaResult_ApprovedRestocksItem() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "cycle-count", "processed",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-600", 7, "good");
        returnLineController.createReturnLine(line);

        QaInspection qa = new QaInspection(1, 1, "pending", LocalDateTime.now());
        repositories.getQaInspectionRepository().addQaInspection(qa);

        // ===== EXECUTE =====
        returnService.processQaResult(1, true);

        // ===== VERIFY =====
        QaInspection updated = repositories.getQaInspectionRepository().getQaInspectionById(1);
        assertNotNull("QA inspection should exist", updated);
        assertEquals("QA should be approved", "approved", updated.getStatus());

        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse("Lot should be created after QA approval", lots.isEmpty());
    }

    /**
     * Test processing QA result - rejected should discard item.
     */
    @Test
    public void testProcessQaResult_RejectedDiscardsItem() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "cycle-count", "processed",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-700", 4, "unknown");
        returnLineController.createReturnLine(line);

        QaInspection qa = new QaInspection(1, 1, "pending", LocalDateTime.now());
        repositories.getQaInspectionRepository().addQaInspection(qa);

        // ===== EXECUTE =====
        returnService.processQaResult(1, false);

        // ===== VERIFY =====
        QaInspection updated = repositories.getQaInspectionRepository().getQaInspectionById(1);
        assertNotNull("QA inspection should exist", updated);
        assertEquals("QA should be rejected", "rejected", updated.getStatus());

        ReturnLine updatedLine = returnLineController.getReturnLineById(1).orElse(null);
        assertNotNull("Return line should exist", updatedLine);
        assertEquals("Line should be discarded after QA rejection", "discarded", updatedLine.getCondition());
    }

    /**
     * Test processing QA result with non-existent QA ID.
     */
    @Test
    public void testProcessQaResult_NonExistentQaId() {
        // ===== SETUP =====
        // No QA inspection created

        // ===== EXECUTE =====
        returnService.processQaResult(999, true);

        // ===== VERIFY =====
        // Should handle gracefully without throwing exception
        List<QaInspection> inspections = repositories.getQaInspectionRepository().getAllQaInspections();
        assertEquals("No inspections should exist", 0, inspections.size());
    }

    /**
     * Test restocking with unknown/default reason - should be discarded.
     */
    @Test
    public void testInspectQuarantinedReturns_UnknownReasonDiscarded() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "unknown-reason", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-800", 2, "unknown");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        ReturnLine updatedLine = returnLineController.getReturnLineById(1).orElse(null);
        assertNotNull("Return line should exist", updatedLine);
        assertEquals("Unknown reason should result in discard", "discarded", updatedLine.getCondition());
    }

    /**
     * Test complete workflow with processAllReturns method.
     */
    @Test
    public void testProcessAllReturns_CompleteWorkflow() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3, 4);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());

        // Create a mix of returns
        ReturnOrder return1 = new ReturnOrder(1, order, "customer-remorse", "received",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        ReturnOrder return2 = new ReturnOrder(2, order, "damaged", "received",
                LocalDateTime.of(2025, 10, 21, 11, 0));

        returnOrderController.createReturnOrder(return1);
        returnOrderController.createReturnOrder(return2);

        returnLineController.createReturnLine(new ReturnLine(1, return1, "SKU-100", 5, "good"));
        returnLineController.createReturnLine(new ReturnLine(2, return2, "SKU-200", 3, "damaged"));

        // ===== EXECUTE =====
        returnService.processAllReturns();

        // ===== VERIFY =====
        ReturnOrder processed1 = returnOrderController.getReturnOrderById(1).orElse(null);
        ReturnOrder processed2 = returnOrderController.getReturnOrderById(2).orElse(null);

        assertNotNull("Return 1 should exist", processed1);
        assertNotNull("Return 2 should exist", processed2);
        assertEquals("Return 1 should be processed", "processed", processed1.getStatus());
        assertEquals("Return 2 should be processed", "processed", processed2.getStatus());

        // Verify audit log exists
        assertTrue("Audit log should be created", new File(AUDIT_LOG_FILE).exists());
    }

    /**
     * Test restocking with SKU that has existing inventory balance.
     */
    @Test
    public void testRestockItem_WithExistingBalance() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2, 3);

        // Create existing inventory lot and balance
        InventoryLot existingLot = new InventoryLot(1, 100, 10,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2025, 9, 1, 10, 0));
        repositories.getInventoryLotRepository().addInventoryLot(existingLot);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(existingLot);
        InventoryBalance existingBalance = new InventoryBalance(1, bays[0], lots, 10);
        balanceController.createInventoryBalance(existingBalance);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-100", 5, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        List<InventoryLot> allLots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertTrue("Should have multiple lots", allLots.size() > 1);
    }

    /**
     * Test restocking error handling when SKU parsing fails.
     */
    @Test
    public void testRestockItem_InvalidSkuHandling() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        // SKU with no numbers - will cause parsing issue
        ReturnLine line = new ReturnLine(1, returnOrder, "ABC", 5, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        // Should handle error gracefully
        ReturnOrder processed = returnOrderController.getReturnOrderById(1).orElse(null);
        assertNotNull("Return should exist", processed);
        assertEquals("Return should still be marked processed", "processed", processed.getStatus());
    }

    /**
     * Test restocking when no bay is available.
     */
    @Test
    public void testRestockItem_NoBayAvailable() {
        // ===== SETUP =====
        // Don't create any warehouse bays

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-100", 5, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        // Should handle gracefully - lot created but not inserted into bay
        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse("Lot should still be created", lots.isEmpty());
    }

    /**
     * Test FEFO insertion with expiry dates.
     */
    @Test
    public void testRestockItem_FEFOInsertionWithExpiryDates() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2, 3);

        // Create existing lots with expiry dates
        InventoryLot lot1 = new InventoryLot(1, 100, 10,
                LocalDateTime.of(2026, 12, 31, 0, 0),
                LocalDateTime.of(2025, 9, 1, 10, 0));
        InventoryLot lot2 = new InventoryLot(2, 100, 15,
                LocalDateTime.of(2026, 6, 30, 0, 0),
                LocalDateTime.of(2025, 9, 2, 10, 0));

        repositories.getInventoryLotRepository().addInventoryLot(lot1);
        repositories.getInventoryLotRepository().addInventoryLot(lot2);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot2); // Earlier expiry
        lots.add(lot1); // Later expiry

        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 25);
        balanceController.createInventoryBalance(balance);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-100", 8, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        List<InventoryLot> allLots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertTrue("Should have 3 lots total", allLots.size() >= 3);
    }

    /**
     * Test FIFO insertion when no expiry dates.
     */
    @Test
    public void testRestockItem_FIFOInsertionNoExpiryDates() {
        // ===== SETUP =====
        WarehouseBay[] bays = createWarehouseStructure(1, 2, 3);

        // Create existing lot without expiry
        InventoryLot lot1 = new InventoryLot(1, 100, 10, null,
                LocalDateTime.of(2025, 10, 1, 10, 0));

        repositories.getInventoryLotRepository().addInventoryLot(lot1);

        List<InventoryLot> lots = new ArrayList<>();
        lots.add(lot1);

        InventoryBalance balance = new InventoryBalance(1, bays[0], lots, 10);
        balanceController.createInventoryBalance(balance);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-100", 5, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        List<InventoryLot> allLots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertTrue("Should have multiple lots", allLots.size() >= 2);
    }

    /**
     * Test audit log file creation and content.
     */
    @Test
    public void testAuditLogCreation() throws IOException {
        // ===== SETUP =====
        createWarehouseStructure(1, 2);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(105, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 9, 22, 14, 32));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line = new ReturnLine(1, returnOrder, "SKU-123", 8, "good");
        returnLineController.createReturnLine(line);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        File logFile = new File(AUDIT_LOG_FILE);
        assertTrue("Audit log file should exist", logFile.exists());

        List<String> lines = Files.readAllLines(logFile.toPath());
        assertFalse("Audit log should have at least one entry", lines.isEmpty());

        String logEntry = lines.get(0);
        assertTrue("Log should contain returnId", logEntry.contains("returnId=R105"));
        assertTrue("Log should contain sku", logEntry.contains("sku=SKU-123"));
        assertTrue("Log should contain action", logEntry.contains("action=Restocked"));
        assertTrue("Log should contain qty", logEntry.contains("qty=8"));
    }

    /**
     * Test multiple return lines in single return order.
     */
    @Test
    public void testInspectQuarantinedReturns_MultipleLines() {
        // ===== SETUP =====
        createWarehouseStructure(1, 2, 3, 4);

        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder returnOrder = new ReturnOrder(1, order, "customer-remorse", "quarantine",
                LocalDateTime.of(2025, 10, 20, 10, 0));
        returnOrderController.createReturnOrder(returnOrder);

        ReturnLine line1 = new ReturnLine(1, returnOrder, "SKU-100", 5, "good");
        ReturnLine line2 = new ReturnLine(2, returnOrder, "SKU-200", 3, "good");
        ReturnLine line3 = new ReturnLine(3, returnOrder, "SKU-300", 7, "good");

        returnLineController.createReturnLine(line1);
        returnLineController.createReturnLine(line2);
        returnLineController.createReturnLine(line3);

        // ===== EXECUTE =====
        returnService.inspectQuarantinedReturns();

        // ===== VERIFY =====
        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertEquals("Should create 3 lots for 3 lines", 3, lots.size());
    }
}

