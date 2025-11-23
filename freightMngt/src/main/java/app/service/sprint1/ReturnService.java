package app.service.sprint1;

import app.controller.InventoryBalanceController;
import app.model.*;
import app.repositories.Repositories;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReturnService {

    private static final String AUDIT_LOG_FILE = "src/main/java/app/service/AuditLogs/return_inspection_audit.log";
    private final Repositories repositories;
    private final InventoryBalanceController inventoryController;
    private final AuditLogger auditLogger;

    public ReturnService() {
        this.repositories = Repositories.getInstance();
        this.inventoryController = new InventoryBalanceController();
        this.auditLogger = new AuditLogger();
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

        quarantined.forEach(this::processReturn);
        auditLogger.flush();
    }

    private void processReturn(ReturnOrder order) {
        if (order == null || order.getReturnId() == null) {
            auditLogger.log(new InspectionEvent(null, null, "Invalid", 0, "Invalid returnOrder or returnId null"));
            return;
        }
        if (order.getReceivedAt() == null) {
            auditLogger.log(new InspectionEvent(order.getReturnId(), null, "Invalid", 0, "ReturnOrder.receivedAt is null"));
            return;
        }

        List<ReturnLine> lines = repositories.getReturnLineRepository()
                .getReturnLinesByReturnId(order.getReturnId());

        for (ReturnLine line : lines) {
            if (line == null) {
                auditLogger.log(new InspectionEvent(order.getReturnId(), null, "Invalid", 0, "Null return line"));
                continue;
            }
            if (line.getQty() == null || line.getQty() <= 0) {
                auditLogger.log(new InspectionEvent(order.getReturnId(), line.getSku(), "Invalid", 0, "Invalid qty <= 0"));
                continue;
            }
            if (line.getSku() == null || line.getSku().trim().isEmpty()) {
                auditLogger.log(new InspectionEvent(order.getReturnId(), null, "Invalid", line.getQty(), "Missing SKU"));
                continue;
            }
            processReturnLine(order, line);
        }

        order.setStatus("processed");
        repositories.getReturnOrderRepository().updateReturnOrder(order.getReturnId(), order);
    }

    private void processReturnLine(ReturnOrder order, ReturnLine line) {
        String reason = Optional.ofNullable(order.getReason()).orElse("").toLowerCase();
        String condition = Optional.ofNullable(line.getCondition()).orElse("").toLowerCase();

        int qtyTotal = line.getQty();
        int qtyRestock = 0;
        int qtyDiscard = 0;

        switch (reason) {
            case "customer-remorse" -> {
                if ("good".equals(condition) || condition.isEmpty()) {
                    qtyRestock = qtyTotal;
                } else {
                    qtyDiscard = qtyTotal;
                }
            }
            case "damaged", "expired" -> qtyDiscard = qtyTotal;
            case "cycle-count" -> {
                sendToQaInspection(line);
                auditLogger.log(new InspectionEvent(order.getReturnId(), line.getSku(), "QA Inspection", qtyTotal, "Sent to QA"));
                return;
            }
            default -> qtyDiscard = qtyTotal;
        }

        if (qtyRestock > 0 && qtyDiscard > 0) {
            line.setQty(qtyDiscard);
            discardItem(line);
            auditLogger.log(new InspectionEvent(order.getReturnId(), line.getSku(), "Discarded", qtyDiscard, null));

            ReturnLine restockLine = new ReturnLine();
            restockLine.setReturnLineId(null);
            restockLine.setReturnOrder(line.getReturnOrder());
            restockLine.setSku(line.getSku());
            restockLine.setQty(qtyRestock);
            restockLine.setCondition("good");

            repositories.getReturnLineRepository().addReturnLine(restockLine);

            if (restockItem(order, restockLine)) {
                auditLogger.log(new InspectionEvent(order.getReturnId(), restockLine.getSku(), "Restocked", qtyRestock, "RET-" + order.getReturnId() + " → Lot"));
            } else {
                auditLogger.log(new InspectionEvent(order.getReturnId(), restockLine.getSku(), "Invalid", qtyRestock, "Restock failed on partial line"));
            }
        } else if (qtyRestock > 0) {
            if (restockItem(order, line)) {
                auditLogger.log(new InspectionEvent(order.getReturnId(), line.getSku(), "Restocked", qtyRestock, "RET-" + order.getReturnId() + " → Lot"));
            } else {
                qtyDiscard += qtyRestock;
                line.setQty(qtyDiscard);
                discardItem(line);
                auditLogger.log(new InspectionEvent(order.getReturnId(), line.getSku(), "Discarded", qtyDiscard, null));
            }
        } else if (qtyDiscard > 0) {
            line.setQty(qtyDiscard);
            discardItem(line);
            auditLogger.log(new InspectionEvent(order.getReturnId(), line.getSku(), "Discarded", qtyDiscard, null));
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
            auditLogger.log(new InspectionEvent(order != null ? order.getReturnId() : null, line.getSku(), "Restocked", line.getQty(), "After QA - RET-" + (order != null ? order.getReturnId() : "?") + " → Lot"));
        } else {
            discardItem(line);
            auditLogger.log(new InspectionEvent(order != null ? order.getReturnId() : null, line.getSku(), "Discarded", line.getQty(), "After QA"));
        }

        repositories.getQaInspectionRepository().updateQaInspection(qaId, qa);
        auditLogger.flush();
    }

    private boolean restockItem(ReturnOrder order, ReturnLine line) {
        try {
            int sku = Integer.parseInt(line.getSku().replaceAll("[^0-9]", ""));
            LocalDateTime receivedAt = order != null ? order.getReceivedAt() : LocalDateTime.now();

            InventoryLot lot = new InventoryLot(
                    generateNewLotId(),
                    sku,
                    line.getQty(),
                    null,
                    receivedAt
            );
            repositories.getInventoryLotRepository().addInventoryLot(lot);
            updateInventoryBalance(lot);
            insertIntoBay(lot);
            return true;
        } catch (Exception e) {
            auditLogger.log(new InspectionEvent(order != null ? order.getReturnId() : null, line.getSku(), "Invalid", line.getQty(), "Restock failed: " + e.getMessage()));
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

    public void processAllReturns() {
        quarantineReturns();
        inspectQuarantinedReturns();
    }

    // Classe interna para eventos de auditoria
    private static class InspectionEvent {
        private final LocalDateTime timestamp;
        private final Integer returnId;
        private final String sku;
        private final String action;
        private final int quantity;
        private final String details;

        public InspectionEvent(Integer returnId, String sku, String action, int quantity, String details) {
            this.timestamp = LocalDateTime.now();
            this.returnId = returnId;
            this.sku = sku;
            this.action = action;
            this.quantity = quantity;
            this.details = details;
        }

        public String toLogLine() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            return String.format("%s | returnId=%s | sku=%s | action=%s | qty=%d%s",
                    timestamp.format(fmt),
                    returnId != null ? "R" + returnId : "R?",
                    sku != null ? sku : "UNKNOWN",
                    action,
                    quantity,
                    details != null && !details.isEmpty() ? " | " + details : ""
            );
        }
    }

    // Classe interna para gestão de auditoria
    private static class AuditLogger {
        private final Queue<InspectionEvent> eventQueue = new LinkedList<>();

        public void log(InspectionEvent event) {
            eventQueue.offer(event);
        }

        public void flush() {
            try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE, true)) {
                while (!eventQueue.isEmpty()) {
                    InspectionEvent event = eventQueue.poll();
                    writer.write(event.toLogLine() + System.lineSeparator());
                }
            } catch (IOException e) {
                // Log error appropriately
            }
        }
    }
}
