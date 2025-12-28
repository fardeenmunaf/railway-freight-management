package app.view;

import app.controller.TrainDispatchController;
import app.model.*;
import app.repositories.Repositories;
import app.service.sprint3.TrainSchedulingService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive UI for the Train Dispatch Scheduler.
 * Allows freight managers to manually dispatch trains, define routes,
 * and manage train schedules with crossing operations. Uses TrainDispatchController
 * for CRUD and management and TrainSchedulingService for automatic scheduling.
 */
public class TrainSchedulingView {

    private static TrainDispatchController controller;
    private static Repositories repositories;
    private static DateTimeFormatter dateTimeFormatter;

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

    private static void displayMainMenu() {
        System.out.println("\n╔═════════════════════════════════════════════════════╗");
        System.out.println("║ TRAIN DISPATCH SCHEDULER - MAIN MENU                ║");
        System.out.println("╠═════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Create New Dispatch (Manual or Automatic)        ║");
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

    private static void createNewDispatch(Scanner scanner) {
        System.out.println("\n=== CREATE NEW DISPATCH ===");

        List<TrainService> services = controller.getAvailableTrainServices();
        if (services.isEmpty()) {
            System.out.println("No train services available. Please create a train service first.");
            return;
        }

        System.out.println("\nAvailable Train Services:");
        for (TrainService ts : services) {
            System.out.printf("ID: %d | Train: %s | Status: %s%n",
                    ts.getServiceId(), ts.getTrain() != null ? ts.getTrain().getCode() : "N/A", ts.getStatus());
        }

        Integer trainServiceId = getIntInput(scanner, "\nEnter Train Service ID: ");
        TrainService selected = repositories.getTrainServiceRepository().getTrainServiceById(trainServiceId);
        if (selected == null) {
            System.out.println("TrainService not found.");
            return;
        }

        System.out.println("\nEnter planned departure time (format: yyyy-MM-dd HH:mm):");
        String departureTimeStr = scanner.nextLine();
        LocalDateTime departureTime;
        try {
            departureTime = LocalDateTime.parse(departureTimeStr, dateTimeFormatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Using current time.");
            departureTime = LocalDateTime.now();
        }

        System.out.println("\nChoose route mode: 1) AUTOMATIC  2) MANUAL");
        int mode = getIntInput(scanner, "Enter choice: ");

        if (mode == 2) {
            // Manual creation via scheduler so we can attach real LineSegments
            List<Integer> routeDef = defineRoute(scanner);
            if (routeDef.isEmpty()) {
                System.out.println("Route definition cancelled.");
                return;
            }
            try {
                // Build manual RoutePoints
                List<RoutePoint> manual = new ArrayList<>();
                for (int i = 0; i < routeDef.size(); i++) {
                    Facility f = repositories.getFacilityRepository().getFacilityById(routeDef.get(i));
                    if (f == null) continue;
                    RoutePoint rp = new RoutePoint();
                    rp.setFacility(f);
                    rp.setSequenceOrder(i + 1);
                    manual.add(rp);
                }

                // Try to set segmentToNext from repository links for each consecutive pair
                setSegmentsForManualRoute(manual);

                // Try find matching freight route if exists
                FreightRoute matched = findMatchingFreightRoute(repositories, routeDef);

                // Schedule using TrainSchedulingService
                TrainSchedulingService scheduler = new TrainSchedulingService();
                int dispatchId = new java.util.Random().nextInt(10000) + 1;
                List<TrainDispatch> existing = repositories.getTrainDispatchRepository().getAllTrainDispatches();
                TrainDispatch scheduled = scheduler.scheduleDispatch(dispatchId, selected, matched, departureTime, "MANUAL", manual, existing);

                // Persist scheduled dispatch
                TrainDispatch saved = repositories.getTrainDispatchRepository().addTrainDispatch(scheduled);
                List<RoutePoint> persisted = new ArrayList<>();
                if (saved.getRoute() != null) {
                    for (RoutePoint rp : saved.getRoute()) {
                        rp.setTrainDispatch(saved);
                        RoutePoint pr = repositories.getRoutePointRepository().addRoutePoint(rp);
                        persisted.add(pr);
                    }
                }
                saved.setRoute(persisted);
                repositories.getTrainDispatchRepository().updateTrainDispatch(saved.getDispatchId(), saved);

                System.out.println("\n✓ Dispatch created successfully!");
                displayDispatchSummary(saved);
            } catch (Exception e) {
                System.out.println("✗ Error creating dispatch: " + e.getMessage());
            }
        } else if (mode == 1) {
            // Automatic using TrainSchedulingService then persist
            TrainSchedulingService scheduler = new TrainSchedulingService();
            // Select a FreightRoute to use for automatic scheduling
            List<FreightRoute> allRoutes = repositories.getFreightRouteRepository().getAllFreightRoutes();
            if (allRoutes.isEmpty()) {
                System.out.println("No freight routes available. Please create a freight route first.");
                return;
            }
            System.out.println("\nAvailable Freight Routes:");
            for (FreightRoute frx : allRoutes) {
                System.out.printf("ID: %d | %s -> %s | %s\n", frx.getRouteId(), frx.getStartFacility() != null ? frx.getStartFacility().getName() : "?", frx.getEndFacility() != null ? frx.getEndFacility().getName() : "?", frx.getRouteName());
            }
            Integer routeId = getIntInput(scanner, "Enter FreightRoute ID to use: ");
            FreightRoute fr = repositories.getFreightRouteRepository().getFreightRouteById(routeId);
            if (fr == null) {
                System.out.println("FreightRoute not found: " + routeId);
                return;
            }

            List<TrainDispatch> existing = repositories.getTrainDispatchRepository().getAllTrainDispatches();
            int dispatchId = new java.util.Random().nextInt(10000) + 1;
            TrainDispatch dispatch = scheduler.scheduleDispatch(dispatchId, selected, fr, departureTime, "AUTOMATIC", null, existing);

            // Persist dispatch and route points
            TrainDispatch saved = repositories.getTrainDispatchRepository().addTrainDispatch(dispatch);
            List<RoutePoint> rps = saved.getRoute();
            List<RoutePoint> persisted = new ArrayList<>();
            for (RoutePoint rp : rps) {
                rp.setTrainDispatch(saved);
                RoutePoint persistedRp = repositories.getRoutePointRepository().addRoutePoint(rp);
                persisted.add(persistedRp);
            }
            saved.setRoute(persisted);
            repositories.getTrainDispatchRepository().updateTrainDispatch(saved.getDispatchId(), saved);

            System.out.println("\n✓ Automatic dispatch created and saved (ID: " + saved.getDispatchId() + ")");
            displayDispatchSummary(saved);
        } else {
            System.out.println("Invalid mode.");
        }
    }

    private static List<Integer> defineRoute(Scanner scanner) {
        System.out.println("\n=== DEFINE ROUTE ===");
        List<Integer> route = new ArrayList<>();
        boolean addingStops = true;
        while (addingStops) {
            System.out.println("\nCurrent route has " + route.size() + " stop(s).");
            if (!route.isEmpty()) {
                System.out.println("Route so far:");
                for (int i = 0; i < route.size(); i++) {
                    Facility f = repositories.getFacilityRepository().getFacilityById(route.get(i));
                    System.out.printf(" %d. %s (ID: %d)%n", i + 1, f != null ? f.getName() : "Unknown", route.get(i));
                }
            }
            System.out.println("Options:");
            System.out.println(" 1. Search facility by name");
            System.out.println(" 2. Enter facility ID directly");
            System.out.println(" 0. Finish route definition");
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

    private static void searchAndAddFacility(Scanner scanner, List<Integer> route) {
        System.out.print("\nEnter facility name to search (partial match): ");
        String searchTerm = scanner.nextLine().trim();
        if (searchTerm.isEmpty()) {
            System.out.println("✗ Search term cannot be empty.");
            return;
        }
        List<Facility> facilities = repositories.getFacilityRepository().getAllFacilities();
        List<Facility> matches = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.getName() != null && f.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                matches.add(f);
            }
        }
        // Sort matches: exact name matches first, then those that contain the term (both case-insensitive)
        matches.sort((a, b) -> {
            String an = a.getName() != null ? a.getName().toLowerCase() : "";
            String bn = b.getName() != null ? b.getName().toLowerCase() : "";
            boolean aExact = an.equals(searchTerm.toLowerCase());
            boolean bExact = bn.equals(searchTerm.toLowerCase());
            if (aExact && !bExact) return -1;
            if (!aExact && bExact) return 1;
            // secondary: shorter name first
            return Integer.compare(an.length(), bn.length());
        });
        if (matches.isEmpty()) {
            System.out.println("✗ No facilities found matching '" + searchTerm + "'.");
            return;
        }
        int displayLimit = Math.min(20, matches.size());
        System.out.println("\nFound " + matches.size() + " matching facilities" + (matches.size() > 20 ? " (showing first 20):" : ":"));
        for (int i = 0; i < displayLimit; i++) {
            Facility f = matches.get(i);
            System.out.printf("%d) %s (ID: %d)\n", i + 1, f.getName(), f.getFacilityId());
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

    private static void viewAllDispatches() {
        System.out.println("\n=== ALL TRAIN DISPATCHES ===");
        List<TrainDispatch> dispatches = controller.getAllDispatches();
        if (dispatches.isEmpty()) {
            System.out.println("No dispatches found.");
            return;
        }
        System.out.println("ID | TrainSvc | PlannedDeparture | Status | Distance(km) | EstTime(min)");
        for (TrainDispatch d : dispatches) {
            System.out.printf("%d | %d | %s | %s | %.1f | %.0f\n",
                    d.getDispatchId(), d.getTrainService() != null ? d.getTrainService().getServiceId() : 0,
                    d.getPlannedDepartureTime() != null ? d.getPlannedDepartureTime().format(dateTimeFormatter) : "N/A",
                    d.getStatus(), d.getTotalDistanceKm(), d.getEstimatedTravelTimeMinutes());
        }
    }

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

    private static void displayDispatchSummary(TrainDispatch dispatch) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║ DISPATCH SUMMARY                                                ║");
        System.out.println("╠═════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Dispatch ID: %-37d║%n", dispatch.getDispatchId());
        System.out.printf("║ Train Service ID: %-37d║%n", dispatch.getTrainService() != null ? dispatch.getTrainService().getServiceId() : 0);
        System.out.printf("║ Route Type: %-37s║%n", dispatch.getRouteType());
        System.out.printf("║ Status: %-37s║%n", dispatch.getStatus());
        System.out.printf("║ Planned Departure: %-37s║%n", dispatch.getPlannedDepartureTime() != null ? dispatch.getPlannedDepartureTime().format(dateTimeFormatter) : "N/A");
        System.out.printf("║ Estimated Arrival: %-37s║%n", dispatch.getEstimatedArrivalTime() != null ? dispatch.getEstimatedArrivalTime().format(dateTimeFormatter) : "N/A");
        System.out.printf("║ Total Distance: %-33.2f km ║%n", dispatch.getTotalDistanceKm());
        System.out.printf("║ Est. Travel Time: %-33.0f min║%n", dispatch.getEstimatedTravelTimeMinutes());
        System.out.printf("║ Route Points: %-37d║%n", dispatch.getRoute() != null ? dispatch.getRoute().size() : 0);
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
    }

    private static void displayRouteDetails(TrainDispatch dispatch) {
        List<RoutePoint> routePoints = controller.getRouteForDispatch(dispatch.getDispatchId());
        if (routePoints.isEmpty()) {
            System.out.println("\nNo route points defined.");
            return;
        }
        System.out.println("\n=== ROUTE DETAILS ===");
        System.out.println("Seq | Facility | Arrival | Departure | Distance | Crossing Op");
        for (RoutePoint rp : routePoints) {
            System.out.printf("%d | %s | %s | %s | %.1f | %s\n",
                    rp.getSequenceOrder(), rp.getFacility() != null ? rp.getFacility().getName() : "N/A",
                    rp.getEstimatedArrivalTime() != null ? rp.getEstimatedArrivalTime().format(dateTimeFormatter) : "N/A",
                    rp.getEstimatedDepartureTime() != null ? rp.getEstimatedDepartureTime().format(dateTimeFormatter) : "N/A",
                    rp.getDistanceFromStartKm(), rp.getCrossingOperation());
        }
    }

    private static void updateDispatchStatus(Scanner scanner) {
        Integer dispatchId = getIntInput(scanner, "\nEnter Dispatch ID: ");
        System.out.println("\nAvailable statuses:");
        System.out.println("1. PLANNED\n2. DISPATCHED\n3. IN_TRANSIT\n4. COMPLETED\n5. CANCELLED");
        int choice = getIntInput(scanner, "Select new status: ");
        String status;
        switch (choice) {
            case 1: status = "PLANNED"; break;
            case 2: status = "DISPATCHED"; break;
            case 3: status = "IN_TRANSIT"; break;
            case 4: status = "COMPLETED"; break;
            case 5: status = "CANCELLED"; break;
            default: System.out.println("Invalid choice."); return;
        }
        TrainDispatch updated = controller.updateDispatchStatus(dispatchId, status);
        if (updated != null) System.out.println("✓ Status updated to: " + status);
        else System.out.println("✗ Dispatch not found.");
    }

    private static void viewRoutePointsWithCrossings() {
        System.out.println("\n=== ROUTE POINTS WITH CROSSING OPERATIONS ===");
        List<RoutePoint> crossings = controller.getRoutePointsWithCrossings();
        if (crossings.isEmpty()) { System.out.println("No crossing operations found."); return; }
        System.out.println("ID | DispatchID | Facility | CrossingOp | WaitMin");
        for (RoutePoint rp : crossings) {
            System.out.printf("%d | %d | %s | %s | %d\n",
                    rp.getRoutePointId(), rp.getTrainDispatch() != null ? rp.getTrainDispatch().getDispatchId() : 0,
                    rp.getFacility() != null ? rp.getFacility().getName() : "N/A",
                    rp.getCrossingOperation(), rp.getExpectedWaitTimeMinutes());
        }
    }

    private static void manageCrossingOperation(Scanner scanner) {
        Integer routePointId = getIntInput(scanner, "\nEnter Route Point ID: ");
        System.out.println("\nCrossing operations:\n1. NONE\n2. WAIT_FOR_CROSSING\n3. SIDING_WAIT\n4. PRIORITY_PASS");
        int choice = getIntInput(scanner, "Select crossing operation: ");
        String operation;
        switch (choice) {
            case 1: operation = "NONE"; break;
            case 2: operation = "WAIT_FOR_CROSSING"; break;
            case 3: operation = "SIDING_WAIT"; break;
            case 4: operation = "PRIORITY_PASS"; break;
            default: System.out.println("Invalid choice."); return;
        }
        Integer waitTime = getIntInput(scanner, "Enter expected wait time (minutes): ");
        RoutePoint updated = controller.updateCrossingOperation(routePointId, operation, waitTime);
        if (updated != null) {
            System.out.println("✓ Crossing operation updated.");
        } else {
            System.out.println("✗ Route point not found.");
            // Provide helpful context: show some existing route points to help the user
            List<RoutePoint> all = repositories.getRoutePointRepository().getAllRoutePoints();
            if (all == null || all.isEmpty()) {
                System.out.println("No route points exist in the system.");
            } else {
                System.out.println("\nHere are some existing RoutePoints (id | dispatchId | facility | seq | crossing):");
                int limit = Math.min(20, all.size());
                for (int i = 0; i < limit; i++) {
                    RoutePoint rp = all.get(i);
                    System.out.printf("%d | %d | %s | %d | %s\n",
                            rp.getRoutePointId(), rp.getTrainDispatch() != null ? rp.getTrainDispatch().getDispatchId() : 0,
                            rp.getFacility() != null ? rp.getFacility().getName() : "N/A",
                            rp.getSequenceOrder() != null ? rp.getSequenceOrder() : 0,
                            rp.getCrossingOperation() != null ? rp.getCrossingOperation() : "NONE");
                }
                if (all.size() > limit) System.out.println("... (list truncated)");
                System.out.println("Use one of the IDs above or run '5' to list RoutePoints with crossing operations.");

                // Prompt for retry
                System.out.println("\nEnter a different Route Point ID to try again (0 to cancel): ");
                int retryId = getIntInput(scanner, "RoutePoint ID: ");
                if (retryId > 0) {
                    RoutePoint retried = controller.updateCrossingOperation(retryId, operation, waitTime);
                    if (retried != null) {
                        System.out.println("✓ Crossing operation updated for ID: " + retryId);
                    } else {
                        System.out.println("✗ Still not found. Aborting.");
                    }
                } else {
                    System.out.println("Cancelled.");
                }
            }
        }
    }

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

    private static void setSegmentsForManualRoute(List<RoutePoint> manualRoute) {
        // For each consecutive pair in manualRoute, find and set the corresponding LineSegment
        for (int i = 0; i < manualRoute.size() - 1; i++) {
            RoutePoint from = manualRoute.get(i);
            RoutePoint to = manualRoute.get(i + 1);

            if (from == null || to == null || from.getFacility() == null || to.getFacility() == null) continue;

            // Use LineSegmentFacilityRepository to find common line segments linking the two facilities
            var lsfRepo = repositories.getLineSegmentFacilityRepository();
            List<LineSegmentFacility> linksFrom = lsfRepo.getLinksByFacility(from.getFacility().getFacilityId());
            List<LineSegmentFacility> linksTo = lsfRepo.getLinksByFacility(to.getFacility().getFacilityId());

            LineSegment matched = null;
            for (LineSegmentFacility lf1 : linksFrom) {
                for (LineSegmentFacility lf2 : linksTo) {
                    if (lf1.getLineSegment() != null && lf2.getLineSegment() != null &&
                        lf1.getLineSegment().getSegmentId().equals(lf2.getLineSegment().getSegmentId())) {
                        matched = lf1.getLineSegment();
                        break;
                    }
                }
                if (matched != null) break;
            }

            if (matched != null) {
                from.setSegmentToNext(matched);
            }
        }
    }

    private static FreightRoute findMatchingFreightRoute(Repositories repositories, List<Integer> routeDef) {
        // For the given route definition (list of facility IDs), try to find a matching FreightRoute
        List<FreightRoute> allRoutes = repositories.getFreightRouteRepository().getAllFreightRoutes();
        for (FreightRoute fr : allRoutes) {
            List<FreightRouteSegment> segs = new ArrayList<>(fr.getRouteSegments());
            segs.sort((a, b) -> Integer.compare(a.getSequenceOrder(), b.getSequenceOrder()));
            List<Integer> seq = new ArrayList<>();
            if (!segs.isEmpty()) {
                seq.add(segs.get(0).getFromFacility().getFacilityId());
                for (FreightRouteSegment s : segs) seq.add(s.getToFacility().getFacilityId());
            }
            if (seq.equals(routeDef)) return fr;
        }
        return null; // No matching route found
    }
}
