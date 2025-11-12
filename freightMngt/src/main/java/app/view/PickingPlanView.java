package app.view;

import app.controller.PickRouteController;
import app.model.Order;
import app.model.PickTask;
import app.repositories.Repositories;
import app.service.PickingPlan;
import app.service.Allocation;
import app.service.PickRouteOptimizationService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * View class responsible for handling the picking plan user interface.
 * This class provides a console-based interface for users to create picking plans
 * for warehouse operations, either for all orders or for a specific order.
 * <p>
 * Features include:
 * - Display of available orders with details
 * - Option to create picking plans for all orders or a specific order
 * - Display of generated pick tasks and pick waves
 * - Display of allocation strategies and eligibilities
 * - Comprehensive feedback on planning results
 */
public class PickingPlanView {

    /**
     * Displays the picking plan interface and handles user interaction.
     * This method provides a complete workflow for creating picking plans:
     * <p>
     * 1. Shows available orders with their details
     * 2. Allows user to select a specific order or process all
     * 3. Creates picking plans with tasks and waves
     * 4. Displays detailed results including strategies
     * 5. Shows allocation eligibilities and statuses
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         PICKING PLAN & ROUTE OPTIMIZER");
        System.out.println("=".repeat(60));
        System.out.println("This tool creates picking plans and optimizes routes");
        System.out.println("Type 'b' at any time to return to menu.\n");

        Repositories repositories = Repositories.getInstance();
        PickingPlan pickingPlan = new PickingPlan();
        PickRouteController routeController = new PickRouteController();

        // Display available orders
        displayOrders(repositories);

        try {
            System.out.println("\nOptions:");
            System.out.println("1. Create plan and optimize routes for a specific order");
            System.out.println("2. Create plan and optimize routes for ALL orders");
            System.out.print("\nSelect option (1-2): ");

            String optionInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(optionInput)) return;

            int option = Integer.parseInt(optionInput);

            PickingPlan.PickingPlanResult result = null;

            if (option == 1) {
                // Process specific order
                System.out.print("Enter order ID: ");
                String orderInput = scanner.nextLine().trim();
                if ("b".equalsIgnoreCase(orderInput)) return;
                int orderId = Integer.parseInt(orderInput);

                // Step 1: Create picking plan
                System.out.println("\n=== Step 1: Creating picking plan for Order " + orderId + " ===");
                result = pickingPlan.planPickingTaskForOrder(orderId);
                displayDetailedPickingPlanResults(result, "Order " + orderId);

                // Step 2: Optimize routes
                System.out.println("\n=== Step 2: Optimizing routes ===");
                Map<String, PickRouteOptimizationService.RouteResult> routes =
                        routeController.calculateRouteForOrder(orderId);
                displayRouteOptimizationResults(routes, "Order " + orderId, routeController);


            } else if (option == 2) {
                // Process all orders
                // Step 1: Create picking plan
                System.out.println("\n=== Step 1: Creating picking plans for all orders ===");
                result = pickingPlan.planPickingTask();
                displayDetailedPickingPlanResults(result, "All Orders");

                // Step 2: Optimize routes for all pick tasks
                System.out.println("\n=== Step 2: Optimizing routes for all pick tasks ===");
                Map<Integer, Map<String, PickRouteOptimizationService.RouteResult>> allRoutes =
                        routeController.calculateRoutesForPickTasks(result);
                displayAllTasksRouteResults(allRoutes, result, routeController);
            } else {
                System.out.println("Invalid option. Please select 1 or 2.");
                return;
            }

            // Display allocation information
            displayAllocationInfo(result);

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numeric values.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Displays all available orders with their details.
     */
    private static void displayOrders(Repositories repositories) {
        System.out.println("AVAILABLE ORDERS:");
        System.out.println("-".repeat(80));
        System.out.printf("%-8s %-15s %-10s %-20s%n",
                "Order ID", "Status", "Priority", "Due Date");
        System.out.println("-".repeat(80));

        List<Order> orders = repositories.getOrderRepository().getAllOrders();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (orders != null && !orders.isEmpty()) {
            orders.stream()
                    .sorted(Comparator.comparing(Order::getOrderId))
                    .forEach(order -> System.out.printf("%-8d %-15s %-10s %-20s%n",
                            order.getOrderId(),
                            order.getStatus() != null ? order.getStatus() : "Unknown",
                            order.getPriority() != null ? order.getPriority().toString() : "Unknown",
                            order.getDueDate() != null ? order.getDueDate().format(formatter) : "Unknown"));
        } else {
            System.out.println("   (no orders found)");
        }
    }

