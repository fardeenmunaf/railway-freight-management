package app.service.sprint2;

import app.controller.InventoryBalanceController;
import app.model.*;
import app.repositories.Repositories;
import app.service.sprint1.ReturnService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReturnService2Test {

    private static ReturnService service;
    private static Repositories repositories;
    private static InventoryBalanceController inventoryController;

    @BeforeAll
    static void setup() {
        repositories = Repositories.getInstance();
        inventoryController = new InventoryBalanceController();
        service = new ReturnService();
    }

    @Test
    void testQuarantineOrdering() {
        ReturnOrder r1 = new ReturnOrder(1, null, "customer-remorse", "received",
                LocalDateTime.of(2025, 1, 15, 10, 0));
        ReturnOrder r2 = new ReturnOrder(2, null, "damaged", "received",
                LocalDateTime.of(2025, 1, 15, 10, 0));
        ReturnOrder r3 = new ReturnOrder(3, null, "expired", "received",
                LocalDateTime.of(2025, 1, 16, 10, 0));

        repositories.getReturnOrderRepository().addReturnOrder(r1);
        repositories.getReturnOrderRepository().addReturnOrder(r2);
        repositories.getReturnOrderRepository().addReturnOrder(r3);

        ReturnLine line1 = new ReturnLine(1, r1, "SKU0001", 10, "good");
        ReturnLine line2 = new ReturnLine(2, r2, "SKU0002", 5, "damaged");
        ReturnLine line3 = new ReturnLine(3, r3, "SKU0003", 8, "good");

        repositories.getReturnLineRepository().addReturnLine(line1);
        repositories.getReturnLineRepository().addReturnLine(line2);
        repositories.getReturnLineRepository().addReturnLine(line3);

        service.processAllReturns();

        assertEquals("processed", r3.getStatus());
        assertEquals("processed", r1.getStatus());
        assertEquals("processed", r2.getStatus());
    }

    @Test
    void testRestockValidGoodItem() {
        ReturnOrder order = new ReturnOrder(10, null, "customer-remorse", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(10, order, "SKU0005", 15, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        service.processAllReturns();

        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        assertFalse(lots.isEmpty());
        assertTrue(lots.stream().anyMatch(lot -> lot.getSku() == 5 && lot.getQty() == 15));
    }

    @Test
    void testDiscardExpiredItem() {
        ReturnOrder order = new ReturnOrder(20, null, "expired", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(20, order, "SKU0010", 7, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        service.processAllReturns();

        ReturnLine updated = repositories.getReturnLineRepository().getReturnLineById(20);
        assertEquals("discarded", updated.getCondition());
    }

    @Test
    void testPartialRestockSplitQty() {
        ReturnOrder order = new ReturnOrder(30, null, "customer-remorse", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(30, order, "SKU0015", 20, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        service.processAllReturns();

        List<InventoryLot> lots = repositories.getInventoryLotRepository().getAllInventoryLots();
        long restockedQty = lots.stream()
                .filter(lot -> lot.getSku() == 15)
                .mapToInt(InventoryLot::getQty)
                .sum();

        assertEquals(20, restockedQty);
    }


    @Test
    void testInvalidReturnMissingSKU() {
        ReturnOrder order = new ReturnOrder(40, null, "customer-remorse", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(40, order, null, 10, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        assertDoesNotThrow(() -> service.processAllReturns());
        assertEquals("processed", order.getStatus());
    }

    @Test
    void testInvalidReturnZeroQty() {
        ReturnOrder order = new ReturnOrder(50, null, "customer-remorse", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(50, order, "SKU0020", 0, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        assertDoesNotThrow(() -> service.processAllReturns());
        assertEquals("processed", order.getStatus());
    }

    @Test
    void testAuditLogFormat() throws IOException {
        ReturnOrder order = new ReturnOrder(60, null, "customer-remorse", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(60, order, "SKU0025", 12, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        service.processAllReturns();

        Path logPath = Path.of("src/main/java/app/service/AuditLogs/return_inspection_audit.log");
        assertTrue(Files.exists(logPath));

        String content = Files.readString(logPath);
        assertTrue(content.contains("returnId=R60"));
        assertTrue(content.contains("sku=SKU0025"));
        assertTrue(content.contains("qty=12"));
    }

    @Test
    void testReinjectedStockVisibility() {
        ReturnOrder order = new ReturnOrder(70, null, "customer-remorse", "received",
                LocalDateTime.now());
        repositories.getReturnOrderRepository().addReturnOrder(order);

        ReturnLine line = new ReturnLine(70, order, "SKU0030", 25, "good");
        repositories.getReturnLineRepository().addReturnLine(line);

        service.processAllReturns();

        List<InventoryBalance> balances = inventoryController.getAllInventoryBalances();
        boolean stockVisible = balances.stream()
                .anyMatch(balance -> balance.getLots().stream()
                        .anyMatch(lot -> lot.getSku() == 30 && lot.getQty() == 25));

        assertTrue(stockVisible);
    }
}
