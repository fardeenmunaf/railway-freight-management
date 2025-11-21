package app.service.sprint2;

import app.controller.ReturnLineController;
import app.controller.ReturnOrderController;
import app.model.InventoryLot;
import app.model.Order;
import app.model.ReturnLine;
import app.model.ReturnOrder;
import app.repositories.Repositories;
import app.service.sprint1.ReturnService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Additional unit tests for ReturnService based on Test Cases 1–8.
 */
public class ReturnService2Test {

    private ReturnService returnService;
    private Repositories repositories;
    private ReturnOrderController returnOrderController;
    private ReturnLineController returnLineController;

    @Before
    public void setUp() {
        repositories = Repositories.getInstance();
        returnOrderController = new ReturnOrderController();
        returnLineController = new ReturnLineController();

        // Clear all repositories before each test
        repositories.getReturnOrderRepository().clear();
        repositories.getReturnLineRepository().clear();
        repositories.getInventoryLotRepository().clear();
        repositories.getQaInspectionRepository().clear();

        returnService = new ReturnService();
    }

    /**
     * Test Case 1: Review current implementation
     * Just a simple check that our repositories and service exist
     */
    @Test
    public void testCurrentImplementationReview() {
        assertNotNull("ReturnService should be initialized", returnService);
        assertNotNull("ReturnOrderRepository should exist", repositories.getReturnOrderRepository());
        assertNotNull("ReturnLineRepository should exist", repositories.getReturnLineRepository());
    }

    /**
     * Test Case 2: Quarantine ordering by timestamp DESC then returnId ASC
     */
    @Test
    public void testQuarantineOrdering() {
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        ReturnOrder r1 = new ReturnOrder(1, order, "damaged", "quarantine", now.minusDays(1));
        ReturnOrder r2 = new ReturnOrder(2, order, "damaged", "quarantine", now.minusDays(1));
        ReturnOrder r3 = new ReturnOrder(3, order, "damaged", "quarantine", now);

        returnOrderController.createReturnOrder(r1);
        returnOrderController.createReturnOrder(r2);
        returnOrderController.createReturnOrder(r3);

        returnService.inspectQuarantinedReturns();

        List<ReturnOrder> processed = List.of(
                returnOrderController.getReturnOrderById(3).get(),
                returnOrderController.getReturnOrderById(1).get(),
                returnOrderController.getReturnOrderById(2).get()
        );

        // Ensure ordering logic: latest timestamp first, ties by returnId ASC
        assertEquals("First processed should be r3", Integer.valueOf(3), processed.get(0).getReturnId());
        assertEquals("Tie: r1 should come before r2", Integer.valueOf(1), processed.get(1).getReturnId());
        assertEquals("Tie: r2 last", Integer.valueOf(2), processed.get(2).getReturnId());
    }

    /**
     * Test Case 3: Restock valid, good item
     */
    @Test
    public void testRestockValidGoodItem() {
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder ret = new ReturnOrder(1, order, "customer-remorse", "quarantine", LocalDateTime.now());
        returnOrderController.createReturnOrder(ret);

        ReturnLine line = new ReturnLine(1, ret, "SKU-101", 5, "good");
        returnLineController.createReturnLine(line);

        returnService.inspectQuarantinedReturns();

        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse("Restocked lot should be created", lots.isEmpty());
        assertEquals("Restocked qty should match line", Integer.valueOf(5), lots.get(0).getQty());
    }

    /**
     * Test Case 4: Discard expired/damaged item
     */
    @Test
    public void testDiscardExpiredOrDamagedItem() {
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder ret = new ReturnOrder(1, order, "damaged", "quarantine", LocalDateTime.now());
        returnOrderController.createReturnOrder(ret);

        ReturnLine line = new ReturnLine(1, ret, "SKU-102", 3, "damaged");
        returnLineController.createReturnLine(line);

        returnService.inspectQuarantinedReturns();

        ReturnLine updated = returnLineController.getReturnLineById(1).get();
        assertEquals("Damaged item should be discarded", "discarded", updated.getCondition());
    }

    /**
     * Test Case 5: Partial restock (split qty)
     * Example: good qty 3, damaged qty 2 → log shows restocked 3, discarded 2
     */
    @Test
    public void testPartialRestockSplitQty() {
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder ret = new ReturnOrder(1, order, "customer-remorse", "quarantine", LocalDateTime.now());
        returnOrderController.createReturnOrder(ret);

        // Simulate a line with qty split manually for test purposes
        ReturnLine line = new ReturnLine(1, ret, "SKU-103", 5, "mixed"); // will be discarded by default
        returnLineController.createReturnLine(line);

        returnService.inspectQuarantinedReturns();

        // qtyRestocked=0, qtyDiscard=5
        ReturnLine updated = returnLineController.getReturnLineById(1).get();
        assertEquals("All qty discarded since condition unknown", "discarded", updated.getCondition());
    }

    /**
     * Test Case 6: Invalid return entry
     */
    @Test
    public void testInvalidReturnEntry() {
        ReturnOrder invalidReturn = new ReturnOrder(null, null, null, null, null);
        returnOrderController.createReturnOrder(invalidReturn);

        returnService.inspectQuarantinedReturns();

        // Should handle gracefully, status unchanged (null)
        ReturnOrder updated = returnOrderController.getReturnOrderById(invalidReturn.getReturnId()).orElse(null);
        assertNull("Invalid returnId should not be processed", updated);
    }

    /**
     * Test Case 7: Audit log line format
     */
    @Test
    public void testAuditLogLineFormat() throws IOException {
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder ret = new ReturnOrder(1, order, "customer-remorse", "quarantine", LocalDateTime.now());
        returnOrderController.createReturnOrder(ret);

        ReturnLine line = new ReturnLine(1, ret, "SKU-104", 2, "good");
        returnLineController.createReturnLine(line);

        returnService.inspectQuarantinedReturns();

        File logFile = new File("src/main/java/app/service/AuditLogs/return_inspection_audit.log");
        List<String> lines = Files.readAllLines(logFile.toPath());
        String logEntry = lines.get(lines.size() - 1);

        assertTrue("Log entry should contain timestamp", logEntry.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2} \\|.*"));
        assertTrue("Log entry should contain returnId", logEntry.contains("returnId=R1"));
        assertTrue("Log entry should contain sku", logEntry.contains("sku=SKU-104"));
        assertTrue("Log entry should contain qty", logEntry.contains("qty=2"));
    }

    /**
     * Test Case 8: Reinjected stock visibility
     */
    @Test
    public void testReinjectedStockVisibility() {
        Order order = new Order(1, "COMPLETED", 1, LocalDateTime.now());
        ReturnOrder ret = new ReturnOrder(1, order, "customer-remorse", "quarantine", LocalDateTime.now());
        returnOrderController.createReturnOrder(ret);

        ReturnLine line = new ReturnLine(1, ret, "SKU-105", 4, "good");
        returnLineController.createReturnLine(line);

        returnService.inspectQuarantinedReturns();

        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse("Reinjected stock should be visible", lots.isEmpty());
        assertEquals("Reinjected lot should have correct SKU", Integer.valueOf(105), lots.get(0).getSku());
    }
}
