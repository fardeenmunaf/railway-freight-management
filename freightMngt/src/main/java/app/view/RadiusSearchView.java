package app.view;

import app.service.sprint2.RadiusSearchService;
import app.model.Station;
import app.service.sprint2.ServiceRegistry;
import app.service.sprint2.StationService;
import app.utils.AVLTree;
import app.utils.Balanced2DTreeBuilder.Balanced2DTree.StationWithDistance;

import java.util.Scanner;

/**
 * View for the Radius Search functionality.
 * Implements USEI10 - Radius search and density summary.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RadiusSearchView {

    private RadiusSearchView() {
    }

    /**
     * Displays the Radius Search interface and handles user interaction.
     * This method provides the complete workflow for the radius search.
     *
     * @param scanner The Scanner object for user input.
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         STATION RADIUS SEARCH SERVICE (USEI10)");
        System.out.println("=".repeat(60));
        System.out.println("This tool finds stations within a radius R of a target point.");
        System.out.println("Type 'b' at any time to return to menu.\n");

        // Obtain shared StationService (indexes and 2D-tree built once)
        StationService stationService = ServiceRegistry.getStationService();

        // Create the radius search service with the spatial 2D tree
        RadiusSearchService service = new RadiusSearchService(stationService.getSpatial2DTree());

        try {
            // 1. Get user input
            double targetLat = getInput(scanner, "Target point Latitude (e.g., 37.7223): ");
            double targetLon = getInput(scanner, "Target point Longitude (e.g., 15.1393): ");
            double radiusKm = getInput(scanner, "Search radius in kilometers (R) (e.g., 50.0): ");

            System.out.printf("\nSearching for stations within %.2f km of (%.4f, %.4f)...\n", radiusKm, targetLat, targetLon);

            // 2. Call the service to get the AVL tree and temporal analysis
            RadiusSearchService.RadiusSearchResult result =
                    service.radiusSearchWithAnalysis(targetLat, targetLon, radiusKm);

            AVLTree<StationWithDistance, StationWithDistance> resultsTree = result.resultsTree;
            RadiusSearchService.DensitySummary summary = service.calculateDensitySummary(resultsTree);

            // 3. Display the results and density summary
            displayResults(summary, resultsTree);

            // 4. Display temporal analysis returned by the service
            System.out.println("\n--- Temporal Analysis (Radius Search) ---");
            System.out.println(result.temporalAnalysis.toString());

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numeric values.");
        } catch (RuntimeException e) {
            if (!"User aborted operation.".equals(e.getMessage())) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
            // If user aborted, just return
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Helper to get double input from the user.
     */
    private static double getInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if ("b".equalsIgnoreCase(input)) {
                throw new RuntimeException("User aborted operation.");
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Displays the search results and the density summary.
     */
    private static void displayResults(RadiusSearchService.DensitySummary summary, AVLTree<StationWithDistance, StationWithDistance> resultsTree) {
        System.out.println("\n--- Search Summary ---");
        System.out.println(summary.toString());

        System.out.println("\n--- Found Stations List (Sorted by Distance ASC, Name DESC) ---");

        if (summary.totalCount == 0) {
            System.out.println("No stations found within the specified radius.");
            return;
        }

        System.out.printf("%-5s | %-40s | %-10s | %-10s | %-10s | %-10s\n",
                "No.", "Station Name", "Dist. (km)", "Country", "Latitude", "Longitude");
        System.out.println("---------------------------------------------------------------------------------------------------");

        int count = 1;
        // The AVL tree is already sorted as required (by distance ASC, then by station name DESC)
        for (StationWithDistance swd : resultsTree.inOrderTraversal()) {
            Station station = swd.station;
            System.out.printf("%-5d | %-40s | %-10.2f | %-10s | %-10.4f | %-10.4f\n",
                    count++,
                    station.getName(),
                    swd.distance,
                    station.getCountry(),
                    station.getLat(),
                    station.getLon());
        }
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.printf("Total Stations Found: %d\n", summary.totalCount);
    }
}
