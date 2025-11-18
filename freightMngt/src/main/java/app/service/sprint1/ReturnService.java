package app.service.sprint1;

import app.controller.InventoryBalanceController;
import app.model.*;
import app.repositories.Repositories;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReturnService {

    private static final String AUDIT_LOG_FILE = "src/main/java/app/service/AuditLogs/return_inspection_audit.log";
    private final Repositories repositories;
    private final InventoryBalanceController inventoryController;

    private final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReturnService() {
        this.repositories = Repositories.getInstance();
        this.inventoryController = new InventoryBalanceController();
    }

    public void quarantineReturns() {
        repositories.getReturnOrderRepository().getAllReturnOrders().stream()
                .filter(r -> "received".equalsIgnoreCase(r.getStatus()))
                .forEach(r -> {
                    r.setStatus("quarantine");
                    repositories.getReturnOrderRepository().updateReturnOrder(r.getReturnId(), r);
                });
    }

    public void inspectQuarantinedReturns() {
        List<ReturnOrder> quarantined = repositories.getReturnOrderRepository()
                .getAllReturnOrders().stream()
                .filter(r -> "quarantine".equalsIgnoreCase(r.getStatus()))
                .sorted(
                        Comparator.comparing(ReturnOrder::getReceivedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparingInt(ReturnOrder::getReturnId)
                )
                .collect(Collectors.toList());

        AtomicInteger logCounter = new AtomicInteger(0);
        quarantined.forEach(r -> processReturn(r, logCounter));
    }

    private void processReturn(ReturnOrder order, AtomicInteger logCounter) {
        if (order == null || order.getReturnId() == null) {
            logWarning(null, null, "Invalid returnOrder or returnId null", logCounter);
            return;
        }
        if (order.getReceivedAt() == null) {
            logWarning(order, null, "ReturnOrder.receivedAt is null", logCounter);
            return;
        }

        List<ReturnLine> lines = repositories.getReturnLineRepository()
                .getReturnLinesByReturnId(order.getReturnId());

        for (ReturnLine line : lines) {
            if (line == null) {
                logWarning(order, null, "Null return line", logCounter);
                continue;
            }
            if (line.getQty() == null || line.getQty() <= 0) {
                logWarning(order, line, "Invalid qty <= 0", logCounter);
                continue;
            }
            if (line.getSku() == null || line.getSku().trim().isEmpty()) {
                logWarning(order, line, "Missing SKU", logCounter);
                continue;
            }
            processReturnLine(order, line, logCounter);
        }

        order.setStatus("processed");
        repositories.getReturnOrderRepository().updateReturnOrder(order.getReturnId(), order);
    }

    private void processReturnLine(ReturnOrder order, ReturnLine line, AtomicInteger logCounter) {
        String reason = Optional.ofNullable(order.getReason()).orElse("").toLowerCase();
        String condition = Optional.ofNullable(line.getCondition()).orElse("").toLowerCase();

        int qtyTotal = line.getQty();
        int qtyRestock = 0;
        int qtyDiscard = 0;

        switch (reason) {
            case "customer-remorse" -> {
                if ("good".equals(condition) || condition.isEmpty()) qtyRestock = qtyTotal;
                else qtyDiscard = qtyTotal;
            }
            case "damaged", "expired" -> qtyDiscard = qtyTotal;
            case "cycle-count" -> {
                sendToQaInspection(line);
                logInspection(order, line, "Sent to QA Inspection", qtyTotal, logCounter);
                return;
            }
            default -> qtyDiscard = qtyTotal;
        }

        if (qtyRestock > 0 && restockItem(order, line)) {
            logInspection(order, line, "Restocked (RET-" + order.getReturnId() + " → Lot)", qtyRestock, logCounter);
        } else if (qtyRestock > 0) {
            qtyDiscard += qtyRestock;
        }

        if (qtyDiscard > 0) {
            line.setQty(qtyDiscard);
            discardItem(line);
            logInspection(order, line, "Discarded", qtyDiscard, logCounter);
        }
    }

    private void sendToQaInspection(ReturnLine line) {
        QaInspection inspection = new QaInspection(
                generateNewQaId(),
                line.getReturnLineId(),
                "pending",
                LocalDateTime.now()
        );
        repositories.getQaInspectionRepository().addQaInspection(inspection);
    }

    public void processQaResult(int qaId, boolean approved) {
        QaInspection qa = repositories.getQaInspectionRepository().getQaInspectionById(qaId);
        if (qa == null) return;

        ReturnLine line = repositories.getReturnLineRepository().getReturnLineById(qa.getReturnLineId());
        if (line == null) return;

        ReturnOrder order = line.getReturnOrder() != null ?
                repositories.getReturnOrderRepository().getReturnOrderById(line.getReturnOrder().getReturnId()) : null;

        qa.setStatus(approved ? "approved" : "rejected");
        if (approved) {
            restockItem(order, line);
            logInspection(order, line, "Restocked (after QA)", line.getQty(), new AtomicInteger(0));
        } else {
            discardItem(line);
            logInspection(order, line, "Discarded (after QA)", line.getQty(), new AtomicInteger(0));
        }

        repositories.getQaInspectionRepository().updateQaInspection(qaId, qa);
    }

    private boolean restockItem(ReturnOrder order, ReturnLine line) {
        try {
            int sku = Integer.parseInt(line.getSku().replaceAll("[^0-9]", ""));
            InventoryLot lot = new InventoryLot(
                    generateNewLotId(),
                    sku,
                    line.getQty(),
                    null,
                    LocalDateTime.now()
            );
            repositories.getInventoryLotRepository().addInventoryLot(lot);
            updateInventoryBalance(lot);
            insertIntoBay(lot);
            return true;
        } catch (Exception e) {
            logWarning(order, line, "Restock failed: " + e.getMessage(), new AtomicInteger(0));
            return false;
        }
    }

    private void discardItem(ReturnLine line) {
        line.setCondition("discarded");
        repositories.getReturnLineRepository().updateReturnLine(line.getReturnLineId(), line);
    }

    private void updateInventoryBalance(InventoryLot lot) {
        InventoryBalance balance = findBalanceBySku(lot.getSku());
        if (balance != null) {
            balance.addLot(lot);
            balance.setQty(balance.getQty() + lot.getQty());
            inventoryController.updateInventoryBalance(balance.getBalanceId(), balance);
        } else {
            InventoryBalance newBalance = new InventoryBalance(generateNewBalanceId(), null, List.of(lot), lot.getQty());
            inventoryController.createInventoryBalance(newBalance);
        }
    }

    private void insertIntoBay(InventoryLot lot) {
        WarehouseBay bay = findAvailableBay(lot.getSku());
        if (bay == null) return;

        InventoryBalance balance = findBalanceByBay(bay.getBayId());
        if (balance == null) {
            InventoryBalance newBalance = new InventoryBalance(generateNewBalanceId(), bay, new ArrayList<>(List.of(lot)), lot.getQty());
            inventoryController.createInventoryBalance(newBalance);
            return;
        }

        List<InventoryLot> lots = new ArrayList<>(balance.getLots());
        int pos = findFEFOInsertPosition(lots, lot);
        lots.add(pos, lot);
        balance.setLots(lots);
        balance.setQty(balance.getQty() + lot.getQty());
        inventoryController.updateInventoryBalance(balance.getBalanceId(), balance);
    }

    private WarehouseBay findAvailableBay(Integer sku) {
        return repositories.getWarehouseBayRepository().getAllWarehouseBays().stream()
                .filter(b -> hasCapacityForLot(b) && isBayCompatibleWithSku(b, sku))
                .findFirst().orElse(null);
    }

    private boolean hasCapacityForLot(WarehouseBay bay) {
        InventoryBalance bal = findBalanceByBay(bay.getBayId());
        int currentLots = bal != null ? bal.getLots().size() : 0;
        return bay.getCapacityBox() > currentLots;
    }

    private boolean isBayCompatibleWithSku(WarehouseBay bay, Integer sku) {
        InventoryBalance bal = findBalanceByBay(bay.getBayId());
        return bal == null || bal.getLots().isEmpty() || Objects.equals(bal.getLots().get(0).getSku(), sku);
    }

    private InventoryBalance findBalanceByBay(Integer bayId) {
        return inventoryController.getAllInventoryBalances().stream()
                .filter(b -> b.getBay() != null && bayId.equals(b.getBay().getBayId()))
                .findFirst().orElse(null);
    }

    private InventoryBalance findBalanceBySku(Integer sku) {
        return inventoryController.getAllInventoryBalances().stream()
                .filter(b -> !b.getLots().isEmpty() && sku.equals(b.getLots().get(0).getSku()))
                .findFirst().orElse(null);
    }

    private int findFEFOInsertPosition(List<InventoryLot> lots, InventoryLot newLot) {
        for (int i = 0; i < lots.size(); i++) {
            InventoryLot current = lots.get(i);
            if (newLot.getExpDate() == null && current.getExpDate() == null) {
                if (newLot.getReceivedDate().isBefore(current.getReceivedDate())) return i;
            } else if (newLot.getExpDate() != null &&
                    (current.getExpDate() == null || newLot.getExpDate().isBefore(current.getExpDate()))) {
                return i;
            }
        }
        return lots.size();
    }

    private int generateNewLotId() {
        return repositories.getInventoryLotRepository().getAllInventoryLots().stream()
                .mapToInt(InventoryLot::getLotId).max().orElse(0) + 1;
    }

    private int generateNewBalanceId() {
        return inventoryController.getAllInventoryBalances().stream()
                .mapToInt(InventoryBalance::getBalanceId).min().orElse(0) - 1;
    }

    private int generateNewQaId() {
        return repositories.getQaInspectionRepository().getAllQaInspections().stream()
                .mapToInt(QaInspection::getQaId).max().orElse(0) + 1;
    }

    private void logInspection(ReturnOrder order, ReturnLine line, String action, int qty, AtomicInteger logCounter) {
        logToFile(order, line, "action=" + action, qty, logCounter);
    }

    private void logWarning(ReturnOrder order, ReturnLine line, String reason, AtomicInteger logCounter) {
        logToFile(order, line, "action=Invalid | reason=" + reason,
                line != null && line.getQty() != null ? line.getQty() : 0, logCounter);
    }

    private void logToFile(ReturnOrder order, ReturnLine line, String details, int qty, AtomicInteger counter) {
        try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE, true)) {
            LocalDateTime ts = LocalDateTime.now().plusNanos(counter.getAndIncrement());
            String log = String.format(
                    "%s | returnId=%s | sku=%s | %s | qty=%d",
                    ts.format(LOG_FMT),
                    order != null && order.getReturnId() != null ? "R" + order.getReturnId() : "R?",
                    line != null && line.getSku() != null ? line.getSku() : "UNKNOWN",
                    details,
                    qty
            );
            writer.write(log + System.lineSeparator());
        } catch (IOException ignored) {}
    }

    public void processAllReturns() {
        quarantineReturns();
        inspectQuarantinedReturns();
    }
}