    /**
     * Displays the results of picking plan generation.
     */
    private static void displayPickingPlanResults(PickingPlan.PickingPlanResult result, String scope) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         PICKING PLAN RESULTS - " + scope);
        System.out.println("=".repeat(60));

        // Display Pick Tasks
        System.out.println("\nPICK TASKS GENERATED:");
        System.out.println("-".repeat(50));
        if (result.getPickTasks() != null && !result.getPickTasks().isEmpty()) {
            System.out.printf("%-10s %-8s %-10s %-15s%n", "Task ID", "Order ID", "SKU", "Waves Count");
            System.out.println("-".repeat(50));

            result.getPickTasks().forEach(task -> {
                System.out.printf("%-10d %-8d %-10d %-15d%n",
                        task.getPickTaskId(),
                        task.getOrderLine().getOrder().getOrderId(),
                        task.getOrderLine().getSku().getItemId(),
                        task.getPickWaves().size());
            });
        } else {
            System.out.println("   (no pick tasks generated)");
        }

        // Display Pick Waves
        System.out.println("\nPICK WAVES GENERATED:");
        System.out.println("-".repeat(65));
        if (result.getPickWaves() != null && !result.getPickWaves().isEmpty()) {
            System.out.printf("%-10s %-25s %-15s %-10s%n",
                    "Wave ID", "Cart", "Status", "Strategy");
            System.out.println("-".repeat(65));

            result.getPickWaves().forEach(wave -> System.out.printf("%-10d %-25s %-15s %-10s%n",
                    wave.getPickWaveId(),
                    wave.getCart() != null ? wave.getCart().getName() : "Unknown",
                    wave.getStatus() != null ? wave.getStatus() : "Unknown",
                    wave.getStrategy() != null ? wave.getStrategy() : "Unknown"));
        } else {
            System.out.println("   (no pick waves generated)");
        }

