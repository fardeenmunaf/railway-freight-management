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

/**
 * Service responsável por gerir o processamento de devoluções no armazém.
 * <p>
 * O processo segue as regras da User Story:
 * - Produtos devolvidos são colocados em quarentena.
 * - São inspecionados por ordem inversa de chegada (mais recentes primeiro).
 * - Dependendo da razão e condição, são reabastecidos (restocked) ou descartados.
 * - Todas as ações são registadas num ficheiro de auditoria.
 * </p>
 */
public class ReturnService {

    /** Caminho do ficheiro de log de auditoria. */
    private static final String AUDIT_LOG_FILE = "src/main/java/app/service/AuditLogs/return_inspection_audit.log";

    /** Repositórios partilhados da aplicação. */
    private final Repositories repositories;

    /** Controlador de inventário usado para atualizar saldos e lotes. */
    private final InventoryBalanceController inventoryController;

    /**
     * Construtor padrão do serviço.
     * Inicializa os repositórios e o controlador de inventário.
     */
    public ReturnService() {
        this.repositories = Repositories.getInstance();
        this.inventoryController = new InventoryBalanceController();
    }

    /**
     * Coloca todas as devoluções com estado "received" em quarentena.
     * <p>
     * Este método atualiza o estado das devoluções para "quarantine"
     * e guarda as alterações no repositório.
     * </p>
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
     * Inspeciona todas as devoluções que estão em quarentena.
     * <p>
     * As devoluções são processadas por ordem inversa de chegada (mais recentes primeiro).
     * Cada devolução é inspecionada linha a linha, determinando se deve ser
     * reabastecida ou descartada.
     * </p>
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

    /**
     * Processa uma única devolução e decide o destino de cada linha (produto).
     *
     * @param returnOrder a devolução a ser processada.
     * @param logCounter  contador de logs para gerar timestamps únicos.
     */
    private void processReturn(ReturnOrder returnOrder, AtomicInteger logCounter) {
        List<ReturnLine> returnLines = repositories.getReturnLineRepository()
                .getReturnLinesByReturnId(returnOrder.getReturnId());

        for (ReturnLine line : returnLines) {
            String reason = returnOrder.getReason().toLowerCase();
            String action;

            // Casos especiais: ciclo de contagem vai para QA Inspection
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
     * Envia uma linha de devolução para inspeção de qualidade (QA).
     *
     * @param line a linha de devolução a ser enviada para QA.
     */
    private void sendToQaInspection(ReturnLine line) {
        Integer qaId = generateNewQaId();
        QaInspection inspection = new QaInspection(
                qaId,
                line.getReturnLineId(),
                "pending",
                LocalDateTime.now()
        );

        repositories.getQaInspectionRepository().addQaInspection(inspection);
        System.out.println("Return line " + line.getReturnLineId() + " enviada para QA inspection.");
    }

    /**
     * Processa o resultado de uma inspeção de QA.
     *
     * @param qaId     identificador da inspeção de QA.
     * @param approved resultado da inspeção (true = aprovado, false = rejeitado).
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

    /**
     * Determina a ação apropriada para um produto devolvido.
     *
     * @param reason    razão da devolução (ex: damaged, expired, customer-remorse).
     * @param condition condição física reportada do produto.
     * @return ação a ser tomada ("Restocked", "Discarded" ou "Sent to QA Inspection").
     */
    private String determineAction(String reason, String condition) {
        switch (reason.toLowerCase()) {
            case "customer-remorse":
                return (condition == null || "good".equalsIgnoreCase(condition)) ? "Restocked" : "Discarded";
            case "damaged":
            case "expired":
                return "Discarded";
            case "cycle-count":
                return "Sent to QA Inspection";
            default:
                return "Discarded";
        }
    }

    /**
     * Reabastece um item no inventário criando um novo lote (lot).
     *
     * @param order ordem de devolução original.
     * @param line  linha de devolução a ser reabastecida.
     */
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

    /**
     * Descarta um item marcando-o como inutilizável.
     *
     * @param line linha de devolução a ser descartada.
     */
    private void discardItem(ReturnLine line) {
        line.setCondition("discarded");
        repositories.getReturnLineRepository().updateReturnLine(line.getReturnLineId(), line);
    }

    /**
     * Atualiza o saldo de inventário com base num novo lote inserido.
     *
     * @param lot lote de inventário a ser adicionado.
     */
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

    /**
     * Insere um novo lote na baia apropriada seguindo a regra FEFO/FIFO.
     *
     * @param lot lote de inventário a ser inserido.
     */
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

    /**
     * Localiza uma baia disponível compatível com o SKU e com capacidade suficiente.
     */
    private WarehouseBay findAvailableBay(Integer sku, Integer requiredCapacity) {
        return repositories.getWarehouseBayRepository().getAllWarehouseBays().stream()
                .filter(b -> hasCapacityForLot(b) && isBayCompatibleWithSku(b, sku))
                .findFirst().orElse(null);
    }

    /** Verifica se uma baia tem capacidade para mais um lote. */
    private boolean hasCapacityForLot(WarehouseBay bay) {
        InventoryBalance balance = findBalanceByBay(bay.getBayId());
        int current = balance != null ? balance.getLots().size() : 0;
        return bay.getCapacityBox() - current >= 1;
    }

    /** Verifica se a baia é compatível com o SKU. */
    private boolean isBayCompatibleWithSku(WarehouseBay bay, Integer sku) {
        InventoryBalance balance = findBalanceByBay(bay.getBayId());
        return balance == null || balance.getLots().isEmpty() || balance.getLots().get(0).getSku().equals(sku);
    }

    /** Procura um saldo de inventário associado a uma baia. */
    private InventoryBalance findBalanceByBay(Integer bayId) {
        return inventoryController.getAllInventoryBalances().stream()
                .filter(b -> b.getBay() != null && bayId.equals(b.getBay().getBayId()))
                .findFirst().orElse(null);
    }

    /**
     * Calcula a posição de inserção de um novo lote seguindo a regra FEFO/FIFO.
     */
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

    /** Procura um saldo de inventário associado a um SKU específico. */
    private InventoryBalance findBalanceBySku(Integer sku) {
        return inventoryController.getAllInventoryBalances().stream()
                .filter(b -> !b.getLots().isEmpty() && sku.equals(b.getLots().get(0).getSku()))
                .findFirst().orElse(null);
    }

    /** Gera um novo identificador de lote. */
    private Integer generateNewLotId() {
        return repositories.getInventoryLotRepository().getAllInventoryLots().stream()
                .mapToInt(InventoryLot::getLotId).max().orElse(0) + 1;
    }

    /** Gera um novo identificador de saldo de inventário. */
    private Integer generateNewBalanceId() {
        List<InventoryBalance> allBalances = inventoryController.getAllInventoryBalances();
        return allBalances.stream().mapToInt(InventoryBalance::getBalanceId).min().orElse(0) - 1;
    }

    /** Gera um novo identificador para inspeção de QA. */
    private Integer generateNewQaId() {
        return repositories.getQaInspectionRepository().getAllQaInspections().stream()
                .mapToInt(QaInspection::getQaId).max().orElse(0) + 1;
    }

    /**
     * Regista a ação de inspeção no ficheiro de auditoria.
     *
     * @param order      ordem de devolução.
     * @param line       linha processada.
     * @param action     ação executada (Restocked, Discarded, etc.).
     * @param logCounter contador para gerar timestamps únicos.
     */
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

    /**
     * Executa todo o fluxo de processamento das devoluções:
     * <ul>
     *     <li>Move devoluções recebidas para quarentena</li>
     *     <li>Inspeciona e processa as devoluções</li>
     *     <li>Gera logs de auditoria</li>
     * </ul>
     */
    public void processAllReturns() {
        System.out.println("Starting return processing workflow...");
        quarantineReturns();
        inspectQuarantinedReturns();
        System.out.println("Return processing completed. Check " + AUDIT_LOG_FILE);
    }
}
