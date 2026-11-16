package app.view;

import app.service.sprint3.ShortestPathService;
import app.service.sprint3.StationLoaderService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ShortestPathUI {

    private final ShortestPathService pathService;
    private final StationLoaderService dataService;
    private final Scanner scanner;

    private ShortestPathUI(Scanner scanner) throws IOException {
        this.dataService = StationLoaderService.getInstance();
        this.pathService = new ShortestPathService();
        this.scanner = scanner;
    }

    public static void display(Scanner scanner) throws IOException {
        ShortestPathUI ui = new ShortestPathUI(scanner);
        System.out.println("Network loaded successfully!");
        System.out.println("Stations: " + ui.dataService.getTotalStations());
        System.out.println("Lines:    " + ui.dataService.getTotalConnections());
        System.out.println();
        ui.showMenu();
    }

    private void showMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("=== RISK-AWARE SHORTEST PATHS ===");
            System.out.println("1. Calculate shortest path");
            System.out.println("2. List available stations");
            System.out.println("0. Return to main menu");
            System.out.print("Choose an option: ");
            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1:
                        calculatePath();
                        break;
                    case 2:
                        listStations();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Invalid input! Please enter a number.");
            }
            System.out.println();
        }
    }

    private void calculatePath() {
        System.out.println("\n=== SHORTEST PATH ===");
        Integer departureId = getStationId("departure");
        Integer arrivalId = getStationId("arrival");

        if (departureId == null || arrivalId == null) {
            return;
        }

        System.out.println("\nCalculating the best path...");
        ShortestPathService.PathResultWithAlgorithm result = pathService.calculateBestPath(departureId, arrivalId);

        if (result == null) {
            System.out.println("Could not calculate a path.");
            System.out.println("   (the stations do not exist or are not connected)");
            return;
        }

        System.out.println("\n--- RESULT ---");
        System.out.println("Algorithm used: " + result.getAlgorithmUsed());
        System.out.println();

        ShortestPathService.PathResult pathResult = result.getPathResult();

        if (result.hasNegativeCycle()) {
            System.out.println("WARNING: negative cycle detected!");
            System.out.println("The network configuration is inconsistent.");
            System.out.println();
        }
        System.out.println(pathResult.formatPath());
    }

    private Integer getStationId(String type) {
        System.out.print("Enter " + type + " station ID: ");
        try {
            Integer id = scanner.nextInt();
            scanner.nextLine();
            if (!dataService.stationExists(id)) {
                System.out.println("Station with ID " + id + " does not exist!");
                return null;
            }
            return id;
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid ID!");
            return null;
        }
    }

    private void listStations() {
        System.out.println("\n=== AVAILABLE STATIONS ===");
        if (dataService.getTotalStations() == 0) {
            System.out.println("No stations loaded.");
            return;
        }

        List<String> formattedStations = dataService.getFormattedStationList();
        formattedStations.forEach(System.out::println);
        System.out.println("\nTotal: " + dataService.getTotalStations() + " stations");
    }
}
