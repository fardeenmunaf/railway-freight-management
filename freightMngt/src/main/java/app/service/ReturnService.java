package app.service;

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

    public ReturnService() {
        this.repositories = Repositories.getInstance();
        this.inventoryController = new InventoryBalanceController();
    }

    /**
     * Põe returns com status "received" em quarentena.
     */
    public void quarantineReturns() {
        List<ReturnOrder> allReturns = repositories.getReturnOrderRepository().getAllReturnOrders();

        List<ReturnOrder> newReturns = allReturns.stream()
                .filter(r -> "received".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());

        for (ReturnOrder r : newReturns) {
            r.setStatus("quarantine");
            repositories.getReturnOrderRepository().updateReturnOrder(r.getReturnId(), r);
        }

        System.out.println("Quarantined " + newReturns.size() + " return orders.");
    }

    /**
     * Inspeciona returns em quarentena.
     */
    public void inspectQuarantinedReturns() {
        List<ReturnOrder> quarantinedReturns = repositories.getReturnOrderRepository().getAllReturnOrders().stream()
                .filter(r -> "quarantine".equalsIgnoreCase(r.getStatus()))
                .sorted(
                        Comparator.comparing(ReturnOrder::getReceivedAt).reversed()
                                .thenComparingInt(ReturnOrder::getReturnId)
                )
                .collect(Collectors.toList());

        AtomicInteger logCounter = new AtomicInteger(0);

        for (ReturnOrder returnOrder : quarantinedReturns) {
            processReturn(returnOrder, logCounter);
        }
    }

    private void processReturn(ReturnOrder returnOrder, AtomicInteger logCounter) {
        List<ReturnLine> returnLines = repositories.getReturnLineRepository()
                .getReturnLinesByReturnId(returnOrder.getReturnId());

        for (ReturnLine line : returnLines) {
            String reason = returnOrder.getReason().toLowerCase();
            String action;

            // Se for cycle-count, manda primeiro para QA
            if ("cycle-count".equals(reason)) {
                sendToQaInspection(line);
                action = "Sent to QA Inspection";
            } else {
                action = determineAction(reason, line.getCondition());

                if ("Restocked".equals(action)) {
                    restockItem(returnOrder, line);
                } else {
                    discardItem(line);
                }
            }

            logInspection(returnOrder, line, action, logCounter);
        }

        returnOrder.setStatus("processed");
        repositories.getReturnOrderRepository().updateReturnOrder(returnOrder.getReturnId(), returnOrder);
    }

    /**
     * Envia item para QA Inspection antes de decidir restock/discard.
     */
    private void sendToQaInspection(ReturnLine line) {
        Integer qaId = generateNewQaId();
        QaInspection inspection = new QaInspection(
                qaId,
                line.getReturnLineId(),
                "pending", // ainda não inspecionado
                LocalDateTime.now()
        );

        repositories.getQaInspectionRepository().addQaInspection(inspection);
        System.out.println("Return line " + line.getReturnLineId() + " enviada para QA inspection.");
    }

    /**
     * Após QA Inspection decidir, esta função pode ser chamada.
     */
    public void processQaResult(int qaId, boolean approved) {
        QaInspection qa = repositories.getQaInspectionRepository().getQaInspectionById(qaId);
        if (qa == null) {
            System.err.println("QA Inspection not found: " + qaId);
            return;
        }

        ReturnLine line = repositories.getReturnLineRepository().getReturnLineById(qa.getReturnLineId());
        ReturnOrder order = repositories.getReturnOrderRepository().getReturnOrderById(line.getReturnLineId());

        if (approved) {
            qa.setStatus("approved");
            restockItem(order, line);
            logInspection(order, line, "Restocked (after QA)", new AtomicInteger(0));
        } else {
            qa.setStatus("rejected");
            discardItem(line);
            logInspection(order, line, "Discarded (after QA)", new AtomicInteger(0));
        }

        repositories.getQaInspectionRepository().updateQaInspection(qaId, qa);
    }

    private String determineAction(String reason, String condition) {
        switch (reason.toLowerCase()) {
            case "customer-remorse":
                return (condition == null || "good".equalsIgnoreCase(condition)) ? "Restocked" : "Discarded";
            case "damaged":
            case "expired":
                return "Discarded";
            case "cycle-count":
                return "Sent to QA Inspection"; // handled before
            default:
                return "Discarded";
        }
    }

    private void restockItem(ReturnOrder order, ReturnLine line) {
        try {
            String newBoxId = "RET-" + order.getReturnId();
            Integer lotId = generateNewLotId();
            Integer sku = Integer.parseInt(line.getSku().replaceAll("[^0-9]", ""));
            LocalDateTime receivedAt = order.getReceivedAt();
            LocalDateTime expiryDate = null;

            InventoryLot lot = new InventoryLot(lotId, sku, line.getQty(), expiryDate, receivedAt);
            repositories.getInventoryLotRepository().addInventoryLot(lot);

            updateInventoryBalance(lot);
            insertIntoBay(lot);
        } catch (Exception e) {
            System.err.println("Error restocking item: " + e.getMessage());
        }
    }

    private void discardItem(ReturnLine line) {
        line.setCondition("discarded");
        repositories.getReturnLineRepository().updateReturnLine(line.getReturnLineId(), line);
    }

    private void updateInventoryBalance(InventoryLot lot) {
        InventoryBalance existingBalance = findBalanceBySku(lot.getSku());
        if (existingBalance != null) {
            existingBalance.addLot(lot);
            inventoryController.updateInventoryBalance(existingBalance.getBalanceId(), existingBalance);
        } else {
            InventoryBalance balance = new InventoryBalance(generateNewBalanceId(), null,
                    new ArrayList<>(List.of(lot)), lot.getQty());
            inventoryController.createInventoryBalance(balance);
        }
    }

    private void insertIntoBay(InventoryLot lot) {
        WarehouseBay bay = findAvailableBay(lot.getSku(), 1);
        if (bay == null) {
            System.err.println("No available bay for SKU " + lot.getSku());
            return;
        }

        InventoryBalance balance = findBalanceByBay(bay.getBayId());
        if (balance != null) {
            List<InventoryLot> lots = new ArrayList<>(balance.getLots());
            int pos = findFEFOInsertPosition(lots, lot);
            lots.add(pos, lot);
            balance.setLots(lots);
            balance.setQty(balance.getQty() + lot.getQty());
            inventoryController.updateInventoryBalance(balance.getBalanceId(), balance);
        } else {
            InventoryBalance newBalance = new InventoryBalance(generateNewBalanceId(), bay,
                    new ArrayList<>(List.of(lot)), lot.getQty());
            inventoryController.createInventoryBalance(newBalance);
        }
    }

    private WarehouseBay findAvailableBay(Integer sku, Integer requiredCapacity) {
        return repositories.getWarehouseBayRepository().getAllWarehouseBays().stream()
                .filter(b -> hasCapacityForLot(b) && isBayCompatibleWithSku(b, sku))
                .findFirst().orElse(null);
    }

    private boolean hasCapacityForLot(WarehouseBay bay) {
        InventoryBalance balance = findBalanceByBay(bay.getBayId());
        int current = balance != null ? balance.getLots().size() : 0;
        return bay.getCapacityBox() - current >= 1;
    }

    private boolean isBayCompatibleWithSku(WarehouseBay bay, Integer sku) {
        InventoryBalance balance = findBalanceByBay(bay.getBayId());
        return balance == null || balance.getLots().isEmpty() || balance.getLots().get(0).getSku().equals(sku);
    }

    private InventoryBalance findBalanceByBay(Integer bayId) {
        return inventoryController.getAllInventoryBalances().stream()
                .filter(b -> b.getBay() != null && bayId.equals(b.getBay().getBayId()))
                .findFirst().orElse(null);
    }

    private int findFEFOInsertPosition(List<InventoryLot> lots, InventoryLot newLot) {
        for (int i = 0; i < lots.size(); i++) {
            InventoryLot current = lots.get(i);
            if (newLot.getExpDate() == null && current.getExpDate() == null) {
                if (newLot.getReceivedDate().isBefore(current.getReceivedDate())) return i;
            } else if (newLot.getExpDate() != null && (current.getExpDate() == null ||
                    newLot.getExpDate().isBefore(current.getExpDate()))) return i;
        }
        return lots.size();
    }

    private InventoryBalance findBalanceBySku(Integer sku) {
        return inventoryController.getAllInventoryBalances().stream()
                .filter(b -> !b.getLots().isEmpty() && sku.equals(b.getLots().get(0).getSku()))
                .findFirst().orElse(null);
    }

    private Integer generateNewLotId() {
        return repositories.getInventoryLotRepository().getAllInventoryLots().stream()
                .mapToInt(InventoryLot::getLotId).max().orElse(0) + 1;
    }

    private Integer generateNewBalanceId() {
        List<InventoryBalance> allBalances = inventoryController.getAllInventoryBalances();
        return allBalances.stream().mapToInt(InventoryBalance::getBalanceId).min().orElse(0) - 1;
    }

    private Integer generateNewQaId() {
        return repositories.getQaInspectionRepository().getAllQaInspections().stream()
                .mapToInt(QaInspection::getQaId).max().orElse(0) + 1;
    }

    private void logInspection(ReturnOrder order, ReturnLine line, String action, AtomicInteger logCounter) {
        try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE, true)) {
            LocalDateTime timestamp = order.getReceivedAt().plusSeconds(logCounter.getAndIncrement());
            String log = String.format("%s | returnId=R%d | sku=%s | action=%s | qty=%d",
                    timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    order.getReturnId(), line.getSku(), action, line.getQty());
            writer.write(log + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing audit log: " + e.getMessage());
        }
    }

    public void processAllReturns() {
        System.out.println("Starting return processing workflow...");
        quarantineReturns();
        inspectQuarantinedReturns();
        System.out.println("Return processing completed. Check " + AUDIT_LOG_FILE);
    }
}
