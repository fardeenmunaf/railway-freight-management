package app.view;

import app.controller.FreightController;
import app.controller.FreightRouteController;
import app.controller.FreightRouteController.SegmentDefinition;
import app.model.*;
import app.model.Freight.FreightStatus;
import app.repositories.Repositories;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * View class for managing freight route planning.
 * Allows creating freights, defining routes, and assigning routes to pending freights.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightsRouteView {

    private static final FreightController freightController = new FreightController();
    private static final FreightRouteController routeController = new FreightRouteController();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Main display method for the Freight Route Planner interface.
     *
     * @param scanner the Scanner object for user input
     */
    public static void display(Scanner scanner) {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              FREIGHT ROUTE PLANNER");
            System.out.println("=".repeat(60));
            System.out.println("| 1. View Pending Freights                               |");
            System.out.println("| 2. Create New Freight                                  |");
            System.out.println("| 3. Assign Route to Freight                             |");
            System.out.println("| 4. Create Simple Route                                 |");
            System.out.println("| 5. Create Complex Route                                |");
            System.out.println("| 6. View All Routes                                     |");
            System.out.println("| 7. View Route Summary                                  |");
            System.out.println("| 0. Return to Main Menu                                 |");
            System.out.println("=".repeat(60));
            System.out.print("Enter your choice (0-7): ");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            try {
                int choice = Integer.parseInt(input);
                if (choice == 0) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                switch (choice) {
                    case 1:
                        viewPendingFreights(scanner);
                        break;
                    case 2:
                        createFreight(scanner);
                        break;
                    case 3:
                        assignRouteToFreight(scanner);
                        break;
                    case 4:
                        createSimpleRoute(scanner);
                        break;
                    case 5:
                        createComplexRoute(scanner);
                        break;
                    case 6:
                        viewAllRoutes(scanner);
                        break;
                    case 7:
                        viewRouteSummary(scanner);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 0 and 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Displays all pending freights.
     */
    private static void viewPendingFreights(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              PENDING FREIGHTS");
        System.out.println("=".repeat(60));

        List<Freight> pendingFreights = freightController.getPendingFreights();

        if (pendingFreights.isEmpty()) {
            System.out.println("No pending freights found.");
            System.out.println("\nWould you like to create a new freight? (y/n): ");
            String response = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(response)) {
                createFreight(scanner);
            }
            return;
        }

        System.out.printf("%-6s %-25s %-20s %-20s %-15s %-10s%n",
                "ID", "Description", "Origin", "Destination", "Deadline", "Route");
        System.out.println("-".repeat(100));

        for (Freight freight : pendingFreights) {
            String origin = freight.getOriginFacility() != null ? freight.getOriginFacility().getName() : "N/A";
            String destination = freight.getDestinationFacility() != null ? freight.getDestinationFacility().getName() : "N/A";
            String deadline = freight.getDeliveryDeadline() != null ? freight.getDeliveryDeadline().format(dateFormatter) : "N/A";
            String route = freight.getAssignedRoute() != null ? "Assigned" : "None";

            System.out.printf("%-6d %-25s %-20s %-20s %-15s %-10s%n",
                    freight.getFreightId(),
                    truncate(freight.getDescription(), 25),
                    truncate(origin, 20),
                    truncate(destination, 20),
                    deadline,
                    route);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Creates a new freight with PENDING status.
     */
    private static void createFreight(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              CREATE NEW FREIGHT");
        System.out.println("=".repeat(60));

        try {
            // Get freight ID
            Integer freightId = freightController.getNextFreightId();
            System.out.println("New Freight ID: " + freightId);

            // Get description
            System.out.print("\nEnter freight description: ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                System.out.println("Description cannot be empty.");
                return;
            }

            // Get weight
            System.out.print("Enter weight (kg): ");
            Double weightKg = Double.parseDouble(scanner.nextLine().trim());

            // Get volume
            System.out.print("Enter volume (m³): ");
            Double volumeM3 = Double.parseDouble(scanner.nextLine().trim());

            // Select origin facility
            System.out.println("\n--- Select Origin Facility ---");
            Facility originFacility = selectFacility(scanner);
            if (originFacility == null) {
                System.out.println("Origin facility selection cancelled.");
                return;
            }

            // Select destination facility
            System.out.println("\n--- Select Destination Facility ---");
            Facility destinationFacility = selectFacility(scanner);
            if (destinationFacility == null) {
                System.out.println("Destination facility selection cancelled.");
                return;
            }

            // Get delivery deadline
            System.out.print("\nEnter delivery deadline (days from now): ");
            int daysFromNow = Integer.parseInt(scanner.nextLine().trim());
            LocalDateTime deadline = LocalDateTime.now().plusDays(daysFromNow);

            // Get special instructions
            System.out.print("Enter special handling instructions (or press Enter to skip): ");
            String instructions = scanner.nextLine().trim();
            if (instructions.isEmpty()) {
                instructions = null;
            }

            // Create freight with PENDING status
            Freight freight = new Freight(
                    freightId,
                    description,
                    null, // Item can be null.
                    weightKg,
                    volumeM3,
                    originFacility,
                    destinationFacility,
                    FreightStatus.PENDING,
                    deadline,
                    instructions
            );

            freightController.createFreight(freight);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("SUCCESS: Freight created with ID " + freightId);
            System.out.println("Status: PENDING");
            System.out.println("=".repeat(60));

            // Ask if user wants to assign a route immediately
            System.out.print("\nWould you like to assign a route to this freight now? (y/n): ");
            String response = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(response)) {
                assignRouteToSpecificFreight(scanner, freightId);
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format.");
        } catch (Exception e) {
            System.out.println("Error creating freight: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Assigns a route to a freight.
     */
    private static void assignRouteToFreight(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              ASSIGN ROUTE TO FREIGHT");
        System.out.println("=".repeat(60));

        // Check for pending freights
        List<Freight> pendingFreights = freightController.getPendingFreights();
        if (pendingFreights.isEmpty()) {
            System.out.println("No pending freights available.");
            System.out.println("\nWould you like to create a new freight? (y/n): ");
            String response = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(response)) {
                createFreight(scanner);
            }
            return;
        }

        // Display pending freights
        System.out.println("\nPending Freights:");
        System.out.printf("%-6s %-30s %-20s %-20s%n", "ID", "Description", "Origin", "Destination");
        System.out.println("-".repeat(80));
        for (Freight freight : pendingFreights) {
            System.out.printf("%-6d %-30s %-20s %-20s%n",
                    freight.getFreightId(),
                    truncate(freight.getDescription(), 30),
                    freight.getOriginFacility() != null ? truncate(freight.getOriginFacility().getName(), 20) : "N/A",
                    freight.getDestinationFacility() != null ? truncate(freight.getDestinationFacility().getName(), 20) : "N/A");
        }

        System.out.print("\nEnter Freight ID: ");
        try {
            Integer freightId = Integer.parseInt(scanner.nextLine().trim());
            assignRouteToSpecificFreight(scanner, freightId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid freight ID.");
        }
    }

    /**
     * Assigns a route to a specific freight.
     */
    private static void assignRouteToSpecificFreight(Scanner scanner, Integer freightId) {
        try {
            Optional<Freight> freightOpt = freightController.getFreightById(freightId);
            if (!freightOpt.isPresent()) {
                System.out.println("Freight not found.");
                return;
            }

            Freight freight = freightOpt.get();
            if (!freight.isPending()) {
                System.out.println("Error: Only PENDING freights can be assigned a route.");
                System.out.println("Current status: " + freight.getStatus());
                return;
            }

            // Display available routes
            List<FreightRoute> routes = routeController.getAllFreightRoutes();
            if (routes.isEmpty()) {
                System.out.println("\nNo routes available.");
                System.out.print("Would you like to create a route? (y/n): ");
                String response = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(response)) {
                    System.out.print("Create simple (s) or complex (c) route? ");
                    String type = scanner.nextLine().trim();
                    if ("s".equalsIgnoreCase(type)) {
                        createSimpleRoute(scanner);
                    } else if ("c".equalsIgnoreCase(type)) {
                        createComplexRoute(scanner);
                    }
                }
                return;
            }

            System.out.println("\nAvailable Routes:");
            System.out.printf("%-6s %-25s %-10s %-20s %-20s%n", "ID", "Name", "Type", "Start", "End");
            System.out.println("-".repeat(85));
            for (FreightRoute route : routes) {
                System.out.printf("%-6d %-25s %-10s %-20s %-20s%n",
                        route.getRouteId(),
                        truncate(route.getRouteName(), 25),
                        route.getRouteType(),
                        route.getStartFacility() != null ? truncate(route.getStartFacility().getName(), 20) : "N/A",
                        route.getEndFacility() != null ? truncate(route.getEndFacility().getName(), 20) : "N/A");
            }

            System.out.print("\nEnter Route ID to assign: ");
            Integer routeId = Integer.parseInt(scanner.nextLine().trim());

            // Assign route
            boolean success = freightController.assignRouteToFreight(freightId, routeId);

            if (success) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("SUCCESS: Route assigned to Freight " + freightId);
                System.out.println("=".repeat(60));

                // Display route summary
                String summary = routeController.getRouteSummary(routeId);
                if (summary != null) {
                    System.out.println("\n" + summary);
                }
            } else {
                System.out.println("Failed to assign route.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Creates a simple route (direct path).
     */
    private static void createSimpleRoute(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              CREATE SIMPLE ROUTE");
        System.out.println("=".repeat(60));

        try {
            Integer routeId = routeController.getNextFreightRouteId();
            System.out.println("New Route ID: " + routeId);

            System.out.print("\nEnter route name: ");
            String routeName = scanner.nextLine().trim();

            System.out.println("\n--- Select Start Facility ---");
            Facility startFacility = selectFacility(scanner);
            if (startFacility == null) return;

            System.out.println("\n--- Select End Facility ---");
            Facility endFacility = selectFacility(scanner);
            if (endFacility == null) return;

            System.out.print("\nEnter notes (or press Enter to skip): ");
            String notes = scanner.nextLine().trim();
            if (notes.isEmpty()) notes = null;

            FreightRoute route = routeController.createSimpleRoute(
                    routeId,
                    routeName,
                    startFacility.getFacilityId(),
                    endFacility.getFacilityId(),
                    notes
            );

            System.out.println("\n" + "=".repeat(60));
            System.out.println("SUCCESS: Simple route created");
            System.out.println("Route ID: " + routeId);
            System.out.println("Route Name: " + routeName);
            System.out.println("Type: SIMPLE");
            System.out.println("Total Distance: " + String.format("%.2f", route.getTotalDistanceKm()) + " km");
            System.out.println("Estimated Duration: " + String.format("%.2f", route.getEstimatedDurationHours()) + " hours");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.out.println("Error creating route: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Creates a complex route with multiple segments.
     */
    private static void createComplexRoute(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              CREATE COMPLEX ROUTE");
        System.out.println("=".repeat(60));

        try {
            Integer routeId = routeController.getNextFreightRouteId();
            System.out.println("New Route ID: " + routeId);

            System.out.print("\nEnter route name: ");
            String routeName = scanner.nextLine().trim();

            System.out.println("\n--- Select Start Facility ---");
            Facility startFacility = selectFacility(scanner);
            if (startFacility == null) return;

            System.out.println("\n--- Select End Facility ---");
            Facility endFacility = selectFacility(scanner);
            if (endFacility == null) return;

            // Create segments
            List<SegmentDefinition> segments = new ArrayList<>();
            int segmentCount = 1;

            while (true) {
                System.out.println("\n--- Segment " + segmentCount + " ---");

                System.out.println("Select FROM facility:");
                Facility fromFacility = selectFacility(scanner);
                if (fromFacility == null) break;

                System.out.println("Select TO facility:");
                Facility toFacility = selectFacility(scanner);
                if (toFacility == null) break;

                System.out.println("Select Line Segment:");
                LineSegment lineSegment = selectLineSegment(scanner);
                if (lineSegment == null) break;

                System.out.print("Enter segment notes (or press Enter to skip): ");
                String segmentNotes = scanner.nextLine().trim();
                if (segmentNotes.isEmpty()) segmentNotes = null;

                segments.add(new SegmentDefinition(
                        segmentCount,
                        fromFacility.getFacilityId(),
                        toFacility.getFacilityId(),
                        lineSegment.getSegmentId(),
                        segmentNotes
                ));

                System.out.print("\nAdd another segment? (y/n): ");
                String response = scanner.nextLine().trim();
                if (!"y".equalsIgnoreCase(response)) break;

                segmentCount++;
            }

            if (segments.isEmpty()) {
                System.out.println("Cannot create complex route without segments.");
                return;
            }

            System.out.print("\nEnter route notes (or press Enter to skip): ");
            String notes = scanner.nextLine().trim();
            if (notes.isEmpty()) notes = null;

            FreightRoute route = routeController.createComplexRoute(
                    routeId,
                    routeName,
                    startFacility.getFacilityId(),
                    endFacility.getFacilityId(),
                    segments,
                    notes
            );

            System.out.println("\n" + "=".repeat(60));
            System.out.println("SUCCESS: Complex route created");
            System.out.println("Route ID: " + routeId);
            System.out.println("Route Name: " + routeName);
            System.out.println("Type: COMPLEX");
            System.out.println("Segments: " + segments.size());
            System.out.println("Total Distance: " + String.format("%.2f", route.getTotalDistanceKm()) + " km");
            System.out.println("Estimated Duration: " + String.format("%.2f", route.getEstimatedDurationHours()) + " hours");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.out.println("Error creating route: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Displays all available routes.
     */
    private static void viewAllRoutes(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              ALL ROUTES");
        System.out.println("=".repeat(60));

        List<FreightRoute> routes = routeController.getAllFreightRoutes();

        if (routes.isEmpty()) {
            System.out.println("No routes found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.printf("%-6s %-25s %-10s %-20s %-20s %-12s%n",
                "ID", "Name", "Type", "Start", "End", "Distance(km)");
        System.out.println("-".repeat(100));

        for (FreightRoute route : routes) {
            System.out.printf("%-6d %-25s %-10s %-20s %-20s %-12.2f%n",
                    route.getRouteId(),
                    truncate(route.getRouteName(), 25),
                    route.getRouteType(),
                    route.getStartFacility() != null ? truncate(route.getStartFacility().getName(), 20) : "N/A",
                    route.getEndFacility() != null ? truncate(route.getEndFacility().getName(), 20) : "N/A",
                    route.getTotalDistanceKm());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Displays detailed route summary.
     */
    private static void viewRouteSummary(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              ROUTE SUMMARY");
        System.out.println("=".repeat(60));

        List<FreightRoute> routes = routeController.getAllFreightRoutes();
        if (routes.isEmpty()) {
            System.out.println("No routes available.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nAvailable Routes:");
        for (FreightRoute route : routes) {
            System.out.printf("%d - %s (%s)%n", route.getRouteId(), route.getRouteName(), route.getRouteType());
        }

        System.out.print("\nEnter Route ID: ");
        try {
            Integer routeId = Integer.parseInt(scanner.nextLine().trim());
            String summary = routeController.getRouteSummary(routeId);

            if (summary != null) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println(summary);
                System.out.println("=".repeat(60));
            } else {
                System.out.println("Route not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid route ID.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Helper method to select a facility from available facilities.
     */
    private static Facility selectFacility(Scanner scanner) {
        List<Facility> facilities = Repositories.getInstance().getFacilityRepository().getAllFacilities();

        if (facilities.isEmpty()) {
            System.out.println("No facilities available. Please add facilities first.");
            return null;
        }

        System.out.println("\nAvailable Facilities:");
        for (int i = 0; i < facilities.size() && i < 20; i++) {
            Facility f = facilities.get(i);
            System.out.printf("%d - %s (Type: %s)%n",
                    f.getFacilityId(),
                    f.getName(),
                    f.getFacilityType() != null ? f.getFacilityType().getName() : "N/A");
        }

        System.out.print("Enter Facility ID: ");
        try {
            Integer facilityId = Integer.parseInt(scanner.nextLine().trim());
            Facility facility = Repositories.getInstance().getFacilityRepository().getFacilityById(facilityId);
            if (facility == null) {
                System.out.println("Facility not found.");
            }
            return facility;
        } catch (NumberFormatException e) {
            System.out.println("Invalid facility ID.");
            return null;
        }
    }

    /**
     * Helper method to select a line segment.
     */
    private static LineSegment selectLineSegment(Scanner scanner) {
        List<LineSegment> segments = Repositories.getInstance().getLineSegmentRepository().getAllLineSegments();

        if (segments.isEmpty()) {
            System.out.println("No line segments available. Please add line segments first.");
            return null;
        }

        System.out.println("\nAvailable Line Segments:");
        for (int i = 0; i < segments.size() && i < 20; i++) {
            LineSegment seg = segments.get(i);
            System.out.printf("%d - Length: %.2f km, Speed Limit: %.0f km/h%n",
                    seg.getSegmentId(),
                    seg.getLengthKm(),
                    seg.getSpeedLimitKmh());
        }

        System.out.print("Enter Line Segment ID: ");
        try {
            Integer segmentId = Integer.parseInt(scanner.nextLine().trim());
            LineSegment segment = Repositories.getInstance().getLineSegmentRepository().getLineSegmentById(segmentId);
            if (segment == null) {
                System.out.println("Line segment not found.");
            }
            return segment;
        } catch (NumberFormatException e) {
            System.out.println("Invalid segment ID.");
            return null;
        }
    }

    /**
     * Helper method to truncate strings for display.
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
