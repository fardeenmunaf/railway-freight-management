package app.view;

import app.controller.TrainDispatchController;
import app.model.Facility;
import app.model.RoutePoint;
import app.model.TrainDispatch;
import app.model.TrainService;
import app.repositories.Repositories;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive UI for the Train Dispatch Scheduler.
 * Allows freight managers to manually dispatch trains, define routes,
 * and manage train schedules with crossing operations.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDispatchScheduler {

    private static TrainDispatchController controller;
    private static Repositories repositories;
    private static DateTimeFormatter dateTimeFormatter;

    /**
     * Displays the Train Dispatch Scheduler menu and handles user interaction.
     * Follows the same pattern as other views in the system.
     *
     * @param scanner the scanner for user input
     */
    public static void display(Scanner scanner) {
        controller = new TrainDispatchController();
        repositories = Repositories.getInstance();
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    createNewDispatch(scanner);
                    break;
                case 2:
                    viewAllDispatches();
                    break;
                case 3:
                    viewDispatchDetails(scanner);
                    break;
                case 4:
                    updateDispatchStatus(scanner);
                    break;
                case 5:
                    viewRoutePointsWithCrossings();
                    break;
                case 6:
                    manageCrossingOperation(scanner);
                    break;
                case 7:
                    dispatchPlannedTrain(scanner);
                    break;
                case 8:
                    viewSpeedCalculation(scanner);
                    break;
                case 0:
                    running = false;
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays the main menu.
     */
    private static void displayMainMenu() {
        System.out.println("\n╔═════════════════════════════════════════════════════╗");
        System.out.println("║       TRAIN DISPATCH SCHEDULER - MAIN MENU          ║");
        System.out.println("╠═════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Create New Manual Dispatch                       ║");
        System.out.println("║ 2. View All Dispatches                              ║");
        System.out.println("║ 3. View Dispatch Details                            ║");
        System.out.println("║ 4. Update Dispatch Status                           ║");
        System.out.println("║ 5. View Route Points with Crossings                 ║");
        System.out.println("║ 6. Manage Crossing Operation                        ║");
        System.out.println("║ 7. Dispatch a Planned Train                         ║");
        System.out.println("║ 8. View Speed Calculation for Segment               ║");
        System.out.println("║ 0. Exit                                             ║");
        System.out.println("╚═════════════════════════════════════════════════════╝");
    }

    /**
     * Creates a new manual dispatch.
     */
    private static void createNewDispatch(Scanner scanner) {
        System.out.println("\n=== CREATE NEW MANUAL DISPATCH ===");

        // List available train services
        List<TrainService> trainServices = controller.getAvailableTrainServices();
        if (trainServices.isEmpty()) {
            System.out.println("No train services available. Please create a train service first.");
            return;
        }

        System.out.println("\nAvailable Train Services:");
        for (TrainService ts : trainServices) {
            System.out.printf("ID: %d | Train: %s | Status: %s%n",
                    ts.getServiceId(),
                    ts.getTrain() != null ? ts.getTrain().getCode() : "N/A",
                    ts.getStatus());
        }

        Integer trainServiceId = getIntInput(scanner, "\nEnter Train Service ID: ");

        // Get departure time
        System.out.println("\nEnter planned departure time (format: yyyy-MM-dd HH:mm):");
        String departureTimeStr = scanner.nextLine();
        LocalDateTime departureTime;
        try {
            departureTime = LocalDateTime.parse(departureTimeStr, dateTimeFormatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Using current time.");
            departureTime = LocalDateTime.now();
        }

        // Define route
        List<Integer> routeDefinition = defineRoute(scanner);
        if (routeDefinition.isEmpty()) {
            System.out.println("Route definition cancelled.");
            return;
        }

        try {
            TrainDispatch dispatch = controller.createManualDispatch(trainServiceId, departureTime, routeDefinition);
            System.out.println("\n✓ Dispatch created successfully!");
            displayDispatchSummary(dispatch);
        } catch (Exception e) {
            System.out.println("✗ Error creating dispatch: " + e.getMessage());
        }
    }

    /**
     * Allows user to define a route by selecting facilities.
     */
    private static List<Integer> defineRoute(Scanner scanner) {
        System.out.println("\n=== DEFINE ROUTE ===");
        System.out.println("Tip: Use search to find facilities by name instead of scrolling through all facilities.");
        List<Integer> route = new ArrayList<>();

        boolean addingStops = true;
        while (addingStops) {
            System.out.println("\n─────────────────────────────────────────────────────");
            System.out.println("Current route has " + route.size() + " stop(s).");

            if (!route.isEmpty()) {
                System.out.println("Route so far:");
                for (int i = 0; i < route.size(); i++) {
                    Facility f = repositories.getFacilityRepository().getFacilityById(route.get(i));
                    System.out.printf("  %d. %s (ID: %d)%n", i + 1,
                            f != null ? f.getName() : "Unknown", route.get(i));
                }
            }

            System.out.println("─────────────────────────────────────────────────────");
            System.out.println("Options:");
            System.out.println("  1. Search facility by name");
            System.out.println("  2. Enter facility ID directly");
            System.out.println("  0. Finish route definition");

            int option = getIntInput(scanner, "Choose option: ");

            if (option == 0) {
                if (route.size() < 2) {
                    System.out.println("✗ Route must have at least 2 stops (origin and destination).");
                } else {
                    addingStops = false;
                }
            } else if (option == 1) {
                searchAndAddFacility(scanner, route);
            } else if (option == 2) {
                addFacilityById(scanner, route);
            } else {
                System.out.println("✗ Invalid option.");
            }
        }

        return route;
    }

    /**
     * Searches for facilities by name and allows user to select one.
     */
    private static void searchAndAddFacility(Scanner scanner, List<Integer> route) {
        System.out.print("\nEnter facility name to search (partial match): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            System.out.println("✗ Search term cannot be empty.");
            return;
        }

        List<Facility> facilities = repositories.getFacilityRepository().getAllFacilities();
        List<Facility> matches = new ArrayList<>();

        // Search for facilities matching the term (case-insensitive)
        for (Facility f : facilities) {
            if (f.getName() != null && f.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                matches.add(f);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("✗ No facilities found matching '" + searchTerm + "'.");
            return;
        }

        // Limit display to first 20 results
        int displayLimit = Math.min(20, matches.size());
        System.out.println("\nFound " + matches.size() + " matching facilities" +
                (matches.size() > 20 ? " (showing first 20):" : ":"));
        System.out.println("┌─────┬────────────────────────────────────────────┬──────────────────────┐");
        System.out.println("│ No. │ Facility Name                              │ Type                 │");
        System.out.println("├─────┼────────────────────────────────────────────┼──────────────────────┤");

        for (int i = 0; i < displayLimit; i++) {
            Facility f = matches.get(i);
            System.out.printf("│ %-3d │ %-42s │ %-20s │%n",
                    (i + 1),
                    truncate(f.getName(), 42),
                    f.getFacilityType() != null ? truncate(f.getFacilityType().getName(), 20) : "N/A");
        }
        System.out.println("└─────┴────────────────────────────────────────────┴──────────────────────┘");

        if (matches.size() > 20) {
            System.out.println("⚠ Too many results. Refine your search for more options.");
        }

        int selection = getIntInput(scanner, "\nSelect facility number (0 to cancel): ");

        if (selection > 0 && selection <= displayLimit) {
            Facility selected = matches.get(selection - 1);
            route.add(selected.getFacilityId());
            System.out.println("✓ Added: " + selected.getName() + " (ID: " + selected.getFacilityId() + ")");
        } else if (selection != 0) {
            System.out.println("✗ Invalid selection.");
        }
    }

    /**
     * Adds a facility to the route by entering its ID directly.
     */
    private static void addFacilityById(Scanner scanner, List<Integer> route) {
        Integer facilityId = getIntInput(scanner, "\nEnter Facility ID: ");

        Facility facility = repositories.getFacilityRepository().getFacilityById(facilityId);
        if (facility != null) {
            route.add(facilityId);
            System.out.println("✓ Added: " + facility.getName() + " (ID: " + facilityId + ")");
        } else {
            System.out.println("✗ Facility not found with ID: " + facilityId);
        }
    }

    /**
     * Views all dispatches.
     */
    private static void viewAllDispatches() {
        System.out.println("\n=== ALL TRAIN DISPATCHES ===");
        List<TrainDispatch> dispatches = controller.getAllDispatches();

        if (dispatches.isEmpty()) {
            System.out.println("No dispatches found.");
            return;
        }

        System.out.println("\n┌─────┬──────────────┬──────────────────────┬──────────┬──────────┬──────────┐");
        System.out.println("│ ID  │ Train Svc ID │ Planned Departure    │ Status   │ Distance │ Est.Time │");
        System.out.println("├─────┼──────────────┼──────────────────────┼──────────┼──────────┼──────────┤");

        for (TrainDispatch d : dispatches) {
            System.out.printf("│ %-3d │ %-12d │ %-20s │ %-8s │ %6.1f km │ %6.0f m │%n",
                    d.getDispatchId(),
                    d.getTrainService() != null ? d.getTrainService().getServiceId() : 0,
                    d.getPlannedDepartureTime() != null ? d.getPlannedDepartureTime().format(dateTimeFormatter) : "N/A",
                    d.getStatus(),
                    d.getTotalDistanceKm(),
                    d.getEstimatedTravelTimeMinutes());
        }
        System.out.println("└─────┴──────────────┴──────────────────────┴──────────┴──────────┴──────────┘");
    }

    /**
     * Views details of a specific dispatch.
     */
    private static void viewDispatchDetails(Scanner scanner) {
        Integer dispatchId = getIntInput(scanner, "\nEnter Dispatch ID: ");
        TrainDispatch dispatch = controller.getDispatchById(dispatchId);

        if (dispatch == null) {
            System.out.println("Dispatch not found.");
            return;
        }

        displayDispatchSummary(dispatch);
        displayRouteDetails(dispatch);
    }

    /**
     * Displays a summary of a dispatch.
     */
    private static void displayDispatchSummary(TrainDispatch dispatch) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                     DISPATCH SUMMARY                        ║");
        System.out.println("╠═════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Dispatch ID:           %-37d║%n", dispatch.getDispatchId());
        System.out.printf("║ Train Service ID:      %-37d║%n",
                dispatch.getTrainService() != null ? dispatch.getTrainService().getServiceId() : 0);
        System.out.printf("║ Route Type:            %-37s║%n", dispatch.getRouteType());
        System.out.printf("║ Status:                %-37s║%n", dispatch.getStatus());
        System.out.printf("║ Planned Departure:     %-37s║%n",
                dispatch.getPlannedDepartureTime() != null ? dispatch.getPlannedDepartureTime().format(dateTimeFormatter) : "N/A");
        System.out.printf("║ Estimated Arrival:     %-37s║%n",
                dispatch.getEstimatedArrivalTime() != null ? dispatch.getEstimatedArrivalTime().format(dateTimeFormatter) : "N/A");
        System.out.printf("║ Total Distance:        %-33.2f km ║%n", dispatch.getTotalDistanceKm());
        System.out.printf("║ Est. Travel Time:      %-33.0f min║%n", dispatch.getEstimatedTravelTimeMinutes());
        System.out.printf("║ Route Points:          %-37d║%n", dispatch.getRoute().size());
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
    }

    /**
     * Displays detailed route information.
     */
    private static void displayRouteDetails(TrainDispatch dispatch) {
        List<RoutePoint> routePoints = controller.getRouteForDispatch(dispatch.getDispatchId());

        if (routePoints.isEmpty()) {
            System.out.println("\nNo route points defined.");
            return;
        }

        System.out.println("\n=== ROUTE DETAILS ===");
        System.out.println("┌─────┬──────────────────────────┬──────────────────────┬──────────────────────┬──────────┬─────────────────┐");
        System.out.println("│ Seq │ Facility                 │ Arrival              │ Departure            │ Distance │ Crossing Op     │");
        System.out.println("├─────┼──────────────────────────┼──────────────────────┼──────────────────────┼──────────┼─────────────────┤");

        for (RoutePoint rp : routePoints) {
            System.out.printf("│ %-3d │ %-24s │ %-20s │ %-20s │ %6.1f km │ %-15s │%n",
                    rp.getSequenceOrder(),
                    rp.getFacility() != null ? truncate(rp.getFacility().getName(), 24) : "N/A",
                    rp.getEstimatedArrivalTime() != null ? rp.getEstimatedArrivalTime().format(dateTimeFormatter) : "N/A",
                    rp.getEstimatedDepartureTime() != null ? rp.getEstimatedDepartureTime().format(dateTimeFormatter) : "N/A",
                    rp.getDistanceFromStartKm(),
                    rp.getCrossingOperation());
        }
        System.out.println("└─────┴──────────────────────────┴──────────────────────┴──────────────────────┴──────────┴─────────────────┘");
    }

    /**
     * Updates dispatch status.
     */
    private static void updateDispatchStatus(Scanner scanner) {
        Integer dispatchId = getIntInput(scanner, "\nEnter Dispatch ID: ");

        System.out.println("\nAvailable statuses:");
        System.out.println("1. PLANNED");
        System.out.println("2. DISPATCHED");
        System.out.println("3. IN_TRANSIT");
        System.out.println("4. COMPLETED");
        System.out.println("5. CANCELLED");

        int choice = getIntInput(scanner, "Select new status: ");
        String status;

        switch (choice) {
            case 1:
                status = "PLANNED";
                break;
            case 2:
                status = "DISPATCHED";
                break;
            case 3:
                status = "IN_TRANSIT";
                break;
            case 4:
                status = "COMPLETED";
                break;
            case 5:
                status = "CANCELLED";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        TrainDispatch updated = controller.updateDispatchStatus(dispatchId, status);
        if (updated != null) {
            System.out.println("✓ Status updated to: " + status);
        } else {
            System.out.println("✗ Dispatch not found.");
        }
    }

    /**
     * Views all route points with crossing operations.
     */
    private static void viewRoutePointsWithCrossings() {
        System.out.println("\n=== ROUTE POINTS WITH CROSSING OPERATIONS ===");
        List<RoutePoint> crossings = controller.getRoutePointsWithCrossings();

        if (crossings.isEmpty()) {
            System.out.println("No crossing operations found.");
            return;
        }

        System.out.println("\n┌─────┬─────────────┬──────────────────────────┬─────────────────┬───────────┐");
        System.out.println("│ ID  │ Dispatch ID │ Facility                 │ Crossing Op     │ Wait Time │");
        System.out.println("├─────┼─────────────┼──────────────────────────┼─────────────────┼───────────┤");

        for (RoutePoint rp : crossings) {
            System.out.printf("│ %-3d │ %-11d │ %-24s │ %-15s │ %7d m │%n",
                    rp.getRoutePointId(),
                    rp.getTrainDispatch() != null ? rp.getTrainDispatch().getDispatchId() : 0,
                    rp.getFacility() != null ? truncate(rp.getFacility().getName(), 24) : "N/A",
                    rp.getCrossingOperation(),
                    rp.getExpectedWaitTimeMinutes());
        }
        System.out.println("└─────┴─────────────┴──────────────────────────┴─────────────────┴───────────┘");
    }

    /**
     * Manages crossing operation for a route point.
     */
    private static void manageCrossingOperation(Scanner scanner) {
        Integer routePointId = getIntInput(scanner, "\nEnter Route Point ID: ");

        System.out.println("\nCrossing operations:");
        System.out.println("1. NONE");
        System.out.println("2. WAIT_FOR_CROSSING");
        System.out.println("3. SIDING_WAIT");
        System.out.println("4. PRIORITY_PASS");

        int choice = getIntInput(scanner, "Select crossing operation: ");
        String operation;

        switch (choice) {
            case 1:
                operation = "NONE";
                break;
            case 2:
                operation = "WAIT_FOR_CROSSING";
                break;
            case 3:
                operation = "SIDING_WAIT";
                break;
            case 4:
                operation = "PRIORITY_PASS";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        Integer waitTime = getIntInput(scanner, "Enter expected wait time (minutes): ");

        RoutePoint updated = controller.updateCrossingOperation(routePointId, operation, waitTime);
        if (updated != null) {
            System.out.println("✓ Crossing operation updated.");
        } else {
            System.out.println("✗ Route point not found.");
        }
    }

    /**
     * Dispatches a planned train.
     */
    private static void dispatchPlannedTrain(Scanner scanner) {
        Integer dispatchId = getIntInput(scanner, "\nEnter Dispatch ID to dispatch: ");

        try {
            TrainDispatch dispatched = controller.dispatchTrain(dispatchId);
            System.out.println("✓ Train dispatched successfully!");
            displayDispatchSummary(dispatched);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Views speed calculation for a segment.
     */
    private static void viewSpeedCalculation(Scanner scanner) {
        Integer trainServiceId = getIntInput(scanner, "\nEnter Train Service ID: ");
        Integer segmentId = getIntInput(scanner, "Enter Line Segment ID: ");

        try {
            double speed = controller.calculateSegmentSpeed(trainServiceId, segmentId);
            System.out.printf("\n✓ Calculated effective speed: %.2f km/h%n", speed);
        } catch (Exception e) {
            System.out.println("✗ Error calculating speed: " + e.getMessage());
        }
    }

    /**
     * Gets integer input from user.
     */
    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Truncates a string to a maximum length.
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
}
