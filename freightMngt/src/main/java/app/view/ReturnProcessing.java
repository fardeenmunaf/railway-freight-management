package app.view;

import app.model.ReturnLine;
import app.model.ReturnOrder;
import app.repositories.Repositories;
import app.service.sprint1.ReturnService;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * View class responsible for handling the inventory dispatch user interface.
 * This class provides a console-based interface for users to dispatch stock
 * from inventory using FEFO/FIFO rules.
 */
public class ReturnProcessing {
    /**
     * Displays the return processing interface and handles user interaction.
     * Allows users to import returns to quarantine or process existing quarantined returns.
     *
     * @param scanner the Scanner object used for reading user input
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              RETURN PROCESSING (QUARANTINE)");
        System.out.println("=".repeat(60));
        System.out.println("Type 'b' at any time to return to menu.\n");

        ReturnService returnService = new ReturnService();

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Import/Queue returns to quarantine");
            System.out.println("2. Process quarantined returns");
            System.out.println("3. Back to main menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(choice) || "3".equals(choice)) return;

            switch (choice) {
                case "1":
                    importReturnsToQuarantine(scanner, returnService);
                    break;
                case "2":
                    processQuarantinedReturns(scanner, returnService);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    /**
     * Handles importing/queueing returns to quarantine.
     */
    private static void importReturnsToQuarantine(Scanner scanner, ReturnService returnService) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           IMPORT RETURNS TO QUARANTINE");
        System.out.println("=".repeat(50));

        returnService.quarantineReturns();

        System.out.println("All received returns have been moved to quarantine.");
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Handles processing of quarantined returns with latest-first ordering.
     */
    private static void processQuarantinedReturns(Scanner scanner, ReturnService returnService) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           PROCESS QUARANTINED RETURNS");
        System.out.println("=".repeat(50));

        Repositories repositories = Repositories.getInstance();
        List<ReturnOrder> quarantinedReturns = getQuarantinedReturns(repositories);

        if (quarantinedReturns.isEmpty()) {
            System.out.println("No returns in quarantine to process.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        int processed = 0;
        int discarded = 0;
        int restocked = 0;

        for (ReturnOrder returnOrder : quarantinedReturns) {
            if (processReturn(scanner, returnService, returnOrder, repositories)) {
                processed++;
                List<ReturnLine> lines = repositories.getReturnLineRepository()
                        .getReturnLinesByReturnId(returnOrder.getReturnId());

                for (ReturnLine line : lines) {
                    String action = determineAction(returnOrder.getReason(), line.getCondition());
                    if ("Restocked".equals(action)) {
                        restocked++;
                    } else if ("Discarded".equals(action)) {
                        discarded++;
                    }
                }
            } else {
                break;
            }
        }

        // Show summary
        showProcessingSummary(processed, restocked, discarded);
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Gets quarantined returns ordered by latest-first.
     */
    private static List<ReturnOrder> getQuarantinedReturns(Repositories repositories) {
        return repositories.getReturnOrderRepository().getAllReturnOrders().stream()
                .filter(r -> "quarantine".equalsIgnoreCase(r.getStatus()))
                .sorted(Comparator.comparing(ReturnOrder::getReceivedAt).reversed()
                        .thenComparingInt(ReturnOrder::getReturnId))
                .collect(Collectors.toList());
    }

    /**
     * Processes a single return order with user interaction.
     */
    private static boolean processReturn(Scanner scanner, ReturnService returnService,
                                         ReturnOrder returnOrder, Repositories repositories) {

        System.out.println("\n" + "-".repeat(40));
        System.out.printf("Return ID: R%d%n", returnOrder.getReturnId());
        System.out.printf("Reason: %s%n", returnOrder.getReason());
        System.out.printf("Received: %s%n", returnOrder.getReceivedAt());
        System.out.println("-".repeat(40));

        List<ReturnLine> returnLines = repositories.getReturnLineRepository()
                .getReturnLinesByReturnId(returnOrder.getReturnId());

        System.out.println("Items in this return:");
        for (ReturnLine line : returnLines) {
            String suggestedAction = determineAction(returnOrder.getReason(), line.getCondition());
            System.out.printf("  - SKU %s: %d units (condition: %s) → Suggested: %s%n",
                    line.getSku(), line.getQty(),
                    line.getCondition() != null ? line.getCondition() : "unknown",
                    suggestedAction);
        }

        System.out.println("\nChoose action:");
        System.out.println("1. Process automatically (follow suggestions)");
        System.out.println("2. Skip this return");
        System.out.println("3. Stop processing");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        if ("b".equalsIgnoreCase(choice) || "3".equals(choice)) {
            return false;
        }

        switch (choice) {
            case "1":
                returnService.processAllReturns();
                System.out.println("Return R" + returnOrder.getReturnId() + " processed automatically.");
                return true;
            case "2":
                System.out.println("Skipped return R" + returnOrder.getReturnId());
                return true;
            default:
                System.out.println("Invalid choice. Skipping return.");
                return true;
        }
    }

    /**
     * Determines suggested action based on return reason and condition.
     */
    private static String determineAction(String reason, String condition) {
        switch (reason.toLowerCase()) {
            case "customer-remorse":
                return (condition == null || "good".equalsIgnoreCase(condition)) ? "Restocked" : "Discarded";
            case "damaged":
            case "expired":
                return "Discarded";
            case "cycle-count":
                return "QA Inspection";
            default:
                return "Discarded";
        }
    }

    /**
     * Shows processing summary with statistics.
     */
    private static void showProcessingSummary(int processed, int restocked, int discarded) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           PROCESSING SUMMARY");
        System.out.println("=".repeat(50));
        System.out.printf("Returns processed: %d%n", processed);
        System.out.printf("Items restocked: %d%n", restocked);
        System.out.printf("Items discarded: %d%n", discarded);
        System.out.println("Audit log updated with all actions.");
        System.out.println("Check: src/main/java/app/service/AuditLogs/return_inspection_audit.log");
    }
}