        // Summary
        System.out.println("\nSUMMARY:");
        System.out.println("-".repeat(30));
        System.out.println("Total Pick Tasks: " + (result.getPickTasks() != null ? result.getPickTasks().size() : 0));
        System.out.println("Total Pick Waves: " + (result.getPickWaves() != null ? result.getPickWaves().size() : 0));
    }

    /**
     * Displays detailed picking plan results with processing information.
     */
    private static void displayDetailedPickingPlanResults(PickingPlan.PickingPlanResult result, String scope) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("         DETAILED PICKING PLAN RESULTS - " + scope);
        System.out.println("=".repeat(80));

        // Display processing details for each order line
        if (result.getProcessingDetails() != null && !result.getProcessingDetails().isEmpty()) {
            System.out.println("\n>>> PROCESSING DETAILS <<<\n");
            
            for (PickingPlan.OrderLineProcessingInfo info : result.getProcessingDetails()) {
                System.out.println("─".repeat(80));
                System.out.println(">>> Processing OrderLine " + info.getOrderLineId() +
                    " (Order " + info.getOrderId() + ", SKU " + info.getSkuId() +
                    ", Qty: " + info.getQtyRequested() + ") <<<");
                
                if ("ERROR".equals(info.getStatus()) || "SKIPPED".equals(info.getStatus())) {
                    System.out.println("  ❌ " + info.getStatus() + ": " + info.getErrorMessage());
                    continue;
                }
                
                // Display location information
                if (info.getLocations() != null && !info.getLocations().isEmpty()) {
                    if (info.getLocations().size() > 1) {
                        System.out.println("🔄 MULTI-BAY ORDER - OrderLine " + info.getOrderLineId() +
                            " (Order " + info.getOrderId() + ") spans " + info.getLocations().size() + " bays:");
                        for (PickingPlan.LocationInfo loc : info.getLocations()) {
                            System.out.println("   → Bay " + loc.getBay() + ": " + loc.getQtyAvailable() + " units");
                        }
                    } else if (info.getLocations().size() == 1) {
                        PickingPlan.LocationInfo loc = info.getLocations().get(0);
                        System.out.println("📍 Single bay - Bay " + loc.getBay() +
                            ": " + loc.getQtyAvailable() + " units");
                    }
                }
                
                // Display cart selection
                if (info.getSelectedCart() != null) {
                    System.out.println("  Selected Cart " + info.getSelectedCart().getCartId() +
                        " (capacity: " + info.getCartCapacity() + " items per trip)" +
                        (info.getCartCapacity() >= 5 ? " ✓" : " - LOW CAPACITY WARNING"));
                }
                
                // Display location details with waves
                if (info.getLocations() != null) {
                    for (PickingPlan.LocationInfo loc : info.getLocations()) {
                        System.out.println("  Location: Aisle " + loc.getAisle() +
                            ", Bay " + loc.getBay() +
                            " - Available: " + loc.getQtyAvailable() +
                            " - To collect: " + loc.getQtyToCollect() +
                            " - Trips needed: " + loc.getTripsNeeded());
                        
                        // Display waves for this location
                        if (loc.getWaves() != null) {
                            for (PickingPlan.WaveInfo wave : loc.getWaves()) {
                                System.out.println("    Wave " + wave.getWaveId() +
                                    ": Cart " + wave.getCartId() +
                                    ", Trip " + wave.getTripNumber() + "/" + wave.getTotalTrips() +
                                    ", Qty: " + wave.getQty() +
                                    " (Total collected: " + wave.getTotalCollected() + "/" + wave.getTotalNeeded() + ")");
                            }
                        }
                    }
                }
                
                // Display task creation
                if (info.getPickTaskId() != null) {
                    System.out.println("  ✓ PickTask " + info.getPickTaskId() +
                        " created with " + info.getWavesCount() + " wave(s) (trips)");
                }
            }
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("=== Picking Task Planning Complete ===");
        System.out.println("Total Pick Tasks created: " + 
            (result.getPickTasks() != null ? result.getPickTasks().size() : 0) + 
            " (one per eligible OrderLine)");
        System.out.println("Total Pick Waves created: " + 
            (result.getPickWaves() != null ? result.getPickWaves().size() : 0) + 
            " (one per trip)");
        System.out.println("=".repeat(80));

        // Display summary tables
        displayPickingPlanResults(result, scope);
    }

    /**
     * Displays allocation information including eligibilities and strategies.
     */
    private static void displayAllocationInfo(PickingPlan.PickingPlanResult result) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("             ALLOCATION STRATEGIES & ELIGIBILITIES");
        System.out.println("=".repeat(60));

        PickingPlan pickingPlan = new PickingPlan();
        List<Allocation.OrderEligibility> eligibilities = pickingPlan.getOrderEligibilities();

        System.out.println("\nORDER ELIGIBILITIES:");
        System.out.println("-".repeat(70));
        System.out.printf("%-8s %-8s %-12s %-12s %-15s%n",
                "Order ID", "SKU", "Requested", "Allocated", "Status");
        System.out.println("-".repeat(70));

        if (eligibilities != null && !eligibilities.isEmpty()) {
            eligibilities.forEach(eligibility -> {
                System.out.printf("%-8d %-8d %-12.1f %-12.1f %-15s%n",
                        eligibility.getOrderId(),
                        eligibility.getSku(),
                        eligibility.getRequestedQty(),
                        eligibility.getAllocatedQty(),
                        eligibility.getStatus());
            });

            // Display strategy summary
            System.out.println("\nALLOCATION STRATEGY SUMMARY:");
            System.out.println("-".repeat(40));

            AllocationSummary summary = calculateAllocationSummary(eligibilities);
            System.out.println("Fully Eligible Orders: " + summary.eligibleCount);
            System.out.println("Partially Eligible Orders: " + summary.partialCount);
            System.out.println("Ineligible Orders: " + summary.ineligibleCount);
            System.out.println("Total Orders Processed: " + eligibilities.size());
        } else {
            System.out.println("   (no eligibility information available)");
        }
    }

    /**
     * Calculates allocation summary statistics.
     */
    private static AllocationSummary calculateAllocationSummary(List<Allocation.OrderEligibility> eligibilities) {
        long eligibleCount = eligibilities.stream()
                .filter(e -> "ELIGIBLE".equals(e.getStatus()))
                .count();
        long partialCount = eligibilities.stream()
                .filter(e -> "PARTIAL".equals(e.getStatus()))
                .count();
        long ineligibleCount = eligibilities.stream()
                .filter(e -> "INELIGIBLE".equals(e.getStatus()))
                .count();

        return new AllocationSummary(eligibleCount, partialCount, ineligibleCount);
    }

    /**
     * Inner class to hold allocation summary data.
     */
    private static class AllocationSummary {
        final long eligibleCount;
        final long partialCount;
        final long ineligibleCount;

        AllocationSummary(long eligibleCount, long partialCount, long ineligibleCount) {
            this.eligibleCount = eligibleCount;
            this.partialCount = partialCount;
            this.ineligibleCount = ineligibleCount;
        }
    }

    /**
     * Displays route optimization results for a single set of routes.
     */
    private static void displayRouteOptimizationResults(
            Map<String, PickRouteOptimizationService.RouteResult> routes,
            String scope,
            PickRouteController routeController) {

        System.out.println("\n"+ "=".repeat(50));
        System.out.println("ROUTE OPTIMIZATION RESULTS - " + scope + ":");
        System.out.println("=".repeat(50));

        if (routes.isEmpty()) {
            System.out.println("No routes calculated.");
            return;
        }

        PickRouteOptimizationService.RouteResult strategyA = routes.get("strategyA");
        PickRouteOptimizationService.RouteResult strategyB = routes.get("strategyB");

        // Display both strategies
        if (strategyA != null) {
            System.out.println("\nSTRATEGY A (Deterministic Sweep):");
            System.out.println("-".repeat(40));
            System.out.println("Total Distance: " + strategyA.getTotalDistance() + " steps");
            System.out.println("Route: " + strategyA.getPath());
        }

        if (strategyB != null) {
            System.out.println("\nSTRATEGY B (Nearest Neighbour):");
            System.out.println("-".repeat(40));
            System.out.println("Total Distance: " + strategyB.getTotalDistance() + " steps");
            System.out.println("Route: " + strategyB.getPath());
        }

        // Compare strategies using controller logic
        PickRouteController.RouteComparison comparison = routeController.compareStrategies(strategyA, strategyB);
        if (comparison != null) {
            System.out.println("\nSTRATEGY COMPARISON:");
            System.out.println("-".repeat(40));

            if (comparison.isEqual()) {
                System.out.println("✓ Both strategies are EQUAL in efficiency");
            } else {
                System.out.println("✓ " + comparison.getBetterStrategy() +
                        " is BETTER (saves " + comparison.getSavedSteps() + " steps)");
            }
        }
    }

    /**
     * Displays route optimization results for all pick tasks.
     */
    private static void displayAllTasksRouteResults(
            Map<Integer, Map<String, PickRouteOptimizationService.RouteResult>> allRoutes,
            PickingPlan.PickingPlanResult planResult,
            PickRouteController routeController) {

        System.out.println("\n"+ "=".repeat(50));
        System.out.println("ROUTE OPTIMIZATION RESULTS - ALL TASKS:");
        System.out.println("=".repeat(50));

        if (allRoutes.isEmpty()) {
            System.out.println("No routes calculated.");
            return;
        }

        // Display individual task results
        for (PickTask task : planResult.getPickTasks()) {
            Integer taskId = task.getPickTaskId();
            Map<String, PickRouteOptimizationService.RouteResult> taskRoutes = allRoutes.get(taskId);

            if (taskRoutes != null && !taskRoutes.isEmpty()) {
                System.out.println("\nPickTask " + taskId + " (Order " +
                        task.getOrderLine().getOrder().getOrderId() + ", SKU " +
                        task.getOrderLine().getSku().getItemId() + "):");

                PickRouteOptimizationService.RouteResult strategyA = taskRoutes.get("strategyA");
                PickRouteOptimizationService.RouteResult strategyB = taskRoutes.get("strategyB");

                if (strategyA != null) {
                    System.out.println("  Strategy A: " + strategyA.getTotalDistance() + " steps - " + strategyA.getPath());
                }
                if (strategyB != null) {
                    System.out.println("  Strategy B: " + strategyB.getTotalDistance() + " steps - " + strategyB.getPath());
                }
            }
        }

        // Get totals from controller
        PickRouteController.TotalDistanceSummary summary = routeController.calculateTotalDistances(allRoutes);

        System.out.println("\nTOTAL DISTANCE SUMMARY:");
        System.out.println("=".repeat(40));
        System.out.println("Total Distance (Strategy A): " + summary.getTotalDistanceA() + " steps");
        System.out.println("Total Distance (Strategy B): " + summary.getTotalDistanceB() + " steps");

        PickRouteController.RouteComparison comparison = summary.getComparison();
        if (comparison != null) {
            if (comparison.isEqual()) {
                System.out.println("✓ Both strategies are EQUAL in total efficiency");
            } else {
                System.out.println("✓ " + comparison.getBetterStrategy() +
                        " is OVERALL BETTER (saves " + comparison.getSavedSteps() + " steps)");
            }
        }
    }
}
