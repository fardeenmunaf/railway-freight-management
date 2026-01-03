package app.view;

import app.controller.TrainAssemblyController;
import app.db.dao.locomotive.LocomotiveDAO;
import app.db.dao.locomotive.LocomotiveDAOImpl;
import app.db.dao.wagon.WagonDAO;
import app.db.dao.wagon.WagonDAOImpl;
import app.model.Locomotive;
import app.model.Wagon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View class for Train Assembly operations (USLP09).
 * Provides a console-based interface for Traffic Managers to assemble trains.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainAssemblyView {

    private final TrainAssemblyController controller;
    private final LocomotiveDAO locomotiveDAO;
    private final WagonDAO wagonDAO;
    private final Scanner scanner;

    private TrainAssemblyView(Scanner scanner) {
        this.controller = new TrainAssemblyController();
        this.locomotiveDAO = new LocomotiveDAOImpl();
        this.wagonDAO = new WagonDAOImpl();
        this.scanner = scanner;
    }

    public static void display(Scanner scanner) {
        TrainAssemblyView view = new TrainAssemblyView(scanner);
        view.showMenu();
    }

    private void showMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║   USLP09: TRAIN ASSEMBLY SYSTEM        ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. List Available Locomotives          ║");
            System.out.println("║ 2. List Available Wagons               ║");
            System.out.println("║ 3. Assemble Train Composition          ║");
            System.out.println("║ 4. Validate Train Composition          ║");
            System.out.println("║ 0. Return to Main Menu                 ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Choose an option: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1:
                        listAvailableLocomotives();
                        break;
                    case 2:
                        listAvailableWagons();
                        break;
                    case 3:
                        assembleTrainComposition();
                        break;
                    case 4:
                        validateTrainComposition();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("[ERROR] Invalid option! Please choose 0-4.");
                }
            } catch (Exception e) {
                scanner.nextLine(); // clear buffer
                System.out.println("[ERROR] Invalid input! Please enter a number.");
            }
        }
    }

    private void listAvailableLocomotives() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("     ALL LOCOMOTIVES IN DATABASE");
        System.out.println("═══════════════════════════════════════════");

        try {
            List<Locomotive> locomotives = locomotiveDAO.getAll();

            if (locomotives.isEmpty()) {
                System.out.println("[INFO] No locomotives found in the database.");
                return;
            }

            System.out.println("\nFound " + locomotives.size() + " locomotive(s):\n");

            for (Locomotive loc : locomotives) {
                displayLocomotiveDetails(loc);
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving locomotives from database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayLocomotiveDetails(Locomotive loc) {
        System.out.println("  ┌─────────────────────────────────────────────────────────────────────");

        // Basic Identification
        System.out.println("  │ LOCOMOTIVE ID: " + (loc.getLocomotiveId() != null ? loc.getLocomotiveId() : "N/A"));
        System.out.println("  │ Brand: " + (loc.getBrand() != null ? loc.getBrand() : "N/A") +
                          " | Model: " + (loc.getModel() != null ? loc.getModel() : "N/A") +
                          " | Year: " + (loc.getYear() != null ? loc.getYear() : "N/A"));
        System.out.println("  │");

        // Power & Performance
        System.out.println("  │ POWER & PERFORMANCE:");
        System.out.println("  │   Power: " + (loc.getPower() != null ? loc.getPower() + " kW" : "N/A"));
        System.out.println("  │   Acceleration: " + (loc.getAcceleration() != null ? String.format("%.3f m/s²", loc.getAcceleration()) : "N/A"));
        System.out.println("  │   Max Speed: " + (loc.getMaxSpeed() != null ? String.format("%.1f km/h", loc.getMaxSpeed()) : "N/A"));
        System.out.println("  │   Operational Speed: " + (loc.getOperationalSpeed() != null ? String.format("%.1f km/h", loc.getOperationalSpeed()) : "N/A"));
        System.out.println("  │   Traction: " + (loc.getTraction() != null ? loc.getTraction() + " kN" : "N/A"));
        System.out.println("  │");

        // Dimensions & Weight
        System.out.println("  │ DIMENSIONS & WEIGHT:");
        if (loc.getDimensions() != null) {
            System.out.println("  │   Dimensions: " + String.format("%.2f m (W) x %.2f m (H)",
                              loc.getDimensions().getWidth() / 100.0,
                              loc.getDimensions().getHeight() / 100.0));
        } else {
            System.out.println("  │   Dimensions: N/A");
        }
        System.out.println("  │   Max Weight: " + (loc.getMaxWeight() != null ? String.format("%.1f t", loc.getMaxWeight()) : "N/A"));
        System.out.println("  │   Total Weight: " + (loc.getTotalWeight() != null ? String.format("%.1f t", loc.getTotalWeight()) : "N/A"));
        System.out.println("  │");

        // Bogie Configuration
        System.out.println("  │ BOGIE CONFIGURATION:");
        System.out.println("  │   Number of Bogies: " + (loc.getMaxBogies() != null ? loc.getMaxBogies() : "N/A"));
        System.out.println("  │   Bogie Type: " + (loc.getBogieType() != null ? loc.getBogieType() : "N/A"));
        System.out.println("  │");

        // Location & Status (if available)
        if (loc.getStatus() != null || loc.getCurrentLocationName() != null || loc.getStartFacility() != null) {
            System.out.println("  │ STATUS & LOCATION:");
            if (loc.getStatus() != null) {
                System.out.println("  │   Status: " + loc.getStatus());
            }
            if (loc.getCurrentLocationName() != null) {
                System.out.println("  │   Current Location: " + loc.getCurrentLocationName());
            }
            if (loc.getStartFacility() != null) {
                System.out.println("  │   Start Facility ID: " + loc.getStartFacility());
            }
            if (loc.getDistanceFromDeparture() != null) {
                System.out.println("  │   Distance from Departure: " + String.format("%.2f km", loc.getDistanceFromDeparture()));
            }
            if (loc.getLocationLat() != null && loc.getLocationLon() != null) {
                System.out.println("  │   Coordinates: " + String.format("%.6f°, %.6f°", loc.getLocationLat(), loc.getLocationLon()));
            }
            System.out.println("  │");
        }

        // Operator (if available)
        if (loc.getOperatorCompanyName() != null) {
            System.out.println("  │ OPERATOR:");
            System.out.println("  │   Company: " + loc.getOperatorCompanyName());
            System.out.println("  │");
        }

        // Transit Information (if in transit)
        if ("IN_TRANSIT".equals(loc.getStatus()) && loc.getTransitDestination() != null) {
            System.out.println("  │ TRANSIT INFORMATION:");
            System.out.println("  │   Destination: " + loc.getTransitDestination());
            System.out.println("  │   Expected Arrival: " + loc.getTransitArrivalDate());
            System.out.println("  │");
        }

        System.out.println("  └─────────────────────────────────────────────────────────────────────");
    }

    private void listAvailableWagons() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("        ALL WAGONS IN DATABASE");
        System.out.println("═══════════════════════════════════════════");

        try {
            List<Wagon> wagons = wagonDAO.getAll();

            if (wagons.isEmpty()) {
                System.out.println("[INFO] No wagons found in the database.");
                return;
            }

            System.out.println("\nFound " + wagons.size() + " wagon(s):\n");

            for (Wagon wagon : wagons) {
                displayWagonDetails(wagon);
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving wagons from database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayWagonDetails(Wagon wagon) {
        System.out.println("  ┌───────────────────────────────────────");
        System.out.println("  │ ID: " + wagon.getWagonId() + " | Model: " +
                          (wagon.getWagonModel() != null ? wagon.getWagonModel().getName() : "N/A"));

        if (wagon.getStatus() != null) {
            System.out.println("  │ Status: " + getWagonStatusIcon(wagon.getStatus()));
        }

        if (wagon.getCurrentLocationName() != null) {
            System.out.println("  │ Location: " + wagon.getCurrentLocationName());
        }

        if (wagon.getDistanceFromDeparture() != null) {
            System.out.println("  │ Distance: " + String.format("%.2f km", wagon.getDistanceFromDeparture()));
        }

        if (wagon.getWagonModel() != null) {
            System.out.println("  │ Payload: " + wagon.getWagonModel().getPayloadWeight() + " t | " +
                             "Max Speed: " + wagon.getWagonModel().getMaxSpeed() + " km/h");
        }

        if (wagon.getOperatorCompanyName() != null) {
            System.out.println("  │ Operator: " + wagon.getOperatorCompanyName());
        }

        if (wagon.getParkingTrackName() != null) {
            System.out.println("  │ Track: " + wagon.getParkingTrackName() +
                             " (Pos: " + wagon.getParkingPosition() + ")");
        }
        System.out.println("  └───────────────────────────────────────");
    }

    private String getWagonStatusIcon(String status) {
        switch (status) {
            case "PARKED": return "PARKED";
            case "WAITING": return "WAITING";
            case "IN_TRANSIT": return "IN_TRANSIT";
            default: return status;
        }
    }

    private void assembleTrainComposition() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("       ASSEMBLE TRAIN COMPOSITION");
        System.out.println("═══════════════════════════════════════════");

        System.out.print("\nEnter Train Service ID:           e.g. 2001:");
        try {
            Integer serviceId = scanner.nextInt();
            scanner.nextLine(); // consume newline

            System.out.print("Enter Locomotive IDs:           e.g.5621: ");
            String input = scanner.nextLine();

            List<Integer> locomotiveIds = parseLocomotiveIds(input);

            if (locomotiveIds.isEmpty()) {
                System.out.println("[ERROR] No valid locomotive IDs provided.");
                return;
            }

            System.out.println("\n[INFO] Assembling train composition...");
            int count = controller.assembleTrainComposition(serviceId, locomotiveIds);
            System.out.println("\n[SUCCESS] Assigned " + count + " locomotive(s) to service " + serviceId);

            // Ask if user wants to validate
            System.out.print("\nValidate composition now? (y/n): ");
            String validate = scanner.nextLine();
            if (validate.equalsIgnoreCase("y")) {
                System.out.println("\n[INFO] Validating composition...");
                String validationResult = controller.validateTrainComposition(serviceId);

                if ("VALID".equals(validationResult)) {
                    System.out.println("\n[SUCCESS] Validation Result: " + validationResult);
                } else {
                    System.out.println("\n[WARNING] Validation Result: " + validationResult);
                }
            }

        } catch (SQLException e) {
            System.err.println("\n[ERROR] Error assembling train composition: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            scanner.nextLine(); // clear buffer
            System.err.println("\n[ERROR] Invalid input: " + e.getMessage());
        }
    }

    private void validateTrainComposition() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("      VALIDATE TRAIN COMPOSITION");
        System.out.println("═══════════════════════════════════════════");

        System.out.print("\nEnter Train Service ID: ");
        try {
            Integer serviceId = scanner.nextInt();
            scanner.nextLine(); // consume newline

            System.out.println("\n[INFO] Validating composition...");
            String validationResult = controller.validateTrainComposition(serviceId);

            if ("VALID".equals(validationResult)) {
                System.out.println("\n[SUCCESS] Validation Result: " + validationResult);
                System.out.println("The train composition meets all technical requirements.");
            } else {
                System.out.println("\n[WARNING] Validation Result: " + validationResult);
                System.out.println("Please review and correct the composition.");
            }

        } catch (SQLException e) {
            System.err.println("\n[ERROR] Error validating train composition: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            scanner.nextLine(); // clear buffer
            System.err.println("\n[ERROR] Invalid input: " + e.getMessage());
        }
    }


    private List<Integer> parseLocomotiveIds(String input) {
        List<Integer> ids = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            try {
                ids.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException e) {
                System.err.println("[WARNING] Invalid ID: " + part);
            }
        }

        return ids;
    }
}

