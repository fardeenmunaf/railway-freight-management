package app.view;

import app.model.*;
import app.service.sprint3.TrainServiceAssemblyService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View class for Complete Train Service Assembly operations (USLP09).
 * Provides a console-based interface for Traffic Managers to assemble complete train services
 * including locomotives, wagons, routes, and schedules.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainAssemblyView {

    private final TrainServiceAssemblyService service;
    private final Scanner scanner;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TrainAssemblyView(Scanner scanner) {
        this.service = new TrainServiceAssemblyService();
        this.scanner = scanner;
    }

    public static void display(Scanner scanner) {
        TrainAssemblyView view = new TrainAssemblyView(scanner);
        view.showMenu();
    }

    private void showMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   USLP09: COMPLETE TRAIN SERVICE ASSEMBLY         ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Assemble Complete Train Service                ║");
            System.out.println("║ 2. View All Train Services                        ║");
            System.out.println("║ 3. View Train Service Details                     ║");
            System.out.println("║ 0. Return to Main Menu                            ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("Choose an option: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1:
                        assembleCompleteTrainService();
                        break;
                    case 2:
                        viewAllTrainServices();
                        break;
                    case 3:
                        viewTrainServiceDetails();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("[ERROR] Invalid option! Please choose 0-3.");
                }
            } catch (Exception e) {
                scanner.nextLine(); // clear buffer
                System.out.println("[ERROR] Invalid input! Please enter a number.");
            }
        }
    }

    /**
     * Main workflow to assemble a complete train service.
     */
    private void assembleCompleteTrainService() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     ASSEMBLE COMPLETE TRAIN SERVICE WORKFLOW       ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        try {
            // Step 1: Select departure facility (first stop)
            System.out.println("\n[STEP 1/7] SELECT DEPARTURE FACILITY");
            Integer departureFacilityId = selectFacility("Enter departure facility ID: ");
            if (departureFacilityId == null) return;

            // Step 2: Select locomotives
            System.out.println("\n[STEP 2/7] SELECT LOCOMOTIVES");
            List<Integer> locomotiveIds = selectLocomotivesInteractive(departureFacilityId);
            if (locomotiveIds == null || locomotiveIds.isEmpty()) {
                System.out.println("[ERROR] At least one locomotive must be selected. Aborting.");
                return;
            }

            // Step 3: Select wagons (optional)
            System.out.println("\n[STEP 3/7] SELECT WAGONS (OPTIONAL)");
            System.out.print("Do you want to add wagons? (y/n): ");
            String addWagons = scanner.nextLine();
            List<Integer> wagonIds = new ArrayList<>();
            if (addWagons.equalsIgnoreCase("y")) {
                wagonIds = selectWagonsInteractive(departureFacilityId);
            }

            // Step 4: Define route stops
            System.out.println("\n[STEP 4/7] DEFINE ROUTE STOPS");
            List<TrainServiceAssemblyService.RouteStop> routeStops = defineRouteStopsInteractive(departureFacilityId);
            if (routeStops == null || routeStops.size() < 2) {
                System.out.println("[ERROR] At least 2 stops (departure and destination) required. Aborting.");
                return;
            }

            // Step 5: Service schedule (automatically from route stops)
            System.out.println("\n[STEP 5/7] SERVICE SCHEDULE");
            // Use departure time from first stop and arrival time from last stop
            LocalDateTime departureDate = routeStops.get(0).getScheduledDeparture();
            LocalDateTime arrivalDate = routeStops.get(routeStops.size() - 1).getSchedArrival();

            System.out.println("  Schedule automatically set from route:");
            System.out.println("  ┌─────────────────────────────────────────────────");
            System.out.println("  │ Departure: " + departureDate.format(dateTimeFormatter));
            System.out.println("  │ Arrival:   " + arrivalDate.format(dateTimeFormatter));
            System.out.println("  │ Duration:  " + formatDuration(java.time.Duration.between(departureDate, arrivalDate).toMinutes()));
            System.out.println("  └─────────────────────────────────────────────────");

            // Step 6: Assign driver (optional)
            System.out.println("\n[STEP 6/7] ASSIGN DRIVER (OPTIONAL)");
            System.out.print("Enter driver user ID (or press Enter to skip): ");
            String driverInput = scanner.nextLine();
            Integer driverId = null;
            if (!driverInput.isEmpty()) {
                try {
                    driverId = Integer.parseInt(driverInput);
                } catch (NumberFormatException e) {
                    System.out.println("[WARNING] Invalid driver ID, proceeding without driver.");
                }
            }

            // Step 7: Set operator company
            System.out.println("\n[STEP 7/7] SET OPERATOR COMPANY");
            System.out.print("Enter operator company ID: ");
            Integer operatorId = scanner.nextInt();
            scanner.nextLine();

            // Confirmation
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║              ASSEMBLY SUMMARY                      ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ Locomotives:     " + locomotiveIds.size());
            System.out.println("║ Wagons:          " + wagonIds.size());
            System.out.println("║ Route Stops:     " + routeStops.size());
            System.out.println("║ Departure:       " + departureDate.format(dateTimeFormatter));
            System.out.println("║ Arrival:         " + arrivalDate.format(dateTimeFormatter));
            System.out.println("║ Driver ID:       " + (driverId != null ? driverId : "None"));
            System.out.println("║ Operator ID:     " + operatorId);
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("\nProceed with assembly? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("[INFO] Assembly cancelled.");
                return;
            }

            // Execute assembly
            System.out.println("\n[INFO] Assembling train service...");
            TrainServiceAssemblyService.AssemblyResult result = service.assembleCompleteTrainService(
                    locomotiveIds, wagonIds, routeStops, departureDate, arrivalDate, driverId, operatorId
            );

            // Display results
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║          ASSEMBLY COMPLETED SUCCESSFULLY!          ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ Train ID:         " + result.getTrain().getTrainId());
            System.out.println("║ Train Code:       " + result.getTrain().getCode());
            System.out.println("║ Service ID:       " + result.getTrainService().getServiceId());
            System.out.println("║ Status:           " + result.getTrainService().getStatus());
            System.out.println("║ Locomotives:      " + result.getLocomotivesAssigned());
            System.out.println("║ Train Stops:      " + result.getTrainStops().size());
            System.out.println("║ Validation:       " + result.getValidationMessage());
            System.out.println("╚════════════════════════════════════════════════════╝");

            if (!result.isValid()) {
                System.out.println("\n[WARNING] Validation issues detected:");
                System.out.println(result.getValidationMessage());
            }

        } catch (SQLException e) {
            System.err.println("\n[ERROR] Database error: " + e.getMessage());
            if (e.getNextException() != null) {
                System.err.println("  Cause: " + e.getNextException().getMessage());
            }
        } catch (Exception e) {
            System.err.println("\n[ERROR] Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Interactive locomotive selection.
     */
    private List<Integer> selectLocomotivesInteractive(Integer departureFacilityId) {
        List<Integer> selectedIds = new ArrayList<>();

        try {
            List<Locomotive> locomotives = service.getAvailableLocomotives(departureFacilityId);

            if (locomotives.isEmpty()) {
                System.out.println("[WARNING] No locomotives available.");
                return selectedIds;
            }

            System.out.println("\nAvailable Locomotives:");
            System.out.println("─────────────────────────────────────────────────────────────");
            for (Locomotive loc : locomotives) {
                System.out.printf("ID: %-5d | %-20s | Status: %-10s | Distance: %.2f km%n",
                        loc.getLocomotiveId(),
                        loc.getBrand() + " " + loc.getModel(),
                        loc.getStatus(),
                        loc.getDistanceFromDeparture() != null ? loc.getDistanceFromDeparture() : 0.0);

                if ("IN_TRANSIT".equals(loc.getStatus())) {
                    System.out.printf("          → Destination: %s | ETA: %s%n",
                            loc.getTransitDestination(),
                            loc.getTransitArrivalDate());
                } else if ("PARKED".equals(loc.getStatus())) {
                    System.out.printf("          → Location: %s%n", loc.getCurrentLocationName());
                }
            }
            System.out.println("─────────────────────────────────────────────────────────────");

            System.out.println("\nEnter locomotive IDs (comma-separated, e.g., 1,2,3):");
            System.out.print("IDs: ");
            String input = scanner.nextLine();

            if (!input.isEmpty()) {
                String[] parts = input.split(",");
                for (String part : parts) {
                    try {
                        selectedIds.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("[WARNING] Invalid ID: " + part);
                    }
                }
            }

            System.out.println("[INFO] Selected " + selectedIds.size() + " locomotive(s).");

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving locomotives: " + e.getMessage());
        }

        return selectedIds;
    }

    /**
     * Interactive wagon selection.
     */
    private List<Integer> selectWagonsInteractive(Integer departureFacilityId) {
        List<Integer> selectedIds = new ArrayList<>();

        try {
            List<Wagon> wagons = service.getAvailableWagons(departureFacilityId);

            if (wagons.isEmpty()) {
                System.out.println("[WARNING] No wagons available.");
                return selectedIds;
            }

            System.out.println("\nAvailable Wagons:");
            System.out.println("─────────────────────────────────────────────────────────────");
            for (Wagon wagon : wagons) {
                System.out.printf("ID: %-5d | Model: %-20s | Status: %-10s | Distance: %.2f km%n",
                        wagon.getWagonId(),
                        wagon.getWagonModel() != null ? wagon.getWagonModel().getName() : "N/A",
                        wagon.getStatus(),
                        wagon.getDistanceFromDeparture() != null ? wagon.getDistanceFromDeparture() : 0.0);

                if ("PARKED".equals(wagon.getStatus())) {
                    System.out.printf("          → Location: %s%n", wagon.getCurrentLocationName());
                }
            }
            System.out.println("─────────────────────────────────────────────────────────────");

            System.out.println("\nEnter wagon IDs (comma-separated, e.g., 1,2,3) or press Enter to skip:");
            System.out.print("IDs: ");
            String input = scanner.nextLine();

            if (!input.isEmpty()) {
                String[] parts = input.split(",");
                for (String part : parts) {
                    try {
                        selectedIds.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("[WARNING] Invalid ID: " + part);
                    }
                }
            }

            System.out.println("[INFO] Selected " + selectedIds.size() + " wagon(s).");

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving wagons: " + e.getMessage());
        }

        return selectedIds;
    }

    /**
     * Interactive route stops definition with automatic time calculation.
     */
    private List<TrainServiceAssemblyService.RouteStop> defineRouteStopsInteractive(Integer departureFacilityId) {
        List<TrainServiceAssemblyService.RouteStop> stops = new ArrayList<>();

        try {
            System.out.println("\n═══════════════════════════════════════════════════════════");
            System.out.println("  ROUTE DEFINITION - Automatic Time Calculation");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  • Travel times calculated automatically based on distance");
            System.out.println("  • Average speed: 80 km/h");
            System.out.println("  • Stop time: 5 minutes at each intermediate station");
            System.out.println("═══════════════════════════════════════════════════════════");

            // First stop is departure
            System.out.println("\n[STOP 1 - DEPARTURE]");
            LocalDateTime currentTime = readDateTime("Departure time (yyyy-MM-dd HH:mm): ");
            if (currentTime == null) return null;

            stops.add(new TrainServiceAssemblyService.RouteStop(
                    departureFacilityId, currentTime, currentTime, 0));

            Facility previousFacility = service.getFacilityById(departureFacilityId);
            if (previousFacility == null) {
                System.err.println("[ERROR] Could not load departure facility information.");
                return null;
            }

            // Intermediate stops with automatic time calculation
            boolean addingStops = true;
            int stopNumber = 2;
            final double AVERAGE_SPEED_KMH = 80.0; // Average train speed
            final int STOP_TIME_MINUTES = 5; // Fixed stop time

            while (addingStops) {
                System.out.println("\n[STOP " + stopNumber + "]");
                System.out.print("Enter facility ID for stop " + stopNumber + " (or 0 to finish): ");

                try {
                    int facilityId = scanner.nextInt();
                    scanner.nextLine();

                    if (facilityId == 0) {
                        addingStops = false;
                        continue;
                    }

                    // Get facility information
                    Facility currentFacility = service.getFacilityById(facilityId);
                    if (currentFacility == null) {
                        System.err.println("[ERROR] Facility " + facilityId + " not found. Try another ID.");
                        continue;
                    }

                    // Calculate distance from previous stop
                    double distanceKm = calculateDistance(
                        previousFacility.getLat(), previousFacility.getLon(),
                        currentFacility.getLat(), currentFacility.getLon()
                    );

                    // Calculate travel time in minutes
                    double travelTimeMinutes = (distanceKm / AVERAGE_SPEED_KMH) * 60.0;

                    // Calculate arrival time (previous departure + travel time)
                    LocalDateTime arrivalTime = currentTime.plusMinutes((long) Math.ceil(travelTimeMinutes));

                    // Calculate departure time (arrival + stop time)
                    LocalDateTime departureTime = arrivalTime.plusMinutes(STOP_TIME_MINUTES);

                    // Display calculated times
                    System.out.println("  ┌─────────────────────────────────────────────────");
                    System.out.println("  │ Facility: " + currentFacility.getName());
                    System.out.println("  │ Distance from previous: " + String.format("%.2f km", distanceKm));
                    System.out.println("  │ Travel time: " + String.format("%.0f minutes", travelTimeMinutes));
                    System.out.println("  │ ─────────────────────────────────────────────────");
                    System.out.println("  │ Calculated arrival:   " + arrivalTime.format(dateTimeFormatter));
                    System.out.println("  │ Stop time:            " + STOP_TIME_MINUTES + " minutes");
                    System.out.println("  │ Calculated departure: " + departureTime.format(dateTimeFormatter));
                    System.out.println("  └─────────────────────────────────────────────────");

                    stops.add(new TrainServiceAssemblyService.RouteStop(
                            facilityId, arrivalTime, departureTime, STOP_TIME_MINUTES));

                    // Update for next iteration
                    currentTime = departureTime;
                    previousFacility = currentFacility;
                    stopNumber++;

                } catch (SQLException e) {
                    System.err.println("[ERROR] Database error: " + e.getMessage());
                    scanner.nextLine();
                } catch (Exception e) {
                    scanner.nextLine();
                    System.out.println("[WARNING] Invalid input, try again.");
                }
            }

            if (stops.size() < 2) {
                System.err.println("[ERROR] At least 2 stops required (departure + destination).");
                return null;
            }

            // Display route summary
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║              ROUTE SUMMARY                         ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            double totalDistance = 0;
            for (int i = 1; i < stops.size(); i++) {
                Facility prev = service.getFacilityById(stops.get(i-1).getFacilityId());
                Facility curr = service.getFacilityById(stops.get(i).getFacilityId());
                double dist = calculateDistance(prev.getLat(), prev.getLon(), curr.getLat(), curr.getLon());
                totalDistance += dist;
            }
            long totalMinutes = java.time.Duration.between(
                stops.get(0).getScheduledDeparture(),
                stops.get(stops.size()-1).getSchedArrival()
            ).toMinutes();

            System.out.println("║ Total stops:      " + stops.size());
            System.out.println("║ Total distance:   " + String.format("%.2f km", totalDistance));
            System.out.println("║ Total time:       " + formatDuration(totalMinutes));
            System.out.println("║ Departure:        " + stops.get(0).getScheduledDeparture().format(dateTimeFormatter));
            System.out.println("║ Arrival:          " + stops.get(stops.size()-1).getSchedArrival().format(dateTimeFormatter));
            System.out.println("╚════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("[ERROR] Error defining route: " + e.getMessage());
            return null;
        }

        return stops;
    }

    /**
     * Calculates distance between two coordinates using Haversine formula.
     *
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Formats duration in minutes to human-readable string.
     */
    private String formatDuration(long minutes) {
        long hours = minutes / 60;
        long mins = minutes % 60;
        return String.format("%d hours %d minutes", hours, mins);
    }

    /**
     * Selects a facility ID.
     */
    private Integer selectFacility(String prompt) {
        try {
            List<Facility> facilities = service.getAllFacilities();

            if (!facilities.isEmpty()) {
                System.out.println("\nAvailable Facilities (showing first 20):");
                System.out.println("─────────────────────────────────────────────────────────────");
                int count = 0;
                for (Facility facility : facilities) {
                    System.out.printf("ID: %-5d | %-40s | Lat: %.4f, Lon: %.4f%n",
                            facility.getFacilityId(),
                            facility.getName(),
                            facility.getLat(),
                            facility.getLon());
                    if (++count >= 20) break;
                }
                System.out.println("─────────────────────────────────────────────────────────────");
            }

            System.out.print(prompt);
            Integer facilityId = scanner.nextInt();
            scanner.nextLine();
            return facilityId;

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving facilities: " + e.getMessage());
            return null;
        } catch (Exception e) {
            scanner.nextLine();
            System.err.println("[ERROR] Invalid input.");
            return null;
        }
    }

    /**
     * Reads a date/time from user input.
     */
    private LocalDateTime readDateTime(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();

        try {
            return LocalDateTime.parse(input, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("[ERROR] Invalid date/time format. Use: yyyy-MM-dd HH:mm");
            return null;
        }
    }

    /**
     * Views all train services.
     */
    private void viewAllTrainServices() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║            ALL TRAIN SERVICES                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        try {
            List<TrainService> services = service.getAllTrainServices();

            if (services.isEmpty()) {
                System.out.println("[INFO] No train services found.");
                return;
            }

            System.out.println("\nFound " + services.size() + " service(s):\n");
            System.out.println("─────────────────────────────────────────────────────────────────────────");
            System.out.printf("%-8s | %-10s | %-18s | %-18s | %-12s%n",
                    "ID", "Train ID", "Departure", "Arrival", "Status");
            System.out.println("─────────────────────────────────────────────────────────────────────────");

            for (TrainService svc : services) {
                System.out.printf("%-8d | %-10d | %-18s | %-18s | %-12s%n",
                        svc.getServiceId(),
                        svc.getTrain() != null ? svc.getTrain().getTrainId() : 0,
                        svc.getDepartureDate() != null ? svc.getDepartureDate().format(dateTimeFormatter) : "N/A",
                        svc.getArrivalDate() != null ? svc.getArrivalDate().format(dateTimeFormatter) : "N/A",
                        svc.getStatus());
            }
            System.out.println("─────────────────────────────────────────────────────────────────────────");

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving train services: " + e.getMessage());
        }
    }

    /**
     * Views details of a specific train service.
     */
    private void viewTrainServiceDetails() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║         TRAIN SERVICE DETAILS                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        System.out.print("\nEnter Train Service ID: ");
        try {
            int serviceId = scanner.nextInt();
            scanner.nextLine();

            List<TrainStop> stops = service.getTrainStopsByService(serviceId);

            if (stops.isEmpty()) {
                System.out.println("[INFO] No stops found for service ID " + serviceId);
                return;
            }

            System.out.println("\nTrain Service " + serviceId + " - Route Stops:");
            System.out.println("─────────────────────────────────────────────────────────────────────────");
            System.out.printf("%-4s | %-10s | %-18s | %-18s%n",
                    "Pos", "Facility", "Arrival", "Departure");
            System.out.println("─────────────────────────────────────────────────────────────────────────");

            for (TrainStop stop : stops) {
                System.out.printf("%-4d | %-10d | %-18s | %-18s%n",
                        stop.getPosition(),
                        stop.getFacility() != null ? stop.getFacility().getFacilityId() : 0,
                        stop.getSchedArr() != null ? stop.getSchedArr().format(dateTimeFormatter) : "N/A",
                        stop.getSchedDep() != null ? stop.getSchedDep().format(dateTimeFormatter) : "N/A");
            }
            System.out.println("─────────────────────────────────────────────────────────────────────────");

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving train service details: " + e.getMessage());
        } catch (Exception e) {
            scanner.nextLine();
            System.err.println("[ERROR] Invalid input.");
        }
    }
}

